# Playwright MCP 멀티 에이전트 경합 문제해결

## Intro

이전에 [Playwright MCP를 활용해 LLM이 스스로 UI 수정하게 하기](https://shanepark.tistory.com/555) 글에서 LLM의 셀프 리프백 루프를 통한 디자인 요소 자동 조절에 대해 다루었다. Claude Code나 Codex 같은 도구가 스스로 UI를 확인하고, 클릭하고, 스크린샷을 찍는 작업이 가능해지면서 사용자가 굳이 코드 변경때마다 눈으로 확인 할 필요 없이 스스로 답이 나올 때 까지 변경을 하게 되었고 그 만족감이 대단하다.

최근에는 Chrome extension 등이 나와서 더 강력하게 브라우저를 제어할 수 있게 되었지만 애초에 Playwright MCP와 Chrome의 extension은 서로 결이 다르기떄문에 Playwright MCP가 대체될 것이라고 생각하지 않는다.

그런데 Playwright mcp 를 여러개의 에이전트가 동시에 호출하여 이용하는 순간, 예상치 못한 문제가 발생한다. 브라우저 탭이 끝없이 늘어나고, 어느 에이전트도 제대로 된 결과를 얻지 못하는 교착 상태에 빠져버리게 된다.

직접 경험해본 사람들은 모두 공감할텐데 매우 짜증난다.

## 문제

Claude Code를 여러 터미널에서 동시에 실행하거나, Task 도구로 서브에이전트를 생성하면 각 에이전트가 독립적으로 Playwright MCP를 호출한다. 문제는 이들이 **같은 MCP 서버 인스턴스**를 공유한다는 점이다.

```
에이전트 A: browser_navigate → localhost:5173
에이전트 B: browser_navigate → localhost:5173/settings
에이전트 A: browser_snapshot → ???
```

에이전트 A가 스냅샷을 찍으려는 순간, 브라우저는 이미 에이전트 B가 이동시킨 `/settings` 페이지를 보여준다. 결과적으로 A는 엉뚱한 화면을 받게 되고, 이후 작업이 꼬이기 시작한다.

[microsoft/playwright-mcp#893](https://github.com/microsoft/playwright-mcp/issues/893) 에서 누군가가 여기에 문제제기를 했지만 그건 playwright mcp 의 문제가 아니라며 묵살당했다. 이슈에서는 다중 MCP 서버 설정이 해결책으로 제시되었는데 그것도 제법 괜찮은 아이디어같다.

## 해결

하지만 복잡한 해결책 대신, 가장 단순한 방법을 시도해보았다. 한 번에 하나의 에이전트만 Playwright를 사용하도록 파일 락을 거는 것이다.

핵심 아이디어는 다음과 같다:

1. Playwright 사용 전에 락 파일(`.playwright.lock`)을 확인한다
2. 락이 없으면 획득하고, 있으면 사용을 포기하거나 대기한다
3. 작업이 끝나면 락을 해제한다
4. 비정상 종료에 대비해 10분 후 락이 자동 만료된다

### 락 스크립트 구현

`scripts/playwright-lock.sh` 파일을 생성했다.

```bash
#!/bin/bash
#
# Playwright Lock Manager
# Prevents multiple Claude Code agents from using Playwright simultaneously

LOCK_FILE=".playwright.lock"
LOCK_TIMEOUT=600  # 10 minutes in seconds

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
LOCK_PATH="$PROJECT_ROOT/$LOCK_FILE"

get_current_timestamp() {
    date +%s
}

get_lock_timestamp() {
    if [[ -f "$LOCK_PATH" ]]; then
        cat "$LOCK_PATH" 2>/dev/null || echo "0"
    else
        echo "0"
    fi
}

is_lock_expired() {
    local lock_time=$(get_lock_timestamp)
    local current_time=$(get_current_timestamp)
    local age=$((current_time - lock_time))
    [[ $age -gt $LOCK_TIMEOUT ]]
}

cmd_check() {
    if [[ ! -f "$LOCK_PATH" ]]; then
        echo "AVAILABLE: Playwright is available."
        exit 0
    fi

    if is_lock_expired; then
        echo "AVAILABLE: Lock expired. Playwright is available."
        exit 0
    else
        echo "LOCKED: Playwright is in use by another agent."
        exit 1
    fi
}

cmd_acquire() {
    if [[ -f "$LOCK_PATH" ]] && ! is_lock_expired; then
        echo "FAILED: Cannot acquire lock. Playwright is in use."
        exit 1
    fi

    [[ -f "$LOCK_PATH" ]] && rm -f "$LOCK_PATH"
    get_current_timestamp > "$LOCK_PATH"
    echo "SUCCESS: Playwright lock acquired."
    exit 0
}

cmd_release() {
    rm -f "$LOCK_PATH"
    echo "SUCCESS: Playwright lock released."
    exit 0
}

case "${1:-}" in
    check)   cmd_check ;;
    acquire) cmd_acquire ;;
    release) cmd_release ;;
    *)       echo "Usage: $0 {check|acquire|release}" && exit 1 ;;
esac
```

락 파일에는 타임스탬프만 저장된다. 단순하지만 만료 시간 계산이 가능하고, 비정상 종료 시에도 10분 후 자동 복구된다.

### 사용 방법

```bash
# 1. Playwright 사용 가능 여부 확인
./scripts/playwright-lock.sh check

# 2. 락 획득 (사용 가능할 때만)
./scripts/playwright-lock.sh acquire

# 3. Playwright 작업 수행
# browser_navigate, browser_snapshot, browser_click 등

# 4. 작업 완료 후 락 해제 (필수!)
./scripts/playwright-lock.sh release
```

### AI 에이전트에게 규칙 알려주기

스크립트만으로는 부족하다. AI 에이전트가 이 규칙을 알고 따라야 한다. `CLAUDE.md`에 다음과 같은 지침을 추가했다.

```markdown
## Playwright Lock System (CRITICAL for Multi-Agent)

When multiple Claude Code agents run concurrently, they may conflict
over Playwright browser control. A lock system prevents deadlocks.

**Lock file**: `.playwright.lock` in project root (auto-expires after 10 minutes)

### Required Workflow

**BEFORE using any Playwright tool (`browser_*`):**
1. Check: `./scripts/playwright-lock.sh check`
2. If AVAILABLE: `./scripts/playwright-lock.sh acquire`
3. If LOCKED: Skip Playwright or ask user

**AFTER finishing all Playwright operations:**
- ALWAYS release: `./scripts/playwright-lock.sh release`
- Release even if errors occurred
```

Claude Code는 `CLAUDE.md`를 프로젝트 컨텍스트로 인식하기 때문에, Playwright 도구를 호출하기 전에 락 확인 절차를 거치게 된다.

### 충돌 시 대응

락이 이미 잡혀 있을 때 에이전트가 어떻게 행동할지도 명시했다.

```markdown
### Handling Lock Conflicts

If `check` returns `LOCKED`:

1. **Preferred**: Skip Playwright for this task if not critical
2. **Alternative**: Ask the user:
   > "Playwright is currently locked by another agent. Would you like me to:
   > 1. Skip visual testing for this task
   > 2. Wait for the lock to be released
   > 3. Force release the lock (only if the other agent crashed)"
```

대부분의 경우 Playwright는 "있으면 좋지만 필수는 아닌" 도구다. 코드 변경이나 로직 수정은 Playwright 없이도 가능하다. 락 충돌 시 건너뛰는 것이 가장 실용적인 선택이다.

race condition 가능성이 전혀 없는건 아니지만 그래도 `acquire` 한번 더 체크하기도 하고 그정도 짧은 시간에 여러개의 에이전트가 동시에 lock을 요청하는 일은 매우 드물기 때문에 복잡도를 늘리지 않는 상황에서 이정도면 나쁘지 않다고 판단했다. 

에이전트가 락 해제를 깜빡하면 10분간 다른 에이전트가 대기해야 하는데, 지금까지 경험으로는 적절히 락을 해제하였고, 자동 만료 덕분에 영구 교착은 발생하지 않는다.

## 마치며

Playwright MCP의 멀티 에이전트 경합 문제는 꽤 답답한 경험이었다. 브라우저 탭이 끝없이 늘어나고, 에이전트들이 서로의 작업을 방해하는 모습을 보면서 여러 해결책을 고민했다. 결국 가장 단순한 방법인 파일 락을 선택했고, 지금까지 교착 상태 없이 잘 동작하고 있다. 복잡한 문제에 단순한 해결책이 통할 때가 있다.

**References**

- [microsoft/playwright-mcp#893 - Parallel agent execution causes tab conflicts](https://github.com/microsoft/playwright-mcp/issues/893)
- [Playwright MCP GitHub Repository](https://github.com/microsoft/playwright-mcp)

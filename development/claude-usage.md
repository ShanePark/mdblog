# Claude Code 사용량 확인 기능 추가 소식

## Intro

요즘에는 `codex-cli`와 `Claude Code`를 번갈아가며 쓰고 있다.

`AGENTS.md` 파일을 만들고 `ln -s AGENTS.md CLAUDE.md` 명령어로 심볼릭 링크를 만들면 두 코딩 에이전트가 같은 컨텍스트 파일을 공유한다. 둘 다 성능이 매우 훌륭해 상황에 따라 골라 쓰기만 하면 된다.

Claude Code는 5h limit이 빡빡해서 주간 리밋에 걸리는 일은 거의 없고, Codex는 5h limit은 넉넉하지만 Weekly limit에 쉽게 닿는 구조다. 그래서 평소에는 `Claude Code Sonnet 4.5`를 메인으로 사용하다가 5시간 제한에 걸리거나 Sonnet으로 풀기 어려운 문제가 있으면 `codex-cli`에서 `gpt-5-codex-high` 모델을 꺼낸다. 월 `$20 + $20 = $40`만 지불하면 누릴 수 있는 가성비 조합이다.

## 사용량 확인

Codex는 `codex-cli 0.40`에서 도입된 사용량 모니터링 기능 덕분에 `/status` 한 줄로 5h / Weekly limit을 쉽게 확인할 수 있다.

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/development/claude-usage.assets/1.webp)

반면 Claude Code에는 그동안 유사한 기능이 없어 `npx ccusage@latest`를 주기적으로 실행하며 5h 리밋이 언제 다가오는지 추측만 할 수 있었다(5h 기준 약 $5 근처에서 걸림). 5h context window가 언제 종료되는지도 정확히 알 수 없어 항상 답답했는데, 어느 날 `/status`를 습관처럼 입력한 순간 마침 세션이 Claude Code였고 새로 추가된 `/usage` 커맨드를 발견했다. 기다리던 기능을 마침내 만나게 된 셈이다.

소소하지만 스트레스를 크게 줄여주는 업데이트다.

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/development/claude-usage.assets/2.webp)

> `/status` 입력 후 Tab을 두 번 누르거나 `/usage`를 바로 입력하면 현재 플랜 기준 사용량과 리셋 시간이 요약된다.  
> Codex는 최소한 한 번은 프롬프트를 보내야 리밋 정보가 나오지만, Claude는 세션이 열리면 바로 윈도우가 시작돼 곧바로 데이터를 볼 수 있다.

[CHANGELOG.md](https://github.com/anthropics/claude-code/blob/main/CHANGELOG.md)를 확인해보면 `/usage` 커맨드는 `Claude Code 2.0.0`에서 추가되었다.

`Claude Code`는 기본적으로 자동 업데이트지만 업데이트가 안 된 상태면 `/usage`가 없을 수도 있다.

1. 현재 버전 확인.

   ```bash
   claude --version
   # 예) 2.0.13 (Claude Code)
   ```

2. 업데이트 방법.

   ```bash
   # 수동 업데이트
   claude update
   
   # 자동 업데이트 끄고 싶을 때
   export DISABLE_AUTOUPDATER=1
   ```

## 마치며

`Sonnet 4.5`가 나오면서 체급이 확 올라왔다. Anthropic 발표에 따르면 SWE-bench Verified 같은 코드 벤치에서 강한 성능을 보였고, 여러 영역에서 이전 최상위였던 `Opus 4.1`을 앞서는 결과가 나왔다. 실제 사용에서도 체감이 크다.

`codex-cli`의 성능도 꾸준히 올라 서로 엎치락뒤치락하며 경쟁하는 구도가 되니 사용자 입장에서는 더없이 만족스럽다.

**References**

- https://github.com/anthropics/claude-code/blob/main/CHANGELOG.md
- https://anthropic.mintlify.app/en/docs/claude-code/setup
- https://www.anthropic.com/news/claude-sonnet-4-5

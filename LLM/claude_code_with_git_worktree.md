# Git Worktree 를 활용한 Claude Code 병렬 실행

## Intro

`AGENTS.md` 파일에 스펙을 명확히 정의해두고 , `PLAN.md` 파일을 생성해 수행할 체크리스트를 작성해두어 컨텍스트를 유지하게끔 하며 Claude Code나 Codex-cli 로 모노레포에 함꼐 들어있는 프론트엔드와 백엔드를 번갈아가며 개발을 진행하곤 했다.

> 다양한 AI AGENT 에서 활용하려면 `CLAUDE.md`, `GEMINI.md` 등 심볼릭링크를 생성해둬야 한다. 혹은 해당 파일명으로 텍스트 파일을 생성하고 내용에 `@AGENTS.md` 만 작성해둬도 알아서 추적 한다.

그런데 개발할 스펙이 이미 명확하게 정의되어 있고 프론트와 백엔드를 서로 독립적으로 개발할 수 있다면 굳이 순차적으로 번갈아가며 개발할 필요가 있을까 하는 생각이 들었다. 그래서 여러 개발자들로부터 적극 추천되었던 Git worktree를 활용해 Claude Code를 병렬로 실행하는 방식을 시도해봤는데, 개발 속도가 눈에 띄게 향상되어 상당히 인상적이다.

물론 Claude Code가 sub agent를 생성해서 병렬 작업을 수행할 수도 있지만, 이는 하나의 작업 공간에서 여러 작업을 동시에 처리하는 방식이다.

반면 Git worktree를 활용하면 물리적으로 분리된 디렉토리에서 각각 독립적인 Claude Code 인스턴스를 실행할 수 있다. 이는 완전히 별개의 agent를 각자의 작업 공간에서 별개의 브랜치로 실행하는 것이므로, 모노레포에서도 서로 간섭 없이 깔끔하게 병렬 작업이 가능하다.

## Claude Code의 Sub Agent

Claude Code는 필요에 따라 sub agent를 생성하여 작업을 분산 처리할 수 있다. Sub agent는 메인 agent가 생성하는 하위 작업자로, 독립적으로 특정 작업을 수행한 후 결과를 메인 agent에게 전달한다.

### Sub Agent 사용법

생각보다 간단하다. Claude Code와 대화 중에 명시적으로 요청하면 된다:

```bash
# "프론트엔드와 백엔드 코드를 sub agent를 만들어서 병렬로 작성해줘"
# "이 작업을 3개의 sub agent로 나눠서 동시에 처리해줘"
```

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/claude_code_with_git_worktree.assets/1.webp)

> 알아서 3개의 서브 에이전트를 병렬로 실행하고 있는 상황. 작업시간이 확실히 줄어든다.

### Sub Agent 활용

Sub agent는 하나의 작업 디렉토리 내에서 여러 파일을 동시에 생성하거나 수정할 때 유용하다. 예를 들어:

- 여러 컴포넌트를 동시에 생성
- 다수의 API 엔드포인트를 병렬로 구현
- 여러 테스트 파일을 동시에 작성

다만 sub agent는 같은 작업 공간을 공유하므로, 서로 다른 브랜치에서 작업하거나 완전히 독립적인 환경이 필요한 경우에는 한계가 있다. 이런 상황에서 Git worktree가 필요하다.

## Git Worktree

Git worktree는 하나의 저장소에서 여러 개의 작업 디렉토리를 동시에 운영할 수 있게 해주는 기능이다. 새로 clone을 받을 필요 없이 최소한의 디스크 공간만 사용하여 독립적인 작업 환경을 구성할 수 있다는 점이 가장 큰 장점이다.

각 worktree는 `.git` 파일을 통해 메인 저장소의 `.git` 디렉토리를 참조한다. 전체 저장소를 다시 clone 받으면 Git 히스토리까지 모두 복사되지만, worktree는 작업 파일만 체크아웃하고 Git 데이터는 공유한다. 덕분에 clone 대비 상당한 디스크 공간을 절약할 수 있다.

### Worktree 생성

Worktree를 생성할 때는 보통 새로운 브랜치도 함께 생성한다. `-b` 옵션을 사용하면 브랜치 생성과 worktree 추가를 한 번에 할 수 있다

```bash
# 새 브랜치와 함께 worktree 생성 (권장)
git worktree add -b feature/frontend ../myproject-frontend

# 백엔드용 새 브랜치와 worktree 생성
git worktree add -b feature/backend ../myproject-backend

# 기존 브랜치를 사용하는 경우 (브랜치가 이미 존재할 때)
git worktree add ../myproject-hotfix hotfix/critical-bug

# 현재 worktree 목록 확인
git worktree list
```

`-b` 옵션을 사용하면 현재 HEAD를 기준으로 새 브랜치를 생성하면서 동시에 worktree를 만든다. 이렇게 하면 각 작업이 독립적인 브랜치에서 진행되므로 나중에 머지할 때도 깔끔하다.

만약 특정 브랜치를 기준으로 새 브랜치를 만들고 싶다면

```bash
# main 브랜치를 기준으로 새 브랜치 생성
git worktree add -b feature/new-feature ../project-new-feature origin/main
```

### Worktree vs Sub Agent

두 방식의 차이를 정리하면 다음과 같다:

**Sub Agent 방식**

- 하나의 작업 디렉토리에서 동작
- 같은 브랜치에서 여러 작업 동시 수행
- 메인 agent가 전체 조율
- 파일 간 의존성이 있는 작업에 적합

**Worktree 방식**

- 물리적으로 분리된 디렉토리
- 각각 다른 브랜치에서 작업
- 완전히 독립적인 Claude Code 인스턴스
- 모듈 간 독립성이 높은 작업에 적합

## Claude Code와 Worktree 조합

먼저 프로젝트 루트에 `AGENTS.md` 파일로 전체 스펙을 정의한다. 다음은 간략한 예시다

```markdown
## 공통 규약
- API 응답 형식: { success: boolean, data?: any, error?: string }
- 인증: Bearer Token (JWT)
- Base URL: /api/v1

## Data Models
- User: { id, email, name, role, createdAt }
- Post: { id, title, content, authorId, createdAt }

## Frontend Requirements
- React 18 기반 SPA
- 사용자 인증 UI
- 게시물 CRUD 화면

## Backend Requirements  
- Node.js + Express
- JWT 기반 인증 미들웨어
- RESTful API (users, posts)
```

### Worktree 사용 예시

이제 각 부분을 독립적인 worktree에서 개발한다. 개발에 앞서 PLAN 을 작성하게 한 뒤 `PLAN.md` 파일을 생성해두면 더 좋다.

```bash
# 인증 기능을 위한 새 브랜치와 worktree 생성
git worktree add -b feature/authentication ../project-auth

# 대시보드를 위한 새 브랜치와 worktree 생성 
git worktree add -b feature/dashboard ../project-dashboard

# 각각에서 Claude Code 실행
cd ../project-auth
claude
# "JWT 기반 인증 시스템을 구현해줘. 로그인, 로그아웃, 토큰 갱신 포함"

cd ../project-dashboard
claude
# "관리자 대시보드를 만들어줘. 사용자 통계와 최근 활동 표시"
```

두 기능이 서로 독립적이기 때문에 동시 개발이 가능하다.

서로 다른 모듈의 테스트 코드를 동시에 작성하는 등의 경우에는 worktree 까지 쓸 필요 없이 subagent 활용만으로 충분하다.

## 고려사항

### 토큰 사용량

당연한 이야기지만 동시에 실행하는 Claude Code agent 수에 비례해서 토큰 사용량이 증가한다. 무턱대고 사용했다가는 순식간에 limit에 다다르게 된다. 지금까지는 클로드 Pro 플랜을 사용하며 여러가지 코드 에이전트를 돌려막기로 버텨왔지만 다중 agent를 사용하면서는 결국 MAXx20 플랜을 구독할 수 밖에 없었다.

### 브랜치 관리

같은 브랜치를 여러 worktree에서 체크아웃할 수 없다. Git이 자동으로 이를 방지하므로, 각 worktree는 다른 브랜치를 사용해야 한다:

```bash
# 이미 사용 중인 브랜치를 체크아웃하려고 하면 오류 발생
git worktree add ../another-dir main
# fatal: 'main' is already checked out at '/original/path'
```

### 파일 충돌 방지

각 Claude Code 인스턴스가 독립적으로 작업하므로 나중에 merge 할 때 귀찮은 충돌 관리를 피하려면 같은 파일을 수정하는 일이 없도록 작업 범위를 명확히 구분하는 것이 중요하다. `PLAN.md`에서 각 모듈의 책임과 파일 구조를 명확히 정의해두면 도움이 된다.

## 마치며

Git worktree를 활용하면 물리적으로 분리된 환경에서 여러 Claude Code를 동시에 실행할 수 있고, 이는 개발 속도를 획기적으로 향상시킨다. 특히 모노레포 환경이나 독립적인 모듈을 동시에 개발해야 하는 상황에서 상당한 시간 절약을 할 수 있다. 토큰보다는 개발자의 시간이 훨씬 비싸다.
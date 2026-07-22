# Claude Code 5시간 리밋, 8시간 근무에 3번 활용하기
## Intro

오전 9시에 업무를 시작하며 Claude Code를 처음 실행하면 첫 번째 5시간 윈도우는 오후 2시에 끝난다. 두 번째 윈도우는 오후 7시까지라서, 일반적인 근무시간 안에서는 사실상 두 개의 윈도우만 쓰게 된다. 하지만 주간 limit에 비해 5h limit이 관대한 Codex 에 비해 Claude Code의 경우에는 주간리밋에 비해 5h 리밋이 꽤 빡빡한 편이다.

> Codex는 최근 주기적으로 리셋을 남발하더니, 기어코 5h limit을 없애버리는 파격을 선보였다.

병렬로 작업을 조금만 빡세게 돌리면 Max20x 플랜에서도 5h 리밋은 금방 차버린다. 이를 해결하기 위해 최소한의 세팅으로 하루 3번의 5h 윈도우를 사용하는 방법을 설명한다.

## 문제

```text
09:00 출근 후 업무시작. 첫 요청
14:00 첫 번째 윈도우 종료

14:00 다음 요청
19:00 두 번째 윈도우 종료
```

오후 7시까지 두 번째 윈도우가 이어지지만, 일반적인 9 to 6 근무에서는 퇴근 전까지 새 윈도우를 한 번 더 활용할 수 없다. 주간 한도는 남아 있는데 5시간 세션 한도에 먼저 도달하는 사용 패턴에서는, 세션 리셋 시각이 근무시간과 맞지 않는 것이 문제가 된다.

그래서 첫 요청을 업무 시작보다 미리 보내놓기로 했다.

```text
06:00 첫번째 요청
11:00 첫 번째 윈도우 종료

11:00 두번재 요청
16:00 두 번째 윈도우 종료

16:00 세 번째 윈도우 시작
```

그런데 6시에 미리 첫번째 요청을 보내놓는게 번거롭기도 하고 깜빡하기도 쉽다.

## Routines

Routines는 프롬프트와 저장소, 커넥터, 실행 조건을 미리 저장해두고 Claude Code를 자동 실행하는 기능이다. schedule뿐만 아니라 GitHub 이벤트나 API 호출로도 시작할 수 있고, Anthropic이 관리하는 클라우드에서 실행되므로 내 컴퓨터가 켜져 있을 필요가 없다.

이 기능은 2026년 4월 13일부터 17일까지의 Claude Code Week 16 업데이트에서 추가됐다. Claude Code on the web이 활성화된 Pro, Max, Team, Enterprise 플랜에서 사용할 수 있으며, 현재는 research preview라서 동작 방식이나 제한은 변경될 수 있다.

### 이전 방식

Routines가 없을 때는 로컬에서 `claude -p`를 호출하는 스크립트를 만들고 cron에 등록하는 식으로 처리해야 했다. 실제로 처음에는 아래와 같이 구성했다.

```cron
57 5 * * 1-5 /home/shane/claude-schedule/trigger.sh morning
2 11 * * 1-5 /home/shane/claude-schedule/trigger.sh midday
```

`trigger.sh`에서는 Claude Code를 비대화형으로 한 번 실행하고, 요청 시각과 응답, stderr, 종료 코드를 날짜별 로그로 남겼다. 작동 자체는 잘했지만 cron의 PATH와 Claude 로그인 상태, 서버 시간대, 작동하는 서버 상태까지 챙겨야 했다.

집에서 항상 켜두고 개인 서버로 사용하는 노트북이 별도로 있어 괜찮았지만, 일반적인 PC의 경우 suspend 상태라면 일반 cron은 놓친 작업을 나중에 실행해주지 않는다. GitHub Actions에 인증 토큰을 넣는 방법도 있지만, 단어 하나를 응답받기 위해 유지할 구성으로는 과하다.

Routines가 생긴 뒤에는 이 용도에 로컬 cron을 고집할 이유가 거의 없어졌다. 정확한 분 단위 제어와 로컬 로그가 반드시 필요한 작업이라면 cron도 여전히 유효하지만, 단순 예약 실행은 Routines가 훨씬 편하다.

### 설정

[Claude Code Routines](https://claude.ai/code/routines)에서 `New routine`을 선택한다.

이름은 알아보기 쉽게 `Claude Window Warmup`으로 정했다. `Instructions`에는 불필요한 작업을 하지 않고 `ready`만 응답하도록 명확하게 적었다.

```text
Reply exactly with: ready

Do not inspect repository files.
Do not run shell commands.
Do not modify files.
Do not use connectors.
```

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/claude/claude-code-routines-5h-window.assets/1.webp)

> Routine 이름과 `Instructions`를 입력한 상태

Routine은 승인 요청 없이 독립적인 Claude Code 클라우드 세션으로 실행된다. 저장소 파일을 읽고 명령을 실행하거나 연결된 커넥터를 사용할 수도 있으므로, 이번 용도에서는 권한을 최대한 줄이는 편이 낫다.

이번 용도라면 실제 업무 저장소 대신 비어 있는 private 저장소 하나를 연결하는 편이 낫다. `Connectors`도 모두 제거했다.

### Schedule

예약은 아래 두 개를 등록했다.

```text
Morning: Weekdays at 05:57 AM
Lunch:   Weekdays at 11:02 AM
```

오후 4시에는 별도 Routine을 만들지 않았다. 4시에 강제로 시작하든 5시에 실제 작업으로 시작하든 퇴근할때까지 마지막 윈도우를 사용하는건 똑같다. 

두개의 스케쥴 사이에는 정확히 5시간이 아니라 약간의 시간 간격을 두는편이 좋다. Routines는 서버 부하 분산을 위해 예정 시각보다 몇 분 늦게 시작될 수 있으므로, 정각에 딱 붙이는 것보다 약간 여유를 두는 편이 안전하다.

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/claude/claude-code-routines-5h-window.assets/2.webp)

> 평일 오전 5시 57분과 11시 2분 예약이 등록된 상태

예약 시각은 사용자의 로컬 시간대를 기준으로 입력하면 된다. 한국에서 사용한다면 별도의 UTC 환산 없이 오전 5시 57분과 오전 11시 2분으로 설정하면 된다. Routines는 예정 시각보다 몇 분 늦게 시작될 수 있다고 하던데 보통은 거의 맞춰서 수행되는 것 같다.

## 확인

생성한 뒤에는 `Run now`를 눌러 바로 테스트한다. 실행 세션을 열었을 때 아래처럼 `ready`가 응답되면 프롬프트는 정상이다.

```text
ready
```

목록에 초록색 성공 상태가 보이는 것만으로는 충분하지 않다. 공식 문서에서도 초록색 상태는 인프라 오류 없이 세션이 시작되고 종료됐다는 뜻일 뿐, 프롬프트의 작업 성공까지 보장하지는 않는다고 설명한다. 실제 실행 내용을 열어 응답을 확인해야 한다.

다음 평일에는 `Settings > Usage`에서 리셋 시각도 확인한다. 오전 Routine 이후 첫 리셋이 11시 전후로 잡히고, 11시 Routine 이후 다음 리셋이 오후 4시 전후로 바뀌면 의도한 흐름이다.

![3](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/claude/claude-code-routines-5h-window.assets/3.webp)

> 출근 후 오전 9시 되기 조금 전에 확인하니, 1시간 47분 후에 리셋된다고 나온다. 정상적으로 트리거가 작동했다는 뜻이다.

Routine 실행도 일반 Claude Code 세션처럼 구독 사용량을 차감한다. 별도의 사용량을 얻는 것이 아니며, 주간 리밋도 그대로다. 또한 Claude 웹과 앱에서 새벽에 먼저 요청을 보내면 같은 공유 사용량 윈도우가 이미 시작되어 있을 수 있다.

## 마치며

이전에는 shell script와 cron으로 필요한 기능을 모두 만들어두었다. 직접 로그까지 남길 수 있어 통제력은 좋았지만, 단순히 사용량 윈도우의 시작 시각을 맞추는 용도에는 번거로웠다.

현재 기준으로는 Claude Code Routines가 가장 간단한 방법이다. 평일 오전 5시 57분과 11시 2분에 짧은 요청을 예약하고, 오후 4시 이후 세 번째 윈도우는 실제 필요할 때 시작하면 된다.

다만 Routines는 아직 research preview이고 예약 실행이 몇 분 늦어질 수 있다. 처음 하루 이틀은 실행 기록과 Usage 화면을 함께 확인한 뒤, 자신의 업무 시작 시각에 맞춰 schedule을 조정하는 것을 추천한다.

**References**

- https://code.claude.com/docs/en/routines
- https://code.claude.com/docs/en/whats-new/2026-w16
- https://support.claude.com/en/articles/9797557-usage-limit-best-practices
- https://support.claude.com/en/articles/11145838-use-claude-code-with-your-pro-or-max-plan

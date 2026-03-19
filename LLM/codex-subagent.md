# Codex 서브에이전트 도입 소식 및 활용팁

## Intro

최근 육아휴직에 들어가면서 개인 용도로만 월 200달러짜리 `Claude Code Max x20`를 유지하는 것이 부담스러워졌다.

그동안 `Claude Code`를 메인으로 쓰면서 ChatGPT Team 플랜을 사용했었고  `codex`의 깊은 코드 이해도와 복잡한 태스크 수행 능력을 개인적으로 높이 사고 있었다. 그런데 마침 2월 초 카카오톡 선물하기에서 ChatGPT Pro 1개월 이용권이 29,000원에 풀리는 대란이 있었고, 정가 월 200달러짜리 상품을 1인당 5개까지 살 수 있었기에 망설임 없이 5개를 구매했다.

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/codex-subagent.assets/2.webp)

> Claude Code Max x20 요금제는 조금 타이트하게 썼다 하면 5시간 리밋, 주간 리밋 모두 금방 금방 다 써버리곤 했었는데 ChatGPT Pro는 밤새 에이전트 혼자 작업하게끔 돌려도 사용량이 여유로웠다. 둘 다 정식 가격은 $200이다.

그렇게 비용절감 목적으로 `codex`로 메인을 바꿨는데, gpt-5.3-codex 에 이어 gpt-5.4 까지 연달아 기대 이상의 성능을 보여줘서 아주 만족스러웠다. 최근에는 macOS 앱도 나왔는데 사용량 2배를 주는 이벤트때문에 사용해봤다가 기대이상으로 좋아서 정착했다. 여러 작업을 동시에 돌릴 때 터미널 창을 여러 개 열어야 했던 불편함이 해소됐고, worktree 내장 지원 덕분에 같은 레포에서 여러 스레드를 격리된 환경으로 돌릴 수 있게 됐다. 다만 `Claude Code`에서 유용하게 쓰던 서브에이전트 기능이 없다는 점은 계속 아쉬웠는데, 드디어 그 공백이 채워졌다.

## 서브에이전트의 이점

코딩 에이전트를 오래 쓰다 보면 context window의 중요성을 금방 체감하게 된다. 탐색 로그, 테스트 결과, 스택 트레이스처럼 작업 과정에서 쏟아지는 중간 결과물이 메인 대화에 쌓이기 시작하면, 유용한 정보가 노이즈에 묻히고 시간이 지날수록 모델 성능이 눈에 띄게 떨어진다. 공식 문서에서는 이를 **context pollution**과 **context rot**이라고 부른다.

서브에이전트 워크플로우는 이 문제를 구조적으로 해결한다. 노이즈가 많은 탐색·테스트·분석 작업은 별도 에이전트에 위임하고, 서브에이전트는 날것의 중간 결과물 대신 요약본만 메인 스레드로 돌려준다. 메인 에이전트는 요구사항과 최종 결정에만 집중할 수 있는 셈이다. 작업을 독립적으로 병렬 실행하니 시간도 아낄 수 있고, 덩어리가 큰 태스크를 다루기 수월해진다는 것도 이점이다.

## 트리거 방법

서브에이전트는 자동으로 되지는 않는다. 프롬프트에 명시적으로 요청해야 한다.

```
이 브랜치를 병렬 서브에이전트로 리뷰해줘.
보안 리스크, 테스트 누락, 유지보수성 각각 하나씩 에이전트를 만들고,
모두 완료되면 카테고리별로 파일 참조와 함께 결과를 요약해줘.
```

`spawn two agents`, `delegate this work in parallel`, `use one agent per point` 같은 표현이 자연스럽게 트리거로 작동한다. 실행 중인 에이전트 스레드는 `/agent` 커맨드로 전환하며 확인할 수 있다.

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/codex-subagent.assets/1.webp)

> codex 에도 서브에이전트가 들어온 감격적인 순간

다만 클로드코드에서 주로 했던 방법과 동일하게 `AGENTS.md`에 서브에이전트 활용 원칙을 넣어두니, 대화에서 매번 "서브에이전트를 써라"고 직접 지시하지 않아도 `codex`가 필요한 시점에 알아서 서브에이전트를 활용하는게 확인되었다. 특히 독립적인 탐색, 검증, 검색처럼 병렬로 돌릴 수 있는 작업에서 효과가 좋아서 컨텍스트 파일에 서브에이전트 활용 관련해서는 꼭 적어두는걸 추천한다.

예를 들어 아래처럼 적어두고 테스트 해 보았는데 원하는대로 잘 동작했다

```md
## 6. Sub-Agent Use

- Consider sub-agent use on every non-trivial task.
- Use sub-agents proactively when independent research, search, verification, or disjoint implementation work can run in parallel within the current checklist item and clearly help.
- Do not use sub-agents for sequential steps, overlapping file edits, or tightly coupled refactors.
- When spawning sub-agents, use the same model as the main agent. Do not override the sub-agent model unless the user explicitly asks for a different one.
- The main agent remains responsible for planning, integration, final verification, and user communication.
```

이런 식으로 규칙을 미리 적어두면 메인 에이전트가 계획과 통합을 맡고, 독립적인 조사나 검증만 서브에이전트에 위임하는 패턴이 자연스럽게 자리잡는다. 서브에이전트를 무조건 많이 만드는 것이 아니라, 병렬화 이점이 분명한 경우에만 쓰도록 제한하는 점도 중요하다.

스스로 생성한 서브에이전트는 `gpt-5.4-mini`를 호출하는걸 확인했는데, 성능이 Sonnet 4.6과 거의 동일하기때문에 충분히 쓸만하다.

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/codex-subagent.assets/3.webp)

> Uses GPT-5.2-Mini

하지만 사용량도 넉넉하게 남은 편이고 왠만하면 더 나은 모델을 사용하고 싶어서 한 줄을 추가했다.

```
 When spawning sub-agents, use the same model as the main agent. Do not override the sub-agent model unless the user explicitly asks for a different one.
```

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/codex-subagent.assets/4.webp)

> same model 사용에 대한 지침을 넣으니 Uses 까지만 써있고 사용중인 모델이 별도로 표시되지 않음.
>
> 물론 GPT-5.4를 사용하라고 명시할 수 있으나 추후에 새로운 모델이 나와서 바꿨을때도 알아서 사용하려면, 메인 에이전트의 모델을 그대로 사용하라는 지침을 작성하는 편이 낫다.

모델 품질 차이에 민감한 작업이라면 이 한 줄도 꽤 유용하다.


## 마치며

`Claude Code`에서만 쓰던 서브에이전트가 `codex`에도 들어왔다. 카카오 대란으로 확보한 Pro 플랜 덕분에 당분간은 모든 모델을 넉넉하게 쓸 수 있어 더욱 반가운 소식이다. 두 도구가 서로의 장점을 빠르게 흡수하며 경쟁하는 구도가 되니, 사용자 입장에서는 더없이 반가운 상황이다. 

Claude Code만 써본 사용자라면 codex도 한번 사용해보길 권한다. $20 Plus 플랜에서도 제법 많은 사용량을 제공해주기 때문에 테스트해보기에는 충분하다. 개인적으로는 복직 후에는 Claude, Codex 모두 병행하여 사용할 생각이다.

**References**

- https://developers.openai.com/codex/subagents

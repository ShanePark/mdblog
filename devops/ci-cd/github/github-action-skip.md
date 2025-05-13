# 커밋시 GitHub Actions 스킵하는 방법

## Intro

코드와 관계없는 파일을 살짝 고치거나 README.md 파일만 업데이트했을 뿐인데, 매번 CI가 돌아가면서 빌드에 3~5분, 배포까지 하면 더 길어지는 시간. 이런 불필요한 CI 실행 때문에 리소스 낭비도 되고, 워크플로우 실행 제한에 걸리는 경우도 있다.

이번 글에서는 GitHub 공식 문서에 나온 내용을 바탕으로, CI를 스킵하는 여러 방법과 주의사항까지 정리해본다.

## 방법1: 커밋 메시지

워크플로우가 `on: push`나 `on: pull_request`에 의해 실행되는 경우, 커밋 메시지에 아래 키워드 중 하나라도 포함시키면 해당 워크플로우는 **자동으로 실행되지 않는다**.

### 사용할 수 있는 키워드

```text
[skip ci]
[ci skip]
[no ci]
[skip actions]
[actions skip]
```

예시

```bash
git commit -m "Update README.md [skip ci]"
```

혹은,

```bash
git commit -m "문서 오타 수정 [no ci]"
```

CI와 관련된 단어들 중 하나만 있어도 동작하므로, 자신의 스타일에 맞게 골라서 쓰면 된다.

## 방법2: `skip-checks` 트레일러 

커밋 메시지에 트레일러(trailer)를 사용하는 것도 가능하다. 트레일러는 커밋 메시지 마지막에 빈 줄 두 개를 넣고 작성해야 한다.

예시:

```text
문서 수정

skip-checks: true
```

`--cleanup=verbatim` 옵션을 사용하지 않으면 Git이 줄바꿈을 자동 정리해서 깨질 수 있으니 주의.

```bash
git commit --cleanup=verbatim
```

## 주의할 점

### 1. 체크가 “Pending” 상태로 남는다

`[skip ci]` 방식으로 워크플로우가 아예 실행되지 않은 경우, GitHub에서 **해당 체크가 성공 처리되지 않고 “Pending”으로 남는다.**

따라서 PR 병합 조건에 “모든 체크가 성공해야 함”이 걸려 있는 경우엔 병합이 **막힐 수 있다.**

> 이럴 땐, skip 없이 새로운 커밋을 하나 더 올려서 체크를 실행시키면 해결된다.

### 2. `pull_request_target` 에는 적용되지 않음

이 스킵 키워드는 `on: push`와 `on: pull_request`에만 동작한다. 만약 `on: pull_request_target`이나 `workflow_dispatch`, `schedule` 같은 이벤트에서는 무시된다.

따라서 모든 경우에 스킵되는 게 아니니, 이벤트 종류를 먼저 확인하자.

### 3. 커밋 메시지에만 적용됨

PR에서 여러 커밋이 있을 경우, 가장 마지막 HEAD 커밋의 메시지를 기준으로 워크플로우 실행 여부가 결정된다. PR의 타이틀이나 설명에는 키워드를 넣어도 효과 없다.

## 결론

간단한 문서 작업, 주석 수정, README 업데이트 같은 변경에 CI/CD를 굳이 돌릴 필요는 없다. 이럴 땐 커밋 메시지에 `[skip ci]` 또는 `skip-checks: true`를 붙여서 **GitHub Actions를 간단하게 스킵**할 수 있다.

단, PR 병합 조건이나 이벤트 종류에 따라 예상과 다르게 작동할 수도 있으니 사용 전에 해당 리포지토리의 설정을 꼭 확인하자.

**References**

- https://docs.github.com/en/actions/managing-workflow-runs-and-deployments/managing-workflow-runs/skipping-workflow-runs
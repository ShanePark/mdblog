# Gemini API 429 RESOURCE_EXHAUSTED Prepay 크레딧 문제해결

## Intro

Spring AI로 돌리던 백엔드 스케줄러가 갑자기 멈췄다. 어제까지는 문제 없이 동작하던 Gemini API 호출이었고, 모델도 무료 쿼터 안에서 쓰던 Gemma 4였기 때문에 처음에는 단순한 rate limit이라고 생각했다.

그런데 로그를 보니 일반적인 분당 요청 제한 문제가 아니었다. 에러 메시지는 `Your prepayment credits are depleted`였고, 상태는 `RESOURCE_EXHAUSTED`였다. 결론부터 말하면 기존 프로젝트가 Google AI Studio의 Prepay 과금 체계에 묶이면서, 무료 쿼터가 남아 있어도 API 호출이 막힌 상황이었다.

[무료 Gemini 2.5 API에서 Gemma 3로의 강제 이주기](https://shanepark.tistory.com/556), [gemma4 출시 소식 및 사용 후기](https://shanepark.tistory.com/563)에 이어 또 한 번 Google AI Studio 무료 API 쪽에서 발목을 잡힌 셈이다. 이번 글에서는 429 로그를 어떻게 해석했고, 비용 충전 없이 무료 티어 프로젝트로 다시 살린 과정을 정리한다.

## 문제

스케줄러 작업 중 Spring AI 쪽에서 아래와 같은 예외가 발생했다.

```text
org.springframework.ai.retry.NonTransientAiException: HTTP 429 - [{
  "error": {
    "code": 429,
    "message": "Your prepayment credits are depleted. Please go to AI Studio...",
    "status": "RESOURCE_EXHAUSTED"
  }
}]
```

429라서 처음에는 요청량을 의심했다. 하지만 AI Studio에서 무료 쿼터를 완전히 소진한 상황은 아니었고, 별도로 모델이나 코드 설정을 바꾼 것도 없었다. 특히 일반적인 quota exceeded 메시지가 아니라 `prepayment credits are depleted`라고 나온 점이 핵심이었다.

즉 이 문제는 단순히 “오늘 요청을 너무 많이 보냈다”가 아니라, 프로젝트의 billing 상태가 바뀌었거나 기존 billing account와 연결된 상태에서 Prepay 잔액이 0이 된 경우로 봐야 한다.

## 원인

Google AI Studio의 Gemini API billing 문서를 확인해보니 2026년 3월 23일부터 Prepay와 Postpay billing plan이 적용된다고 안내되어 있다. Paid Tier로 올릴 때는 billing account를 연결하고, 필요한 경우 최소 10달러 상당의 credit을 선불로 충전해야 한다.

문제는 한번 Paid Tier 쪽으로 묶인 프로젝트는 무료 프로젝트처럼 동작하지 않는다는 점이다. 공식 문서에는 Prepay credit balance가 0달러가 되면 해당 billing account에 연결된 모든 프로젝트의 모든 API key가 동시에 멈춘다고 되어 있다. 또한 프로젝트와 API key는 독립적인 과금 설정을 갖지 않고, 연결된 프로젝트와 billing account의 tier와 billing status를 상속한다.

내 경우도 이 흐름에 걸린 것으로 보인다. 과거에 Google Cloud 또는 AI Studio에 결제 수단을 등록했던 이력이 있었고, 해당 프로젝트가 Free Tier 전용 프로젝트가 아니라 Tier 1 / Prepay 쪽으로 분류되어 있었다. 그래서 무료 사용량이 남아 있더라도 Prepay credit이 0달러인 순간 API 호출이 차단된 것이다.

이 경우 `429 RESOURCE_EXHAUSTED`라고 찍히지만, 해결 방향은 retry나 sleep이 아니다. Prepay credit을 충전하거나, billing account 연결이 없는 Free Tier 프로젝트로 다시 분리해야 한다.

## 해결

유료 credit을 충전하면 당연히 바로 해결할 수 있다. 하지만 이 프로젝트는 개인용 스케줄러이고, 기존에도 무료 쿼터 안에서 충분히 동작하던 용도였다. 최소 크레딧을 충전해서 Paid Tier로 계속 쓰는 것보다, billing이 꼬이지 않은 Free Tier 전용 프로젝트를 새로 만드는 쪽이 낫다고 판단했다. 

최소 금액인 10달러를 충전 해볼까 생각했지만 사용하지 않으면 1년 후에 크레딧이 사라지기 때문에 너무 아까웠다.

AI Studio에서 아래 순서로 처리했다.

1. Google AI Studio 콘솔에 접속한다.
2. 우측 상단에서 `Create a new project`를 선택한다.
3. 결제 계정이 연결되지 않은 새 프로젝트를 만든다.
4. 새 프로젝트에서 API key를 다시 발급받는다.
5. 새 API key를 반영한다.

새로 발급된 API key는 기존에 쓰던 키보다 훨씬 길고 복잡했다. API 키 관련해서도 보안상 개선이 있었나보다.

복사 과정에서 일부가 잘리면 다시 엉뚱한 인증 오류를 만나게 되니, 환경 변수에 넣은 뒤에는 실제 값 길이가 제대로 들어갔는지 확인하는 편이 좋다.

Spring Boot 쪽에서는 별도의 코드 수정 없이 `.env` 파일의 key만 교체 후 재시작했다. DB에서 API 키를 관리한다면 재시작 할 필요도 없을것이다.

## 확인

API key를 교체한 뒤에는 같은 스케줄러 작업을 다시 실행했다. 별도의 retry 정책이나 모델 변경 없이 바로 정상 동작했고, 기존에 발생하던 `Your prepayment credits are depleted` 메시지도 사라졌다.

이번 문제에서 확인할 포인트는 세 가지다.

1. `RESOURCE_EXHAUSTED`라고 해서 모두 같은 rate limit 문제가 아니다.
2. 메시지에 `prepayment credits`가 나오면 billing plan과 credit 잔액을 먼저 봐야 한다.
3. 무료로 계속 쓰고 싶다면 billing account가 연결되지 않은 Free Tier 프로젝트인지 확인해야 한다.

특히 기존 Google Cloud 프로젝트를 AI Studio로 가져와 쓰거나, 과거에 카드 정보를 등록한 계정이라면 프로젝트의 `Billing Tier`와 `Status`를 먼저 확인하는 것이 좋다. API key만 새로 만든다고 항상 해결되는 것은 아니고, key가 속한 프로젝트의 billing 상태가 중요하다.

## 주의사항

이번 해결 방법은 “유료 기능을 무료로 우회한다”는 의미가 아니다. Paid Tier가 필요한 모델, 더 높은 rate limit, 데이터 처리 조건이 필요한 상황이라면 billing을 연결하고 credit을 충전하는 것이 맞다.

다만 무료 쿼터 안에서 충분히 돌아가는 개인 프로젝트라면 이야기가 다르다. 이때는 Paid Tier 프로젝트에 남아 있다가 Prepay 잔액 0달러로 전체 호출이 막히는 것보다, Free Tier 전용 프로젝트를 명확히 분리해두는 편이 관리하기 쉽다.

또 Google의 무료 티어와 rate limit은 계속 바뀌고 있다. 2025년 말 Gemini 무료 티어 축소 때도 그랬고, 2026년 3월 Prepay/Postpay 전환도 마찬가지다. 개인 프로젝트에서 무료 API를 운영성 있게 쓰려면 에러 로그에 찍힌 메시지를 그대로 보고, 단순히 “429니까 기다리면 되겠지”라고 처리하지 않는 것이 중요하다.

## 마치며

이번 429 에러는 코드 문제가 아니라 billing 상태 문제였다. `Your prepayment credits are depleted`가 보인다면 rate limit 재시도보다 AI Studio의 프로젝트와 billing tier를 먼저 확인하는 것을 추천한다.

개인적으로는 무료 티어 프로젝트와 유료 프로젝트를 앞으로 완전히 분리해서 관리할 생각이다. 무료 쿼터로 충분한 작업은 Free Tier 전용 프로젝트에 두고, 유료 기능이나 높은 한도가 필요한 작업만 별도 Paid Tier 프로젝트로 올리는 방식이 가장 덜 헷갈린다.

**References**

- https://ai.google.dev/gemini-api/docs/billing
- https://aistudio.google.com/

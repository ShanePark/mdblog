# Playwright MCP를 활용해 LLM이 스스로 UI 수정하게 하기

## Intro

LLM에게 UI 수정을 요청할 때마다 브라우저를 새로고침하며 결과를 확인하고, 다시 수정 요청을 하는 과정이 반복되곤 한다. 코드는 잘 생성해주지만, 실제로 의도한 대로 동작하는지는 직접 확인해야 하는 번거로움이 있다. 가끔 여러번의 수정 요청에도 제대로 처리가 되지 않으면 잘 안된 부분에 대해 브라우저의 devtools에서 현 상황을 보여주거나 스크린샷을 찍어서 직접 LLM에게 건네기도 한다. 해결에는 큰 도움이 되지만 여간 번거로운 일이 아니다.

Playwright MCP를 활용하면 이런 수작업을 LLM이 스스로 처리하도록 만들 수 있다. AI가 직접 브라우저를 제어하며 수정 사항을 적용하고, 스크린샷을 찍어 확인하고, 문제가 있으면 다시 수정하는 과정을 자동으로 수행한다.

## Playwright

> https://github.com/microsoft/playwright-mcp

Playwright는 Microsoft에서 개발한 브라우저 자동화 도구다. Chromium, Firefox, WebKit을 모두 지원하며, 헤드리스 모드로도 실행 가능하다. 테스트 자동화에 주로 사용되지만, MCP와 결합하면 LLM이 직접 브라우저를 제어할 수 있게 된다.

Playwright MCP를 통해 LLM은 웹페이지를 방문하고, 요소를 클릭하고, 스크린샷을 찍으며, JavaScript 코드를 실행할 수 있다. 이는 단순히 코드를 생성하는 것을 넘어, 실제로 브라우저에서 결과를 확인하고 수정하는 반복 작업까지 가능하게 만든다.

## 설치

Codex와 Claude Code 모두에서 Playwright MCP를 사용할 수 있다. 설치 방법은 간단하다.

### Codex

```bash
codex mcp add playwright npx "@playwright/mcp@latest"
```

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/playwright-mcp.assets/1.webp)

> Added global MCP server 'playwright'.

codex 실행 후 `/mcp` 를 입력해본다.

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/playwright-mcp.assets/2.webp)

> 정상적으로 MCP가 등록되었음

쿼리를 날려 동작을 테스트해본다.

```
관리 페이지에서 스크롤시에 sticky 되어야 하는 부분(다중선택 모드, 이미지 상세 정보)이 고정되지 않고 함께 스크롤 되는 문제가 있는데 그걸 수정해주고, playwright mcp 를 활용해서 정상적으로 적용되었는지 확인 해줘
```

![3](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/playwright-mcp.assets/3.webp)

> 스스로 페이지 방문 하고 버튼도 클릭해가며 스크린샷도 찍어 확인한다.

최종 완료 후 로그를 확인해보니 LLM은 다음과 같은 순서로 작업을 진행했다

1. 페이지 접속: `browser_navigate`로 관리 페이지 접속 
2. 현재 상태 파악: `browser_evaluate`로 DOM 구조와 CSS 속성 확인 
3. 문제 요소 식별: sticky가 적용되어야 할 요소들의 선택자 파악 
4. 코드 수정 시도: `browser_run_code`로 CSS 수정 적용 
5. 스크롤 테스트: 페이지를 스크롤하며 동작 확인
6. 스크린샷 캡처: 수정 전후 상태를 시각적으로 기록
7. 추가 수정: `browser_run_code`와 `browser_evaluate`를 여러 번 반복하며 미세 조정한다. LLM은 첫 시도에서 완벽하게 해결하지 못했지만, 스스로 문제를 파악하고 다시 수정하는 과정을 거쳤다. z-index 문제인지, position 속성 문제인지, 아니면 부모 요소의 overflow 설정 때문인지 하나씩 검증하며 해결책을 찾아갔다.

### Claude Code

클로드코드에서는 아래의 명령어로 mcp를 추가할 수 있으며 그 외 동작 및 사용법은 Codex와 동일하다.

```bash
# Local scope
claude mcp add playwright npx @playwright/mcp@latest

# User scope
claude mcp add playwright --scope user -- npx -y @playwright/mcp@latest
```

## 다양한 활용 시나리오

Playwright MCP의 활용 범위는 생각보다 넓다. 몇 가지 유용한 시나리오를 소개한다.

반응형 디자인 검증

> 모바일, 태블릿, 데스크톱 해상도에서 네비게이션 메뉴가 
> 올바르게 표시되는지 확인하고 문제가 있다면 수정해줘

LLM이 viewport 크기를 변경하며 각 해상도에서 레이아웃을 점검하고 미디어 쿼리를 조정한다.

접근성 개선

> 페이지의 모든 이미지에 적절한 alt 텍스트가 있는지 확인하고,
> 없다면 이미지 내용을 분석해서 추가해줘

이미지를 하나씩 확인하며 누락된 alt 속성을 찾아 적절한 설명을 추가한다.

### 폼 유효성 검증
> 회원가입 폼의 모든 필드를 테스트하고, 
> 에러 메시지가 제대로 표시되는지 확인해줘

다양한 입력값으로 폼을 테스트하며 유효성 검증 로직의 누락이나 오류를 찾아낸다.

### 성능 최적화
> 페이지 로딩 시 레이아웃 시프트가 발생하는 요소를 찾아서 수정해줘

페이지 로딩 과정을 관찰하며 CLS(Cumulative Layout Shift)를 유발하는 요소를 식별하고 개선한다.

## 마치며

Playwright MCP 의 도입으로 가장 큰 장점은 반복적인 확인 작업에서 해방된다는 점이다. 코드를 수정하고, 브라우저를 새로고침하고, 결과를 확인하는 사이클을 LLM이 대신 수행한다. 특히 여러 브라우저나 해상도에서 테스트해야 할 때 시간을 크게 절약할 수 있다.

다만 멀티모달 + 긴 LLM 사용시간으로 인해 많은 토큰을 사용하는건 감안해야 한다.

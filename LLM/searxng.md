# SearXNG 소개 및 OpenClaw 연동

## Intro

최근 OpenClaw를 사용하면서 가장 아쉬운 부분은 웹검색이었다. 에이전트가 최신 웹을 보게 하려면 결국 `web_search` 품질이 받쳐줘야 하는데, 기본 후보로 많이 거론되는 Brave Search는 편한 대신 유료 API이고 사용량 제한도 신경 써야 한다. 그래서 Brave Search fallback으로 DuckDuckGo가 보통 사용된다.

처음에는 Playwright로 여러 사이트를 방문하며 하는 식으로 기본 검색을 사용하도록 설정도 해봤는데 실제로 어느 정도 결과는 가져오지만, 여러 소스를 돌며 검색하기에는 일관성이 부족했고 속도도 아쉬웠다. 무엇보다 브라우저 화면을 그대로 읽다 보니 LLM이 바로 소비하기 좋은 형태가 아니어서 토큰도 더 쓰게 된다. 결국 내가 원한 것은 브라우저 자동화가 아니라, OpenClaw가 안정적으로 붙을 수 있는 검색 레이어였다.

그래서 여러가지 테스트 끝에 결정하고 지금 사용중인게 SearXNG다. 이번 글에서는 SearXNG에 대한 나의 생각과 현재 쓰는 설정을 정리해본다.

## SearXNG

### web_search

OpenClaw 공식 문서를 보면 `web_search`는 브라우저 자동화가 아니라 가벼운 HTTP 검색 도구라고 되어있다. 반면 web_fetch는 이미 알고 있는 URL을 읽는 용도이고, JS가 많은 페이지나 로그인 흐름은 별도의 Browser 도구로 처리하라고 안내한다. 결국 검색 provider는 “무엇을 읽을지 찾는 단계”를 맡고, 그 뒤의 fetch나 browser가 실제 읽기와 상호작용을 담당하는 셈이다.

그래서 OpenClaw에서 검색 provider는 옵션이 아니라 기반에 가깝다. 검색이 약하면 최신 웹 접근 전체가 흔들린다. 최근 OpenClaw를 만지면서 가장 먼저 체감한 한계도 바로 이 부분이었다. 기본설정상의 web_search는 검색 기능이 아쉽다.

### JSON

일반 검색 결과로 보여지는 HTML은 기본적으로 사람을 위한 형식이다. 카드, 광고, 탭, 추천 검색어, 불필요한 마크업이 함께 섞여 있기 때문에 브라우저로 보기에는 편해도 에이전트가 바로 이해하기에는 비효율적이다.

반면 OpenClaw 같은 에이전트는 구조화된 결과를 더 잘 다룬다. 제목, URL, snippet 같은 정보가 JSON으로 정리되어 있으면 어떤 링크를 볼지 고르기도 쉽고, 불필요한 화면 요소를 읽지 않아도 되니 토큰도 아낄 수 있다. 내가 Playwright 대신 별도 검색 레이어를 찾게 된 가장 큰 이유도 바로 이 점이었다.

SearXNG는 검색 자체를 대신 만들어주는 단일 검색엔진이라기보다, 여러 검색 서비스를 한 인터페이스로 묶어주는 메타서치 엔진에 가깝다. 공식 문서도 최대 250개의 검색 서비스를 집계할 수 있다고 설명한다.

개인적으로는 이 점이 핵심이었다. 특정 벤더 하나에 전적으로 의존하기보다, 어떤 엔진을 살리고 뺄지, 어떤 언어를 기본으로 둘지, 어떤 탭만 남길지 내가 직접 정할 수 있다. 검색 품질이 자동으로 좋아진다기보다 검색 경로에 대한 제어권을 돌려받는 느낌에 가깝다.

또 OpenClaw는 SearXNG를 단순 HTML 스크래핑이 아니라 네이티브 JSON API로 붙인다. 검색을 위해 다시 브라우저를 돌리는 구조가 아니라, 비교적 일정한 형태의 검색 결과를 받을 수 있다는 점에서 궁합이 괜찮았다.

### 비용

공식 문서 기준 OpenClaw는 Brave Search, DuckDuckGo, Exa, Firecrawl, Gemini, Perplexity, SearXNG 등을 지원한다. 이 중 Brave Search는 가장 무난해 보였지만, 2026년 4월 10일 기준 Brave Search API 페이지는 월 5달러 무료 크레딧과 이후 1,000건당 5달러 가격 정책을 안내하고 있다.

취미나 테스트 단계에서는 크게 부담이 아닐 수도 있다. 다만 개인적으로는 검색 레이어에 계속 외부 과금 구조를 얹는 것이 마음에 들지 않았다. 특히 OpenClaw처럼 이것저것 붙여보며 자주 실험할 때는 더 그랬다.

물론 SearXNG를 마냥 공짜라고만 보면 곤란하다. 공식 문서도 공개 인스턴스는 관리자 신뢰 문제가 있고, 보호가 약하면 CAPTCHA나 IP ban 때문에 결과가 줄 수 있다고 경고한다. 결국 SearXNG는 외부 API 비용을 운영 복잡도로 바꾸는 선택에 가깝다. 그래도 개인 서버에서 통제 가능한 검색 레이어를 갖고 싶다면 충분히 매력적이라고 생각했다. 설정과정이 다소복잡하다는 단점이 있지만 Claude Code나 Codex의 도움을 받으면 된다.

## 로컬테스트

OpenClaw에 바로 붙이기 전에 먼저 로컬에서 단독으로 띄워서 테스트했다. 브라우저에서 localhost:8888로 접속해 검색 결과가 의도대로 나오는지 확인해보는 편이 훨씬 마음이 편했다.

### compose.yaml

```yaml
services:
  searxng:
    image: docker.io/searxng/searxng:latest
    container_name: searxng-test
    restart: unless-stopped
    ports:
      - "127.0.0.1:8888:8080"
    volumes:
      - ./config:/etc/searxng
      - ./data:/var/cache/searxng
    healthcheck:
      test: ["CMD-SHELL", "wget -qO- http://127.0.0.1:8080/ >/dev/null 2>&1 || exit 1"]
      interval: 30s
      timeout: 5s
      retries: 5
      start_period: 20s
```

구성 자체는 복잡하지 않다. 설정 디렉터리를 바인드해서 `settings.yml`만 직접 관리하고, 캐시 디렉터리를 분리해두는 정도면 테스트용으로 충분했다.

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/searxng.assets/2.webp)

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/searxng.assets/1.webp)

> 도커를 띄우면 브라우저에서 localhost:8888 에 직접 접속해서 직접 검색을 해볼 수 있다. 
>
> 이 단계에서 아래의 설정값에 맞게 탭 구성이 의도대로 줄었는지 지정된 검색엔진들이 쓰였는지, Naver 결과가 실제로 잡히는지, 응답 속도는 괜찮은지 등을 확인하고 조율할 수 있다. OpenClaw 통합 전에 먼저 커스텀을 충분히 해두는걸 추천한다.
>
> Google News에 구문 분석 오류가 떠있는데 이런 부분도 확인하며 수정해줘야 한다.

### settings.yml

`settings.yml`에서는 기본 엔진을 그대로 다 열어두지 않고, 필요한 것만 남기는 방향으로 정리했다. 여기서 핵심은 `use_default_settings`로 upstream 기본값을 상속받고, 필요한 override만 얹는 방식이다. 개인적으로는 이게 설정 파일을 가볍게 유지하는 가장 좋은 방법이었다.

```yaml
# Keep this file limited to intentional overrides from upstream defaults.
use_default_settings:
  engines:
    # Use an allowlist so removed engines stay gone across upgrades.
    # Brave is intentionally excluded because it was producing rate-limit noise.
    # Wikipedia/Wikidata and the broader wiki-heavy set are intentionally excluded
    # because they were slow enough to become bottlenecks and were not returning
    # especially useful results for this instance.
    keep_only:
      - duckduckgo
      - duckduckgo images
      - duckduckgo news
      - duckduckgo videos
      - google
      - google images
      - google news
      - google videos
      - naver
      - naver images
      - naver news
      - naver videos

search:
  autocomplete: ""
  languages:
    - ko
    - en
  default_lang: "ko"
  formats:
    - html
    - json

server:
  secret_key: "CHANGE_ME"
  # GET makes browser navigation and tab handling nicer than POST for personal use.
  method: "GET"

ui:
  default_locale: "ko"

# Keep only tabs that still have engines in the allowlist.
categories_as_tabs:
  general:
  images:
  news:
  videos:

engines:
  # Use a custom XPath parser for Naver web because this instance wants Naver enabled
  # as a primary Korean source and tuned to the current page structure.
  - name: naver
    engine: xpath
    paging: false
    search_url: https://search.naver.com/search.naver?where=web&query={query}
    results_xpath: # 여기는 당시의 페이지 구조에 맞게 별도 조정
    url_xpath: # 여기는 당시의 페이지 구조에 맞게 별도 조정
    disabled: false

  # Enable Naver verticals by default.
  - name: naver images
    disabled: false

  - name: naver news
    disabled: false

  - name: naver videos
    disabled: false
```

여기서 특히 중요한 건 `search.formats`의 json이다. SearXNG 공식 Search API 문서에 따르면 JSON으로 결과를 받으려면 해당 포맷이 활성화되어 있어야 하고, 꺼져 있으면 403 Forbidden이 난다. OpenClaw에서 SearXNG를 provider로 붙일 생각이라면 사실상 미리 넣어두는 편이 안전하다.

기본 설정에서 naver는 disabled 되어 있다. 이번 용도에서는 한국어와 영어 웹 검색이 중심이라 Google, DuckDuckGo, Naver 정도만 남기는 편이 더 낫다고 판단했다. languages와 default_lang를 ko, en, ko로 둔 것도 같은 이유다. 탭 역시 general, images, news, videos 정도만 남겨서 UI를 단순하게 유지했다.

추가로 나는 Naver 웹 검색을 기본 엔진으로 쓰기 위해 현재 페이지 구조에 맞춘 xpath 엔진 설정도 따로 넣었다. 손이 가는 부분이지만, 한국어 검색 품질을 챙기려면 의미가 있다. 반면 Brave Search는 rate-limit noise가 있었고, Wikipedia, Wikidata처럼 상대적으로 느리거나 이번 용도에서 효율이 낮다고 느낀 쪽은 과감히 제외했다.

## OpenClaw 통합

### 버전

OpenClaw 공식 문서의 SearXNG Search 페이지를 보면 설정 자체는 매우 단순하다. SearXNG 인스턴스를 하나 띄우고, `openclaw configure --section web`에서 provider를 searxng로 고르거나 SEARXNG_BASE_URL만 지정하면 될 것처럼 보인다.

그런데 내 환경에서는 처음에 이 방식이 바로 동작하지 않았다. 확인해보니 2026년 4월 1일 공개된 OpenClaw 2026.4.1 릴리스에서야 web_search용 bundled SearXNG provider plugin이 추가되었다. 즉, 문서만 보면 간단해 보이지만 실제로는 설치된 OpenClaw 버전이 충분히 최신이어야 했다.

### compose

OpenClaw를 업데이트한 뒤에는 같은 compose 안에 SearXNG 서비스를 추가하는 방식으로 붙였다. 이번 구성에서는 OpenClaw와 SearXNG가 같은 네트워크에서 통신하므로, 테스트용 단독 인스턴스처럼 굳이 호스트 포트를 열지 않았다.

```yaml
services:
  searxng:
    image: docker.io/searxng/searxng:latest
    restart: unless-stopped
    volumes:
      - ./searxng/config:/etc/searxng
      - ./searxng/data:/var/cache/searxng
    healthcheck:
      test: ["CMD-SHELL", "wget -qO- http://127.0.0.1:8080/ >/dev/null 2>&1 || exit 1"]
      interval: 30s
      timeout: 5s
      retries: 5
      start_period: 20s
```

구성 자체는 복잡하지 않다. 핵심은 OpenClaw 옆에 SearXNG 서비스를 하나 더 두고, 설정 디렉터리와 캐시 디렉터리를 분리하는 정도다. 직접 포트를 외부에 노출하지 않아도 되니 compose가 조금 더 깔끔해졌다.

### provider

공식 문서 기준 SearXNG는 auto-detection order가 200이다. 그래서 다른 provider 키가 이미 들어 있는 환경이라면 자동 선택을 기대하기 어렵다. 실제로 붙일 때는 provider를 명시적으로 searxng로 잡는 편이 더 안전하다.

**.openclaw/openclaw.json**

```json
 "plugins": {
    "entries": {
      "browser": {
        "enabled": true
      },
      "searxng": {
        "enabled": true,
        "config": {
          "webSearch": {
            "baseUrl": "http://searxng:8080",
            "categories": "general,news",
            "language": "ko"
          }
        }
      },
      "openai": {
        "enabled": true
      }
    }
  },
...
 "tools": {
    "web": {
      "search": {
        "enabled": true,
        "provider": "searxng",
        "openaiCodex": {
          "enabled": false,
          "mode": "cached"
        }
      }
```

> Codex의 기본 검색엔진도 사용해봤는데, 실시간성이 많이 떨어져서 내 기준에서는 써먹을 수가 없었다.

plugins/searxng/openclaw.plugin.json이나 plugins/searxng/src/searxng-web-search-provider.js 같은 파일을 직접 만들어 넣지 않아도 된다는 점이 좋았다. 2026.4.1 버전 이전이라면 꽤 귀찮았을 일을 버전 업데이트 한 번으로 넘긴 셈이다.

## 마치며

에이전트에게 검색 능력은 정말 중요하다. 최근 OpenClaw를 만져보면서 특히 그 부분을 크게 느꼈고, 그래서 여러 우회 방법을 찾다가 SearXNG까지 오게 되었다. 지금 단계 기준으로는 Brave Search 같은 상용 검색 API 의존도를 낮추면서도, 브라우저 자동화보다 더 다루기 쉬운 검색 레이어를 만들고 싶을 때 가장 균형이 좋은 선택지라고 생각한다.

다만 한번 설정해 두면 끝나는게 아니고 검색엔진을 계속해서 커스텀 하며 내 입맛에 맞춰야하고, 검색 워크플로에 문제가 생겼을 때는 스스로 해결해야 한다는 점은 장점이기도 하고 단점이기도 하겠다.

**References**

- https://docs.openclaw.ai/tools/web
- https://docs.openclaw.ai/tools/searxng-search
- https://github.com/openclaw/openclaw/releases/tag/v2026.4.1
- https://docs.searxng.org/
- https://docs.searxng.org/dev/search_api.html
- https://docs.searxng.org/own-instance.html
- https://github.com/searxng/searxng
- https://brave.com/search/api/

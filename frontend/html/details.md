# HTML) details 와 summary 태그활용해 접기와 펼치기 간단 구현

## Intro

요즘에는 페이지에 표시할 내용이 너무 많을 때에는 중요한 정보를 한 페이지에 들어오도록 하면서, 간단한 클릭 한번으로 숨겨져 있는 정보들도 확인할 수 있도록 하기 위해 접기/펼치기를 많이 사용 하고 있습니다.

<img src=https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/html/details.assets/image-20211213220501354.webp width=750 height=410 alt=1>

> www.naver.com

거의 매일 접속하는 naver 페이지만 보더라도 메인에 접기/펼치기 버튼 두개가 바로 보입니다.

<img src=https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/html/details.assets/image-20211213220538608.webp width=750 height=420 alt=2>

> 검색 히스토리

![image-20211213220553681](https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/html/details.assets/image-20211213220553681.webp)

> 서비스 전체보기

물론 네이버에서 `details` 태그를 사용해서 접기와 펼치기를 구현한 것은 아닙니다. 시간적 여유가 있으며 접고 펼칠때에 상황에 맞는 여러가지 이벤트들을 넣고, 맘에 드는 css 를 적용하는 등 예전처럼 접고 펼치기를 구현 해 내려면 자바스크립트 코드를 작성하는 방법 밖에 없었습니다.

![image-20211213220902343](https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/html/details.assets/image-20211213220902343.webp)

> 네이버의 접기 펼치기 코드를 보니 a 태그에 href="#" 을 쓰고.. onclick="return false;" 해둔다음에 클릭 이벤트를 리스너로 받아 처리하는 듯 합니다. 네이버 개발자도 사람이었다! 

하지만 그냥 단순하게 접고 펼치는 기능 만 필요하다면 굳이 스크립트를 작성하고 리스너를 걸고 css로 스타일을 주고 할 것 없이 HTML이 기본적으로 제공하는 Details 태그를 활용 하면 아주 간단하게 구현 할 수 있습니다.

## Summary 태그

> https://developer.mozilla.org/en-US/docs/Web/HTML/Element/summary

### 예시

HTML의 `<summary>`element를 활용해 `<detail`> 요소의 요약이나 캡션을 지정 할 수 있습니다. `<summary>` 요소를 클릭하면 `<details>` 요소를 열고 닫으며 토글 시킵니다.

말로 표현하니 잘 와닫지 않으니 간단한 코드 예제를 작성 해 보겠습니다.

```html
<details>
    <summary>접기 버튼</summary>
Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.
</details>

```

정말 단순하게 details 태그 안에 summary 태그가 있는 형태 입니다. Run Pen 을 눌러 코드를 실행할 수 있습니다.

<iframe height="300" style="width: 100%;" scrolling="no" title="Untitled" src="https://codepen.io/shane-park/embed/preview/WNZoLgO?default-tab=html%2Cresult&editable=true&theme-id=dark" frameborder="no" loading="lazy" allowtransparency="true" allowfullscreen="true">
  See the Pen <a href="https://codepen.io/shane-park/pen/WNZoLgO">
  Untitled</a> by Shane Park (<a href="https://codepen.io/shane-park">@shane-park</a>)
  on <a href="https://codepen.io">CodePen</a>.
</iframe>

> 별것 도 아닌 것 같은데 아주 단순한 접기 펼치기가 완성되었습니다.

## 사용법

`<summary>` 요소는 heading content와 plain text 뿐만 아니라, HTML요소를 비롯한 모든 문장을 사용 할 수 있습니다. 한가지 유의해야 할 점으로는 `<summary>`는 반드시 `<details`> 요소의 첫번째 자식으로만 들어가야 합니다. 그래서, summary를 클릭 했을 때에는 summary의 부모 `<details>` 이 토글되며 열리거나 닫히게 됩니다.

### 기본 예제

```html
<details open>
  <summary>전체 보기</summary>
  <ol>
    <li>보유중인 현금: $500.00</li>
    <li>요금: $75.30</li>
    <li>지불 마감일: 5/6/19</li>
  </ol>
</details>
```

<iframe height="300" style="width: 100%;" scrolling="no" title="details2" src="https://codepen.io/shane-park/embed/preview/vYeyvoL?default-tab=html%2Cresult&editable=true&theme-id=dark" frameborder="no" loading="lazy" allowtransparency="true" allowfullscreen="true">
  See the Pen <a href="https://codepen.io/shane-park/pen/vYeyvoL">
  details2</a> by Shane Park (<a href="https://codepen.io/shane-park">@shane-park</a>)
  on <a href="https://codepen.io">CodePen</a>.
</iframe>

### html을 사용한 summary

<iframe height="300" style="width: 100%;" scrolling="no" title="details3" src="https://codepen.io/shane-park/embed/preview/PoJbVYW?default-tab=html%2Cresult&editable=true&theme-id=dark" frameborder="no" loading="lazy" allowtransparency="true" allowfullscreen="true">
  See the Pen <a href="https://codepen.io/shane-park/pen/PoJbVYW">
  details3</a> by Shane Park (<a href="https://codepen.io/shane-park">@shane-park</a>)
  on <a href="https://codepen.io">CodePen</a>.
</iframe>

> strong 태그를 사용 해 보았습니다. CodePen의 `strong` 태그를 `h2`로 바꿔서 한번 실행 해 보세요.

마찬가지로, 이미지를 넣을 수도 있으며 용도에 따라 하나의 커다란 블럭이 summary가 될 수도 있습니다.

## Broswer compatibility

![image-20211213223510845](https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/html/details.assets/image-20211213223510845.webp)

브라우저 호환성도 크게 걱정 하지 않아도 됩니다.

우리의 영원한 적인 Internet Explorer에서는 지원을 하지 않습니다. Safari에 `No` 표시 되어 있는 `display: list-item`는 단순하게 summary 요소가 해당 기본 스타일을 포함하는 지에 대한 여부이기 때문에 특별히 신경 쓰지 않고 사용 하실 수 있습니다.

그럼 Internet Explorer에서는 어떻게 나올지, IE를 염두해야 하는 환경에서는 사용하면 안될지 걱정 하실 수도 있지만, 단순하게 접기/펼치기가 지원이 안되고 그냥 항상 펼쳐 져 있기 때문에 아주 치명적이지는 않습니다.

힘들게 스크립트 작성하지 말고 태그 하나로 간단하게 접기 펼치기 해결하세요! 

 
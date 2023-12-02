# [Chrome] 인쇄할때 보이는 print css 브라우저별로 미리보기

## Intro

웹 어플리케이션을 사용하면서 웹 브라우저에 보이는 화면을 그대로 프린트 하는 일은 사실 흔하지 않다.

연구데이터에 관련된 도메인으로 몇몇 프로젝트를 진행하다보니, 브라우저에서 보이는 화면 프린트 했을 때의 인쇄 결과물에 대한 요구사항이 종종 들어올 때가 있는데, 화면에 보여지는 것과 최대한 비슷하게 해달라는 고객도 있고 특정 포맷으로 보기좋게 인쇄되기를 원하는 클라이언트도 있었다.

사실 브라우저에서 인쇄물에 대해서도 css를 지원하기 때문에 어느정도의 css에 대한 이해가 있다면 어렵지 않게 해낼 수 있지만, 자주하는 일이 아니기 때문에 매번 할 때마다 다시 기억을 더듬어야 하고, 검색했을때 그렇게 쉽게 나오는 정보도 아니기 때문에 이번 기회에 한번 정리를 해두려고 한다. 

특히 브라우저에서 바로 미리보기를 하며 수정한다면 효율적으로 작업해 시간을 많이 절약할 수 있다.

## @media print

CSS에서 `@media print`는 인쇄를 위한 스타일을 정의할 때 사용하는 문법이다. 

웹 페이지를 화면에 표시할 때와 달리, 인쇄 시점에만 특정 스타일이 적용되도록 할 수 있다. 예를 들어, 배경색이나 이미지를 제거하거나, 글꼴 크기를 조정하는 등의 변경이 가능한데 잘만 활용하면 꽤나 그럴싸한 결과를 만들 수 있다.

본 블로그를 예시로 `@media print` 에 대한 내용을 예제로 들어겠다.

### CSS 적용 전

![image-2023120240621965 pm](https://raw.githubusercontent.com/ShanePark/mdblog/main/frontend/css/print-css.assets/1.webp)

어느 정도는 그럴 싸 해 보이기도 하지만 몇몇 불필요한 내용들이 눈에 띈다. 중간에 애드센스가 들어가는 큰 빈자리도 거슬린다.

이제 CSS를 적용해보자. 인쇄물에 대한건 `@media print {}` 블럭 안에 작성하면 된다.

### CSS 적용 후

감출건 감추고 조절이 필요한 요소는 약간의 변화를 줬다. 

```css
@media print {
  header,
  .toc,
  .revenue_unit_wrap,
  .dark-toggle,
  .box-meta .box-info,
  .article-footer,
  .area-aside,
  #upBtn,
  .container_postbtn,
  .menu_toolbar
  {
    display: none !important;
  }

  #container {
    margin-top: 0px;
  }

  #tt-body-page .article-view h2 {
    margin-top: 30px;
  }

  #tt-body-page .article-view code {
    border-radius: 5px;
    border: 1px solid black;
    font-weight: bold;
    color: black;
  }
  
}
```

참고 인쇄시에는 브라우저 자체적으로 Background graphics 가 체크 해제되어있는 경우가 많은데, 프린트 하면서 불필요한 잉크 사용을 줄이려고 의도된 것으로 보인다. 그렇기때문에 백그라운드 컬러에 의존하는 CSS 들은 border를 그리거나 하는 방식으로 우회하는편이 좋다.

![image-2023120251232659 pm](https://raw.githubusercontent.com/ShanePark/mdblog/main/frontend/css/print-css.assets/3.webp)

> Background graphics가 체크 해제되어 있는 상태

이제 결과물을 확인하자.

![image-2023120250618017 pm](https://raw.githubusercontent.com/ShanePark/mdblog/main/frontend/css/print-css.assets/2.webp)

>  CSS 적용 전에 비해서 인쇄물이 개선되었다.

## 브라우저별 인쇄물 미리보기

다양한 웹 브라우저는 각기 다른 방식으로 웹 페이지를 인쇄한다. Chrome, Firefox, Safari 등 주요 브라우저별로 인쇄 미리보기를 하는 방법에 대해 알아보도록 하겠다.

### Chrome

가장 많이 사용되는 크롬 브라우저다. 브라우저 버전이 올라가면서 메뉴의 위치는 달라질 수 있지만 큰 틀에서는 어느정도 일관성을 유지하기 때문에 일단 알아두면 찾기 어렵지 않을것이다.

`F12` 키를 눌러 개발자도구를 켠다. 

우측 상단에 **`⋮`** 모양 버튼을 클릭하면 `More tools` 메뉴가 보이는데 거기에서 `Rendering`을 선택한다. 

![image-2023120251913518 pm](https://raw.githubusercontent.com/ShanePark/mdblog/main/frontend/css/print-css.assets/4.webp)

Rendering 메뉴에 들어가면 다양한 렌더링 옵션이 보인다. 다크모드 토글도 간단하게 해볼 수 있으니 필요하다면 활용해보자.

![image-2023120252134409 pm](https://raw.githubusercontent.com/ShanePark/mdblog/main/frontend/css/print-css.assets/5.webp)

우리가 필요한건 `Emulate CSS media type` 이다. `No emulation`으로 선택된걸 클릭해보면 총 세가지 옵션이 존재한다.

![image-2023120252332843 pm](https://raw.githubusercontent.com/ShanePark/mdblog/main/frontend/css/print-css.assets/6.webp)

이 중 `print`를 선택하면 인쇄물을 미리볼 수 있다. 실시간으로 개발자모드에서 CSS를 편집해보며 미리보기 할 수 있기 때문에 간편하다.

### Firefox

firefox는 매우 간단한데, 개발자도구를 열고 우측 상단에 있는 문서모양 아이콘을 클릭하면 토글된다.

![image-2023120252514365 pm](https://raw.githubusercontent.com/ShanePark/mdblog/main/frontend/css/print-css.assets/7.webp)

### Safari

사파리에서도 매우 간단하게 프린트기 모양 버튼을 눌러 토글한다. 

![image-2023120253202758 pm](https://raw.githubusercontent.com/ShanePark/mdblog/main/frontend/css/print-css.assets/8.webp)

크롬 빼고는 다 간단하다. 개인적으로는 크롬에서도 버튼을 추가해줬으면 한다.

## 글마침

지금까지 웹 페이지를 인쇄할 때 적용되는 `@media print` CSS및 주요 브라우저에서의 인쇄 미리보기 방법에 대해 알아보았다. 

이를 잘 활용하면 인쇄가 빈번한 웹페이지를 개발할 때에 도움이 되는 것은 물론이고, 웹서핑중 개인이 인쇄하고자 하는 페이지를 입맛에 맞게 출력해내는데도 유용할 것이라 생각한다.
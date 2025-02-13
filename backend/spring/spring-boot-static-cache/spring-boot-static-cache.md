# [Spring Boot] 정적 파일 캐싱 및 자동 버전 관리 설정

## Intro

웹 애플리케이션에서 정적 파일(이미지, CSS, JS 등)의 성능 최적화는 중요하다. 캐시를 활용하면 불필요한 네트워크 요청을 줄여서 성능을 개선할 수 있으며, 파일의 버전 관리를 자동화하면 배포할 때 브라우저가 새로운 리소스를 제대로 가져오게 만들 수 있다. 잘못 캐시해두면 기껏 새로운 정적 파일을 배포했지만 사용자들은 브라우저에 캐시된 엉뚱한 정적자원을 활용하는 사태가 벌어진다.

Spring Boot에서는 `spring.web.resources.cache.period` 및 `spring.web.resources.chain.strategy.content.enabled` 설정을 활용하면 정적 파일의 캐싱과 버전 관리를 쉽게 적용할 수 있다.

이번 글에서는 새로운 Spring Boot 프로젝트를 생성하고, 정적 리소스 캐싱 및 자동 버전 관리 설정을 적용한 후, 브라우저에서 캐시가 잘 동작하는지 확인하는 과정을 확인한다.

## 테스트 프로젝트 생성

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/spring-boot-static-cache/spring-boot-static-cache.assets/1.webp)

> Spring Initializr

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/spring-boot-static-cache/spring-boot-static-cache.assets/2.webp)

JDK는 21, 스프링부트는 최신 버전으로 선택하고 의존성은 Spring web 및 Thymeleaf 만 간단하게 포함시킨다. 나중에 추가하긴 했는데 hot swap을 하려면  `spring-boot-devtools` 도 포함하는게 낫다.

이후 `src/main/resources/static` 에 두개의 파일을 생성해준다.

- style.css
- script.js

내용은 대충 아무거나 적어둔다.

```css
h1 {
    color: red;
}
```

```js
function sayHi() {
  alert('Hi!');
}
```

이번에는 `src/main/resources/templates/index.html` 파일을 생성한다.

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="ko">
<head>
  <title>Spring Boot 정적 파일 캐싱 테스트</title>
  <link rel="stylesheet" th:href="@{/style.css}"/>
  <script th:src="@{/script.js}"></script>
</head>
<body>
<h1>Spring Boot 정적 파일 캐싱 테스트</h1>
</body>
</html>
```

어플리케이션을 실행해서 브라우저에서 확인해보자.

![3](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/spring-boot-static-cache/spring-boot-static-cache.assets/3.webp)

> 새로고침을 누르며 Network 탭을 확인해본다. 매번 정적 자원들을 새로 요청해서 받아오는 것을 확인할 수 있다.

## 캐시

### 설정

이제 캐시를 하도록 설정해보자. application.yml에 아래와 같이 작성한다. 정적자원을 720시간(30)일 동안 캐시한다는 뜻이다.

```yaml
spring:
  web:
    resources:
      cache:
        period: PT720H 
```

어플리케이션을 새로 실행해준다음에 다시 브라우저에서 확인해본다.

![4](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/spring-boot-static-cache/spring-boot-static-cache.assets/4.webp)

크롬 브라우저에서는 (memory cache) 라고 나온다.

![5](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/spring-boot-static-cache/spring-boot-static-cache.assets/5.webp)

> 정적파일들이 캐시되었기 때문에 페이지 로딩 속도가 빨라진다.

### 문제점

그런데 이 경우 문제가 발생하는데, css 를 다음과 같이 변경하고

```css
h1 {
    color: blue;
}
```

새로 고침을 해도, 캐시 된 파일을 불러온다. 어플리케이션을 재구동 해도 마찬가지다.

![6](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/spring-boot-static-cache/spring-boot-static-cache.assets/6.webp)

이 문제를 해결하기 위해 아래와 같이 v 파라미터를 붙이는 방법이 있는데 수정할 때 마다 일일히 바꿔줘야 하는 번거로움이 있다.

```html
  <link rel="stylesheet" th:href="@{/style.css?v=1}"/>
  <script th:src="@{/script.js?v=2}"></script>
```

## 자동 버전생성

### 실습

이번에는 위에서 발생한 문제를 해결하기 위해 아래와 같이 `application.yml` 파일을 수정한다.

```yaml
spring:
  web:
    resources:
      cache:
        period: PT720H 
      chain:
        strategy:
          content:
            enabled: true
            paths: /**
```

정적 파일의 자동 버전 관리(해시 기반 URL 변경) 설정을 활성화 하는건데, `/**`를 설정해서 모든 정적 리소스를 대상으로 한다. 이렇게 하면 파일이 변경될 때 새로운 해시값이 자동으로 파일명 뒤에 달린다.

바로 확인해보자.

![7](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/spring-boot-static-cache/spring-boot-static-cache.assets/7.webp)

이번에는 css 및 js 의 파일명 뒤에 길게 문자들이 많이 달렸고, 두번째 요청부터는 정상적으로 cache도 이루어진다.

이 상태에서 css 파일을 조금 바꿔서 새로고침해보면

```css
h1 {
    color: green;
}
```

![8](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/spring-boot-static-cache/spring-boot-static-cache.assets/8.webp)

css 는 변경되었기 때문에 파일명이 변경되었고 새로 불러왔지만, script는 변화가 없기에 기존의 cache를 계속 이용한다. 물론 한번 더 페이지를 새로고침 하면 변경된 css도 cache에서 불러온다.

### 원리

### Spring의 `ResourceUrlEncodingFilter`

Spring Boot에서 `spring.web.resources.chain.strategy.content.enabled: true`를 설정하면 **정적 리소스 URL에 자동으로 해시값을 추가하는 기능**이 활성화된다.

내부적으로 `ResourceUrlEncodingFilter`가 동작하며, 이를 통해 정적 파일의 내용을 기반으로 해시값이 생성되고, 파일의 URL에 추가된다.

예를 들어 `style.css` 파일이 있을 때,

- 원래 경로: `style.css`
- 해시 적용 후: `style-b186f35fefc62d625cba798a79010b2b.css`
- 파일이 변경되면: `style-35dce2f486e998bce50c3fe97ae64877.css`

### `Cache-Control` 헤더 설정

Spring Boot는 `spring.web.resources.cache.period` 설정을 기반으로 `Cache-Control` HTTP 헤더를 자동으로 추가한다.

- `max-age=2592000` (30일)
- 브라우저는 지정된 기간 동안 캐시 유지

### 주의사항

정적 리소스에 자동 해시값이 붙도록 하려면 반드시 **템플릿 엔진의 URL 표현식을  활용해야 한다**. 단순히 아래와 같이 HTML 태그에서 직접 경로를 지정하면

```html
<link rel="stylesheet" href="/style.css">
```

Spring Boot의 자동 버전 관리 기능이 작동하지 않아, 해시가 추가되지 않는다. 자동 해시값 적용 기능은 내부적으로 `ResourceUrlEncodingFilter`가 Thymeleaf 등의 템플릿 엔진과 연동되어 동작하는데, 이를 위해서는 아래와 같이 템플릿 엔진 전용 URL 표현식을 사용해야 한다.

```html
<link rel="stylesheet" th:href="@{/style.css}">
```

이렇게 작성하면, 설정된 `spring.web.resources.chain.strategy.content.enabled: true` 옵션에 따라 파일 내용 기반의 해시값이 URL에 자동으로 추가된다.

다른 템플릿 엔진(예: FreeMarker, Mustache 등)을 사용하는 경우에는 해당 템플릿 엔진에서 URL을 생성할 때 해시값 자동 추가 기능을 지원하는지 확인해야 한다. 만약 기본 기능이 없다면, 별도로 `ResourceUrlEncodingFilter`를 등록하거나, URL 변환 로직을 구현하여 해시값을 적용하는 방법을 고려해야 한다.
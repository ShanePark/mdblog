# 스프링 시큐리티 필터가 2번 중복적용된 문제 해결

## Intro

JWT 토큰 값을 읽어서, 상황에 따라 인증하거나 만료되었다면 리프레시 토큰을 사용해 새로운 토큰을 발급받는 역할을 하는 `JwtAuthFilter` 를 만들어 등록해두었다.

시큐리티 필터체인에서 아래와 같이, AuthorizationFilter 이전에 등록해서 작동하도록 해 두었는데

```kotlin
  http.addFilterBefore(jwtAuthFilter, AuthorizationFilter::class.java)
```

요청이 올 때마다 자꾸 해당 필터를 두번씩 거치는 문제가 있었다.

## 원인

간편하게 Bean으로 등록 해두고 사용하려고 클래스에 `@Component` 어노테이션을 붙여서 사용했는데 그게문제였다.

아래는 스프링 부트 도큐먼트에서 Filter에 관한 내용이다

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/security-filter-dupe.assets/1.webp)

> https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#web.servlet.embedded-container.servlets-filters-listeners

요약하자면, 스프링 빈으로 등록된 Servlet, Filter, Listener 인스턴스들은 내장 컨테이너에 자동으로 등록된다는 이야기. 

특히 맨 아래 보이는 경고에서는, Filter 빈을 등록하면 어플리케이션의 생명주기에서 매우 이른시기에 등록되기 때문에 다른 빈들과 상호작용이 필요하다면 `DelegatingFilterProxyRegistrationBean` 을 고려하라고 한다. 

> 예전에 스프링 프레임워크를 배울때는 필터와 인터셉터의 차이에 대해 스프링의 관여 여부를 따지곤 했었는데, `DelegatingFilterProxyRegistrationBean` 덕분에 스프링이 관리하는 필터도 서블릿 필터 체인에 등록할 수 있게 되었다.

그러니 원인을 분석해보자면 똑같은 필터가 자동으로, 수동으로 총 2번 등록되었다는 이야기다. 자동으로 등록된 필터의 문제점은, URL 패턴 없이 그냥 모든 요청을 대상으로 다 적용이 되며 특히 순서를 보장하기도 굉장히 까다롭다는 것이다.

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/security-filter-dupe.assets/2.webp)

스프링 시큐리티에서 ignoring 등 여러가지 장치를 두어도 전혀 먹히지 않는 이유가 그것이다. 필터가 시큐리티 필터체인 밖에 존재하기 때문이다.

> https://docs.spring.io/spring-security/reference/servlet/architecture.html

## 해결

필터가 자동으로 등록되지 않도록 `@Component` 어노테이션을 제거한다. 

스프링 시큐리티 설정파일에서는 해당 `Filter`를 생성자를 통해 생성해 필터체인에 등록하면 되는데, 필터에 필요한 객체들은 해당 설정파일 객체의 생성자를 통해 주입받으면 된다. 이후 수동으로 원하는 위치에 해당 필터를 적용하면 된다.

```kotlin
val jwtAuthFilter = JwtAuthFilter(authService, jwtConfig, isSecure)
http.addFilterBefore(jwtAuthFilter, AuthorizationFilter::class.java)
```

스프링은 사용하기 쉽고 편하지만 워낙 내부에서 복잡하게 많은 일이 일어나고, 방대한 프레임워크기 때문에 잘 알고 쓰는게 중요하다. 

스프링시큐리티는 복잡하고 어려울 뿐만 아니라 사용하기도 전혀 쉽지 않기 때문에 충분한 학습을 하며 사용하는 것이 좋겠다.

**References**

- https://www.baeldung.com/spring-boot-add-filter
- https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle
- https://docs.spring.io/spring-security/reference
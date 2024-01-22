# [Spring Boot] 정적자원 캐싱하는방법

## Intro

가족들이 필요로해서 만들었고, 운영한지 이제 한 2년정도 된 스프링부트로 만든 토이 프로젝트가 있다. 

리액트를 필두로 몇년째 이어지는 프론트엔드 춘추전국시대에 특별히 마음에 들거나 잘하는 프론트엔트 프레임워크가 없다보니, 사용하고 싶은 프론트엔드 스펙이 정해지면 그때 떼낼 생각으로 최대한 API콜 위주로 하며 thymeleaf로 개발했고, 아직까지는 큰 불편없이 쓰고 있다.

지금까지는 `부트스트랩` `jQuery` `vue.js` 등의 라이브러리를 대부분 CDN에서 받아오도록 해놨었는데 이제부터는 소스코드에 포함해 배포하기로 했다. 

오프라인이나 내부망에서 사용할 프로젝트는 아니기때문에 특별히 변경해야 할 이유는 없었지만 그래도 어플리케이션의 전체적인 구동을 통제하에 두고싶은 마음에 해보았다. 

## 문제

그런데 CDN을 쓰다가 WAS 에서 정적 자원으로 제공하는 방식으로 변경하고 보니 큰 문제가 생겼다. CDN은 콘텐츠를 효율적으로 캐싱하고, 부하를 전 세계에 분산시켜주는데 그 엄청난 이점을 스스로 포기한것이다. 

localhost 에서 개발할때는 느끼지 못했는데, 새로 배포하고 보니 SPA도 아니라서 매번 각종 폰트와 라이브러리들을 새로 불러와서 굉장히 느렸다. 트래픽이 많을 프로젝트가 아니라서 서버 부하는 부담이 없었는데 페이지 로딩 속도가 느린건 확실히 문제다.

각종 css, js 및 이미지와 폰트는 브라우저가 캐싱해두도록 하자.

## 스프링부트 정적자원 캐싱

### 캐시 등록 전

브라우저에서 캐시를 하도록 하려면 `Cache-Control` 헤더를 사용해야 한다.

먼저 지금의 상태를 확인해보자.

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/cache-static-resource.assets/1.webp)

> Cache-Control 헤더가 no-store 로 지정된 상태.

스프링부트에서 cacheControl을 설정하는 방법은 여러가지가 있다. 

- Controller에서 ResponseEntity를 반환하며 `.cacheControl` 메서드를 사용하는 방법
- WebRequest 의 `checkNotModified` 메서드로 확인하고 `NOT_MODIFIED`를 반환하는 방법
- resourceHandler를 등록하는 방법
- `application.yml`에 정적자원 캐시 정책을 설정

이 중에 지금의 상황에서는 세번째, 네번째 방법이 어울린다.

### resourceHandler 등록

먼저 resourceHandler 등록하는 방법을 알아보자. 

아래의 코드는 모두 스프링 3.2 버전을 기준으로 작성되었으며, Kotlin 언어로 작성되었지만 java와 거의 차이가 없다.

아래의 코드는 외부 라이브러리들을 모아둔 `resources/lib` 내의 모든 자원들에 대해 1년동안 캐시하도록 설정한 모습니다.

${code:WebMvcConfigurer.kt}

```kotlin
import com.tistory.shanepark.dutypark.security.config.LoginMemberArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.http.CacheControl
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.time.Duration

@Configuration
class WebMvcConfigure : WebMvcConfigurer {
  
		...
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/lib/**")
            .addResourceLocations("classpath:/static/lib/")
            .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic())
    }

}

```

위에 보이는 것 처럼 resourceHandler를 등록해줬다. 대상 경로 패턴, 로케이션을 지정 하고, 캐시를 하고 싶은 기간을 등록하면 된다.

![3](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/cache-static-resource.assets/3.webp)

그러고 확인해보면, Cache-Control 부분에 max-age가 설정된 것이 확인된다.

브라우저를 새로고침 해보면

![4](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/cache-static-resource.assets/4.webp)

>  라이브러리로 등록된 자원들이 `memory cache` 되어 로딩 없이 페이지를 띄우는 것이 확인된다.

### 정적자원 캐시 정책 설정

이번에는 조금 더 쉽게 하는 방법이다. `application.yml`에 아래의 내용을 추가해준다.

${code:application.yml}

```yaml
spring:
  web.resources.cache.period: PT720H # 30 days
```

`PT720H`는 720시간을 말하는데, 해당 문법은 java.time.Duration 클래스를 확인해보면 자세히 나와있다.

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/cache-static-resource.assets/2.webp)

이렇게 설정만 해주면 간단하게 정적자원에 캐시정책이 적용된다.

![5](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/cache-static-resource.assets/5.webp)

아까 인터셉터를 적용했을때는 `lib` 경로만을 대상으로 했었는데, 이번에는 resources handler에 의해 다루어지는 모든 정적자원이 대상이 되었다. 

다루어지는 경로들의 목록은 아래와 같다.

![6](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/cache-static-resource.assets/6.webp)

> WebProperties.java 내의 Resources 클래스

## 주의할점

마지막으로 주의할 점이 있는데, 이렇게 한번 캐시되어버린 정적 자원은 나중에 문제를 유발 할 수 있다.

기껏 파일을 수정했는데 클라이언트에서는 본인들이 이미 캐시해둔 css 혹은  js 파일등을 계속 쓰기 때문이다. 이때는 `/css/base.css?v=20240121` 이런식으로 버전 전략을 활용하는 방법이 있다. 

아주 자주 바뀌는게 아니라면 귀찮음이 덜할 것이고 너무 자주 바뀐다면 캐시하지 않거나 캐시 기간을 아주 짧게 가져가면 될 것이다.

끝

**References**

- https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-config/static-resources.html
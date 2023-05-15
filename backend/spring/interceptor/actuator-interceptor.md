# [Spring] /actuator 엔드포인트에 인터셉터 적용 문제

## Intro

사이드 프로젝트에 actuator 를 적용해보았다. 

그런데 `/actuator` 경로를 그냥 오픈하고 싶지는 않았는데, 마침 기존에 관리자 페이지 접근 권한 부여를 위해 사용하던 인터셉터가 하나 있어서, actuator 엔드포인트에 등록 해 두고 그대로 사용하면 되겠다 싶었다.

아래 코드는 기존의 인터셉터 등록에 관련된 코드.

```kotlin
@SpringBootApplication(exclude = [UserDetailsServiceAutoConfiguration::class])
@EnableJpaAuditing
@EnableScheduling
class DutyparkApplication(
    private val authService: AuthService,
    private val jwtConfig: JwtConfig,
    @Value("\${server.ssl.enabled}") private val isSecure: Boolean
) : WebMvcConfigurer {
    override fun addInterceptors(
        registry: InterceptorRegistry
    ) {
        registry.addInterceptor(JwtAuthInterceptor(authService, jwtConfig.tokenValidityInSeconds, isSecure))
            .addPathPatterns("/**")
            .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.map", "/error")
            .order(0)

        registry.addInterceptor(AdminAuthInterceptor())
            .addPathPatterns("/admin/**").order(1)
    }
}
```

Jwt 인증처리의 경우에는 필터로 처리하는게 일반적이지만, 인터셉터로 처리하면 어떤 장단점이 있을까 궁금하기도 해서 청개구리 심보로 인터셉터로 등록해서 처리하고 있었다. 마찬가지로, 관리자 권한에 대해서도 인터셉터로 처리를 하고 있었는데 드디어 발목잡는 일이 생겼다.

앞으로는 인증 처리에 필터를 사용하는 이유에 대해서 몇가지 근거를 더 댈 수 있겠다.

처음엔 정말 단순하게 생각해서 `/actuator` 엔드포인트도 pathPattern에 추가하면 간단히 처리할 수 있을 거라 생각했다.

```kotlin
registry.addInterceptor(AdminAuthInterceptor())
  .addPathPatterns("/admin/**", "/actuator/**").order(1)
```

그런데 참 희한하게도 관리자 권한이 없는데도 불구하고 `/actuator` 엔드포인트에 아무런 문제 없이 도달이 가능했다.

심지어는 아래와 같이 actuator 경로를 이미 인증 처리가 잘 진행되고 있는 `/admin`의 하위로 보내버려도

```yaml
management:
  endpoints:
    web:
      base-path: "/admin/actuator"
```

아무런 인증 없이도 해당 엔드포인트에 자유롭게 접근 할 수 있었다.

![image-20230515221458886](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/interceptor/actuator-interceptor.assets/1.webp)

> 로그인 하지 않아도 actuator 를 자유롭게 접근 가능

본 글은 이 문제의 원인과 해결 방안에 대한 내용을 다루어 볼 것이다.

## 원인

인터셉터는 DispatcherServlet이 처리하는 요청에만 적용되기 때문에, 디스패처가 아닌 `WebMvcEndpointHandlerMapping` 에 의해 처리되는 `/actuator` 엔드포인트의 경우에는 등록해둔 인터셉터가 전혀 작동을 하지 않는다.

![image-20230515232554070](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/interceptor/actuator-interceptor.assets/3.webp)

> order `-100`임 수고

이 경우에는 역시 여러분이 생각하는 것 처럼 `필터`가 훌륭한 모범 답안이다. `DelegatingFilterProxy` 덕분에 필터 내부에서도 스프링 빈을 얼마든지 주입받아 사용할 수 있기 때문에 JWT 및 리프레시 토큰등의 처리도 필터레벨에서 자유롭게 처리하는게 가능하다.

하지만, 여전히 미리 만들어 두었던 인터셉터를 간편하게 등록해서 사용하고 싶다는 미련이 떠나지 않는다.

관련 내용을 검색하다 보니 Github의 Spring boot 코드 저장소에서도 관련 이슈가 등록되어 논의되었던 기록을 발견했다.

> https://github.com/spring-projects/spring-boot/issues/11234

해당 이슈에서도 "나는 인터셉터를 쓰고 싶어" 를 주장하는 쪽과 "그러지 말고 필터써라" 를 주장하는 집단이 치열한 토론을 벌였는데, Andy Wilkinson은 여러 고려끝에 interceptor로 actuator 엔드포인트를 컨트롤하는 방법을 <u>받아들이지 않기로</u> 결정했다고 한다. 쭉 읽어보면 인터셉터를 주장하던 쪽도  납득하고 필터사용을 받아들이는 분위기다.

## 해결

인터셉터로 처리하려고 했던 인증 부분을 필터로 리팩터링 해주면 된다. 끝.

하지만 이렇게 끝내기는 아쉽다. 인터셉터로 한번 처리해보고 싶다는 미련이 떠나질 않는다. 다른 사람들도 이런 똥고집을 부리고 싶을 때가 있을까?

> 필터가 적절한건 나도 알겠는데, 되는지만 확인해보고 싶다. 방법이 아에 없지는 않을 거 아닌가..

그래서 되는것을 확인만 해보기 위해서 `/actuator` 엔드포인트에 인터셉터를 적용해보려 한다. 위의 Github에 등록된 이슈에서 **[jnizet](https://github.com/jnizet)**가 작성한 Code snippet을 참고하였다.

먼저 `ActuatorConfig.kt` 라는 이름으로 클래스를 하나 생성한다.

${code:ActuatorConfig.kt}

```kotlin
@Configuration
class ActuatorConfig(
    private val authService: AuthService,
    private val jwtConfig: JwtConfig,
    @Value("\${server.ssl.enabled}") private val isSecure: Boolean
) : WebMvcEndpointManagementContextConfiguration() {

    override fun webEndpointServletHandlerMapping(
        webEndpointsSupplier: WebEndpointsSupplier?,
        servletEndpointsSupplier: ServletEndpointsSupplier?,
        controllerEndpointsSupplier: ControllerEndpointsSupplier?,
        endpointMediaTypes: EndpointMediaTypes?,
        corsProperties: CorsEndpointProperties?,
        webEndpointProperties: WebEndpointProperties?,
        environment: Environment?
    ): WebMvcEndpointHandlerMapping {

        val webEndpointServletHandlerMapping = super.webEndpointServletHandlerMapping(
            webEndpointsSupplier,
            servletEndpointsSupplier,
            controllerEndpointsSupplier,
            endpointMediaTypes,
            corsProperties,
            webEndpointProperties,
            environment
        )

        webEndpointServletHandlerMapping.setInterceptors(
            JwtAuthInterceptor(
                authService,
                jwtConfig.tokenValidityInSeconds,
                isSecure
            )
        )
        webEndpointServletHandlerMapping.setInterceptors(AdminAuthInterceptor())

        return webEndpointServletHandlerMapping
    }
}
```

코드를 보면 알겠지만 이 방법은 WebMvcEndpointManagementContextConfiguration 클래스를 확장한다.

재밌게도 해당 클래스를 확인 해 보면 위에서 그렇게 인터셉터로 처리하는걸 안 받아주겠다고 거절하던 `Andy Wilkinson` 이 직접 작성한 클래스다.

![image-20230515224208767](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/interceptor/actuator-interceptor.assets/2.webp)

> 안된다고 거절하자 그가 작성한 클래스를 찾아가 괴롭히기로 결정 했다.

나는 위에서 총 2개의 인터셉터를 등록 했는데, `AdminAuthInterceptor`가 정상적으로 작동하려면 `JwtAuthInterceptor` 가 먼저 동작을 해야 하기 때문에 그렇다. 

스프링 빈을 자유롭게 주입받아 사용할 수 있기 때문에 동일한 인터셉터를 매번 등록할 때 마다 새로 생성할 필요 없이 한번 Bean으로 등록 해 두고 필요한곳에서 불러와 사용할 수 있다.

```kotlin
    @Bean
    fun jwtAuthInterceptor(
        authService: AuthService,
        jwtConfig: JwtConfig,
        @Value("\${server.ssl.enabled}") isSecure: Boolean
    ): JwtAuthInterceptor {
        return JwtAuthInterceptor(authService, jwtConfig.tokenValidityInSeconds, isSecure)
    }

    @Bean
    fun adminAuthInterceptor(): AdminAuthInterceptor {
        return AdminAuthInterceptor()
    }
```

> 빈 등록

Interceptor 사용처1

```kotlin
@SpringBootApplication(exclude = [UserDetailsServiceAutoConfiguration::class])
@EnableJpaAuditing
@EnableScheduling
class DutyparkApplication(
    private val jwtAuthInterceptor: JwtAuthInterceptor,
    private val adminAuthInterceptor: AdminAuthInterceptor,
) : WebMvcConfigurer {
    override fun addInterceptors(
        registry: InterceptorRegistry
    ) {
        registry.addInterceptor(jwtAuthInterceptor)
            .addPathPatterns("/**")
            .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.map", "/error")
            .order(0)

        registry.addInterceptor(adminAuthInterceptor)
            .addPathPatterns("/admin/**", "/actuator/**").order(1)
    }
}
```

Interceptor 사용처2

```kotlin
@Configuration
class ActuatorConfig(
    private val jwtAuthInterceptor: JwtAuthInterceptor,
    private val adminAuthInterceptor: AdminAuthInterceptor,
) : WebMvcEndpointManagementContextConfiguration() {

    override fun webEndpointServletHandlerMapping(
        webEndpointsSupplier: WebEndpointsSupplier?,
        servletEndpointsSupplier: ServletEndpointsSupplier?,
        controllerEndpointsSupplier: ControllerEndpointsSupplier?,
        endpointMediaTypes: EndpointMediaTypes?,
        corsProperties: CorsEndpointProperties?,
        webEndpointProperties: WebEndpointProperties?,
        environment: Environment?
    ): WebMvcEndpointHandlerMapping {

        val webEndpointServletHandlerMapping = super.webEndpointServletHandlerMapping(
            webEndpointsSupplier,
            servletEndpointsSupplier,
            controllerEndpointsSupplier,
            endpointMediaTypes,
            corsProperties,
            webEndpointProperties,
            environment
        )

        webEndpointServletHandlerMapping.setInterceptors(jwtAuthInterceptor)
        webEndpointServletHandlerMapping.setInterceptors(adminAuthInterceptor)

        return webEndpointServletHandlerMapping
    }
}
```

이렇게 설정 한 후 확인해보면, `/actuator` 엔드포인트에 접근 할 때 등록한 인터셉터가 의도한 대로 잘 동작한다.

어쨌든 되는지만 확인해보기로 한거니깐 작동한다는 걸 확인한걸로 됐다.

## 결론

어쨌든 이런 경우에는 필터를 사용하는게 적합하다. 

인터셉터를 고집하며 WebMvcEndpointManagementContextConfiguration 를 확장한  **[jnizet](https://github.com/jnizet)**도 결국 필터를 사용하는 쪽으로 리팩터링했다. 인터셉터를 옹호하며 몇몇 주장을 냈지만, 다른 사용자들의 "필터써", "response에서 401 status 바로 내면 되지" 와 같은 답변에 그도 납득을 할 수 밖에 없던 모양. 스프링 측에서도 공식적으로 Filter 사용을 권고한다.

끝. 

**References**

- https://github.com/spring-projects/spring-boot/issues/11234
- https://stackoverflow.com/questions/57125304/spring-boot-cant-intercept-actuator-access
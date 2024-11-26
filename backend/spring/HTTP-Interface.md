# Spring 6의 HTTP Interface

## Intro

Spring Framework 6.1 에서 새롭게 등장한 HTTP Interface는 외부 API를 호출하고 데이터를 처리하는 과정을 아주 단순하게 해준다.

기존에도 RestTemplate, WebClient 등의 강력한 도구들이 있었지만, Feign Client 에서 영감을 받은 선언적 인터페이스 방식의 HTTP Interface는 간결한 코드로 손쉽게 사용할 수 있다.

이번 글에서는 스프링에서 외부 API 호출을 어떻게 진화시켜왔는지 살펴보고, 마지막으로 HTTP Interface를 활용해 간단히 외부 API 요청을 처리하는 방법을 알아본다. 실습에 앞서 스프링부트 프로젝트를 생성해주자.

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/HTTP-Interface.assets/2.webp)

스프링부트 버전은 꼭 3.1+ 해줘야 Http Interface를 사용할 수 있다. Dependencies에 Spring Web도 추가해준다.

![3](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/HTTP-Interface.assets/3.webp)

> Spring boot 3.4.0

## 스프링에서의 외부 API 호출 변천사

### 1. RestTemplate

오래전 Spring 3 에서 RestTemplate이 도입되어 외부 API 호출이 단순화되었다. 하지만 코드가 다소 장황해지고 객체 매핑 등을 직접 처리해야 하는 단점이 있었다.

```java
RestTemplate restTemplate = new RestTemplate();
String url = "https://api.sampleapis.com/coffee/iced";

ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

System.out.println(response.getBody());
```

![4](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/HTTP-Interface.assets/4.webp)

> 실행 결과

RestTemplate은 여전히 많은 프로젝트에서 사용되지만 더 나은 선택지가 많아졌다.

### 2. WebClient

Spring WebFlux의 일부로 추가된 WebClient는 비동기 및 동기 요청을 모두 지원하며, RestTemplate보다 더 유연하고 기능이 강력하다. 하지만 초기 설정과 사용 방식이 RestTemplate보다 복잡하게 느껴질 수 있다.

WebClient를 사용하려면 webflux 의존성이 필요하다.

```groovy
implementation 'org.springframework.boot:spring-boot-starter-webflux'
```

예제 코드는 아래와 같다.

```java
       WebClient webClient = WebClient.create("https://api.sampleapis.com/coffee/iced");

        List<Map> coffees = webClient.get()
                .retrieve()
                .bodyToFlux(Map.class)
                .collectList()
                .block();

        coffees.forEach(System.out::println);
```

![5](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/HTTP-Interface.assets/5.webp)

> 실행 결과

WebClient는 RestTemplate의 단점을 보완하여 지금도 널리 쓰인다.

## HTTP Interface

Spring 6에서 소개된 HTTP Interface는 기존의 WebClient를 내부적으로 활용하면서도, 인터페이스를 통해 HTTP 호출을 직접적으로 추상화한다. 코드는 더욱 간결해지고, 애노테이션 기반의 선언적 프로그래밍 방식 덕분에 유지보수성도 높아졌다.

Feign Client 를 사용해 봤다면, 매우 익숙할 것이다.

Spring Boot 3.1 이상을 사용하는 프로젝트라면 추가 설정 없이 사용할 수 있다. 과도기를 겪고 있는지 사용법이 자주 바뀌고 있는데, 지금의 예제는 스프링부트 3.4 기준으로 작성하였다.

### 1. HTTP Interface 정의

다음과 같이 API 호출을 위한 인터페이스를 정의한다.

```java
import org.springframework.web.service.annotation.GetExchange;
import java.util.List;

public interface CoffeeApiClient {
    @GetExchange("/coffee/iced")
    List<Coffee> getIcedCoffees();
}
```

> `@GetExchange`는 GET 요청을 보낼 엔드포인트를 정의한다.

### 2. 모델 클래스 작성

API 응답 데이터를 매핑할 클래스가 필요하다. `Coffee` 클래스는 다음과 같이 정의한다.

```java
import java.util.List;

public record Coffee(
        String title,
        String description,
        List<String> ingredients,
        String image,
        int id
) {}
```

### 3.설정 파일 등록

```java
@Configuration
public class CoffeeApiConfig {

    @Bean
    public CoffeeApiClient coffeeApiClient() {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.sampleapis.com")
                .build();

        return HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient))
                .build()
                .createClient(CoffeeApiClient.class);
    }

}
```

### 4. 사용 예제

스프링 애플리케이션에서 `CoffeeApiClient`를 주입받아 간단히 데이터를 처리할 수 있다.

```java
@Service
@RequiredArgsConstructor
public class HttpInterfaceService {

    private final CoffeeApiClient coffeeApiClient;

    public void printCoffees() {
        coffeeApiClient.getIcedCoffees()
                .forEach(coffee -> System.out.println(coffee.title() + " - " + coffee.description()));
    }

}
```

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/HTTP-Interface.assets/1.webp)

> 실행 결과

## 결론

Spring 6의 HTTP Interface는 스프링 개발자들에게 보다 단순한 외부 API 호출 방식을 제공한다.

얼핏 보기에는 더 복잡하고 작성하는 코드양이 더 많아보이지만, 모듈화를 할 수 있어서 여러가지 API를 호출할 때 코드 재활용하며 확장에 능하기 때문에 실제로는 훨씬 간결하게 표현된다.

RestTemplate의 사용성에서 WebClient로 진화했던 것처럼, HTTP Interface는 선언적이고 직관적인 방식으로 코드의 간결함과 유지보수성을 한층 더 높였다.

외부 API 호출이 많은 프로젝트라면 HTTP Interface로 마이그레이션 해보길 추천한다.
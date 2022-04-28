# SpringBoot의 JSON 직렬화시 날짜 처리

## Intro

SpringBoot 버전을 1.5에서 2.5로 마이그레이션 한 이후로 인지하지 못했던 여러가지 변화 들이 하나 둘 씩 더 발견되고 있습니다.

이번에 발견된 변화는 꽤나 당황스러웠는데.. Date 객체를 JSON으로 직렬화 할 때, 그 형태가 달라졌다는 겁니다. API 를 제공하는 입장에서는 일관적인 포맷으로 제공해주는게 굉장히 중요한데 API 스펙 자체가 변경되어 버릴 수 있는 큰 문제 입니다.

비교를 해 보면

![image-20220428140200413](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/springboot-json-date.assets/image-20220428140200413.png)

> Spring Boot 1.5 에 의존하는 프로젝트



![image-20220428140141003](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/springboot-json-date.assets/image-20220428140141003.png)

> Spring Boot 2.5 에 의존하는 프로젝트

스프링 부트 버전이 달라졌을 뿐인데 반환하던 API의 포맷이 변경 되어 버렸습니다.

이 변경을 추적 해 보겠습니다. 다소 내용이 길기 때문에 과정을 건너 띄고 설정 방법만 보려면 `SpringBoot와 Jackson` 을 건너 띄고 맨 아래 단락으로 바로 내려가주세요.

## SpringBoot와 Jackson

### SpringBoot 2.0

SpringBoot 2.0 버전이 되며 JSON 지원에 많은 변화가 있었습니다. 일단 기존의 1.5 버전에서는 SpringBootStartWeb이 jackson-databind를 직접 의존하고 있었는데요

![image-20220428152745872](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/springboot-json-date.assets/image-20220428152745872.png)

> https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web/1.5.22.RELEASE

아래는 스프링 부트 2.0 Release Note의 일부를 발쵀 한 내용 인데요

부트 2.0 버전으로 넘어 오면서 spring-boot-starter-json 이 생겼고, JSON 입출력에 관련한 라이브러리들이 여기 모였습니다.

![image-20220428140341676](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/springboot-json-date.assets/image-20220428140341676.png)

> https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.0-Release-Notes

그래서 StarterWeb에서는 해당 spring-boot-starter-json에 의존합니다.

![image-20220428152934701](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/springboot-json-date.assets/image-20220428152934701.png)

> https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web/2.6.7

Dependencies에서 `org.springframework.boot » spring-boot-starter-json`를 확인 할 수 있는데요

![image-20220428153031570](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/springboot-json-date.assets/image-20220428153031570.png)

> https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-json/2.6.7

클릭 해서 확인 해 보면, 이제 여기에서 jackson-datbind 뿐만 아니라, jackson-datatype-jdk 및 jackson-datatype-jsr310 등에 의존 하고 있습니다.

jackson-datatype-jdk8 및 jackson-datatype-jsr310은 자바 8 로 넘어오며 추가된 데이터 타입들을 지원 하기 위한 모듈인데요, JSR-310에 명시된 자바8의 시간 및 날짜 타입들 및 Optional, OptionalLong, OptionalDouble 등의 데이터 타입들을 사용 할 수 있습니다. 두 모듈은 Jackson 2.8.5 버전 부터 [`Jackson Java 8 Modules`프로젝트](https://github.com/FasterXML/jackson-modules-java8)에 통합 되었습니다.

### Jackson MessageConverter

스프링부트에 Jackson의 메시지 컨버터가  등록되는 과정에 대해 살펴보겠습니다.

정확한 지점을 찾기 힘들어서 이곳저곳 브레이크 포인트를 찍어보며 확인 해 보았는데요, 스프링의 WebMvcConfiguration 과정 중 `addDefaultHttpMessageConverters` 메서드가 호출 될 때

![image-20220428161213737](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/springboot-json-date.assets/image-20220428161213737.png)

여기에서 AllEncompassingFormHttpMessageConverter 를 생성 해 messageConverter로 추가 하는데요 

이번에는 `AllEncompassingFormHttpMessageConverter` 의 생성자를 살펴 보면

![image-20220428161256446](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/springboot-json-date.assets/image-20220428161256446.png)

`jackson2Present` 를 확인 후 true일때는 `MappingJackson2HttpMessageConverter`를 파트 컨버터로 추가 하게 됩니다. 

jackson2Present 는 비교적 간단하게 확인 하는데요

```java
jackson2Present = ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", classLoader) && ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", classLoader);
```

> 클래스유틸즈의 isPresent 메서드로 com.fasterxml.jackson.databind 패키지가 포함 되었는지를 검사 합니다.

이제 `MappingJackson2HttpMessageConverter` 의 생성자를 살펴 보면 되겠습니다.

![image-20220428161613536](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/springboot-json-date.assets/image-20220428161613536.png)

ObjectMapper를 하나 빌드 해서 등록 하는 과정을 거치네요.

### autoconfigure

그러면 jackson은 각각의 자료형별로 어떤식으로 직렬화 할 지에 대한 설정은 어디서 할까요?

![image-20220428162305513](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/springboot-json-date.assets/image-20220428162305513.png)

> JacksonObjectMapperBuilderConfiguration

그건 spring-boot-autoconfigure 에 있는 JacksonObjectMapperBuilderConfiguration 에서 찾을 수 있었습니다

해당 지점에 브레이크 포인트를 찍고 확인을 해 보니

![image-20220428162351138](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/springboot-json-date.assets/image-20220428162351138.png)

customizers 변수명으로 StandardJackson2ObjectMapperBuilderCustomizer들의 리스트가 들어가 있었으며, 그 안에  JacksonProperties 객체로 각종 설정 값을 담고 있습니다.

지금은 따로 설정을 해준게 없기 때문에 대부분 null혹은 size=0의 상태 입니다.

그러면 이제 이 객체의 빌드 과정에서 `public void configure(ObjectMapper objectMapper)` 메서드 호출을 통해 스프링에서 사용 할 ObjectMapper의 설정을 하게 됩니다.

![image-20220428163212195](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/springboot-json-date.assets/image-20220428163212195.png)

> 굉장히 많은 설정값들을 모두 주입합니다.

![image-20220428163316744](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/springboot-json-date.assets/image-20220428163316744.png)

그러면 그 과정에서 ObjectMapper 객체의 `_deserializationConfig`와 `_serializationConfig` 에 모든 설정값을 저장 하게 됩니다.

지금은 특별히 설정 한 값이 없기 때문에 이번에는 Default 설정을 찾아 가봅니다.

![image-20220428164145850](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/springboot-json-date.assets/image-20220428164145850.png)

모든 jackson-databind 의 기본 설정은 상수로 정의된 DEFAULT_BASE 라는 이름의 BaseSettings 를 따릅니다.

![image-20220428164333639](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/springboot-json-date.assets/image-20220428164333639.png)

그 객체 안에는 또 DateFormat이 상수로 정의 되어 있습니다. 일반적으로 StdDateFormat을 사용하고, 특별히 설정을 할 경우에는 이걸 대체 할 것이라고 합니다. 그러면 이제 StdDateFormat을 확인 해 보아야 겠네요

![image-20220428165224374](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/springboot-json-date.assets/image-20220428165224374.png)

드디어 찾았습니다. 

친절하게 위에 주석으로 설명 되어 있는데요, 직렬화에는 ISO-8601 포맷을 사용 하며, 역직렬화에는 ISO-8601과 RFC-1123 을 모두 사용한다고 합니다. 스프링부트 1.5 버전이 의존중인 Jackson-databind 2.12.6 버전의 StdDateFormat도 확인을 해 보았는데 얼추 일치하더라고요. 그럼 여기까지는 문제가 없습니다.

### customize

그럼 이번에는 자동 설정 후 커스터마이징 하는 부분을 살펴 보아야 겠습니다. 

![image-20220428170734238](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/springboot-json-date.assets/image-20220428170734238.png)

아까 살펴본 `JacksonObjectMapperBuilderConfiguration` 에서 객체를 빌드하기 직전에 customize 과정을 거치는데요. 

![image-20220428170817604](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/springboot-json-date.assets/image-20220428170817604.png)

customize 메서드를 확인 해 보면 jacksonProperties 를 기반으로 serialization 설정을 커스터마이징 합니다.

jacksonProperties는 Jackson2ObjectMapperBuilderCustomizerConfiguration 등록 과정에 주입 됩니다.

![image-20220428172619412](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/springboot-json-date.assets/image-20220428172619412.png)

jacksonProperties에 원하는 설정 값을 담아 생성한다면, 직렬화를 원하는 대로 할 수 있다는 이야기 입니다.

## 설정

그렇다면 스프링부트 버전 변화에 따른 Date의 직렬화 차이를 어떻게 극복 해야 할까요?

가장 단순한 방법으로는 @JsonFormat 어노테이션으로 하나하나의 객체에서 각각 Date 타입이 직렬화 될 포맷을 설정 해 주는 방법이 있습니다. 하지만 이 경우에는 API의 크기가 방대하면 대처하기가 쉽지 않죠.

이때는 Jackson 설정을 해주는 방법이 있습니다. application.properties 파일에 잭슨 설정을 작성 해 주면, 방금 찾아본 jacksonProperties로 넘어 가며 원하는 설정으로 ObjectMapper를 만들어 줍니다.

### Spring Boot 2.5

만약 스프링부트 2 버전을 사용하지만, 이전처럼 Date를 timestamp(Long) 로 직렬화 하고 싶은 경우에는

```xml
spring.jackson.serialization.write-dates-as-timestamps = true
```

로 설정 해 주면 됩니다. 저는 yaml 파일을 사용하기 때문에 아래와 같이 작성 할 수 있습니다.

```yaml
spring:
  jackson:
    serialization:
      write-dates-as-timestamps: true
```

이렇게 설정 하고 JacksonAutoConfiguration의 StandardJackson2ObjectMapperBuilderCustomizer 메서드에 브레이크 포인트를 찍고 디버깅을 해 보면

![image-20220428174432564](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/springboot-json-date.assets/image-20220428174432564.png)

jacksonProperties의 serialization에 WRITE_DATES_AS_TIMESTAMP 설정이 들어 가 있는것을 확인 할 수 있습니다. 이렇게 하면 기존처럼 Date를 timestamp 형식으로 JSON 직렬화 해줍니다.

### Spring Boot 1.5

반대로 스프링부트 1.5 버전에서 Date를 ISO-8601 포맷으로 직렬화 하는 방법도 있습니다.

방금 했던 것과 반대로 `spring.jackson.serialization.write-dates-as-timestamps = false`를 할 수도 있고, 혹은 애초에 date-format을 정해 주는 방법도 있습니다.

```xml
spring.jackson.date-format=com.fasterxml.jackson.databind.util.StdDateFormat
```

그 외에 위에서 알아본 Jackson2ObjectMapperBuilderCustomizer 의 customize 메서드를 직접 구현 한다면 원하는 포맷으로 설정 해 줄 수도 있겠네요. @LocalDataTime 로 하나 하나 설정하기엔 개발자의 꼼꼼함에 의존해야 하다 보니 아무래도 부담이 큽니다.

### 

이상으로 SpringBoot에서는 Jackson 라이브러리를 이용해 날짜 타입을 어떤식으로 직렬화 하며 설정은 어떻게 할 수 있는지에 대해 알아 보았습니다. 날짜를 timestamp로 보내는 것과 ISO-8601 포맷으로 보내는 것 중 저는 가독성 측면에서 후자에 한표를 주고 싶습니다. 

사실 계산하는건 개발자 입장에서 시간에 대한 라이브러리들이 워낙 잘 준비가 되어있기 때문에 딱히 차이가 없지만 API를 눈으로 확인할 때의 두 값은 정말 큰 차이가 있습니다. 감사합니다.
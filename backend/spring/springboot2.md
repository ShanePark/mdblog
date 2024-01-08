# SpringBoot 2.x 버전 프로젝트 생성 방법

## Intro

새로운 프로젝트 요청이 왔는데, `Java 11` 을 사용해 스프링부트로 프로젝트를 만들어야 하는 요구사항이 명시되었다.

그런데 스프링부트 3버전대의 등장 이후 2.x 버전으로 프로젝트를 생성한 적이 한번도 없었어서 이제야 알았는데 Spring Initializr 에서 스프링 부트 2.x 버전이 모두 제거되었다. 

스프링 3.0 부터는 최소 JDK 요구 사항이 17이기 때문에 Spring Initialzr 에서는 이제 더이상 17 아래의 자바 버전들도 취급을 안해준다.

![image-20240108102546379](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/springboot2.assets/5.webp)

> 자바 버전은 17, 21만 선택 가능하다.

![image-20240108102029660](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/springboot2.assets/1.webp)

> 스프링부트는 2버전 선택이 불가능하다

당연하겠지만 `start.spring.io` 에 들어가서 생성하려 해도 마찬가지다.

![image-20240108102053957](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/springboot2.assets/2.webp)

> https://start.spring.io/

이정도에서 끝나는게 아니고 `start.spring.io` 에서만 선택을 못하는게 아니고 CLI에서의 생성도 막아뒀다. 작정하고 스프링부트 3 아래 버전을 손절하는게 느껴진다.

스프링 부트의 버전별 지원기간을 확인해보자.

![image-20240108102222148](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/springboot2.assets/3.webp)

> https://spring.io/projects/spring-boot/#support

위의 사진에 보이는 것 처럼, 2.7.x 버전은 2023년 11월부로 지원이 끝났다.

![image-20240108102341864](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/springboot2.assets/4.webp)

> https://github.com/spring-projects/spring-boot/tree/2.7.x

Github Repository를 확인해보아도 Spring Boot 2.7.x 브랜치는 2달전부터 아무런 커밋이 없다.

## 스프링 부트 2.x 버전을 생성하려면

그래도 스프링부트 2.x버전을 생성해야하는 상황은 있을 수 있다. 

- 첫번째로, 지금의 나처럼 요구사항이 그렇게 온 경우. 요즘엔 도커를 많이 쓰기 때문에 JDK버전등을 맞춰야 할 필요성이 예전보다는 적어졌지만 그럼에도 JDK 선택이 제한되고 컨테이너 기술을 사용할 수 없는 제약상황이 있다면 어쩔 수 없다.

- 두번째로는 학습이다. 

스프링부트 3.0이 릴리즈된지 이제 1년쯤되었다. 그 전의 모든 학습 자료는 스프링부트 2.x 버전 혹은 그 이하를 기준으로 만들어졌다는 소리인데 최근 1년간 생성된 교육자료에 비해 그 이전의 교육자료의 양과 질이 당연히 좋을 수밖에 없다.

물론 스프링부트 2.x 에 맞춰진 교육자료를 3.x 으로 스스로 마이그레이션 하며 학습하는 방법이 있기는 하지만 아무래도 집중력이 크게 분산될 수밖에 없다. 개인적으로 이런 상황에서는 교육자료의 버전에 맞춰 학습을 하는 편이 학습 효과나 흥미에 있어서는 효율적이라고 생각한다. 

특히나, 스프링 시큐리티의 달라진점들을 스스로 마이그레이션 하며 학습하려고 한다면... 아마 본인의 마음 깊은 속에 숨겨져있던 여러가지 감정을 끄집어내는 계기가 될 것이다.

### 3.x 프로젝트 생성

스프링부트 initializr를 이용해 쉽게 생성하려면 3.x 버전으로 먼저 생성하고 2.x 로 마이그레이션 하는 방법이 가장 간단하다.

![image-20240108102716151](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/springboot2.assets/6.webp)

사진에서는 `Spring Web` `Spring Data JPA` `Lombok` 등을 추가했지만, 스프링 버전에 따라 의존성이 다를 수 있으니 의존성을 전혀 넣지 않고 프로젝트를 생성하는 것도 좋다. 특히 MYSQL 같은 경우에는 [Spring Boot 3 에서 MYSQL 의존성 못찾는 경우](https://shanepark.tistory.com/466)처럼 영문도 모르고 왜 안되는건가 고민하며 시간을 빼앗길 수 있다.

### Build.gradle 수정

프로젝트를 생성한 후에는 바로 `build.gradle` 을 수정하면 된다.

![image-20240108102926471](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/springboot2.assets/7.webp)

마지막 으로 배포된 2.x 스프링부트 버전은 `2.7.18` 이다.

${code:build.gradle}

```groovy
plugins {
    id 'java'
//    id 'org.springframework.boot' version '3.2.1'
    id 'org.springframework.boot' version '2.7.18'
    id 'io.spring.dependency-management' version '1.1.4'
}

group = 'io.github.shanepark'
version = '0.0.1-SNAPSHOT'

java {
//    sourceCompatibility = '17'
    sourceCompatibility = '11'
}

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    compileOnly 'org.projectlombok:lombok'
    runtimeOnly 'org.postgresql:postgresql'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

변경된 부분만 주석 처리해보았다. 학습 목적이라면 자바 버전까지 변경할 필요는 없다.

상황에 따라 dependencies 부분도 바꿔줘야 할 수 있다.

![image-20240108103204006](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/springboot2.assets/8.webp)

Project Settings를 확인해보면 `Language level` 이 11로 변했다. SDK도 선택해서 자바 버전에 맞춰준다.

이제 실행해보면 `v2.7.18`로 실행된다. 열심히 개발하면 된다. 

![image-20240108103922692](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/springboot2.assets/9.webp)

그래도 왠만하면 3.x 버전 쓰자. 끝
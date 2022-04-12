# Spring Boot 1.5 -> 2.5 마이그레이션 회고

## Intro

최근 스프링 부트 1.5 버전으로 작성된 프로젝트를 Spring Boot: 2.5.10 버전으로 올리는 작업을 진행 하였습니다. 스프링부트 앞자리 버전 차이가 굉장히 큰데, 온라인 게임에 비유한다면 메이플스토리에서의 빅뱅 혹은 와우의 대격변 전후를 생각하면 이해가 빠르겠습니다.

단순히 pom.xml에서 버전 명시를 변경한다고 뿅 하고 바뀌는건 아니고, 스프링 부트 변경에 따라 여러가지 클래스가 사라지고 새로 생겨났으며 각종 메서드들의 변화도 상당합니다. 스프링 설정 하는 방법도 달라지고, 구석구석 영향이 가지 않는 곳이 거의 없기 때문에 프로젝트가 크면 클수록 그 여파가 상당합니다. 제가 이번에 마이그레이션을 진행한 프로젝트도 몇년간 진행되며 몇번의 고도화를 거친 프로젝트이다 보니 규모도 제법 되는데다 모듈프로젝트를 하나씩 떼어놓고 보면 어플리케이션이 10개가량 됩니다.

마이그레이션을 진행 하며 바꿔도 바꿔도 끝이 없다는 것에 먼저 한번 놀랐는데, 차라리 눈에 보이는건 찾아 고치기라도 쉽지만.. 특히 사이드이펙트들은 그 원인을 바로바로 찾아내기가 쉽지 않기 때문에 중간중간 정말 절망도 많이 했습니다. 그래도 꾸준히 하다보니 어플리케이션들이 하나 둘 씩 올라오기 시작했고 이제 작업은 마무리 단계에 다다랐습니다. 뭔가 인터넷에서 검색 했을때는 뚝딱 하고 변경 한 것 처럼 작성된 글이 많았는데 무턱대고 시작하기에는 그 작업량이 상당하니 충분히 여력을 쏟아 부을 수 있을 때 진행하시길 추천합니다.

마이그레이션 작업 중에도 한번씩 요구사항이나 고객사 요청에 따른 유지보수를 위해 기존 버전으로 돌아와 작업을 할 필요가 있었는데 Git으로 버전관리를 한다고 해도 버전 전환에 따른 시간정신적 비용이 꽤나 크기 때문에 생각보다 마음을 단단히 먹어야 합니다. 다른 작업과 병행하며 어느정도 마무리하는데 1주일 가량 걸렸습니다만 상황에 따라 크게 다르기 때문에 프로젝트가 작다면 몇분만에 끝날수도, 크다면 몇 달이 걸릴수도 있겠습니다.

## Before You Start

https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.0-Migration-Guide 의 스프링 공식 마이그레이션 가이드를 참고 했습니다.

### 최신의 1.5.x 버전으로 업그레이드

Before you start the upgrade, make sure to upgrade to the latest `1.5.x` available version. This will make sure that you are building against the most recent dependencies of that line.

> 스프링 공식 문서에서는 업그레이드를 시작 하기 전에 최신의 1.5.x 버전으로 업그레이드를 먼저 해보라고 권합니다. 

![image-20220224135625618](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-boot-migration.assets/image-20220224135625618.png)

> 마지막 1.5.x 버전을 확인 해 보니 v1.5.22.RELEASE 입니다.

## 스프링 부트2 버전으로 업그레이드

dependency 들에 대한 검토를 마쳤다면 2.0.x  중 최신 버전으로 업그레이드 합니다. `2.0.0.RELEASE`는 많은 문제가 있었기 때문에 처음 버전으로 변경하지는 않는 것이 좋다고 합니다. 최소 2.0.1 부터 시작하라고 하네요.

> 2.0.x 의 마지막 버전은 2.0.9 입니다.

![image-20220224154821314](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-boot-migration.assets/image-20220224154821314.png)

> 일단 pom.xml 에서 버전만 바꿔보았을 뿐인데 983개의 에러가 반겨줍니다.

###  환경 설정 마이그레이션

Spring Boot 2.0으로 넘어오며 많은 설정 property들의 이름이 변경되거나 삭제 되었습니다. 따라서 그에 맞춰 application.properties 혹은 application.yml 파일을 알맞게 변경 해야 합니다.

Spring-boot-properties-migrator 모듈을 추가하면, 환경설정을 읽고 분석해 줄 뿐만 아니라, 런타임때 적절한 properties로 migrate 해주기도 합니다.

**maven**

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-properties-migrator</artifactId>
	<scope>runtime</scope>
</dependency>
```

**gradle**

```groovy
runtime("org.springframework.boot:spring-boot-properties-migrator")
```

### Next Steps

추가로 세부 사항을 확인 하고 싶다면 아래의 링크를 참고 해 주세요. 해당사항이 있다면 읽고 진행해도 좋지만 변경하면서 필요할 때마다 읽어도 괜찮습니다.

If you wish to look into specifics, here’s a curated list of resources - otherwise, proceed to the next sections:

- [Spring Boot 2.0.0 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.0.0-Release-Notes)
- [Running Spring Boot on Java 9](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-with-Java-9)
- [Upgrading to Spring Framework 5.0](https://github.com/spring-projects/spring-framework/wiki/Upgrading-to-Spring-Framework-5.x#upgrading-to-version-50)

### Building Your Spring Boot Application

The plugin configuration attributes that are exposed as properties now all start with a `spring-boot` prefix for consistency and to avoid clashes with other plugins.

플러그인 구성 속성은 이제 `Spring-Boot` 접두사로 시작합니다. 일관적인 이름으로 인해 다른 플러그인과의 충돌도 피할 수 있습니다.

예를 들어, 아래의 커맨드는 prod 프로필을 활성화 시킵니다.

```properties
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

이제 정말 스프링 부트 마이그레이션을 시작 해 보겠습니다.

pom.xml 에 기입한 스프링 부트 버전을 변경하는것으로 긴 여정이  시작됩니다.

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.5.10</version>
    <relativePath/> <!-- lookup parent from repository -->
</parent>
```

 지금부터는 제가 마이그레이션을 진행 하며 겪었던 문제들을 공유하겠습니다.

## Bean 순환 참조 해결

![image-20220225100014868](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-boot-migration.assets/image-20220225100014868.png)

이전에 찾지 못했던 순환 참조 구조를 찾아서 알려줍니다. 알려주는데 그치지 않고 순환 고리를 끊지 않으면 빌드가 되지 않습니다. 순환이 약하다면 어렵지 않게 끊을 수 있었는데 지독하게 얽혀있는 몇몇 서비스들은 서로 떼내는게 쉽지 않았습니다. Setter 주입을 한다면 어떻게 넘어갈 수 있겠지만 추후 메모리 문제가 발생할 수 있으니 생성자 주입을 유지하며 고리를 끊어내는 것이 권장됩니다.

생성자 주입 방식으로 DI 했을때는 위와 같이 스프링부트가 찾아내어 알려 주지만, Field Injection 방식에서는 그러지 못합니다. 

![image-20220225103245100](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-boot-migration.assets/image-20220225103245100.png)

> Bean을 생성하다가 실패 했는데, Spring Boot에서 순환 참조를 의심 하고 있습니다.

그 이유를 간략하게 알아보면 SpringFramework 구동 시점에서

- DispatcherServlet 및 ContextLoader가 생성 되고, 설정들을 읽습니다.
- 설정을 바탕으로 Bean 객체들을 하나씩 생성 합니다.

이 Bean 객체 생성 단계에서 순환참조 오류를 방지 할 수 있을지 없을지가 결정되는데 Field Injection을 통한 의존성 주입을 했다면 참조하느 객체를 선언하지 않았기 때문에 정상적으로 초기화가 완료 되는 것 처럼 보입니다. 하지만 후에 해당 객체를 로드 할 시점이 되어서야 순환 참조 에러가 발생하는 거죠.

하지만, 생성자 주입 방식이라면 빈 객체 생성 시점에 순환 참조 오류가 발생하기때문에 바로 알아 챌 수 있습니다. 테스트의 용이함을 비롯해 생성자 주입방식이 절대적 이점을 가지고 있기 때문에 이번 기회에 모두 생성자 주입으로 변경 하였습니다. 다만 Google Guava의 EventBus는 생성자주입시 오히려 순환참조가 되어 버려서 필드 인젝션을 해야 했습니다.

![image-20220225114928666](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-boot-migration.assets/image-20220225114928666.png)

> 수많은 필드 인젝션을 생성자 주입으로 변경하고 나니 숨겨져 있던 순환 참조를 찾을 수 있었습니다.

너무 급한 상황이라면 일단 applcation.yml 파일에 아래의 설정으로 bean 선언 오버라이딩을 허용 해 주면 순환참조가 있어도 어플리케이션을 구동 할 수 있다고 합니다.

```yaml
spring:
  main:
    allow-bean-definition-overriding=true
```

특히 SpringBoot 2.5에서 적당히 순환참조를 끊어서 어플리케이션 구동에 성공 한 후에 2.6 버전으로 한단계 더 올려보려고 했을때는 제가 미처 잡지 못한 순환 참조를 모두 다 잡아내며 어플리케이션이 절대 실행 되지 않았는데요.

Spring Boot 2.6 릴리즈노트를 확인 해 보면 그 이유를 알 수 있습니다. 2.6부터는 순환참조를 기본적으로 모두 금지시켜 버렸습니다.

![image-20220319161348481](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-boot-migration.assets/image-20220319161348481.png)

> https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.6-Release-Notes

이때는 아래처럼 순환 참조를 허용 하는 방법이 있기는 합니다만.. 

```properties
spring.main.allow-circular-references=true
```

어찌됐건 그동안 미뤄 두었었다면 이번 기회를 통해 순환 참조 고리를 가능한 끊어내는걸 추천합니다. 저도 적당히 끊어내고 하려고 했지만 BeanType에서 Bean을 찾아오지 못해 Solr에서 원하는대로 인덱싱을 하지 못하는 등 여러가지 문제가 있었습니다. 그 원인이 전혀 예상치 못했던 빈의 순환참조라는걸 알았을때는 정말 아찔했습니다. 버그를 찾아내기도 쉽지 않았거든요.

## 프로젝트 적용중 문제 해결

### spring-security-oauth2

> Cannot resolve org.springframework.security.oauth:spring-security-oauth2:unknown 

문제가 되는 dependency는 아래와 같습니다.

```xml
<dependency>
    <groupId>org.springframework.security.oauth</groupId>
    <artifactId>spring-security-oauth2</artifactId>
</dependency>
```

spring-security-oauth2를 찾지 못합니다.

![image-20220224162042089](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-boot-migration.assets/image-20220224162042089.png)

> https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.0.0-M5-Release-Notes

Spring Boot 릴리즈 노트에서 OAuth 2.0에 대한 내용을 찾아보니 Spring Security OAuth 프로젝트가 Spring Security 코어로 마이그레이션 되었다고 합니다. 

![image-20220224163113723](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-boot-migration.assets/image-20220224163113723.png)

> https://docs.spring.io/spring-security-oauth2-boot/docs/2.2.6.BUILD-SNAPSHOT/reference/htmlsingle/

다행히도 Spring Boot 1.x 버전에서 사용하다가 Spring Boot 2.x 버전에서 삭제된 OAuth2 를 사용 할 수 있도록 지원해주는 프로젝트가 존재합니다. 아래의 의존성을 추가해 주면 해결 됩니다.

```xml
<dependency>
    <groupId>org.springframework.security.oauth.boot</groupId>
    <artifactId>spring-security-oauth2-autoconfigure</artifactId>
    <version>2.0.0.RELEASE</version>
</dependency>
```

### spring-session

> Unresolved dependency: 'org.springframework.session:spring-session:jar:unknown'

문제가 되는 dependency는 아래와 같습니다.

```xml
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session</artifactId>
</dependency>
```

![image-20220224163705396](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-boot-migration.assets/image-20220224163705396.png)

> https://docs.spring.io/spring-session/docs/2.2.x/reference/html/upgrading-2.0.html

이번에는 spring-session이 문제입니다. spring-session-core 모듈로 replace 되었다고 안내 되네요.

그냥 pom.xml 에서 해당 의존성을 삭제 하면 되겠습니다.

### PageRequest

> java: constructor PageRequest in class org.springframework.data.domain.PageRequest cannot be applied to given types;
>   required: int,int,org.springframework.data.domain.Sort
>   found: int,java.lang.Integer
>   reason: actual and formal argument lists differ in length

사용중인 PageRequest 생성자의 접근제어자가 protected로 변경 되어 사용하지 못하는 오류가 발생했습니다.

![image-20220224164716685](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-boot-migration.assets/image-20220224164716685.png)

그래서 PageRequest 클래스를 확인 해보니, 그 대신 static 팩터리 메서드가 보입니다. 그걸 사용하도록 변경 해 줍니다.

```java
PageRequest.of(page, size, sort);
```

![image-20220224174230405](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-boot-migration.assets/image-20220224174230405.png)

한개씩 바꾸려면 끝도 없으니 `Cmd + Shift + R` 모두 찾아 바꾸기 기능을 이용해 한번에 캐스팅을 해 주세요.

> new Sort() 생성자도 마찬가지로 변경되었습니다. Sort.by() 로 생성합니다.

### Page.getOffset()

![image-20220224165048613](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-boot-migration.assets/image-20220224165048613.png)

Pageable의 getOffset이 int에서 long으로 변경되었습니다. 

이건 간단히  Integer로 형 변환을 하면 해결할 수 있습니다. 역시 찾아바꾸기로 한번에 바꿔주도록 합니다.

### junit

> java: package org.junit does not exist

unit 패키지의 위치가 이동되었기 때문에 기존의 import는 에러가 납니다.

![image-20220224165351189](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-boot-migration.assets/image-20220224165351189.png)

`org.unit.jupiter.api` 로 이동되었네요. 기존의 import를 삭제하고 새로 import 하여 해결 했습니다.

마찬가지로 `org.junit.Assert` 또한 제거되었습니다. `org.junit.jupiter.api.Assertions` 를 대신 사용하면 됩니다.

### Specifications

> Cannot resolve symbol 'Specifications'

`org.springframework.data.jpa.domain.Specifications` 의 행방도 찾을 수 없네요.

일단 Hibernate를 어떻게 사용하고 있었는지 확인 해 봅니다.

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>${hibernate.version}</version>
</dependency>
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-ehcache</artifactId>
    <version>${hibernate.version}</version>
</dependency>
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <!-- This artifact doesn't track the main Hibernate version. -->
    <version>${hibernate-validator.version}</version>
</dependency>
```

Hibernate를 따로 추가해 사용 하고 있었습니다. 

```xml
<hibernate.version>5.2.17.Final</hibernate.version>
<hibernate-validator.version>6.0.10.Final</hibernate-validator.version>
```

사용중인 Hibernate의 버전은 각각 이렇게 되었었습니다.

일단 버전을 싹 지우고 Spring boot가 알아서 지정해주도록 했습니다.

그런데 변경 후에도 여전히 문제가 에러가 고쳐지지 않는데

![image-20220224172921535](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-boot-migration.assets/image-20220224172921535.png)

equals의 리턴타입을 확인해보니 Specitication 으로 되어 있습니다.

![image-20220224171609017](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-boot-migration.assets/image-20220224171609017.png)

Specifications 에서 Specification으로 바뀐줄도 모르고 빌드도 새로 하고 캐시 초기화까지 했었네요.

전부 `Specification` 으로 변경 해 줍니다.

### findOne

![image-20220224171305178](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-boot-migration.assets/image-20220224171305178.png)

findOne이 Optional 객체를 반환하기 때문에 모두 변경 해 주어야 합니다. .get() 을 모두 붙여줍니다.

또한,  기존의 findOne은 findById() 로 변경되었으며, delete는 deleteById 로 변경되었습니다. 

모두 변경해줘야 합니다.

### SpringBootServletInitializer

> Cannot resolve symbol 'SpringBootServletInitializer'

SpringBootServletInitializer는 또 어디로 갔을까요..

![image-20220224173328245](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-boot-migration.assets/image-20220224173328245.png)

패키지 경로가 변경되었네요.

- 변경 전 `org.springframework.boot.web.support.SpringBootServletInitializer` 
- 변경 후 `org.springframework.boot.web.servlet.support.SpringBootServletInitializer`

### EntityManager

> Could not autowire. No beans of 'EntityManager' type found. 

Autowire로 사용중이던 EntityManager를 받아오질 못합니다.

SpringBoot 2.x 에서는 EntityManager를 불러 오려면 `@Autowire` 대신에  `@PersistenceContext` 어노테이션을 대신 달아야합니다.

### RelaxedPropertyResolver

> java.lang.ClassNotFoundException: org.springframework.boot.bind.RelaxedPropertyResolver
>
> Could not evaluate condition on org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration$WhitelabelErrorViewConfiguration due to org/springframework/boot/bind/RelaxedPropertyResolver not found. Make sure your own configuration does not rely on that class. This can also happen if you are @ComponentScanning a springframework package (e.g. if you put a @ComponentScan in the default package by mistake)

이번에는 에러메시지가 굉장히 친절합니다. SpringBoot 1.5에 있던 RelaxedPropertyResolver 를 불러오다가 실패 했다고 하며, 컴포넌트 스캔이 잘못 되었는지 확인 하라고 합니다.

![image-20220224180111127](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-boot-migration.assets/image-20220224180111127.png)

Congiguration을 확인 하러 가니 일단 deprecated가 눈에 띕니다. WebMvcConfigurerAdapter 상속하던걸 빼고, 인터페이스인 WebMvcConfigurer 를 구현 하도록 변경 해 줍니다.

```java
public class AdminApplication implements ServletContextAware, WebMvcConfigurer
```

하지만 그래도 여전히 해결되지 않는데요. stackoverflow를 찾아 보았습니다.

![image-20220224180714556](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-boot-migration.assets/image-20220224180714556.png)

> https://stackoverflow.com/questions/62497326/org-springframework-boot-bind-relaxedpropertyresolver-not-found

버전이 혼재되어 있을거고 해서  pom.xml을 열심히 훑어봤는데, Spring 관련 의존성 버전에는 문제가 없습니다. 딱히 `RelaxedPropertyResolver` 를 사용하는 코드도 없기 때문에, 다른 패키지중에 누군가가 해당 클래스에 의존하는 것으로 보입니다.

pom.xml 에서 버전을 명시적으로 사용하던 패키지들을 찾아, SpringBoot 가 알아서 지정해주도록 변경하니 (버전 명시 삭제) 어느 순간 해결되었습니다.

### Liquibase

liquibase 설정법도 달라졌습니다.

application.yml에서의 설정을 아래와 같이 변경했습니다.

변경전

```yml
liquibase.change-log: classpath:liquibase/changelog.xml
```

변경후

```
spring.liquibase:
  change-log: classpath:liquibase/changelog.xml
  database-change-log-table: dbchangelog
  database-change-log-lock-table: dbchangeloglock
```

여기에서 각각의 테이블명을 정확히 잘 입력해줘야 합니다. 오타를 냈더니 제멋대로 테이블을 만들고는 liquibase 를 처음부터 다시 다 DB에 넣으려고 시도 하더라고요.

### PasswordEncoder

PasswordEncoder를 주입 하던 중 에러가 발생합니다.

```
Description:

An attempt was made to call a method that does not exist. The attempt was made from the following location:

    org.springframework.security.config.annotation.authentication.configuration.InitializeUserDetailsBeanManagerConfigurer$InitializeUserDetailsManagerConfigurer.configure(InitializeUserDetailsBeanManagerConfigurer.java:73)

The following method did not exist:

    org.springframework.security.authentication.dao.DaoAuthenticationProvider.setPasswordEncoder(Ljava/lang/Object;)V

The method's class, org.springframework.security.authentication.dao.DaoAuthenticationProvider, is available from the following locations:

    jar:file:/home/shane/.m2/repository/org/springframework/security/spring-security-core/5.5.0/spring-security-core-5.5.0.jar!/org/springframework/security/authentication/dao/DaoAuthenticationProvider.class

The class hierarchy was loaded from the following locations:

    org.springframework.security.authentication.dao.DaoAuthenticationProvider: file:/home/shane/.m2/repository/org/springframework/security/spring-security-core/5.5.0/spring-security-core-5.5.0.jar
    org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider: file:/home/shane/.m2/repository/org/springframework/security/spring-security-core/5.5.0/spring-security-core-5.5.0.jar


Action:

Correct the classpath of your application so that it contains a single, compatible version of org.springframework.security.authentication.dao.DaoAuthenticationProvider


Process finished with exit code 1

```

스프링 시큐리티의 버전이 달라지며 일어난 문제로 보입니다.

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-config</artifactId>
    <!-- <version>4.2.7.RELEASE</version> -->
</dependency>
```

기존에는 spring-security-config 에서 버전을 명시 하고 있었는데요, 버전 부분을 제거해줍니다.

### Redis

Redis 에서도 문제가 있습니다.

```
Description:

An attempt was made to call a method that does not exist. The attempt was made from the following location:

    org.springframework.session.data.redis.config.ConfigureNotifyKeyspaceEventsAction.getNotifyOptions(ConfigureNotifyKeyspaceEventsAction.java:74)

The following method did not exist:

    org.springframework.data.redis.connection.RedisConnection.getConfig(Ljava/lang/String;)Ljava/util/List;

The method's class, org.springframework.data.redis.connection.RedisConnection, is available from the following locations:

    jar:file:/home/shane/.m2/repository/org/springframework/data/spring-data-redis/2.5.1/spring-data-redis-2.5.1.jar!/org/springframework/data/redis/connection/RedisConnection.class

The class hierarchy was loaded from the following locations:

    org.springframework.data.redis.connection.RedisConnection: file:/home/shane/.m2/repository/org/springframework/data/spring-data-redis/2.5.1/spring-data-redis-2.5.1.jar


Action:

Correct the classpath of your application so that it contains a single, compatible version of org.springframework.data.redis.connection.RedisConnection


Process finished with exit code 1

```

SpringSession 의존성을 변경해서 해결 했습니다.

변경 전

```
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session</artifactId>
    <version>1.3.5.RELEASE</version>
</dependency>
```

변경 후

```
<!-- spring session -->
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-core</artifactId>
    </dependency>
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-redis</artifactId>
    </dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

여기에서 끝나는게 아니고 `org.springframework.session.web.http.HttpSessionStrategy`가 제거되었기 때문에 HttpSessionIdResolver를 대신 구현 해 주며 많은 부분의 코드를 뜯어 고쳐야 합니다. HttpSessionStrategy 등도 사라졌기 때문에 변경해야할 부분이 제법 있었습니다.

### HandlerInterceptorAdapter

> Deprecated

HandlerInterceptorAdapter 가 deprecated 되었습니다. 대신 HandlerInterceptor를 구현해줍니다.

### ContextPath

ContextPath 지정 방법이 변경되었습니다. application.yml 파일을 변경해줘야 합니다.

변경 전

```yml
server:
  context-path: /adm
  port: 8080
```

변경 후

```yml
server:
  servlet.context-path: /adm
  port: 8080
```

### Lombok @Slf4j

Lombok 플러그인에서 `Slf4j` 어노테이션을 이용해 로그를 찍고 있었는데 아래와 같은 에러가 발생 합니다.

```
java: Can't get the delegate of the gradle IncrementalProcessingEnvironment.

java: cannot find symbol
  symbol:   variable log
  location: class openapi.OpenApiController
```

pom.xml 파일에서 

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.2</version>
    <scope>provided</scope>
</dependency>
```

위와 같이 lombok 버전을 명시 해서 사용 하고 있었는데, 해당 버전이 새로운 스프링부트 버전과 맞지 않던 것으로 보입니다. version 명시를 제거 하니 문제가 해결되었습니다. 

![image-20220302113845614](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-boot-migration.assets/image-20220302113845614.png)

> `1.18.20` 버전이 추가되었습니다. 18.2와 18.20은 수학적으로는 같은 값으로 보이지만 버전 표시에서는 마이너 버전 18개의 갭이 있는 꽤 큰 차이 입니다.

### No property UNSORTED found for type

```
SEVERE: Servlet.service() for servlet [dispatcherServlet] in context with path [/adm] threw exception [Request processing failed; nested exception is org.springframework.data.mapping.PropertyReferenceException: No property UNSORTED found for type OpenApiRequest!] with root cause
org.springframework.data.mapping.PropertyReferenceException: No property UNSORTED found for type OpenApiRequest!
```

`@PageableDefault` 사용에 대해서도 조금 달라졌는지, 정렬 방법을 명시해주지 않았을 때 UNSORTED 프로퍼티를 찾으려고 시도하며 에러를 발생했습니다. 생성되는 Pageable 객체를 확인 해 보니, UNSORTED 를 기준으로 오름차순 정렬 하려고 시도 하고 있었습니다. 정렬 기준을 정해주면 문제가 해결됩니다.

변경 전

```
public String findOpenapiRequest(@PageableDefault Pageable pageable, Model model) {
	...
}
```

변경 후

```
public String findOpenapiRequest(
@PageableDefault(sort = "requestTime", direction = Sort.Direction.DESC)Pageable pageable, Model model) {
	...
}
```

### multipart 설정

SpringBoot 2.x 버전으로 올라가면서 multipart 설정 방법이 변경되었습니다.  application.yml 파일에서 `http` 를 `servlet`으로 변경해줍니다.

변경 전

```yaml
spring.http.multipart:
  enabled: true
  max-file-size: ${MULTIPART_MAX_FILE_SIZE:10000MB}
  max-request-size: ${MULTIPART_MAX_REQUEST_SIZE:10000MB}
  file-size-threshold: 10KB
```

변경 후

```yaml
spring.servlet.multipart:
  enabled: true # default true. 생략 가능.
  max-file-size: ${MULTIPART_MAX_FILE_SIZE:10000MB}
  max-request-size: ${MULTIPART_MAX_REQUEST_SIZE:10000MB}
  file-size-threshold: 10KB
```

### spring.config.location

Spring Boot 1.X 버전에서는 `spring.config.location` 환경변수를 설정하면 해당 설정값이 추가로 등록 되었지만, 2.0 버전부터는 기존의 설정값을 무시하도록 변경되었습니다.

릴리즈 노트에서 눈에 띄게 안내했던 내용도 아니기 때문에 해당 상황에서 버그를 찾기가 굉장히 어려웠습니다.

제가 겪은 상황을 정리한 포스팅을 아래에 링크로 남겨 두겠습니다

> [일간에러) application.yml 파일을 못읽을때 spring.config.location](https://shanepark.tistory.com/351)

### SecurityAutoConfiguration

SecurityAutoConfiguration 클래스의 패키지가 변경되었습니다. 그렇기 때문에 기존에 applcation.yml 에서

```yml
spring.autoconfigure.exclude: 
  - org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration
```

로 작성 해 두었던 설정을 계속 쓴다면 아래의 오류가 발생합니다.

```java
Cannot resolve 'org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration' for key 'org.springframework.boot.autoconfigure.EnableAutoConfiguration' 
```

![image-20220412135347191](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-boot-migration.assets/image-20220412135347191.png)

또한 무시하고 그냥 프로젝트를 실행 하면, 스프링 시큐리티 자동 설정을 하기 때문에 실행시 아래와 같은 경고가 발생합니다.

```
Using generated security password: 8e9635b2-da71-49f5-9811-63eba7eb451c

This generated password is for development use only. Your security configuration must be updated before running your application in production.
```

![image-20220412135542918](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-boot-migration.assets/image-20220412135542918.png)

해결 방법은 그렇게 어렵지 않은데, 변경된 패키지를 입력 해 주면 됩니다.

security 패키지 하위에 servlet 라는 중간 패키지가 추가 되었습니다. 아래와 같이 변경 해 줍니다.

```yaml
spring.autoconfigure.exclude: 
  - org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
```



## 마치며

도무지 끝이 안보여 중간에 엎어지는거 아닌가. 회사의 숙원사업 중 하나였던 최신 스프링부트로 버전업하는 일이 이대로 엎어지는건 아닌가 걱정이 많았지만 하다보니 거의 마무리가 지어졌습니다.

물론 운영중인 프로젝트들에 적용이 되기 전에 충분한 테스트가 먼저 선행되어야 겠고, 예기치 못한 에러를 맞이하게 될 지도 모르지만 이제부터 기능추가할 때는 스프링부트 버전에 구애받지 않는다는 점이 너무나도 만족스럾습니다.

마이그레이션 작업을 해 보며 구석구석 오랫동안 들여다 보지 않았던 코드들도 보게 되며 숨겨져 있던 취약점도 발견 하게 되는 기회기 때문에 한번쯤은 해보실 만 하다고 생각합니다. 두번은 곤란하지만요.

ref: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.0-Migration-Guide

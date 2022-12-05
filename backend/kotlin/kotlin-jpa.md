# [Kotlin] 코틀린에서 JPA사용하기. LAZY 로딩이 의도대로 작동 하지 않는다면..

## Intro

얼마전 코틀린으로 진행중인 토이프로젝트에서 응답속도가 생각만큼 나오지 않기에 쿼리 나가는걸 하나하나 확인 해 보았습니다.

그랬더니 분명 LAZY로 되어있는 여러가지 연관관계들이, 따로 사용하는 곳도 없는데 전부 다 하나씩 Eager Fetch 처럼 불러지는게 확인 되었습니다.

코틀린에서 JPA를 사용하다보면 자바와는 다른 코틀린의 설계로 인해 의도치 않은 문제가 많이 발생 할 수 있는데요. 어떤 문제가 있었는지, 그리고 LAZY 로딩이 의도대로 작동하게끔 하려면 어떻게 해야 하는지에 대해 알아보겠습니다.

> 필요시 모든 예제 코드는 https://github.com/Shane-Park/helloKotlin/tree/master/jpatest 에서 확인 하실 수 있습니다.

## 예제 코드

### 프로젝트 생성

문제 상황을 재현하고 해결 하기 위해 스프링부트 프로젝트를 생성 해 보도록 하겠습니다.

![image-20221205205241179](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/kotlin-jpa.assets/image-20221205205241179.png)

> Kotlin 프로젝트를 생성 합니다. Language: Kotlin 으로 선택 해 줍니다.

![image-20221205205655176](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/kotlin-jpa.assets/image-20221205205655176.png)

> JPA 테스트를 위해 Spring Data JPA와 Spring Web 및 H2 Database 추가해 줍니다.

자동으로 Spring Initializr가 만들어준 gradle 설정 파일은 아래와 같습니다.

**build.gradle.kts**

```kotlin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.0"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.21"
    kotlin("plugin.spring") version "1.7.21"
    kotlin("plugin.jpa") version "1.7.21"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

```

이게 꽤나 중요한데요, 처음에 initializr 에서 몇가지를 빼먹어서 나중에 추가 하다 보면 몇가지 꼭 필요한 플러그인이 들어가있지 않기 때문에 JPA를 사용하려다가 여러가지 문제를 맞이하게 됩니다. 

### Plugins

먼저 어떤 플러그인들이 있는지 확인 해 보도록 하겠습니다. plugins에 Kotlin 및 스프링과 관련된 걸로 눈여겨 봐야 할 것은 두가지가 있는데요.

첫번째로 `kotlin("plugin.spring")`를 확인 해 보겠습니다.

![image-20221205210212949](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/kotlin-jpa.assets/image-20221205210212949.png)

> https://spring.io/guides/tutorials/spring-boot-kotlin/

java와는 다르게 코틀린에서는 기본 quialifer가 final로 되어 있기 때문에 프록시를 적극 활용하는 스프링 프레임워크에서 많은 제약이 발생 할 수 있는데요, 자동으로 open qualifier 를 붙여 주는 All-open 컴파일러 플러그인을 추가해줍니다. 이 덕분에 `@Configuration` 이나 `@Transaction` 같은 빈들이 의도대로 작동 하도록 해 줍니다. 이 외에도 아래의 어노테이션들이 붙었을 경우 자동으로 객체 및 하위 프로퍼티들을 자동으로 open 으로 변경 해 줍니다.

- @Component
- @Async
- @Cacheable
- @SpringBootTest
- @Controller
- @RestController
- @Service
- @Repository
- @Component

> All-Open Compiler 플러그인에 대해 더 자세한 내용은 https://kotlinlang.org/docs/all-open-plugin.html 를 참고 해주세요

두번째로 플러그인으로는 `kotlin("plugin.jpa")` 가 보입니다.

![image-20221205210222664](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/kotlin-jpa.assets/image-20221205210222664.png)

> https://spring.io/guides/tutorials/spring-boot-kotlin/

No-Arg 컴파일러 플러그인은 자동으로 **argument가 없는 생성자**를 추가 해 줍니다. 

> 자바로 코딩을 했을 때, JPA 스펙을 위해 엔티티 클래스에 롬복의 `@NoArgsConstructor`를 붙여줬던걸 생각 하면 이해하기가 쉽습니다. 

이렇게 만들어진 생성자는 인위적으로 만들어졌기 때문에 Java나 Kotlin에서 직접 호출되지는 못하지만 리플렉션에 활용 됩니다. 코틀린에서 굳이 second constructor를 생성 하지 않고도 JPA가 클래스를 만들 수 있도록 해주는 편리한 플러그인 입니다.

> No-arg compiler 플러그인에 대한 더 자세한 내용은 https://kotlinlang.org/docs/no-arg-plugin.html 를 참고해주세요.

### Depencencies

이번에는 의존성에 대해 확인 해 보겠습니다.

```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
```

Dependency를 쭉 보면, java에서는 보이지 않았던게 3가지 보이는데요 각각의 역할은 아래와 같습니다.

- `kotlin-stdlib-jdk8` jdk8에 해당하는 코틀린의 스탠다드 라이브러리
- `kotlin-reflect` 코틀린 리플렉션 라이브러리
- `jackson-module-kotlin` 코틀린 클래스와 data 클래스의 직렬화/역직렬화 지원. (기본생성자만 있는 클래스들뿐 아니라 세컨 생성자 혹은 스태틱 팩토리도 지원)

### 컴파일러 옵션

KotlinCompile 태스크도 눈에 띕니다.

```kotlin
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}
```

코틀린의 정말 핵심적인 기능중 하나가 `널 안정성` 인데요, null에 대해서 컴파일 타임에 다루다보니, java에서 아주 쉽게 발생하지만, 그 파장이 심각하기로 악명 높은 `NullPointerException` 이 런타임에 발생하는걸 방지해줍니다. Optional로 래핑할 필요도 없습니다.

[JSR-305](https://jcp.org/en/jsr/detail?id=305)에 정의된 @NotNull 주석은 java에서 null을 사용할 수 없음을 표기하기 위해 사용되는데요, Kotlin support for JSR 305 annotations 덕분에 Kotlin 개발자가 null 관련 이슈들을 컴파일 타임에 처리할 수 있습니다. 

위와 같이 컴파일러 플래그에 `-Xjsr305` 를 strict 옵션으로 추가 하면 활성화 됩니다.

## 테스트

### Entity 및 Repository 생성

Github의 이슈와 마일스톤을 본따서, 아주 간단하게 아래와 같은 관계의 엔티티를 생성 해 보았습니다.

![image-20221205214311547](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/kotlin-jpa.assets/image-20221205214311547.png)

양방향 관계까지는 맺지 않고 단방향으로만 심플하게 작성하였습니다.

**Issue.kt**

```kotlin
package com.example.jpatest.entity

import jakarta.persistence.*

@Entity
class Issue(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var title: String,
    var content: String,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "milestone_id")
    val milestone: Milestone,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    val author: Member,

    )
```

**Milestone.kt**

```kotlin
package com.example.jpatest.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Milestone(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    val title: String,

)

```

**Member.kt**

```kotlin
package com.example.jpatest.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Member(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    val name: String

)
```

이후 Repository도 각각 생성 해 줍니다.

```kotlin
interface IssueRepository : JpaRepository<Issue, Long>
interface MilestoneRepository : JpaRepository<Milestone, Long>
interface MemberRepository : JpaRepository<Member, Long>
```

### 설정파일

테스트를 위한 데이터베이스 설정 파일을 작성 합니다. H2를 활용합니다.

**application.yml**

```yaml
spring:
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: create-drop
      dialect.H2Dialect: org.hibernate.dialect.H2Dialect
    datasource:
        url: jdbc:h2:mem:testdb
        username: sa

```

>  쿼리가 나가는걸 눈으로 확인 하기 위해 show-sql 옵션을 true로 해 두었습니다.

### 테스트 코드 작성

이제 테스트를 위한 준비가 되었으니, 테스트 코드를 작성 해 줍니다.

**IssueRepositoryTest.kt**

```kotlin
package com.example.jpatest.repository

import com.example.jpatest.entity.Issue
import com.example.jpatest.entity.Member
import com.example.jpatest.entity.Milestone
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IssueRepositoryTest {

    @Autowired
    lateinit var issueRepository: IssueRepository

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var milestoneRepository: MilestoneRepository

    @BeforeAll
    fun setup() {
        val member = memberRepository.save(Member(name = "user"))
        val milestone = milestoneRepository.save(Milestone(title = "milestone"))
        issueRepository.save(Issue(title = "issue", content = "content", author = member, milestone = milestone))
    }

    @Test
    fun `test how many queries called`() {
        issueRepository.findById(1L).get()
    }

}

```

이제 의도대로라면 Issue의 Milestone과 Member 를 모두 LAZY 로딩을 하기로 했기 때문에, issue를 불러오는 쿼리가 **한번**만 나가야 합니다.

정말 의도대로 되었을까요? 확인을 해 보면..

![image-20221205215542703](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/kotlin-jpa.assets/image-20221205215542703.png)

> 쿼리가 3번 나갔습니다...

처음에 의도한대로 issue 에서 `findById`를 하는 것 까진 괜찮았는데, 그 다음에 아직 사용하지도 않은 member와 milestone 까지도 다 불러오네요

디버그를 해서 확인 해 봅니다. 

![image-20221205215844233](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/kotlin-jpa.assets/image-20221205215844233.png)

> 적당한 위치에 브레이크 포인트를 찍고, evaluate 해 보면

![image-20221205215920189](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/kotlin-jpa.assets/image-20221205215920189.png)

`milestone`과 `author` 가 프록시 객체가 들어와야 하는데, 실제 엔티티가 들어와 있습니다. 어떻게 된 것일까요?

## 원인 및 해결

### 원인

JPA 스펙에 따르면, 모든 **JPA와 관련된 클래스와 프로퍼티**들은 반드시 `open` 되어야 합니다. Hibernate의 경우에는 이 규칙을 강요하지는 않기 때문에 final entity를 마주한다고 해도 에러를 던지거나 하진 않는데요.

하지만 final class는 <u>상속이 불가능</u>하기 때문에 proxy 메카니즘이 정상적으로 작동하지 못하고, 프록시 생성이 안되기 때문에 Lazy loading도 동작 할 수도 없습니다. 이는 곧, 항상 `fetch eager` 옵션으로 작성한다는걸 의미합니다.

자바와는 다르게, 코틀린에서는 모든 클래스와 프로퍼티가 기본적으로 final로 선언 되는데요, 이에 대한 설명은 제가 이전에 [Mockito 사용시 final class 문제 해결](https://shanepark.tistory.com/422) 할 때 작성했던 글로 갈음하겠습니다.

![image-20221205221909583](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/kotlin-jpa.assets/image-20221205221909583.png)

> https://shanepark.tistory.com/422

### 해결1. open

문제를 알았으니 해결을 할 수 있습니다. 모든 객체와 프로퍼티에 open 을 붙여서 final class가 아니게 선언 해 주면 됩니다.

![image-20221205222021424](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/kotlin-jpa.assets/image-20221205222021424.png)

> Issue

![image-20221205222030789](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/kotlin-jpa.assets/image-20221205222030789.png)

> Milestone

![image-20221205222042015](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/kotlin-jpa.assets/image-20221205222042015.png)

> Member

보기만 해도 어질어질 합니다. 이중 한군데의 property 에서 라도 open을 깜빡 잊고 빼먹는다면, 해당 엔티티는 Lazy 로딩이 되지 않습니다. 

지금은 엔티티가 단 3개고, 프로퍼티도 얼마 없지만.. 더 늘어난다면 두통이 올 것 같습니다. 어쨌든 모두 open을 붙였으니 테스트를 진행 해 보겠습니다.

![image-20221205222251350](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/kotlin-jpa.assets/image-20221205222251350.png)

> 정말 쿼리가 딱 한번만 나갔습니다!

이번에도 디버그 모드에서 확인을 해 보면

![image-20221205222354982](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/kotlin-jpa.assets/image-20221205222354982.png)

> `milestone`과 `author`는 **HibernateProxy** 객체가 들어가 있는 것이 확인 됩니다.

### 해결2. allopen

일단 먼저 하나씩 일일히 붙여 뒀던 모든 open을 제거해 주었습니다. open을 항상 수동으로 작성해주려고 하면 개발자가 분명 실수하게 될 것입니다.

![image-20221205222714577](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/kotlin-jpa.assets/image-20221205222714577.png)

> Shift + Command + R 로 한번에 open 이라는 단어 제거

이쯤에서 본글 초반에 `예제코드 > Plugins` 에서 확인했던 kotlin-spring 의 All-Open 플러그인을 돌이켜 보면, 아래와 같은 어노테이션들이 있으면 자동으로 open 객체로 만들어준다고 했었는데요

![image-20221205222546581](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/kotlin-jpa.assets/image-20221205222546581.png)

> https://kotlinlang.org/docs/all-open-plugin.html#command-line-compiler

지금 저희가 본격적으로 사용하고 있는 `@Entity`가 없습니다. 엔티티는 open이 자동으로 되지 않고 있다는 뜻 인데요

스프링 공식 문서를 확인 해 보면 `Entity`, `Embeddable`, `MappedSuperclass` 를 추가하라고 제안합니다.

![image-20221205222825434](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/kotlin-jpa.assets/image-20221205222825434.png)

> https://spring.io/guides/tutorials/spring-boot-kotlin/

추천하는 대로 그대로 추가 해 줍니다.

**build.gradle.kts**

```kotlin
allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}
```

![image-20221205222930457](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/kotlin-jpa.assets/image-20221205222930457.png)

> 40~44 번 라인에 allOpen을 추가 한 상태

추가 후 다시 테스트 해 봅니다.

결과가 어떤가요? 위의 내용을 붙여서 여기에서 쿼리가 한번만 나가게 잘 고쳐지신 분도 있을테고 혹은 여전히 쿼리가 3번 나가는 분들도 계실 수 있는데요.

저는 당황스럽게도 여전히 쿼리가 3번 나갔습니다.

![image-20221205223025787](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/kotlin-jpa.assets/image-20221205223025787.png)

다행히도, 이번에는 어렵지 않게 원인을 파악 할 수 있었는데요.. 

열흘 전 쯤인 2022년 11월 24일부터 **SpringBoot3.0**이 GA로 풀리게 되었고, 저도 이번에 처음으로 스프링부트 3.0으로 테스트 프로젝트를 생성 해 보았거든요. 

그런데 Entity 어노테이션을 붙이며 import 할 때 아주 눈에 띄는게 있어서 머리속에 담아 두고 있던게 있었는데

![image-20221205223217937](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/kotlin-jpa.assets/image-20221205223217937.png)

> jakarta.persistence.Entity

눈썰미 좋은 분들은 위에서 엔티티를 생성 할 때부터 눈치 채셨겠지만, 오라클과의 상표권 문제로 인해 SpringBoot3.0 부터는 javax가 아닌 `jakarta` 패키지로 옮겨졌습니다.

그렇게 때문에 스프링 부트 3.0 이후의 버전을 사용한다면 아래와 같이 작성 해 주셔야 합니다.

```kotlin
allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}
```

![image-20221205223725441](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/kotlin-jpa.assets/image-20221205223725441.png)

> jakarta.persistence 패키지로 변경된 상태

이후 테스트를 다시 진행 해 보면 의도대로 쿼리가 깔끔하게 한번만 나갑니다.

![image-20221205223809492](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/kotlin-jpa.assets/image-20221205223809492.png)

지금까지 코틀린에서 JPA를 문제 없이 사용하는 방법에 대해 알아보았습니다.

위의 예제 코드들은 https://github.com/Shane-Park/helloKotlin/tree/master/jpatest 에서 전부 확인 하실 수 있습니다.

이상입니다. 

**References**

- https://spring.io/guides/tutorials/spring-boot-kotlin/
- https://medium.com/@millon.leo/are-you-sure-the-lazy-fetch-mode-is-configured-correctly-in-your-kotlin-hibernate-project-e75f072cdb5d
- https://www.jpa-buddy.com/blog/best-practices-and-common-pitfalls/
- https://kotlinlang.org/docs/all-open-plugin.html#command-line-compiler
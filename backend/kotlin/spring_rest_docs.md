# [Kotlin] Spring Rest Docs 적용하기 build.gradle.kts

## Intro

안녕하세요. 이번에 토이 프로젝트로 진행 중인 코틀린 프로젝트에 Spring REST Docs 를 도입을 해 보았습니다.

회사에서는 모든 프로젝트가 메이븐으로 되어 있고, gradle 도 학습용으로만 사용해 보았기 때문에 다루는 방법을 아직은 잘 알지 못하는 상황인데 그와중에 코틀린 DSL 까지 사용하려니 레퍼런스도 충분하지 않아 굉장히 힘들었습니다.

기본적으로 Spring REST Docs 공식 레퍼런스의 안내를 따르고 막히는 부분들에서는 Kotlin DSL 을 사용한 다른 프로젝트들을 찾아 비교해보며 간신히 API 페이지를 띄우는데 성공 했는데, 그 과정을 공유해보겠습니다. 

저처럼 Kotlin 프로젝트에서 Spring Rest Docs 도입에 어려움을 겪는 분들에게 도움이 되었으면 합니다.

## Spring Rest Docs

### API 문서화

여러가지 REST API 컨트롤러들을 추가하다 보면, 점점 쌓이고 쌓이다 어느순간부터는 그 수가 감당하기 어려워 집니다.

![image-20220930223319081](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/spring_rest_docs.assets/image-20220930223319081.png)

> IntelliJ IDEA 에서 제공해주는 Endpoints 를 이용하면 한눈에 알아보기 좋게 정리 해 주기는 하지만, 문서라고 보기엔 부족합니다.

이렇게 API 문서를 작성해야 할 필요성이 대두되는데요. 요즘에는 **Swagger**와 **Spring Rest Docs** 가 보편적으로 사용되고 있습니다.

### Swagger

회사에서 진행중인 프로젝트에서 API 문서화 요구가 있어서 올해 초에 Swagger를 도입 해 보았었는데요, 몇가지 이유가 있었지만 그때 Swagger를 선택한 가장 큰 이유는 부끄럽게도 테스트 코드 작성의 부담 이었습니다. Spring Rest Docs 에서는 테스트코드를 무조건 작성해야 하고 테스트에 성공해야만 문서가 작성되기 때문입니다.

아주 간편하게 그럴싸한 API 문서를 만들어내는 Swagger를 사용하며 큰 매력을 느꼈지만, 어노테이션 기반으로 프로덕션 코드에 문서화에 대한 내용이 들어가야 한다는 점 이나 현행화에 대한 보장이 안된다는 단점 또한 확실하게 느낄 수 있는 경험 이었습니다.

Swagger 적용 방법에 대해서는 아래의 링크에 정리 해 두었습니다.

> [Swagger 활용 API Document 자동 생성](https://shanepark.tistory.com/336)

**Swagger의 장점**

- 적용하기가 매우 간단하다. springfox-boot-starter와 springfox-swagger-ui 를 의존성에 추가 하고 Swagger 설정 파일 하나만 빈으로 등록 하면 바로 `/swagger-ui` 경로에 그럴싸한 문서 페이지가 생성됩니다.
- Spring Rest Docs와 다르게 테스트 코드를 작성 할 필요가 없다.
- Postman 처럼 API를 쉽게 테스트 해 볼 수 있는 화면을 제공 해 준다.

**Swagger의 단점**

- `@Api`, `@ApiResponses`, `@ApiParam`, `@ApiModel`등의 어노테이션 및 문서에 들어가는 텍스트가 프로덕트 코드에 덕지덕지 얽혀버린다.
-  코드가 바뀌었을 때, 작성해 둔 문서를 일일히 함께 변경 해 주지 않으면 실제 코드와 일치하지 않아 현행화에 대한 보장이 되지 않는다.

이번에 코틀린으로 진행중인 토이 프로젝트에서는 이미 모든 코드 작성시 일일히 테스트 코드를 함께 작성 하고 있으며, 이전 경험을 통해 Swagger의 단점에 대해 어느정도 몸소 느꼈기에, 이번에는 Spring Rest Docs를 사용해 문서화를 하기로 했습니다.

## Spring Rest Docs

### 문서 확인

이번글에서는 간단한 예제와 샘플 코드를 바탕으로 코틀린 프로젝트에 Spring Rest Docs를 적용시키는 방법에 대해 알아보겠습니다.

![image-20220930230643946](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/spring_rest_docs.assets/image-20220930230643946.png)

> https://spring.io/projects/spring-restdocs#learn

Rest Docs의 Current 버전은 2.0.6 입니다. 해당 버전의 Reference Doc을 참고 해서 진행 해 보도록 하겠습니다.

![image-20220930231025069](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/spring_rest_docs.assets/image-20220930231025069.png)

>  https://docs.spring.io/spring-restdocs/docs/current/reference/html5/
>
> 참고로 전체 샘플 코드들은 https://github.com/spring-projects/spring-restdocs/tree/v2.0.6.RELEASE/samples 에서 확인 하실 수 있는데 아쉽게도 Kotlin DSL로 작성 된 것은 없습니다.

레퍼런스 문서에는 간단한 소개 및 샘플 코드들 안내 이후에 최소 사항을 알려줍니다. 

java 8과 스프링 프레임워크 5 이상이 필요하네요

![image-20220930231208117](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/spring_rest_docs.assets/image-20220930231208117.png)

이제 build.gradle 에 추가 되어야 할 내용들을 안내 해 주는데요, 너무 많고 당황스럽기 때문에 저희는 한번에 하지 않고 서서히 추가 시키면서 진행 하도록 하겠습니다.

무작정 다 붙여놓고 잘 작동하기를 바라며 기도를 해도 저희는 `build.gradle.kts` 파일을 쓰고 있기 때문에 어림 없습니다.

### 의존성 추가

RestDocs 사용을 위한 최소한의 의존성만 추가 해 나가며 점점 필요한 내용들을 build.gradle.kts 에 붙여 가며 진행 할 예정입니다.

제일 먼저 필요한 의존성을 추가 해 줍니다. 일단 spring-restdocs-mockmvc 하나만 등록 하도록 하겠습니다.

테스트에서 사용 될 것이기 때문에 testImplementation에 추가 해 줍니다.

**build.gradle.kts**

```kotlin
// Spring Rest Docs
testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
```

![image-20220930232337462](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/spring_rest_docs.assets/image-20220930232337462.png)

> 맨 아래 한줄이 추가되었습니다.

### 테스트 코드 작성

문서화를 위한 간단한 API 테스트 코드를 작성 하도록 하겠습니다. 

공식 문서에서 JUnit5의 설정 방법을 확인 하면 아래와 같이 안내 해 주고 있습니다. 그 외 jUnit4 를 사용하거나 기타 다른 방법을 원하시면 [문서](https://docs.spring.io/spring-restdocs/docs/current/reference/html5/#getting-started-documentation-snippets-setup-manual)를 확인 해 주세요.

![image-20220930233348493](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/spring_rest_docs.assets/image-20220930233348493.png)

안내대로 테스트 클래스에 어노테이션을 달아 줍니다.

```kotlin
@ExtendWith(RestDocumentationExtension::class)
@SpringBootTest
class QuizApiControllerDocTest {
	...
}
```

RestDocumentationExtension을 적용 하면 기본 설정으로 Maven 에서는 `target/generated-snippets` 에, Gradle은 `build/generated-snippets` 경로에 문서 조각들을 생성 한다고 합니다.

![image-20220930233650630](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/spring_rest_docs.assets/image-20220930233650630.png)

그 다음으로는 안내에따라 mockMvc 를 @BeforeEach 내에서 할당 해 줍니다.

위의 자바 코드를 코틀린으로 작성 하면 아래와 같이 쓸 수 있습니다.

```kotlin
private var mockMvc: MockMvc? = null

@BeforeEach
fun setUp(webApplicationContext: WebApplicationContext, restDocumentation: RestDocumentationContextProvider) {
  this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
  .apply<DefaultMockMvcBuilder>(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
  .build()
}
```

MockMvc 인스턴스는 MockMvcRestDocumentationConfigurer에 의해 설정이 되는데요, documentationConfiguration() 스태틱 메서드로 mockMvc 인스턴스 객체를 획득 할 수 있습니다. 받은 객체를, 미리 전역 변수로 선언 해 둔 mockMvc 변수에 할당 해 줍니다.

이제 그렇게 받아온 mockMvc 로 RESTful 서비스의 테스트 코드를 작성 합니다.

아래는 공식 문서에 작성된 샘플 입니다.

**MockMvc**

```kotlin
this.mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON)) 
		.andExpect(status().isOk()) 
		.andDo(document("index")); 
```

**WebTestClient**

```kotlin
this.webTestClient.get().uri("/").accept(MediaType.APPLICATION_JSON) 
		.exchange().expectStatus().isOk() 
		.expectBody().consumeWith(document("index")); 
```

**REST Assured**

```kotlin
RestAssured.given(this.spec) 
		.accept("application/json") 
		.filter(document("index")) 
		.when().get("/") 
		.then().assertThat().statusCode(is(200)); 
```

저는 아래와 같이 샘플 테스트 코드를 작성 해 보았습니다.

```kotlin
@SpringBootTest
@ExtendWith(RestDocumentationExtension::class)
class QuizApiControllerDocTest {

    private var mockMvc: MockMvc? = null

    @Autowired
    private lateinit var quizRepository: QuizRepository

    @BeforeEach
    fun setUp(webApplicationContext: WebApplicationContext, restDocumentation: RestDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply<DefaultMockMvcBuilder>(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
            .build()
    }

    @Test
    @DisplayName("retrieve all quizzes")
    fun allQuizTest() {
        this.mockMvc!!.perform(
            MockMvcRequestBuilders.get("/api/quiz")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(document("index"))
    }

}
```

아무래도 문서화를 위한 테스트 코드 작성시 중복되는 공통의 설정이 제법 있을 테니, 베이스 코드를 작성 해 두고 상속받아 사용한다면 좀 더 간편하게 작성 할 수 있겠습니다.

이제 방금 작성한 테스트 코드를 실행 해 봅니다. IDE의 도움을 받아서 방금 작성한 코드만 실행 하거나 아래처럼 전체 테스트를 수행 해도 됩니다.

```bash
./gradlew clean test --info
```

이제 테스트 수행에 성공 했다면

![image-20221001092427710](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/spring_rest_docs.assets/image-20221001092427710.png)

아래에 보이는 것 처럼 `build/generated/generated-snippets`  경로에 방금 실행한 테스트 코드에 해당하는 adoc 파일이 추가된 것을 확인 하실 수 있습니다.

![image-20221001092307395](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/spring_rest_docs.assets/image-20221001092307395.png)



`http-response.adoc` 파일을 열어 보면 아래처럼 보여 줍니다.

![image-20221001092621156](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/spring_rest_docs.assets/image-20221001092621156.png)

> 처음에는 왼쪽의 텍스트만 보여 주는데, intelliJ IDEA가 제안하는 AsciiDoc 플러그인을 설치 하시면 오른쪽 처럼 보기 좋게 미리보기를 만들어 줍니다.

### Snippets 활용하기

이제 준비된 문서 조각을 토대로 전체 API 문서를 작성 해 줘야 합니다.

![image-20221001092854697](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/spring_rest_docs.assets/image-20221001092854697.png)

> https://docs.spring.io/spring-restdocs/docs/current/reference/html5/#getting-started-documentation-snippets-setup-manual

빌드 툴에 따라 각각 지정되어 있는 소스파일 위치에 `.adoc` 소스 파일을 작성 해 주면 Generated files 위치에 html 파일을 생성 해 준다고 되어 있습니다.

일단 src 하위에 `docs` 폴더를 생성 해 주고

![image-20221001093133730](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/spring_rest_docs.assets/image-20221001093133730.png)

그 하위에 asciidoc 폴더를 만든 후에, index.adoc 파일을 생성 해 주었습니다.

![image-20221001093209882](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/spring_rest_docs.assets/image-20221001093209882.png)

 이제 `index.adoc` 파일에는 문서화 하고 싶은 문서 조각들을 include 시켜 줍니다.

저는 생성된 조각 6 개 중 3개를 사용 해 보도록 하겠습니다.

```adoc
include::{snippets}/index/http-request.adoc[]
include::{snippets}/index/http-response.adoc[]
include::{snippets}/index/curl-request.adoc[]
```

![image-20221001093511288](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/spring_rest_docs.assets/image-20221001093511288.png)

### Asciidoctor 추가해 API 문서 생성

이제 빌드 할 때 방금 작성한 `.adoc` 파일의 설정에 따라 완성된 API 문서를 작성 할 수 있도록 아스키 닥터 플러그인을 추가 하도록 하겠습니다.

제일 먼저 플러그인을 추가 해 주고

```kotlin
plugins {
  	...
    id("org.asciidoctor.jvm.convert") version "3.3.2"
}
```

의존성도 추가 해 주고

```kotlin
dependencies {
		...
    // Spring Rest Docs
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc") // 기존에 이미 추가했음.
    testImplementation("org.springframework.restdocs:spring-restdocs-asciidoctor") // 새로 추가 할 것.
}
```

태스크를 추가 해 줍니다.

test -> asciidoctor -> build 순으로 설정 되도록 dependsOn을 걸어 주었으며, 위에서 snippets 들이 작성된 폴더인 `build/generated-snippets` 경로를 test 에서의 outputs.dir 및 asciidoctor의 inputs.dir 로 지정 해 주었습니다.

```kotlin
tasks {
    val snippetsDir by extra { file("build/generated-snippets") }

    test {
        outputs.dir(snippetsDir)
    }

    asciidoctor {
        inputs.dir(snippetsDir)
        dependsOn(test)
    }

    build {
        dependsOn(asciidoctor)
    }
}
```

이제 빌드를 진행 해 봅니다

```bash
./gradlew clean build
```

빌드를 마치니 `build/docs/asciidoc` 폴더 하위에 index.html 파일이 생성 되었습니다!

![image-20221001100852831](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/spring_rest_docs.assets/image-20221001100852831.png)

이제 작성된 파일을 서빙이 가능한 위치로 옮겨 주는 마지막 작업을 추가 해 줍니다. 아까 등록한 asciidoctor 태스크에 doLast로 copy 작업을 등록 했습니다. 

이렇게 하면 생성된 html 파일을 `src/main/resources/static/docs` 폴더로 옮겨 줍니다.

```kotlin
asciidoctor {
  inputs.dir(snippetsDir)
  dependsOn(test)
  doLast {
    copy {
      from("build/docs/asciidoc")
      into("src/main/resources/static/docs")
    }
  }
}
```

이제 새로 빌드를 해 보면

```bash
./gradlew clean build 
```

![image-20221001101351344](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/spring_rest_docs.assets/image-20221001101351344.png)

방금 지정한 copy into 경로인 `resources/static/css/docs` 경로에 index.html 파일이 생성 된 것이 확인 됩니다.

서버를 띄워서 확인 해 보도록 하겠습니다.

```bash
java -jar build/libs/quidev-0.0.1-SNAPSHOT.jar 
```

방금 빌드된 파일을 `java -jar` 로 띄워도 되고 간단하게 IDE에서 실행시켜도 상관 없습니다.

![image-20221001101503025](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/spring_rest_docs.assets/image-20221001101503025.png)

이제 `http://localhost:8080/docs/index.html` 경로를 확인 해 봅니다.

![image-20221001102137932](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/spring_rest_docs.assets/image-20221001102137932.png)

앗. html 파일을 생성 하긴 했는데, index.adoc에서 include 한 경로에 문제가 있습니다. {snippets} 경로를 못찾거나 index.html 파일 생성 시점에 문서 조각들이 생성이 되지 않았나 봅니다.

task 쪽을 조금 손을 보도록 하겠습니다. asciidoc 기본 설정을 등록 하도록 했습니다.

asciidoctor 에 관한 내용만 추리면 아래와 같습니다.

```kotlin
val asciidoctorExt: Configuration by configurations.creating
dependencies {
    asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor")
}

val snippetsDir by extra { file("build/generated-snippets") }
tasks {
    test {
        outputs.dir(snippetsDir)
    }

    asciidoctor {
        inputs.dir(snippetsDir)
        configurations(asciidoctorExt.name)
        dependsOn(test)
        doLast {
            copy {
                from("build/docs/asciidoc")
                into("src/main/resources/static/docs")
            }
        }
    }

    build {
        dependsOn(asciidoctor)
    }
}
```

  

제가 작성한 **build.gradle.kts** 전문은 아래와 같습니다.

```kotlin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.2"
    id("io.spring.dependency-management") version "1.0.12.RELEASE"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    kotlin("plugin.jpa") version "1.6.21"
    kotlin("kapt") version "1.7.10"
}

group = "kr"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity5")
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.8.1")
    // Database
    implementation("org.postgresql:postgresql:42.5.0")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
    // QueryDSL
    implementation("com.querydsl:querydsl-jpa:5.0.0")
    kapt("com.querydsl:querydsl-apt:5.0.0:jpa")
    // Spring Boot Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    // Spring Rest Docs
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
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

val asciidoctorExt: Configuration by configurations.creating
dependencies {
    asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor")
}

val snippetsDir by extra { file("build/generated-snippets") }
tasks {
    test {
        outputs.dir(snippetsDir)
    }

    asciidoctor {
        inputs.dir(snippetsDir)
        configurations(asciidoctorExt.name)
        dependsOn(test)
        doLast {
            copy {
                from("build/docs/asciidoc")
                into("src/main/resources/static/docs")
            }
        }
    }

    build {
        dependsOn(asciidoctor)
    }
}

```

이렇게 해서 다시 빌드 후 `http://localhost:8080/docs/index.html` 경로를 확인 해 봅니다.

![image-20221001110224672](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/spring_rest_docs.assets/image-20221001110224672.png)

> 준비 한 API 문서가 표시 드디어 됩니다.

index.adoc 파일을 조금 수정 해 주면 조금 더 보기 좋아집니다.

```adoc
= Quidev API
:toc:

== [GET] /api/quiz

=== Curl
include::{snippets}/index/curl-request.adoc[]
=== Request
include::{snippets}/index/http-request.adoc[]
=== Response
include::{snippets}/index/http-response.adoc[]
```

![image-20221001114758240](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/spring_rest_docs.assets/image-20221001114758240.png)

이제부터는 Spring REST Docs의 요청, 응답 필드를 설정 해 주고 API에 샘플 데이터도 넣어 주는 등의 작업이 필요합니다.

해당 작업들은 본 글의 내용을 벗어나기 때문에 이쯤에서 마무리 하겠습니다.

이상입니다.

 

**References**

- https://docs.spring.io/spring-restdocs/docs/current/reference/html5/
- https://github.com/bswsw/kotlin-spring-rest-docs
- https://github.com/spring-projects/spring-restdocs/blob/main/samples/junit5/build.gradle
- https://github.com/awakuwaku/spring-rest-docs-kotlin-sample/blob/main/build.gradle.kts
- https://github.com/unbroken-dome/gradle-helm-plugin/blob/master/build.gradle.kts
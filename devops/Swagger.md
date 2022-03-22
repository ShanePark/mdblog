# `Swagger 활용 API Document 자동 생성

## Intro

프론트엔드와 백엔드의 업무가 점점 더 구분되어가고, MSA가 보편화되고 있는 지금의 추세에서 RESTAPI의 쓰임이 점점 더 많아지고 있습니다. 외부에 공개하는 API 뿐만 아니라 소프트웨어 내부적으로만 사용하는 API라고 해도 어플리케이션이 점점 커질수록 개발자의 기억력에만 의존하기에는 그 규모가 점점 버거워지기 마련입니다.

특히나 API를 공개하거나 개발자간의 협력에서 필요한 상황이 오면 체계화된 읽기 좋은 API Document의 작성은 선택이 아닌 필수입니다. 마침 회사에서 진행중인 프로젝트에도 조금의 여유가 생겨 API Document 정리를 해야할 때가 왔습니다.

여러가지 오픈소스 선택지가 있습니다만 이번에는 Swagger를 선택해서 테스트를 진행 해 보았으며 꽤나 만족스러웠습니다. 심지어 마크다운 문법을 지원합니다.

간단한 스프링 부트 프로젝트 예제를 통해 적용 해 보겠습니다. RestAPI를 사용하는 아무 스프링 부트 프로젝트 하나 준비하시면 됩니다.

## Spring Boot 프로젝트에 적용

### 의존성 추가

**Maven**

pom.xml 파일에 아래의 dependency를 추가 해 줍니다.

```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-boot-starter</artifactId>
    <version>3.0.0</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>3.0.0</version>
</dependency>
```

**Gradle**

```groovy
implementation group: 'io.springfox', name: 'springfox-boot-starter', version: '3.0.0'
implementation group: 'io.springfox', name: 'springfox-swagger-ui', version: '3.0.0'
```

### Bean 등록

**SwaggerConfig.java**

```java
@Configuration
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
}
```

RestController만 있는 프로젝트라면 paths에 `.paths(PathSelectors.any())` 를 쓰면 됩니다. 

제가 최종적으로 적용시킬 프로젝트에서는 뷰와 RESTAPI가 혼재되어 있기 때문에 api 경로를 특정 할 필요가 있었습니다

```java
@Bean
public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.ant("/**/api/**"))
        .build();
}
```

> 처음에는 `/api/**`로 했었는데, contextPath가 영향을 주는 것을 확인하고는 위와 같이 변경 하였습니다.  ContextPath를 명시적으로 넣기에는, 어플리케이션을 실행하는 환경에 따라 달라질 수 있기 때문에 유연하게 기입하는게 좋습니다. 

이렇게 할 경우에는 단점이 있는데, 요청 URL중 중간에 /api/가 들어가면 의도치 않게 포함될 수도 있습니다. 그런경우까지 커버하려면 ContextPath를 servletContext 빈에서 받아와서 ant에 넣어 주면 됩니다.

```java
private ServletContext context;

public void setServletContext(ServletContext servletContext) {
    this.context = servletContext;
}

@Bean
public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .ignoredParameterTypes(Context.class, Pageable.class, RepositoryCommand.class)
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.ant(servletContext.getContextPath() + "/api/**"))
        .build();
}
```

> 프로젝트에서 사용하고 있는 Docket 등록 코드입니다.

아래의 예제에서는 `.paths(PathSelectors.any())` 로 진행하였습니다.

## 실행

이제 모든 설정이 완료 되었으니 프로젝트를 실행 해 줍니다. Swagger는 API 에 대한 정보를 JSON response로 주는데요, 데이터의 양이 방대하기 때문에 가독성이 현저히 떨어집니다. 위에서 저희는 swagger-ui 를 미리 추가 해 두었기 때문에 이를 이용해 깔끔하게 정리된 문서를 확인 할 수 있습니다. 브라우저를 띄워 `(contextPath)/swagger-ui` 주소로 요청을 보내봅니다.

http://localhost:8080/swagger-ui

> 혹시 404가 뜬다면 `/swagger-ui/index.html` 주소를 입력 해 보세요. swagger-ui 버전에 따라 경로가 다르다고 합니다.

![image-20220223160947956](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220223160947956.png)

> 보기에 아주 깔끔하게 API 컨트롤러들이 정리 되어 있습니다. 

member-controller를 클릭해서 확인 해 보겠습니다.

![image-20220223161038096](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220223161038096.png)

> 사용 가능한 API 목록을 아주 깔끔하게 보여줍니다. 

이중 하나를 클릭해 확인 해 보겠습니다.

![image-20220223161145844](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220223161145844.png)

> 필요한 Parameter와 응답 목록이 나옵니다. 심지어는 Postman 에서 처럼 API 테스트도 가능합니다.

## 문서 편집

API에 대해 설명이 없기 때문에, 어떤 API들이 있는지 정도는 파악 할 수 있다고 하더라도 상세스펙에 대한 부연 설명이 필요합니다. 이 때는 Swagger의 어노테이션 기반으로 자세한 설명을 붙일 수 있습니다.

사용할 수 있는 어노테이션 목록에 대해 알아보겠습니다.

### @Api

![image-20220223161953321](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220223161953321.png)

Controller의 이름을 작성할 수 있습니다. 

그런데 @Api 어노테이션이 굉장히 특이한 점이 있었는데요..

![image-20220223163913731](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220223163913731.png)

분명 description 항목을 deprecated 해 두었는데 막상 확인을 해 보면

![image-20220223163939591](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220223163939591.png)

value 에 적은 값은 온데간데 없고, deprecated된 description 에 작성한 내용만이 표기 되더라고요. 그래서 찾아보니, description이 deprecated된 대신 tags 를 사용하도록 권장하고 있습니다. description은 우측의 작은 글씨를 변경하지만, tags를 변경하면 좌측의 굵은 제목 텍스트를 변경 해 줍니다.

```java
@RestController
@RequiredArgsConstructor
@Api(tags = "외부API접근")
public class ExtensionsApiController extends ServletSupport {
```

위와 같이 설정 했을 때에는 아래와 같이 표기됩니다.

![image-20220316120150929](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220316120150929.png)

> 훨씬 알아보기 편합니다. tags를 적극적으로 사용해 주시면 되겠습니다.

### @ApiIgnore

![image-20220223164552575](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220223164552575.png)

> @Api에서 hidden=true로 하면 API 목록에서 숨길 수 있을 줄 알았는데 아무리 해도 사라지지 않더라고요.. 

![image-20220316141742360](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220316141742360.png)

> 다른 어노테이션을 이것 저것 다 달아보며 확인해보니 `@ApiIgnore` 어노테이션을 달아야만 해당 컨트롤러를 API 문서에서 숨길 수 있었습니다.

### @ApiResponses

![image-20220223162227704](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220223162227704.png)

각 응답코드별로 메시지를 작성할 수 있습니다.

![image-20220223164040490](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220223164040490.png)

### @ApiParam

![image-20220223162357791](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220223162357791.png)

파라미터에 대한 설명을 적을 수 있습니다.

![image-20220223164052289](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220223164052289.png)

### @ApiModel

![image-20220223162642277](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220223162642277.png)

Swagger가 적용될 Model에 상세한 정보를 작성할 수 있습니다.

### @ApiModelProperty

![image-20220223162735634](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220223162735634.png)

속성에 대한 설명도 붙일 수 있습니다.

![image-20220223164121695](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220223164121695.png)

### @ApiIgnore

![image-20220223164930769](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220223164930769.png)

숨겨야 할 API가 있다면 Controller나 Method 위에 @ApiIgnore 를 달아 주면 됩니다.

![image-20220223164954218](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220223164954218.png)

그러면 더이상 API 문서에 나타나지 않습니다.

### @Operation

메서드 단위로 문서 편집을 할 때에는 @Operation 메서드를 활용합니다.

```java
@GetMapping("/api/my/")
@Operation(summary = "제출 통계",
           description = "로그인 한 유저가 제출 중, 혹은 제출 완료한 데이터들의 현황을 반환합니다.<br>br로 줄 바꿀수도 있고\n\\n으로도 줄바꿈이 가능합니다.")
```

특정 메서드에 위와 같이 어노테이션을 달아 준다면

![image-20220321115142473](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220321115142473.png)

> 해당 메서드에 summary 와 description을 추가 해 주기 때문에 보다 자세한 설명을 달 수 있습니다.

심지어는 마크다운 문법을 지원합니다.

```java
@Operation(summary = "내 저장소 현황",
           description = "로그인 유저의 내 저장소 사용 현황을 조회합니다." +
           "\n### 조회 결과" +
           "\n- count: 저장된 총 파일 수" +
           "\n- quota: 사용가능 저장공간" +
           "\n- size: 사용중 저장공간")
```

![image-20220322135704390](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220322135704390.png)

### 파라미터에서 특정 클래스 무시

내부적으로 이용하는 대부분의 API 에서 편의를 위해 요청을 보낸 사용자의 회원 정보나 로케일 등을 받아오도록 해 두었는데요, 이 파라미터가 API 문서에 들어가면 굉장히 문서가 복잡해 집니다.

![image-20220316152240645](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220316152240645.png)

> 파라미터가 끝도 없습니다.

이럴때는 처음에 Docket을 Bean에 등록 할 때, 파라미터 타입에서 무시 할 클래스들을 기입 할 수 있습니다.

```java
@Bean
public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .ignoredParameterTypes(Context.class, Pageable.class)
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.ant("/**/api/**"))
        .build();
}
```

변경 후에는

![image-20220316152612748](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220316152612748.png)

필요없는 모든 파라미터가 사라지고 딱 필요한 것만 깔끔하게 남았습니다.

### 그 외

![image-20220223164408538](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220223164408538.png)

io.swagger.annotations 패키지를 확인해보면 그 외에도 많은 어노테이션들이 있습니다. 

![image-20220321114524535](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220321114524535.png)

뿐만 아니라 io.swagger.v3.oas.annotations 에도 추가로 사용 가능한 어노테이션이 있습니다.

## SpringBoot 2.0이하 혹은 스프링부트 없이 설정

### 오류

실무에서 진행중인 프로젝트에 적용을 하려다 보니 Spring Boot 버전이 낮아서 그대로 작동하지 않았습니다.

Swagger가 WebFluxConfigurer 를 필요로 하는데, SpringBoot 2.0에서 추가 된 class 이기 때문입니다. 제가 적용을 시도하고 있는 프로젝트는 Spring boot 1.5 버전을 사용하고 있습니다.

![image-20220223150145939](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220223150145939.png)

```
java.lang.NoClassDefFoundError: org/springframework/web/reactive/config/WebFluxConfigurer
```

### Dependency

```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>3.0.0</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>3.0.0</version>
</dependency>
```

이때는 springfox-boot-starter 대신 필요한 의존성들을 다 가지고 있는 springfox-swagger2 를 추가합니다.

또한 Swagger2를 명시적으로 활성화 해 주어야 합니다.

```java
@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {                                    
}
```

추가로, 리소스핸들러의 자동 설정에서 도움을 받지 못한다면 SwaggerUI 리소스 핸들러를 추가 해 주어야 합니다.

```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("swagger-ui.html")
      .addResourceLocations("classpath:/META-INF/resources/");

    registry.addResourceHandler("/webjars/**")
      .addResourceLocations("classpath:/META-INF/resources/webjars/");
}
```

스프링 부트를 사용하고, 버전이 낮다면 스프링부트 버전을 올리는게 좋겠습니다. 이번 일을 계기로 진행중인 프로젝트의 스프링부트 1.5 -> 2.5 마이그레이션도 진행 하였습니다.

이상입니다.

Ref

> https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api
>
> https://github.com/springfox/springfox/issues/1139

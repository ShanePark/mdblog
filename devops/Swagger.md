# Swagger 활용 API Document 자동 생성

## Intro

프론트엔드와 백엔드를 구분하고, MSA가 보편화되고 있는 지금의 추세에서 RESTAPI는 상당한 강점을 가지고 있습니다. 하지만 아무리 내부적으로만 사용한다고 해도 API의 양이 많아질수록 개발자의 기억력에만 의존하기에는 점점 버거워집니다.

특히나 API를 공개하거나 협력에 사용한다면 체계화된 보기 좋은 API Document 작성의 필요성이 대두되는데요, 마침 회사에서도 API Document 정리를 해야할 때가 왔습니다.

여러가지 공개 소프트웨어가 있습니다만 Swagger를 선택해서 진행 해 보았으며 꽤나 만족스러웠습니다.

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
//                .paths(PathSelectors.ant("/api/**"))
                .paths(PathSelectors.any())
                .build();
    }
}
```

RestController만 있는 프로젝트라면 paths에 `.paths(PathSelectors.any())` 를 쓰면 됩니다. 

제가 최종적으로 적용시킬 프로젝트에서는 뷰와 RESTAPI가 혼재되어 있기 때문에 api 경로를 특정 할 필요가 있습니다만 테스트용 프로젝트에서는 .any()로 진행합니다.

## 실행

이제 모든 설정이 완료 되었으니 프로젝트를 실행 해 줍니다. Swagger는 API 에 대한 정보를 JSON response로 주는데요, 데이터의 양이 방대하기 때문에 가독성이 현저히 떨어집니다. 위에서 저희는 swagger-ui 를 미리 추가 해 두었기 때문에, 깔끔하게 정리된 문서를 확인 할 수 있습니다.  브라우저를 띄워 contextPath 하위에 swagger-ui 를 넣어 요청해봅니다.

http://localhost:8080/swagger-ui

![image-20220223160947956](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220223160947956.png)

보기에 아주 깔끔하게 API 컨트롤러들이 정리 되어 있습니다. 이중 member-controller를 클릭해서 확인 해 보겠습니다.

![image-20220223161038096](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220223161038096.png)

사용 가능한 API 목록을 아주 깔끔하게 보여줍니다. 이중 하나를 클릭해 확인 해 보겠습니다.

![image-20220223161145844](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220223161145844.png)

> 필요한 Parameter와 응답 목록이 나옵니다. 심지어는 Postman 에서 처럼 API 테스트도 가능합니다.

## 문서 편집

API에 대해 설명이 없기 때문에, 어떤 API들이 있는지 정도는 파악 할 수 있다고 하더라도 상세스펙에 대한 부연 설명이 필요합니다. 이 때는 Swagger의 어노테이션 기반으로 자세한 설명을 붙일 수 있습니다.

사용할 수 있는 어노테이션 목록에 대해 알아보겠습니다.

### @Api

![image-20220223161953321](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220223161953321.png)

Controller의 이름을 작성할 수 있습니다. 

그런데 @Api 어노테이션이 버그인지 굉장히 특이한 점이 있었는데요..

![image-20220223163913731](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220223163913731.png)

분명 description 항목을 deprecated 해 두었는데 막상 확인을 해 보면

![image-20220223163939591](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220223163939591.png)

value 에 적은 값은 온데간데 없고, deprecated된 description 에 작성한 내용만이 표기 되더라고요.

![image-20220223164552575](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220223164552575.png)

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

숨겨야 할 API가 있다면 Controller나 Method 위에 @ApiIgnore 를 달아 줍니다.

![image-20220223164954218](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220223164954218.png)

그러면 더이상 API 문서에 나타나지 않습니다.

### 그 외

![image-20220223164408538](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/Swagger.assets/image-20220223164408538.png)

io.swagger.annotations 패키지를 확인해보면 그 외에도 더 많은 어노테이션들이 있습니다.

## SpringBoot 2.0이하

### 오류

실무에서 진행중인 프로젝트에 적용을 하려다 보니 Spring Boot 버전이 낮아서 그대로 작동하지 않았습니다.

WebFluxConfigurer 를 사용하는데, SpringBoot 2.0에서 추가 된 class 이기 때문입니다. 해당 프로젝트는 Spring boot 1.5 버전을 사용하고 있습니다.

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

이때는 springfox-boot-starter 대신 필요한 의존성들을 다 가지고 있는 springfox-swagger2 를 추가하면 됩니다.

Ref

> https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api

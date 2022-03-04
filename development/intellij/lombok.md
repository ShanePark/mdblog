# 일간에러 2022-03-04 Lombok@Slf4j 적용 에러 해결

## Error

```
java: Can't get the delegate of the gradle IncrementalProcessingEnvironment.

java: cannot find symbol
  symbol:   variable log
  location: class openapi.OpenApiController
```

롬복 플러그인을 활용해 @Slf4j 어노테이션을 사용하던 중 위와 같은 에러 발생.

## 해결

### 방법1

보통의 경우에는 Annotation Processor 설정이 안되있거나 Lombok 플러그인이 설치되어 있지 않을 때 발생.

둘중 하나라도 걸린다고 생각한다면

![image-20220302112332221](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/lombok.assets/image-20220302112332221.png)

> Lombok 플러그인이 설치되어 있는지 확인 하고, 없으면 설치

![image-20220302112401157](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/lombok.assets/image-20220302112401157.png)

> Preferences -> Build, Execution, Deployment -> Compiler -> Annotation Processors -> Enable annotation processing 체크박스에 체크

이후 IntelliJ를 재시작 하면 보통 해결 된다.

### 방법2

하지만 나의 경우는 위에 해당하는 상황이 아님. 프로젝트내 여러 모듈 중 특정 한개의 모듈에서만 문제가 발생 하고 있음.

**pom.xml**

```xml
<!-- https://mvnrepository.com/artifact/org.projectlombok/lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.2</version>
    <scope>provided</scope>
</dependency>
```

lombok 버전을 특정 버전으로 명시 해 두었는데, 기존의 버전이 새로운 환경과 맞지 않는 부분이 있지 않을까 의심됨. 그래서`<version></version>` 부분을 모두 삭제 한 뒤 스프링부트가 알아서 적절한 버전을 선택해 주도록 했다. Refresh 하니 에러가 더이상 발생하지 않음. 

![image-20220302113845614](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/lombok.assets/image-20220302113845614.png)

라이브러리를 확인 해 보니, 1.18.20 버전이 들어와 있음. 마이너 버전이라도 이후 18번의 패치가 더 이루어 지며 수정이 된 것으로 보임.

혹시 위에서의 방법들로 해결이 되지 않는다면 아래 링크를 참고하여 다른 방법을 시도해보세요.

> https://stackoverflow.com/questions/24006937/lombok-annotations-do-not-compile-under-intellij-idea/30125507#30125507

문제 해결 끝.
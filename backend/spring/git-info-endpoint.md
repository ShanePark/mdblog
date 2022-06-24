# [Spring Boot] git 정보 확인하는 endpoint 작성하기

## Intro

서버에 현재 배포되어 있는 어플리케이션의 버전 정보를 알 수 있는 방법이 있지 않을까 고민이 되었습니다.

하나의 프로젝트가 여러개의 서버에서 각기 다르게 서비스 되고 있다 보니 해당 정보를 관리자 페이지에서 확인 할 수 있게끔 하면 좋겠다는 생각이 들었는데요.

`git-commit-id-plugin` 이라는 메이븐 플러그인을 활용 하면 어렵지 않게 구현 할 수 있습니다.

![image-20220624150457179](/home/shane/Documents/git/shane/mdblog/backend/spring/git-info-endpoint.assets/image-20220624150457179.png)

> https://docs.spring.io/spring-boot/docs/2.1.7.RELEASE/reference/html/howto-build.html#howto-git-info

## 설정

### Maven Dependencies

**pom.xml**

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-maven-plugin</artifactId>
    </plugin>
    <plugin>
      <groupId>pl.project13.maven</groupId>
      <artifactId>git-commit-id-plugin</artifactId>
    </plugin>
  </plugins>
</build>
```

pom.xml 에 git-commit-id-plugin 플러그인을 추가 해 줍니다.

혹시 gradle 사용자라면 gradle-git-properties 플러그인을 대신 사용 할 수 있다고 합니다.

```groovy
plugins {
	id "com.gorylenko.gradle-git-properties" version "1.5.1"
}
```

 이제 `git.properties` 파일을 자동으로 생성 해 줍니다.

### 빌드

빌드 하는 과정에서 해당 파일을 생성 해 주기 때문에 메이븐을 클린 하고 새로 빌드 해 줍니다.

```bash
mvn clean install
```

![image-20220624151023123](/home/shane/Documents/git/shane/mdblog/backend/spring/git-info-endpoint.assets/image-20220624151023123.png)

> IntelliJ IDEA 에서는 우측의 Maven Tool 에서 clean과 install 을 할 수도 있습니다.

빌드 후에 `target/classes` 경로를 확인 해 보면

![image-20220624162358885](/home/shane/Documents/git/shane/mdblog/backend/spring/git-info-endpoint.assets/image-20220624162358885.png)

> target/classes

git.properties 파일이 생성 된것이 확인 됩니다.

아무리 어플리케이션을 껐다 켜도 해당 파일이 생성되는건 아니기 때문에 빌드를 새로 해 주어야 합니다.

![image-20220624162542598](/home/shane/Documents/git/shane/mdblog/backend/spring/git-info-endpoint.assets/image-20220624162542598.png)

> git.properties

해당 파일을 보면 커밋 아이디와 branch 명, 커밋 메시지 등 여러가지 git 정보가 포함 되어 있습니다.

### Bean  등록

**Application.java**

```java
@Bean
public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
    PropertySourcesPlaceholderConfigurer propsConfig 
        = new PropertySourcesPlaceholderConfigurer();
    propsConfig.setLocation(new ClassPathResource("git.properties"));
    propsConfig.setIgnoreResourceNotFound(true);
    propsConfig.setIgnoreUnresolvablePlaceholders(true);
    return propsConfig;
}
```

이제 git.properties 파일을 활용 하기 위해 위와같이 Bean을 등록 해 줍니다.

### Controller

`org.springframework.beans.factory.annotation.Value` 어노테이션을 활용하면 원하는 정보를 바로 받아 올 수 있습니다.

```java
@Value("${git.commit.message.short}")
private String commitMessage;

@Value("${git.branch}")
private String branch;

@Value("${git.commit.id}")
private String commitId;

@RequestMapping("/commitId")
@ResponseBody
public Map<String, String> getCommitId() {
    Map<String, String> result = new HashMap<>();
    result.put("Commit message",commitMessage);
    result.put("Commit branch", branch);
    result.put("Commit id", commitId);
    return result;
}
```

## 테스트

설정한 정보가 잘 넘어가는지 테스트를 진행 해 봅니다.

![image-20220624163529872](/home/shane/Documents/git/shane/mdblog/backend/spring/git-info-endpoint.assets/image-20220624163529872.png)

매핑해둔 URL 로 요청을 보내자, 원하는대로 commit id와 commit branch, 그리고 메시지 등 원하는 정보를 정상적으로 받아오는 것을 확인 할 수 있습니다.

감사합니다.
# [H2] Database "mem:testdb" not found, either pre-create it or allow remote database creation 해결

## Intro

스프링부트에서는 아래와 같은 설정으로 간단하게 인메모리 H2 데이터베이스를 사용 할 수 있습니다.

**application.yml**

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
```

**build.gradle.kts**

```kotlin
runtimeOnly("com.h2database:h2")
```

이후 h2:console 설정을 켠다면, 콘솔 페이지에서 데이터베이스를 직접 확인 해 볼 수 있는데요

```yaml
spring:
  h2: 
    console:
      enabled: true
      settings:
        web-allow-others: true
```

위와 같이 설정했을때에는, `/h2-console` 경로로 들어가면 데이터베이스를 웹 페이지에서 직접 확인 해 볼 수 있습니다.

그런데, Test Connection을 해 보았을 때 아래와 같이 오류가 발생하는 경우가 있습니다.

![image-20221115215104863](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/mem-testdb-not-found.assets/image-20221115215104863.png)

```
Database "mem:testdb" not found, either pre-create it or allow remote database creation (not recommended in secure environments) [90149-214] 90149/90149 (Help)
```

## 원인

에러 메시지에서 알 수 있는 것 처럼, `mem:testdb` 데이터베이스가 존재하지 않기 때문입니다. 미리 생성을 하거나 리모트 데이터베이스 생성을 허용하면 된다고 하면서, 두번째 옵션은 보안상 추천하지 않는다고 하네요.

External Libraries를 확인 해 보면

![image-20221115215421959](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/mem-testdb-not-found.assets/image-20221115215421959.png)

`h2:database:2.1.214` 버전이 들어와 있는 것을 확인 할 수 있습니다. 

그런데 `H2:1.4.198` 버전부터 보안상의 문제로 자동 데이터베이스 생성을 막아 두었는데요, 그렇게 때문에 DB 파일이 생성되지 않고 있기 때문에 이러한 문제가 발생 한 것 입니다.

만약 H2 콘솔에서의 데이터베이스 생성을 허용 한다면, `/h2-console` 페이지로 접속 할 수 있는 누구나 시스템에 접근해 원하는 무엇이든 할 수 있는 보안상 허점이 생기게 됩니다.

## 해결

### 데이터베이스 수동 생성

물론 H2 버전을 1.4.197 이하로 낮추는 방법으로 (보통 1.4.193로 많이 변경합니다) 문제를 해결 할 수 있지만, 위에서 언급 했던 것 처럼 찝찝한 보안상의 이슈가 생깁니다.

조금은 번거롭더라도 수동으로 데이터베이스 파일을 생성 해 주는 것이 좋은데요.

데이터 베이스 생성은 아래의 링크를 참고 하시면 됩니다.

> https://h2database.com/html/tutorial.html#creating_new_databases

하지만 이어서 소개해드릴 더 쉬운 방법이 있습니다.

### 더미 Entity 생성으로 DB 생성 유도하기

제가 주로 사용하는 방법인데요, JPA를 사용한다면 `ddl-auto` 기능을 활용해서 쉽게 해결 할 수 있습니다.

다들 아시는 것 처럼, DDL-AUTO 기능을 사용하면 스프링부트 어플리케이션이 실행되며 자동으로 필요한 테이블을 생성 하는데요, 그 과정에서 데이터베이스도 만들게 됩니다.

1. 아래와 같이 ddl-auto를 create-drop 으로 변경 하고 (후에는 제거하거나 최소 update로는 변경 하는걸 추천 합니다.)

```yaml
spring:
  application:
    name: user-service
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
  jpa:
    hibernate:
      ddl-auto: create-drop # 시작할 때 새로 생성 하고 세션을 마칠 때 스키마를 소멸 시킵니다.
    show-sql: true
  h2:
    console:
      enabled: true
      settings:
        web-allow-others: true
```

2. 엔티티 클래스를 하나 생성 해 줍니다.

뭐든 상관 없으니 JPA가 자동으로 테이블을 만들 수 있을 정도로만 해주면 충분 합니다.

```kotlin
@Entity
class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

}
```

3. 이제 준비가 되었으니 어플리케이션을 실행 해 줍니다.

![image-20221115222318054](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/mem-testdb-not-found.assets/image-20221115222318054.png)

> 위에 보이는 것 처럼, 어플리케이션이 실행 되며 테이블이 drop 및 create 됩니다.

4. 이제 h2-console 에서 `Test Connection` 이 문제 없이 가능합니다.

![image-20221115222703946](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/mem-testdb-not-found.assets/image-20221115222703946.png)

이상입니다. 
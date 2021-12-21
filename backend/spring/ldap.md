# LDAP으로 스프링 시큐리티 인증하기

> https://spring.io/guides/gs/authenticating-ldap/

## 시작하기전에

### 우리는 이걸 할꺼에요.

- Spring Security의 임베디드 자바기반의 LDAP 서버에 의해 보안되는 간단한 웹 어플리케이션
- 몇몇 유저 셋을 포함한 데이터 파일과 LDAP 서버 로드

### 무엇이 필요해요?

- 15분의 시간

- 좋아하는 IDE

  > 메모장도 상관 없어요!
  >
  > 하지만 STS나 IntelliJ IDEA가 있으면 더 쉽게 할 수 있어요.

- JDK 1.8 이상

- Gradle 4 이상 혹은 Maven 3.2 이상

### 어떻게 따라해요?

대부분의 다른 Spring 가이드 문서들 처럼 완전 기초부터 하나씩 기본적인 단계를 따라하며 할거에요. 대부분 이미 익숙한 개념들이니 걱정 할 필요 없어요.  

크게 두가지 방법이 있는데요. 어느 방법을 선택하건 결론적으로 작동하는 코드를 작성하실 거에요.

1. 완전 처음부터 하기 (Spring Initializr로 프로젝트 생성부터)
2. Git에서 ldap 프로젝트 clone 하기

> `git clone https://github.com/spring-guides/gs-authenticating-ldap.git`

## 프로젝트 생성

기초적인 프로젝트 생성부터 시작해 보겠습니다.

> LDAP 프로젝트를 생성하는 이유는 보안이 불안정한 웹 애플리케이션의 보안을 높이려고 하는 거잖아요. 그래서 일단 허점이 많은 웹 어플리케이션을 먼저 만들고, 글의 후반부에 Spring Security나 LDAP 기능을 위한 dependency 들을 붙일게요.

일단 Spring initilizr를 이용해 프로젝트를 생성 합니다. IDE를 이용하거나 https://start.spring.io/ 를 통해 생성해 주세요. 

![image-20211221221918211](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/ldap.assets/image-20211221221918211.png)



> 자바 11, Jar, Gradle을 선택 했는데, 뭐 크게 중요하진 않아요.

의존성은 Spring Web만 선택 해 주면 됩니다.

![image-20211221222118141](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/ldap.assets/image-20211221222118141.png)

> 저는 Lombok이 너무 좋아서 공식 가이드엔 없지만 몰래 하나 넣어보겠습니다..

다 했으면 Finish를 눌러 프로젝트를 생성합니다.



## 보안이 허술한 웹 어플리케이션

![image-20211221222324688](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/ldap.assets/image-20211221222324688.png)

일단 금방 프로젝트가 생성 되었습니다. xml로 한땀 한땀 설정 하던 때를 생각하면 스프링 부트는 정말 축복 입니다.

### 간단한 웹 컨트롤러 생성

스프링에서는 MVC 컨트롤러를 통해 REST 엔드포인트들을 만들 수 있습니다.

![image-20211221223109654](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/ldap.assets/image-20211221223109654.png)

HomeController.java

```java
package com.tistory.shanepark.ldap.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "Welcome to the home page!";

}

```

@RestController 어노테이션을 적어 줌으로서 아래의 두 어노테이션이 포함됩니다.

- @Controller: 컨트롤러라는 것을 알려주고 컴포넌트(@Component) 스캔 대상으로 지정해줍니다.
- @ResponseBody: view가 따로 없고, 브라우저에 메시지를 직접 전달합니다.

### 서버실행

그대로 서버를 실행 해 봅니다.

LdapApplication.java 파일을 실행 해 줍니다.

![image-20211221223833466](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/ldap.assets/image-20211221223833466.png)

http://localhost:8080

![image-20211221223905277](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/ldap.assets/image-20211221223905277.png)

해당 컨트롤러로 요청을 보내니, 미리 작성해둔 메시지를 응답합니다.

## 스프링 시큐리티

### 의존성 추가

Spring Security 설정을 위해 필요한 몇 의존성들을 추가 해 줍니다.

build.gradle에 아래의 내용들을 추가 해 줍니다.

```groovy
implementation("org.springframework.boot:spring-boot-starter-security")
implementation("org.springframework.ldap:spring-ldap-core")
implementation("org.springframework.security:spring-security-ldap")
implementation("com.unboundid:unboundid-ldapsdk")

```

Maven 이라면 pom.xml에 아래의 내용을 추가 해 주세요

```xml
<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
		<groupId>org.springframework.ldap</groupId>
		<artifactId>spring-ldap-core</artifactId>
</dependency>
<dependency>
		<groupId>org.springframework.security</groupId>
		<artifactId>spring-security-ldap</artifactId>
</dependency>
<dependency>
		<groupId>com.unboundid</groupId>
		<artifactId>unboundid-ldapsdk</artifactId>
</dependency>

```

위의 의존성들은 Spring Security와 unboundid, 그리고 open source LDAP 서버를 추가 해 줍니다. 

> 의존성을 추가 한 후에는 반드시 새로고침을 한번 해 주세요!

### WebSecurityConfig.java

이제 위에 있는 의존성들을 추가했으니, 순수한 자바를 이용한 보안 설정이 가능해 졌습니다.

```java
package com.example.authenticatingldap;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .authorizeRequests()
        .anyRequest().fullyAuthenticated()
        .and()
      .formLogin();
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
      .ldapAuthentication()
        .userDnPatterns("uid={0},ou=people")
        .groupSearchBase("ou=groups")
        .contextSource()
          .url("ldap://localhost:8389/dc=springframework,dc=org")
          .and()
        .passwordCompare()
          .passwordEncoder(new BCryptPasswordEncoder())
          .passwordAttribute("userPassword");
  }

}

```

보안 설정을 위해 `WebSecurityConfigurer`를 사용합니다. 위의 예제에서는 `WebSecurityConfigurerAdapter`에 있는 메서드들 구현함으로서 그걸 하고 있는데요, 

![image-20211221224843361](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/ldap.assets/image-20211221224843361.png)

코드를 타고 WebSecurityConfigurerAdapter 를 살펴 보면, WebSecurityConfigurer 를 구현 하고 있는걸 확인 할 수 있습니다.

### LDAP 서버

저희는 또 LDAP 서버가 필요합니다. Spring Boot는 순수 자바로 임베디드 서버의 자동 설정을 할 수 있는데요, 위의 코드에서 이미 사용 했습니다. 

![image-20211221225056414](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/ldap.assets/image-20211221225056414.png)

> .ldapAuthentication()

`ldapAuthentication()` 메서드가 그러한 것들을 해 주기 때문에, 로그인 폼에서의 유저네임을 `{0}` 에 대입하여 LDAP 서버에서 `uid={0},ou=people,dc=springframework,dc=org` 를 만족하는 데이터를 검색 합니다. 또한, `passwordCompare()` 메서드를 통해 비밀번호 속성의 이름과 인코더를 설정 합니다.

## 회원 데이터 등록

LDAP 서버는 LDIF(LDAP Data Interchange Format)  파일을 사용해 유저 데이터를 교환 할 수 있습니다. 

또한, LDIF 파일은 `application.properties` 내에 있는 `spring.ldap.embedded.ldif` 속성을 통해 Spring Boot에 데이터 파일을 등록 할 수 있습니다.

**src/main/resources/test-server.ldif**

```properties
dn: dc=springframework,dc=org
objectclass: top
objectclass: domain
objectclass: extensibleObject
dc: springframework

dn: ou=groups,dc=springframework,dc=org
objectclass: top
objectclass: organizationalUnit
ou: groups

dn: ou=subgroups,ou=groups,dc=springframework,dc=org
objectclass: top
objectclass: organizationalUnit
ou: subgroups

dn: ou=people,dc=springframework,dc=org
objectclass: top
objectclass: organizationalUnit
ou: people

dn: ou=space cadets,dc=springframework,dc=org
objectclass: top
objectclass: organizationalUnit
ou: space cadets

dn: ou=\"quoted people\",dc=springframework,dc=org
objectclass: top
objectclass: organizationalUnit
ou: "quoted people"

dn: ou=otherpeople,dc=springframework,dc=org
objectclass: top
objectclass: organizationalUnit
ou: otherpeople

dn: uid=ben,ou=people,dc=springframework,dc=org
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: Ben Alex
sn: Alex
uid: ben
userPassword: $2a$10$c6bSeWPhg06xB1lvmaWNNe4NROmZiSpYhlocU/98HNr2MhIOiSt36

dn: uid=bob,ou=people,dc=springframework,dc=org
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: Bob Hamilton
sn: Hamilton
uid: bob
userPassword: bobspassword

dn: uid=joe,ou=otherpeople,dc=springframework,dc=org
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: Joe Smeth
sn: Smeth
uid: joe
userPassword: joespassword

dn: cn=mouse\, jerry,ou=people,dc=springframework,dc=org
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: Mouse, Jerry
sn: Mouse
uid: jerry
userPassword: jerryspassword

dn: cn=slash/guy,ou=people,dc=springframework,dc=org
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: slash/guy
sn: Slash
uid: slashguy
userPassword: slashguyspassword

dn: cn=quote\"guy,ou=\"quoted people\",dc=springframework,dc=org
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: quote\"guy
sn: Quote
uid: quoteguy
userPassword: quoteguyspassword

dn: uid=space cadet,ou=space cadets,dc=springframework,dc=org
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: Space Cadet
sn: Cadet
uid: space cadet
userPassword: spacecadetspassword



dn: cn=developers,ou=groups,dc=springframework,dc=org
objectclass: top
objectclass: groupOfUniqueNames
cn: developers
ou: developer
uniqueMember: uid=ben,ou=people,dc=springframework,dc=org
uniqueMember: uid=bob,ou=people,dc=springframework,dc=org

dn: cn=managers,ou=groups,dc=springframework,dc=org
objectclass: top
objectclass: groupOfUniqueNames
cn: managers
ou: manager
uniqueMember: uid=ben,ou=people,dc=springframework,dc=org
uniqueMember: cn=mouse\, jerry,ou=people,dc=springframework,dc=org

dn: cn=submanagers,ou=subgroups,ou=groups,dc=springframework,dc=org
objectclass: top
objectclass: groupOfUniqueNames
cn: submanagers
ou: submanager
uniqueMember: uid=ben,ou=people,dc=springframework,dc=org
```

> `주의!` LDIF 파일은 프로덕션 시스템에서 사용하기엔 부적합합니다. 하지만 테스트 목적으로는 충분히 유용합니다.

## LDAP 확인

이제 http://localhost:8080 에 접속하면 스프링 시큐리티가 제공 하는 로그인 페이지로 연결 됩니다.

![image-20211221230056730](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/ldap.assets/image-20211221230056730.png)

올바르지 않은 로그인 데이터를 입력 하면

![image-20211221230217731](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/ldap.assets/image-20211221230217731.png)

> application.properties 설정하는걸 깜빡 해서 커넥션 거부가 되었습니다.

aplication.properties에 아래의 내용을 등록 합니다.

```properties
spring.ldap.embedded.ldif=classpath:test-server.ldif
spring.ldap.embedded.base-dn=dc=springframework,dc=org
spring.ldap.embedded.port=8389

```

이후 다시 시도해보면

![image-20211221230741500](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/ldap.assets/image-20211221230741500.png)

> 드디어 비밀번호가 틀렸다는 내용이 나옵니다.

이제 유저네임 `ben` 비밀번호 `benpassword`를 입력해 접속 하면..

![image-20211221231451444](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/ldap.assets/image-20211221231451444.png)

이제 정상적으로 로그인 처리를 하고 숨겨운 페이지를 보여 줍니다!

<br><br>

수고하셨습니다. Spring Security로 보안을 높인 웹 어플리케이션을 작성 하셨습니다. 

위의 전체 프로젝트 코드는 아래 링크에서 확인 하실 수 있습니다.

> https://github.com/Shane-Park/mdblog/tree/main/projects/ldap
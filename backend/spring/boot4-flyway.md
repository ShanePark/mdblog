# Spring Boot 4 마이그레이션 후 Flyway가 작동하지 않을 때

## Intro

Spring Boot 4로 마이그레이션한 뒤 Flyway가 조용히 작동하지 않는 문제가 있었다. 애플리케이션은 정상 실행되고 DB 조회도 문제없어서, 새로운 마이그레이션 SQL을 추가하기 전까지는 눈치채지 못했다. 원인은 Spring Boot 4의 모듈화 정책 변경이었다.

## 해결

Spring Boot 4부터는 `flyway-core` 의존성만으로는 자동 설정이 되지 않는다. 명시적으로 starter를 사용해야 한다.

```kotlin
// 변경 전 (Spring Boot 4에서 작동 안 함)
implementation("org.flywaydb:flyway-core")
implementation("org.flywaydb:flyway-mysql")

// 변경 후 (정상 작동)
implementation("org.springframework.boot:spring-boot-starter-flyway")
implementation("org.flywaydb:flyway-mysql")
```

Liquibase를 사용하는 경우에도 마찬가지로 `spring-boot-starter-liquibase`로 변경해야 한다.

### 원인

Spring Boot 4의 가장 큰 변화는 코드베이스의 모듈화다. 기존에는 auto-configuration이 하나의 큰 jar로 제공됐지만, 이제는 작고 집중된 모듈들로 분리되었다. 이로 인해 third-party 의존성만 추가하면 자동 설정이 됐던 것들이, 이제는 명시적으로 starter를 사용해야 하는 경우가 생겼다.

[마이그레이션 가이드](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide#module-dependencies)에도 이 내용이 나와 있다:

> For instance, if you are using Flyway or Liquibase you used to only have the relevant third-party dependency. You now need to replace that with spring-boot-starter-flyway or spring-boot-starter-liquibase, respectively.

문제는 Flyway가 작동하지 않아도 애플리케이션이 정상 실행된다는 점이다. 기존 테이블과 데이터는 그대로 있으니 조회도 잘 되고, 에러 로그도 없다. 새 마이그레이션 파일을 추가하고 그게 적용되지 않는 걸 확인하기 전까지는 알아채기 어렵다.

### Spring Boot 4 주요 변경점

- Spring Framework 7 기반, Jakarta EE 11 (Servlet 6.1) 요구
- Java 17~25 지원 (Java 25 first-class 지원)
- Jackson 3 마이그레이션 (`com.fasterxml.jackson` → `tools.jackson`)
- JSpecify null-safety annotations 적용
- `spring-boot-starter-web` → `spring-boot-starter-webmvc` 변경
- 테스트 인프라도 별도 starter 필요 (`spring-boot-starter-*-test`)

### 마이그레이션 경험

이번 마이그레이션은 Claude Code에게 [마이그레이션 가이드 문서](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)를 통째로 전달하고 맡겼다. 서브에이전트를 여러 개 생성하며 의존성 변경, 패키지 이동, deprecated API 대응 등을 스스로 처리했고, 테스트도 직접 돌려가며 확인했다. 전체 작업에 3~4시간 정도 걸렸는데, 이전에 1→2나 2→3 마이그레이션 때 며칠씩 걸렸던 것에 비하면 훨씬 수월했다.

다만 Flyway 문제처럼 조용히 실패하는 케이스는 AI가 잡아내지 못했다. 테스트가 통과하고 애플리케이션이 정상 실행되니 문제가 없다고 판단한 모양이다. 결국 이런 부분은 사람이 직접 확인해야 할 영역인 것 같다.

## 마치며

Spring Boot 4 마이그레이션 후 Flyway가 작동하지 않는다면, `flyway-core` 대신 `spring-boot-starter-flyway`를 사용하고 있는지 확인해보자.

**References**

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- [Spring Boot 4.0.0 Release](https://spring.io/blog/2025/11/20/spring-boot-4-0-0-available-now/)
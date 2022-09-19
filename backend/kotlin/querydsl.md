# [Kotlin] 코틀린에서 queryDSL 설정하기

## Intro

자바에서도 QClass 생성은 처음 하면 꽤나 당황스러운 과정인데, 코틀린은 또 과정이 달랐기에 결과물을 기록으로 남기고자 합니다.

2022년 9월 19일 기준의 최신 라이브러리들에서 잘 작동하고 있는 방법 입니다.

## 설정

**build.gradle.kts**

```kotlin
plugins {
	...
 	 kotlin("kapt") version "1.7.10"
}

dependencies {
  ...
    implementation("com.querydsl:querydsl-jpa:5.0.0")
    kapt("com.querydsl:querydsl-apt:5.0.0:jpa")
}
```

![image-20220919224119317](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/querydsl.assets/image-20220919224119317.png)

> 소스코드는 딱 위의 변경 사항만 있었습니다.

의존성 변경 후에 `Shift + Command + I` 키를 입력 해서 Load Gradle Changes를 해 주고 빌드를 해 줍니다.

```bash
./gradlew build
```

![image-20220919221840115](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/querydsl.assets/image-20220919221840115.png)

빌드가 정상적으로 이루어 졌다면, `build/generated/source/kapt/main` 디렉터리에 아래와 같이 Entity 들을 토대로 QClass 파일들이 생성 된 것을 확인 하실 수 있습니다.

![image-20220919221927308](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/querydsl.assets/image-20220919221927308.png)

> QClass 들이 정상적으로 추가 되었습니다.

그러고 나서 JpaQueryFactory 를 빈으로 등록 해주는 설정 파일을 하나 추가해주면 됩니다.

```kotlin
package kr.quidev.common.config

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Configuration
class QueryDslConfig {

    @PersistenceContext
    lateinit var em: EntityManager

    @Bean
    fun jpaQueryFactory(): JPAQueryFactory {
        return JPAQueryFactory(em)
    }
}

```

이후에는 편하게 `private val jpaQueryFactory: JPAQueryFactory` 를 주입 받아 사용 하시면 됩니다.

```kotlin
package kr.quidev.quiz.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.quidev.quiz.domain.entity.QQuiz
import kr.quidev.quiz.domain.entity.Quiz
import org.springframework.stereotype.Repository

@Repository
class QuizRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : QuizRepositoryCustom {

    override fun getList(pageSize: Long, page: Long): List<Quiz> {
        return jpaQueryFactory.selectFrom(QQuiz.quiz)
            .limit(pageSize)
            .offset(page * pageSize)
            .fetch()
    }
}
```

이상입니다. 

**References**

- https://github.com/querydsl/querydsl/issues/1828
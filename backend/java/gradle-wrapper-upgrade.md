# Gradle Wrapper 버전이 낮아서 JDK 21 지원을 안한다면

## Intro

프로젝트의 Spring boot 버전을 `3.2.5` 에서 `3.3.5`로 업데이트 하는 김에 사용하는 JDK도 17 에서 21로 업데이트 하려고 했다.

그런데 사용중인 Gradle 의 버전이 낮다 보니 바로 변경되지는 않았다.

코틀린 버전도 JDK 21을 지원하는 버전이 아니여서 업그레이드가 필요했는데, 전체적으로 겪었던 내용들을 정리해본다.

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/gradle-wrapper-upgrade.assets/1.webp)

> JAVA 21을 지원하지 않음.

## Gradle

JDK 18 까지만 지원을 하는 Gradle을 사용하고 있다.

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/gradle-wrapper-upgrade.assets/2.webp)

제일 먼저 gradle의 업그레이드가 필요했다.

지금 쓰는 버전을 확인 해 본다.

```bash
./gradlew --verson
```

 ![3](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/gradle-wrapper-upgrade.assets/3.webp)

확인 결과 굉장히 오래된 7.5 버전을 사용중이다.

아래의 명령어로 원하는 gradle 버전으로 업데이트 할 수 있다. 처음에는 아래와 같이 8.3으로 업데이트 했는데, 

```bash
./gradlew wrapper --gradle-version 8.3
```

![4](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/gradle-wrapper-upgrade.assets/4.webp)

> Gradle 업데이트 후에는 새로 Load 해주어야 한다.

업데이트 하고 보니

![5](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/gradle-wrapper-upgrade.assets/5.webp)

자바 21이 아직 인큐베이팅이다. 그래서 Gradle은 8.11로 업그레이드 했다.

```bash
./gradlew wrapper --gradle-version 8.11
```

## Kotlin

Kotlin 은 원래 `1.7.22` 버전을 쓰고 있었는데, `1.9.20`부터 JDK 21을 지원한다.

코틀린 버전도 거기에 맞춰준다.

```kotlin
  kotlin("jvm") version "1.9.20"
  kotlin("plugin.spring") version "1.9.20"
  kotlin("plugin.jpa") version "1.9.20"
```

코틀린 컴파일 옵션에서 jvmTarget도 변경해준다.

```kotlin
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "21"
    }
}
```

자바 버전도 명시해준다.

```kotlin
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
```

## Github Action

CI/CD 에도 영향을 주므로, Github action도 변경해줘야한다.

```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v3
        with:
          submodules: recursive
          token: ${{ secrets.PAT_TOKEN }}
      - name: Copy secret files
        run: |
          cp dutypark_secret/application-op.yml src/main/resources/
      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Build with Gradle
        uses: gradle/gradle-build-action@67421db6bd0bf253fb4bd25b31ebb98943c375e1
        with:
          arguments: build
      - name: Upload artifact
        uses: actions/upload-artifact@v4
        with:
          name: dutypark-ci
          path: build/libs/*.jar
```

크게 바꿀 건 없고 JDK 17 에서 21로만 변경 해 주었다.

`gradle-build-action`은 프로젝트의 Gradle Wrapper 버전을 따르기 때문에 따로 변경할건 없다.



생각보다 꽤 바꿀게 많았지만 문제 없이 잘 작동하는 것을 확인했다.

**References**

- https://docs.gradle.org/8.4/release-notes.html
- https://kotlinlang.org/docs/whatsnew1920.html
- https://docs.spring.io/spring-boot/appendix/dependency-versions/coordinates.html
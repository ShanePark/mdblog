# [Maven/Gradle] 의존중인 모든 라이브러리의 라이센스 정보 불러오기

## Intro

소프트웨어 개발을 하는 과정에서는 다양한 서드파티 라이브러리를 사용하게 된다. 이들 라이브러리는 우리가 직접 작성해야 할 코드의 양을 획기적으로 줄여주며 이미 검증된 라이브러리를 사용하면 코드의 안정성 향상에도 많은 도움을 준다.

하지만 각 라이브러리는 자체 라이센스 정책을 가지고 있으며, 개발자로서 이를 이해하고 준수하는 것은 법적이나 윤리적인 측면에서 매우 중요하다.

이번에 사용중인 모든 서드파티 라이센스 정보를 프로젝트에 기입해야 하는 일이 필요했다. 수동으로 관리하고 문서화 하는 것은 실수하기도 쉽고 시간도 많이 소요되기 때문에 자동화 할 수 있는 도구를 찾아보았고, 다행히도 제법 잘 만들어진 몇 도구들이 있어 어렵지 않게 라이센스 정보를 한번에 생성할 수 있었다. 

지금부터 이 글을 통해 Maven 및 Gradle 에서 프로젝트 라이센스 정보를 한데 모으는 방법을 자세히 알아보겠다.

## Maven

### maven-site-plugin

메이븐에서 사용할 수 있는 몇가지 도구를 찾아봤는데, 먼저 가장 기본적인 `maven-site-plugin`를 활용해보겠다.

`pom.xml`에 아래와 같이 maven-site-plugin을 추가 해준다.

**pom.xml**

```xml
  <build>
    <plugins>   
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-site-plugin</artifactId>
        <version>3.9.1</version>
      </plugin>
    </plugins>
  </build>
```

추가 한 후에는  `mvn site`를 실행해준다.

```bash
./mvn site
```

![image-20230719135557114](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/third-party-license.assets/1.webp)

> 부지런히 모든 dependency들을 다운로드 받아 확인 중

상당히 오랜 시간이 걸리기 때문에, 명령어를 실행 하고 다른 일을 하고 있는 게 좋다.

![image-20230719140057304](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/third-party-license.assets/2.webp)

> 무려 5분이 걸렸다.

이제 자동으로 생성된 Licenses 정보를 확인해보자.

![image-20230719140142576](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/third-party-license.assets/3.webp)

`./target/site/licenses.html` 파일을 먼저 확인해보자.

![image-20230719140302996](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/third-party-license.assets/4.webp)

해당 파일은 프로젝트 자체의 라이센스에 대한 내용이지, 디펜던시에 대한 내용이 아니라고 한다.

이번에는 `dependencies.html` 파일을 확인해본다.

![image-20230719140454044](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/third-party-license.assets/5.webp)

> 의존중인 라이브러리들와 각각의 라이센스 정보가 표기된다.

라이센스 정보를 한눈에 확인 할 수 있다.

### License Maven Plugin

이번에는 다른 플러그인을 사용해보자. 

라이센스 정보를 텍스트파일로 만들어 코드에 포함하는것이 목적이기 때문에 그에 더 적합한 라이브러리를 찾아 보았다.

**pom.xml**

```xml
  <build>
    <plugins>         
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>license-maven-plugin</artifactId>
        <version>2.2.0</version>
      </plugin>
    </plugins>
  </build>
```

아래의 명령어를 입력

```bash
./mvn license:add-third-party
```

![image-20230719141548105](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/third-party-license.assets/6.webp)

> 라이센스 정보가 없는 라이브러리에 대한 경고가 나오고, THIRD-PARTY.txt 라는 파일명으로 서브파티 정보를 기록 했다고 나온다.

해당 파일을 확인 해 보면

![image-20230719141718683](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/third-party-license.assets/7.webp)

텍스트 파일로 아주 완벽하게 정리되어 있다.

이번에는 target 폴더에 라이센스 정보 파일을 생성하지 않고, 코드 경로에 포함하여 커밋을 할 때 라이센스 정보도 함께 포함 되도록 해보자.

**pom.xml**

```xml
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>license-maven-plugin</artifactId>
        <version>2.2.0</version>
        <configuration>
          <thirdPartyFilename>LICENSES_THIRD_PARTY</thirdPartyFilename>
          <outputDirectory>${project.basedir}/</outputDirectory>
        </configuration>
      </plugin>
```

보이는 것 처럼, **configuration**에 파일명 및 경로를 지정해주면 된다.

이제 다시 `mvn license:add-third-party`를 실행 하면 설정한 경로에 라이센스 정보 파일이 생성된것을 확인 할 수 있다.

## Gradle

Gradle에서는 몇 가지 서로 다른 개발자의 `license-gradle-plugin` 이 있었는데, `hierynomus/license-gradle-plugin` 의 경우에는 마지막 커밋이 1년이 넘었고, 최신 Gradle에서 정상적으로 작동하지 않아 `jaredsburrows/gradle-license-plugin` 를 선택했다.

${code:build.gradle}

```groovy
plugins {
  id("com.jaredsburrows.license") version "0.9.3"
}
```

위에 있는 플러그인을 추가 하고

```bash
./gradlew licenseReport
```

라이센스 정보를 생성한다.

![image-20230719144943039](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/third-party-license.assets/8.webp)

CSV, HTML, JSON, Text 총 네가지 파일 타입으로 자동으로 생성준다.

아래와 같이 설정을 변경해주면 원하는 리포트만 생성하도록 할 수 있다. 

${code:build.gradle}

```groovy
licenseReport {
    generateCsvReport = false
    generateHtmlReport = false
    generateJsonReport = false
    generateTextReport = true
}
```

이번에는 위에서 메이븐 실습때 했던 것 처럼, 리포트 파일의 파일명도 원하는대로 변경하고, 소스코드에 포함되도록 `build.gradle`과 같은 경로에 리포트 파일이 생성되도록 해본다.

${code:build.gradle}

```groovy
task copyLicenseReport(type: Copy) {
    dependsOn 'licenseReport'

    from "$buildDir/reports/licenses"
    into "."

    rename 'licenseReport.txt', 'LICENSES_THIRD_PARTY'
}
```

위의 내용을 추가 한 후에, `./gradlew copyLicenseReport` 를 실행 해주면 된다.

<br><br>

이렇게 해서 의존 라이브러리에 대한 라이센스 정보를 효과적이고 간편하게 정리할 수 있었다. 

자동화 도구를 사용하여 보다 시간을 아끼고 보다 중요한 작업에 집중할 수 있도록 하자.

**References**

- https://maven.apache.org/plugins/maven-site-plugin/
- https://www.mojohaus.org/license-maven-plugin/
- https://github.com/jaredsburrows/gradle-license-plugin
# [Java] JaCoCo로 코드 커버리지 측정하기

## Intro

### Code Coverage

코드 커버리지는 작성한 소스코드중 얼마나 많은 부분이 자동화된 테스트에서 실질적으로 실행되었는지를 측정할 수 있는 지표 입니다. 

높은 코드 커버리지는 테스트가 실행되는 동안 소스코드의 더 많은 부분이 실제적으로 실행된것을 의미하기 때문에 보다 낮은 커버리지의 프로그램에 비해 버그가 발생할 확률이 더 낮다고 기대할 수 있습니다.

> 물론 높은 커버리지가 더 좋은 테스트임을 보장하는 것은 아닙니다.

특히 최근의 트랜드처럼 급한 템포의 개발이 이루어지는 상황에서도 빠르게 만들어낼 뿐만 아니라 신뢰수준이 높고 좋은 퀄리티의 코드를 작성하기 위해서는 정말 중요한 지표라고 생각됩니다.

진행중인 사이드 프로젝트에서 코드 커버리지를 측정하고 해당 지표를 조금씩 개선해보는 실습을 진행 해 보겠습니다.

### Coverage criteria

코드 커버리지 툴은 하나 이상 항목을 활용해 커버리지를 측정하는데요, 기본적으로는 아래와 같습니다.

- Function coverage: 선언된 함수들 중 얼마나 많은 항목이 호출되었는가
- State coverage: 프로그램에서 얼마나 많은 명령문이 실행 되었는가
- Branches(Edge) coverage: 조건문 중 얼마나 많은 분기들이 실행 되었는가
- Condition(Predicate) coverage: 각 불리언 부분식이 true와 false 모든 값으로 평가되었는가
- Line coverage: 얼마나 많은 소스코드 라인들이 테스트 되었는가

### Tools

코드 커버리지 측정에 앞서 가장 먼저 할 일은 프로젝트에 적절한 툴을 찾는 것 입니다. 

자바진영에서는 JaCoCo, Cobertura, Clover 등이 많이 쓰이고 있습니다. 이 글에서는 Jacoco를 활용하는 방법에 대해 다루어보려고 합니다. 

가장 많이 쓰여 레퍼런스가 풍부하기도 하고, 깃헙에서 비교 해 보았을 때 [Jacoco](https://github.com/jacoco/jacoco) 는 꾸준히 유지보수가 이루어지고 있는 반면 [Cobertura](https://github.com/cobertura/cobertura)는 지난몇년간 변화가 거의 없었습니다. Star/Fork 도 3배 이상 차이납니다. 

> Clover는 앞의 두가지와 다르게 Atlassian에서 개발한 **상용** 소프트웨어기 때문에 제외했습니다.

## Jacoco

### 적용

직접 코드에 적용 시켜보도록 하겠습니다. 본인이 가지고 있는 프로젝트 중 테스트코드가 한개라도 있고 규모가 작은곳에 먼저 적용해 보시면 되겠습니다.

**Maven**

**pom.xml**

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.2</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>prepare-package</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

jacoco.org 에서 제공하는 샘플 pom.xml 파일은 https://www.jacoco.org/jacoco/trunk/doc/examples/build/pom.xml 에서 참고 하실 수 있습니다.

**Gradle**

${code:build.gradle}

```groovy
plugins {
    id 'jacoco'
}

test {
    finalizedBy jacocoTestReport // 테스트 종료후 항상 리포트 생성
}
jacocoTestReport {
    dependsOn test // 리포트 생성을 위해서는 test가 먼저 완료되어야 함
}
```

### 측정

이제 테스트를 실행 해 봅니다. 저는 gradle을 사용 중이기 떄문에 gradle 명령으로 테스트를 실행해 보았습니다.

```bash
./gradlew test
```

![image-20230225142908256](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/jacoco.assets/image-20230225142908256.png)

그러면 테스트가 완료 되고나서, `build` 경로에 jacoco 폴더가 생긴게 확인됩니다. jacoco 폴더에는 테스트 결과가 바이너리 포맷으로 `test.exec` 파일에 저장되어 있습니다. 

바이너리 포맷은 저희가 눈으로 확인을 할 수 없기 때문에 Sonar Qube 처럼 해석을 해주는 툴이나 플러그인이 필요한데요.

마침 확인해보면 `reports/jacoco` 경로도 생성된게 확인됩니다. 그 안에 생성된 index.html 파일을 열어 보면 

![image-20230225143158941](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/jacoco.assets/image-20230225143158941.png)

측정된 코드 커버리지가 한눈에 알아보기 쉽게 표기되어 있습니다. 이제 클릭을 하고 계속 들어가면

![image-20230225143711594](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/jacoco.assets/image-20230225143711594.png)

> Element 를 클릭 하면 좀 더 상세 확인이 가능 합니다.

![image-20230225143748377](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/jacoco.assets/image-20230225143748377.png)

커버리지내에 들어오지 못한 라인에 대해 자세한 확인을 할 수 있습니다.

### IntelliJ IDEA

인텔리제이에는 기본적으로 코드 커버리지 측정을 위한 플러그인이 설치되어 있습니다.

![image-20230225143904342](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/jacoco.assets/image-20230225143904342.png)

그럼 Coverage 를 포함해 테스트를 돌려 보겠습니다.

![image-20230225145555540](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/jacoco.assets/image-20230225145555540.png)

실행을 하면..

![image-20230225145823158](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/jacoco.assets/image-20230225145823158.png)

> 마찬가지로 코드 커버리지가 보기 쉽게 표기 됩니다.

하지만, Jacoco에서는 `Ps` 코드가 99% 커버리지로 나왔었는데 인텔리제이 커버리지 플러그인은 100%로 잡아주네요.

Jacoco는 측정하는 Branch Coverage를 인텔리제이의 커버리지모듈은 제공하지 않기 때문입니다.

IntelliJ에서의 테스트 커버리지 측정툴을 Jacoco로 변경 해 보겠습니다. 

`Run > Edit Configuration`에 들어 가서 Modify options 를 확인 해 보면 

![image-20230225144912584](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/jacoco.assets/image-20230225144912584.png)

> Specify alternative coverage runner 가 보입니다.

![image-20230225150105143](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/jacoco.assets/image-20230225150105143.png)

이걸 JaCoCo로 변경 해서 다시 실행을 해 보았습니다.

![image-20230225150858508](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/jacoco.assets/image-20230225150858508.png)

> 테스트에 실패

Jacoco로 바꾸었는데 테스트 실행에 실패합니다. 여기서 문제없이 실행 된 분도 있을 거에요. 

여러가지 이유를 찾아 봤는데 이런경우 자바 호환이 문제인 경우가 대부분이더라고요. 일단 JaCoCo의 릴리즈노트를 확인 하며 각 버전별로 지원하는 JAVA 버전을 확인 해 보았습니다.

![image-20230225154237432](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/jacoco.assets/image-20230225154237432.png)

> https://www.jacoco.org/jacoco/trunk/doc/changes.html

지금 gradle에서 따로 버전명시를 하지 않아서 0.8.7 버전으로 들어가있는데 여기에서는 Java 17 정식 지원은 0.8.8 부터네요.

제 gradle 버전 정보를 먼저 확인 해 보겠습니다.

![image-20230225155834526](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/jacoco.assets/image-20230225155834526.png)

> Gradle 버전은 8.0.1이고, JVM 버전은 17.0.2 입니다.

일단 먼저 jacoco 버전을 자바17까지 정식 지원하는 0.8.8로 올려보겠습니다.

${code:build.gradle}

```groovy
plugins {
    id 'java'
    id 'maven-publish'
    id 'jacoco'
}

jacoco {
    toolVersion = "0.8.8"
}

```

여기서 해결이 될 수도 있지만, JVM를 좀 가리는지, 아님 아키텍처가 aarch64 라서 그런지 바로 해결되지는 않더라고요.

그래서 `Preference > Build, Excution, Deployment > Build Tools > Gradle` 에서 Gradle의 JVM을 이것저것 바꿔보며 테스트하니

![image-20230225161654833](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/jacoco.assets/image-20230225161654833.png)

제가 가지고 있는 JDK 중에서는 Zulu JDK1.8를 선택했을때만 Test with Coverage 가 문제없이 실행 되었습니다. 일반 Gradle Task에서는 문제 없는걸로 봐선, JaCoCo나 Gradle자체의 문제보다 인텔리제이까지 함께 얽히다보니 호환 문제가 좀 있는 것 같습니다.

![image-20230225161818781](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/jacoco.assets/image-20230225161818781.png)

> JaCoCo로 테스트를 하면 Class, Method, Line, Branch 를 확인합니다. Branch Coverage를 측정하기 위해서는 기본 모듈대신 JaCoCo를 써야 합니다.

인텔리제이에서는 아래사진처럼 코드 커버리지에서 지적받은 라인은 초록색이 아닌 다른 색으로 체크가 되기 때문에, 이를 참고하여 테스트를 추가해 주면 됩니다. 테스트가 불필요한 클래스라면 분석 대상에서 제외할 수도 있습니다.

![image-20230225173423190](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/jacoco.assets/image-20230225173423190.png)

성공하는 케이스만 테스트했기 때문에, 실패 케이스의 분기는 타지를 못했습니다. 

실패 케이스까지 작성을 해 줘야 Ps 의 Branch coverage를 100%로 만들 수 있습니다. 바로 추가로 작성해서 커버리지를 끌어 올려 주었습니다.

![image-20230225221815823](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/jacoco.assets/image-20230225221815823.png)

> 인텔리제이에서는 Project 탐색기에도 각 클래스별 커버리지를 표시해주기 때문에 정말 편리합니다. 

부족한 테스트 정보들을 참고 하여 조금씩 테스트 케이스를 수정해줍니다. 또 좋은것은, 이렇게 하다보면 코드에서 쓸데없는 부분들이 발견되어 깔끔하게 리팩터링이 되기도 합니다.

![image-20230226003732889](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/jacoco.assets/image-20230226003732889.png)

>  JaCoCo 덕분에 조금씩 추가 및 리팩터링을 하다 보니 금새 코드 커버러지 100%를 달성 할 수 있었습니다.

## Next.. 

원래는 한번에 다루려고 했었는데, 글이 워낙에 길어지다 보니 Github Action을 활용해 Coverage Badge를 붙이는 방법에 대해서는 다음 글 [Github Action으로 코드 커버리지 뱃지 생성하기](https://shanepark.tistory.com/457) 에 작성했습니다. 혹시 관심이 있다면 확인 해 주세요.

이상입니다.

**References**

- https://www.atlassian.com/continuous-delivery/software-testing/code-coverage#:~:text=Code%20coverage%20is%20a%20metric,get%20started%20with%20your%20projects.
- https://en.wikipedia.org/wiki/Java_code_coverage_tools
- https://en.wikipedia.org/wiki/Code_coverage
- https://www.baeldung.com/jacoco
- https://medium.com/capital-one-tech/improve-java-code-with-unit-tests-and-jacoco-b342643736ed
- https://docs.gradle.org/current/userguide/jacoco_plugin.html
- https://www.jetbrains.com/help/idea/code-coverage.html
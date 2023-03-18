# Gradle-Kotlin 멀티모듈 생성하기

## Intro

프로젝트를 진행하다보면 `분리`에 대한 고민이 드는 시점이 종종 있습니다. 

당장 필요해서 열심히 구현하고보니 다른 프로젝트에서도 자주 쓰일 것 같을 때도 있고, 다른프로젝트에서 여기에 있는 기능을 가져다 쓰려고 하는데 전부 포함시키기에는 불필요한 의존성이 너무 많을 때도 있습니다.

가장 쉬운 방법은 Copy And Paste~~(CNP방법론)~~ 이 있겠지만, 그렇게 복사해낸 코드에 변경사항이라도 필요하면 일일히 복사된곳을 찾아가서 하나하나 고쳐줘야 하는 번거로움이 있습니다. DRY(Don't Repeat Yourself) 원칙을 위배하면 나중에 수습하기가 참 고생입니다.

특히, 서로간 통신이 빈번해 동일한 도메인을 공유해야하는 프로젝트간에는 이 `공통된 객체`간의 동기화가 보장되는게 정말 중요합니다. 복사 붙여넣기만으로 버티기에는 외줄타기가 될 뿐더러 결코 오래 지속될수가 없습니다.

사실 각 모듈간의 분리에 대해서는 고민이 더 필요합니다. 진행중인 프로젝트에서도 깊은 생각없이 

> 이 의존성은 여러 프로젝트에서 쓸것 같으니깐 코어모듈에 넣으면 되겠네!

라고 생각하고 계속 코어모듈을 비대하게 키웠다가 나중에 의존성 정리를 하느라 참 고생을 하기도 했었습니다. 본 글에서는 모듈을 분리하는 방법에 대해서는 다루지 않고 단순히 코틀린으로 멀티모듈을 세팅하는 방법에 대해 작성해보려고 합니다.

모듈 분리에 대한 노하우는 용근님의 [멀티 모듈 설계 이야기](https://techblog.woowahan.com/2637/) 를 읽어보시면 많은 도움이 될 것 같습니다.

## 프로젝트 생성

### IntelliJ IDEA

처음에는 IntelliJ IDEA로 Gradle 프로젝트를 생성하려고 했습니다. 그런데 문제가 있어서 gradle 명령어로 새로 만들게 되었습니다.

일단 인텔리제이로 만들려고 했던 부분은 눈으로만 확인 해 주세요. 

![image-20230316220731387](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230316220731387.png)

> root로 사용할 프로젝트 명을 입력해줍니다.

Create 를 클릭해서 root 프로젝트를 생성 해 줍니다. 

![image-20230316221501058](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230316221501058.png)

그러면 위와 같이 첫번째 Gradle 프로젝트가 생성 됩니다.

이번에는 첫번째 모듈을 추가해봅니다.

![image-20230316222657206](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230316222657206.png)

우클릭 후 `New` - `Module`... 

![image-20230316224416026](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230316224416026.png)

그런데 Kotlin이 보이지 않습니다. Java, Groovy, JavaScript .. 심지어 Go랑 Ruby도 있는데 말이죠.

- https://youtrack.jetbrains.com/issue/IDEA-296699/New-module-wizard-There-is-no-option-to-create-Kotlin-module

- https://youtrack.jetbrains.com/issue/KTIJ-20957/Decide-whether-to-fix-or-disable-New-module-functionality-for-new-project-wizard-for-Kotlin

찾아보니 유트랙에 이미 이슈로 등록 되어 있었는데, 뭔가 문제가 많아서 비활성화해 두었다고 합니다.

여기에서 평범한 디렉터리를 추가 하여 설정파일을 일일히 추가하며 하는 방법이 있지만 갑자기 그렇게 넘어가면 익숙하지가 않은 경우 당황스러울 수가 있습니다. 그래서 Gradle이 제공하는 멀티모듈 예제를 통해 구조를 파악하고 나서 수동으로 추가해보려 합니다.

### Gradle init

먼저 기존에 만든건 제거해줍니다. 프로젝트 루트 폴더를 새로 생성 하고 이동합니다.

```bash
mkdir table-parser
cd table-parser
```

![image-20230316231911260](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230316231911260.png)

Gradle 프로젝트를 init 합니다. 혹시 설치된 gradle이 없다면 먼저 설치 해 주셔야 하는데요, brew로 설치 해도 되고 `brew install gradle` sdkman이 있다면 sdkman으로 설치 해도 `sdk install gradle` 좋습니다.

```bash
gradle init
```

![image-20230316231926992](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230316231926992.png)

> 처음에 project type을 선택 하는데, application을 선택 합니다.

![image-20230316231958116](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230316231958116.png)

언어는 당연히 Kotlin을 선택 해 줍니다.

그 다음으로, multiple subprojects? 라며 기능별로 여러개의 서브프로젝트로 나눌거냐고 물어보는데요.

멀티모듈 프로젝트를 생성할 계획이니  2번 `Yes`를 선택해줍니다.

![image-20230316230708717](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230316230708717.png)

- 빌드스크립트는 원하는걸 선택하면 됩니다. Groovy가 좀 더 익숙할 수 있지만 이왕 코틀린 프로젝트를 하는거 Kotlin으로 선택했습니다.
- new API 를 사용할거냐고 하는데, 기본값이 no기 때문에 그냥 엔터를 치면 no로 선택됩니다. 다음 릴리즈에 바뀔 가능성이 있다고 합니다.

![image-20230318141034001](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230318141034001.png)

마지막으로 프로젝트 이름과 패키지를 입력 하면 생성이 완료 됩니다.

## 프로젝트 분석

생성된 프로젝트를 3레벨 까지 펼쳐 보면 아래와 같습니다.

root 프로젝트 하위에 app, list, utilities 라는 세개의 모듈이 있습니다.

![image-20230316232240911](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230316232240911.png)

이제 인텔리제이로 프로젝트를 띄워 봅니다. 

간단하게 터미널에 아래 명령어만 입력 해도 인텔리제이에서 해당 프로젝트를 열어 줍니다.

```bash
idea .
```

기본적인 구조가 아래와 같이 보입니다. 

![image-20230316232543285](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230316232543285.png)

> Gradle이 알아서 다 해주니 정말 편하네요.

`settings.gradle.kts` 파일을 보면, include에 app, list, utilities 가 되어 있고 각각의 이름의 모듈이 위치해 있습니다.

![image-20230316232831061](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230316232831061.png)

> utilities 는 list에 의존하고 있습니다.

![image-20230316232902751](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230316232902751.png)

> app은 utilities에 의존하고 있습니다.

서브 프로젝트를 테스트 하고 어플리케이션 실행 까지 해 봅니다.

```bash
./gradlew check
./gradlew run
```

![image-20230316233030419](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230316233030419.png)

> Hello World!

어플리케이션 빌드도 해봅니다.

```bash
./gradlew build
```

![image-20230316233329571](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230316233329571.png)

`app.tar` 파일이 생성되었습니다.  ` tar xvf app.tar` 명령어로 압축을 풀어서 확인 해 봅니다.

![image-20230316233458393](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230316233458393.png)

lib 안에 list와 utilities 모두 포함되어 있습니다. 대강 어떤식으로 구성해야할지 충분한 힌트가 되었습니다.

`gradle projects` 명령어로 전체적인 프로젝트 구조를 확인할 수도 있습니다.

```bash
gradle -q projects
```

![image-20230318132039701](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230318132039701.png)

## 모듈 추가

### 추가

이제 새로운 모듈을 추가해봅니다. 아까 인텔리제이로 모듈을 만들려고 하다 실패했으니 모듈 역시 수동으로 만들어 보겠습니다.

저는 커맨드라인으로 입력 했지만, 인텔리제이상에서 디렉터리 추가, 파일 추가를 이용하여도 상관 없습니다.

먼저 루트 디렉터리 하위에 모듈로 사용할 폴더를 하나 추가 합니다. 그리고는 그 폴더로 이동합니다.

```bash
mkdir table-parser-core
cd table-parser-core
```

이제 gradle 명령어를 이용해 새로 프로젝트를 생성하고 불필요한 파일들을 삭제하고 build.gradle.kts 파일을 수정해서 모듈로 만드는 방법도 있지만 그냥 처음부터 만드는 방법이 훨씬 간단합니다.

```bash
touch build.gradle.kts
```

이제 루트 프로젝트에 포함을 시켜주면 되겠습니다.

루트 프로젝트의 `settings.gradle.kts` 에서 include에 새로 생성한 프로젝트를 추가 해줍니다

![image-20230318133423250](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230318133423250.png)

> 가장 우측에 "table-parser-core"를 추가 한 상태

처음 폴더만 추가한 상태에서는 `table-parser-core`폴더에 파랑색 네모 표시가 없었는데, include 시키고 나니 추가된것이 보입니다.

이번에는 `build.gradle.kts (:app)` app 모듈에도 방금 추가한 코어 모듈을 추가 해 줍니다. 

![image-20230318133506638](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230318133506638.png)

그리고 새로 생성한 `build.gradle.kts (:table-parser-core)` 에는 buildSrc에 있는 kotlin-common-conventions 플러그인을 적용 해 줍니다. kotlin-common-conventions 는 아래와 같이 작성되어 있습니다.

![image-20230318145333143](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230318145333143.png)

> kotlin-common-conventions 

이걸 등록 해주면 되는데요. table-parser-core 의 build.gradle.kts 입니다.

$(code:build.gradle.kts)

```kotlin
plugins {
    id("io.github.shanepark.tableparser.kotlin-common-conventions")
}

```

![image-20230318145424876](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230318145424876.png)

정말 간단한 상태이지만, 이제 필요한 소스/테스트/리소스 폴더들을 알아서 인식해 줍니다. 

buildSrc/src/main/kotlin에 보면 보이는것처럼 세개의 설정 파일이 등록되어 있습니다.

![image-20230318145518739](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230318145518739.png)

> 각각 용도별로 필요한 공통 설정을 담아둘 수 있는 구조
>
> 만약 라이브러리 용도인데, application-conventions 를 추가해준다면, 메인클래스를 찾지 못해 에러가 나오겠죠?

한번 적절한 경로에 해당 폴더들을 생성 해 보겠습니다. 

![image-20230318141600668](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230318141600668.png)

모듈에서 새로만들기로 Directory를 선택 해 줍니다.

![image-20230318141822450](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230318141822450.png)

그러면 위에 보이는 것 처럼 Gradle Source Sets 라면서 필요한 목록이 쭉 나옵니다. 필요한걸 모두 선택해서 생성 해 줍니다.

![image-20230318140019705](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230318140019705.png)

> 소스 폴더 및 테스트, 리소스 폴더가 잘 인식 되고 있습니다.

### 테스트

table-parser-core 모듈이 정상적으로 추가되었는지를 테스트 해 보도록 하겠습니다.

table-parser-core에 CoreClass 라는 클래스를 생성 합니다.

![image-20230318142420427](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230318142420427.png)

이 클래스를 app 모듈에서 사용해보도록 하겠습니다.

![image-20230318142456174](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230318142456174.png)

> app 모듈에서 아무런 문제 없이 CoreClass 클래스를 사용할 수 있는 상태

이제 app을 실행 해 보면..

![image-20230318145632614](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/gradle/gradle-kotlin-multimodule.assets/image-20230318145632614.png)

정상적으로 추가한 코드가 잘 실행되는 것을 확인 할 수 있습니다!

이제 이 구조를 바탕으로 어플리케이션을 필요한 모듈 단위로 나누고 적절하게 설계하는 훈련을 하면 되겠습니다.

## 마치며

지금까지 그레이들-코틀린 에서 멀티모듈을 만드는 방법에 대해 알아보았습니다. 사실 이미 해본 개발자들은 그거 별거 없고 그냥 만들면 되지 않겠냐며 쉽게 이야기하실 수 있겠지만 한번도 해본 적 없는 입장에서는 어디서부터 어떻게 시작해야할지도 막막합니다.

그래서 gradle이 제공해주는 기본 멀티모듈 프로젝트를 통해 학습을 해 보았습니다. 위의 구조를 잘 파악하면 기존의 싱글모듈 프로젝트도 멀티 모듈 프로젝트로 금새 변환하실 수 있을거라 생각됩니다.

시작은 했으니 지금부터는 훌륭한 코드베이스를 보면서 감을 익히는게 좋겠는데요

- [스프링에서 제공하는 멀티모듈 예제](https://github.com/spring-guides/gs-multi-module/tree/main/complete)

- 얼마전에 제미니님이 공개하신 [spring-boot-kotlin-template](https://github.com/team-dodn/spring-boot-kotlin-template) 

이렇게 두가지를 추천해드리겠습니다. 이상입니다.

**References**

- https://techblog.woowahan.com/2637/
- https://spring.io/guides/gs/multi-module/
- https://github.com/spring-guides/gs-multi-module
- https://docs.gradle.org/current/userguide/multi_project_builds.html
- https://docs.gradle.org/current/samples/sample_building_kotlin_applications_multi_project.html
# Gradle 프로젝트 maven cental repository 에 배포하기

## Intro

제가 코딩테스트 문제풀이를 할 때, 테스트코드 작성을 보다 쉽고 간단하게 하려고 만들어둔 라이브러리가 있습니다. 항상 반복적으로 작성하던 부분들도 시간이 아까워서 조금씩 기능을 넣다 보니 나름 leetcode 문제 풀이 할 때 도움이 많이 됩니다.

혼자만 오랫동안 사용해왔었는데, 주변에 마찬가지로 코딩문제 풀이를 하지만 테스트를 너무 번거롭고 어렵다는 이유로 아예 작성하지 않는 친구들에게 소개해주었습니다.

어떨지 몰랐는데 써보더니 너무 좋다며 유용하게 사용하고 테스트도 열심히 작성하길래 다른 관심있는 분들도 쉽게 사용할 수 있도록 하기 위해 메이븐 central에 배포하기로 결정을 했습니다.

이전에는 [jitpack](https://shanepark.tistory.com/227) 으로만 배포했었는데.. 이번에 작업해보며 난이도 차이가 너무 커서 당황스러웠습니다만 어찌어찌 성공 했기에 그 방법에 대해 나누어보려고 합니다.

## JIRA 계정 생성 및 이슈 등록

### 요구사항

https://central.sonatype.org/publish/requirements/

Central Repository에서 사용 가능한 컴포넌트들의 최소한의 퀄리티 수준을 보장하기 위해서 몇가지 요구사항이 정의되어 있습니다.

위의 링크를 참고하여 요구사항에 맞춰 정보를 수정 혹은 추가해야 합니다. 제법 조건이 많은데 전부 맞추기엔 이것저것 너무 많아서 일단 신청 후 반려되면 정보를 업데이트 하는식으로 전략을 세웠습니다. 체크섬 정보도 제공해야하고 scm, 개발자 정보, 라이센스, 기타 등등 굉장히 많습니다. 어쨌든 나중에 배포할때 결국 다 입력하게 되긴 합니다.

### 회원가입

아래의 링크에서 회원 가입이 가능합니다.

https://issues.sonatype.org/secure/Signup!default.jspa

가입 완료 후에는 바로 무엇을 할 건지 물어보는데요, Create an issue 를 선택 합니다.

![image-20230301161516845](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/publish-maven-central.assets/image-20230301161516845.png)

> Create an issue

Project와 Issue Type은 아래와 같이 설정합니다.

![image-20230301161610777](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/publish-maven-central.assets/image-20230301161610777.png)

> Project: Community Support - OSPRH
>
> Issue Type: New Project

이제 내용을 입력하는데요, 예제 이슈 https://issues.sonatype.org/browse/OSSRH-63492 를 참고하여 작성해 주었습니다.

참고로 저는 아래와 같이 입력했다가 한번 반려를 당했으니 그룹아이디를 `com.github.*` 으로 입력 하려고 했던 분들은 미리 `io.github.*`으로 변경해서 입력해주세요.

![image-20230301170606720](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/publish-maven-central.assets/image-20230301170606720.png)

Create를 클릭해서 마무리 해 줍니다.

![image-20230301170908394](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/publish-maven-central.assets/image-20230301170908394.png)

> 이슈가 등록 된 모습

이제 Bot이 자동으로 체크를 하고 승인을 금방 해 줍니다.

### 반려 해결

![image-20230301171332633](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/publish-maven-central.assets/image-20230301171332633.png)

올리자 마자 Bot이 답글을 달아줍니다. `com.github` 으로 시작하는 그룹아이디는 사용 할 수 없다고 합니다. group id를 모두 io.github으로 시작하도록 변경 해 주었습니다.

그리고, 소유권 확인을 위해 `OSSRH-89300` 이라는 저장소도 생성하라고 합니다. 시키는대로 합니다.

#### 저장소 생성

![image-20230301171813257](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/publish-maven-central.assets/image-20230301171813257.png)

> 소유권 확인용 저장소 생성

#### GroupId 변경

${code:build.gradle}

```groovy
group = 'io.github.shanepark'
version = '1.1.0'
description = 'PSH: Problem Solving Helper'
```

그룹 아이디는 `build.gradle`에 있는 것과 신청서에 작성하는것을 모두 변경합니다.

![image-20230301172354173](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/publish-maven-central.assets/image-20230301172354173.png)

#### 재신청

편집이 완료되었으면 Status를 Waiting for Response에서 Open으로 변경해줍니다.

![image-20230301172627771](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/publish-maven-central.assets/image-20230301172627771.png)

이후 다음엔 어떤게 또 문제가 될지 조금 기다려 보았습니다.. 1~2분만 기다리면

![image-20230301173640300](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/publish-maven-central.assets/image-20230301173640300.jpeg)

Requirements를 충족 안하게 많아서 반려될거라고 생각했는데 금방 승인되었습니다.

## GPG 키 생성 및 배포

Central Repository에 artifact를 배포하기 위해 꼭 필요한 또 한가지가 바로 GPG 키 입니다. 

https://central.sonatype.org/publish/requirements/gpg/ 를 참고해서 진행해 줍니다.

### 키 생성

맥북에서는 아래의 명령어로 GnuPG 를 설치할 수 있습니다.

```bash
brew install gpg
gpg --version # GnuPG 2.4.0
```

![image-20230301180036295](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/publish-maven-central.assets/image-20230301180036295.png)

gpg가 준비 되었다면 Key Pair를 생성해 줍니다.

```bash
gpg --gen-key
```

이름, 이메일주소를 입력 해 달라고 합니다. 이후 passphrase(암호)도 두번 입력해줍니다.

키를 생성하면 기본 유효기간은 2년이고, 만료가 되면 키와 passphrase를 통해 연장이 가능하다고 합니다.

![image-20230301180513854](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/publish-maven-central.assets/image-20230301180513854.png)

> 키가 생성되었습니다.

언제든 키를 확인 하고 싶으면, 아래의 명령어를 입력 하면 됩니다.

```bash
gpg --list-keys
```

![image-20230301180754401](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/publish-maven-central.assets/image-20230301180754401.png)

위에서 보이는 `/Users/shane/.gnupg/pubring.kbx` 가 공개 keyring 파일의 경로입니다. 

그리고 또 키 아이디 `7E0442B198431D937C078B7047787A9F2F311C65` 가 확인이 되는데요, keyid 의 마지막 8개 캐릭터 `2F311C65`는 shortID가 불리는데 이걸 대신 사용할 수 도 있습니다. 생성일 `2023-03-01`도 확인이 되네요. 

### 키 배포

이제 생성한 공개키를 배포 해 줍니다. 위에서 확인된 키 ID를 마지막에 넣어주면 됩니다. shortID를 써도 됩니다.

Central Maven은 지금 아래의 세가지 GPG 키서버를 지원합니다. 

- keyserver.ubuntu.com
- keys.opengpg.org
- pgp.mit.edu

sonatype의 예제가 우분투 키서버로 되어 있어서 그대로 진행 해 보겠습니다.

```bash
gpg --keyserver keyserver.ubuntu.com --send-keys 7E0442B198431D937C078B7047787A9F2F311C65
```

![image-20230301181630617](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/publish-maven-central.assets/image-20230301181630617.png)

## Gralde 수정

https://central.sonatype.org/publish/publish-gradle/

이제 Gradle로 OSSRH에 배포 하는 일만 남았습니다만 개인적으로 정말 힘들었습니다. 

위의 튜토리얼을 그대로 따라 하다가 문제에 많이 부딪혔는데 [망나니개발자](https://mangkyu.tistory.com/237) 님의 글이 큰 도움이 되었습니다.

### Metadata와 Signing

`s01.oss.sonatype.org`에 릴리즈하는게 승인이 났다고 해도, OSSRH에 배포하기 위해서는 `pom.xml`에 아까의 모든 요구사항을 만족시켜야 하고, 서명 과정도 필요합니다.

Gralde의 maven 플러그인을 활용하면 메타데이터를 관리할 수 있고, 필요한  `pom.xml`파일도 생성해 줍니다.

${code:build.gradle}

```groovy
apply plugin: 'maven-publish'
apply plugin: 'signing'
```

### Jar file

javadoc과 source도 반드시 제공해야 합니다.

${code:build.gradle}

```groovy
task javadocJar(type: Jar) {
    classifier = 'javadoc'
    from javadoc
}

task sourcesJar(type: Jar) {
    classifier = 'sources'
    from sourceSets.main.allSource
}

artifacts {
    archives javadocJar, sourcesJar
}
```

### Signing Artifacts

${code:build.gradle}

```groovy
signing {
    sign configurations.archives
}
```

### secretKeyRingFile 생성

그리고 또 secretKeyRingFile이 필요한데요. signing.pgp 파일을 원하는 위치에 생성 해 줍니다.

```bash
gpg --export-secret-keys 2F311C65 > ~/Documents/dev/keystore/signing.pgp
```

![image-20230301191654443](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/publish-maven-central.assets/image-20230301191654443.png)

> 비밀번호 입력이 필요합니다.

아티팩트서명을 위해, Credentials를 로컬 `gradle.properties` 파일에 만들어 주라고 합니다.

`GRADLE_USER_HOME`에 위치하면 되는데, 따로 지정하지 않았다면 `USER_HOME/.gradle`에 넣으면 된다고 하는데, 

```bash
vi ~/.gradle/gradle.properties
```

${code:gradle.properties}

```properties
signing.keyId=2F311C65
signing.password=키비밀번호입력
signing.secretKeyRingFile=/Users/shane/Documents/dev/keystore/signing.pgp
ossrhUsername=지라-아이디-입력
ossrhPassword=지라-비밀번호-입력
```

저는 개인적으로는 굳이 여러개의 아티팩트를 배포할게 아니라면 안드로이드에서 하는것처럼 local.properties 파일을 추가해서 ignore 등록해두고 하는것도 좋은 것 같습니다. 

이정도 해보고 서명한 Artifact가 빌드 되는지도 확인 해 보았습니다.

```bash
 ./gradlew sign 
```

![image-20230301192159416](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/publish-maven-central.assets/image-20230301192159416.png)

### StagingProfile 확인

sonatype에 배포하는 과정에서 StagingProfile이 필요한데요. https://s01.oss.sonatype.org/ 에 접속 해서

![image-20230301223916539](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/publish-maven-central.assets/image-20230301223916539.png)

좌측 Staging Profiles 메뉴에 들어가면 주소창에 세미콜론 이후 profile id가 보입니다. 저는 28로 시작한게 아이디입니다. 기록해둡니다.

이제 필요한 gradle 설정을 다 넣고 잘 작동하게 해줘야하는데. 이게 개인적으로 너무 힘들었습니다. 제 gradle 설정 전문을 올릴테니 참고해서 진행해보세요

첫번째로는 build.gradle 파일입니다. 

${code:build.gradle}

```groovy
buildscript {
    dependencies {
        classpath 'io.github.gradle-nexus:publish-plugin:1.2.0'
    }
}

plugins {
    id 'java'
    id 'jacoco'
}

apply plugin: 'io.github.gradle-nexus.publish-plugin'
apply from: "$rootDir/scripts/publish-maven.gradle"
apply from: 'publish.gradle'

group = ext['PUBLISH_GROUP_ID']
version = ext['PUBLISH_VERSION']
description = ext['PUBLISH_DESCRIPTION']

java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.9.2'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.9.2'
    testImplementation 'org.assertj:assertj-core:3.24.2'
}

test {
    useJUnitPlatform()
    finalizedBy jacocoTestReport
}

jacoco {
    toolVersion = "0.8.8"
}

jacocoTestReport {
    dependsOn test
    reports {
        xml.enabled true
        html.enabled false
    }
}

```

그리고 두번째로는 하위에 scripts 디렉터리를 만들고 `publish-maven.gradle` 이라는 파일을 생성했습니다.

여기에 pom 정보가 모두 들어가야 해서 꽤 방대합니다.

${code:publish-maven.gradle}

```groovy
apply plugin: 'maven-publish'
apply plugin: 'signing'
apply from: 'publish.gradle'

task sourcesJar(type: Jar) {
    archiveClassifier.set("sources")
}

task javadocJar(type: Jar) {
    archiveClassifier.set("javadoc")
}

artifacts {
    archives sourcesJar
    archives javadocJar
}

group = PUBLISH_GROUP_ID
version = PUBLISH_VERSION

ext["signing.keyId"] = ''
ext["signing.password"] = ''
ext["signing.secretKeyRingFile"] = ''
ext["ossrhUsername"] = ''
ext["ossrhPassword"] = ''
ext["sonatypeStagingProfileId"] = ''

File secretPropsFile = project.rootProject.file('local.properties')
if (secretPropsFile.exists()) {
    Properties p = new Properties()
    p.load(new FileInputStream(secretPropsFile))
    p.each { name, value ->
        ext[name] = value
    }
} else {
    ext["signing.keyId"] = System.getenv('SIGNING_KEY_ID')
    ext["signing.password"] = System.getenv('SIGNING_PASSWORD')
    ext["signing.secretKeyRingFile"] = System.getenv('SIGNING_SECRET_KEY_RING_FILE')
    ext["ossrhUsername"] = System.getenv('OSSRH_USERNAME')
    ext["ossrhPassword"] = System.getenv('OSSRH_PASSWORD')
    ext["sonatypeStagingProfileId"] = System.getenv('SONATYPE_STAGING_PROFILE_ID')
}

publishing {
    publications {
        release(MavenPublication) {
            groupId PUBLISH_GROUP_ID
            artifactId PUBLISH_ARTIFACT_ID
            version PUBLISH_VERSION

            artifact("$buildDir/libs/${project.getName()}-${version}.jar")
            artifact sourcesJar
            artifact javadocJar

            pom {
                name = PUBLISH_ARTIFACT_ID
                description = PUBLISH_DESCRIPTION
                url = PUBLISH_URL
                licenses {
                    license {
                        name = PUBLISH_LICENSE_NAME
                        url = PUBLISH_LICENSE_URL
                    }
                }
                developers {
                    developer {
                        id = PUBLISH_DEVELOPER_ID
                        name = PUBLISH_DEVELOPER_NAME
                        email = PUBLISH_DEVELOPER_EMAIL
                    }
                }
                scm {
                    connection = PUBLISH_SCM_CONNECTION
                    developerConnection = PUBLISH_SCM_DEVELOPER_CONNECTION
                    url = PUBLISH_SCM_URL
                }
                withXml {
                    def dependenciesNode = asNode().appendNode('dependencies')

                    project.configurations.implementation.allDependencies.each {
                        def dependencyNode = dependenciesNode.appendNode('dependency')
                        dependencyNode.appendNode('groupId', it.group)
                        dependencyNode.appendNode('artifactId', it.name)
                        dependencyNode.appendNode('version', it.version)
                    }
                }
            }
        }
    }
    repositories {
        maven {
            name = "sonatype"
            url = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"

            credentials {
                username ossrhUsername
                password ossrhPassword
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            packageGroup = PUBLISH_GROUP_ID
            stagingProfileId = sonatypeStagingProfileId
            username = ossrhUsername
            password = ossrhPassword
        }
    }
}

signing {
    sign publishing.publications
}

```

그리고 이번에는 build.gradle과 동일 경로에 생성한 publish.gradle 파일입니다.

${code:publish.gradle}

```groovy
ext {
    PUBLISH_GROUP_ID = 'io.github.shanepark'
    PUBLISH_VERSION = '1.1.0'
    PUBLISH_ARTIFACT_ID = 'psh'
    PUBLISH_DESCRIPTION = 'PSH: Problem Solving Helper'
    PUBLISH_URL = 'https://github.com/shanepark/psh'
    PUBLISH_LICENSE_NAME = 'MIT License'
    PUBLISH_LICENSE_URL = 'https://github.com/ShanePark/psh/blob/master/LICENSE'
    PUBLISH_DEVELOPER_ID = 'shanepark'
    PUBLISH_DEVELOPER_NAME = 'Shane Park'
    PUBLISH_DEVELOPER_EMAIL = 'psh40963@naver.com'
    PUBLISH_SCM_CONNECTION = 'scm:git:github.com/ShanePark/psh.git'
    PUBLISH_SCM_DEVELOPER_CONNECTION = 'scm:git:ssh://github.com/ShanePark/psh.git'
    PUBLISH_SCM_URL = 'https://github.com/ShanePark/psh'
}
```

마지막으로 local.properties 파일도 같은 경로에 생성했습니다.

${code:local.properties}

```properties
signing.keyId=2F311C65
signing.password=GPG암호입력
signing.secretKeyRingFile=/Users/shane/Documents/dev/keystore/signing.pgp #비공개키 경로 입력
ossrhUsername=shanepark #sonatype유저명입력
ossrhPassword=sonatype암호입력
sonatypeStagingProfileId=28196faf06633 #sonatype스테이징프로필아이디입력
```

## 배포

gradle 설정을 진짜 몇시간동안 끙끙대며 바꾼 끝에 간신히 배포에 성공했습니다. 오류가 날 때마다 검색해보며 계속 변경했는데 정말 힘들었습니다.

쉽게 되신다면 좋겠지만, 잘 안되신다면 맨 아래에 제가 Reference로 남긴 주소들에 방문해서 함께 참고하시면 도움이 될 것 같습니다.

빌드를 먼저 하고 아래의 명령어를 입력 해 줍니다.

```bash
./gradlew publishReleasePublicationToSonatypeRepository
```

![image-20230301230646511](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/publish-maven-central.assets/image-20230301230646511.png)

> 마침내 배포에 성공

이제 배포를 했다면 https://s01.oss.sonatype.org/#stagingRepositories 에 접속해 Staging Repositories를 확인 해 봅니다. `Refresh` 버튼을 클릭 하면

![image-20230301231205419](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/publish-maven-central.assets/image-20230301231205419.png)

> 로딩이 꽤 오래 걸립니다.

![image-20230301231309725](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/publish-maven-central.assets/image-20230301231309725.png)

위에 보이는 것 처럼 Status가 open 으로 나옵니다. 선택 해서 Close 버튼을 눌러 줍니다.

![image-20230301231415927](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/publish-maven-central.assets/image-20230301231415927.png)

> Close 완료까지 시간이 좀 걸릴거라고 합니다. 

Confirm을 눌러 줍니다. 그러고 나서 한참 후에 Refresh 버튼을 누르다보면 Release 버튼이 활성화됩니다.

![image-20230301231605564](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/publish-maven-central.assets/image-20230301231605564.png)

> Release 버튼이 활성화 되었습니다.

이제 Release를 눌러 마무리해줍니다.

![image-20230301231636788](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/publish-maven-central.assets/image-20230301231636788.png)

> 꽤 시간이 걸릴거라고 합니다. 

Confirm을 눌러 마무리 해 줍니다. 저는 그러고 15분정도 후에 maven central 에서 확인이 되었습니다.

![image-20230301233032262](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/publish-maven-central.assets/image-20230301233032262.png)

더 기다리면 검색에도 표시됩니다.

![image-20230302000040861](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/publish-maven-central.assets/image-20230302000040861.png)

드디어 길고 길었던 Gradle 프로젝트를 maven central에 배포하기 작업을 완료 했습니다. 라이브러리를 혼자 쓰거나 업무에서 임시로 사용하는 목적이라면 차라리 jitpack 으로 손쉽게 배포하는걸 추천합니다.

> [나만의 라이브러리 만들어 jitPack으로 배포하고 Maven/Gradle 에서 사용하기](https://shanepark.tistory.com/227)

이상입니다. 

**References**

- https://www.jetbrains.com/help/space/publish-artifacts-to-maven-central.html
- https://central.sonatype.org/publish/publish-guide/
- https://mangkyu.tistory.com/237
- https://proandroiddev.com/publishing-android-libraries-to-mavencentral-in-2021-8ac9975c3e52
- https://github.com/gradle-nexus/publish-plugin
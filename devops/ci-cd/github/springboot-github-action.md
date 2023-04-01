# Github Action을 활용한 SpringBoot 프로젝트 CI/CD

## Intro

매번 새로운 커밋이 발생할 때마다 서버에 새로운 버전으로 업로드하고, 기존에 실행 중이던 프로세스를 종료한 후, 새로 업로드한 프로젝트를 실행하는 일련의 과정은 상당히 번거로운 작업입니다.

최근 주목받고 있는 CI/CD 방법론은 이러한 문제를 해결하며 다양한 이점을 제공합니다.

> `CI/CD`는 지속적 통합(Continuous Integration)과 지속적 배포(Continuous Deployment)의 약자로서, 소프트웨어 개발 프로세스를 혁신적으로 개선하는데 중요한 역할을 수행합니다. CI/CD를 통해 코드 변경 사항을 자동으로 빌드, 테스트 및 배포함으로써 개발 속도를 높이고 안정성을 확보할 수 있습니다. 이러한 자동화 방법론은 개발자가 수동으로 빌드와 배포 과정을 반복하지 않아도 되게 하여 많은 시간을 절약할 수 있습니다.

이번에는 Github Action을 활용한 SpringBoot 프로젝트의 CI/CD 파이프라인 구축 방법에 대해 알아보겠습니다. 이번 글의 목표는 main 브랜치에 push가 발생하는 경우 자동으로 서버에 배포되도록 하는 파이프라인을 구축하는 것입니다.

실무에서는 하나의 jar 파일만으로 구동되는 서버는 드물고, 데이터베이스, 검색 엔진, 캐시 관리자 등 다양한 외부 의존성이 필요합니다. 또한, 무중단 배포를 위해서는 로드 밸런서 구축 등 복잡한 과정도 필요합니다. 

하지만 이번 글에서는 그러한 부분은 제외하고 최대한 복잡도를 줄여, 간단하게 아래의 과정에만 집중해 실습해보려고 합니다.

- main 브랜치에 커밋이 발생하면 자동으로 테스트 및 빌드가 이루어진다.
- 빌드된 jar 파일이 배포 서버에 복사된다.
- 배포 서버에서는 기존에 실행되고 있는 서버를 중단한다.
- 새로 빌드된 jar 파일로 서버를 실행한다.

지금부터 차근차근 진행해보도록 하겠습니다.

## 프로젝트 생성

### Spring Initializr

기존에 개발중인 프로젝트가 있다면 바로 적용해도 괜찮습니다.

하지만 최대한 현재 관심사에 집중하기 위해 아주 간단한 스프링부트 프로젝트를 생성하고, 그 프로젝트를 바탕으로 실습을 진행해보려고 합니다.

![image-20230401111215758](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401111215758.png)

> 프로젝트 생성

간단히 Spring Web 정도만 추가합니다. Actuator도 있으면 좋겠지만 Web만 하겠습니다.

![image-20230401111240027](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401111240027.png)

그리고 기본적인 Controller를 생성해주고, 버전 변경을 표시할 수 있도록 property 값을 한개 만들어둡니다.

**HomeController.java**

```java
@RestController
public class HomeController {

    @Value("${app.version:1.0.0}")
    String version;

    @GetMapping("/")
    public String home() {
        return "Hello CICD! version: " + version;
    }
}

```

application.properties

```properties
app.version=1.0.1
```

![image-20230401115113446](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401115113446.png)

서버를 실행해서 확인도 해줍니다.

![image-20230401115256314](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401115256314.png)

> 정상 작동!

### Github 저장소에 push

이제 새로 만든 프로젝트를 commit 후 Github 저장소에 push까지 해줍니다.

커밋

```bash
git add .
git commit -m "Commit message"
```

![image-20230401115439909](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401115439909.png)

커밋 후에는 새로운 저장소를 생성 해줍니다.

![image-20230401115703584](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401115703584.png)

> Github.com에서 저장소 생성

생성이 완료되면 push 해줍니다.

```bash
#git remote add origin 저장소주소
git remote add origin git@github.com:ShanePark/ci-cd-example.git
git branch -M main
git push -u origin main
```

푸쉬가 완료 되면 Github 저장소에서 코드 확인이 가능합니다.

![image-20230401115918365](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401115918365.png)

## 파이프라인 생성

### 빌드

일단 간단하게, main 브랜치에 push가 일어날 경우에 자동으로 빌드를 시도하는 스크립트를 먼저 작성해보겠습니다.

`Actions`메뉴를 확인해보면 `Java with Gradle`이 보입니다.

![image-20230401120036639](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401120036639.png)

> Java with Gradle

Configure를 클릭해서 확인해봅니다.

![image-20230401120221995](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401120221995.png)

확인해보면, 따로 수정할 필요 없이 바로 사용 가능한 스크립트가 작성되어있습니다.

바로 우측의 `Commit changes..` 를 클릭해 저장해보겠습니다.

![image-20230401120302717](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401120302717.png)

> main에 커밋

커밋과 동시에 CI 작업이 이루어집니다. `Actions`에 들어가서 확인 해 보면 Status가 In Progress로 변경되어 있습니다.

![image-20230401120326268](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401120326268.png)

잠시 기다리면..

![image-20230401120504837](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401120504837.png)

> 실패했습니다

에러메시지를 읽어보면 Java 11 에서 `org.springframework.boot:spring-boot-gradle-plugin:3.0.5` 를 찾을 수 없다고 합니다. 스프링부트 3.0 부터는 Java 17이 최소 요구사항이기 때문입니다.

방금 생성한 `gradle.yml` 파일을 자바 17로 수정해줍니다.

```bash
name: Java CI with Gradle

on:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]

permissions:
  contents: read

jobs:
  build:

    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v3
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    - name: Build with Gradle
      uses: gradle/gradle-build-action@67421db6bd0bf253fb4bd25b31ebb98943c375e1
      with:
        arguments: build
```

새로 Commit을 하고 기다리면..

![image-20230401120754479](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401120754479.png)

> 이번에는 정상적으로 빌드에 성공했습니다!

### SSH키 생성

배포 스크립트 작성에 앞서 SSH key 를 생성하려고 합니다. 이미 호스트에서 사용하고 있는 키를 그대로 써도 되지만, 별개의 키를 준비하는쪽이 보안상 더 좋겠다는 생각이 들었습니다.

```bash
mkdir ci-key
ssh-keygen -t rsa -b 4096 -f ~/Downloads/ci-key/ci-key
# ssh-keygen -t rsa -b 4096 -f ~/Downloads/ci-key/ci-key -C "your.email@example.com"
```

![image-20230401121441106](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401121441106.png)

> 키가 생성되었습니다.

새로 생성한 키를 접속할 서버에 등록합니다.

```bash
cat ~/Downloads/ci-key/ci-key.pub | ssh <SSH_USER>@<SSH_HOST> "cat >> ~/.ssh/authorized_keys"
# 예시로 저는 asus 라는 접속 정보를 저장해두었기 때문에 아래와 같이 입력하였습니다.
# cat ~/Downloads/ci-key/ci-key.pub | ssh asus "cat >> ~/.ssh/authorized_keys"
```

등록이 잘 되었는지 생성한 키를 활용해 ssh 접속을 해 봅니다.

```bash
ssh -i ~/Downloads/ci-key <SSH_USER>@<SSH_HOST>
```

![image-20230401122000193](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401122000193.png)

> 정상적으로 접속이 잘 되는 상황

새로 생성한 키가 잘 등록이 되었습니다. 이제 이 키파일을 이용하면 되겠습니다.

### 키 등록

`Settings` > `Security` > `Actions` 에 리포지터리 시크릿을 등록 할 수 있습니다. 

![image-20230401122435610](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401122435610.png)

New repository secret을 누르고 비밀키를 등록해줍니다. 주석부분도 포함해도 상관 없습니다.

![image-20230401122827564](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401122827564.png)

Add secret을 누르면 키가 추가됩니다.

저는 추가로 접속할 **서버 ip 주소**와 **접속 계정명**도 숨기기 위해 secret에 등록 했습니다.

![image-20230401123101729](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401123101729.png)

> 위와 같이 SERVER_IP 등을 추가할 수 있습니다.

![image-20230401123949159](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401123949159.png)

> 필요한 키가 추가된 상태

이제 준비가 완료되었습니다. 

### 스크립트 작성

이제 새로운 스크립트를 작성 해 줍니다.

```yaml
name: Java CI with Gradle

on:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]

permissions:
  contents: read

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Build with Gradle
        uses: gradle/gradle-build-action@67421db6bd0bf253fb4bd25b31ebb98943c375e1
        with:
          arguments: build
      - name: Upload artifact
        uses: actions/upload-artifact@v2
        with:
          name: cicdsample
          path: build/libs/*.jar

  deploy:
    needs: build
    runs-on: ubuntu-latest
    steps:
      - name: Download artifact
        uses: actions/download-artifact@v2
        with:
          name: cicdsample
      - name: Setup SSH
        uses: webfactory/ssh-agent@v0.5.4
        with:
          ssh-private-key: ${{ secrets.SSH_PRIVATE_KEY }}
      - name: SCP transfer
        run: scp *.jar ${{ secrets.SSH_USER }}@${{ secrets.SERVER_IP }}:~/cicd
      - name: Execute remote commands
        run: |
          ssh ${{ secrets.SSH_USER }}@${{ secrets.SERVER_IP }} "sudo fuser -k 8080/tcp"
          ssh ${{ secrets.SSH_USER }}@${{ secrets.SERVER_IP }} "sudo nohup java -jar ~/cicd/*.jar &"
```

> 빌드를 하고, 생성한  jar 파일을 배포서버에 업로드 합니다. 이후 배포서버에서는 이미 실행중인 프로젝트는 종료 시키고 나서 새로운 프로젝트를 nohup으로 실행시켜줍니다.

이번에는 Build와 Deploy가 나누어져 진행됩니다.

![image-20230401125149398](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401125149398.png)

이번에도 실패를 했는데요

![image-20230401125330257](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401125330257.png)

로그를 확인해보니 파일전송까지도 성공을 했지만, 실행을 하는 과정에서 `sudo` 명령에 실패했습니다.

사실 8080 포트는 well-known port(0~1023) 에 속하지 않기 때문에 꼭 sudo로 할 필요는 없지만, 나중에 실제 배포할 때 어차피 문제가 될 것이기 때문에 미리 해결해보겠습니다. 

몇가지 방법이 있겠지만, 가장 간단한 방법은 sudo 명령시 암호를 입력할 필요가 없도록 해주는 것 입니다.

배포 서버에서 sudoers 파일을 수정해줄건데요, `sudo visudo` 로 쉽게 수정할 수 있습니다. 다만, 기본 에디터가 nano로 설정되어있기 떄문에 저는 vim이 더 익숙해서 에디터를 먼저 변경해주었습니다.

```bash
sudo update-alternatives --config editor
```

![image-20230401163007132](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401163007132.png)

> 3번에 있는 vim으로 변경해주었습니다.

이제 수정하러 가봅니다.

```bash
sudo visudo
```

아래의 내용을 **제일 아래줄**에 추가해줍니다. `nohup`과 `fuser`에 대해서는 sudo 명령시 비밀번호 필요 없이 작동하도록 예외처리 해주는 설정입니다. 각 프로세스의 정확한 위치는 경우에따라 다를 수 있으니 `which fuser` 그리고 `which nohup`을 입력해서 서버에서 직접 확인해보셔야 합니다.

${code:/etc/sudoers}

```
shane ALL=(ALL:ALL) NOPASSWD: /usr/bin/fuser -k 8080/tcp, /usr/bin/nohup
```

그리고 `sudo` 에서는 `java`에 대한 PATH를 모르기때문에 위치까지 정확히 기입해 주어야 합니다. 서버에서 `which java`를 입력해서 설치된 자바 경로를 미리 확인해주세요. 저는 sdkman 을 사용해서 경로가 이렇지만, 보통 `/usr/lib/java` 같은 경로입니다.

${code:gradle.yml}

```yaml
      - name: SCP transfer
        run: scp *.jar ${{ secrets.SSH_USER }}@${{ secrets.SERVER_IP }}:~/cicd
      - name: Execute remote commands
        run: |
          ssh -v ${{ secrets.SSH_USER }}@${{ secrets.SERVER_IP }} "sudo fuser -k 8080/tcp || true"
          ssh -v ${{ secrets.SSH_USER }}@${{ secrets.SERVER_IP }} "sudo nohup /home/shane/.sdkman/candidates/java/current/bin/java -jar ~/cicd/*.jar  > ~/cicd/nohup.log 2>&1 &"
```

총 변경사항은 다음과 같습니다.

- java 에서 정확한 java의 위치가 명시된 것으로 변경

- 로그 남길 위치 지정
- `fuser -k` 에서 종료할 프로세스가 없으면 exit status가 1(실패)이기 때문에 fuser 결과를 무시하도록 `|| true`

수정한 최종 `gradle.yml`파일은 아래와 같습니다.

${code:gradle.yml}

```groovy
name: Java CI with Gradle

on:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]

permissions:
  contents: read

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Build with Gradle
        uses: gradle/gradle-build-action@67421db6bd0bf253fb4bd25b31ebb98943c375e1
        with:
          arguments: build
      - name: Upload artifact
        uses: actions/upload-artifact@v2
        with:
          name: cicdsample
          path: build/libs/*.jar

  deploy:
    needs: build
    runs-on: ubuntu-latest
    steps:
      - name: Download artifact
        uses: actions/download-artifact@v2
        with:
          name: cicdsample
      - name: Setup SSH
        uses: webfactory/ssh-agent@v0.5.4
        with:
          ssh-private-key: ${{ secrets.SSH_PRIVATE_KEY }}
      - name: Add remote server to known hosts
        run: |
          mkdir -p ~/.ssh
          ssh-keyscan ${{ secrets.SERVER_IP }} >> ~/.ssh/known_hosts
      - name: SCP transfer
        run: scp *.jar ${{ secrets.SSH_USER }}@${{ secrets.SERVER_IP }}:~/cicd
      - name: Execute remote commands
        run: |
          ssh -v ${{ secrets.SSH_USER }}@${{ secrets.SERVER_IP }} "sudo fuser -k 8080/tcp || true"
          ssh -v ${{ secrets.SSH_USER }}@${{ secrets.SERVER_IP }} "sudo nohup /home/shane/.sdkman/candidates/java/current/bin/java -jar ~/cicd/*.jar  > ~/cicd/nohup.log 2>&1 &"
```

`sudo nohup java` 명령이 문제없이 궁금해 작동할지 미리 호스트에서 테스트를 해 보았습니다.

```bash
ssh -i ~/Downloads/ci-key/ci-key shane@192.168.0.10 "sudo nohup /home/shane/.sdkman/candidates/java/current/bin/java --version"
```

![image-20230401172724892](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401172724892.png)

> sudo 명령을 썼지만, 비밀번호 요청 없이 잘 작동 합니다.

### -plain.jar 생성 금지

스프링부트 설정을 따로 변경하지 않았을 때는 `gradle clean build`를 하면 `SNAPSHOT.jar`와 `SNAPSHOT-plain.jar` 이렇게 두개의 파일이 모두 생성되기 때문에, plain.jar는 생성하지 않도록 해줍니다. 

> 그렇지 않으면 java -jar *.jar 할 때 no main manifest attribute, in /home/shane/cicd/ci-cd-sample-0.0.1-SNAPSHOT-plain.jar 에러가 발생합니다. 파일명을 정확히 기입해도 되지만, jar파일 하나만 만들도록 하는게 더 좋겠습니다.

build.gradle 파일에 한줄을 추가해주면 됩니다.

${file:build.gradle}

```groovy
jar.enabled = false
```

그러면 이제는 `-plain.jar` 이름의 파일은 만들지 않습니다.

변경할게 굉장히 많았네요. 이제 다시 확인해보면..

![image-20230401175232581](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401175232581.png)

> 10트만에 배포 성공

## 배포 확인

배포에 성공했으니 이제 요청을 보내 봅니다.

![image-20230401184638444](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401184638444.png)

> 성공적으로 서버가 뜬 상태.

이제 `CI/CD` 가 실질적으로 진행되는지를 확인하기 위해, 간단한 커밋을 해 봅니다. 

![image-20230401184854913](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401184854913.png)

> 간단하게 버전 변수만 변경해주고

커밋 및 푸쉬까지 해줍니다.

![image-20230401185050590](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401185050590.png)

> 커밋 완료

새로운 버전에 대한 파이프라인이 자동으로 진행됩니다.

![image-20230401185153522](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401185153522.png)

어플리케이션이 종료되고 다시 꺼지는 잠깐의 시간 (1~2초) 동안에는 서비스가 다운 됩니다.

![image-20230401185158327](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401185158327.png)

> 서비스가 다운된 상태.

서비스 다운을 피하기 위해서는 무중단 배포를 적용해주어야 합니다. 지금이야 프로젝트가 작아 바로 켜지지만, 실행하는데 오래 걸린다면 다운타임이 길어져 불편합니다. 무중단 배포에 대해서도 조만간 다루어보려고 합니다.

![image-20230401185205326](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/github/springboot-github-action.assets/image-20230401185205326.png)

> service up!

새로운 버전으로 자동 배포가 이루어졌습니다. 이제부터는 배포에 대해서는 신경쓰지 않고 코드 작성에 좀 더 집중할 수 있겠네요.

필요하시다면 본 샘플 프로젝트는 아래의 주소에서 자세한 코드를 확인하실 수 있습니다.

> https://github.com/ShanePark/ci-cd-example

이상입니다. 

**References**

- https://github.com/webfactory/ssh-agent
- https://github.com/actions/upload-artifact
- https://github.com/actions/download-artifact
- https://man.openbsd.org/ssh-keygen.1
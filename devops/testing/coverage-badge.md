# Github Action으로 코드 커버리지 뱃지 생성하기

## Intro

[Jacoco로 코드 커버리지 측정하기](https://shanepark.tistory.com/455) 에 이어지는 글 입니다.

이전 글에서는 코드 커버리지란 무엇인지, 그리고 Jacoco를 활용해 Gradle 혹은 Maven으로 만든 자바 프로젝트의 코드 커버리지를 측정하는 방법에 대해 알아보았습니다.

이번에는 `README.md`파일에 빌드 성공여부와 코드 커버리지를 뱃지로 만들어 등록하는 방법에 대해 알아보려고 합니다.

![image-20230301140938125](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301140938125.png)

별 것 아닌거 같아 보이는 이 뱃지 2개를 다는게 CI/CD를 경험해보지 못한 저에게는 생각보다 쉽지 않았습니다. 이 뱃지를 만드는게 최종 목표가 아니고, 자동 테스트 및 Workflow를 통해 부산물로 얻어지는게 위의 Build 결과와 코드커버리지 뱃지입니다. 

## Github Action 등록

### Codecov 회원가입

일단 깃헙 액션에 등록에 앞서서 Codecov 회원가입을 해주겠습니다.

https://about.codecov.io/sign-up/

![image-20230301085441380](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301085441380.png)

>  Github으로 가입하면 간단하게 가입 및 연동이 됩니다.

가입을 완료 하면 repo 목록이 나오는데요 아래와 같이 목록이 보이지 않는다면

![image-20230301085703153](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301085703153.png)

https://app.codecov.io/gh 직접 이동해주면 됩니다. 

그러면 아래와 같이 공개 및 비공개 저장소 목록을 모두 불러 옵니다.

![image-20230301090113307](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301090113307.png)

이제 Repository 목록이 보인다면 등록을 원하는 repo를 고르고 `Setup Repo` 를 클릭합니다.

그러면 repository secret을 보여주며 적용 방법을 단계별로 설명해줍니다. 안내대로 따라 해 보겠습니다.

![image-20230301143514721](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301143514721.png)

### Step 1: Repository Secret 추가

![image-20230301091055459](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301091055459.png)

> 등호 우측의 모자이크 부분이 시크릿 키 입니다. CODECOV_TOKEN= 까지 복사할 필요는 없습니다.

Github의 해당 저장소에서 `Settings > Security > Secrets and variables > Actions` 에 찾아들어가서 `New repository secret`을 클릭해서 방금 복사한 시크릿키를 추가 해 줍니다. 

![image-20230301090507697](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301090507697.png)

버튼을 클릭하고 추가 하면

![image-20230301090621346](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301090621346.png)

> 토큰 등록 성공

추가 하면 이와 같이 등록된 Repository secrets 가 보입니다. 이름은 CODECOV_TOKEN 지금 막 등록 됐다고 나오네요.

### Step 2: Github App 설치

![image-20230301143656062](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301143656062.png)

이제 Github App을 설치해줍니다. Pull Request에 찾아와서 커버리지 측정 결과에 대해 상세하게 댓글을 달아주는 기특한 모습을 확인 할 수 있습니다.

https://github.com/apps/codecov 에 접속해서 Install을 눌러서

<img src="https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301090730878.png" alt="image-20230301090730878" style="zoom:50%;" />

Github App에 있는 Codecov를 추가 해 줍니다.

<img src="https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301090805508.png" alt="image-20230301090805508" style="zoom:50%;" />

> 원하는 Repository 범위를 설정 하는데, 일단 All repositories 까지는 필요 없어서 원하는 저장소를 선택하여 추가 해 주었다.

설치 후 Codecov이 추가 된 모습 입니다.

![image-20230301091253833](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301091253833.png)

### Step3: Github Actions workflow 

![image-20230301143739014](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301143739014.png)

아래 두줄을 깃헙 액션에 추가해주라고 합니다.

```yaml
- name: Upload coverage reports to Codecov
  uses: codecov/codecov-action@v3
```

Github 액션은 처음 써보지만 당황하지 말고 일단 Actions 메뉴에 들어가봅니다.

![image-20230301084453893](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301084453893.png)

Java with Gradle이 가장 눈에 들어옵니다. Configure 를 클릭해서 확인해보겠습니다.

![image-20230301091801442](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301091801442.png)

yaml 형식으로 작성되어 있습니다. 쭉 읽어 보니 master 브랜치에 push나 pull request 가 감지되면 JDK11에서 build를 하는 스크립트로 보입니다. 

맨 아래 step에 Codecov 내용을 추가 해주면 될 것 같습니다.

![image-20230301092156304](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301092156304.png)

> steps에 Codecov 내용 추가

이제 Commit을 해줍니다. master에 바로 할건지 다른 브랜치에 먼저 커밋을 할건지 물어보는데 바로 master로 적용해 보았습니다.

커밋을 마치고 나서 Actions에 들어가 보면

![action](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/action.gif)

>  빙글빙글 돌며 스크립트가 열심히 실행 되고 있습니다.

조금 기다려보면

![image-20230301092452614](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301092452614.png)

> 45초만에 빌드에 성공했다고 표시됩니다.

빌드가 완료되었습니다. 자 그러면 이번엔 PR을 날려보겠습니다. branch를 하나 만들어서 파일 1개만 추가하고 test라는 이름으로 PR을 만들어 보았습니다.

![image-20230301092716292](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301092716292.png)

> PR 전송

PR을 보내면..

![image-20230301092754278](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301092754278.png)

즉시 CI 작업이 실행 됩니다. 그런데 작업이 진행중인데도 Merge 버튼이 활성화 되어있네요.

### CI 작업동안 Merge 막기

CI 작업이 돌아가고 있는데 Merge 버튼이 활성화 되어 있으면, 작업이 완료되지 않아도 실수로 Merge 시킬 수 있습니다. 설정으로 막아보겠습니다.

`Settiongs > Branches` 에 들어가서

![image-20230301092900793](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301092900793.png)

> master 브랜치에 해당하는 rule이 벌써 있기 때문에 Edit을 클릭

이미 적용중인 룰이 따로 없다면 Add rule을 하면 되습니다.

![image-20230301093128350](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301093128350.png)

`Require status checks to pass before merging`을 선택 하고, 원하는 작업(build)을 추가 한 뒤 저장 해 줍니다.

### Codecov 에러 해결

그런데 PR을 올리면 자동으로 커버리지 측정에 대한 결과를 보여주길 기대했는데

![image-20230301094203034](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301094203034.png)

`Ci with Gradle / build` 는 끝났는데도 불구하고 Codecov 리포트가 따로 추가되지는 않은 모습입니다. 

Details 버튼을 눌러 확인해봅니다. Upload coverage reports to Codecov 단계를 확인 해 보면

![image-20230301094307332](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301094307332.png)

> There was an error running the uploader: No coverage files located, please try use '-f', or change the project root with '-R'

커버리지 파일이 없다고 합니다. 커버리지 리포트 파일을 생성하지 않았기 때문인데요, 이전글에서 jacoco를 추가 해 두었기 때문에 거기서 생성한 리포트 파일을 이용하면 되겠네요.

지원 포맷을 알아봅니다.

![image-20230301150423082](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301150423082.png)

> https://docs.codecov.com/docs/supported-report-formats

exec를 바로 써먹진 못하고, html도 안되네요. 이전 글에서 추가한  jacocoTestReport 작업에 xml 생성을 true로 설정해줍니다.

${code:build.gradle}

```groovy
jacocoTestReport {
    dependsOn test
    reports {
        xml.enabled true
        html.enabled false
    }
}
```

또 막히는 부분이 있으면 https://github.com/codecov/example-java-gradle 에 있는 샘플 코드를  참고 하면 좋습니다.

이후 gradle test 를 실행하면

```bash
./gradlew clean test
```



![image-20230301113202705](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301113202705.png)

보이는 것 처럼 xml 리포트 파일이 생성됨을 확인 할 수 있습니다. 이제 수정한 정보를 새로 커밋해줍니다.

이미 PR을 등록해 둔 상태에서 커밋만 해도 Action이 새로 실행이 됩니다.

![image-20230301113433947](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301113433947.png)

> jacoco 테스트 결과를 xml파일로 생성하도록 설정을 해 두었더니, 이번에는 정상적으로 Codedov 작업이 완료되습니다.

Codecov 사이트에서도 확인 해 보면 정상적으로 테스트 결과가 등록 된 것이 확인 됩니다.

![image-20230301113616005](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301113616005.png)

그러고 조금 기다리면 PR에도 측정 결과를 댓글로 알려줍니다. 드디어 어느정도 다 온 것 같습니다.

![image-20230301113725918](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301113725918.png)

테스트 코드를 충분히 보강 한 후, 다음 PR도 날려보면

![image-20230301130511564](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301130511564.png)

> 커버리지가 올라간 내용으로 기본 브랜치와 비교도 해 줍니다.

## Badge 등록

자 이제 모든 준비가 완료되었으니 전리품인 뱃지만 달아주면 되겠습니다.

총 2가지 뱃지를 달건데요 하나는 `Java CI with Gradle` 액션에 대한 결과, 그리고 또 하나는 커버리지 정보 입니다.

![image-20230301115915243](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301115915243.png)

여기에서 Create status badge 를 누르면 해당 액션에 대한 뱃지 정보를 가져올 수 있습니다.

![image-20230301115948361](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301115948361.png)

> 간단합니다

이번에는 코드 커버리지 정보인데요, Codecov에서 뱃지기능을 기본으로 제공해주고 있기 때문에 이 역시 간단합니다.

app.codecov.io 에서 master 브랜치로 옮겨서 Settings에 들어갑니다. 그러면 좌측 메뉴에 세가지가 보이는데 `Badges & Graphs` 를 클릭 합니다.

![image-20230301115033792](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301115033792.png)

> 뱃지 정보를 가져올 수 있는 이미지 링크가 제공됩니다.

해당 코드조각을 README.md 파일에 추가 해 주면 됩니다.

![image-20230301132107296](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301132107296.png)

그런데 이상하게 codedov가 unknown 으로 표시되더라고요. 

이것저것 살펴봤는데 딱히 오류가 없어서 혹시 다른 저장소들도 같은 문제를 겪고 있나 체크해보려고 codecov에서 제공한 샘플 저장소에 들어가보니

![image-20230301132202113](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301132202113.png)

> 여기도 unknown

마찬가지 문제가 발생하고 있더라고요. 아무래도 Codecov 자체적인 문제인 듯 한데 언제 고쳐질지 잘 모르겠습니다.

그래도 이대로 두고싶지는 않아서 shild.io 에서 제공하는 Codecov badge 기능을 사용해 보았습니다.

https://shields.io/category/coverage

![image-20230301132539118](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301132539118.png)

이 중에서 Codecov에 해당하는건 두가지가 있는데요 둘중 원하는걸 선택하고 필요한 정보를 입력 해 줍니다.

![image-20230301132650023](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301132650023.png)

그러면 Badge URL을 제공해 줍니다. markdown 형태로 받아와서 `README.md` 파일에 추가 해줍니다.

![image-20230301132941353](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/testing/coverage-badge.assets/image-20230301132941353.png)

> 드디어 빌드와 커버리지 Badge를 추가했습니다!

생각보다 간단하지 않았습니다만 이렇게 해서 커버리지 뱃지를 추가 할 수 있었습니다.

이상입니다.

**References**

- https://docs.codecov.com/docs/quick-start
- https://github.com/codecov/example-java-gradle
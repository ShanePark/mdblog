# Jenkins 튜토리얼 - 설치 및 실행

## 설치

### 요구사항

Jenkins를 설치하고 실행하기 위해서는 아래의 조건을 만족해야 한다.

- 호스트 머신이
  - 램 최소 256 MB (권장 2 GB)
  - 젠킨스 및 도커 이미지를 위한 여분의 저장공간 10 GB
- 아래의 소프트웨어 들이 설치 되어 있어야함
  - Java 11, 17, or 21
  - Docker (아직 설치가 안되어있다면  [**Get Docker**](https://docs.docker.com/get-docker/) 참고)

### 다운로드

https://www.jenkins.io/download/ 링크에서 젠킨스를 다운 받는다.

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/Jenkins-tutorial.assets/2.webp)

OS에 맞춰 원하는 무엇으로 다운받아도 상관 없으나 본 튜토리얼은 `.war` 파일로 진행한다

### 실행

다운로드 받은 war 파일을 java로 실행한다. httpPort는 원하는 값을 넣으면 되는데, 8000으로 진행해보도록 한다.

```bash
java -jar jenkins.war --httpPort=8000
```

![3](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/Jenkins-tutorial.assets/3.webp)

> Jenkins가 실행 된 상태. 설치를 계속 진행하려면 화면에 보이는 패스워드를 사용하라고 한다.

http://localhost:8000/ 에 접속한다

![4](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/Jenkins-tutorial.assets/4.webp)

위에서 복사해둔 패스워드를 붙여 넣는다.

![5](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/Jenkins-tutorial.assets/5.webp)

둘 중 선택하라고 하는데 `Install suggested plugins` 를 선택해보았다.

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/Jenkins-tutorial.assets/1.webp)

> 설치가 완료되기를 기다린다

첫번째 Admin User를 생성하는 폼이 나온다.

![6](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/Jenkins-tutorial.assets/6.webp)

> 관리자 계정을 생성한다

![7](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/Jenkins-tutorial.assets/7.webp)

> Jenkins URL을 설정한다. 개발 환경에서는 그대로 두면 된다.

![8](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/Jenkins-tutorial.assets/8.webp)

> 설치 완료

![9](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/Jenkins-tutorial.assets/9.webp)

설치가 끝났고, Dashboard 화면이 보인다.

## 첫번째 파이프라인 만들기

젠킨스 파이프라인은 CD 파이프라인을 Jenkins에 구현하고 통합하는 것을 지원하는 플러그인의 모음이다.

CD pipeline은 버전 컨트롤에 있는 소스코드를 사용자에게 고객에게 소프트웨어로 직접 전달하는 것을 자동화한것이다.

젠킨스 파이프라인은 간단한 것 부터 복잡한 파이프라인까지 모두 코드로 모델링 할 수 있는 확장 가능한 도구를 제공한다.

`jenkinsfile` 이라 불리는 텍스트파일에 작성하며, 버전관리에 포함한다.

### Docker Pipeline plugin 설치

`Manage Jenkins` -> `Plugins` 순서로 메뉴를 찾아 들어간다

![10](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/Jenkins-tutorial.assets/10.webp)

Docker Pipeline 를 검색해 설치한다. 우측의 `Install`을 누르면 된다.

![11](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/Jenkins-tutorial.assets/11.webp)

설치 후 재시작한다

![12](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/Jenkins-tutorial.assets/12.webp)

재시작이 될 동안 파이프라인에 사용할 깃헙 저장소를 만들어두자.

### Github Repository 생성

![15](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/Jenkins-tutorial.assets/15.webp)

> 새로운 리포지터리를 생성한다.

리포지터리를 생성 한 후에는 `Jenkinsfile` 이라는 파일명으로 파일 생성

```
pipeline {
    agent { docker { image 'maven:3.9.6-eclipse-temurin-17-alpine' } }
    stages {
        stage('build') {
            steps {
                sh 'mvn --version'
            }
        }
    }
}
```

![16](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/Jenkins-tutorial.assets/16.webp)

> 파일명과 내용을 입력 하고 `Commit changes` 를 클릭한다. 
>
> 파일명은 대소문자를 구분하기 때문에 반드시 첫 J를 대문자로 작성해야 한다. 위의 스크린샷에서는 두가지 문제가 있는데 그렇게 하면 안된다.
>
> - 파일명의 첫 글자가 소문자다
> - 맨 윗줄을 제대로 주석처리하지 않았다

### Item 추가

이제 재시작된 Jenkins 에서 `New Item` 클릭

![13](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/Jenkins-tutorial.assets/13.webp)

적당히 아이템 이름을 입력 한 뒤 `Multibranch Pipeline` 선택

![14](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/Jenkins-tutorial.assets/14.webp)

`Add source` 를 누르고 Github 선택

![17](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/Jenkins-tutorial.assets/17.webp)

위에서 생성한 저장소 주소를 넣고 `Validate`

![18](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/Jenkins-tutorial.assets/18.webp)

이후 하단의 Save를 눌러서 저장한다. 그러면 자동으로 리퍼지터리 스캔을 진행한다.

![19](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/Jenkins-tutorial.assets/19.webp)

> 진행중.. 로그를 살펴보면, Jenkinsfile는 없고 jenkinsfile 은 있다고 나온다. 말했던 것 처럼 실수해서 그렇다. 파일명은 반드시 `Jenkinsfile` 로 해야한다.

![20](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/Jenkins-tutorial.assets/20.webp)

> 파일명 변경 후 재시도..

![21](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/Jenkins-tutorial.assets/21.webp)

>  main branch 를 체크하고 Jenkinsfile 도 찾아내었다.

![22](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/Jenkins-tutorial.assets/22.webp)

빌드에 성공했다.

![23](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/Jenkins-tutorial.assets/23.webp)

> 로그를 확인하면 mvn --version 에 대한 출력값이 보인다.

### stage 추가

이번에는 `Jenkinsfile`을 조금 수정해서 build 이후 hello 스테이지가 실행 되게 한다

![24](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/Jenkins-tutorial.assets/24.webp)

확인

![25](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/Jenkins-tutorial.assets/25.webp)

![26](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/Jenkins-tutorial.assets/26.webp)

> hello 가 실행되었다.

이제 준비된 Jenkins 서버를 가지고 무궁무진하게 활용하면 되겠다.

**References**

- https://www.jenkins.io/doc/pipeline/tour/getting-started/
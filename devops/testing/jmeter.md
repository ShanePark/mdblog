# Apache JMeter를 활용한 부하테스트

## Intro

부하테스트는 여러명의 사용자가 동시에 어플리케이션에 요청을 보내는 상황을 시뮬레이션 하여 다양한 부하조건에서의 응답을 테스트 하는 과정 입니다. 얼마만큼의 요청을 견딜 수 있는지 테스트 함으로서 병목 구간을 찾아 성능 개선을 하거나 서버를 증설하는 등의 필요한 대응을 할 수 있습니다.

다양한 테스트 툴이 있지만, 사용하기 쉬운 JMeter를 활용해 간단한 테스트를 진행 해 보겠습니다.

## 설치

<img src=https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/jmeter.assets/image-20220426104256919.webp width=672 height=502 alt=1>

Apt-cache로 검색을 해 봤더니 이미 apt 저장소에 있길래 apt를 이용해 설치 해 보았습니다.

```bash
sudo apt install jmeter
```

![image-20220426104413915](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/jmeter.assets/image-20220426104413915.webp)

> https://jmeter.apache.org/download_jmeter.cgi

MacOS 유저라면 brew로 설치 하셔도 되고, Apache 홈페이지를 통해 다운 받을 수 도 있기 떄문에 운영체제에 상관 없이 다운 받아 압축을 푸시면 됩니다. 

압축 해제 후 unix기반(Linux, MacOS)이라면 jmeter.sh 파일을, 윈도우는 jmeter.bat 파일을 실행 하면

![image-20220426104633715](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/jmeter.assets/image-20220426104633715.webp)

> 이렇게 실행 되는걸 확인 하실 수 있습니다.

## 테스트 작성

![image-20220426114850480](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/jmeter.assets/image-20220426114850480.webp)

> 좌측 두번째 버튼인 Template을 클릭 합니다.

![image-20220426114944224](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/jmeter.assets/image-20220426114944224.webp)

Recording으로 템플릿이 선택 되어 있는데, 바로 Create를 클릭 합니다.

![image-20220426115350606](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/jmeter.assets/image-20220426115350606.webp)

>  혹시 Thread Group이 없으면 Test Plan을 우클릭 후 Thread Group을 하나 추가 해 줍니다.

이제 Thread Group을 우클릭 하고 Add -> Listener -> Http Request를 클릭해 추가해줍니다.

![image-20220426115712473](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/jmeter.assets/image-20220426115712473.webp)

이제 테스트할 Http 서버의 정보를 입력하는데요, 일단 랜딩 페이지("/")를 두드리도록 해 보겠습니다.

`Server Name or IP` 에는 localhost를 집어 넣고, 포트 넘버에도 사용중인 번호를 넣었습니다.

그 외 아래의 입력 칸들을 통해 특정 주소나 파라미터를 입력 할 수도 있습니다.

![image-20220426115839847](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/jmeter.assets/image-20220426115839847.webp)

Thread Group 에서 Number of Threads로 사용할 쓰레드(유저) 의 수와 Loop Count로 반복할 횟수를 정해 줍니다. 

처음에 테스트 대상이 테스트 진행하는 로컬과 같다는걸 망각하고. Thread를 말도안되게 높게 입력했다가 컴퓨터가 바로 죽어버리더라고요,,, 처음에는 적당히 넣어줍니다.

![image-20220426131950973](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/jmeter.assets/image-20220426131950973.webp)

> 실행을 하기 전에는 설정을 저장 해 줘야 합니다.

이제 실행 버튼을 누르면 설정 해 둔 대로 부하 테스트를 진행합니다. 

확인을 위해 nginx의 로그를 띄워 두었습니다. 

![image-20220426132203445](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/jmeter.assets/image-20220426132203445.webp)

> 정확히 3개의 쓰레드에서 각 3번씩 지정해둔 대로 총 9번의 요청이 보내졌습니다.

![image-20220426132717061](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/jmeter.assets/image-20220426132717061.webp)

> Thead 300개로 테스트를 진행 했을 때 CPU 점유가 급격하게 올라간 모습입니다.

지금은 랜딩 페이지에 부하를 주기 때문에 이정도도 별 문제 없지만, WAS 에 로직이 들어간 요청을 하면 훨 큰 부하가 가해집니다.

## 플러그인 추가

![image-20220426133111477](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/jmeter.assets/image-20220426133111477.webp)

> https://jmeter-plugins.org/

jmeter에 필요한 플러그인이 있다면 위의 사이트에서 다운 받으실 수 있습니다.

원하는 플러그인을 다운 받아서 jMeter가 설치 된 폴더의 `/lib/ext` 에 넣으면 되는데요 

![image-20220426133510071](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/jmeter.assets/image-20220426133510071.webp)

> https://jmeter-plugins.org/install/Install/

Plugins-manager 를 이용하면 간단하게 체크박스 체크만으로 원하는 플러그인을 설치 할 수 있습니다.

Download 우측의 링크를 클릭해 `plugins-manager.jar` 파일을 다운 받아 해당 경로에 넣어 줍니다.

![image-20220426133722652](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/jmeter.assets/image-20220426133722652.webp)

> 저의 경우에는 /usr/share/jmeter 폴더에 jmeter가 설치 되어 있어서 아래의 명령어로 옮겼습니다.

```bash
sudo mv jmeter-plugins-manager-1.7.jar /usr/share/jmeter/lib/ext
```

다운 받은 폴더를 해당 폴더에 넣어 주고 jmeter를 재시작 해 줍니다. 저는 이 과정에서 apt 로 설치한 jmeter는 문제가 있어서 저는 jmeter를 새로 다운로드 해서 진행 했습니다.

![image-20220426135423747](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/jmeter.assets/image-20220426135423747.webp)

> 이제 Options 메뉴에 Plugins Manager가 생겼습니다.

![image-20220426135502093](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/jmeter.assets/image-20220426135502093.webp)

체크박스에서 설치하고자 하는 플러그인을 선택 하고 `Apply Changes and Restart jMeter`를 클릭하기만 하면 간단하게 새로운 플러그인이 설치 됩니다.

## 테스트 진행

### 부하테스트

이번에는 Thread Group에 Listner -> Add -> jp@gc Transaction Per Second를 추가 한 뒤 부하 테스트를 진행 해 그래프를 확인 해 보겠습니다.

![image-20220426140504089](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/jmeter.assets/image-20220426140504089.webp)

> TPS: Thread 500개, repeat 3회 

![image-20220426140706645](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/jmeter.assets/image-20220426140706645.webp)

> TPS: Thread 30개, Repeat 50회

여러가지 테스트를 해본 결과 제 테스트 환경에서는 아래의 TPS(Transaction Per Second)가 측정되었습니다.

| Page\Thread(users)        | 10      | 100     | 300    | 500  |
| ------------------------- | ------- | ------- | ------ | ---- |
| 랜딩 페이지("/")          | 120 t/s | 200 t/s | 65 t/s | FAIL |
| 톰캣이 비즈니스 로직 처리 | 28 t/s  | 26 t/s  | 25 t/s | FAIL |

Thread는 400개 까지도 큰 문제 없었지만 500이 되면 성공하는 트랜잭션 보다 실패하는 트랙잭션이 더 많았습니다.

### 쿠키설정

테스트에 로그인 정보를 입력 해야 하기 때문에 Cookie 정보가 필요 할 수 있습니다.

그땐 Thread Group을 우클릭 하고, Add -> Config Element -> HTTP Cookie Manager를 추가 한 뒤에

![image-20220427172734020](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/jmeter.assets/image-20220427172734020.webp)

> Add

추가된 Http Cookie Manager를 클릭 하고

![image-20220427172859498](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/jmeter.assets/image-20220427172859498.webp)

Add 를 눌러 필요한 쿠키에 대한 정보를 입력 하고 Save 하면 테스트에 쿠키를 사용 할 수 있습니다.



지금까지 jMeter를 활용해 부하 테스트를 진행 해 보았습니다. 이상입니다.

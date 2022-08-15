# [MacOS] M1 맥북 도커로 ORACLE DB 실행하기

## Intro

M1 맥북을 구입 한 이후로 약 1년 반동안, 오라클 데이터베이스를 띄우기 위해 참 많은 노력을 했습니다. 

많은 고민과 시도 끝에 결국 [오라클 클라우드에 DB를 띄워놓고 사용하는 방법](https://shanepark.tistory.com/208) 으로 해결을 해 왔는데요, 난이도가 높은건 둘째 치더라도 인터넷이 안되는 환경에서는 이용할 수 없었습니다.

> 인터넷이 안되면 사실 개발을 못하는게 맞지않나..?!

하지만 이제는 더이상 그럴 필요가 없어졌습니다. 오픈 소스 컨테이너 런타임인 `Colima`를 사용해 `oci-oracle-xe` 이미지를 x86/64 환경으로 띄운다면 M1 맥북에서 오라클 데이터베이스를 로컬에서도 띄울 수 있습니다. 

아래의 내용을 차근 차근 진행해주시면 마침내 `localhost:1521` 를 얻으실거에요.

## 설치

### colima 설치

Colima는 무거운 Docker Desktop을 대신해 간단한 CLI 환경에서 도커 컨테이너들을 실행 할 수 있는 오픈 소스 소프트웨어 입니다.

> https://github.com/abiosoft/colima

brew 를 활용해서 손쉽게 설치합니다. brew가 없다면 [링크](https://shanepark.tistory.com/45)를 참고해서 먼저 설치 해 주세요.

```bash
brew install colima
```

![image-20220802074204725](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220802074204725.png)

### docker 설치

```bash
brew install docker
```

docker가 아직 설치되어 있지 않았다면 설치 해 주고, 이미 설치되어있다면 Docker desktop을 종료해주세요.

Colima는 Docker Desktop을 대신하는 것 이기 때문에, 같이 띄우면 안됩니다. Docker Desktop을 삭제 해도 상관은 없는데, 제가 잠깐 써봤을때는 서로 이미지를 공유하는 것 같지는 않았습니다.

### colima 실행

Colima와 Docker를 모두 설치했다면, colima를 x86_64 환경으로 띄워 줍니다.

Colima를 사용하지 않고 Docker Desktop 환경에서는 `oci-oracle-xe` 이미지로 컨테이너를 띄웠을 때 아키텍처가 달라 문제가 되었었는데, 그걸 Colima가 해결 해 줍니다.

```bash
colima start --memory 4 --arch x86_64
```

![image-20220802074246457](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220802074246457.png)

> `docker ps` 명령어가 작동됩니다.

정상적으로 가상 환경이 준비 되면 docker 명령어들이 작동됩니다.

이제 오라클 서버를 띄우겠습니다. 비밀번호 옵션만 각자 원하는대로 변경 해 주세요.

```bash
docker run -e ORACLE_PASSWORD=pass -p 1521:1521 -d gvenzl/oracle-xe
```

![image-20220802074143096](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220802074143096.png)

용량이 꽤 큰데, 어느 정도 시간이 걸려 설치가 완료 되었습니다.

![image-20220802074435458](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220802074435458.png)

> oracle 컨테이너가 떠있습니다.

이제 로그를 확인 해 봅니다.

```bash
docker logs -f 컨테이너명
```



![image-20220802074458233](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220802074458233.png)

![image-20220802074511589](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220802074511589.png)

같은 도커 이미지를 Docker desktop 에서 돌렸을때에는 실행이 되지 않았는데, Colima로 돌리니 Database mounted가 되었습니다!

조금 더 기다리면..

![image-20220802074556719](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220802074556719.png)

> 마침내 DATABASE IS READY TO USE 가 되었습니다.

## 연결 테스트

이제 DBeaver로 연결 테스트를 해 보겠습니다.

Host 는 localhost, Database는 xe, 포트는 1521 을 입력하고 유저네임은 system, 비밀번호는 아까 위에서 옵션으로 준 값을 입력 (pass) 하고 테스트를 합니다.

![image-20220802074851769](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220802074851769.png)

m1 맥북에서 localhost:1521 로 DB 접속에 처음 성공한 감격의 순간 입니다.

이제 마음놓고 M1 맥북에서도 로컬에서 오라클 데이터베이스를 사용하실 수 있게 되었습니다 :) 

## 재시작후 데이터가 사라져요

제가 Docker 사용에 익숙하지 않은 분들이 제법 있을거라는걸 충분히 배려하지 못했던 것 같더라고요 죄송해요. 그래서 내용을 추가했습니다.

사실 데이터가 진짜로 사라진건 아니고, 컨테이너가 종료 되었을때 같은 컨테이너를 다시 띄워주지 않고 그냥 `docker run -e ORACLE_PASSWORD=pass -p 1521:1521 -d gvenzl/oracle-xe` 를 또 입력 하신다면 **새로운 컨테이너**가 또 생성 됩니다. 

그렇기 때문에 이전에 사용하던 컨테이너를 그대로 사용 하시려면 **같은 컨테이너**를 다시 띄우셔야 하는데요.

아래의 명령어를 입력 하면 **종료된 컨테이너 목록**도 확인 할 수 있습니다.

```bash
docker ps -a
```

![image-20220815204617787](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220815204617787.png)

제걸 예로 들면, **trusting_nash** 라는 이름으로 된 `oracle-xe` 컨테이너가 종료 된 상태인게 보입니다. 저 컨테이너를 다시 띄워줘야 해요.

가장 왼쪽에 보이는 컨테이너 ID를 입력 해도 되고, 처음에 실행 할 때 지정해주지 않았기 때문에 임의로 도커가 만들어준 제 경우에는 trusting_nash 라고 되어 있는 컨테이너 이름을 입력 해도 됩니다. `docker start` 명령으로 실행 해 주세요.

```bash
docker start f4ac517e4ee4
```

![image-20220815204804384](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220815204804384.png)

이후 다시 `docker ps`를 해보면, 기존에 사용하던 컨테이너가 다시 떠있는걸 확인 할 수 있고, DB 접속을 해 보시면 기존에 사용하던 데이터를 이어서 사용 하실 수 있습니다.

사실 이렇게 매번 하려면 번거롭기 때문에 두가지 방법을 추천 드릴게요.

1. **컨테이너 이름 설정해서 띄우기**

컨테이너 명을 명시해서 띄운다면, 나중에 켜고 끌 때 좀 더 수월하게 하실 수 있습니다.

```bash
docker run --name oracle -e ORACLE_PASSWORD=pass -p 1521:1521 -d gvenzl/oracle-xe
```

![image-20220815205215329](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220815205215329.png)

이제 컨테이너 이름이 식별하기 쉽기 때문에, 다음에 다시 띄울때는 `docker start oracle` 만 해주면 됩니다.

2. **볼륨 설정하기**

애초에 볼륨으로 데이터를 매핑 해 두면 컨테이너를 실수로 지운다고 해도 데이터를 계속 사용 하실 수 있습니다.

볼륨에 대한 자세한 내용은 본 글의 범위를 넘어가기 때문에 간단히 언급만 하고 넘어가도록 하겠습니다. 

참고로 맵핑할 폴더를 미리 생성해두지 않고 도커가 자동으로 만들게 냅두면 권한 문제가 발생해 컨테이너가 한번에 뜨지 못합니다. 미리 mkdir로 생성 해 주세요. 경로는 저는 Documents 폴더에 해 두었지만 원하는 경로로 지정 하시면 됩니다.

```bash
cd ~/Documents
mkdir oracledb

docker run --name oracle -v ~/Documents/oracledb:/opt/oracle/oradata -e ORACLE_PASSWORD=pass -p 1521:1521 -d gvenzl/oracle-xe
```

![image-20220815210114496](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220815210114496.png)

> 볼륨을 걸고, 컨테이너가 정상적으로 뜬 상태 입니다.

![image-20220815210255971](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220815210255971.png)

> 자동으로 생성된 oracledb 폴더의 구조 입니다. 이 폴더만 있으면 컨테이너를 새로 만들어도 데이터를 유지 할 수 있습니다.

혹시 11g 버전을 꼭 사용 해야만 하는 상황인 분들은, 볼륨 경로도 함께 변경이 필요하니 아래 내용을 참고해주세요

```bash
docker run --name oracle -v ~/Documents/oracledb:/u01/app/oracle/oradata -e ORACLE_PASSWORD=pass -p 1521:1521 -d gvenzl/oracle-xe:11
```

> 11g의 경우

볼륨에 대한 이해가 어렵다면, 처음부터 볼륨까지 설정 해서 사용하실 필요는 없고, 컨테이너 이름만 지정해서 사용하시면 됩니다. 

다만 볼륨지정을 하지 않았을 경우에는 실수로라도 컨테이너를 날려 버릴 경우 데이터가 다 지워지기 때문에, 날려먹으면 안되는 프로젝트에 관련된 데이터라던가 하는 경우에는 일단 잘 몰라도 어딘가에 볼륨을 설정 해 두고 진행해주세요.

이상입니다. 감사합니다. 

**ref**

- https://github.com/gvenzl/oci-oracle-xe/issues/63
- https://github.com/abiosoft/colima
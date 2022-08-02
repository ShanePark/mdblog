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

**ref**

- https://github.com/gvenzl/oci-oracle-xe/issues/63
- https://github.com/abiosoft/colima
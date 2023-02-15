# [MacOS] M1, M2 맥북 도커로 ORACLE DB 실행하기

## Intro

M1 맥북을 처음 구입 한 이후로 약 1년 반동안, 오라클 데이터베이스를 띄우기 위해 참 많은 노력을 했었습니다. 원래부터 Oracle이 MacOS를 정식 지원을 하지는 않았지만, 그나마 이전의 맥북에서는 작동시킬 방법들이 있었는데 Apple Silicon 에서는 먹히지가 않았습니다. 아키텍처가 바꼈거든요.

많은 고민과 시도 끝에 결국 [오라클 클라우드에 DB를 띄워놓고 사용하는 방법](https://shanepark.tistory.com/208) 으로 한참을 해결을 해 왔는데요, 난이도가 높은건 둘째 치더라도 인터넷이 안되는 환경에서는 이용할 수 없었습니다.

> 요즘엔 사실 인터넷이 안되면 개발을 못하는게...

하지만 이제는 방법이 생겼습니다. 오픈 소스 컨테이너 런타임인 `Colima`를 사용해 `oci-oracle-xe` 이미지를 x86/64 환경으로 띄운다면 M1 맥북에서도 오라클 데이터베이스를 띄울 수 있습니다. 지금 저는 M2 MacBook Air 를 사용하고 있고 역시 잘 작동 합니다.

사실 본 글의 내용이 초보자들에게 쉬운건 아니기 때문에 어느정도 이미 다른 환경에서 비슷한 과정을 했던 분들을 대상으로 작성 되었지만, 제가 M1 맥북 에어를 처음 샀을 때 그러했던 것 처럼 지금 이 글을 읽고 있는 대다수의 분들은 Docker는 커녕 OracleDB도 아직 안써본 분들이 많을거에요.

그렇기에 쉽지는 않겠지만 딱히 대안이 없는 상황이기에 차근 차근 진행 해 보시고, 아래 수백개의 질문과 답변도 참고해보세요.

아래의 내용을 차근 차근 잘 진행해주시면 마침내 `localhost:1521` 를 얻으실거에요.

## 설치

### colima 설치 (*필수)

Colima는 무거운 Docker Desktop을 대신해 간단한 CLI 환경에서 도커 컨테이너들을 실행 할 수 있는 오픈 소스 소프트웨어 입니다.

> https://github.com/abiosoft/colima

brew 를 활용해서 손쉽게 설치합니다. brew가 없다면 [링크](https://shanepark.tistory.com/45)를 참고해서 먼저 설치 해 주세요.

```bash
brew install colima
```

![image-20220802074204725](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220802074204725.png)

### docker 설치

docker가 아직 설치되어 있지 않았다면 본 항목을 확인하며 설치해주시고, 기존에 이미 설치되어있다면 실행중인 Docker desktop을 종료만 하고 아래의 Colima 실행으로 넘어가주세요.

![image-20220821090106960](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220821090106960.png)

도커를 아직 설치 한 적이 없으시다면 도커 설치가 필요한데요..

도커 데스크탑을 설치할 수도 있고, 도커 엔진만 설치해서 하실 수도 있는데. 도커 데스크탑을 원하시면 아래의 링크에서 우측 Mac with Apple chip을 선택 해서 다운 받으시면 됩니다.

![image-20220821082548481](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220821082548481.png)

> https://www.docker.com/products/docker-desktop/

아니면 brew로도 설치가 가능합니다.

```bash
brew install --cask docker
```

Colima는 Docker Desktop을 대신해서 docker 엔진을 실행해주기 때문에, 도커 데스크탑과 같이 띄우면 안된다고 생각했는데..

테스트를 해 보니 같이 실행해도 문제는 없더라고요. 

![image-20220821091050604](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220821091050604.png)

그래서 둘다 실행 되어 있다면 docker 명령어를 누가 가져가나 했는데

![image-20220821091307865](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220821091307865.png)

> 둘다 동시에 실행되면 colima가 docker desktop로 설정되어 있던 default docker context를 가져가 버립니다. 
>
> Docker context가 다르면 이미지 공유도 안되더라고요.

![image-20220916224949482](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220916224949482.png)

위에서 처럼 Docker Context를 변경 하면 자유롭게 왔다 갔다 하면서 사용 할 수 있긴 하지만, 헷갈릴 수 있으니 왠만하면 처음에는 Docker Desktop은 종료 하고 해주세요. 

**Docker Context 목록 보기** (안따라 하셔도 됩니다.)

```bash
docker context ls
```

**Docker Context 변경** (안따라 하셔도 됩니다.)

```bash
docker context use desktop-linux
docker context use colima
```

**Docker Engine 만 설치** (Docker Desktop 설치 했으면 필요 없음.)

Docker desktop은 필요 없고 도커엔진만 필요하다면 brew로 도커 엔진만 설치하셔도 됩니다.

```bash
brew install docker
```

![image-20220821082753728](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220821082753728.png)

> 도커 엔진만 설치하면 Treating docker as a formula. For the cask, use homebrew/cask/docker 라고 나옵니다. 도커 엔진이 있으면, 그걸 구동 할 수 있는 도커 머신이 필요한데요. Docker Desktop 혹은 Colima 가 그 역할을 해 줍니다.
>
> 초보자분들은 이렇게 하지 말고 Docker Desktop을 설치해주세요.

### colima 실행

Colima와 Docker를 모두 설치했다면, colima를 x86_64 환경으로 띄워 줍니다.

Colima를 사용하지 않고 Docker Desktop 환경에서는 `oci-oracle-xe` 이미지로 컨테이너를 띄웠을 때 아키텍처가 달라 문제가 되었었는데, 그걸 Colima가 해결 해 줍니다. 

```bash
colima start --memory 4 --arch x86_64
```

![image-20220802074246457](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220802074246457.png)

> `docker ps` 명령어가 잘 작동됩니다.

정상적으로 가상 환경이 준비 되었다면 docker 명령어들이 작동됩니다.

이제는 오라클 서버를 띄우겠습니다. 비밀번호 옵션만 각자 원하는대로 변경 해 주세요. 

> 컨테이너 이름을 명시하지 않고 실행 후 나중에 차차 변경하는 식으로 글을 작성했었는데, 명령어 치는데 oracle이라는 이름의 컨테이너가 없다는 댓글이 많이 달려서 처음부터 컨테이너명을 명시하도록 글을 수정했습니다.
>
> 원래는 restart 옵션도 글 후반부에 더했었지만, docker 사용법이 서툰 분들을 위해 처음부터 모두 포함된 명령어를 포함한걸 감안해주세요.

```bash
docker run --restart unless-stopped --name oracle -e ORACLE_PASSWORD=pass -p 1521:1521 -d gvenzl/oracle-xe
```

![image-20220802074143096](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220802074143096.png)

용량이 꽤 큰데, 어느 정도 시간이 걸려 설치가 완료 되었습니다.

![image-20220802074435458](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220802074435458.png)

> oracle 컨테이너가 떠있습니다.

이제 로그를 확인 해 봅니다.

```bash
# docker logs -f (컨테이너명)
docker logs -f oracle
```

![image-20220802074458233](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220802074458233.png)

![image-20220802074511589](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220802074511589.png)

같은 도커 이미지를 Docker desktop 에서 돌렸을때에는 실행이 되지 않았는데, Colima로 돌리니 Database mounted가 되었습니다!

조금 더 기다리면..

![image-20220802074556719](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220802074556719.png)

> 마침내 DATABASE IS READY TO USE 가 되었습니다.

## 연결 테스트

이제 DBeaver로 연결 테스트를 해 보겠습니다. SQL Developer가 설치되어있다면 그걸 이용하셔도 됩니다.

Host 는 **localhost**, Database는 **xe**, 포트는 **1521** 을 입력하고 유저네임은 **system**, 비밀번호는 아까 위에서 옵션으로 준 값을 입력 (pass) 하고 테스트를 합니다. 데이터베이스명이 혹시 orcl 로 되어 있다면, xe로 꼭 바꿔주셔야 합니다.

![image-20220802074851769](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220802074851769.png)

m1 맥북에서 localhost:1521 로 DB 접속에 처음 성공한 감격의 순간 입니다.

이제 마음놓고 M1 맥북에서도 로컬에서 오라클 데이터베이스를 사용하실 수 있게 되었습니다 :) 

## SCOTT 계정 생성

윈도우에서 오라클을 쓸 때는 아래와 같이 기존에 포함된 scott 계정을 unlock만 해서 바로 쓸 수 있는데요,

```sql
ALTER USER SCOTT
2 IDENTIFIED BY tiger
3 ACCOUNT UNLOCK;

CONNECT scott/tiger;
DESCRIBE EMP;
```

해당 도커 이미지에는 샘플 계정이 포함 되어 있지 않기 때문에

![image-20230126095055792](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20230126095055792.png)

> ORA-01918: user 'SCOTT' does not exist 라고 나옵니다.

그렇기 때문에 수동으로 생성 해 주셔야 합니다. 

### 계정 생성

 sqlplus로 접속 후에

```bash
docker exec -it oracle sqlplus
```

> 유저네임은 system, 비밀번호는 위에서 입력(예시는 pass)한 값을 입력 합니다.

먼저 유저를 생성 하고 필요한 권한을 부여 합니다.

```sql
CREATE USER scott identified by tiger;
-- 한줄씩 입력해주세요.
GRANT CONNECT, resource, dba to scott;
```

![image-20230126100205611](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20230126100205611.png)

> User created, Grant succeeded.

생성 후에는 아래 쿼리로 유저가 정상적으로 생성 된 것을 확인 할 수 있습니다.

```sql
select username from dba_users where username = 'SCOTT';
```

![image-20230126095607033](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20230126095607033.png)

### SCOTT 으로 접속

`ctrl + d` 키로 접속을 끊고 나서 다시 sqlplus로 접속 합니다. 

```bash
docker exec -it oracle sqlplus
```

대신 이번에는 계정명 scott 암호 tiger로 접속 합니다.

![image-20230126100415173](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20230126100415173.png)

> scott 으로 접속한 상태.

이제 `demobld.sql` 파일을 구해서 쿼리를 실행 해 줍니다. 총 117 라인의 SQL 파일인데 아래 링크에서 확인하실 수 있습니다. 14년 전 커밋이라서 링크가 깨질 가능성은 낮아 보이지만, 혹시나 접속이 안되더라도 구글에 `demobld.sql` 을 검색하면 같은 파일이 많이 나옵니다.

https://github.com/mv/mvdba/blob/master/demo/demobld.sql

쿼리를 실행 한 뒤에, 샘플 데이터가 입력이 잘 되었는지 확인 해 봅니다. 

```sql
select * from emp;
```

![image-20230126100726727](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20230126100726727.png)

> 정상적으로 데이터가 입력 된 상태.

이제 이 샘플 계정을 활용해 SQL 기초를 연습 하시면 됩니다.

## 자주묻는질문: 재시작후 데이터가 사라져요

제가 Docker 사용에 익숙하지 않은 분들이 제법 있을거라는걸 충분히 배려하지 못했던 것 같더라고요. 그래서 내용을 추가했습니다!

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

거기에 restart 옵션까지 준다면, 나중에 colima만 실행 하면 컨테이너는 알아서 실행 되도록 할 수 있습니다. 되도록이면 restart 옵션 주는 것을 추천 합니다.

```bash
docker run --restart unless-stopped --name oracle -e ORACLE_PASSWORD=pass -p 1521:1521 -d gvenzl/oracle-xe
```

![image-20221127151846174](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20221127151846174.png)

> restart 옵션 덕에 colima 를 띄우니 oracle container가 자동으로 실행 된 상태 

이제 컨테이너 이름이 식별하기 쉽기 때문에, 다음에 다시 띄울때는 `docker start oracle` 만 해주면 됩니다.

두번째 방법은 볼륨을 설정 하는 방법 입니다.

2. 볼륨 지정해서 띄우기(위치미지정)

애초에 볼륨으로 데이터를 매핑 해 두면 컨테이너를 실수로 지운다고 해도 데이터를 계속 사용 하실 수 있습니다.

```bash
docker run --name oracle -d -p 1521:1521 -e ORACLE_PASSWORD=pass -v oracledb:/opt/oracle/oradata gvenzl/oracle-xe
```

이렇게 하면 oracledb 라는 볼륨을 생성해서 모든 데이터를 저장 합니다.

나중에 볼륨을 확인하려면 

```bash
docker volume ls
```

명령어로 확인 하실 수 있어요.

![image-20220825115718339](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220825115718339.png)

리눅스의 경우에는 `/var/lib/docker/volumes/` 에서 볼륨 경로를 직접 확인 할 수 있지만 대신 MacOS 의 경우에는 도커를 네이티브로 구동 할 수 없다보니 Hyperkit라는 가상화 이미지를 사용합니다.

`~/Library/Containers/com.docker.docker/Data/vms/0` 경로에 가상 이미지가 있고, 이미지 내에서 `var/lib/docker` 경로까지 들어 가야 도커의 기본 경로라고 하는데.. 굉장히 복잡합니다.

그래서 볼륨을 원하는 위치를 지정해서 하는 방법이 있는데 아래의 방법으로 하시면 됩니다.

## 볼륨 설정해서 컨테이너 띄우기

> 위치지정

볼륨에 대한 자세한 내용은 본 글의 범위를 넘어가기 때문에 간단히 언급만 하고 넘어가려고 했는데 이게 권한 문제를 일으키더라고요.

그래서 볼륨을 설정 한 상태로 컨테이너를 띄우는 방법을 한단계씩 안내해드릴테니 천천히 따라 해 주세요. 

사실 내용이 너무 어려울 수 있긴 한데 그래도 볼륨지정이 필요한 상황이 있을 수 있어서 적어둡니다

> @jeeweon 님께서 제가 적어둔 볼륨 지정을 따라해 보시다가 에러로 고생을 해 주셔서 그 덕분에 본 내용을 추가 할 수 있었습니다. 감사합니다.

### 1. 볼륨 지정할 폴더 생성 후 컨테이너 띄우기

```bash
mkdir ~/Documents/oracledb
docker run -d --name oracle -v ~/Documents/oracledb:/opt/oracle/oradata -p 1521:1521 -e ORACLE_PASSWORD=pass gvenzl/oracle-xe
```

> 볼륨 지정시 권한 문제로 비밀번호 지정이 어차피 문제되기 때문에 여기에서 환경변수로 선언하는 비밀번호는 크게 의미 없습니다.
>
> `~/Documents/oracledb` 경로를 만들어서 볼륨을 지정하는데, 원하시면 다른 경로로 변경하셔도 됩니다.

컨테이너 생성에 성공 하면 로그를 확인합니다.

```bash
docker logs -f oracle
```

위의 명령을 띄워 놓고 데이터베이스가 생성되는 로그를 확인 해주세요. 제법 오랜 시간이 걸립니다.

![image-20220824211245890](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220824211245890.png)

> 볼륨을 걸고, 컨테이너가 정상적으로 뜬 상태 입니다. 데이터 베이스를 생성 중 입니다.
>
> 약 3GB 의 파일을 풀기 때문에 때문에 꽤 오래 걸려요.

완성되면 oracledb 폴더가 아래와 같은 구조가 됩니다.

![image-20220815210255971](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220815210255971.png)

> 자동으로 oracledb 폴더가 생성되고 파일들이 생깁니다. 이 폴더만 있으면 컨테이너를 새로 만들어도 데이터를 유지 할 수 있습니다.

### 2. 데이터 베이스 생성완료. 하지만 password file 열기 오류

![image-20220824211558466](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220824211558466.png)

이제 볼륨을 걸어서 진행 했다면 권한 문제로 비밀번호 파일을 열지 못하기 때문에 컨테이너가 `ALTER USER SYS    IDENTIFIED BY "pass"` 를 시도하다가 그대로 죽어 버립니다. 실제로 해당 컨테이너 내부로 들어가서 확인 해 보면, `/opt/oracle/oradata` 의 소유주가 호스트의 uid로 지정 되어있습니다.

호스트에서 해당 컨테이너가 볼륨을 마운트 된 폴더에 접근을 할 수 있도록 권한 설정을 따로 해 주어야 합니다.

### 3. 컨테이너 다시 실행후 uid 확인

일단 죽어있는 컨테이너를 다시 실행 해 줍니다.

```bash
docker start oracle
```

![image-20220824211845793](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220824211845793.png)

실행 후에는 컨테이너 내부의 uid를 확인 해 줍니다.

````bash
docker exec -it oracle id
````

![image-20220824211949528](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220824211949528.png)

해당 컨테이너 내부에서 oracle 이라는 이름의 uid는 54321인 것으로 확인 됩니다.

이제 이 uid로 소유자을 변경 해 주면 됩니다.

### 4. 소유자 및 퍼미션 변경

```bash
sudo chown -R 54321:54321 ~/Documents/oracledb
```

![image-20220824212212682](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220824212212682.png)

> 54321로 소유자가 변경 되었습니다.

컨테이너 내부에서 확인 해 보면 소유자가 oracle로 확인 됩니다.

```bash
docker exec -it oracle ls -al /opt/oracle/oradata
```

![image-20220824212336169](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220824212336169.png)

이어서 퍼미션 정보도 변경 해 줍니다. 퍼미션을 열어주지 않으면 호스트에 권한이 없어서 컨테이너를 띄울 때 문제가 생깁니다.

```bash
 sudo chmod -R 777 oracledb
```

![image-20220824212931572](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220824212931572.png)

이제 변경 된 퍼미션으로 문제없이 실행되는지 확인을 위해 컨테이너를 재 시작 해줍니다.

```bash
docker restart oracle
```

![image-20220824213041978](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220824213041978.png)

재시작 후에는 로그를 확인 해 보면 천천히 DB가 뜨는게 확인 됩니다.

```bash
docker logs -f oracle
```

![image-20220824213141649](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220824213141649.png)

![image-20220824213208113](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220824213208113.png)

컨테이너가 정상적으로 뜨고, 오라클 DB가 준비되었다면 `Ctrl+C` 로 로그를 종료 시키고, 비밀번호를 초기화 해 줍니다.

```bash
docker exec oracle resetPassword <원하는 비밀번호>
```

![image-20220824213250141](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220824213250141.png)

> 권한 문제가 해결되었다면 비밀번호가 정상적으로 변경 됩니다.

### 5. 접속 테스트

이제 모든 설정이 끝났습니다.. 디비 클라이언트를 켜고 Database 에는 xe를, Username에는 system, 그리고 패스워드에는 위에서 resetPassword 할 때 입력한 값을 써 줍니다.

![image-20220824213559116](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220824213559116.png)

모든 설정이 완료되었습니다.

이제 실수로 컨테이너를 지운다고 해도 ~/Documents/oracledb 폴더만 멀쩡히 있다면 데이터베이스를 유지 할 수 있습니다.

## 마치며

볼륨에 대한 이해가 어렵다면, 처음부터 볼륨까지 설정 해서 사용하실 필요는 없고, 컨테이너 이름만 지정해서 사용하시면 됩니다. 

다만 볼륨지정을 하지 않았을 경우에는 실수로라도 컨테이너를 날려 버릴 경우 데이터가 다 지워지기 때문에, 날려먹으면 안되는 프로젝트에 관련된 데이터라던가 하는 경우에는 일단 잘 몰라도 어딘가에 볼륨을 설정 해 두고 진행해주세요. 볼륨을 지정하지 않고 사용하던 컨테이너는 이미지로 커밋 한 후에 볼륨을 지정 해도 괜찮습니다.

혹시 11g 버전을 꼭 사용 해야만 하는 상황인 분들은, 볼륨 경로도 함께 변경이 필요하니 아래 내용을 참고해주세요

```bash
docker run --name oracle -v ~/Documents/oracledb:/u01/app/oracle/oradata -e ORACLE_PASSWORD=pass -p 1521:1521 -d gvenzl/oracle-xe:11
```

> 11g에서의 볼륨 설정 예시

그 외의 버전이 필요한 경우에도 https://hub.docker.com/r/gvenzl/oracle-xe/tags 에서 태그정보를 확인해서 이미지명을 변경 해서 컨테이너를 생성 하시면 됩니다. 확인해보니 11g, 18c, 21c가 준비되어 있습니다.

이상입니다. 감사합니다.  

**ref**

- https://github.com/gvenzl/oci-oracle-xe/issues/63
- https://github.com/abiosoft/colima
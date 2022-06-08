# Docker) MariaDB 컨테이너 설치하기. IntelliJ IDEA 로 DB 접속하기

## Intro

입사 전 국비학원에서 Oracle Database 교육을 몇주간 짧게 받은게 전부였는데 입사 후에도 맡은 프로젝트들에서 JPA를 사용하다 보니 쿼리를 직접 짤 일이 많지 않았습니다. 데이터베이스 공부를 따로 하려고 했는데 자바 심화과정, 스프링, JPA, 운영체제, 자료구조 및 알고리즘 등 평소 너무나도 공부하고싶었던 것들이 너무나도 많았다보니 자연스레 우선순위에서 밀려 아직까지도 손을 대지 못하고 있습니다.

업무중에 드문 드문 조금이라도 복잡한 쿼리를 짜내야 하는 일이 생길 때 마다 개념은 어렴풋 알고 있어도 정확한 사용법을 모르는 문법이나 함수 등을 매번 검색하다보니 병목이 생기는 구간이라는게 명백해졌습니다. 병목구간을 알면서도 외면하기엔 양심의 가책이 큽니다.

학습도 중요하지만 꾸준히 사용을 해서 내것으로 익히는 것도 중요하기때문에 일단 SQL 문제를 많이 풀어보며 어떤점이 부족하고 어느 부분들 좀 더 중점적으로 공부해야 하는지를 파악해 보려고 합니다. 다행히 몇달전부터 취미로 틈날 때 마다 풀고 있는 Leetcode를 보니 약 50개의 SQL 문제들이 있습니다. 이걸 다 풀어보고 나면 공부 방향이 어느 정도 잡히지 않을까 싶습니다.

![image-20220326115551701](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/mariadb.assets/image-20220326115551701.png)

DB 학습용으로 처음에는 H2를 MySQL 모드로 띄워놓고 사용했는데, H2는 IntelliJ IDEA의 DataBase 기능으로 접속시 스키마 조회가 정상적으로 되지 않는 등 불편함이 있어서  MariaDB 를 Docker Container로 띄워놓고 사용하려고 합니다.

혹시 Docker를 아직 설치하지 않은 분은 아래의 링크중 사용하시는 OS에 맞는 설치 방법을 확인 해 주세요. 

> 윈도우는 WSL까지 올려야 해서 설치가 조금 더 까다롭습니다.

- [MacOS ) m1 맥북 docker 설치하기 + 가상환경에 postgreSQL 띄워 보기](https://shanepark.tistory.com/194)
- [Windows) Docker 설치하기. + 도커 가상환경에 PostgreSQL 설치하기](https://shanepark.tistory.com/188)
- [Ubuntu 20.04 LTS ) Docker 설치하기](https://shanepark.tistory.com/237)
- [CentOS) Docker 및 Compose 설치](https://shanepark.tistory.com/278)

## MariaDB Container 생성

hub.docker의 mariaDB Official Image를 확인 합니다.

![image-20220326121251370](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/mariadb.assets/image-20220326121251370.png)

> Downlaods 카운트가 무려 10억건이 넘습니다.

![image-20220326121030950](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/mariadb.assets/image-20220326121030950.png)

> https://hub.docker.com/_/mariadb
>
> 저는 M1 맥북을 사용하고 있는데 ARM 64 태그가 붙어 있어 반갑네요.

아래로 조금 내리면 이미지 사용법도 작성 되어 있습니다.

![image-20220326121537938](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/mariadb.assets/image-20220326121537938.png)

위의 내용을 바탕으로 mariaDB 서버 컨테이너를 띄워보겠습니다.

```bash
docker run \
	--detach \
	--name mariadb \
	--env MARIADB_USER=shane \
	--env MARIADB_PASSWORD=changeme \
	--env MARIADB_ROOT_PASSWORD=changeme \
	--publish 3306:3306 \
	mariadb:latest

```

좀 더 읽기 쉽게 줄을 나누었고, 대개의 경우 host port에 매핑해 사용하기 때문에 포트설정 하는 옵션도 추가 했습니다.

맨 위부터 하나씩 옵션을 설명 해 드리면

- `--detach`: 컨테이너를 백그라운드에서 실행하고, 컨테이너 ID를 프린트 해 줍니다.  `-d`로 짧게 작성 할 수 있습니다.
- `--name`: 컨테이너의 이름을 정합니다. `-n`으로 짧게 작성 할 수 있습니다.
- `--env`: 환경변수를 설정 합니다. `-e`로 작성 가능합니다.
- `--publish`: 컨테이너의 포트를 Host에 매핑 합니다. 좌측은 호스트 운영체제의 포트, 우측은 도커 컨테이너의 포트 입니다. `-p`로 축약합니다.

그 외 더 다양한 옵션은 https://docs.docker.com/engine/reference/commandline/run/ 에서 간단히 확인 가능합니다.

![image-20220326125508114](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/mariadb.assets/image-20220326125508114.png)

> 위에 작성한 실행문을 그대로 실행 하였습니다.

이후 docker 컨테이너가 실행 되면 해당 컨테이너의 ID가 표기 됩니다. 이제 `docker ps`를 입력해 상태를 확인 합니다.

![image-20220326125604501](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/mariadb.assets/image-20220326125604501.png)

> Host 의 3306 포트 매핑되어 잘 실행 되어 있습니다.

## 유저 생성 및 권한 설정

이제 데이터베이스를 만들었으니 유저를 생성 하고 권한을 부여 해 주겠습니다. 혼자만 연습용으로 쓰는 DB라고 해도 root 계정을 그대로 사용하는건 자바에서 객체의 모든 메서드와 인스턴스를 public으로 열어두는 것 처럼 불편함이 있습니다.

### Container에 접속

일단 DB 및 유저 생성을 하기 위해 컨테이너에 접속 해야 합니다.

```bash
docker exec -it mariadb /bin/bash
```

혹시 컨테이너 이름을 다르게 지으셨다면 mariadb 대신 설정하신 컨테이너 명을 작성해 주어야 합니다. 혹은 Container ID를 적어주어도 됩니다.

### DB 초기설정

Root 권한으로 MYSQL 데이터베이스에 접속해 설정 DB 및 유저를 추가하고 권한을 설정 해 줍니다.

```bash
mysql -u root -p
```

> 명령어 실행 후 처음에 컨테이너를 생성할 때 작성한 root 암호를 입력 합니다. 위에서는 `changeme` 라고 작성 해 두었습니다.

![image-20220326131746476](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/mariadb.assets/image-20220326131746476.png)

> Root 계정으로 접속 되었습니다.

leetcode라는 데이터베이스를 만들고, shane이라는 유저에게 해당 DB의 모든 권한을 부여해보겠습니다.

- DB 생성: `create database leetcode;`
- 사용자 생성: `create user 'shane'@'%' identified by 'shanepassword';`
- 권한 부여: `grant all privileges on leetcode.* to 'shane';`

![image-20220326132616577](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/mariadb.assets/image-20220326132616577.png)

> shane 유저를 생성하는데 실패 했는데, 곰곰히 생각해보니 사실 컨테이너를 만들 때 환경변수 설정으로 이미 shane이라는 유저를 생성 했습니다. 이미 같은 이름의 유저가 있어서 생성이 안되었습니다.

leetcode 라는 이름의 database를 만들고, shane 유저에게 해당 DB의 모든 권한을 부여 했습니다.

## IntelliJ IDEA에서 접속

지금까지는 DBeaver만 사용해왔는데 아무래도 Eclipse 기반이다 보니 단축키 사용에 혼동이 있었습니다. intelliJ IDEA의 Database 기능이 JetBrains의 DataGrip 라고 하여 한번 사용 해 보았는데, 굉장히 만족스러워서 앞으로 계속 사용하게 될 것 같습니다.

![image-20220326133059358](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/mariadb.assets/image-20220326133059358.png)

우측 상단에 Database 라는 탭이 작게 있습니다. 클릭하고 좌측의 `+` 버튼을 눌러 DB 연결정보를 추가 해 줍니다.

![image-20220326133130596](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/mariadb.assets/image-20220326133130596.png)

> Data Source > mariaDB 선택

![image-20220326133206332](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/mariadb.assets/image-20220326133206332.png)

> DB 접속 정보를 입력 해 줍니다. 포트를 변경하지 않았다면 User, Password, Database만 입력 후 Test Connection 을 눌러 봅니다.

처음 접속한다면 Driver를 설치하게 되는데, 설치 후 Test Connection에서 성공 하면 초록색으로 Succeeded 라는 글자가 나옵니다.

혹시 연결에 실패했다면 user명, 비밀번호, Database명을 확인 해 보시고, 다 맞다면 컨테이너가 작 잘동 중인지, 작동중이라면 3306 포트를 정상적으로 매핑 했는지를 확인 해 보세요.

![image-20220326133527009](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/mariadb.assets/image-20220326133527009.png)

접속 되었다면 이제 다른 DB 클라이언트를 사용 했을 때 처럼 자유롭게 쿼리를 실행하고 DB를 확인 할 수 있습니다.

 이상입니다.
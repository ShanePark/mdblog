# MacOS ) m1 맥북 docker 설치하기 + 가상환경에 postgreSQL 띄워 보기

## Intro

Windows에 Docker를 설치 해 보았으니, Mac에서도 Docker를 설치 해 보겠습니다. 

예전에는 MySQL건 postgreSQL이건 DB를 설치 할 때 마다 로컬에 직접 설치를 했었는데요, 그러면 필요할때만 켜고 끄려고 해도 DB를 켜고 끄는 명령어를 입력 해 주어야 하고 이게 확인하기 전까지는 돌아가고 있는지 아닌지도 눈에 보이지가 않아 불편했는데요, 한번 Docker를 사용 해 보니 그 매력이 굉장해서 왠만한 건 이제 Docker로 돌리게 될 것 같습니다. 

특히 Docker의 장점은 쉬운 설치 및 개발 환경 그대로 배포가 가능하다는 점 등이 있습니다. 특히 Docker Compose를 이용하면 yaml 포맷으로 다수의 container를 묶어서 실행 및 관리 할 수도 있으며 volume을 활용해 손쉽게 데이터베이스를 통째로 백업 하고 불러올 수도 있습니다.

## 설치 

brew search 를 해보았습니다. 

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/docker.assets/img-20220326124941782.png)

 

이 경우에는 Formulae 와 Casks가 모두 있기 때문에 그냥 brew install docker를 한다면 Formulae로 설치 됩니다.

Formulae는 CLI 환경, Casks는 GUI 환경 입니다. 사용하기 편하신 걸 설치하면 좋은데 초보자는 아무래도 GUI 환경에서가 하기 좋습니다.

 

아래의 명령어를 입력해 설치합니다.

```xml
brew install --cask docker
```

 

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/docker.assets/img-20220326124941774.png)



가상 환경을 세팅하다 보니 시간이 꽤 걸립니다.



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/docker.assets/img-20220326124941873.png)



설치가 완료되었습니다.!

 

이제 docker 명령어를 입력 하면 Option들에 대한 설명이 나옵니다.



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/docker.assets/img-20220326124941737.png)



 

Alfred 로 검색해도 어플리케이션이 설치되어 있는 것을 확인 할 수 있습니다.



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/docker.assets/img-20220326124941621.png)



또한 우측 상단에는 Docker 가 실행 되어 있습니다.



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/docker.assets/img.png)



## Postgres 띄우기 

Docker를 실행 해 보면 Container가 아직 한개도 없습니다.



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/docker.assets/img-20220326124941957.png)



 

Terminal에 Windows에 했던 것과 정확히 똑같은 명령어를 입력해 postgreSQL 컨테이너를 띄워 보도록 하겠습니다.

 

```xml
docker run --name PostgreSQL -e POSTGRES_USER=shane -e POSTGRES_PASSWORD=java -d -p 5432:5432 postgres
```

POSTGRE_USER= 에 있는 shane 은 본인이 원하는 아이디로, PASSWORD= 옆에 있는 비밀번호도 java가 아닌 원하시는 비밀번호로 변경하시면 됩니다.

--name 은 이름 설정 입니다. 옆에 postgreSQL 이라고 입력한건 docker에 등록 할 이름인데, myPostgres 등 원하시는 이름으로 하시면 됩니다.

-d 는 백그라운드 모드 (detached mode)

-p 는 호스트와 컨테이너의 포트 연결

-e 는 컨테이너에서 사용할 환경 변수를 설정하는 명령어 입니다. 



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/docker.assets/img-20220326124941914.png)



금방 컨테이너가 등록 되고 실행 됩니다. docker GUI로 Container를 확인해보면 Running 중인 것을 확인 할 수 있습니다.



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/docker.assets/img-20220326124941784.png)



이제 이렇게 서버가 켜졌다면 접속을 확인 해 보겠습니다.

저는 DBeaver를 사용 해 보도록 하겠습니다.

DB 종류는 PosetgreSQL로 고르고



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/docker.assets/img-20220326124941949.png)



 

방금 docker를 띄울때 지정한 이이디와 비밀번호를 입력 합니다. 그래서 Test Connection을 쏴 보면



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/docker.assets/img-20220326124941931.png)



Docker로 띄운 PostgreSQL에 잘 접속 하는 것이 확인 됩니다!

 

간단하게 게시판 만들떄 썼던 테이블도 넣어 봅니다.

```sql
CREATE TABLE public.board (
	boardno serial NOT NULL,
	title varchar NULL,
	"content" varchar NULL,
	writer varchar NULL,
	CONSTRAINT board_pk PRIMARY KEY (boardno)
);
INSERT INTO public.board (title,"content",writer) VALUES
	 ('2번째 글 제목','2번째 내용','2번작성자'),
	 ('새글','새글 써봅니다.','새글맨'),
	 ('글 수정','수정도 잘됩니다.','수정맨'),
	 ('1번째 글 제목','1번째 내용,내용','1번작성자');
```

 

그러고 쿼리를 조회해 봅니다

```sql
select * from board;
```

 



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/docker.assets/img-20220326124941647.png)



 

아주 좋습니다! 그대로 이전에 Windows 의 docker 환경에서 postgres 띄워 만들서 준비했던 스프링 부트로 만든 게시판도 실행 해 보았습니다.



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/docker.assets/img-20220326124942022.png)



문제 없이 서버가 실행 되고 해당 url로 접속도 잘 되는 것을 확인했습니다 ! docker 를 잘 사용 한다면 정말 배포에 들이는 시간과 비용을 줄여 생산성이 향상 되겠다는 생각이 듭니다.
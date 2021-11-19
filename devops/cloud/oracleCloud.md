# Mac) Oracle Cloud FreeTier 이용해 웹 어플리케이션 배포하기 4) 무료 데이터 베이스 만들기 및 데이터 이관하기

[
Mac) Oracle FreeTier 이용해 웹 어플리케이션 배포하기 1)인스턴스 만들고 접속하기.](https://shanepark.tistory.com/170)

[Mac) Oracle FreeTier 이용해 웹 어플리케이션 배포하기 2) 톰캣 설치하기 및 배포 + 외부 접속 허용](https://shanepark.tistory.com/171)

[Mac) Oracle FreeTier 이용해 웹 어플리케이션 배포하기 3) 타임존 문제 해결](https://shanepark.tistory.com/172)

[Mac) Oracle FreeTier 이용해 웹 어플리케이션 배포하기 4) 무료 데이터 베이스 만들기 및 데이터 이관하기](https://shanepark.tistory.com/173)

[Mac) Oracle Cloud FreeTier 이용해 웹 어플리케이션 배포하기 5) 톰캣 도메인 연결해 배포하기](https://shanepark.tistory.com/174)

[Mac) Oracle Cloud FreeTier 이용해 웹 어플리케이션 배포하기 6) 오라클 전자지갑 사용해 프로젝트와 연결하기](https://shanepark.tistory.com/207)

## 시작에 앞서

### 사족

해당 글은 2021년 11월인 지금으로부터 약 4개월전인 2021년 7월에 작성 했던 글 입니다. 하지만 어느 순간 보니 글의 내용이 다 날라가 있더라고요, 글을 한번 씩 수정해서 업데이트 하다가 실수로 모두 지워 버렸던 것 같습니다.

지금에는 블로그 글을 쓸 때마다 Github 저장소에 꼬박꼬박 백업을 하며, 버전 관리를 하고 있으나 이 당시에는 티스토리에만 글을 작성하던 때라서 아무리 노력해도 글을 복원 할 방법을 찾지 못했습니다. 그냥 어쩔 수 없이 이때의 기억을 되살려 다시 한번 해 보려고 합니다.

사실 제가 M1 맥북을 처음 샀을 때 가장 고생 했던게 오라클 데이터베이스를 사용하는 문제였고, 지금도 해당 문제로 고생하고 있는 분들이 여전히 많을 거라고 생각합니다. 그에 관련된 여러 글을 시리즈처럼 썼었는데, 하필 가장 중요한 부분 중 하나인 `4) 무료 데이터 베이스 만들기 및 데이터 이관하기` 편이 날아가게 되어서 심히 속상하지만, 그걸 발견한 지금 손놓고 있을 수는 없다고 생각하기때문에 얼른 밤이 늦었지만 그때의 기억을 되살려 더 완성도 높은 글을 작성 해 보려고 합니다.

그래도 방문자 분들에게 최신의 정보를 제공 할 수 있다는 걸로 위안을 삼아봅니다..

### M1 맥북과 오라클

오라클 서버를 네이티브로 돌리지 못한다고 m1으로 오라클 데이터베이스를 활용한 개발을 못하는 것은 아닙니다. 오히려 데이터베이스를 로컬로 띄우고 개발하는 일은 굉장히 적어지고 있습니다. 왠만한 DB는 도커를 이용해서 구동하고 있으며, 데이터베이스도 보통은 클라우드를 이용해서 가상으로 서버를 띄우는 일이 많습니다. 어차피 앞으로 하게 될 거, 미리 겪어보는것도 좋다고 생각합니다.

조금 어려울 수는 있는데, 너무 어렵지는 않습니다. 차근차근 따라하다 보면 누구든 가능하며, 막히는 부분이 있다면 편하게 댓글을 달아주세요. 아는 한 도와드리겠습니다.

## Oracle Cloud Free tier 가입

보통은 AWS를 많이들 사용하시는데요. 무료로 사용하고 싶은데, 과금의 위험에서 벗어나고 싶은 분들에게 Oracle을 추천합니다.

Amazon의 AWS, Microsoft의 Azure, Google의 Google Cloud등이 이미 주름잡고 있는 Cloud 시장에 후발 주자로 들어가서 그런지 꽤나 파격적인 정책을 펼칩니다. 심지어 Free Tier로 확실히 나누어 놨기 때문에 실수로라도 과금이 될 가능성이 없습니다.

처음 Free tier에 가입하면 $300 만큼의 Credit을 지급하기도 하니 충분히 해볼 만 한 가치가 있습니다.

혹시 회원 가입을 아직 하지 않은 분이 있을 수 있으니 가입 부터 글을 작성 해 보겠습니다. 혹시 이미 가입 한 분은 아래로 스크롤을 조금 내리셔도 괜찮습니다.

![image-20211119234306655](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211119234306655.png)

> https://www.oracle.com/cloud/free/

회원가입은 간단 합니다.

![image-20211119234613946](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211119234613946.png)

> 너무나 당연하게 이름, 이메일 정도 쓰고 이메일 확인 등의 절차가 있습니다.

이제 회원가입은 좀 건너 띄고 바로 데이터베이스 생성을 해 보겠습니다.

## Autonomous Database 생성

### DB 생성 준비

일단 처음 로그인 하면 아래와 같은 화면을 볼 수 있습니다.

![image-20211119235121781](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211119235121781.png)

>  여기에서 데이터 베이스 생성을 위해서는 우측 하단의 Create an ADW database를 선택 하면 됩니다.

![image-20211119235204514](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211119235204514.png)

>  혹은 좌측 상단의 햄버거 버튼을 누르고 Oracle Database -> Autonomous Databases 를 클릭 하고 들어가 보면

![image-20211119235234535](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211119235234535.png)

대략 이런 화면이 나옵니다. 저는 해당 데이터베이스를 접속하지 않은 지 오래되었다 보니 자동으로 접속이 서버가 종료된 상태 입니다.

![image-20211119235421644](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211119235421644.png)

일단 기존의 데이터 베이스를 삭제 하고 새로 만들어 보겠습니다. 여러분은 아무것도 없는 상태일 테니 다음 단계부터 함께 진행 해 주시면 됩니다.

### DB 생성

![image-20211119235536449](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211119235536449.png)

>  일단 `Create Autonomous Database` 를 클릭합니다.

![image-20211119235732918](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211119235732918.png)

> Display name은 익숙하고 편한 이름으로 직접 작성해주세요. 다른건 안바꿔도 됩니다.

![image-20211119235804155](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211119235804155.png)

> Always Free 가 체크 되어 있습니다.

DB 버전도 무료에서는 정해져있는 19c 버전만 사용 할 수 있습니다.

![image-20211119235826760](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211119235826760.png)

> 어차피 Always Free를 끄지도 못하니, 과금의 걱정은 전혀 없습니다. 무료 요금제를 위해 옵션을 켜주세요.

![image-20211119235937314](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211119235937314.png)

> 관리자 비밀번호를 정해줍니다.

![image-20211120000007473](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211120000007473.png)

> 여기도 따로 바꿔 줄 건 없습니다.

![image-20211120000052056](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211120000052056.png)

Advanced Option에도 따로 설정 해 줄 건 없습니다.

Create Autonomous Database를 클릭해 생성해줍니다.

![image-20211120000126138](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211120000126138.png)

> DB가 생성되고 있습니다.!!

![image-20211120000226805](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211120000226805.png)

> 조금만 더 기다리면 바로 준비가 됩니다.

### DB 접속

이제 DB에 접속을 해야 겠죠.

두번째 메뉴인 DB Connection을 클릭 해 봅니다.

![image-20211120000327420](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211120000327420.png)

Oracle Cloud Database 는 특이하게 전자지갑을 사용 합니다. Download wallet을 클릭해서 다운 받아주세요.

![image-20211120000411961](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211120000411961.png)

비밀번호를 입력 하고 다운 받아 줍니다.

![image-20211120000453878](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211120000453878.png)

이제 전자지갑 준비가 끝났습니다.

![image-20211120000511394](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211120000511394.png)

> SQL Developer를 실행 해 줍니다.

오랜만에 실행 해서 예전에 지웠나 했는데 다행히도 아직 있더라고요.

![image-20211120000552782](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211120000552782.png)

새로운 연결을 위해 좌측 상단의 십자가 모양을 클릭하고요

![image-20211120000633786](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211120000633786.png)

Connection Type 에서 Cloud wallet을 선택 해 줍니다.

그러고 우측의 의  Browse... 를 눌러 방금 다운 받은 전자지갑을 선택 해 줍니다.

![image-20211120000723376](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211120000723376.png)

![image-20211120000759471](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211120000759471.png)

Username 에는 admin을 입력 하고, Password 에는 아까 DB 생성할 때 입력한 관리자 비밀번호를 입력 합니다.

Services 에는 기본으로 설정된 high를 두고, 모두 입력 했으면 Test를 클릭해 테스트 해 봅니다.

좌측 하단에 Status: Success 가 기분 좋게 나옵니다.

준비가 되었으면 Connect를 클릭해서 접속 합니다.

![image-20211120001104499](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211120001104499.png)

예제를 위해 오라클의 국룰인 dept 테이블과 emp 테이블을 생성 해 보았습니다.

```sql
create table dept(  
  deptno     number(2,0),  
  dname      varchar2(14),  
  loc        varchar2(13),  
  constraint pk_dept primary key (deptno)  
);
create table emp(  
  empno    number(4,0),  
  ename    varchar2(10),  
  job      varchar2(9),  
  mgr      number(4,0),  
  hiredate date,  
  sal      number(7,2),  
  comm     number(7,2),  
  deptno   number(2,0),  
  constraint pk_emp primary key (empno),  
  constraint fk_deptno foreign key (deptno) references dept (deptno)  
);

```

<br><br>

이어서 몇몇 데이터도 넣습니다.

연습용으로 아주 좋은 데이터 입니다. 실제 학원에서도 보통 이 데이터로 실습을 하니 넣어두시면 좋습니다.

```sql
insert into dept
values(10, 'ACCOUNTING', 'NEW YORK');
insert into dept
values(20, 'RESEARCH', 'DALLAS');
insert into dept
values(30, 'SALES', 'CHICAGO');
insert into dept
values(40, 'OPERATIONS', 'BOSTON');
 
insert into emp
values(
 7839, 'KING', 'PRESIDENT', null,
 to_date('17-11-1981','dd-mm-yyyy'),
 5000, null, 10
);
insert into emp
values(
 7698, 'BLAKE', 'MANAGER', 7839,
 to_date('1-5-1981','dd-mm-yyyy'),
 2850, null, 30
);
insert into emp
values(
 7782, 'CLARK', 'MANAGER', 7839,
 to_date('9-6-1981','dd-mm-yyyy'),
 2450, null, 10
);
insert into emp
values(
 7566, 'JONES', 'MANAGER', 7839,
 to_date('2-4-1981','dd-mm-yyyy'),
 2975, null, 20
);
insert into emp
values(
 7788, 'SCOTT', 'ANALYST', 7566,
 to_date('13-JUL-87','dd-mm-rr') - 85,
 3000, null, 20
);
insert into emp
values(
 7902, 'FORD', 'ANALYST', 7566,
 to_date('3-12-1981','dd-mm-yyyy'),
 3000, null, 20
);
insert into emp
values(
 7369, 'SMITH', 'CLERK', 7902,
 to_date('17-12-1980','dd-mm-yyyy'),
 800, null, 20
);
insert into emp
values(
 7499, 'ALLEN', 'SALESMAN', 7698,
 to_date('20-2-1981','dd-mm-yyyy'),
 1600, 300, 30
);
insert into emp
values(
 7521, 'WARD', 'SALESMAN', 7698,
 to_date('22-2-1981','dd-mm-yyyy'),
 1250, 500, 30
);
insert into emp
values(
 7654, 'MARTIN', 'SALESMAN', 7698,
 to_date('28-9-1981','dd-mm-yyyy'),
 1250, 1400, 30
);
insert into emp
values(
 7844, 'TURNER', 'SALESMAN', 7698,
 to_date('8-9-1981','dd-mm-yyyy'),
 1500, 0, 30
);
insert into emp
values(
 7876, 'ADAMS', 'CLERK', 7788,
 to_date('13-JUL-87', 'dd-mm-rr') - 51,
 1100, null, 20
);
insert into emp
values(
 7900, 'JAMES', 'CLERK', 7698,
 to_date('3-12-1981','dd-mm-yyyy'),
 950, null, 30
);
insert into emp
values(
 7934, 'MILLER', 'CLERK', 7782,
 to_date('23-1-1982','dd-mm-yyyy'),
 1300, null, 10
);
```

<br><br>

전부 데이터를 넣었으니 간단한 쿼리를 테스트 해 봅니다.

```sql
select emp.*, dept.dname
from emp
join dept on (emp.deptno = dept.deptno);

```



![image-20211120001641950](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211120001641950.png)

아주 잘 되네요. 만족 스럽습니다.

## 데이터 이관

이번에는 혹시 기존에 사용하고 있던 데이터베이스가 있는 분들을 위해서 새로운 서버로 옮겨 오는 방법을 알려드리겠습니다.

여러가지 방법이 있는데 보통 DB 규모가 크면 덤프를 하는데요, 아직 보통은 DB가 작은 분들이 대부분일 테니 그냥 쉽게 하는 방법을 알려드리겠습니다.

![image-20211120001827680](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211120001827680.png)

> DB는 이 gaia 에서 옮겨 올 예정입니다.

백업 보내는 컴퓨터와 백업 받는 컴퓨터가 달라도 상관 없습니다. 지금부터는 백업 하는 컴퓨터에서 작업 해 주시면 됩니다. Windows 피시여도 상관 없습니다.

![image-20211120001909201](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211120001909201.png)

우측 상단의 Tools 에 보면  Database Export가 있습니다. 클릭해줍니다.

![image-20211120001935529](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211120001935529.png)

> 제일 먼저 Connection에 추출할 DB를 선택 합니다. 여기에서는  gaia를 선택 합니다.

![image-20211120002056601](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211120002056601.png)

> Show Schema 체크박스를 해지해 주고, 저장될 파일 경로만 Browse 눌러 찾기 쉬운 장소로 선택 해 줍니다.
>
> 준비 되었으면 next> 를 클릭합니다.

![image-20211120002205565](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211120002205565.png)

> 여기에서는 딱히 바꿔줄 게 없습니다. Next> 를 누릅니다.

그 다음 Specify Objects나 Specify Data도 특별히 바꾸지 말고 Next를 눌러 줍니다.

![image-20211120002254035](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211120002254035.png)

이제 여기에서 Finish 를 클릭 합니다.

![image-20211120002305805](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211120002305805.png)

부지런히 백업이 됩니다.

솔직히 이게 sql insert문으로 만드는 과정과 실제  insert 치는 부분이 느려서 .sql 로 추출하는 건 잘 안하는데요, 쉽기 때문에 데이터 양이 적은 초보때만 이렇게 합니다.

![image-20211120002622140](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211120002622140.png)

2분 정도 걸려서 드디어 끝났네요. 저는 백업을 네트워크를 통해 했기 때문에 느렸지만, 백업 하고자 하는 PC에서 추출 했다면 정말 금방 끝났을 듯 하네요.

해당 메시지는 추출은 정상적으로 끝났지만, gaiadb.sql 파일이 너무 길어서 sql developer에서 열지 못했다는 메시지 입니다. 저렇게 떴다고 하시 해야 하나 걱정 하지 않으셔도 됩니다.

<br><br> 이제 Visual Studio Code를 이용해 백업 한 파일을 열어봅니다.

![image-20211120002718809](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211120002718809.png)

총 7천줄이 조금 넘네요 

이제 DB를 이관할 데이터 베이스로 와서 모두 붙여넣기 합니다. 저는 이제 아까 만든 든  Oracle cloud에 넣습니다.

![image-20211120002852363](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211120002852363.png)

몽땅 붙여넣기 하고 `Ctrl + Enter` 를 눌러 바로 실행합니다.

![image-20211120002949256](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211120002949256.png)

열심히 자료가 들어가고 있습니다. 생각보다 오래 걸리지 않습니다. insert 되는 동안에는 인터럽트 하지 않게 조심하세요.

![image-20211120003048175](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oracleCloud.assets/image-20211120003048175.png)

> 이제 모두 끝났습니다.

20초 정도 걸린 것 같습니다. Oracle Cloud 데이터베이스의 Table 안에 GAIA에서 가져온 모든 테이블들이 잘 삽입 되어 있는 것이 확인 됩니다.!!

## 마치며

### 마침글

무료 데이터베이스라는데 정말 말도 안되게 좋은 건데, 공짜로 이렇게 쓰게 해주니 고마울 따름입니다.

저는 학원 다닐때 맥북 사고, 데이터베이스를 설치하지 못하니 집에 남는 윈도우 노트북에 Oracle 서버를 설치 하고, 공유기 포트포워딩으로 1521 포트를 열어서 학원에서든 어디서든 그 데이터베이스만 사용했던 기억이 나네요. 이렇게 오라클에서 무료로 데이터베이스를 제공해 주기 때문에 사실 그럴 필요가 없었지만 그때는 할 줄을 몰랐습니다.

이렇게 DB를 로컬이 아닌 인터넷 상에 구축 해 두면 또 장점이, 팀원들과 한 DB로 함께 개발을 할 수 있다는 점입니다.

비록 m1 맥북을 구매하셔서 오라클을 쉽게 사용하진 못했지만, 여기까지 오셨다면 그 위기가 기회가 되어서 남들보다 훨씬 더 좋은 데이터베이스 활용을 하고 있다는 자부심을 가져도 된다고 말씀드리고 싶습니다.

수고 정말 많으셨습니다. 

### 추가로

DB를 실제 프로젝트와 연결 해서 사용하고 싶다면, Oracle Cloud 전자지갑을 어플리케이션과 연결하는건 기존의 방법과 꽤나 차이가 있습니다. 난이도가 높기 때문에 어느 정도 익숙해진 후에 시도 해 보시길 권장드리며, 국비 학원 기준으로는 최소 초급, 중급 프로젝트가 끝난 후에 진행해보시는게 좋습니다. 3 개월 차 까지는 어려울 가능성이 높습니다.

도전할 준비가 되었다면 아래의 링크를 클릭해주세요!

> [Mac) Oracle Cloud FreeTier 이용해 웹 어플리케이션 배포하기 6) 오라클 전자지갑 사용해 프로젝트와 연결하기](https://shanepark.tistory.com/207) 
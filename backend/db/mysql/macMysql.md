# Mac) MYSQL 설치 및 초기 설정

## 설치

### Homebrew

 HOMEBREW 패키지 설치를 이용해 설치하는게 가장 간단하고, 나중에 관리하기도 좋습니다. 

사실 요즘엔 데이터베이스를 도커에 올리고 볼륨을 따로 빼놓는게 관리하기 좋다고는 생각이 들지만, 데이터베이스를 막 배우기 시작할 때 갑자기 도커를 사용하려고 하면 난이도가 너무 확 뛰는 느낌이 있게 때문에 직접 설치하시는걸 처음엔 권장드립니다.		

아직 homebrew가 설치되어있지 않다면?

> Homebrew는 맥북의 패키지 관리 프로그램 입니다. 개발자라면 반드시 필요하며 배우 유용하니 아래 링크를 참고해서 먼저 설치 하고 진행 해 주세요.

[shanepark.tistory.com/45](https://shanepark.tistory.com/45)

<br><br>

자 이제 Homebrew가 이미 설치 되었다는 가정 하에 진행하겠습니다. 터미널을 켜고..

```bash
brew install mysql
```

을 입력하면 정말 간단하게 최신버전의 mysql을 설치할 수 있습니다.

<br><br>

설치가  완료되었으면, 설치된 mysql 의 버전을 확인해봅니다.

```bash
mysql -V
```

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/mysql/macMysql.assets/img.png)

<br><br>

8.0.23 버전이 설치되었습니다. 아래 명령어를 입력해서 서버를 켜줍니다.

```
mysql.server start
```

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/mysql/macMysql.assets/img-20211111193712542.png)

<br><br>

## 설정

MYSQL 서버가 정상적으로 시작되었다면, 이제 초기 설정을 해줘야 합니다. 아래 명령어를 입력해 설정할 수 있습니다.

```bash
mysql_secure_installation
```

<br><br>

### 비밀번호

처음에는 비밀번호 복잡성 유무에 대한 질문이 나옵니다.  복잡한 비밀번호를 하려면 YES , 아니면 NO 라고 입력하면 됩니다.

저는 NO 를 선택했습니다.

<br><br>

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/mysql/macMysql.assets/img-20211111193712499.png)

```
New password:

Re-enter new password:
```

비밀번호를 입력하고, 재입력 하면 비밀번호가 설정됩니다.

<br><br>

### 익명유저

```
By default, a MySQL installation has an anonymous user,
allowing anyone to log into MySQL without having to have
a user account created for them. This is intended only for
testing, and to make the installation go a bit smoother.
You should remove them before moving into a production
environment.

Remove anonymous users? (Press y|Y for Yes, any other key for No) : Yes
```

MYSQL 에는 기본 설정으로 익명 유저를 만드는데요, 제거를 원한다면 YES를 입력하면 됩니다.

<br><br>

### root 접속 권한

```
Normally, root should only be allowed to connect from
'localhost'. This ensures that someone cannot guess at
the root password from the network.

Disallow root login remotely? (Press y|Y for Yes, any other key for No) : 
```

localhost 에서만 root 로 접속할 수 있는지, 아니면 외부에서도 root 로 접속 할 수 있게 할지에 대한 질문입니다.

원격에서 root계정 접속이 불가능하게 하려면 YES, 아니면 NO를 입력합니다. yes 를 추천합니다

<br><br>

```
By default, MySQL comes with a database named 'test' that
anyone can access. This is also intended only for testing,
and should be removed before moving into a production
environment.

Remove test database and access to it? (Press y|Y for Yes, any other key for No) : yes

- Dropping test database...
Success.

 - Removing privileges on test database...
Success.
```

테스트 database를 삭제할지에 대한 질문입니다. yes 를 입력하면 test 데이터베이스를 모두 삭제합니다.

<br><br>

```
Reloading the privilege tables will ensure that all changes
made so far will take effect immediately.

Reload privilege tables now? (Press y|Y for Yes, any other key for No) : yes
```

 <br><br>

이상으로 초기 설정이 완료되었습니다. 언제든지 아래 명령을 입력해서 다시 설정할 수 있습니다.

```
mysql_secure_installation
```

 <br><br>

## 접속

mysql 에 접속하려면 아래 명령어를 입력하고, 비밀번호 입력하라는 메시지가 나올때 입력 해주면 됩니다.

```
> mysql -u root -p
```

<br><br>

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/mysql/macMysql.assets/img-20211111193712479.png)

정상적으로 모든 셋팅이 완료되었다면 이렇게 접속이 잘 됩니다. exit로 mysql을 종료시킬 수 있습니다.

<br><br>

### 서버종료

나중에 서버를 끄고 싶을때는 terminal에서 아래와 같이 입력하시면 됩니다.

```
mysql.server stop
```

<br><br>

## 마치며

추가로, conda를 사용중이라면 후에 PyMySQL 을 설치할때 terminal에서 pip install PyMySQL  로 pymysql 을 설치하지 마시고

conda install pymysql 을 입력하셔서 설치하는게 좀 더 안전하다고 생각합니다. 사실 `pip`도 conda로 실행될 가능성이 높긴 하지만..

 <br><br>

이제 mysql에서 터미널 만으로는 데이터베이스 관리가 힘드니, Sequel Pro를 추가로 설치해서 사용하시면 됩니다!

Sequel Pro 설치법은 아래 링크를 확인해주세요.

[shanepark.tistory.com/43](https://shanepark.tistory.com/43)

수고하셨습니다.
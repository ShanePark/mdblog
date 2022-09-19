# Mac) MYSQL 설치 및 초기 설정

## 설치

### Homebrew

HOMEBREW 패키지 설치를 이용해 설치하는게 가장 간단하고, 나중에 관리하기도 좋습니다. 

사실 요즘엔 데이터베이스를 도커에 올리고 볼륨을 따로 빼놓는게 관리하기 좋다고는 생각이 들지만, 데이터베이스를 막 배우기 시작할 때 갑자기 도커를 사용하려고 하면 난이도가 너무 확 뛰는 느낌이 있게 때문에 직접 설치하시는걸 처음엔 권장드립니다.		

**아직 homebrew가 설치되어있지 않다면?**

> Homebrew는 맥북의 패키지 관리 프로그램 입니다. 개발자라면 반드시 필요하며 배우 유용하니 아래 링크를 참고해서 먼저 설치 하고 진행 해 주세요. [shanepark.tistory.com/45](https://shanepark.tistory.com/45)

자 이제 Homebrew가 이미 설치 되었다는 가정 하에 진행하겠습니다. 터미널을 켜고. 아래와 같이 입력 합니다.

```bash
brew install mysql
```

그러면 정말 간단하게 최신버전의 mysql을 설치할 수 있습니다.

설치가  완료되었으면, 설치된 mysql 의 버전을 확인해봅니다.

```bash
mysql -V
```

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/mysql/macMysql.assets/img.png)

8.0.23 버전이 설치되었습니다. 아래 명령어를 입력해서 서버를 켜줍니다.

```
mysql.server start
```

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/mysql/macMysql.assets/img-20211111193712542.png)

서버가 켜지고 SUCCESS! 라고 나옵니다.

## 설정

MYSQL 서버가 정상적으로 시작되었다면, 이제 초기 설정을 해줘야 합니다. 아래 명령어를 입력해 설정할 수 있습니다.

```bash
mysql_secure_installation
```

### 비밀번호

![image-20220919203004955](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/mysql/macMysql.assets/image-20220919203004955.png)

처음에는 비밀번호 복잡성 유무에 대한 질문이 나옵니다.  복잡한 비밀번호를 하려면 YES , 아니면 NO 라고 입력하면 됩니다.

저는 NO 를 선택했습니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/mysql/macMysql.assets/img-20211111193712499.png)

이번에는 원하는 비밀번호를 입력 하라고 합니다. 설정할 비밀번호를 입 력 해 주세요.

```
New password:

Re-enter new password:
```

비밀번호를 입력하고, 재입력 하면 비밀번호가 설정됩니다.

### 익명유저

![image-20220919203103544](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/mysql/macMysql.assets/image-20220919203103544.png)

MYSQL 에는 기본 설정으로 익명 유저를 만드는데요, 제거를 원한다면 YES를 입력하면 됩니다.

### root 접속 권한

![image-20220919203120725](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/mysql/macMysql.assets/image-20220919203120725.png)

localhost 에서만 root 로 접속할 수 있는지, 아니면 외부에서도 root 로 접속 할 수 있게 할지에 대한 질문입니다.

원격에서 root계정 접속이 불가능하게 하려면 Y, 아니면 그 외 아무 키를 입력합니다. 외부에서 접속 하려면 y 를 입력 해주세요.

![image-20220919203212840](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/mysql/macMysql.assets/image-20220919203212840.png)

테스트 database를 삭제할지에 대한 질문입니다. yes 를 입력하면 test 데이터베이스를 모두 삭제합니다.

![image-20220919203230996](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/mysql/macMysql.assets/image-20220919203230996.png)

마지막으로 privilege 테이블들을 reload 하면 지금까지 설정한 내용이 즉각 적용됩니다. Y를 입력 해 줍니다.

![image-20220919203323448](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/mysql/macMysql.assets/image-20220919203323448.png)

이상으로 초기 설정이 완료되었습니다. 언제든지 아래 명령을 입력해서 다시 설정할 수 있습니다.

```bash
mysql_secure_installation
```

## 접속

mysql 에 접속하려면 아래 명령어를 입력하고, 비밀번호 입력하라는 메시지가 나오면 비밀번호를 입력 해주면 됩니다.

```bash
 mysql -u root -p
```

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/mysql/macMysql.assets/img-20211111193712479.png)

정상적으로 모든 셋팅이 완료되었다면 이렇게 접속이 잘 됩니다. `exit`로 mysql을 종료시킬 수 있습니다.

### 서버종료

나중에 서버를 끄고 싶을때는 terminal에서 아래와 같이 입력하시면 됩니다.

```
mysql.server stop
```

## 마치며

이제 mysql에서 터미널 만으로는 데이터베이스 관리가 힘드니, Sequel Pro나 Dbeaver 등의 데이터베이스 클라이언트를 설치 하시면 됩니다.

Sequel Pro 설치법은 아래 링크를 확인해주세요.

[shanepark.tistory.com/43](https://shanepark.tistory.com/43)

수고하셨습니다.
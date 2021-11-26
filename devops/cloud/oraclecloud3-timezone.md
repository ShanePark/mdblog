# Mac) Oracle Cloud FreeTier 이용해 웹 어플리케이션 배포하기 3) 타임존 문제 해결. timezone region not found , java.net.SocketException: Permission denied

| 관련 글 목록                                                 |
| ------------------------------------------------------------ |
| [Mac) Oracle FreeTier 이용해 웹 어플리케이션 배포하기 1)인스턴스 만들고 접속하기.](https://shanepark.tistory.com/170) |
| [Mac) Oracle FreeTier 이용해 웹 어플리케이션 배포하기 2) 톰캣 설치하기 및 배포 + 외부 접속 허용](https://shanepark.tistory.com/171) |
| [Mac) Oracle FreeTier 이용해 웹 어플리케이션 배포하기 3) 타임존 문제 해결](https://shanepark.tistory.com/172) |
| [Mac) Oracle FreeTier 이용해 웹 어플리케이션 배포하기 4) 무료 데이터 베이스 만들기 및 데이터 이관하기](https://shanepark.tistory.com/173) |
| [Mac) Oracle Cloud FreeTier 이용해 웹 어플리케이션 배포하기 5) 톰캣 도메인 연결해 배포하기](https://shanepark.tistory.com/174) |
| [Mac) Oracle Cloud FreeTier 이용해 웹 어플리케이션 배포하기 6) 오라클 전자지갑 사용해 프로젝트와 연결하기](https://shanepark.tistory.com/207) |

위 2번 글에 이어지는 내용 이지만, 이미 배포는 했지만 DB쪽에 문제가 발생한 분들이라면 문제 이전글을 따로 확인 하지 않으셔도 무방 합니다.

## Intro

여태 집에있는 laptop 노트북 (windows)를 서버 컴퓨터로 활용 해 왔습니다. 집에 전기를 워낙 안쓰다보니 월 6천원 가량밖에 안나와서 그냥 서버를 쭉 켜 왔는데, 이제 학원도 끝났고 집에서 서버 돌리는 노트북이 linux가 아니다 보니 불편한 점이 제법 많아서. 이번에 노트북을 linux로 새로 설치하든 운영하는 서버들을 모두 cloud로 올리든 둘 중 하나는 해야겠다고 생각 했습니다.

AWS는 free tier를 사용 한다고 해도 예상치 못한 과금의 함정이 종종 있는데, Oracle Cloud 같은 경우에는 애초에 카드 등록을 할 필요도 없기 때문에 Oracle을 통해 서버를 구축 했습니다. 기존에 배포하던 프로젝트들을 war파일 그대로 옮겨서 배포만 하면 될 거라고 생각했는데 이상하게도 Ubuntu로 실행한 tomcat 서버에서는 DB와의 연결에 문제가 발생했습니다.

특히, 핵심 에러를 발견 할 수 있었는데요

### 1. timezone region not found

```
Caused by: org.springframework.jdbc.CannotGetJdbcConnectionException: Could not get JDBC Connection; nested exception is java.sql.SQLException: Cannot create PoolableConnectionFactory (ORA-00604: error occurred at recursive SQL level 1
ORA-01882: timezone region not found
```

>  지금부터 해결 해 보려고 합니다.

해당 에러는 Oracle에 설정된 Timezone과 서버의 Timezone이 일치하지 않기 때문에 발생하는 에러 입니다.

인스턴스의 타임존, OS의 타임존, Tomcat의 타임존, DB의 타임존 네 타임존이 맞물려 돌아가야 합니다. 

<br><br>

linux의 날짜와 시간은 간단하게 date 명령어로 확인 할 수 있습니다.

```zsh
date
```

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/ devops/cloud/oraclecloud3-timezone.assets/img-20211126221847459.png)

네. 리눅스는 기본적으로 UTC 타임존으로 설정 되어 있습니다. 시간대를 변경 해 줘야 합니다.

다음 명령어로 seoul의 time zone을 확인 할 수 있습니다.

```xml
timedatectl list-timezones | grep Seoul
```

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/ devops/cloud/oraclecloud3-timezone.assets/img-20211126221847397.png)



Asia/Seoul로 하면 된다고 합니다.

다음 명령어로 timezone을 변경해 줍니다

```xml
sudo timedatectl set-timezone Asia/Seoul
```

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/ devops/cloud/oraclecloud3-timezone.assets/img-20211126221847421.png)



이제 다시 date 명령어를 입력 하면, 한국 시간으로 잘 적용 된 것을 확인 할 수 있습니다.

이제 서버를 다시 켜 보겠습니다.

### 2. java.net.SocketException: Permission denied

일단 서버를 켰을때 혹시 저처럼 java.net.SocketException: Permission denied 에러가 나오면서 서버가 제대로 켜지지 않는다면.

Linux가 일반 사용자에게 wellknown port 사용 권한을 주지 않기 때문입니다.  관리자 계정으로 서버를 켜주세요.

```xml
 sudo ./startup.sh
```

로 간단하게 sudo 권한으로 서버를 켤 수 있습니다.

### 확인

서버를 켜서, 제가 배포한 어플리케이션에  로그인을 시도하니

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/ devops/cloud/oraclecloud3-timezone.assets/img-20211126221847499.png)

보이는 것 처럼, 문제 없이 DB와 연동되어 로그인 처리를 해냅니다. timezone 문제를 해결하기 전에는 로그인 부터 막혔었습니다.

## 마치며

간단하게 적었지만 정말 왜 `여기선 되는데 저기선 안되지?` 의 전형적인 문제로 참 고민을 많이 했습니다. 

같은 문제로 고민하는 분들에게 조금이나마 도움이 되었으면 합니다. 이상입니다.
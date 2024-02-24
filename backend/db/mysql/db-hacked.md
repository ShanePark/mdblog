# [MYSQL] 데이터베이스 털리다 - DB털이 예방하기

## Intro

이제 1년 좀 넘은 토이프로젝트가 하나 있다. 

와이프만 쓰라고 대충 만들었던건데 한명씩 한명씩 사용자가 늘어서 그래도 지금은 몇명이 쓰고 있고, 요구사항도 꾸준히 반영해서 처음에 비해 기능도 제법 들어갔다.

토이프로젝트라서 처음 시작할 때, 디비 비밀번호를 사용하면 안되는 아주 뻔한 암호로 만들었었는데.. 아침에 일어나 컴퓨터를 켜니 새벽 4시쯤에 Slack 알림이 와있었다.

![3](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/db/mysql/db-hacked.assets/3.webp)

[[SpringBoot] 에러 발생시 Slack으로 알림 보내기](https://shanepark.tistory.com/430) 를 적용해서 에러가 발생하면 슬랙 알림이 오게끔 적용을 해뒀었는데, 그덕에 문제가 있다는건 발견했다. 그런데 오류를 잘 보면 select 쿼리를 실패한건데. 디비 접속이 잘 안되나? 해서 로그를 살펴보니 테이블이 없단다.

> 위에 적힌 Request IP를 추적해보니 Hangzhou로 나온다. 아이피가 몇개 찍혀있는 걸 보니 차단을 대비해 돌려가며 쓰는 듯 하다

그래서 디비에 접속을 해보니 정말 테이블이 없고 README 테이블이 하나 생겨있다.

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/db/mysql/db-hacked.assets/1.webp)

> I have backed up all your databases. To recover them you must pay 0.008 BTC (Bitcoin) to this address: ..

오 비밀번호를 치고 들어왔구나..! DB를 전부 백업해두었으니 0.008 비트코인을 보내주면 복구해준단다.

디비털이범이 보기에도 막상 디비 총 사이즈가 별로 크지 않으니 적게 요구한 것 같은데 그래도 나름 50만원돈 된다.

> 응 너 그 돈 못받아~ㅜ

이로서 첫 외부 방문자가 기록되었다.

## 복구

복구는 사실 그닥 어렵지 않았는데 두가지 방법을 사용할 수 있다. 모두 블로그에 정리 해 두었으니 참고하면 된다.

- [[MYSQL Docker] 데이터베이스 매일 자동 백업하기](https://shanepark.tistory.com/448)
- [[mysql] 실수로 날린 데이터 binlog로 복구하기](https://shanepark.tistory.com/499)

> 물론 내 경우는 매일 매일 백업을 해두었고 작은 규모의 개인 프로젝트기 때문에 큰 문제가 없었던건데, 실제 운영하는 서비스에서 이런 일이 일어난다면 골치아플것이다. 덕분에 디비 사냥꾼들의 존재를 알았으니 앞으로 좀 더 신경써야겠다.

## 예방

### 1. 비밀번호 변경

일단 제일 먼저 털린 비밀번호를 변경하자

mysql에 접속해서

```bash
mysql -u root -p
```

모든 사용자 정보를 조회한다

```sql
SELECT User, Host FROM mysql.user;
```

그러고 사용자들의 패스워드를 변경해준다.

```sql
ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password';
```

예측하기 어렵고 가급적 뚫기 어려운 비밀번호를 만들자.

### 2. 외부 접속 차단

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/db/mysql/db-hacked.assets/2.webp)

사용자 목록을 보면 `dutypark` 사용자만 Host 가 `%`로 표시되어 있다. 아래의 세단계로 접속 아이피 주소를 제한할 수 있다.

1. 기존 사용자 권한 제거

기존의 `dutypark` 사용자를 제거하려면, 다음 명령어를 실행한다

```sql
DROP USER 'dutypark'@'%';
```

2. 특정 IP 주소에서 접속할 수 있는 `dutypark` 사용자 생성

이제 두 개의 IP 주소에서 접속할 수 있도록 `dutypark` 사용자를 다시 생성한다. 각 IP 주소에 대해 별도의 사용자를 생성해야 한다. 

물론 한개의 아이피만 설정해도 좋은데, 매번 다이나믹 포트포워딩을 하기엔 번거로워서 자주 개발을 하는 집 ip랑 운영 서버 이렇게 두개 추가하려 한다.

```sql
CREATE USER 'dutypark'@'192.168.1.100' IDENTIFIED BY '새로운비밀번호';
CREATE USER 'dutypark'@'192.168.1.101' IDENTIFIED BY '새로운비밀번호';
```

이렇게 하면, `dutypark` 사용자는 `192.168.1.100`과 `192.168.1.101` 이 두 IP 주소에서만 MySQL 데이터베이스에 접속할 수 있다.

3. 권한 부여

새로 생성한 사용자에게 필요한 권한을 부여해야 한다. 예를 들어, 모든 데이터베이스에 대한 모든 권한을 부여하려면 다음의 명령을 사용한다.

```sql
GRANT ALL PRIVILEGES ON *.* TO 'dutypark'@'192.168.1.100';
GRANT ALL PRIVILEGES ON *.* TO 'dutypark'@'192.168.1.101';
```

변경 사항을 적용하기 위해 권한을 새로고침한다

```sql
FLUSH PRIVILEGES;
```

이제 `dutypark` 사용자는 지정한 두 개의 IP 주소에서만 접속할 수 있으며, 다른 주소에서의 접속은 거부된다.

### 3. 기타

이 외에도 데이터베이스 접속 포트를 변경하고, 방화벽을 설정하는 등의 추가적인 조치를 하는 것이 좋다.

## 세줄요약

- 백업 잘 하자
- 암호 설정 잘 하자
- 접속 권한, 포트, 방화벽 관리 잘 하자
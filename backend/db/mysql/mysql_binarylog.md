# [mysql] 실수로 날린 데이터 binlog로 복구하기

## Intro

날렸다 테이블. 없어졌다 데이터.

다행히도 업무에 쓰던 DB는 아니다. 현재는 가까운 가족들만을 고객으로 우리들 끼리만 쓰고 있는 사이드 프로젝트인데, 개발서버랑 운영서버를 헷갈려서 데이터를 유실했다.

다행인건 매일 밤 12시에 자동으로 백업을 해왔다는 것.

> [[MYSQL Docker] 데이터베이스 매일 자동 백업하기](https://shanepark.tistory.com/448)

사건이 발생한건 오후 8시쯤이지만, 로그를 보니 오늘 오후 7시쯤 마지막 로그가 찍혀있었다. 00시 ~ 19시 사이의 데이터를 백업해보자.

## 데이터 복구

### 바이너리 로그 파일 찾기 

요즘엔 많이들 그렇겠지만, mysql을 도커로 돌고 있다. 그래서 도커 컨테이너에 관련된 내용도 조금 포함되는데, 도커를 사용하지 않다고 해도 아래 내용을 따라 진행하는데는 무리가 없을 것이다.

**도커 컨테이너 접속**

컨테이너명을 정확히 입력해준다.

```bash
# docker exec -it <컨테이너명> bash
docker exec -it mysql-container bash
```

**mysql에 root 로 접속**

````bash
 mysql -u root -p
````

**binary log 활성화 여부 확인**

바이너리 로그 활성화 여부를 확인한다. 활성화가 안되어있다면 다른 복구 전략을 고려해봐야 하겠지만, 특별히 설정을 하지 않았다면 대부분 `ON` 상태일 것이다.

```bash
SHOW VARIABLES LIKE 'log_bin';
```

![7](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/db/mysql/mysql_binarylog.assets/7.webp)

> Value 가 ON으로 되어 있다면 활성화 되어 있는 것.

**바이너리 로그 파일 목록 확인**

아래 명령어를 입력해 바이너리 로그 파일들의 목록을 확인한다.

```bash
SHOW BINARY LOGS;
```

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/db/mysql/mysql_binarylog.assets/2.webp)

**바이너리 로그 파일 위치 확인**

바이너리 로그 파일들의 목록을 확인했으니 이제 어디있는지를 확인해본다.

```bash
SHOW VARIABLES LIKE 'log_bin_basename';
```

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/db/mysql/mysql_binarylog.assets/1.webp)

해당 위치에 가서 확인해보면 먼저 확인했던 `binlog` 파일들이 보인다.

![3](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/db/mysql/mysql_binarylog.assets/3.webp)

### 바이너리 로그로 데이터 복구

이제 방금 찾아낸 바이너리 로그 파일과 `mysqlbinlog` 를 이용해 데이터를 복구해내면 된다.

해당 컨테이너에 `mysqlbinlog` 가 설치되어 있다면 훨씬 편했겠지만, 최소의 데이터만 가볍게 담아내는 컨테이너 특성상 설치되어 있지 않았다. 

일단 binlog 파일을 호스트로 복사해서 진행해보자.

```bash
docker cp dutypark-db:/var/lib/mysql/binlog.000079 .
docker cp dutypark-db:/var/lib/mysql/binlog.000080 .
```

이후에도 scp로 파일을 또 복사하는 과정을 거치긴 했지만 해당 파일들을 맥북으로 무사히 가져올 수 있었다.

![4](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/db/mysql/mysql_binarylog.assets/4.webp)

> 도커 파일을 호스트로 복사해온 상태

맥북에서는 아래의 명령어로 간단하게 mysql을 설치할 수 있다.

```bash
brew install mysql
```

이제 mysqlbinlog 프로그램을 이용해 분석이 가능하다

```bash
mysqlbinlog ~/Downloads/binlogs/binlog.000079
```

![5](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/db/mysql/mysql_binarylog.assets/5.webp)

> 사람이 눈으로 확인 할 수 있는 데이터가 되었다. 물론 컴퓨터도 알아 듣는다.

이제 백업이 필요한 시간 사이의 바이너리 로그를 sql 파일로 추출해내면 준비는 끝난다. 

전부 다 추출할 필요는 없고 범위를 선택 할 수 있는데, 매일 00시 00분에 백업을 해두었기 때문에 00시부터 19시 사이의 binlog를 sql로 추출해본다.

> `binlog.000079` 파일과 `binlog.000080` 파일을 둘 다 분석해보니, 해당 시간 사이에는 binlog.000080 에만 로그가 있었다. 그래서 아래의 명령어만 실행했다.

```bash
mysqlbinlog --start-datetime="2024-02-05 00:00:00" --stop-datetime="2024-02-05 19:00:00" binlog.000080 > queries.sql
```

![6](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/db/mysql/mysql_binarylog.assets/6.webp)

> 추출해낸 파일

해당 파일은 일반적인 SQL로 보이지는 않지만, mysql에 그대로 쿼리처럼 복사 붙여넣기 한 후 실행하면 된다.

내용이 많지 않다면 바로 실행해도 되고, 아래처럼 sql파일 통째로 실행해도 된다. 해당 쿼리를 실행하는데는 권한이 필요하니 권한문제가 생기면 root로 실행하면 된다.

```bash
mysql -u root -p dbname < queries.sql
```

이후 데이터베이스를 확인해보면 해당 시간 사이에 일어났던 이벤트들이 모두 적용되어 있는 것을 확인할 수 있었다.

물론 애초에 컨테이너가 아니었다면 아래와 같은 싱글라인으로도 간단히 복구가 가능하다.

```bash
mysqlbinlog --start-datetime="2024-02-05 00:00:00" --stop-datetime="2024-02-05 19:00:00" binlog.000080 | mysql -u root -p
```

DB를 날려먹는 경험은 언제나 새롭고 항상 짜릿하다.

끝

## References

- https://dev.mysql.com/doc/refman/8.0/en/mysqlbinlog.html
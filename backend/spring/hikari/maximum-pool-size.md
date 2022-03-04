# HikariCP) Connection Pool 설정 및 확인하기

## Intro

실제 사용하는 커넥션 수에 비해서 커넥션 풀을 넉넉하게 잡고 있었는데, 해당 DB를 여기저기서 다 사용하다 보니 커넥션 풀을 조금 타이트하게 가져가 달라는 요청을 받았습니다.

Connection Pool을 조절하고, 실제로 DB에서 커넥션을 몇개나 물고 있는지 확인 해 보도록 하겠습니다.

## 설정

### application.yml

스프링 부트에서 Hikari 설정은 spring.datasource.hikari 에 합니다.

변경 전

```yaml
spring:
  datasource:
    type: com.zaxxer.hikari.HikariDataSource
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/postgres
    username: idr
    password: idr1234
```

> 히카리 설정을 따로 주기 전 입니다.

변경 후

```yaml
spring:
  datasource:
    type: com.zaxxer.hikari.HikariDataSource
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/postgres
    username: idr
    password: idr1234
    hikari:
      maximum-pool-size: 20
```

> 최대 커넥션 풀을 20개로 정해주었습니다.

```yaml
maximum-pool-size: ${POSTGRES_MAX_POOL_SIZE:20}
```

> maximum-pool-size를 환경 변수로 따로 빼고싶다면 위와 같이 설정 할 수 있습니다.

그랬을 경우에는 docker-compose 등을 사용 한다면

```yaml
environment:
	- POSTGRES_MAX_POOL_SIZE=50
```

> 위와 같이 간편하게 설정값을 코드 밖으로 빼낼 수 있습니다.

## 확인

### Connection Pool 확인

이제 설정이 잘 되었는지 확인을 해 보겠습니다.

커넥션 풀에 대한 로그는 DEBUG 레벨로 남고 있는데요.. 일단 아래와 같이 로깅레벨을 전부 debug로 바꿔보았습니다.

```yaml
logging:
  level:
    root: debug
```

그러고 어플리케이션을 실행하면.. 고통스러울 만큼 로그가 엄청나게 많이 찍히는데요.. 그중

![image-20220304172523125](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/hikari/maximum-pool-size.assets/image-20220304172523125.png)

> 맨 위에 보면 HikariPool에 대한 로그가 보입니다.

일단 설정대로 총 20개의 Pool이 등록이 되어 있습니다. 로그가 너무 많이 찍혀서 줄여보려고 하는데 Hikari 쪽 패키지가 `c.z.hikari.pool.HikariPool` 로 찍힙니다. 

여기서 각각 c와 z는 `com` , `zaxxer`의 약자 입니다. Hikari쪽만 로깅 레벨을 debug로 하도록 수정 해 줍니다.

```yaml
logging:
  level:
    com.zaxxer.hikari.pool.HikariPool: debug
```

이제 어플리케이션을 다시 구동 해 봅니다.

![image-20220304172732922](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/hikari/maximum-pool-size.assets/image-20220304172732922.png)

> 어플리케이션이 구동 되며 커넥션 풀에 커넥션들을 하나씩 등록 하고, 총 20개의 커넥션이 준비 된 것을 로그를 통해 확인 할 수 있습니다.

### Database 확인

이번에는 실제 jdbc가 데이터베이스에서 몇개의 커넥션을 물고 있는지 확인 해 보겠습니다.

저는 PostgreSQL을 사용중 이며, 본 DB에서는 아래의 명령어로 활성 커넥션 상태를 확인 할 수 있습니다.

```sql
select * 
from pg_stat_activity;
```

![image-20220304173035950](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/hikari/maximum-pool-size.assets/image-20220304173035950.png)

사용중인 커넥션들의 정보가 보입니다. 이 중에서 실제 어플리케이션이 사용중인 DB만 확인 해 보려면 

```sql
select count(*)
from pg_stat_activity
where application_name like '%JDBC%';
```

저는 이렇게 쿼리를 했습니다. application_name과 username 등을 적절하게 조건을 걸어 카운트 하면 됩니다.

![image-20220304173320058](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/hikari/maximum-pool-size.assets/image-20220304173320058.png)

>  확인 해 보니 딱 설정 한 만큼 커넥션을 물고 있습니다.

이상입니다.
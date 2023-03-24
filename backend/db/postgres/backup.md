# postgres 전체 데이터를 sql 파일로 백업

## Intro

liquibase를 사용하여 데이터베이스 스키마를 관리하고 있습니다. 주로 PostgreSQL을 사용하고 있는데, 테스트를 수행할 때 별도의 외부 DB를 사용하지 않고 인메모리 DB를 이용하고자 했습니다. 

그런데 `gen_random_uuid`와 같은 일부 PostgreSQL 문법과 시퀀스 생성 등이 발목을 잡았습니다.

그래서 인메모리 DB로 테스트를 할 때는 liquibase를 사용하지 않고, `data.sql` 파일을 불러와서 DB스키마를 생성하려고 합니다. 이를 위해 먼저 liquibase를 사용하여 PostgreSQL 데이터베이스에 초기 데이터를 입력한 다음, 이를 SQL로 추출하여 H2에서 사용 가능하게 편집하려고 합니다. 

liquibase로 변경 로그를 불러와 SQL로 추출할 수도 있지만, 수정해야 할 내용이 더 많아지고 복잡해지기 때문에 이 방법을 선택하였습니다.

## Backup

pg_dump 명령어를 이용해 간단하게 sql 파일로 백업해낼 수 있습니다.

```bash
# pg_dump -U [사용자 이름] -h [호스트 이름] [데이터베이스 이름] > [백업 파일 경로]
pg_dump -U sa -h localhost postgres > backup.sql
```

![image-20230324152200209](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/db/postgres/backup.assets/image-20230324152200209.png)

생각보다 오래 걸리지 않습니다. 확인을 해 보면

![image-20230324152334596](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/db/postgres/backup.assets/image-20230324152334596.png)

> backup.sql 파일 크기 2.0M

2.0M 의 백업 파일이 정상적으로 잘 생성된걸 확인 할 수 있습니다. 그런데 파일을 확인해보니 `COPY FROM stdin;` 이 덕지덕지 있어서 H2에서 사용하기가 너무 불편했습니다. insert문으로 바꿔줘야하는데 수작업으로 하기엔 양이 정말 많습니다. 

다행히도 옵션으로 제공합니다.

```bash
pg_dump -U sa -h localhost --inserts postgres > backup.sql
```

이렇게 하면 깔끔하게 INSERT INTO 문으로 나옵니다. 

이제 컨테이너 내부에서 호스트로 가져와서 사용하기만 하면 되겠습니다.

```bash
# docker cp [컨테이너 이름 또는 ID]:/path/to/file /host/path/to/file
docker cp testdb:/backup.sql .
```

![image-20230324152510751](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/db/postgres/backup.assets/image-20230324152510751.png)

> 호스트로 가져오기 성공

이렇게 추출된 SQL 파일을 수정해서 H2에서 사용하도록 변경 하였습니다. 

예약어라던가 약간의 문법 차이들이 있어 수정해야할게 꽤 많았는데 하나하나 에러 메시지를 확인하며 수정하니 인메모리 H2 데이터베이스를 활용해 테스트가 가능하게 되었습니다.

이상입니다.

 

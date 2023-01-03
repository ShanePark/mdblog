# 할 때마다 까먹어서 정리하는 PSQL 접속 방법

## Intro

가끔 필요한 일이 돌아왔다. 데이터베이스에 직접 접속해서 뭔가를 변경 해야 한다.

Docker 네트워크 내에서만 통신하고 외부로는 바인딩도, 포트 포워딩도 되어 있지 않으니, 번거롭게 다시 설정 하고 컨테이너 재시작 할 필요 없이 컨테이너 내부에 접속해서 바로 변경하면 되겠군. 

> 얼른 마칠 생각을 하며 PostgreSQL 이 떠 있는 컨테이너에 접속하는중

```bash
docker exec -it postgres_container bash
```

![image-20230103161516692](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/postgres/psql_help.assets/image-20230103161516692.png)

이제 psql을 써볼까나..?

> psql 명령어와 함께 손에 익은 옵션을 기계적으로 붙이는중

```bash
psql -U postgres
```

![image-20230103161946492](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/postgres/psql_help.assets/image-20230103161946492.png)

앗.. postgres 유저가 없다..? 참. 나는 다른 이름으로 유저명을 지정했지

> 유저명을 변경해 재시도 하는 중

```bash
psql -U my_user_name
```

![image-20230103162113808](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/postgres/psql_help.assets/image-20230103162113808.png)

이번에는 또 데이터베이스가 없고 하네. 이건 할 때마다 생각이 안 나는군 정말

## PSQL

기본적으로 psql 명령어는 옵션을 주지 않고 실행 하면 기본값인 `root` 사용자로 접속을 시도합니다. 또한, 서버 호스트는 기본 로컬 소켓, 포트는 기본 **5432** 등등 여러가지가 자동으로 설정됩니다. 

그렇기 때문에 설정이 바뀌어야 할 경우에는 적절 한 값으로 옵션을 직접 부여해서 접속 해 주어야 합니다.

![image-20230103162228035](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/postgres/psql_help.assets/image-20230103162228035.png)

> root로 시도중

 PSQL의 기본 사용법은 `psql [OPTION]... [DBNAME [USERNAME]]` 이며 다양한 옵션을 아래의 명령어로 손 쉽게 확인 할 수 있습니다.

```bash
psql --help
```

옵션 목록은 아래와 같습니다.

### 일반 옵션

- `-c, --command=COMMAND `   하나의 명령(SQL 또는 내부 명령)만 실행하고 끝냄
- `-d, --dbname=DBNAME`      연결할 데이터베이스 이름(기본 값: "root")
- `-f, --file=FILENAME`      파일 안에 지정한 명령을 실행하고 끝냄
- `-l, --list`               사용 가능한 데이터베이스 목록을 표시하고 끝냄
- `-v, --set=, --variable=NAME=VALUE`      psql 변수 NAME을 VALUE로 설정 (예, -v ON_ERROR_STOP=1)
- `-V, --version`            버전 정보를 보여주고 마침

### 입출력 옵션

- `-a, --echo-all`           스크립트의 모든 입력 표시
- `-b, --echo-errors`        실패한 명령들 출력
- `-e, --echo-queries`       서버로 보낸 명령 표시
- `-E, --echo-hidden`        내부 명령이 생성하는 쿼리 표시
- `-L, --log-file=FILENAME`  세션 로그를 파일로 보냄
- `-n, --no-readline`        확장된 명령행 편집 기능을 사용중지함(readline)
- `-o, --output=FILENAME`    쿼리 결과를 파일(또는 |파이프)로 보냄
- `-q, --quiet`              자동 실행(메시지 없이 쿼리 결과만 표시)
- `-s, --single-step`        단독 순차 모드(각 쿼리 확인)
- `-S, --single-line`        한 줄 모드(줄 끝에서 SQL 명령이 종료됨)

### 출력 형식 옵션

- `-A, --no-align`           정렬되지 않은 표 형태의 출력 모드
- `-F, --field-separator=STRING`
                             unaligned 출력용 필드 구분자 설정(기본 값: "|")
- `-H, --html`               HTML 표 형태 출력 모드
- `-P, --pset=VAR[=ARG]`     인쇄 옵션 VAR을 ARG로 설정(\pset 명령 참조)
- `-R, --record-separator=STRING`
                             unaligned 출력용 레코드 구분자 설정 (기본 값: 줄바꿈 문자)
- `-t, --tuples-only`        행만 인쇄
- `-T, --table-attr=TEXT`    HTML table 태그 속성 설정(예: width, border)
- `-x, --expanded`          확장된 표 형태로 출력
- `-z, --field-separator-zero`   unaligned 출력용 필드 구분자를 0 바이트로 지정
- `-0, --record-separator-zero`  unaligned 출력용 레코드 구분자를 0 바이트로 지정

### 연결 옵션

- `-h, --host=HOSTNAME`      데이터베이스 서버 호스트 또는 소켓 디렉터리 (기본값: "로컬 소켓")
- `-p, --port=PORT`         데이터베이스 서버 포트(기본 값: "5432")
- `-U, --username=USERNAME`  데이터베이스 사용자 이름(기본 값: "root")
- `-w, --no-password`        암호 프롬프트 표시 안 함
- `-W, --password`           암호 입력 프롬프트 보임(자동으로 처리함)

## 결론

`psql -U 유저명` 후에 공백을 하나 두고 혹은, `-d`나 `--dbname` 옵션으로 DB 이름을 지정해주면 원하는 결과를 얻을 수 있었습니다.

```bash
psql -U username databasename
```

사실 위의 명령도 풀어 쓰면 아래와 같습니다.

```bash
psql --dbname=my_db_name --host=localhost --port=5432 --username=my_user_name --no-password
```

![image-20230103163947367](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/postgres/psql_help.assets/image-20230103163947367.png)

앞으로는 postgres 접속이 필요 할 때 시간 낭비를 더 이상 하지 않기 위해 정리 해 보았습니다.

이상입니다.

### References

- https://www.postgresql.org/docs/current/app-psql.html
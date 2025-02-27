# PostgreSQL CVE-2025-1094 대응 마이그레이션

## Intro

최근 PostgreSQL에서 심각한 보안 취약점인 **CVE-2025-1094**가 발견되었다. 이 취약점은 특정 상황에서 SQL 인젝션 공격이 가능하도록 만드는 치명적인 문제로, `PQescapeLiteral()`, `PQescapeIdentifier()`, `PQescapeString()`, `PQescapeStringConn()` 같은 libpq 함수들이 따옴표 구문을 제대로 처리하지 못하는 데서 비롯되었다.

PostgreSQL **17.3, 16.7, 15.11, 14.16, 13.19 이전 버전**이 영향을 받으며, PostgreSQL 개발팀은 **2025년 2월 13일** 해당 취약점을 해결한 보안 패치를 발표했다.

![image-20250224151635863](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/db/postgres/postgres-migration.assets/1.webp)

> https://www.boho.or.kr/kr/bbs/view.do?bbsId=B0000133&pageIndex=1&nttId=71662&menuNo=205020

현재 진행 중인 프로젝트들도 이 취약점 영향을 받기 때문에 신속하게 PostgreSQL을 최신 버전(17.4)으로 마이그레이션했다.

기존에 사용중이던 Postgres의 버전이 10.x로 많이 낮아서 마이너버전 업데이트 만으로는 해결이 불가능했다.

------

## 일반적인 PostgreSQL 메이저 버전 마이그레이션

PostgreSQL은 마이너 버전(예: 16.1 → 16.7) 업데이트는 기존 데이터 디렉토리를 그대로 유지하면서 패키지만 교체하면 되지만, 메이저 버전(예: 13 → 17) 업그레이드는 데이터베이스를 새 버전에 맞게 변환해야 한다.

가장 간단한 방법은 **`pg_upgrade`** 도구를 사용하는 것이다.

### 1. 기존 데이터 백업

혹시 모를 문제에 대비하여 기존 데이터 디렉토리를 백업해둔다.

```bash
sudo systemctl stop postgresql
cp -r /var/lib/postgresql/13 /var/lib/postgresql/13_backup
```

### 2. 새 PostgreSQL 버전 설치

예를 들어, PostgreSQL 13에서 17로 업그레이드할 경우 새 버전을 설치한다.

```bash
sudo apt update
sudo apt install postgresql-17 -y
```

### 3. `pg_upgrade` 실행

기존 데이터 디렉토리(`/var/lib/postgresql/13/main`)를 새 데이터 디렉토리(`/var/lib/postgresql/17/main`)로 변환한다.

```bash
sudo -u postgres pg_upgrade \
  -d /var/lib/postgresql/13/main \
  -D /var/lib/postgresql/17/main \
  -b /usr/lib/postgresql/13/bin \
  -B /usr/lib/postgresql/17/bin \
  -U postgres
```

### 4. PostgreSQL 17 실행 및 기존 버전 제거

업그레이드가 완료되면 새로운 PostgreSQL을 실행하고, 기존 버전을 정리한다.

```bash
sudo systemctl start postgresql
sudo apt remove postgresql-13 -y
```

이렇게 하면 기존 데이터가 새 버전에 맞게 변환되면서 PostgreSQL을 최신 버전으로 마이그레이션할 수 있다.

------

## Docker Compose 환경

문제는 Docker Compose로 PostgreSQL을 운영하는 경우 `pg_upgrade`를 사용할 수 없다는 점이다.

컨테이너 기반 환경에서는 기본적으로 볼륨에 데이터가 저장되지만, 컨테이너 자체는 일회성이기 때문에 `pg_upgrade`처럼 로컬 경로 기반으로 동작하는 도구를 사용하기 어렵다.

그렇기 때문에 Docker 환경에서는 기존 데이터를 백업한 후, 새로운 컨테이너를 생성하여 데이터를 복원하는 방식으로 마이그레이션을 진행해야 한다.

### 1. 데이터 백업

```bash
docker compose exec -t myproject-db pg_dumpall -U myuser > backup.sql
```

### 2. 기존 볼륨 백업

볼륨을 삭제하기 전에 기존 볼륨 데이터도 안전하게 보관한다.

```bash
cp -r data/myproject-db-data data/myproject-db-data-backup
```

### 3. 기존 컨테이너 중지 및 제거

```bash
docker compose stop myproject-db
docker compose rm myproject-db
```

### 4. 기존 볼륨 제거

```bash
sudo rm -rf data/myproject-db-data
```

### 5. `docker-compose.yml` 수정

PostgreSQL 최신 버전을 사용하도록 변경한다.

```yaml
services:
  myproject-db:
    container_name: myproject_dev-db
    image: postgres:latest
    restart: unless-stopped
    networks:
      - myproject-network
    ports:
      - 5432:5432
    volumes:
      - ./data/myproject-db-data:/var/lib/postgresql/data
    environment:
      - POSTGRES_DB=${MYPROJECT_POSTGRES_SCHEMA}
      - POSTGRES_USER=${MYPROJECT_POSTGRES_USER}
      - POSTGRES_PASSWORD=${MYPROJECT_POSTGRES_PASSWORD}
      - TZ=${TZ}
```

### 6. PostgreSQL 재실행 및 버전 확인

```bash
docker-compose up -d
docker compose exec -it myproject-db psql --version
# psql (PostgreSQL) 17.4 (Debian 17.4-1.pgdg120+2)
```

> CVE-2025-1094 가 해결된 17.3 보다 높은걸 확인해야 한다.

### 7. 데이터 복원

데이터 양에 비례해 제법 오래 걸릴 수 있다.

```bash
cat backup.sql | docker compose exec -T myproject-db psql -U myuser -d postgres
```

### 8. 비밀번호 변경 및 재색인

- Postgres 14 버전부터 기본 인증 방식이 MD5 암호화에서 SCRAM-SHA-256 으로 변경되었기때문에 로그인이 안될 수 있다. 비밀번호를 유지하려고 해도 아래의 명령어로 한번 암호를 설정해주지 않으면 인증에 실패할 수 있다.

- Collation 처리 방식 변경으로 기존 인덱스가 깨질 가능성이 있으므로 재색인도 한번 해준다.

```sql
# 비밀번호 변경
ALTER USER myuser WITH PASSWORD 'mypassword1234';
# 재색인
REINDEX DATABASE myproject_db;
```

이제 DB에 접속하고, 어플리케이션도 구동해보며 마이그레이션이 잘 완료되었는지를 확인한다.

## 결론

- 일반적인 PostgreSQL 환경에서는 `pg_upgrade`를 사용하면 데이터 마이그레이션이 간단하지만
- Docker Compose 환경에서는 `pg_upgrade`를 사용할 수 없기 때문에 기존 데이터를 백업 후, 컨테이너를 재생성하여 복원하는 방식으로 마이그레이션을 수행해야 한다.
- 마이그레이션을 통해 PostgreSQL을 최신 버전으로 업데이트하고, CVE-2025-1094 취약점을 해결한다.
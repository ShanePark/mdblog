# Liquibase 변경사항을 sql 파일로 추출하기

## Intro

Liquibase를 사용하여 데이터베이스 스키마를 관리하며, 테스트용 데이터베이스 생성에 어려움을 겪었습니다. 

현재는 여러 외부 환경을 구축한 후 통합 테스트를 진행하고 있지만, 나중에 CI/CD를 고려할 때는 외부 환경에 의존하지 않고 독립적으로 테스트가 가능하도록 만들고 싶었습니다. 검색 엔진인 Apache Solr는 Mocking을 하고, Redis는 인메모리로 사용할 수 있어서 테스트에서는 그걸 사용하게끔 하고 나니 이제 데이터베이스만 남아있었거든요. 

제가 토이프로젝트등에서 하고 있는 것 처럼 H2 인메모리 DB를 사용하기로 했습니다. 그걸위해 먼저 PostgreSQL로 되어 있는 DB를 테스트에서는 H2로 마이그레이션 해야합니다.

하지만 운영 환경에서의 Liquibase를 그대로 실행할 경우 PostgreSQL에 의존적인 문법 등이 있어서 그걸 그대로 사용할 수는 없었습니다. 따라서, Liquibase의 change log를 활용해 SQL문을 추출해 보려고 합니다.

결론적으로는, Liquibase로 수년간 쌓인 change log를 SQL로 모두 추출하고 H2로 마이그레이션 하는건 변경해야할 게 너무나도 많았기 때문에 Postgres를 하나 새로 띄운 후 수정 사항을 적용해서 그 내용을 SQL로 추출하는 방법을 사용하였습니다. 

> 관심이 있으신분은 [postgres 전체 데이터를 sql 파일로 백업](https://shanepark.tistory.com/462) 을 확인해주세요.

어쨌든 그 과정에서 Liquibase CLI로도 모든 변경 사항을 SQL로 추출하는 기능을 사용해 보았기 때문에 이 방법을 기록해두려 합니다. 사실, Liquibase 동작 자체가 SQL을 만들어서 스스로 실행하는 것이기 때문에 SQL 파일로 추출이 가능한게 당연하긴 합니다.

## Liquibase CLI 설치

### Download

먼저 Liquibase를 다운로드 합니다. https://www.liquibase.com/download 에서 다운받을 수 있습니다.

Windows / MacOS / Linux / Unix 모두 사용 가능합니다.

![image-20230324105355943](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/db/liquibase/into_sql.assets/image-20230324105355943.png)

### Install

> 더 자세한 내용은 [Install Guide](https://docs.liquibase.com/start/install/home.html) 참고하세요.

다운로드한 tar 파일을 원하는 위치에 폴더를 만들어 압축을 풀어줍니다.

```bash
mkdir -p ~/Documents/utils/liquibase4.20 && tar -xvf liquibase-4.20.0.tar.gz -C ~/Documents/utils/liquibase4.20
```

Liquibase를 설치한 경로를 PATH에 등록해줍니다. 아래의 내용을 해당 Shell 설정 파일에(예: `~/.bashrc` 또는 `~/.zshrc`) 추가해주면 됩니다.

```bash
export PATH=$PATH:~/Documents/utils/liquibase4.20
```

이후 `source ~/.zshrc`를 실행하면 이제 Liquibase 명령어를 사용할 준비가 완료됩니다. 버전 확인 명령을 실행해봅시다.

```bash
liquibase --version
```

![image-20230324110545336](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/db/liquibase/into_sql.assets/image-20230324110545336.png)

> 정상적으로 설치 완료되었습니다.

## SQL 생성

이제 설치한 liquibase CLI를 이용해 SQL 파일을 생성해 보겠습니다. updateSQL 명령어를 사용하는데요, **변경 로그파일의 위치**와 **데이터베이스 접속정보**를 입력해줘야합니다.

```bash
liquibase --changeLogFile="./liquibase/changelog.xml" --url="jdbc:postgresql://localhost:54320/postgres" --username="sa" --password="sa" updateSQL > output.sql
```

위의 명령을 실행하면

![image-20230324111736071](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/db/liquibase/into_sql.assets/image-20230324111736071.png)

> 생각보다 빠르게 완료됩니다.

금방 `output.sql` 파일을 생성해줍니다.

실제로 데이터베이스에 해당 SQL이 적용되지는 않았고 쿼리문만 생성했기때문에 왜 접속 정보가 필요한지 궁금했는데요, 관련해 찾아보니 Liquibase가 특정 데이터베이스 플랫폼에 맞는 SQL을 생성하기 위해 DB 서버가 필요하다고 합니다. 

DB 서버를 실행하지 않으면 SQL 생성이 불가능 하더라고요.이제 `output.sql` 파일을 열어 보면, 적절한 SQL문이 생성되어 있는것이 확인됩니다.

놀라운건 H2 데이터베이스로 SQL을 생성 할 경우에는 실제 H2를 띄우지 않고도 가능했는데요, 궁금해서 Liquibase 설치폴더의 README.txt 파일을 읽어보니 Liquibase에 H2 Database가 포함되어있다고 하던데 그 이유인 것 같습니다. 그래서 아래와 같이 실행하면, 따로 H2를 띄우지 않더라도 H2 데이터베이스용 SQL을 생성해줍니다. 

```bash
liquibase --changeLogFile="./liquibase/changelog.xml" --url="jdbc:h2:mem:testdb" --username="sa" --password="" updateSQL > output.sql
```

다만, 특정 벤더에 종속적인 함수등을 직접 사용했다면 H2로 띄운다고 해서 그걸 알아서 고쳐주거나 하지는 않았습니다. 개인차가 있겠지만 상황에 따라 즉시 마이그레이션 가능한 경우도 있고, 변경해야할게 너무 많아서 SQL 을 뽑아내는게 마이그레이션에서 의미가 없을 수도 잇겠습니다.

지금까지 Liquibase 변경사항을 SQL 파일로 추출하는 방법에 대해 알아보았습니다.

이상입니다. 

**References**

- https://docs.liquibase.com/
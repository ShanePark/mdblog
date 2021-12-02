# Postgres) 계층형 쿼리작성 및 csv로 결과 저장하기

> 도커 컨테이너에 있는 Postgresql 데이터베이스에 접근해서 psql로 계층형 쿼리 조회하고 그  결과를 csv 파일로 저장해서 로컬로 가져오기

## 계층형 쿼리

상품 p 가 있고, 아이디는 product_id, 상위 product는 parent_product_id 라고 할 때,

아래와 같이 계층형 쿼리를 작성 할 수 있습니다.

```sql
with recursive search_product(product_id) as
(
	select p.*
	from product p 
	where product_id =21306
	
	union all
	
	select p.*
	from product p, search_product sp
	where k.parent_product_id = sp.product_id
)
select * from search_product;
```

> 해당 쿼리 실행시 product의 모든 컬럼들을 21306번 상품 부터, 하위 상품 순으로 보여줍니다. 첫번째 `where` 절에 product_id로 조건을 주지 말고, parent_product_id 가 null인 조건을 부여한다면, 최상위 상품으로 부터 그 하위 상품들을 조회 해 올 수 있습니다.



## CSV 저장

### docker

저는 데이터베이스가 docker에서 실행 중 이기 때문에 적당할 경로를 찾기 위해 `ls -al`를 입력 해 보았습니다. 그 전에 mkdir로 새 폴더를 만들려고 시도 해 보았지만 컨테이너 내부에서 폴더 만들기가 안되었습니다.

![image-20211201150200838](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/postgres/hierarchyCsv.assets/image-20211201150200838.png)

`tmp` 폴더가 유일하게 모든 권한이 열려 있네요. 저 폴더에 저장하면 될 듯 합니다.

### csv 파일 생성

COPY 명령어를 사용해 쿼리 결과를 csv 파일로 만들 수 있습니다. CSV답게, Delimiter는 `Comma`로 하겠습니다.

```sql
COPY(
with recursive search_product(product_id) as
(
	select p.*
	from product p 
	where product_id =21306
	
	union all
	
	select p.*
	from product p, search_product sp
	where k.parent_product_id = sp.product_id
)
select * from search_product
) to '/tmp/dump.csv' With CSV DELIMITER ',';

```

![image-20211201150059655](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/postgres/hierarchyCsv.assets/image-20211201150059655.png)

>  COPY 3303 이라며, 3303개의 쿼리 결과를 csv로 저장했다고 나옵니다.

잘 저장 되었는지 확인 해 보겠습니다.

```zsh
cat /tmp/dump.csv
```

![image-20211201150434700](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/postgres/hierarchyCsv.assets/image-20211201150434700.png)

> 쿼리결과가 제대로 저장 된 것이 확인 됩니다.

## Docker 컨테이너 내의 파일 로컬로 가져오기

도커 컨테이너의 파일을 로컬로 가져오는 명령어는 아래와 같습니다.

`docker cp [컨테이너id]:[컨테이너내의원본파일경로] [복사해올경로]`

`b5528453216b` 컨테이너의 `/tmp/dump.csv` 파일을 현재 경로로 복사 하겠습니다. 지금의 명령어는 도커 컨테이너 내부가 아닌 로컬에서 실행 되어야 합니다.

> 컨테이너 id는 `docker ps` 명령어를 통해 찾을 수 있습니다.

```zsh
docker cp b5528453216b:/tmp/dump.csv .
```

명령어를 수행 하면 로컬의 명령어를 수행 한 위치에 `dump.csv` 파일이 생성 된 것을 확인 할 수 있습니다.

이상입니다.
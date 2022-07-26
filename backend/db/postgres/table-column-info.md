# [POSTGRES] 전체 테이블, 컬럼 정보 조회

## Intro

여러가지 산출물 작업을 해야 하는데, 데이터베이스에 대한 내용들은 이미 워낙 방대하기 때문에 도저히 손으로 작업 할 엄두가 나지 않았습니다.

필요에 의해 쿼리를 작성 했으나 추후 또 필요할 경우가 생겼을 때 시간을 절약 하기 위해, 또한 비슷한 고민을 하고 있는 분들에게 도움이 되었으면 하는 마음에 글로 작성해 남겨두려 합니다.

## SQL

제가 산출물 작업 하면서 필요한 내용들 위주로 쿼리를 작성 하였기 때문에 필요한 자료가 조금씩 다를 경우에는 일단 실행 해본 후에 쿼리를 약간씩 수정해서 사용 하시면 됩니다.

### 전체 테이블 주석과 실제 테이블명 조회

![image-20220726175305322](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/postgres/table-column-info.assets/image-20220726175305322.png)

> 테이블에 주석을 달아놓지 않았다면 NULL로 표기됩니다. 평소에 주석을 꼭 달아두어야 나중에 서류 작업 할 때 편합니다. 예시를 들기 위해 엉뚱한 DB에서 쿼리를 보내다 보니 대부분 주석은 NULL로 나오는 부분 양해 부탁드립니다.

```sql
SELECT pg_catalog.obj_description(pgc.oid, 'pg_class'), t.table_name
FROM information_schema.tables t
INNER JOIN pg_catalog.pg_class pgc
ON t.table_name = pgc.relname 
WHERE t.table_type='BASE TABLE'
AND t.table_schema='public'
order by table_name;
```

### 특정 테이블의 컬럼정보 조회1

조회시 테이블명 / 컬럼명 / 주석 / null 가능 여부 순서대로 조회 됩니다.

![image-20220726175837692](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/postgres/table-column-info.assets/image-20220726175837692.png)

```sql
select c.relname, a.attname  as "colname"
    ,(SELECT col_description(a.attrelid, a.attnum)) AS comment
	,a.attnotnull as "nullable"
from
    pg_catalog.pg_class c
    inner join pg_catalog.pg_attribute a on a.attrelid = c.oid
where
    c.relname = '테이블명'
    and a.attnum > 0
    and a.attisdropped is false
    and pg_catalog.pg_table_is_visible(c.oid)
order by a.attrelid, a.attnum;
```

### 모든 테이블의 컬럼 정보 조회

![image-20220726175954078](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/postgres/table-column-info.assets/image-20220726175954078.png)

> 위에서 살펴본 두개의 합체 버전이라고 보면 됩니다.

```sql
select c.relname as table_name, a.attname  as "column_name"
    ,(SELECT col_description(a.attrelid, a.attnum)) AS comment
from
    pg_catalog.pg_class c
    inner join pg_catalog.pg_attribute a on a.attrelid = c.oid
where
    c.relname in (
    SELECT t.table_name
	FROM information_schema.tables t
		INNER JOIN pg_catalog.pg_class pgc ON t.table_name = pgc.relname 
	WHERE t.table_type='BASE TABLE' AND t.table_schema='public' order by table_name)
    and a.attnum > 0
    and a.attisdropped is false
    and pg_catalog.pg_table_is_visible(c.oid)
order by relname, a.attrelid, a.attnum;

```

### 특정 테이블의 컬럼정보 조회2

![image-20220726180147171](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/postgres/table-column-info.assets/image-20220726180147171.png)

이번에는 해당 컬럼의 데이터 타입과 길이가 필요 할 때 사용 할 수 있는 쿼리 입니다.

```sql
select
	table_name,
	column_name,
	udt_name as "type",
	character_maximum_length as length
	,
	(case
		when is_nullable = 'NO' then 'N'
		else ''
	end) as "nullable"
from
	INFORMATION_SCHEMA.COLUMNS
where
	table_name = '테이블명';
```

### 특정 테이블의 컬럼정보 조회3

![image-20220726180215723](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/postgres/table-column-info.assets/image-20220726180215723.png)

이번에는 위에서 했던 조회1과 조회2의 정보를 섞어서

- 테이블 명
- 코멘트
- 컬럼 명
- 데이터 타입
- 최대 길이
- Null 여부(Y,N)

이렇게 6가지 정보를 조회하도록 해 보았습니다. 이정도면 테이블 정의서를 작성하기에 충분 합니다.

```sql
select
	cols.table_name,
	c.comment,
	cols.column_name,
	cols.udt_name as "type"
	,
	cols.character_maximum_length as length
	,
	(case
		when cols.is_nullable = 'NO' then 'N'
		else 'Y'
	end) as "nullable"
from
	INFORMATION_SCHEMA.columns cols
inner join (
	select
		c.relname as table_name,
		a.attname as "column_name"
		    ,
		(
		select
			col_description(a.attrelid, a.attnum)) as comment
	from
		    pg_catalog.pg_class c
	inner join pg_catalog.pg_attribute a on
		a.attrelid = c.oid
	where
		    c.relname in (
		select
			t.table_name
		from
			information_schema.tables t
		inner join pg_catalog.pg_class pgc on
			t.table_name = pgc.relname
		where
			t.table_type = 'BASE TABLE'
			and t.table_schema = 'public'
		order by
			table_name)
		and a.attnum > 0
		and a.attisdropped is false
		and pg_catalog.pg_table_is_visible(c.oid)
	order by
		relname,
		a.attrelid,
		a.attnum) c on
	(cols.table_name = c.table_name
		and cols.column_name = c.column_name)
where
	cols.table_name = '테이블명';
```

위에서 전체 테이블 명 조회 쿼리와 조합한다면, 데이터베이스 내 전체 테이블 및 컬럼에 대한 조회도 가능합니다.

<br><br>

이상입니다.

즐거운 서류 작업 되세요 :)
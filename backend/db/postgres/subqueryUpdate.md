# Postgres) rownum 사용과 Subquery를 통한 update

## rownum

Postgres 에서도 Oracle의 rownum을 사용 할 수 있을까요? 가능합니다. row_number()를 이용하면 됩니다.

그럼 아래 처럼 그냥 호출 하면 rownum을 사용 할 수 있을 것 같지만..

```sql
select row_number(), i.*
from item i;
```

![image-20211119145307908](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/postgres/subqueryUpdate.assets/image-20211119145307908.png)

윈도우 함수 호출에는 OVER 절이 필요함 이라는 에러가 나옵니다.

딱히 조건을 걸 생각이 없었지만 그래도 over절은 넣어줘야 하나 봅니다.

<br><br>

그럼 이렇게 작성 한다면 rownum을 사용 할 수 있습니다. as로 alias 를 주는것도 좋겠네요.

```sql
select row_number() over(), i.*
from item i;

```

## Subquery를 통한 update

이제 rownum을 받아 왔으니, 서브쿼리에서 rownum을 활용 해서 일괄 업데이트를 해 보겠습니다.

```sql
update product 
set title = subquery.title
from	( select d.id, '상품' || (row_number() over()) as title 
			from product d ) as subquery	
where product.id = subquery.id;

```

위와 같이 작성해 update를 하면, 상품1, 상품2, 상품3, ... 으로 모든 상품 명이 업데이트 됩니다.

원하는 정렬 기준이 있다면 서브쿼리 안에서 orderby를 넣으면 되지만,  rownum의 특성 상 정렬 후에 한번 더 감싸서 사용 해 주어야 합니다.!

rownum을 활용해서 DB에 dummy 데이터를 넣을때 유용하게 사용 할 수 있습니다!
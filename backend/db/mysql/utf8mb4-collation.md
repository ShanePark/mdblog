# MySQL 이모지 저장되지 않는 문제 해결

## Intro

데이터베이스에 이모지가 포함된 텍스트 데이터를 저장하려 했더니 다음과 같은 에러가 발생했다.

```java
org.springframework.orm.jpa.JpaSystemException: could not execute statement 
[Incorrect string value: '\xF0\x9F\x91\x89 \xEC...' for column 'content' at row 1] 
```

`INSERT`할 때 **Incorrect string value** 오류가 발생한 것이다. 문자셋은 utf8mb4로 제대로 설정되어있었는데 어떤 문제가 있었는지 알아보자.

## 원인 찾기

### 데이터베이스의 문자셋과 Collation 확인

```sql
SELECT schema_name AS database_name,
       default_character_set_name AS character_set,
       default_collation_name AS collation
FROM information_schema.schemata
WHERE schema_name = 'dutypark'; -- dutypark 대신 본인의 데이터베이스 이름으로 변경
```

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/db/mysql/utf8mb4-collation.assets/1.webp)

아무런 문제가 없었는데, character_set 및 collation에 대해 간단하게 알아보면

**character_sets**

- `utf8mb3`(이전에는 그냥 `utf8`으로 사용됨)는 **최대 3바이트까지 저장 가능**한 UTF-8 인코딩 방식이다.

​	BMP(Basic Multilingual Plane, U+0000 ~ U+FFFF) 문자까지만 지원하므로, 이모지나 일부 한자 확장 문자를 저장할 수 없음.

- `utf8mb4`는 **최대 4바이트까지 저장 가능**한 UTF-8 인코딩 방식이다.

​	SMP(Supplementary Multilingual Plane, U+10000 ~ U+10FFFF) 문자까지 저장할 수 있으므로, 이모지, CJK 확장 문자 등도 OK.

즉, MySQL에서 이모지를 저장하려면 `utf8mb4`를 사용해야 한다.

**collations**

- **utf8mb4_general_ci**:
  - 비교적 빠름
  - 단순한 문자열 비교를 수행하며, 일부 언어의 정렬 규칙을 올바르게 처리하지 못함.
  - 예를 들어, `"ß"`(독일어 sharp s)를 `"ss"`와 동일하게 간주하는 등 일부 문자 구별이 모호함.
- **utf8mb4_unicode_ci**:
  - 국제 표준(Unicode Collation Algorithm, UCA)을 따름.
  - `utf8mb4_general_ci`보다 **더 정확한 문자열 정렬과 비교 가능**.
  - 대소문자 구별 없이 다국어 텍스트를 비교할 때 권장됨.

`utf8mb4_general_ci`에서는 일부 4바이트 문자가 제대로 처리되지 않기 때문에 `utf8mb4_unicode_ci`를 사용해야 한다.

이제 데이터베이스의 모든 character set 및 collation을 확인하고  변경해보자. 데이터베이스가 설정이 잘 되었다고 해도 테이블이나 컬럼의 Collation이 잘못 설정되면 여전히 이모지 저장 시 오류가 발생했다.

### 모든 테이블의 Collation 확인

```sql
SELECT table_name,
       table_collation
FROM information_schema.tables
WHERE table_schema = 'dutypark'; -- dutypark 대신 본인의 데이터베이스 이름으로 변경
```

나는 여기에서 문제를 찾을 수 있었는데 몇 개 테이블의 collation이 `utf8mb3_general_ci` 로 되어 있었다.

```
table_name | table_collation          
-----------|--------------------------
todo       | utf8mb3_general_ci
users      | utf8mb4_unicode_ci
comments   | utf8mb4_unicode_ci
```

### 모든 컬럼의 Collation 확인

```sql
SELECT table_name,
       column_name,
       data_type,
       character_set_name,
       collation_name
FROM information_schema.columns
WHERE table_schema = 'dutypark' -- dutypark 대신 본인의 데이터베이스 이름으로 변경
  AND collation_name IS NOT NULL;
```

collation이 `utf8mb3_general_ci` 로 설정된 몇몇 테이블들은 해당 컬럼들의 collation 도 당연히 `utf8mb3_general_ci` 로 되어 있었다.

```
table_name | column_name | data_type | character_set_name | collation_name        
-----------|------------|-----------|--------------------|----------------------
todo       | content    | text      | utf8mb4            | utf8mb4_general_ci  
users      | username   | varchar   | utf8mb4            | utf8mb4_unicode_ci  
comments   | body       | text      | utf8mb4            | utf8mb4_unicode_ci  
```

## 해결

### 모든 테이블의 Collation 변경

```sql
SELECT CONCAT('ALTER TABLE ', table_name, ' CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;')
FROM information_schema.tables
WHERE table_schema = 'dutypark'; -- dutypark 대신 본인의 데이터베이스 이름으로 변경
```

이 쿼리를 실행하면, 자동으로 필요한 `ALTER TABLE` 명령어가 생성된다.  생성된 SQL을 실행하면 모든 테이블의 Collation이 일괄 변경된다."

```sql
ALTER TABLE todo CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE users CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE comments CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 모든 컬럼의 Collation 변경

테이블 설정만 변경하면 안되고 `TEXT`, `VARCHAR` 컬럼은 추가로 모두 변경해줘야한다.

```sql
SELECT CONCAT(
               'ALTER TABLE ', TABLE_NAME,
               ' MODIFY ', COLUMN_NAME, ' ', COLUMN_TYPE,
               ' CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;'
       )
FROM information_schema.columns
WHERE table_schema = 'dutypark' -- dutypark 대신 본인의 데이터베이스 이름으로 변경
  AND data_type IN ('char', 'varchar', 'text', 'mediumtext', 'longtext');
```

이 쿼리를 실행하면, 아래와 같은 SQL이 자동 생성된다. 생성된 SQL을 실행해준다.

```sql
ALTER TABLE todo MODIFY content TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE users MODIFY username VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE comments MODIFY body TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 변경 후 확인하기

다시 Collation을 확인해서 변경이 제대로 적용되었는지 확인했다. 각각 table, column 의 charset 및 collation을 조회해본다. 정상적으로 변경된게 확인되면 이제 DB에 이모지를 넣었을 때 오류가 발생하지 않는다.

```sql
SELECT table_name,
       table_collation
FROM information_schema.tables
WHERE table_schema = 'dutypark'; -- dutypark 대신 본인의 데이터베이스 이름으로 변경
```

```sql
SELECT table_name,
       column_name,
       data_type,
       character_set_name,
       collation_name
FROM information_schema.columns
WHERE table_schema = 'dutypark' -- dutypark 대신 본인의 데이터베이스 이름으로 변경
  AND collation_name IS NOT NULL;
```

## **결론**

- utf8mb4를 사용하고 있어도 `utf8mb4_general_ci` Collation을 사용하면 이모지 저장에 실패한다.
- 대신 `utf8mb4_unicode_ci`를 사용해야 한다.  
- 데이터베이스, 테이블, 컬럼 순서대로 Collation을 변경해주면 해결된다.
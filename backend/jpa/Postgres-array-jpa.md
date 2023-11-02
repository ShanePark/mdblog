# Postgres의 배열컬럼을 JPA(Hibernate)에서 사용하는방법

## Intro

Postgresql 에서는 특이하게도 [배열](https://www.postgresql.org/docs/current/arrays.html) 타입을 사용할 수 있다.

개인적으로는 이걸 선호하지는 않는데, 일반적인 다른 데이터베이스와 다른 형태의 쿼리작성이 필요하고 좀 더 복잡해지기 때문이다. 만약에 DB 마이그레이션이라도 해야 한다면 참 골치아프겠다.

특히 이런경우 ORM과의 호환성이 큰 문제가 될 수 있다. 업무에서 JPA를 주로 사용하고 있는데, 예상했던대로 자체적으로는 Postgres의 배열타입을 지원해주지 않았다. 

대신 UserType을 구현하여 새로운 타입을 정의할 수 있긴 했는데 그 방법으로 문제를 해결해보려 한다.

## 준비 작업

시연을 위해 샘플 프로젝트 및 샘플 데이터베이스를 준비하였으나, 이미 진행중인 프로젝트와 DB가 있다면 건너뛰어 코드 및 엔티티 부분만 참고해서 문제를 해결하면 되겠다.

### Project 생성

![image-20231102144514668](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/jpa/Postgres-array-jpa.assets/1.webp)

> 스프링부트 프로젝트를 생성했다

![image-20231102144539150](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/jpa/Postgres-array-jpa.assets/2.webp)

> 의존성으로 Spring Web, Lombok, Postgres, JPA를 추가한다.
>
> 스프링부트 버전은 최신버전인 3.1.5를 선택했다.

### Postgres 설치 및 실행

도커를 사용해 간단하게 샘플 데이터베이스를 띄웠다. DB를 순식간에 띄우고 금방 깔끔하게 제거 해낼 수 있으니 좋다.

**Docker**

아래는 샘플 DB를 띄우기 위해 사용한 도커 명령어다. 샘플 코드 작성이라면 DB를 직접 띄우는것보단 도커가 나을 것이며 샘플이 아직 개발중인 코드라면 이미 준비된 DB가 있을테니 그걸 그대로 사용하면 된다. 

> Docker 사용법에 대한 내용은 여기에 따로 기술하지 않겠다.

```bash
docker run --name postgres-array-test \
	--restart unless-stopped \
	-e POSTGRES_USER=test \
	-e POSTGRES_PASSWORD=1234 \
	-d -p 54321:5432 postgres
```

**psql**

DB가 떴다면, PSQL로 접속해준다.

```bash
docker exec -it postgres-array-test bash
psql -U test test
```

**테이블 생성**

이후 샘플용 테이블을 생성해준다. **배열 컬럼**을 만드는걸 잊지 말자.

```sql
CREATE TABLE sample (
  id UUID PRIMARY KEY,
  name VARCHAR(255),
  memo TEXT[]
);
```

![image-20231102150140922](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/jpa/Postgres-array-jpa.assets/3.webp)

> `\dt` 명령어로 테이블 목록을 볼 수 있다. 정상적으로 sample 테이블이 생성 되었다.

### Base Code

일단 Entity를 제외한 나머지 코드들은 아래와 같이 작성하였다.

${code:application.yml}

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:54321/test
    username: test
    password: 1234
    driver-class-name: org.postgresql.Driver
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true

```

> 위에서 Docker로 띄운 DB 접속정보를 정확히 입력한다. 실행되는 쿼리를 눈으로 확인하기 위해 `show-sql` 옵션도 켜두었다.

**메인 클래스**

메인 클래스는 Spring Initializr가 만들어준 그대로이다.

${code:PostgresArrayJpaApplication.java}

```java
@SpringBootApplication
public class PostgresArrayJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostgresArrayJpaApplication.class, args);
    }

}
```

**컨트롤러**

작동여부를 확인하기 위해 간단한 CR~~U~~D 기능을 제공하는 컨트롤러도 작성해주었다.

${code:SampleController.java}

```java
@RestController
@RequiredArgsConstructor
public class SampleController {

    private final SampleRepository sampleRepository;

    @GetMapping("/samples")
    public List<Sample> findAll() {
        return sampleRepository.findAll();
    }

    @GetMapping("/samples/{id}")
    public Sample find(@PathVariable UUID id) {
        return sampleRepository.findById(id).orElseThrow();
    }

    @PostMapping("/samples")
    public Sample createSample(
            @RequestParam String name,
            @RequestParam(required = false) String[] memo
    ) {
        Sample sample = new Sample(name, memo);
        return sampleRepository.save(sample);
    }

    @DeleteMapping("/samples/{id}")
    public void deleteSample(@PathVariable UUID id) {
        sampleRepository.deleteById(id);
    }

}
```

**리퍼지터리**

${code:SampleRepository.java}

```java
public interface SampleRepository extends JpaRepository<Sample, UUID> {
}

```

## Code1. 직접 구현

### 코드

`org.hibernate.usertype.UserType` 을 구현한 StringArrayType을 만들어준다. 꽤 많은 메서드를 구현해야 했다.

코드를 작성하면서 몇가지 긴가민가하고 꺼림찍한 부분이 있긴 했지만 간단한 예시에서는 원하는대로 작동하긴 했다.

> 참고로, 스프링 부트 3.1.5 버전에서 샘플 코드를 작성하였기 때문에  Hibernate 버전이 6.2.13로 잡혔는데, 5.x 버전대의 Hibernate와는 제법 차이가 있었다. 
>
> 일단 5.x 버전에서는 UserType에 Generic을 사용하지도 않으며, 커넥션도 `session.connection()` 으로 받아왔었지만, 6.x 버전에서는 `session.getJdbcConnectionAccess().obtainConnection()` 으로 한번 돌아서 받아오는 등 여러 차이로 인해 변경된 인터페이스를 살펴볼 필요가 있었다.

${code:StringArrayType.java}

```java
public class StringArrayType implements UserType<String[]> {

    @Override
    public int getSqlType() {
        return Types.ARRAY;
    }

    @Override
    public Class<String[]> returnedClass() {
        return String[].class;
    }

    @Override
    public boolean equals(String[] x, String[] y) {
        return Objects.deepEquals(x, y);
    }

    @Override
    public int hashCode(String[] x) {
        return x.hashCode();
    }

    @Override
    public String[] nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        Array array = rs.getArray(position);
        if (array == null) {
            return null;
        }
        return (String[]) array.getArray();
    }

    @Override
    public void nullSafeSet(PreparedStatement st, String[] value, int index, SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.ARRAY);
            return;
        }
        Array array = session.getJdbcConnectionAccess()
                .obtainConnection()
                .createArrayOf("text", value);
        st.setArray(index, array);
    }

    @Override
    public String[] deepCopy(String[] value) {
        return value != null ? value.clone() : null;
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(String[] value) {
        return deepCopy(value);
    }

    @Override
    public String[] assemble(Serializable cached, Object owner) {
        return deepCopy((String[]) cached);
    }
}

```

이번엔 Sample Entity다. 이것 또한 Hibernate 5.x 버전과 조금 차이가 있었다.

> 본 글의 말미에 Hibernate 5.x (Spring Boot 2.x 버전) 를 사용하는 코드도 올려둘테니 구 버전 사용자도 걱정하지 않아도 된다.

${code:Sample.java}

```java
@Entity
@Table(name = "sample")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class Sample {

    @Id
    private UUID id = UUID.randomUUID();

    @Column(name = "name")
    private String name;

    @Type(value = StringArrayType.class)
    @Column(name = "memo", columnDefinition = "text[]")
    private String[] memo = new String[0];

    public Sample(String name, String[] memo) {
        this.name = name;
        if (memo != null) {
            this.memo = memo;
        }
    }

}
```

방금 추가한 `StringArrayType` 객체를 type 으로 지정하였다.

### 테스트

Postman을 활용해 API 테스를 진행해본다. 일단, name 만 넣고 memo 에는 아무것도 넣지 않았다.

Postman이 없다면 curl로도 테스트 가능하다.

```bash
curl --location --request POST 'localhost:8080/samples?name=shane'
```

![image-20231102162528509](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/jpa/Postgres-array-jpa.assets/4.webp)

> 정상적으로 저장된 Sample 엔티티의 memo가 빈 배열로 출력된다.

이번에는 memo에 세가지 메모를 입력 해 보았다.

```bash
curl --location --request POST 'localhost:8080/samples?name=shane&memo=memo1&memo=memo2&memo=memo3'
```

![image-20231102162624614](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/jpa/Postgres-array-jpa.assets/5.webp)

> 정상적으로 저장되었다.

![image-20231102162703862](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/jpa/Postgres-array-jpa.assets/6.webp)

> 쿼리도 잘 만들어진것이 확인된다.

## Code2. 라이브러리 사용

### hypersistence-utils

이번에는 로마니아의 자바 챔피언인 [vladmihalcea](https://github.com/vladmihalcea)가 만든 [hypersistence-utils](https://github.com/vladmihalcea/hypersistence-utils) 를 이용해보겠다. (License: Apache-2.0)

`hypersistence-utils`는 사용중인 Hibernate 버전에 맞춰서 버전을 선택하면 되는데, 지금은 Hibernate 6.2 버전을 사용하고 있으니 `hypersistence-utils-hibernate-62`를 쓰면 되겠다.

- **Maven**

```xml
<dependency>
    <groupId>io.hypersistence</groupId>
    <artifactId>hypersistence-utils-hibernate-62</artifactId>
    <version>3.6.0</version>
</dependency>
```

- **Gradle**

```groovy
implementation 'io.hypersistence:hypersistence-utils-hibernate-62:3.6.0'
```

그러고 나서 직접 구현했던 `StringArrayType.java`는 과감히 삭제해준다.

이후 Entity로 돌아와서 확인해보면, `io.hypersistence.utils.hibernate.type.array` 패키지에 같은 이름의 객체가 구현되어 있는게 보인다.

![image-20231102163827603](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/jpa/Postgres-array-jpa.assets/7.webp)

코드를 확인해보면, 복잡한 계층 구조로 견고하게 작성되어있다.

![image-20231102164145613](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/jpa/Postgres-array-jpa.assets/8.webp)

위에서 직접 구현했던 UserType 인터페이스도 보인다. `StringArrayType.java` 를 직접 만들때는 테스트를 위해 대강 구현 했지만, 데이터베이스를 직접 다루는 만큼 여러 엣지 케이스 및 에러 핸들링등이 수반되어야 한다. 그런면에서 특별한 문제가 없다면 검증된 라이브러리를 쓰는 편이 좋을것이다.

이미 나머지는 모두 구현되어 있기 때문에 테스트해보면 똑같이 잘 작동하는게 확인된다.

### Hibernate 5

이번에는 글 초반부터 언급했던 것 처럼 Hibernate 버전을 5버전대로 변경해보겠다. 약간의 차이가 있지만 어렵지는 않다.

스프링 부트 버전은 `2.7.17`로 선택했고, 거기에 대응되는 Hibernate 버전은 `5.6.15`기 때문에 `hypersistence-utils-hibernate-55` 로 라이브러리 의존성을 변경한다. Hibernate 5.5과 5.6 버전 모두 55를 사용한다.

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '2.7.17'
    id 'io.spring.dependency-management' version '1.1.3'
}
...

dependencies {
    implementation 'io.hypersistence:hypersistence-utils-hibernate-56:3.6.0'
...
        
```

이제 엔티티를 조금 변경해준다. 거의 비슷하지만 확실히 Hibernate 6 버전대와는 차이가 있다.

${code:Sample.java}

```java
@Entity
@Table(name = "sample")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
@TypeDef(name = "string-array", typeClass = StringArrayType.class)
public class Sample {

    @Id
    private UUID id = UUID.randomUUID();

    @Column(name = "name")
    private String name;

    @Type(type = "string-array")
    @Column(name = "memo", columnDefinition = "text[]")
    private String[] memo = new String[0];

    public Sample(String name, String[] memo) {
        this.name = name;
        if (memo != null) {
            this.memo = memo;
        }
    }

}
```

테스트 해보면 당연히 잘 작동한다.

### List

마지막으로 배열대신 편리한 List로 변경해보겠다. 라이브러리에 모든게 준비되어 있기 때문에 편하게 사용할 수 있다.

${code:Sample.java}

```java
@Entity
@Table(name = "sample")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
@TypeDef(name = "list-array", typeClass = ListArrayType.class)
public class Sample {

    @Id
    private UUID id = UUID.randomUUID();

    @Column(name = "name")
    private String name;

    @Type(type = "list-array")
    @Column(name = "memo", columnDefinition = "text[]")
    private List<String> memo = new ArrayList<>();

    public Sample(String name, String[] memo) {
        this.name = name;
        if (memo != null) {
            this.memo = List.of(memo);
        }
    }

}
```

> Hibernate 6.x 에서는 `@Type(ListArrayType.class)` 면 충분하다

### 결론

그래도 개인적인 생각으로는 Postgres 배열 컬럼 사용을 피하는 편이 좋지 않을까 싶다. 

특히, 얼핏보면 연관관계를 부모 엔티티가 보관하는 장점이 있는 것으로 오해할 수 있으나 DB에서 `1:N` 관계에서의 외래키는 자식테이블이 갖고있는것이 자연스러우며 테스트 삼아 시도는 해 보았으나 코드가 매우 어색하고 복잡해 도저히 써먹기가 곤란했다.

단순한 텍스트를 배열로 가지는 경우에 쓰일수 있긴 하겠으나 여러 가지 단점을 고려해 보았을때는 차라리 마샬링의 번거로움을 감수하더라도 배열보다는 JSON 형태로 저장하는게 낫다고 생각한다.

**References**

- https://www.postgresql.org/docs/current/arrays.html
- https://github.com/vladmihalcea/hypersistence-utils
- https://vladmihalcea.com/postgresql-array-java-list/
# 여러개의 Datasource 등록중 겪은@ConfigurationProperties 적용 문제와 해결

## Intro

여러개의 데이터베이스에 연결하는 스프링부트 프로젝트에서 커넥션풀 설정이 제대로 적용되지 않고 있다고 하여 확인을 해 보았습니다. 

데이터베이스를 한개만 쓴다면, `spring.datasource` 에만 등록하면 충분합니다. 하지만 여러개를 등록하려고 하면 스프링부트의 autoconfigure 만으로는 부족합니다.

일단, 프로젝트를 열어 코드를 살펴보니 아래와같이 `application.yml` 에 각각의 데이터 소스를 설정 할 때, prefix를 주어서 각각의 데이터소스를 설정하고 있었습니다. 

${code:application.yml}

```yaml
myapp.datasource:
  type: com.zaxxer.hikari.HikariDataSource
  driver-class-name: org.postgresql.Driver
  jdbcUrl: jdbc:postgresql://${POSTGRES_HOST:localhost:5432}/${POSTGRES_SCHEMA:postgres}
  username: username
  password: password
  hikari: 
    poolName: aip
    connection-timeout: 3000
    maximum-pool-size: 5
    registerMbeans: false
  
db2.datasource:
  type: com.zaxxer.hikari.HikariDataSource
  driver-class-name: com.tmax.tibero.jdbc.TbDriver
  jdbcUrl: jdbc:tibero:thin:
  ...
```

그리고 각각의 설정을 불러와서 DataSource를 빈으로 등록 하고 있었는데요,

```java
@Primary
@Bean("dataSource")
@ConfigurationProperties("myapp.datasource")
public DataSource datasource() {
    return DataSourceBuilder.create().build();
}
```

DB 접속은 문제 없이 되는데, 확인해보면 유독 커넥션풀에 대한 설정만 적용이 되지 않았습니다.

## 문제 확인

일단 제일 먼저, `return DataSourceBuilder.create().build();` 에서 반환되는 DataSource를 체크해보았습니다. 

![image-20230419173937287](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/configuration_properties.assets/image-20230419173937287.webp)

확인을 해 보면, 일단 `HikariDataSource` 로 데이터소스의 클래스 타입은 의도한 대로 되었습니다.

다만, 생성된 객체를 보면 아무런 데이터도 없는게 보입니다. 이는 `@ConfigurationProperties` 의 작동방식에 대한 이해가 먼저 필요합니다.

스프링은 빈을 생성하고 빈 저장소에 등록하기 전에 몇가지 빈 후 처리기 (PostProcessor) 를 활용해  필요한 조작을 합니다. 그 원리로`@ConfigurationProperties` 가 동작하는거고, 해당 작업을 맡은 후처리기는 `ConfigurationPropertiesBindingPostProcessor` 입니다.

![image-20230419180015948](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/configuration_properties.assets/image-20230419180015948.webp)

> beforeInitialization 에서 bind 를 해 줍니다.

bind 하기 직전에 브레이크포인트를 찍고 확인을 해보면

![image-20230419180036456](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/configuration_properties.assets/image-20230419180036456.webp)

dataSource라는 이름으로 빈이 넘어왔습니다.  인스턴스의 타입이 HIkariDataSource 입니다.

이제 `ConfigurationPropertiesBinder` 클래스의 bind 메서드로 넘어가는데요

![image-20230419180230654](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/configuration_properties.assets/image-20230419180230654.webp)

어노테이션의 정보를 불러와서 설정값을 바인드해주는 것을 볼 수 있습니다.

## 원인

이제 일반적인 `spring.datasource` 하위에 `hikari` 옵션을 두었을때와 지금 설정에서의 차이점에 대해 생각해봐야 합니다.

### SpringBoot AutoConfigurer

원래대로 스프링부트 자동 설정을 이용했다면 `DataSourceProperties` 에 설정값들이 들어가고, 그걸 바탕으로 스프링이 `DataSourceAutoConfiguration` 에서 커넥션풀 설정값을 포함한 DataSource 를 생성해줄겁니다.

먼저 `DataSourceProperties` 를 확인해보겠습니다.

![image-20230419200242395](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/configuration_properties.assets/image-20230419200242395.webp)

prefix에 낯 익은 `spring.datasource`가 보입니다.

그리고 여기에 name, url, username, password 등등 DataSource 설정에 필요한 프로퍼티들이 모두 있습니다. 

그러면 실제 데이터소스 구현체에 들어가는 설정값은 어디에서 적용이 될까요? 그건 `DataSourceConfiguration.java` 에서 찾을 수 있었습니다.

![image-20230419202018834](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/configuration_properties.assets/image-20230419202018834.webp)

> 이곳에서 Hikari, DBCP, OracleUcp 등의 세부 설정값 주입이 이루어집니다.

### ConfigurationProperties

하지만 지금 상황에서는후처리기를 활용하여 설정값을 주입해서 만드는 DataSource 입니다. 그리고, 그 과정에서 후처리 대상은 Autoconfigure에서 설정했던 `DataSourceProperties`가 아닌  `HikariDataSource` 입니다.

이번에는 `HikariDataSource`를 살펴 보면

![image-20230419181324117](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/configuration_properties.assets/image-20230419181324117.webp)

위에 보이는 것 처럼, `HikariConfig`를 상속하고 있습니다.

그리고 이제 HikariConfig 를 확인해보면 여기에서

![image-20230419181510382](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/configuration_properties.assets/image-20230419181510382.webp)

maxPoolSize 와 같은 값이 설정되어야 합니다. 각각 필요한 setter를 public 메서드로 가지고 있습니다.

그런데 위에서의 설정을 다시 확인 해 보면

```yaml
myapp.datasource:
  type: com.zaxxer.hikari.HikariDataSource
  driver-class-name: org.postgresql.Driver
  jdbcUrl: jdbc:postgresql://${POSTGRES_HOST:localhost:5432}/${POSTGRES_SCHEMA:postgres}
  username: username
  password: password
  hikari: 
    poolName: aip
    connection-timeout: 3000
    maximum-pool-size: 5
    registerMbeans: false
```

`maximum-pool-size` 등이 hikari의 하위뎁스에 작성되어 있습니다. 이건 SpringBoot의 AutoConfigure 설정 관계에 따른 건데요.

문제는 이렇게 되면, url과 username 및 password 등은 정상적으로 후처리기가 입력을 해 주지만, 정작 hikari 하위에 있는 관련된 설정정보들이 제대로 전달이 되지 않습니다.

## 해결

이제 원인을 찾았으니 문제를 해결 해 줍니다.

hikari 하위 레벨에 작성했던 커넥션풀에 대한 설정을 한단계 위로 올려줘서 설정값이 정상적으로 바인딩 되도록 해 줍니다.

```yaml
myapp.datasource:
  type: com.zaxxer.hikari.HikariDataSource
  driver-class-name: org.postgresql.Driver
  jdbcUrl: jdbc:postgresql://${POSTGRES_HOST:localhost:5432}/${POSTGRES_SCHEMA:postgres}
  username: username
  password: password
  poolName: aip
  connection-timeout: 3000
  maximum-pool-size: 5
  registerMbeans: false
```

이후 다시 어플리케이션을 실행 해보면

![image-20230419181949844](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/configuration_properties.assets/image-20230419181949844.webp)

정상적으로 원하는만큼의 컬렉션 풀이 생성되는것이 확인됩니다.

조금 더 명확하게 하려면, 빈 등록할 때 부터 DataSource가 아닌 HikariDataSource로 등록을 하고, 설정값 주입도 HikariConfig 객체에 먼저 한 뒤에 설정값을 받는 생성자를 호출해서 만들어 등록하는 편이 조금 더 좋겠습니다.

```java
 public HikariDataSource(HikariConfig configuration)
 {
    configuration.validate();
    configuration.copyStateTo(this);

    LOGGER.info("{} - Starting...", configuration.getPoolName());
    pool = fastPathPool = new HikariPool(this);
    LOGGER.info("{} - Start completed.", configuration.getPoolName());

    this.seal();
 }
```

> HikariDataSource.java

이상입니다.



**References**

- https://programmersought.com/article/25581101625/
- https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/
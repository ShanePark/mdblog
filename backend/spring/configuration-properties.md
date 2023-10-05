# [스프링 부트] 외부 설정값으로 간단하게 복잡한 커스텀 빈 주입 받기

## intro

웹 프로젝트를 진행하다보면 외부 설정값을 어플리케이션 내에서 활용할 일이 많다.

하드코딩을 해두면 후에 변경하기 쉽지 않기 때문에 외부에 설정값으로 빼는게 좋은데, 설정값이 한두개 일 때야 크게 어렵지 않지만 설정값이 점점 늘어나고 심지어 계층형 구조를 가진다면 점점 복잡해진다.

스프링에서는 편리하게 여러가지 설정값을 관리하기 위한 방법을 제공하는데 그 중 `@ConfigurationProperties`는 특히 프로퍼티의 값을 Bean으로 매핑하는 강력하고 직관적인 기능을 제공한다.

본 글에서는 `@ConfigurationProperties`의 기본 사용법과 주요 특징 및 주의할 점에 대해 다루어보려고 한다.

## main

### 프로젝트 생성

![image-20231005172131027](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/configuration-properties.assets/1.webp)

스프링부트 3.x 버전으로 먼저 생성하고 추후 2.x 버전으로 낮추며 변경사항을 반대로 확인해보도록 하겠다.

의존성은 간단하게 `Spring Web` 만 추가하였다.

### 코드 작성

${code:BootConfigurationPropertiesApplication.java}

```java
@SpringBootApplication
@ConfigurationPropertiesScan
public class BootConfigurationPropertiesApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootConfigurationPropertiesApplication.class, args);
    }

}
```

제일 먼저 `@ConfigurationPropertiesScan` 어노테이션을 메인어플리케이션 클래스에 달아주는데, 보통은 설정에 관련된 어노테이션들은 별도의 `@Configuration` 클래스에 달기도 하겠지만 간단한 샘플 코드이므로 이렇게 했다.

`@ConfigurationPropertiesScan` 대신 `@EnableConfigurationProperties`어노테이션을 사용하는 방법도 있지만, 그 경우에는 등록할 클래스들을 아래와 같이 일일히 기입해 주어야 하기 때문에 ConfigurationPropertiesScan 으로 한번에 스캔을 하도록 하는쪽이 간편하다.

```java
@EnableConfigurationProperties({Car.class})
```

이제 설정값을 매핑할 객체를 만들어보자.

${code:Car.java}

```java
@ConfigurationProperties(prefix = "car")
public class Car {

    public Car(String name, int range, double battery, int price) {
        this.name = name;
        this.range = range;
        this.battery = battery;
        this.price = price;
    }

    private final String name;
    private final int range;
    private final double battery;
    private final int price;

    @Override
    public String toString() {
        return "Car {" +
                "name=' " + name + '\'' +
                ", range= " + range + "km" +
                ", battery= " + battery + "kWh" +
                ", price= " + price + "₩" +
                " }";
    }

    public String getName() {
        return name;
    }

    public int getRange() {
        return range;
    }

    public double getBattery() {
        return battery;
    }

    public int getPrice() {
        return price;
    }
}
```

전혀 특별할게 없는 코드다. **Car** 객체를 선언했고, 프로퍼티로는 이름, 주행거리, 배터리 용량, 가격을 표기했다.

외부 설정값을 불러와서 사용할때는 그 값이 변할 일이 없기 때문에 불변값으로 선언하고 생성자를 통해 값을 주입하도록 했다.

`ConfigurationProperties` 어노테이션을 달며 prefix도 선언해준다.

이번에는 외부의 설정값을 작성해보자.

${code:application.yml}

```yaml
car:
  name: Tesla Model Y RWD
  range: 350
  battery: 56.88
  price: 56_990_000
```

스프링에서 외부 설정값을 주입하는 방법은 여러가지가 있다. 환경변수나 커맨드라인 인자 그리고 `applcation.yml` 혹은 properties 파일을 이용하는데, 그 우선순위는 변경이 까다로울 수록 높다.

그런의미에서 `application.yml`에 기입된 설정은 추후에 실행할때 다른 방법들로 손쉽게 오버라이딩이 가능하다.

이제 테스트 코드를 작성해보자.

${code:CarTest.java}

```java
@SpringBootTest
class CarTest {

    @Autowired
    Car car;

    @Test
    public void test() {
        assertThat(car).isNotNull();
        System.out.printf("\n\n%s\n\n", car);
    }

}
```

Spring의 도움을 받아 외부 설정을 주입받기 때문에 `@SpringBootTest`로 작성하였고, Car 객체를 Field injection 한다.

출력된 결과를 확인해보면 아래와 같다.

![image-20231005175207941](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/configuration-properties.assets/2.webp)

> 원하는 데이터들이 적절하게 주입된 상태

### 계층이 포함된 보다 복잡한 객체

이번에는 좀 더 복잡한 객체를 만들어보자. 

${code:application.yml}

```yaml
company:
  name: Tesla
  location:
    city: Palo Alto
    country: USA
  employees:
    - name: Shane
      position: Developer
    - name: Elon
      position: Manager
```

아까는 객체를 먼저 선언했지만 이번에는 반대로 설정값을 먼저 작성해본다.

이번 예제처럼 계층뿐만 아니라 리스트도 주입이 가능하다.

${code:Company.java}

```java
@ConfigurationProperties(prefix = "company")
public record Company(
        String name,
        Location location,
        List<Employee> employees
) {
}
```

이번에는 자바 16에서 정식으로 추가된 레코드 객체를 이용해보자. 코드가 훨씬 간단명료하게 작성된다.

${code:Location.java}

```java
public record Location(
        String city,
        String country
) {
}

```

${code:Employee.java}

```java
public record Employee(
        String name,
        String position) {
}
```

이번에도 테스트 코드를 작성해본다.

${code:CompanyTest.java}

```java
@SpringBootTest
class CompanyTest {

    @Autowired
    Company company;

    @Test
    public void test() {
        assertThat(company).isNotNull();
        System.out.printf("\n\n%s\n\n", company);
    }

}

```

실행결과

![image-20231005180251415](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/configuration-properties.assets/3.webp)

문제없이 원하는대로 외부설정값이 객체에 바인딩 되었다.

### 스프링부트 2.x

스프링 부트 버전을 다운그레이드 하고 차이점을 알아보자.

```groovy
    id 'org.springframework.boot' version '2.7.16'
```

`build.gradle` 혹은 `pom.xml` 에서 기재되어있는 스프링부트 버전을 변경하면 끝이다.

이제 똑같은 테스트 코드를 실행해보면 테스트에 실패한다.

![image-20231005202531541](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/configuration-properties.assets/4.webp)

> Consider adding @ConstructorBinding to com.example.bootconfigurationproperties.Car if you intended to use constructor-based configuration property binding.

Car 클래스가 String 타입의 bean을 요구하는데 찾을 수 없다고 한다. 아래 보면 에러 메시지에 정확하게 정확하게 해결책까지 보여주는데, 생선자 기반 프로퍼티 바인딩을 하려면 `@ConstructorBinding` 어노테이션을 Car 클래스에 붙이라고 한다.

시키는 대로 Car 클래스에 `@ConstructorBinding`만 하나 더 추가로 달아주면 테스트에 성공한다.

${code:Car.java}

```java
@ConfigurationProperties(prefix = "car")
@ConstructorBinding
public class Car {
  ...
}
```

스프링 부트 3.0.0 릴리즈 노트를 확인 해보면 아래와 같이 기재되어 있다.

![image-20231005202847645](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/configuration-properties.assets/5.webp)

> https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0.0-M2-Release-Notes#constructingbinding-no-longer-needed-at-the-type-level

단일 생성자의 경우에는 `@ConstructorBinding` 를 생략할 수 있다는 내용이다.

실제 스프링 3.0 버전에서 `ConstructorBinding.java` 코드를 확인 해보면 아래와 같이 **Deprecated** 된 것을 확인 할 수 있다.

![image-20231005203158328](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/configuration-properties.assets/7.webp)

여기에서 재밌는건 Record 클래스의 경우 `@ConstructorBinding`가 없어도 문제가 없었다는거다. 의아해서 찾아보니 스프링부트 2.6 릴리즈 노트에서 힌트를 찾을 수 있었는데

![image-20231005203941975](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/configuration-properties.assets/8.webp)

> https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.6-Release-Notes#records-and-configurationproperties

Record의 경우는 어차피 하나의 생성자를 가지기 때문에 3.0에 적용하기 전에 이미 레코드를 대상으로 먼저 적용을 해봤던걸로 보인다.

`@ConstructorBinding`을 붙인 상태로 다시 3.x 버전으로 마이그레이션한다면 이번에는 해당 어노테이션이 에러를 내는데

![image-20231005204348080](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/configuration-properties.assets/9.webp)

`@Target`에 써있는 것 처럼 생성자를 대상으로 하는 어노테이션으로 변경되었기 때문이다.

### 다수의 생성자

변경된 `@ConstructorBinding` 어노테이션을 써보기 위해, 이제 다시 3.x 버전으로 돌아와서, 생성자가 여러개라면 어떻게 되는지 확인해보자.

```java
@ConfigurationProperties(prefix = "car")
public class Car {

    public Car() {
        this.name = "";
        this.range = 0;
        this.battery = 0.0;
        this.price = 0;
    }

    public Car(String name, int range, double battery, int price) {
        this.name = name;
        this.range = range;
        this.battery = battery;
        this.price = price;
    }
```

테스트 결과

![image-20231005204727826](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/configuration-properties.assets/10.webp)

이번에는 setter가 없다는 오류가 발생한다. 기본적으로 ConfiguratonProperties는 Setter로 주입을 하는데, 위의 경우 모든 프로퍼티가 final로 선언되어 있기 때문에 setter를 가질 생각이 없다. 드디어 `@ConstructorBinding`을 쓸 차례다.

![image-20231005204945813](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/configuration-properties.assets/11.webp)

`@ConstructorBinding`를 자동완성 하면 두개의 클래스가 나오는데, 사실 둘다 잘 동작한다.

 다만, 이전에 다른 목적으로 사용되던 `ConstructorBinding` 클래스와의 차별점을 두기 위해 기존의 것은 Deprecate 하고, `.bind` 패키지에 새로운 클래스를 만들어뒀으니 그걸 쓰도록 하자.

두개의 생성자에서도 생성자로 외부 값을 주입하는 예제 코드는 아래와 같다.

${code:Car.java}

```java
@ConfigurationProperties(prefix = "car")
public class Car {

    public Car() {
        this.name = "";
        this.range = 0;
        this.battery = 0.0;
        this.price = 0;
    }

    @ConstructorBinding
    public Car(String name, int range, double battery, int price) {
        this.name = name;
        this.range = range;
        this.battery = battery;
        this.price = price;
    }

    private final String name;
    private final int range;
    private final double battery;
    private final int price;

    @Override
    public String toString() {
        return "Car {" +
                "name=' " + name + '\'' +
                ", range= " + range + "km" +
                ", battery= " + battery + "kWh" +
                ", price= " + price + "₩" +
                " }";
    }

    public String getName() {
        return name;
    }

    public int getRange() {
        return range;
    }

    public double getBattery() {
        return battery;
    }

    public int getPrice() {
        return price;
    }
}
```

## Conclusion

사실 `ConfigurationProperties`는 사용자가 따로 찾아 쓰지 않아도 이미 스프링부트에서 엄청나게 잘 활용하고 있다.

하나의 예시를 들자면 datasource를 설정할 때 인데, `DataSourceProperties` 클래스를 확인해보면 익숙한 어노테이션이 위에 붙어있는것을 확인할 수 있다.

![image-20231005205942290](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/configuration-properties.assets/12.webp)

또한, 포트나 context-path 혹은 ssl등을 설정할 때 쓰는 ServerProperties도 모두 한번쯤 사용해봤을 것이다.

![image-20231005210131261](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/configuration-properties.assets/13.webp)

그렇다면 마지막으로, 스프링은 어떤 과정을 통해 외부 설정값을 빈으로 주입할까?

코드를 찾아보면, `ConfigurationPropertiesBindingPostProcessor` 가 `ConfigurationProperties` 어노테이션이 붙은 빈을 초기화 하는 동작에서 빈 후처리기로 작동하여  프로퍼티 값들을 바인딩 해주는 것을 확인할 수 있다. 

![image-20231005211341832](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/configuration-properties.assets/14.webp)

지금까지 알아본 것처럼 `@ConfigurationProperties` 를 사용하면 `@Value` 를 쓰는것 보다 복잡한 설정을 간편하게 주입받을 수 있다. 다만 프로퍼티 이름의 네이밍 컨벤션(설정값 케밥 케이스 -> 자바 카멜케이스) 이나 주입받는 설정의 우선순위, 그리고 사용방법 등에 대해서 의식하며 사용해야 의도치 않은 동작을 피할 수 있겠다.

위에서 예시로 든 전체 코드는 아래의 Github 저장소에서 확인 할 수 있다.

https://github.com/ShanePark/mdblog/tree/main/projects/boot-configuration-properties

**References**

- https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config
- https://www.baeldung.com/configuration-properties-in-spring-boot
- https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0.0-M2-Release-Notes#constructingbinding-no-longer-needed-at-the-type-level
- https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.6-Release-Notes#records-and-configurationproperties

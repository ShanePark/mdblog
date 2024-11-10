# [Java] static 키워드 이해하기

## Intro

자바를 사용해 코드를 작성하다보면 `static` 키워드를 자주 마주하게 된다.

하지만 이 키워드가 적절하지 않게 사용되는 경우가 종종 있는데, 이번 글에서는 `static` 키워드의 개념과, 언제 어떻게 사용해야 하는지 예제 코드를 통해 자세히 알아보겠다.

## static

자바에서 `static` 키워드는 클래스의 멤버(변수나 메서드)를 `클래스 레벨`로 정의할 때 사용된다. 일반적인 클래스의 변수나 메서드는 <u>인스턴스를 생성한 뒤</u>에 접근할 수 있지만, `static` 키워드를 사용하면 <u>클래스 인스턴스 없이도</u> 해당 멤버에 접근할 수 있다.

간단히 말해, `static` 멤버는 클래스 자체에 속해 있으므로 여러 인스턴스가 공유하는 특징을 갖는다. 인스턴스가 여러 개여도 `static` 변수는 딱 하나만 존재하고, 이를 모든 인스턴스가 공통으로 참조하게 된다.

예를 들어보자.

```java
public class StringUtils {

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static String toUpperCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase();
    }
}

public class Main {
    public static void main(String[] args) {
        String input = "hello";

        boolean isEmpty = StringUtils.isNullOrEmpty(input);
        System.out.println("Is input null or empty? " + isEmpty);

        String upper = StringUtils.toUpperCase(input);
        System.out.println("Uppercase: " + upper);
    }
}

```
위 코드에서 `isNullOrEmpty`와 `toUpperCase` 메서드는 `static` 메서드이기 때문에 `StringUtils` 클래스의 인스턴스 생성 없이 바로 호출할 수 있다. 이렇게 `static` 메서드를 사용하면 특정 객체와 관계없이 공통적인 유틸리티 기능을 제공할 수 있다.

 `static` 변수는 **메서드 영역**(Method Area)에 저장되며, 클래스가 처음 로딩될 때 메모리에 할당된다. 이는 **힙 영역**(Heap area)에 저장되는 인스턴스 변수와 달리 클래스 로딩 시점에 메모리에 올라가고, 프로그램 종료 시까지 유지된다.

## static 블록

`static 블록`은 클래스가 로딩될 때 한 번 실행되는 초기화 블록으로, `static` 변수의 복잡한 초기화가 필요한 경우에 사용된다. 이 블록은 클래스가 메모리에 로딩되면서 자동으로 실행되며, 여러 개의 `static 블록`이 있을 경우 위에서 아래로 순차적으로 실행된다.

예를 들어보자.

```java
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DatabaseConfig {
    public static final String DB_URL;
    public static final String DB_USER;
    public static final String DB_PASSWORD;

    static {
        Properties properties = new Properties();
        try (InputStream input = DatabaseConfig.class.getResourceAsStream("/db_config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find db_config.properties");
                throw new IOException("Configuration file not found");
            }
            properties.load(input);

            DB_URL = properties.getProperty("db.url");
            DB_USER = properties.getProperty("db.user");
            DB_PASSWORD = properties.getProperty("db.password");

            System.out.println("Database configuration loaded.");
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Failed to load database configuration");
        }
    }
}

```

위 코드에서 `DatabaseConfig` 클래스는 `static 블록`을 사용하여 데이터베이스 설정을 한 번만 초기화한다. 이 `static 블록`은 **클래스가 처음 로딩될 때** 실행되며, 이후 모든 인스턴스가 이 설정 값을 공유하게 된다.

## 필요한 때

### 1. 공통 데이터나 기능이 필요할 때

`static`은 공통된 데이터를 유지하거나 유틸리티 기능을 제공할 때 유용하다. 예를 들어, 전역에서 접근 가능한 상수를 정의할 때 `static final` 키워드를 사용하여 클래스 전체에서 변하지 않는 값을 설정할 수 있다.

```java
public class Constants {
    public static final double PI = 3.14159;
}
```
`PI`는 변하지 않는 값이므로 `static final`로 선언하여 클래스 인스턴스 생성 없이 바로 접근할 수 있다.

### 2. 유틸리티 클래스나 메서드 작성 시

Java의 `Math` 클래스처럼 인스턴스를 생성하지 않아도 사용할 수 있는 유틸리티 메서드를 작성할 때에도 `static`을 사용한다. 예를 들어, 특정 숫자의 최대값을 구하거나 문자열을 변환하는 작업을 수행하는 메서드를 `static`으로 정의하면, 편리하게 사용할 수 있다.

```java
public class MathUtils {
    public static int add(int a, int b) {
        return a + b;
    }
}

public class Main {
    public static void main(String[] args) {
        int sum = MathUtils.add(5, 10);
        System.out.println("Sum: " + sum);
    }
}
```
위의 예시처럼 `add` 메서드는 특정 객체와 관계없이 사용할 수 있는 일반적인 기능을 제공하므로 `static`으로 정의한다. 이렇게 하면 굳이 인스턴스를 만들지 않고도 `MathUtils.add()` 형태로 호출할 수 있다.

## 실무에서의 사용

다음은 실무에서 `static` 키워드가 자주 사용되는 몇 가지 경우다.

### 1. 싱글톤 패턴 구현

싱글톤 패턴에서는 클래스의 유일한 인스턴스를 `static`으로 선언하여 관리할 수 있다.

```java
public class Singleton {
    private static Singleton instance;

    private Singleton() {}

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
```
위 코드에서 `getInstance()` 메서드를 통해 하나의 인스턴스를 반환하며, 인스턴스를 저장하는 `instance` 변수는 `static`으로 선언하여 클래스 레벨에서 관리한다.

### 2. 로그 관리

로그를 관리하는 클래스에서 로깅 관련 메서드를 `static`으로 선언하면, 모든 클래스에서 동일한 로깅 메서드를 사용할 수 있어 효율적이다.

```java
public class Logger {
    public static void log(String message) {
        System.out.println("LOG: " + message);
    }
}
```
모든 클래스에서 `Logger.log("Some message") 형태로 호출하여 로그를 기록할 수 있다. 이를 통해 로그 시스템을 전역적으로 사용할 수 있다.

## static 사용 시 주의할 점

- **메모리 사용**: `static`으로 선언된 멤버는 프로그램이 종료될 때까지 메모리에 상주하므로, 과도한 데이터를 `static`으로 선언할 경우 메모리 사용량이 불필요하게 증가할 수 있으니 필요한 경우에만 신중히 사용하자
- **동기화 문제**: 멀티 스레드에서 `static` 변수에 접근해 값을 변경할 경우 동기화 문제가 발생할 수 있으므로, 멀티스레드 환경에서는 신중히 사용하거나 적절한 동기화 처리를 해야 한다.

## 결론

자바에서 `static` 키워드는 클래스 레벨에서 멤버를 공유하거나, 인스턴스 생성 없이 사용할 수 있는 유틸리티 기능을 제공하는 데 유용하다. 그러나 무분별하게 사용하면 과도한 메모리 사용을 초래할 수 있으므로, 반드시 필요한 경우에만 사용하는 것이 좋다.

끝
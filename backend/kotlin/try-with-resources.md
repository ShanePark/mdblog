# Kotlin에서의 Try with Resources

## Intro

Java 7부터 도입된 `try-with-resources` 구문은 파일이나 데이터베이스 커넥션처럼 꼭 닫아야 하는 자원을 사용한 후 자동으로 닫아줘서 자원 누수를 방지하는 기능을 제공한다. 그러나 Kotlin에서는 Java처럼 `try-with-resources` 구문을 따로 제공하지 않는다. 그렇다면 Kotlin에서는 어떻게 자원을 안전하게 다룰까? 

Kotlin에서는 `use`라는 확장 함수를 통해 이와 비슷한 기능을 구현한다. 이번 글에서는 Kotlin에서 자원을 안전하게 관리하는 방법을 알아본다.

## 자동 자원 관리

Kotlin은 `Closeable` 인터페이스를 구현한 객체에 대해 `use`라는 확장 함수를 제공한다. 이 함수는 자원을 사용한 후 자동으로 닫아주기 때문에 Java의 `try-with-resources`와 유사한 역할을 한다.

### Java

Java에서는 보통 자원을 다룰 때 다음과 같은 단계로 관리한다.

```java
resource = acquireResource();
try {
    useResource(resource);
} finally {
    releaseResource(resource);
}
```

이처럼 `finally` 블록에서 자원을 닫는 방식은 `Automatic Resource Management`라고 불린다. Java에서는 `Closeable` 또는 `AutoCloseable` 인터페이스를 구현한 객체를 `try-with-resources` 구문에서 사용할 수 있다.

### Kotlin

Kotlin도 동일하게 `Closeable` 또는 `AutoCloseable` 인터페이스를 구현한 객체를 통해 자원을 관리할 수 있다. 하지만 별도의 언어 구문을 제공하지 않고 `use`라는 확장 함수를 사용해 자동 자원 관리를 지원한다.

Kotlin의 `use` 함수는 `Closeable`이나 `AutoCloseable`을 구현한 객체에 대해 자원을 닫는 역할을 한다. 다음은 `use` 함수의 기본 예제다.

```kotlin
import java.io.File

fun readFile(fileName: String): String {
    return File(fileName).bufferedReader().use { reader ->
        reader.readText()
    }
}
```

위 코드는 `bufferedReader`가 `Closeable`을 구현하므로 `use` 함수와 함께 사용할 수 있다. `use` 블록이 끝나면 `reader` 객체가 자동으로 닫히기 때문에, 자원을 안전하게 관리할 수 있다.

```kotlin
FileWriter("test.txt").use { it.write("something") }
```

위 코드처럼 `use` 함수는 람다 표현식에서 `it`이라는 암시적 변수를 사용해 자원 객체를 바로 참조할 수 있다. 그러나 명확한 가독성을 위해 자원에 이름을 부여하는 것도 좋은 선택이다.

Kotlin의 `use` 함수는 표준 라이브러리에서 제공되는 `Closeable` 인터페이스의 확장 함수로 정의되어 있다. 이 함수는 자원 사용 후 `close` 메서드를 호출해 자원을 해제하며, 코드가 블록을 벗어날 때 예외가 발생해도 안전하게 자원을 닫아준다.

```kotlin
inline fun <T : Closeable?, R> T.use(block: (T) -> R): R {
    var closed = false
    try {
        return block(this)
    } catch (exception: Throwable) {
        closed = true
        try {
            this?.close()
        } catch (closeException: Throwable) {
            exception.addSuppressed(closeException)
        }
        throw exception
    } finally {
        if (!closed) {
            this?.close()
        }
    }
}
```

> 위와 같은 내부 로직 덕분에 `use`는 예외 상황에서도 안전하게 자원을 해제해준다.

### 여러 자원 관리하기

자바의 try-with-resources 구문에서는 여러개의 자원을 동시에 선언하고 관리할 수 있다.

```java
// Java의 try-with-resources
try (
    BufferedReader reader1 = new BufferedReader(new FileReader("file1.txt"));
    BufferedReader reader2 = new BufferedReader(new FileReader("file2.txt"))
) {
    // 두 자원을 동시에 사용 가능
    System.out.println(reader1.readLine() + reader2.readLine());
}

```

하지만 아쉽게도 코틀린에서는 그런 방법을 지원하지 않는다.

`use` 함수는 단일 자원에 대해 편리하게 사용할 수 있지만, 여러 자원을 다룰 때는 `use` 블록을 중첩해서 사용해야 한다. 이로인해 java에 비해 중첩 구조가 늘어나는 단점이 있다.

```kotlin
fun readTwoFiles(file1: String, file2: String): String {
    return File(file1).bufferedReader().use { reader1 ->
        File(file2).bufferedReader().use { reader2 ->
            reader1.readLine() + reader2.readLine()
        }
    }
}
```

중첩된 구조가 깊어질 경우, 별도의 함수를 만들어 코드의 가독성을 높여야한다.

## 결론

Kotlin에서는 Java의 `try-with-resources`와 같은 구문 대신 `use` 함수를 통해 자원을 안전하게 관리할 수 있다.

 `use` 함수는 `Closeable` 인터페이스를 구현한 객체에 대해 자원을 자동으로 해제해주기 때문에 Java의 `try-with-resources` 구문 못지않게 간결하고 안전하게 자원을 다룰 수 있다.

**References**

- https://www.baeldung.com/kotlin/try-with-resources
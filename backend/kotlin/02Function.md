# 가볍게 알아보는 Kotlin) 2. 함수와 연산자

## Function

### 기본 함수호출

자바에서는 메서드를 사용하지만, 코틀린에서는 다른 프로그래밍 언어에서 처럼 `함수` 개념이 존재합니다.

일반적인 함수 사용법은 `fun 함수명(파라미터){내용}` 이며, 자바 코드로 변환될때는 클래스 내의 메서드로 바뀌게 됩니다.

```kotlin
fun main() {
    printHello()
}

fun printHello() {
    println("Hello Kotlin")
}
```

![image-20220329213033256](/Users/shane/Documents/GitHub/mdblog/backend/kotlin/02Function.assets/image-20220329213033256.png)

> 실행 결과

### Parameter

함수에 파라미터를 넣을때는 자바와는 반대로 `변수명:자료형` 으로 작성 합니다. 재밌게도 자바에서 `int a` 라고 썼지만, 코틀린에서는 `a: Int`라고 작성 해야 합니다.

```kotlin
fun main() {
    var a = 1
    var b = 2
    print("a + b = ${plus(a,b)}")
}

fun plus(a: Int, b: Int): Int {
    return a + b
}
```

![image-20220329220834732](/Users/shane/Documents/GitHub/mdblog/backend/kotlin/02Function.assets/image-20220329220834732.png)

> 실행 결과

#### 변수 이름에 할당

그런데 재밌게도, 자바에서는 파라미터의 순서만으로 각 파라미터를 할당 하지만, 코틀린에서는 파라미터 이름에 변수를 직접 할당 할 수 있습니다.

```kotlin
fun main() {
    var a = 1
    var b = 2
    println("a / b = ${divide(a, b)}")
    println("b / a = ${divide(num2 = a, num1 = b)}")
}

fun divide(num1: Int, num2: Int): Double {
    return num1.toDouble() / num2
}
```

![image-20220329220957754](/Users/shane/Documents/GitHub/mdblog/backend/kotlin/02Function.assets/image-20220329220957754.png)

> 실행 결과

여전히 a, b 의 순서로 파라미터를 넣어 줬지만, num2와 num1 이라고 파라미터를 직접 지정해주면, 각각 원하는 자리에 파라미터가 배정 됩니다.

이렇게 할 바엔 그냥 평소처럼 순서를 지키며 작성하는 것과 뭐가 다른가 싶기도 하겠지만, 파라미터에 default value를 지정 할 경우에는 얘기가 달라집니다.

```kotlin
fun main() {
    println(sum(b = 2))
}

fun sum(a:Int=0, b:Int=1, c:Int=2):Int {
    return a + b + c
}
```

![image-20220329221415751](/Users/shane/Documents/GitHub/mdblog/backend/kotlin/02Function.assets/image-20220329221415751.png)

> 실행 결과

이렇게 a, b, c에 기본값을 주고 특정 파라미터만 값을 변경 하고 싶은 경우에는 직접 파라미터를 할당 하면 편리합니다.

#### Unit

Unit은 자바에서의 void 처럼 반환값이 없을 때 사용합니다.

![image-20220329221848646](/Users/shane/Documents/GitHub/mdblog/backend/kotlin/02Function.assets/image-20220329221848646.png)

return 타입을 따로 명시해주지 않으면 자동으로 반환타입이 Unit 이 되며

![image-20220329222035701](/Users/shane/Documents/GitHub/mdblog/backend/kotlin/02Function.assets/image-20220329222035701.png)

 `:Unit`은 굳이 명시 해 주지 않아도 됩니다.

### Function Overloading

자바에서의 Method Overloading과 마찬가지로, 함수 오버로딩이 가능 합니다.

```kotlin
fun main() {
    overloading();
    overloading(1);
    overloading("Hello");
}

fun overloading(s: String) {
    println("one String Parameter: $s")
}

fun overloading(i: Int) {
    println("one int Parameter: $i")
}

fun overloading() {
    println("No parameter")
}
```

![image-20220329224654286](/Users/shane/Documents/GitHub/mdblog/backend/kotlin/02Function.assets/image-20220329224654286.png)

> 실행 결과

### Local function

코틀린은 펑션 내부에 펑션을 선언하는 지역 함수의 사용이 가능합니다.

```kotlin
fun main() {
    func1()
}

fun func1() {
    println("func1")
    fun func2(){
        println("func2")
        fun func3(){
            println("func3")
        }
        func3()
    }
    func2()
}
```

![image-20220329225313794](/Users/shane/Documents/GitHub/mdblog/backend/kotlin/02Function.assets/image-20220329225313794.png)

당연하게도, 지역 함수들은 그 외부에서는 호출이 불가능 합니다.

## Operator


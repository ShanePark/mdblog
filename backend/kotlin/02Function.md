# 가볍게 읽어보는 Kotlin) 2. 함수와 연산자

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

![image-20220329213033256](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/02Function.assets/image-20220329213033256.png)

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

![image-20220329220834732](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/02Function.assets/image-20220329220834732.png)

> 실행 결과

#### 변수 이름에 할당

그런데 재밌게도, 자바에서는 파라미터의 순서만으로 각 파라미터를 구분해 할당 하지만, 코틀린에서는 파라미터 이름에 변수를 직접 할당 할 수 있습니다.

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

![image-20220329220957754](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/02Function.assets/image-20220329220957754.png)

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

![image-20220329221415751](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/02Function.assets/image-20220329221415751.png)

> 실행 결과

이렇게 a, b, c에 기본값을 주고 특정 파라미터만 값을 변경 하고 싶은 경우에는 직접 파라미터를 할당 하면 편리합니다.

#### Unit

Unit은 자바에서의 void 처럼 반환값이 없을 때 사용합니다.

![image-20220329221848646](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/02Function.assets/image-20220329221848646.png)

return 타입을 따로 명시해주지 않으면 자동으로 반환타입이 Unit 이 되며

![image-20220329222035701](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/02Function.assets/image-20220329222035701.png)

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

![image-20220329224654286](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/02Function.assets/image-20220329224654286.png)

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

![image-20220329225313794](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/02Function.assets/image-20220329225313794.png)

당연하게도, 지역 함수들은 그 외부에서는 호출이 불가능 합니다.

## Operator

### UnaryOperator

- java 에서 처럼 변수 앞에 `+`나 `-`를 붙일 수 있습니다. + 연산자는 아무 변화가 없지만, - 연산자는 부호를 반대로 만듭니다.
- `.UnaryMinus()` 메서드를 사용해 부호를 반대로 할 수도 있습니다.

```kotlin
var a = 1;
var b = -1;

println("+ 연산자는 아무런 변화를 주지 않는다.")
var a1 = +a;
var b1 = +b;
println("a: $a, +a: $a1")
println("b: $b, -b: $b1")

println("\n- 연산자는 부호를 반대로 바꾼다.")
var minusA = -a;
var minusB = -b;
println("a: $a, -a: $minusA")
println("b: $b, -b: $minusB")
println(".unanryMinus() 메서드와 동일한 기능을 한다.")
println("a.unaryMinus() = ${a.unaryMinus()}")
println("b.unaryMinus() = ${b.unaryMinus()}")
```

![image-20220407225226297](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/02Function.assets/image-20220407225226297.png)

> 실행 결과

- not 연산자는 논리 값을 반대로 변경 해 줍니다.

```kotlin
var trueValue = true;
var falseValue = false;
println("\n! 연산자는 불리언 값을 반대로 바꾼다.")
println("!trueValue: ${!trueValue}")
println("!falseValue: ${!falseValue}")
println(".not() 메서드와 동일한 기능을 한다.")
println("trueValue.not() = ${trueValue.not()}")
println("falseValue.not() = ${falseValue.not()}")
```

![image-20220407225439674](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/02Function.assets/image-20220407225439674.png)

> 실행 결과

- java에서와 마찬가지로 증감 연산자도 활용 할 수 있습니다.
- `.inc()` 메서드를 대신 호출 할 수도 있습니다.

```kotlin
println("\n증감 연산자는 java에서와 활용 방법이 동일하다. .inc() 메서드를 호출한다.")
println("a: $a")
println("a++: ${a++}")
println("a: $a")
println("++a: ${++a}")
```

![image-20220407225528585](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/02Function.assets/image-20220407225528585.png)

> 실행 결과

### ArithmeticOperator

- 기본적인 사칙 연산은 java와 동일합니다.

```kotlin
val a = 123
val b = 4

println("기본적인 사칙 연산은 java와 동일")
println("a + b = ${a+b}")
println("a - b = ${a-b}")
println("a * b = ${a*b}")
println("a / b = ${a/b}")
println("a % b = ${a%b}")
```

![image-20220407225624180](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/02Function.assets/image-20220407225624180.png)

> 실행 결과

- java에서는 없는 IntRange가 존재합니다. 반복문을 돌리거나 할 때 유용하게 사용됩니다.

```kotlin
val range:IntRange = 10..20
println("\nIntRange")
println("10..20: $range")
println("range.first = ${range.first}")
println("range.last = ${range.last}")
```

![image-20220407225733115](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/02Function.assets/image-20220407225733115.png)

> 실행 결과

- 비교 연산도 다른 프로그래밍 언어들과 같은 방법으로 사용합니다.

```kotlin
println("\na:$a, b:$b 일때 비교 연산")
println("a==b = ${a==b}")
println("a!=b = ${a!=b}")
println("a>=b = ${a>=b}")
println("a<=b = ${a<=b}")
```

![image-20220407225830724](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/02Function.assets/image-20220407225830724.png)

> 실행 결과

다음 글에서는 제어문에 대해 알아보겠습니다.

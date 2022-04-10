# 가볍게 읽어보는 Kotlin) 3. 제어문

안녕하세요. 지난 글에 이어서 이번에는 코틀린에서의 제어문에 대해 알아보려고 합니다.

기존에 이미 자바에 대해 알고있는 개발자들을 대상으로, 코틀린이 java와 다르다고 하는데 얼마나 많이 그리고 어떤것들이 다른지를 알기 쉽게 간단하게 안내하는걸 목표로 한 시리즈 입니다. 

기본적으로 자바의 문법을 알고 있음을 전제로 하고 어떤 점들이 다른지 간략하게 코틀린의 제어문에 대해 알아보겠습니다.

## 조건문

### IF

if 의 사용은 사실 특별할 게 없기 때문에 거의 같습니다.

자바에서 처럼 if와 else를 활용해서 다양한 조건을 걸어 줄 수 있습니다.

```kotlin
val a1 = 5

if (a1 < 10) {
  println("a1 < 10")
} else if (a1 > 10) {
  print("a1 > 10")
}

if (a1 == 10) {
  println("true")
} else {
  println("false")
}
```

![image-20220410204456932](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/03ControlFlowStatements.assets/image-20220410204456932.png)

> 실행 결과

### 삼항연산자

코틀린에서는 특이하게도 자바와 달리 삼항 연산자가 없습니다. 

그 이유는 코틀린의 특성과 관련이 있는데요. 자바에서의 if-else는 statement(조건식) 이지만 Kotlin에서의 if 는 expression(조건문) 입니다. expression 은 그 자체로 값을 반환하는데요 그렇기 때문에 삼항연산자가 필요 없습니다.

![image-20220410205148041](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/03ControlFlowStatements.assets/image-20220410205148041.png)

> https://kotlinlang.org/docs/control-flow.html

기존의 if 와 else 만으로도 충분히 깔끔하고 직관적인 식을 만들어 낼 수 있기 때문입니다.

삼항연산자에 익숙한 많은 개발자들이 Ternary operator의 추가를 요청 했고 [그에 대한 수많은 토론이 무려 2년 동안 있었지만](https://discuss.kotlinlang.org/t/ternary-operator/2116) 필요 없음으로 결론 내려졌습니다.

```kotlin
val str: String = if (a1 == 5) "5" else "not 5"
```

자바였다면 아래와 같았겠네요

```java
String str = (a1==5) ? "5" : "not 5"
```

사실 삼항 연산자의 가독성이 떨어지는 것을 고려하면 저도 if-else의 사용쪽에 한 표를 주고 싶습니다. 충분히 코드도 짧으니깐요.

### if is an Expression

방금 위에서 저희는 if가 expression 이라는 이야기를 했습니다. 그게 어떤건지 아직 충분히 와닿지 않았을텐데 아래의 코드를 보면 바로 이해가 되실 거라고 생각합니다.

```kotlin
val a1 = 5

val str2:String = if(a1==5) {
  println("process the block then assign")
  "a1 is 5"
}else {
  println("process the block before assign")
  "not 5"
}
println("str2 = $str2")
```

갑자기 보면 이게 도대체 무슨 코드인가 싶을 텐데. 자바와 달리 expression 이기 때문에 가능 한 것 입니다.

if 조건문을 실행 하고 곧바로 값을 할당 해 냅니다.

![image-20220410210945677](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/03ControlFlowStatements.assets/image-20220410210945677.png)

> 실행 결과

if 조건문을 통해 str2에 변수를 바로 할당 하는데, 심지어 그 과정에서 코드블럭을 통해 다른 작업도 먼저 수행이 가능 합니다.

### When

java에서의 스위치문이 코틀린에서는 when 이 되어 돌아왔는데요. 바로 코드를 확인 해 보겠습니다.

```kotlin
val v = Random.nextInt(0, 4)
when (v) {
  1 -> println("v == 1")
  2 -> println("v == 2")
  3 -> println("v == 3")
  else -> println("else v == $v")
}
```

`when` 은 분기 조건이 만족될 때 까지 모든 분기에 대해 인자를 순차적으로 대입 하는데요.

위의 경우에는 랜덤으로 0~3 의 숫자를 뽑고, 상황에 맞게 해당 숫자를 출력해서 보여 주도록 해 보았습니다.

- 2가지 케이스 동시 수행

```kotlin
when (v) {
  0, 1 -> println("0 or 1")
  2, 3 -> println("2 or 3")
  else -> println("else")
}
```

> 뿐만 아니라 여러가지 케이스들을 한번에 다룰 수도 있습니다.

- Double / Float

```kotlin
val d = 12.34

when (d) {
  12.34 -> println("12.34")
  45.67 -> println("45.67")
  else -> println("else")
}
```

> Integer 뿐만 아니라 Double 타입도 when 문을 사용 할 수 있습니다.

- String

```kotlin
val names = arrayOf("철수", "영희", "민수")
when (names[Random.nextInt(0, 3)]) {
  "철수", "영희" -> println("철수 or 영희")
  "민수" -> println("민수")
}
```

> 당연히 String도 가능 합니다.

- IntRange

```kotlin
when (Random.nextInt(0, 5)) {
  in 0..2 -> println("0~2")
  in 3..4 -> println("3~4")
}
```

> IntRange `..` 도 사용 할 수 있습니다.

- Function

```kotlin
fun oddOrEven(num: Int) = when (num) {
    1 -> "홀수(1)"
    2 -> "짝수(2)"
    else -> "그 외"
}
```

함수 형태로 사용 하는 것 또한 가능합니다.

## 반복문

### for

코틀린에서는  IntRange를 사용해서 for문을 돌립니다.

```kotlin
val oneToTen = 1..10
for (i in oneToTen) {
  println("i = $i")
}
```

![image-20220410212231040](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/03ControlFlowStatements.assets/image-20220410212231040.png)

> 실행 결과

2씩 늘리는 것도 가능하고요

```kotlin
val oneToTwentyStepTwo = 1..20 step 2
for (j in oneToTwentyStepTwo) {
  println("j = ${j}")
}
```

![image-20220410212259362](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/03ControlFlowStatements.assets/image-20220410212259362.png)

> 실행 결과

반대로 줄이며 순회 하는 것도 가능합니다.

```kotlin
val tenDownTo1 = 10 downTo 1 step 1
for (k in tenDownTo1) {
  println("k = ${k}")
}
```

![image-20220410212335531](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/03ControlFlowStatements.assets/image-20220410212335531.png)

> 실행 결과

또한, 자바에서 했던 것 처럼 continue나 break 도 모두 사용 할 수 있습니다.

```kotlin
for (i in 1..10) {
  if (i % 2 == 1) continue
  if (i >= 7) break;
  println("i = ${i}")
}
```

![image-20220410212410089](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/03ControlFlowStatements.assets/image-20220410212410089.png)

> 실행 결과

### while

while을 통한 반복문은 자바와 사용법이 동일 합니다.

```kotlin
fun main() {
    var i = 1
    while (i <= 10) {
        println("i++ = ${i++}")
    }

    println()

    do {
        println("i-- = ${i--}")
    } while (i >= 1)

}
```

![image-20220410212511367](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/03ControlFlowStatements.assets/image-20220410212511367.png)

> 실행 결과

Kotlin의 control-flow에 대한 더 자세한 내용은 아래의 링크를 참고 해 주세요. 

이상입니다.

> https://kotlinlang.org/docs/control-flow.html
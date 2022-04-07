# 가볍게 읽어보는 Kotlin) 1.기본 문법 및 변수와 자료형

## Intro

코틀린은 Intelli IDEA를 개발한 JetBrains 사에서 공개한 오픈 소스 프로그래밍 언어 입니다. JVM 위에서 돌아가며 자바와 유사 하지만 간결한 문법과 다양한 기능 추가로 굉장히 호평을 받고 있습니다. 처음 공개된 2011년 에는 그렇게 큰 관심을 받지 못했지만, 오라클이 Java API 저작권으로 좋지 않은 신호를 계속 보이자 Google에서는 2017년 안드로이드의 공식 언어로 Kotlin을 추가 하였습니다. 

![image-20220329204525109](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/01HelloKotlin.assets/image-20220329204525109.png)

그 때 부터 비약적인 관심을 받기 시작한 코틀린은 간결한 문법, 예외처리를 강제하지 않음, Null 안정성 등 으로 비약적인 생산성과 코드량 감소로 입소문을 타기 시작하였고, 이제는 흔히 네카라쿠배로 불리는 대기업 IT회사들을 필두로 여러가지 신규 서비스들이 코틀린으로 작성되고 있습니다.

Python을 처음 배웠을 때 그 간결한 문법에 감탄했었고, 알고리즘 풀이를 할 때에는 자바의 장황한 문법 때문에 타 언어 사용자에 비해 불리하다는 생각도 했었는데 여러모로 매력적이게 느껴지는 언어입니다. 바로 실무 프로젝트에서 사용하긴 힘들지 몰라도 간단한 코딩 문제풀이 등에 사용하며 익숙해지면 후에 큰 무기가 될 수 있을거란 생각에 조금씩 꾸준히 익숙해져 보려고 합니다.

기존에 자바로 개발을 해 왔다면, 코틀린은 이런게 이렇게 다르구나 하며 가볍게 읽을 수 있게 정리를 해 보았습니다.

## 기본 문법

### 세미콜론;

명령문의 마지막에 세미콜론을 붙이는 것은 이제 코틀린에서는 선택 사항입니다. 자바스크립트 처럼 굳이 붙이지 않아도 됩니다.

다만 한줄에 여러 명령문을 입력하려면 매번 명령이 끝날 때마다 `;`을 붙여 구분해 주어야 합니다.

![image-20220328213836006](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/01HelloKotlin.assets/image-20220328213836006.png)

### 주석

Kotlin의 주석은 Java에서의 주석과 그 사용법이 완전히 동일 합니다. 평소처럼 사용 하면 됩니다.

- `//`: 한 줄 주석
- `/* */`: 여러줄 주석

### 출력

이 역시 자바와 거의 같습니다.

- print: `System.out.print`
- println: `System.out.println`
- 자바와 다르게 printf는 없습니다. 대신 값을 출력 할 때는 JSP의 EL표현식 같은 `${}` 를 사용 합니다. 굉장히 편하죠.
- 또한 중괄호 안에 아무런 수식 없이 변수 이름만 들어간다면 중괄호는 생략하고 $만 입력 할 수 있습니다.

```kotlin
fun main() {
    print("한줄")
    print("두줄")
    println("세줄")
    println("네줄")
    println("${5}번째줄")
}
```

![image-20220328213556334](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/01HelloKotlin.assets/image-20220328213556334.png)

> 실행 결과

## 변수

### 정수

int 값을 넘어간다고 해도 끝에 L을 붙이지 않아도 됩니다. 물론 굳이 L을 붙여도 상관은 없습니다.

```kotlin
fun main() {
    // integer
    println(123)

    // even if it's bigger than Integer.MAX_VALUE, does not matter.
    println(10_000_000_000L)
    println(10_000_000_000_000)
}
```

### 부동소수점

```kotlin
println(12.34)
println(56.78f)
```

부동소수점을 입력하면 자동으로 **8바이트 double 타입**이 할당 됩니다. 

4바이트의 float 타입을 사용하고 싶다면 맨 끝에 대문자 혹은 소문자의 `f` 를 붙여 주도록 합니다.

### 문자, 문자열, Boolean 타입

자바와 동일하게 문자는 따옴표, 문자열은 쌍따옴표를 사용하며 논리값은 true / false로 표현합니다.

```kotlin
/**
* Character
*/
println('a')
println('A')

/**
* String
*/
print("Hello World")

/**
* boolean
*/
println(true)
println(false)
```

### Raw String

파이썬에서 보고 조금 부러웠던 건데, Raw String 작성이 가능합니다. 물론 자바에서도 Java12 부터 추가 되기는 했지만  [JEP 326: Raw String Literals](http://openjdk.java.net/jeps/326) 자바에서는 특이하게 Groovy나 Python과 다르게 백틱 \` 을 사용하는데, 코틀린에서는 `"""`를 사용합니다.

```kotlin
println("별 헤는 밤\n계절이 지나가는 하늘에는 가을로 가득 차 있습니다.\n나는 아무 걱정도 없이 가을 속의 별들을 다 헤일 듯합니다.")

/**
* Raw String
*/
println("""
        |별 헤는 밤
        |계절이 지나가는 하늘에는 가을로 가득 차 있습니다.
        |나는 아무 걱정도 없이 가을 속의 별들을 다 헤일 듯합니다.
    """.trimMargin())
```

![image-20220328221154466](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/01HelloKotlin.assets/image-20220328221154466.png)

## 자료형

Kotlin에서는 모든 자료형을 객체로 만들어 관리 하기 때문에 자바에서의 primitive type들도 객체 클래스 타입 입니다.

또한, 코틀린에서는 변수 선언시 val(변경불가) 혹은 var 두가지 키워드를 사용 하는데 각각 자바스크립트의 const와 let 개념입니다.

타입은 생략 가능하며, 생략해서 선언하면 저장되는 값에 따라서 자료형이 자동으로 결정됩니다.

- 정수: Long(8), Int(4), Short(2), Byte(1)
- 부호 없는 정수: ULong(8), UInt(4), UShort(2), UByte(1)
- 실수: Double(8), Float(4)
- 논리: Boolean(1)
- 문자: Char(2)
- 문자열: String

![image-20220328222626307](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/01HelloKotlin.assets/image-20220328222626307.png)

> val로 선언한 변수에 새로운 값을 할당하려고 하면 에러가 발생합니다.

### Null 허용 변수

```kotlin
var nullable: Int? = null
var hundred: Int = 100
var notNull: Int = 200
```

코틀린은 Null safe 를 위해 변수 선언 때 부터 null 허용 여부를 설정 할 수 있습니다.

null을 허용 하는 변수일 경우에만 자료형 뒤에 `?`를 붙여 줍니다.

![image-20220328223205119](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/01HelloKotlin.assets/image-20220328223205119.png)

null을 허용하지 않는 변수라도, null을 허용하는 변수의 끝에 `!!`를 붙여 null을 허용하지 않는 변수의 값으로 변환하여 할당 할 수 있는데요.

```kotlin
var nullable: Int? = null
//    var notNull: Int = null
var hundred: Int = 100
var notNull: Int = 200

println("nullable: $nullable")
println("hundred: $hundred")
println("notNull: $notNull")

notNull = nullable!!

println("notNull: $notNull")
```

![image-20220328224017667](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/01HelloKotlin.assets/image-20220328224017667.png)

 그럴 경우에는 자바에서는 거의 매일 만나던 NullPointException을 다시 만날 수 있습니다.

다음 글에서는 함수와 연산자에 대해 알아보겠습니다.

JetBrains Academy의 무료 강의 링크 달아두며 글 마치겠습니다.

![image-20220329224412149](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/01HelloKotlin.assets/image-20220329224412149.png)

> https://hyperskill.org/tracks/18

Ref

- [윤재성의 Google 공식 언어 Kotlin 프로그래밍 시작하기](https://www.inflearn.com/course/%EA%B5%AC%EA%B8%80-%EA%B3%B5%EC%8B%9D-%EC%BD%94%ED%8B%80%EB%A6%B0-%EC%8B%9C%EC%9E%91%ED%95%98%EA%B8%B0/dashboard)

- https://kotlinlang.org/docs/home.html

# Kotlin) Data class 에 기본 생성자 만들기

## Intro

Kotlin과 SpringBoot를 활용해 스프링 시큐리티를 공부하고 있습니다. 아직은 코틀린에 익숙하지가 않아 자바로 된 코드를 코틀린으로 작성 할 때는 한번씩 꽤나 막히는 부분이 있는데, 이번에는 기본 생성자를 찾지 못한다는 오류가 발생했습니다. 

![image-20220525223129078](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/data-class-default-constructor.assets/image-20220525223129078.png)

> 생성자가 없다는 오류

기본 생성자를 찾지 못해 에러가 발생하고 있었는데요.. 자바였다면 그냥 @NoArgsContructor 라는 롬복 어노테이션 하나만으로도 해결 할 수 있는 이 상황을 코틀린에서는 어떻게 해결 해야 할까요?

문제의 원인이 된 상황과 해결 방법을 확인 해 보겠습니다.

## 원인

일단 발단이 된 코드는 아래와 같습니다.

![image-20220525223259913](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/data-class-default-constructor.assets/image-20220525223259913.png)

jackson 라이브러리를 활용해 request의 reader를 그대로 읽어 AccountDto 로 변환 하는 과정 인데요.

Jackson가 해당 객체(AccountDto)를 어떻게 생성 하고, 직렬화 할 수 있는지를 알아야 하는데 immutable 하게 관리하고 싶어 data 클래스로 만들었다보니 기본 생성자가 없습니다.

그렇기 때문에 Jackson은 해당 요청을 직렬화 해내지 못했고 예외가 발생 한 것 입니다.

## 기본 생성자

그렇다면 코틀린에서 data 클래스의 기본 생성자는 어떻게 만들어야 할까요?

크게 두가지 방법이 있는데요. 주 생성자에서 각각의 파라미터에 기본 값을 설정 하거나 혹은 부 생성자(secondary constructor)를 선언하는 방법 입니다.

### Secondary Constructor

부 생성자를 선언해 문제를 해결 해 보겠습니다.

**변경 전 코드**

```kotlin
data class AccountDto(
    var username: String,
    var password: String,
    var email: String,
    var age: Int,
    var role: String,
)
```

변경 전에는 위와 같이 기본 생성자만 있었는데요.

**변경 후 코드**

```kotlin
data class AccountDto(
    var username: String,
    var password: String,
    var email: String,
    var age: Int,
    var role: String,
) {
    constructor() : this("", "", "", 0, "")
}
```

파라미터가 없는 부 생성자를 만들어주는 방법으로 default 생성자를 만들 수 있습니다.

![image-20220525224209744](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/data-class-default-constructor.assets/image-20220525224209744.png)

> 이렇게 하면 깔끔하게 직렬화 해 내는 것을 확인 할 수 있습니다.

### Primary Constructor

마찬가지로 주 생성자에 기본 값을 넣어 주는 방법도 있습니다.

```kotlin
data class AccountDto(
    var username: String = "",
    var password: String = "",
    var email: String = "",
    var age: Int = 0,
    var role: String = "",
)
```

> 이쪽의 코드가 좀 더 자바와 문맥상 비슷 하고 깔끔 해 보이기도 합니다.

## 기본 생성자 없이 직렬화

그렇다면, 기존의 의도처럼 DTO 클래스를 immutable 하게 관리하며 직렬화도 가능하게 하는 방법은 없을까요?

다행히도 가능합니다. 굳이 var로 바꾸지 않고도, 기본 생성자를 만들지 않고도 Jackson에게 필요한 정보를 전달 할 수 있습니다.

바로 @JsonProperty 어노테이션을 활용 하는건데요 바로 코드를 보여드리겠습니다.

```kotlin
data class AccountDto(
    @JsonProperty("username") val username: String,
    @JsonProperty("password") val password: String,
    @JsonProperty("email") val email: String?,
    @JsonProperty("age") val age: Int?,
    @JsonProperty("role") val role: String?,
)
```

AccountDto 클래스를 immutable 하게 유지하기 위해 모든 변수를 val로 다시 변경하였습니다.

대신 `@JsonProperty` 어노테이션을 통해 해당 객체를 만드는 설명서를 자세하게 기입 해 두면 Jackson이 똑똑하게도 그걸 보고 그대로 객체를 생성 해 줍니다.

자바에서는 생성자에 `@JsonCreator` 어노테이션을 달아주는데, 코틀린에서는 위와같이만 해도 충분 했습니다.

## 마치며

아직 코틀린이 자바만큼 익숙하지는 않아 Best Practice로 작성되었다고 확신 할 수는 없지만 최소한 위에서부터 두 단계를 거치며 첫번째로 버그를 해결하였고 두번째로 원하는 요구사항을 충족시켰습니다. 앞으로도 코틀린에 대해 더 익숙해지며 더 나은 코드가 떠오른다면 해당 글의 코드를 조금씩 수정 하거나 내용을 덧붙이도록 하겠습니다.

감사합니다.

ref

- https://stackoverflow.com/questions/37873995/how-to-create-empty-constructor-for-data-class-in-kotlin-android

- https://stackoverflow.com/questions/30568353/how-to-de-serialize-an-immutable-object-without-default-constructor-using-object

- https://proandroiddev.com/parsing-optional-values-with-jackson-and-kotlin-36f6f63868ef
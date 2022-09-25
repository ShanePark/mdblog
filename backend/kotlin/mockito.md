# [Kotlin] 코틀린에서 Mockito 사용시 final class 문제 해결

## Intro

코틀린으로 코드를 작성하다보면 간혹가다가, 자바에서는 별 문제 없던게 의도대로 동작하지 않을 때가 있습니다.

이번에는 테스트 코드를 작성하다가 스터빙을 하려고 하는데 문제가 있었는데요,

아래와같이 테스트를 하려고 했습니다.

```kotlin
 // When
	val memberContext = Mockito.mock(MemberContext::class.java)
  Mockito.`when`(memberContext.member).thenReturn(member)

  quizService.edit(memberContext, quiz.id!!, quizEditDto)

  // Then
  val findById = quizService.findById(quiz.id!!)
  assertThat(findById.description).isEqualTo(updatedDescription)
  assertThat(findById.answer).isEqualTo(updatedAnswer)
  assertThat(findById.explanation).isEqualTo(updatedExplanation)
  assertThat(findById.examples).hasSize(examples.size)
  for (i in examples.indices) {
    assertThat(findById.examples[i].text).isEqualTo(examples[i])
  }
```

Quiz 엔티티를 업데이트 하는 로직인데, 로그인한 사용자와 요청자가 일치하는지 여부를 확인 할 필요가 있어서 MemberContext를 파라미터로 받습니다. 서비스 로직과는 상관이 없기 때문에 테스트 고립을 위해 memberContext에서 member를 꺼내오는 부분을 스터빙 하려고 했는데, 해당 테스트 코드 실행시 아래와 같이 에러가 나왔습니다.

```kotlin
Cannot mock/spy class kr.quidev.security.domain.MemberContext
Mockito cannot mock/spy because :
 - final class
org.mockito.exceptions.base.MockitoException: 
Cannot mock/spy class kr.quidev.security.domain.MemberContext
Mockito cannot mock/spy because :
 - final class
	at app//kr.quidev.quiz.service.QuizServiceTest.editQuizTest(QuizServiceTest.kt:175)
```

## 원인

### Java

자바에서는 기본적으로 모든 클래스는 상속이 가능 합니다. 상속을 하지 못하게 하려면 클래스를 일일히 final로 선언 해야만 했는데요.

이펙티브 자바의 아이템 19번을 보면, "상속을 고려해 설계하고 문서화 하라. 그렇지 않았다면 **상속을 금지 하라**." 라고 말하고 있습니다. 상속을 고려한 클래스를 설계하는데는 비용이 크게 발생합니다. 문서화도 해 줘야 하며,  일단 한번 문서화 된 내부 사용 패턴과 protected 메서드들은 변화를 주기도 쉽지 않습니다.

심지어 상속을 허용하는 클래스들은 지켜야 할 제약이 추가로 존재하기도 합니다. 부모 클래스의 변경이 자주 일어나는데도 상속을 하게 된다면 **Fragile base class** 문제에 빠지기도 쉽습니다. 

> https://en.wikipedia.org/wiki/Fragile_base_class
>
> 간단히 말해, 부모 클래스에서의 변화가 그를 상속하는 다른 클래스들에 예상치 못한 결과를 초래하는 일을 말 합니다. 보통 객체지향을 처음 배울 때 상속의 단점 및 주의사항으로 반드시 언급 되는 내용 입니다.

그렇기 때문에 상속용으로 설계하지 않은 클래스는 클래스를 final로 선언하는 방법으로 상속을 금지하는 것이 좋습니다.

### Kotlin

위에서 알아보았던 문제를 익히 잘 알고 있던 Kotlin 개발자들은, 무분별한 상속을 막기 위해 애초부터 코틀린에서는 기본적으로 모든 클래스의 기본 변경자가 final이 되도록 못 박았습니다. 상속이 필요한 경우에는 앞에 open 이라는 상속 변경자를 명시적으로 선언 해야 합니다.

- final : 기본 변경자. 상속 불가
- open : 상속 가능. 오버라이드 할 수 있음.
- abstract : 추상클래스. 반드시 오버라이드 해야 함. 스스로 인스턴스화 불가능.

### Mockito

코틀린에서는 기본적으로 모든 클래스가 final로 선언되어 있다는걸 이제 충분히 알게 되었습니다.

그러면 처음으로 돌아와서, 

```kotlin
Mockito cannot mock/spy because :
 - final class
```

에 대해서 알아 보도록 하겠습니다.

Mockito는 AOP에서 사용되는 **메서드 인터셉트**을 사용해 메서드 인보케이션에 대한 정보를 저장하고 불러옵니다. Mockito는 메서드 인터셉트를 위해 Spring AOP나 AspectJ와 같은 AOP 프레임워크를 사용하지는 않고, 런타임 코드 생성 및 조작을 해주는 Byte Buddy와 리플렉션 및 인터셉트를 위해서 는 Objenesis 라이브러리를 각각 사용한다고 합니다.

> Mockito 작동 원리에 대한 글 
>
> https://medium.com/@gorali/how-mockito-works-7d3a2c77da71

위에서 알아 본 것 처럼 final 클래스는 상속이 불가능하며 final 메서드는 오버라이딩이 불가능한데, Mockito가 동작하는 과정에서 그로 인한 제약이 생긴 것 입니다. 다행인건 Mockito 2 부터 이러한 제약을 극복해서, final 클래스도 모킹이 가능해졌습니다.

## 해결

일단 제일 먼저 mockito의 버전을 확인 해 보았습니다.

![image-20220925225157197](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/mockito.assets/image-20220925225157197.png)

스프링 부트 2.7 버전을 사용 하고 있는데, 그에 딸려 온 모키토 버전은 4.5.1입니다. final 제약이 해결된건 Mockito 2 버전 부터기 때문에 추가적으로 Mockito 버전을 변경 해 줄 필요는 없습니다.

대신, 수동으로 final class도 mock 을 할 수 있도록 설정을 변경 해 주어야 합니다.

![image-20220925225819863](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/mockito.assets/image-20220925225819863.png)

> https://github.com/mockito/mockito/wiki/What%27s-new-in-Mockito-2#unmockable

위에서 안내 해 준 대로, `src/test/resources` 하위에 `mockito-extensions` 라는 이름의 디렉터리를 생성 하고, `org.mockito.plugins.MockMaker` 라는 이름의 파일을 생성 해 줍니다. 그 후에는 

```
mock-maker-inline
```

라고 단 한 줄 입력 해서 저장 하면 `MockMaker` 설정은 끝입니다.

![image-20220925230019947](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/mockito.assets/image-20220925230019947.png)

> 안내해주는 대로 따라 합니다.

이후에 테스트 케이스를 다시 실행 해 보면..

![image-20220925230105150](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/mockito.assets/image-20220925230105150.png)

final 클래스임에도 정상적으로 Mock 객체를 생성 해 내는데 성공 했습니다.

## 마치며

최신의 라이브러리 덕분에 큰 어려움을 겪지 않고도 한계를 극복해 낼 수 있었습니다.

아니었다면 어떻게 해결해야 했을까요.. final로 설정 하는 대신 open 을 걸어 주고, 상속을 못하도록 다른 제한을 걸어주거나. 상속 대신 데코레이팅 하도록 우회를 했으면 됐을까 모르겠지만 쉽사리 더 좋은 방법이 떠오르지는 않습니다. 우리가 오픈소스에 더욱 관심을 가져야 하는 이유 입니다.

아무쪼록 `mock-maker-inline` 이라는 한 줄의 텍스트로 테스트가 가능하지긴 하지만 단순히 문제를 해결하고 넘어가는데 그치지 않고, 어떤 문제 때문에 Mocking이 안됐으며, 왜 코틀린에서는 final 변경자가 기본으로 지정 되어 있는지를 한번 더 이해하는 시간이 되었으면 합니다.

이상입니다. 

**References**

- https://stackoverflow.com/questions/14292863/how-to-mock-a-final-class-with-mockito
- https://stackoverflow.com/questions/57536196/cant-mock-final-class-in-test-using-mockito-with-kotlin
- https://www.baeldung.com/mockito-final
- https://antonioleiva.com/mockito-2-kotlin/
- https://medium.com/@gorali/how-mockito-works-7d3a2c77da71
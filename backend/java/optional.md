# [Java] Optional 올바르게 사용하기

## Intro

자바가 8버전으로 넘어오며 추가된 기능들이 굉장히 많이 있습니다. 보통 가장 먼저 언급되는 Lamdba와 Stream API 뿐만 아니라 조금의 차이는 있지만 Joda-Time을 그대로 가져온듯한 날짜와 시간 API, 인터페이스에 추가된 Default 메서드와 static 메서드 등 지금까지 이 기능들 없이 어떻게 코드를 짰을까 싶은 요소들이 굉장히 많습니다. 그중에서도 둘째로 치면 서러워 할 클래스가 있으니 바로 Optional 입니다.

### Optional

![image-20220720231556398](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/optional.assets/image-20220720231556398-16583589580901.png)

> java.util.Optional.java

- 자바 아키텍트인 Brian Goetz는 **결과 값이 없음**을 명확하게 표현 하려는 의도로 Optional을 추가했다고 합니다.

- 자바8 이전까지는 `null`이 그 역할을 해 왔지만, null을 사용할 경우에는 에러가 발생할 가능성이 압도적으로 높기 때문에 그 문제를 해결하기 위해 등장한 것 입니다. 

- API Note 말미에는 <u>Optional 그 자체에 null을 할당하는 실수를 절대 하지 말라</u> 는 당부도 빼놓지 않았습니다.

사실 Optional 객체는 공개 메서드의 종류도 많지 않고, 얼핏 보면 `isPresent()`로 null 체크가 가능하고 `orElse()` 로 값을 꺼내오면 되는구나 싶은 단순해보이는 개념 때문에 사용하기 쉽다고 오해할 수 있는데 사실 Optional을 올바르게 사용하는건 꽤나 <u>까다롭습니다.</u> 오히려 과도한 남용으로 인해 안쓰는 것만 못한 케이스가 매우 빈번하게 일어나고 있습니다.

올바르게 사용하기 어려울 뿐 아니라 잘못 사용할 경우에는 오히려 성능상 손해도 볼 수 있기 때문에 지금부터 Optional에 대해 함께 알아보고 앞으로는 **꼭 필요한 곳**에서만 **올바르게** 사용 할 수 있도록 노력 하는게 좋겠습니다.

## 1. Optional 변수에 Null을 할당하지 말것

Brian Goetz가 API 문서에 직접 남길 정도로 강조한 내용인데요, Optional 변수는 항상 **Optional 인스턴스**를 가리키고 있어야 합니다. 자바8 API에는 없던 코멘트가 후에 추가 된걸 보면 첫 발표 이후 Optional에 `null` 할당하는 경우가 굉장히 많았던 것 으로 추측됩니다.

**나쁜 예**

```java
public Optional<Member> findMember() {
    Optional<Member> emptyMember = null;
}
```

**올바른 예**

```java
public Optional<Member> findMember() {
    Optional<Member> emptyMember = Optional.empty();
}
```

## 2. `get()`은 값이 존재하는게 확실할 때에만.. 아니 그냥 쓰지 말것!

> 사실 자바 10에서 `orElseThrow()` 가 추가되었기 때문에 `.get()` 메서드는 진작에 @Deprecated 되었어도 할말이 없습니다.

**나쁜 예**

```java
Optional<Member> member = findMember(id);
Member findMember = member.get();
```

get을 곧바로 사용하고 있는데요. 이 경우 member가 empty 인 경우에는 java.util.NoSuchElementException 이 발생합니다.

**개선 중**

```java
Optional<Member> member = findMember(id);
if (member.isPresent()){
  findMember = member.get();
} else {
  findMember = null;
}
```

이번에는 isPresent로 확실하게 값이 있음을 보장 한 후에 get을 꺼내 왔습니다. 코드에 특별히 문제는 없지만 추후 언젠가는 get이 Deprecated 될 것으로 예상되기때문에 optional을 올바르게 사용하기 위해 코드를 한번 더 변경 해 보겠습니다.

**올바른 예**

```java
Optional<Member> member = findMember(id);
Member findMember = member.orElse(null);
```

위의 개선중 예제는 문제가 있는 코드는 아니지만 orElse를 활용하면 보다 직관적이고 간결한 코드 작성이 가능합니다.

예제에서는 null을 할당했지만, 미리 불변 상수로 준비해둔 default value를 손쉽게 할당 할 수도 있습니다.

### 예외를 던져야 할 경우

반면, 이전의 get을 정말로 **올바르게** 사용 하고 있었다면 자바 10 이후로는 그 역할을 `orElseThrow()` 가 해주고 있습니다. get과 orElseThrow의 동작은 완전하게 동일하지만, 메서드 명이 의도하는 바를 확실하게 표현하고 있기 때문에 오해를 없애기 위해 get 대신 orElseThrow를 사용 하는것이 좋겠습니다.

**나쁜 예**

```java
Optional<Member> member = ...;
if (member.isPresent()){
  return member.get();
} else {
  throw new NoSuchElementException();
}
```

**올바른 예**

```java
Optional<Member> member = ...;
return member.orElseThrow();
```

## 3. 컬렉션 / 배열을 담지 말것

모두 알고 계시는 것 처럼, Optional은 **결과 값이 없음**을 명확하게 표현 하려는 의도로 추가되었습니다. 그렇다면 비어있는 컬렉션이나 배열을 표현할때에도 Optional을 사용 하는게 맞을까요?

사실 컬렉션과 배열은 자체적으로 이미 비어있는 상태를 표현할 준비가 되어있습니다. 

![image-20220721082602845](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/optional.assets/image-20220721082602845.png)

emptyList()나 emptyMap()등을 활용하면 굳이 비싼값을 지불하고 Optional이라는 컨테이너로 한번 더 포장 할 필요 없이 비어있는 상태를 나타낼 수 있습니다. 배열도 마찬가지로 크기가 0인 배열로 비어있는 상태를 손쉽게 표현 할 수 있습니다.

**나쁜 예**

```java
List<Member> members = findMembers(); // null을 반환 할 수 있음.
return Optional.ofNullable(members);
```

**올바른 예**

```java
List<Member> members = findMembers();
return members= == null ? Collections.emptyList() : members;
```

## 4. Optional.of()와 Optional.ofNullable() 을 혼동하지 말 것

Optional.of() 메서드는 파라미터로 null을 받을 경우에 NullPointerException이 발생합니다. null일 수 있는 값으로 Optional 객체를 생성 할 때에는 반드시 ofNullable을 사용 해야 합니다.

**나쁜 예**

```java
Member member = findMember(); // null 일 수 있음
return Optional.of(member) // member가 null일 경우 NullPointerException 발생
```

**올바른 예**

```java
Member member = findMember(); // null 일 수 있음
return Optional.ofNullable(member) // NPE가 발생할 가능성 없음.
```

## 5. primitive 타입을 `Optional<T>` 형태로 쓰지 말것

래핑 객체가 필요한 특정 상황이 아닌 이상은 `Optional<Integer>` 형태로 사용하지 말고 non-generic 형태인 OptionalInt, OptionalLong 혹은 OptionalDouble 을 사용해야합니다.

안그래도 이미 Optional로 한단계 감싸며 메모리와 성능적으로 많은 손해를 봤는데 원시타입 값 마저도 래핑을 하고, 사용 할 때 마다 박싱과 언박싱 과정을 거친다면 그 손해가 더욱 커지게 됩니다. 이를 대비해 원시 타입을 바로 래핑해둔 `OptionalInt`  등이 이미 준비되어 있기 때문에 우리는 편하게 사용만 하면 됩니다.

**나쁜 예**

```java
Optional<Integer> count = Optional.of(20);
Optional<Long> price = Optional.of(5_000_000_000L);
Optional<Double> average = Optional.of(10.5d);
```

**올바른 예**

```java
OptionalInt count = OptionalInt.of(20);
OptionalLong price = OptionalLong.of(5_000_000_000L);
OptionalDouble average = OptionalDouble.of(10.5d);
```

## 6. 동등 비교를 위해 Unwarp 하지 말것

Optional로 감싸진 두개의 값을 서로 비교 하기 위해서는 각각의 값을 꺼내어 비교해야 한다고 오해하기 쉽습니다.

하지만 Optional.equals()의 코드를 확인 해 보면

![image-20220721085124464](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/optional.assets/image-20220721085124464.png)

참조 대상을 비교할 뿐만 아니라 비교하는 대상이 Optional일 경우에는 각자의 value를 비교해 주기 때문에 굳이 값을 꺼낼 필요가 없습니다. 그렇다고해서 Optional끼리 동등 연산(`==`) 을 사용해서는 안됩니다. 꼭 equals를 호출해 비교해 주세요.

## 마치며

사실 지금까지 알아본건 Optional의 아주 기본적인 사용 방법들에 불과합니다. 

위에서는 다루지 않은 ifPresent() 나 orElseGet() 등의 코드를 더 간결하고 직관적으로 작성할 수 있게 도와줄 고차원 메서드들도 있으며 자바9에서 추가된 Optional.stream() 기능 덕에 Stream API를 활용한 메서드 체인형태로 코드를 작성 할 수도 있게 되었습니다. 그 외에도 자바 11에서 `.isEmpty()`가 추가 되어 비어있는 상태 확인을 위해 `.isPresent()`를 한 뒤에 not 연산을 할 필요도 없어 졌습니다.

Optional 객체는 처음 얼핏 봤을 때의 느낌과는 전혀 다르게 올바르게 사용하기에는 꽤나 까다로운 존재임이 분명 합니다. 지금까지 코드를 작성할때 Optional 을 어떻게 사용해왔는지 되돌아보고 올바른 사용방법에 대해 고민 해 본 시간이었습니다.

이상입니다.

**Reference**

- https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html
- https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Optional.html
- https://dzone.com/articles/using-optional-correctly-is-not-optional
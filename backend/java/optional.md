# [Java] Optional 올바르게 사용하기

## Intro

자바가 8버전으로 넘어오며 추가된 기능들이 굉장히 많이 있습니다. 보통 가장 먼저 언급되는 Lamdba와 Stream API 뿐만 아니라 조금의 차이는 있지만 Joda-Time을 그대로 가져온듯한 날짜와 시간 API, 인터페이스에 추가된 Default 메서드와 static 메서드 등등 지금까지 이 기능들 없이 어떻게 코드를 짰을까 싶은 요소들이 굉장히 많습니다. 그중에서도 둘째로 치면 서러워 할 클래스가 있으니 바로 Optional 입니다.

### Optional

![image-20220720231556398](/Users/shane/Documents/GitHub/mdblog/backend/java/optional.assets/image-20220720231556398.png)

> java.util.Optional.java

Brian Goetz는 **결과 값이 없음**을 명확하게 표현 하려는 의도로 Optional을 추가했다고 합니다.

자바8 이전까지는 `null`이 그 역할을 해 왔지만, null을 사용할 경우에는 에러가 발생할 가능성이 압도적으로 높기 때문에 그 문제를 해결하기 위해 등장한 것 입니다. API Note 말미에는 <u>Optional 그 자체에 null을 할당하는 실수를 절대 하지 말라고</u> 강조하고 있네요.

사실 Optional 객체는 사용가능한 메서드의 종류도 많지 않고, 얼핏 보면 `isPresent()`로 null 체크가 가능하고 `orElse()` 로 값을 꺼내오면 되는구나 싶은 단순해보이는 개념 때문에 사용하기 쉽다고 오해할 수 있는데 사실 Optional을 올바르게 사용하는건 <u>전혀 만만한 일이 아닙니다.</u> 오히려 과도한 남용으로 인해 안쓰는 것만 못한 케이스가 매우 빈번하게 일어나고 있습니다.

까다로울 뿐만 아니라 성능상 손해도 볼 수 있기 때문에 지금부터 Optional에 대해 함께 알아보고 앞으로는 **꼭 필요한 곳**에서만 **올바르게** 사용 할 수 있도록 노력 하는게 좋겠습니다.

## 1. Optional 변수에 Null을 할당하지 말것

Brian Goetz가 API 문서에 직접 남길 정도로 강조한 내용인데요 Optional 변수는 항상 Optional 인스턴스를 가리키고 있어야 합니다.

자바8 API에는 없던 코멘트를 따로 추가 한걸 보면 발표 이후 Optional에 null 할당하는 경우가 굉장히 많았던 것 으로 추측됩니다.

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

## 2. `get()`은 값이 존재하는게 확실할 때 에만 호출.. 아니 그냥 쓰지 말것!

> 사실 자바 10에서 `orElseThrow()` 가 추가되었기 때문에 `.get()` 메서드는 @Deprecated 되었어도 할말이 업습니다.

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

위의 개선중 예제도 문제가 있는 코드는 아니지만 orElse를 활용하면 보다 직관적이고 간결한 코드 작성이 가능합니다.

예제에서는 null을 할당했지만, 미리 불변 상수로 준비해둔 default value를 손쉽게 할당 할 수도 있습니다.

### 예외를 던져야 할 경우

반면, 이전의 get을 정말로 **올바르게** 사용 하고 있었다면 자바 10 이후로는 그 역할을 `orElseThrow()` 가 해주고 있습니다. 완전 동일하게 작동 하지만 메서드 명이 의도하는 바를 확실하게 표현하고 있기 때문에 get 대신 orElseThrow를 사용 하는것이 좋습니다.

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

## 3. 컬렉션을 담지 말것







Reference

- https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html
- https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Optional.html
- https://dzone.com/articles/using-optional-correctly-is-not-optional
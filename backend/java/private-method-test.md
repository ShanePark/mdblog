# JAVA) Private method를 테스트 하는법

## Intro

간단하게 만든 private 메서드가 잘 작동하는지 궁금했습니다. 기존에 이미 작동하고 로직의 계속 반복되던 부분을 private 메서드로 따로 추출해서 반복을 제거 하려는 의도 였는데.. 이게 쏘아올린 작은 공이 생각보다 많은 생각을 하게 만들었습니다.

일단, 일반적인 방법으로는 private 메서드를 테스트 해 볼 수 없었는데요, 애초에 호출을 하지 못하니 테스트도 불가능한게 당연합니다.

### private method를 테스트 해야할까?

TDD의 아버지이자, Junit의 창시자인 켄트벡은 2020년 어느날 트위터에 아래의 링크를 남겼습니다.

![image-20220509151526826](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/private-method-test.assets/image-20220509151526826.png)

> http://shoulditestprivatemethods.com/

해당 링크에 방문 해 보면, 하얀 바탕의 한 가운데에 **NO**라고 96px의 큰 글자만을 남겨 두었는데요

![image-20220509152254598](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/private-method-test.assets/image-20220509152254598.png)

F12를 눌러 소스를 확인 해 보면

![image-20220509151708205](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/private-method-test.assets/image-20220509151708205.png)

private 메서드의 테스트를 고려 할 때 마다, 우리의 코드는 책임이 제대로 할당되지 않았다고 외치고 있으니 좀 들으라고 합니다.

그렇습니다. 사실 애초에 해당 private 메서드를 사용하는 public 메서드에 대한 테스트가 애초에 잘 작성되어 있었고, 문제없이 통과 했다면 굳이 private method를 따로 테스트 해 볼 필요도 없는 것 이었습니다.

## Private method 테스트

### Java reflection

그럼에도 불구하고 private 메서드를 테스트 하고 싶다면, 자바의 리플렉션을 활용해 해결 가능합니다.

![image-20220509153350610](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/private-method-test.assets/image-20220509153350610.png)

> private 메서드를 테스트 하지 못하는 상황

returnOne 메서드의 접근 제어자가 private으로 되어 있기 때문에 테스트 코드에서 호출을 하지 못하고 있습니다. 이 때, Reflection을 사용하도록 자바 테스트 코드를 살짝 변경 해 줍니다.

Method 클래스는 java.lang.reflect 패키지에 위치합니다.

```java
@Test
public void test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Capsule capsule = new Capsule();
    Method method = Capsule.class.getDeclaredMethod("returnOne");
    method.setAccessible(true);
    assertThat(method.invoke(capsule)).isEqualTo(1);
}
```

리플렉션을 사용 하더라도 private 메서드에는 기본적으로 접근 하지 못하니

![image-20220509153832721](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/private-method-test.assets/image-20220509153832721.png)

 `method.setAcceisble(true)` 를 꼭 호출해 접근 가능하게 변경 해 주어야 합니다.

![image-20220509153924265](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/private-method-test.assets/image-20220509153924265.png)

> private 메서드를 별 문제없이 테스트 성공했습니다.

### Parameter

이번에는 파라미터도 전달 해 테스트를 해 보겠습니다.

```java
Method method = Capsule.class.getDeclaredMethod("echo", String.class);
```

일단 처음에 Method 객체를 생성 할 때, 파라미터로 받을 클래스들을 가변 인자로 순서대로 입력 해 줍니다.

![image-20220509154220610](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/private-method-test.assets/image-20220509154220610.png)

> 파라미터를 받는 private method의 테스트도 문제없이 성공합니다.

파라미터를 포함한 테스트 코드는 아래와 같습니다.

```java
@Test
public void paraTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Capsule capsule = new Capsule();
    Method method = Capsule.class.getDeclaredMethod("echo", String.class);
    method.setAccessible(true);
    assertThat(method.invoke(capsule, "Hello test")).isEqualTo("Hello test");
}
```

### ReflectionTestUtils

사실상 지금은 자바를 삼켜버렸다고 평가받고있는 스프링 프레임워크를 사용하다면, 거기에 포함되어 있는 ReflectionTestUtils를 사용해 조금 더 쉽게 테스트를 할 수 있습니다. 

Generic을 사용하기때문에 타입 캐스팅도 따로 필요 없고, setAccessible을 해 줄 필요도 없습니다.

같은 내용의 테스트를 진행 할 때 아래의 코드로 간단하게 표현 됩니다.

![image-20220509154916671](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/private-method-test.assets/image-20220509154916671.png)

> 위에 있는 paraTest 에 비해 테스트 코드가 훨씬 간결하고 직관적 입니다.

```java
@Test
public void reflectionTestUtils() {
    Capsule capsule = new Capsule();
    assertThat(ReflectionTestUtils.<String>invokeMethod(capsule, "echo", "test123")).isEqualTo("test123");
}
```

지금까지 Private 접근자로 작성된 메서드를 테스트 하는 방법에 대해 알아보았습니다. 메서드 뿐만 아니라, private 으로 선언된 필드의 테스트가 필요할 때에도 마찬가지로 리플렉션을 활용 할 수 있습니다.

이상입니다.
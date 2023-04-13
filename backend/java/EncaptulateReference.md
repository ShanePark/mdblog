# Java) 컬렉션이나 배열같은 참조 변수의 캡슐화

## Intro

<img src=https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/EncaptulateReference.assets/image-20220123170650158.webp width=750 height=623 alt=1>

> 행정안전부 소프트웨어 개발보안 가이드
>
> https://www.mois.go.kr/frt/bbs/type001/commonSelectBoardArticle.do;jsessionid=TjAX2IwVk6hpONx8dKSZ4VTj.node10?bbsId=BBSMSTR_000000000015&nttId=88956

지난 주에 행정안전부의 소프트웨어 개발 보안 가이드를 읽다 보니 꽤나 인상적인 부분이 있었습니다. 

자바의 캡슐화에 대해 단순히 private으로 변수를 생성하고 그에대해 Setter만 막아 두면, 해당 변수는 외부에서 변경이 불가능 하다고 너무나도 당연하게 여기고 있었는데 해당 문구를 보고 잠깐 고민을 해보니 자바의 캡슐화에 대해 너무 안일하게 생각하고 있었구나 싶었습니다.

## 캡슐화의 허점

### Public 메소드로 반환된 Private 배열

그럼 위에서 말하는 Public 메소드에서 반환된 private 배열이 왜 문제가 되는지 간단한 코드를 통해 알아 보겠습니다.

**Capsule.java**

```java
package com.tistory.shanepark.collection;

class Capsule {
    private final String[] arr;

    Capsule(String... values) {
        this.arr = values;
    }

    public String[] getArr() {
        return arr;
    }
}

```

> Capsule 이라는 이름의 클래스를 만들어 보았습니다. 단순하게 private 프로퍼티로 arr이라는 이름의 스트링 배열을 가지고 있으며 생성할 때 String 가변인자를 받아 해당 스트링들을 arr에 담습니다. Setter를 따로 선언하지 않았기 때문에 언뜻 보면 처음 결정한 arr 데이터를 외부에서 절대 변경할 수 없도록 캡슐화가 된 것 같습니다.

바로 테스트케이스를 만들어 확인을 해 봅니다.

**CapsuleTest.java**

```java
class CapsuleTest {

    @Test
    public void testArr() {
        Capsule capsule = new Capsule("hello", "world");
        String[] arr = capsule.getArr();
        arr[0] = "modified";

        Assertions.assertThat(capsule.getArr()).containsExactly("hello", "world");

    }

}
```

 `hello`와 `world` 를 인자로 capsule 객체를 생성 합니다. 그리고는 `hello`와 `world`를 정확히 포함하고 있는지 확인하는 테스트 케이스 입니다.

중간에 `modified`로 캡슐 내부의 데이터를 변경하려고 시도 했는데 과연 우리의 캡슐화는 변경을 잘 막아 냈을까요?

![image-20220123181007361](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/EncaptulateReference.assets/image-20220123181007361.webp)

> 아니요

테스트 결과, 있어야 할 `hello`는 없고 엉뚱한 `modified` element가 발견되었다고 합니다.

분명, private final로 데이터를 변경하지도 못하고, 읽기만 할 수 있게 했지만 우리가 변경하지 못하게 한건 arr 배열의 참조일 뿐 이지, arr 배열 자체를 변경하는 건 막지 못했습니다.

결론적으로, private으로 캡슐화 했다고 믿고 있던 데이터는 Public 한 Getter에 의해 변조 되었습니다.

### Public 메소드로 반환된 Collection 객체

이번에는 배열이 아닌 List를 대상으로 같은 테스트를 진행 해 보았습니다.

### Capsule.java

```java
class Capsule {
    private final String[] arr;
    private final List<String> list;

    Capsule(String... values) {
        this.arr = values;
        this.list = Arrays.asList(values);
    }

    public String[] getArr() {
        return arr;
    }

    public List<String> getList() {
        return list;
    }
}

```

### CapsuleTest.java

```java
class CapsuleTest {

    @Test
    public void testArr() {
        Capsule capsule = new Capsule("hello", "world");
        Assertions.assertThat(capsule.getArr()).containsExactly("hello", "world");

        List<String> list = capsule.getList();
        list.set(0, "modified");
        Assertions.assertThat(capsule.getList()).containsExactly("hello", "world");

    }

}
```

과연 List도 Public 접근자를 통해 수정이 가능 했을까요?

![image-20220123182207262](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/EncaptulateReference.assets/image-20220123182207262.webp)

> 캡슐화 실패

public 접근자로 열려있는 Getter를 통해 List를 받아 온 뒤, 거기에서 setter를 호출하니 대책없이 당해 버렸습니다.

## 해결책

그렇다면 참조변수는 캡슐화를 통한 보호가 전혀 불가능 한 것일 까요? 그렇지 않습니다.

Getter로 데이터를 요구 할 때 참조를 그대로 보내지 않으면 됩니다.

**Capsule.java**

```java

class Capsule {
    private final String[] arr;
    private final List<String> list;

    Capsule(String... values) {
        this.arr = values;
        this.list = Arrays.asList(values);
    }

    public String[] getArr() {
        return Arrays.copyOf(arr, arr.length);
    }

    public List<String> getList() {
        return new ArrayList<>(list);
    }
}

```

이번에는 각각 배열과 리스트의 값을 복사해서 반환하도록 Getter를 수정 해 보았습니다.

**CapsuleTest.java**

```java
class CapsuleTest {

    @Test
    public void testArr() {
        Capsule capsule = new Capsule("hello", "world");
        String[] arr = capsule.getArr();
        arr[0] = "modified";
        Assertions.assertThat(capsule.getArr()).containsExactly("hello", "world");

        List<String> list = capsule.getList();
        list.set(0, "modified");
        Assertions.assertThat(capsule.getList()).containsExactly("hello", "world");

    }

}
```

그러고 아까 와 같은 테스트를 다시 한번 실행 해 봅니다.

![image-20220123183203895](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/EncaptulateReference.assets/image-20220123183203895.webp)

> 성공

Public getter를 통해 받아온 참조 변수를 이용해 변조를 시도 했지만, 실제 캡슐화 시킨 데이터가 변경되지 않음이 확인 되었습니다.

## 추가 및 제거 메서드

Setter로 통째로 변경하는 대신에, List에 값을 하나씩 추가하거나 제거 할 수 있는 public 메서드를 사용 할 수 있습니다.

**Capsule.java**

```java
class Capsule {
    private final List<String> list;

    Capsule(String... values) {
        this.list = Arrays.asList(values);
    }

    public List<String> getList() {
        return new ArrayList<>(list);
    }

    public void add(String str) {
        this.list.add(str);
    }

    public void delete(String str) {
        this.list.remove(str);
    }
}

```

이번에는 배열에 대한 내용은 모두 지워버리고 List만 남겨 두었습니다. 대신 add 와 remove 메서드를 추가 해 보았는데요.

테스트는 아래와 같습니다.

```java
package com.tistory.shanepark.collection;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class CapsuleTest {

    @Test
    public void testArr() {
        Capsule capsule = new Capsule("hello", "world");

        List<String> list = capsule.getList();
        list.set(0, "modified");
        Assertions.assertThat(capsule.getList()).containsExactly("hello", "world");

        capsule.add("!");
        Assertions.assertThat(capsule.getList()).contains("!");

        capsule.delete("world");
        Assertions.assertThat(capsule.getList()).containsExactly("hello", "!");
    }

}

```

그런데 테스트를 실행 하니..

![image-20220123184549770](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/EncaptulateReference.assets/image-20220123184549770.webp)

> UnsupportedOperationException이 발생 하였습니다.

![image-20220123184809532](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/EncaptulateReference.assets/image-20220123184809532.webp)

> 해당 List가 add 메서드를 지원하지 않을 경우에 UnsupportedOperationException을 발생한다고 되어 있는데요

![image-20220123185005692](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/EncaptulateReference.assets/image-20220123185005692.webp)

> 그래서 asList 메서드를 확인 해 보니, Return a `FIXED-SIZE` list 라고 적혀 있었습니다. 해당 메서드를 통해 만든 리스트는 크기가 고정되어 있기 때문에 add 메서드 호출시 예외가 발생 한 것 이었습니다.

그래서 생성자를 아래와 같이 변경하고 다시 테스트를 진행 해 봅니다.

```java
Capsule(String... values) {
  this.list = Arrays.stream(values).collect(Collectors.toList());
}
```

![image-20220123185420060](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/EncaptulateReference.assets/image-20220123185420060.webp)

> 이제 통과를 합니다.

## 정리

이상으로 참조 변수의 캡슐화에 대해 간단하게 알아 보았습니다.

public 접근자로 열려있는 Getter를 통해 private으로 선언된 데이터를 변경할 수 있다는 점에서 확실히 조심해야 할 필요가 있겠습니다.

특히, 조회만 할 수 있고 변경을 못하도록 막고 싶다면 Getter에서 해당 요소를 그대로 반환하는게 아닌, 복사본을 넘겨야 한다는걸 명심해야 겠습니다. 
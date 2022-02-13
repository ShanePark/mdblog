# JAVA) Equals를 override 할 경우 hashCode도 오버라이드 해야 하는 이유

## Intro

몇일 전 Equals를 Override 해서 사용 하던 중, hashCode를 오버라이드 하지 않아 문제가 있었습니다.

많은 java 책들에게 equals의 오버라이드 시에는 hashCode도 꼭 함께 오버라이드 하라고 하는데, 정작 그 이유를 알지 못해 간과 하고 있었는데요 그러다 마침내 문제에 봉착 했습니다. equals로 비교 했을 때 같은 값이기 때문에, Set에 담을 때, 같은 값으로 인식되며 담기지 않아야 하는데 또 담겨버리는 것 이었습니다.

이로인해 약간의 혼란을 겪은 후로 왜 Equals를 오버라이드 할 때에는 hashCode도 함께 오버라이드 해 줘야 하는 이유에 대해 확실하게 깨달을 수 있었습니다.

이번 경험을 통해 익힌 내용을 쉽고 간단한 예제 코드를 통해 풀어 보도록 하겠습니다. 

![image-20220213211444608](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/equalsHashcode.assets/image-20220213211444608.png)

> IntelliJ IDEA에서는 equals를 Generate 하려고 하면, 자동으로 equals() and hashCode()가 나오며 함께 오버라이드 하도록 유도 해 줍니다.

## 문제상항

### 코드

제가 코드를 작성 하던 중 문제가 되었던 부분을 간단하게 재현 해 보았습니다.

**EqualsHashcode.java** 코드 전문

```java
package com.tistory.shanepark.object;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class EqualsHashcode {

    @Test
    void test() {
        MyPair a = new MyPair(1, 2);
        MyPair b = new MyPair(2, 1);

        Set<MyPair> set = new HashSet<>();
        set.add(a);
        set.add(b);
        Assertions.assertThat(a).isEqualTo(b);
        Assertions.assertThat(a.equals(b)).isTrue();
        Assertions.assertThat(set.size()).isEqualTo(1);

    }

    private class MyPair {
        int num1;
        int num2;

        public MyPair(int num1, int num2) {
            this.num1 = num1;
            this.num2 = num2;
        }

        @Override
        public boolean equals(Object o) {
            MyPair p = (MyPair) o;
            if (p.num1 == num1 && p.num2 == num2) {
                return true;
            } else if (p.num1 == num2 && p.num2 == num1) {
                return true;
            } else {
                return false;
            }
        }

    }

}

```

쉬운 코드지만 간단하게 코멘트를 해 보겠습니다.

- MyPair 라는 클래스를 정의 하였습니다. 
- MyPair 클래스는 두개의 int 값을 property로 가지게 됩니다.
- 각기 다른 MyPair가 가지고 있는 두 수가 같다면 같은 객체로 인식합니다.
- num1과 num2의 순서는 상관 없습니다.



```java
private class MyPair {
  int num1;
  int num2;

  public MyPair(int num1, int num2) {
    this.num1 = num1;
    this.num2 = num2;
  }

  @Override
  public boolean equals(Object o) {
    MyPair p = (MyPair) o;
    if (p.num1 == num1 && p.num2 == num2) {
      return true;
    } else if (p.num1 == num2 && p.num2 == num1) {
      return true;
    } else {
      return false;
    }
  }

}
```

MyPair 부분만 떼 보았습니다. num1, num2를 프로퍼티로 가지고 있으며 equals를 재 정의 하였습니다.

<br><br>

```java
@Test
void test() {
  MyPair a = new MyPair(1, 2);
  MyPair b = new MyPair(2, 1);

  Set<MyPair> set = new HashSet<>();
  set.add(a);
  set.add(b);
  Assertions.assertThat(a).isEqualTo(b);
  Assertions.assertThat(a.equals(b)).isTrue();
  Assertions.assertThat(set.size()).isEqualTo(1);

}
```

테스트 코드 부분입니다.

a 와 b 페어를 각각 만들었습니다만 제가 재정의 한 equals에 따르면 둘은 같은 객체입니다.

따라서 `a.equals(b)` 는 true를 반환해야 하며, set에 a와 b를 각기 담는다고 해도 set의 사이즈는 1이 되어야 합니다.

하지만 test 결과는 의도대로 되지 않았습니다.

![image-20220213214440557](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/equalsHashcode.assets/image-20220213214440557.png)

equals 부분에는 문제가 없었으나, set의 사이즈가 2가 되는 참사가 일어났습니다.

### 원인

이유는 간단합니다. HashSet에 add 할 때는 hashCode 가 일치하지 않으면 동일한 객체로 보지 않습니다.

a와 b의 hashCode를 한번 비교 해 보겠습니다.

```java
Assertions.assertThat(a.hashCode()).isEqualTo(b.hashCode());
```

![image-20220213214924748](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/equalsHashcode.assets/image-20220213214924748.png)

a의 hashCode() 결과값은 `394721749` 가 나왔지만 b의 hashCode() 결과값은 `282828951` 가 나왔습니다.

이로인해 발생 한 문제였으며 HashSet 뿐만 아니라 HashMap을 사용 할 때에도 같은 문제가 발생 합니다.

### hashCode만 Override 한다면

방금 살펴 본 코드는 equals만 재정의 한 경우 입니다. 반대로 hashCode만 재정의 한 경우에는 어떤 일이 일어날까요?

HashSet의 contains 코드를 한번 살펴 보겠습니다.

![image-20220213220110093](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/equalsHashcode.assets/image-20220213220110093.png)

> 내부적으로 HashMap을 사용하며 containsKey를 호출 합니다.

![image-20220213220144255](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/equalsHashcode.assets/image-20220213220144255.png)

> HashMap의 containsKey는 getNode 메서드의 호출 경과가 null이 아닌지를 비교합니다.

이제 여기서 중요한 getNode 메서드를 살펴 볼 차례 입니다.

![image-20220213220247483](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/equalsHashcode.assets/image-20220213220247483.png)

> 일단 hash 값을 먼저 비교 해 본 뒤에 key Object의 equals 메서드를 호출해 비교합니다.

여기서 중요한게 equals 메서드를 호출해 비교한다는 건데요, equals 메서드가 Override가 되지 않았다면 기본적으로 객체의 equals 비교는 모두 아시는 것 처럼

![image-20220213220724989](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/equalsHashcode.assets/image-20220213220724989.png)

동등 비교를 하게 됩니다. 이 경우에는 객체의 Reference를 비교하기 때문에 같은 메모리상에 저장된 객체가 아니라면 우리가 논리적으로 아무리 '동일'하다고 여기는 객체들의 관계도 같지 않다고 처리가 되며, Set이나 Map에서는 원하는 객체를 영영 찾지 못하는 불상사가 일어나게 됩니다. 

언제나 equals와 hashCode를 꼭 함께 재정의 해줘야만 한다고 하면 꼭 그런건 아니라는 의견도 있을 수 있겠습니다. equals를 사용 하지만 hash 값을 사용하는 Collection은 사용 하지 않다는 자신이 있다는 혹자가 있을 수도 있겠죠.

하지만 소프트웨어 개발 과정에서는 여럿이서 알게 모르게 협업하게 되는 경우가 많습니다. 또한 소프트웨어의 중요한 원칙중 하나인 `Open Close Principal` 즉, 변경에는 닫혀 있지만 확장에는 열려있는 구조를 추구하기 위해서는 굳이 리스크를 안고 가지 말고 equals와 hashCode는 항상 함께 재정의 해줘야 한다고 여기는 것이 좋겠습니다.

## 해결

그럼 마지막으로 위의 코드에서의 문제를 해결 해 보겠습니다.

방법은 우리 모두 잘 알고 있습니다. hashCode 를 재정의 해 주어야겠죠?

저는 java.util 패키지에 있는 Objects 클래스의 hash 메서드를 참고 해 보도록 하겠습니다.

![image-20220213221558716](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/equalsHashcode.assets/image-20220213221558716.png)

>  `Object.hash()` 메서드의 인자로 Object 배열을 넘기면 Arryas.hashCode() 메서드를 활용해 hashCode를 생성 해 줍니다. 

![image-20220213221639669](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/equalsHashcode.assets/image-20220213221639669.png)

> 그러면 해당 Object 배열을 순회 하며 기존의 result 값에 31 을 곱하고 hashCode() 결과값을 더하도록 구현 되어 있네요.

그럼 이제 hashCode() 메서드를 재정의해볼까요? num1과 num2의 순서와 상관 없이 항상 같은 값을 보장 하도록 코드를 작성 해야 합니다.

num1과 num2의 순서가 달라진다고 해도 둘 사이의 연산이 같은, 즉 교환법칙이 성립하는 연산들을 활용 하려고 하는데요, 우리에게 익숙한 연산중에는 더하기와 곱하기 연산이 그렇습니다.

```java
        @Override
        public int hashCode() {
            return Objects.hash((num1 + num2) * num1 * num2);
        }
```

그래서 num1과 num2의 값이 순서에 상관없이 같다면 어느 상황에서도 같은 hash 를 반환하는 연산을 하나 만들어 보았습니다. 

해시코드를 생성하는 비용에 있어서 이정도 연산이 얼마나 큰 부담을 주는지는 잘 모르겠지만 그게 부담스럽다면 매번 새로 계산하기 보다는 캐싱을 해두는게 좋겠습니다.

캐싱을 한다면 아래와 같은 코드로 표현 될 수 있겠네요.

```java
    private class MyPair {
        int num1;
        int num2;
        private Integer hashCode;

        public MyPair(int num1, int num2) {
            this.num1 = num1;
            this.num2 = num2;
        }

        @Override
        public boolean equals(Object o) {
            MyPair p = (MyPair) o;
            if (p.num1 == num1 && p.num2 == num2) {
                return true;
            } else if (p.num1 == num2 && p.num2 == num1) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            if (this.hashCode == null)
                this.hashCode = Objects.hash((num1 + num2) * num1 * num2);
            return this.hashCode;
        }
    }
```

이렇게만 한다면 Thread safe 하지는 않을 것 같긴 한데 본 포스팅에서는 그 이상으로 파고들지는 않겠습니다.

이제 문제가 해결되었는지 확인 해 보도록 하겠습니다.

```java
    @Test
    void test() {
        MyPair a = new MyPair(1, 2);
        MyPair b = new MyPair(2, 1);

        Set<MyPair> set = new HashSet<>();
        set.add(a);
        set.add(b);
        Assertions.assertThat(a).isEqualTo(b);
        Assertions.assertThat(a.equals(b)).isTrue();
        Assertions.assertThat(set.size()).isEqualTo(1);
        Assertions.assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
```

![image-20220213223234856](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/equalsHashcode.assets/image-20220213223234856.png)

먼길 돌아와 모든 테스트 케이스를 통과 할 수 있게 되었습니다.

이상으로 Equals를 override 할 경우 hashCode도 오버라이드 해야 하는 이유에 대해 알아보았습니다. 감사합니다.
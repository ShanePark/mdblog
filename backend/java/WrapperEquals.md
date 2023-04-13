# java) 래퍼 클래스의 동등 연산자 사용을 피해야 하는 이유

## Intro

매주 일요일 11시 30분부터 오후 1시까지, Leetcode에서 진행하는 Contest에 참여하고 있습니다.

여느 날처럼 문제를 풀고 있었고, 로직상 분명 통과 할 거라고 생각했는데, 생각지도 못한 엉뚱한 곳에서 자꾸 무한 루프가 발생하는 일이 생겼습니다. 그래서 디버깅을 진행 하던 중 눈으로 보고도 믿기 힘든 상황이 발생 했습니다. 

<img src=https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/WrapperEquals.assets/image-20220410142355887.webp width=750 height=550 alt=1>

디버거의 Variables 를 보면, poll 의 value도 128, peek의 value도 128 이지만, 둘의 동등 비교 결과인 same의 결과가 `false` 로 되어 있습니다. 이 때문에 if 문에서 조건 만족 상황의 블럭에 들어가지 않고 else 구문을 타고 있습니다.

순간적으로 당황을 하긴 했지만, 예전에 같은 경우가 한번 있었고, 그때는 단순 버그나 생각지 못한 오버플로 등이 있지 않았을까 생각하며 Integer.compare로 비교하게 해서 통과를 했던 경험이 있기 때문에 일단 비교문만 변경해 테스트는 통과를 하였습니다.

컨테스트가 모두 종료 되고 나서 이때의 문제를 확실하게 파고 들기 위해 주석을 남겨 두었었고 확인을 해 보았습니다.

## 원인

Integer와 같은 래퍼 클래스는 Primitive Type이 아닌 객체기 때문에 동등 연산에서 값을 비교하는 게 아닌 각각의 Reference를 비교 합니다. 그렇기 때문에 값이 같아도 false 가 나온 건데요. 왜 하필 다른 테스트 케이스들에서는 문제가 없었는데 value가 128이 되었을 때 문제가 되었는지 궁금했습니다.

### valueOf()

그래서 아래의 실험을 위해 아래의 코드를 작성해 실행 해 보았는데요

```java
public class IntegerEquals {
    public static void main(String[] args) {
        for (int i = 0; i < 200; i++) {
            Integer i1 = Integer.valueOf(i);
            Integer i2 = Integer.valueOf(i);
            System.out.printf("i1=%3d, i2=%3d, (i1==i2) = %s\n", i1, i2, (i1 == i2));
        }
    }
}
```

![image-20220410142954317](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/WrapperEquals.assets/image-20220410142954317.webp)

> 실행 결과

정말 신기하게도 i의 값이 128이 될 때 부터는 `i1==i2`가 false로 나오기 시작 했습니다. 도대체 왜 그럴까요?

추적을 위해 코드를 하나씩 따라 가 보도록 했습니다.

일단 처음에 `i1 = Integer.valueOf(i)` 부터 시작 하기 때문에 valueOf 메서드를 먼저 확인 해 보도록 합니다.

![image-20220410143338461](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/WrapperEquals.assets/image-20220410143338461.webp)

단순하게 new Integer 를 하지 않고, 일단 IntegerCache의 low 와 high 사이에 있는지 먼저 확인을 한 뒤에, 캐시하는 사이즈 내에 있으면 캐시 해 둔 Integer를 반환 하고 그렇지 않으면 `new Integer()` 생성자를 호출 하도록 작성 되어 있습니다.

그러면 이제 IntegerCache 클래스를 확인 해 보아야 겠습니다.

![image-20220410143538923](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/WrapperEquals.assets/image-20220410143538923.webp)

확인해보니 IntegerCache는 Integer 클래스에 내부 클래스로 담겨 있었습니다. low는 -128로 고정 되어 있고, high는 JVM의 설정 값을 불러오도록 되어 있네요. 설정된 값이 따로 존재 하지 않으면 기본적으로 high의 값은 127 입니다.

![image-20220410143858523](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/WrapperEquals.assets/image-20220410143858523.webp)

그 후에는 archivedCache 라는 Integer 배열에 low 부터 high 사이의 모든 값들을 캐싱 해 두고 사용 하도록 되어 있네요.

결과적으로, -128 ~ 127 사이의 Integer는 valueOf 로 생성을 할 때 새로 생성되지 않고 캐싱해둔 객체를 참조 합니다.

그렇다면 valueOf 가 문제였을까요?

### new Integer()

```java
public static void main(String[] args) {
  for (int i = 0; i < 200; i++) {
    Integer i1 = new Integer(i);
    Integer i2 = new Integer(i);
    System.out.printf("i1=%3d, i2=%3d, (i1==i2) = %s\n", i1, i2, (i1 == i2));
  }
}
```

그래서 이번에는 애초에 new Integer()를 하도록 해서 같은 테스트를 진행 해 보았습니다. 그 결과

![image-20220410144643994](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/WrapperEquals.assets/image-20220410144643994.webp)

`i=0` 일 때 부터 이미 동등 연산자의 결과가 false가 나오는 것을 확인 할 수 있었습니다.

![image-20220410144750508](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/WrapperEquals.assets/image-20220410144750508.webp)

> 참고로 new Integer()는 java9 버전 부터 Deprecated 되었는데요. 그이 이유가 valueOf의 경우가 메모리 공간이나 성능적으로 훨씬 효율적이기 때문이라고 써 있습니다. 아까 확인 한 것 처럼 자주 사용하는 값은 캐싱을 해 두기 때문입니다. 

그런데 생각해보면 저는 처음의 코드에서 valueOf 메서드를 호출 한 적이 없습니다. valueOf를 탓하기에는 조금 더 확인이 필요합니다.

### Autoboxing

제 처음 코드를 확인 해 보면 PriorityQueue 에 담아 두었던 값을 꺼내며 동등 비교를 하는데요, 처음에 해당 큐에 값을 담을 때에는 primitivy type인 int 값을 담았습니다.

같은 상황을 재현해봅니다.

```java
public static void main(String[] args) {
  for (int i = 0; i < 200; i++) {
    Integer i1 = i;
    Integer i2 = i;
    System.out.printf("i1=%3d, i2=%3d, (i1==i2) = %s\n", i1, i2, (i1 == i2));
  }
}
```

Integer 값에 int 값을 넣으며 오토 박싱이 되는 상황입니다. 실행 결과는

![image-20220410145340067](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/WrapperEquals.assets/image-20220410145340067.webp)

> 실행 결과

오토 박싱을 통해도 valueOf 메서드를 호출 했을 때 처럼 캐싱 범위를 벗어나자 마자 동등 비교에서 문제가 되었습니다.

그래서 오토박싱에도 같은 문제가 있는 이유를 찾기 위해 Oracle의 자바 문서에서 autoboxing 관련된 부분을 찾아 보았습니다.

- 오라클에 작성된 문서기 때문에 해당 문서의 스크린샷을 찍기엔 저작권이 너무  무서워서 링크로 갈음 하겠습니다.

아래 링크의 `the compiler converts the previous code to the following at runtime` 가 작성된 부분을 확인 하시면 됩니다. 설명된 내용을 요약 하면, 컴파일 과정에서 오토박싱 과정을 통해 변수를 할당하는 코드가 런타임에는 `Integer.valueOf` 메서드로 갈음 된다는 내용 입니다.

> https://docs.oracle.com/javase/tutorial/java/data/autoboxing.html

이로서 확실히 이 문제의 원인을 파악 했습니다.

## 해결

이제 문제의 원인을 찾았으니 해결 해야 합니다. 동등 비교가 아닌 Equals 메서드를 사용 하거나 Integer.compare 메서드를 활용 하면 되겠습니다. 이를 위해 간단한 실험을 해 보았습니다.

```java
public static void main(String[] args) {
  for (int i = 0; i < 200; i++) {
    Integer i1 = Integer.valueOf(i);
    Integer i2 = Integer.valueOf(i);
    boolean same = i1 == i2;
    boolean same2 = Integer.compare(i1, i2) == 0;
    System.out.printf("i1=%3d, i2=%3d, same = %s, same2 = %s\n", i1, i2, same, same2);
  }
}
```

한번은 동등연산, 한번은 compare 메서드를 통해 비교를 해 보았는데요. 그 결과는

![image-20220410150146037](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/WrapperEquals.assets/image-20220410150146037.webp)

> 실행 결과

동등 연산은 128부터 역시 캐시 범위를 벗어나며 false를 표시 하지만, Integer.compare 메서드의 경우에는 문제 없이 비교를 해 내기 시작 했습니다.

![image-20220410150230248](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/WrapperEquals.assets/image-20220410150230248.webp)

인텔리제이에서는 Integer.compare 의 결과를 0과 비교하는 코드를 동등 연산으로 바꾸라고 유혹 하며 회색 글자로 표현 하지만.. 저 유혹에 넘어 가면 안됩니다. 꼭꼭 숨겨져 있어 찾아 내기도 쉽지 않은 버그를 양산하게 됩니다. 절대적으로 잘못된 안내이기 때문에 JetBrains 사에 곧바로 이슈 리포트도 해 두었습니다.

![image-20220410151313781](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/WrapperEquals.assets/image-20220410151313781.webp)

> https://youtrack.jetbrains.com/issue/IDEA-291826

## 마치며

지금까지 java에서의 래퍼 클래스를 비교 할 때에 동등 연산자를 사용하면 안되는 이유에 대해 알아 보았습니다.

캐시되는 범위의 값 내에서는 잘 동작하기 때문에 동등 연산자를 사용해도 된다고 착각 하기 쉽지만, 금방 문제가 되기 때문에 항상 래퍼 클래스는 equals 메서드를 사용 하거나 compare 를 하도록 습관을 들이는 것이 좋겠습니다.

이상입니다.
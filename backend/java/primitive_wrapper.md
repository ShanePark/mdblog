# [Java] Primitive vs Wrapper class 기본형 타입과 래퍼클래스

## Intro

자바에서 기본형 타입과 래퍼클래스 간에는 변환이 자동으로 이루어지기 때문에 그 차이를 굳이 인식하지 않고도 어렵지 않게 코드를 작성할 수 있습니다. 그나마 스트림을 사용할 때나 필요에 의해 명시적으로 `.boxed()` 나 `.mapToInt()` 를 호출하게 되는데요.

모두 아시는 것처럼 primitive와 각각의 Wrapper 클래스는 많은 차이가 있습니다. 그 차이점에 대해 자세히 다루어 보려고 합니다.

둘의 차이에 대해 이해하기 위해서는, 먼저 자바의 스택 메모리와 힙 메모리에 대한 이해가 필요합니다.

## Stack Memory vs Heap Space

JVM은 메모리를 효율적이고 효과적으로 관리하기 위해 Stack과 Heap으로 불리는 두 가지 영역으로 메모리를 분리했습니다. 우리가 새로운 변수 혹은 객체를 선언할 때나, 메서드 호출할 때 등등의 상황마다 JVM은 스택 혹은 힙 공간에 각각의 작업에 필요한 메모리를 지정합니다.

### Stack

Stack은 접근이 쉽고 빠르기 때문에 메서드 호출 및 지역변수를 저장하기 위해 사용됩니다. 프로세스 내 각각의 쓰레드들은 각자의 스택을 가지고 있기 때문에, 변수 혹은 method call 은 각각의 쓰레드에 보관됩니다.

- 새로운 메서드가 호출되고 반환됨에 따라 각각 크기가 늘어나고 줄어듭니다.
- Stack 내부의 변수는 호출한 메서드가 실행 중인 동안만 유지됩니다.
- 자동으로 할당되고 메서드가 종료될 때 자동으로 해제됩니다.
- Stack 메모리가 가득 차면, `java.lang.StackOverFlowError` 가 발생합니다.
- Heap 메모리에 비해 접근 속도가 빠릅니다.

### Heap

Heap 은 상대적으로 Stack에 비해 크고, 필요에 따라 공간을 늘리거나 줄일 수 있기 때문에 객체를 저장하는 데 사용합니다. 또한 프로세스 내의 다른 쓰레드들과 공유하므로 여러개의 쓰레드는 heap 영역에 있는 같은 객체에 접근할 수 있습니다.

Heap 공간은 런타임에서 Java 객체의 동적 메모리 할당에 사용됩니다. 새로운 객체는 항상 heap 공간에 생성되며, 이러한 객체에 대한 참조만이 stack 영역에 저장됩니다.

Heap 공간은 또다시 아래와 같이 몇 개의 영역으로 구분됩니다.

`Young Generation` : Minor GC 의 대상. eden과 Survivior(S0, S1)로 구분됨

`Old Generation(Tenured)`: 오랫동안 살아남은 객체들이 저장되는 공간.

> 클래스나 메서드 메타 데이터를 저장했지만, 크기가 고정되어 문제가 있던 PermGen(Permanent Heap)은 JDK8부터 사라지고 자동으로 크기가 조절되는 Metaspace 영역으로 교체됨 (Native Memory)

- 자동으로 해제되지 않기 때문에 가비지 컬렉터가 필요하며 더이상 참조되지 않는 객체들은 그 대상이 됩니다.
- Heap 공간이 가득 차면, `java.lang.OutOfMemoryError`가 발생합니다.
- Stack 메모리에 비해 비교적으로 메모리 접근 속도가 느립니다.
- thread-safe 하지 않기 때문에 동시성에 대한 고려가 필요합니다.

### 예시

```java
public class MemoryExample {
    private static Cat createCat(String name, int age) {
        return new Cat(name, age);
    }

    public static void main(String[] args) {
        String name = "에디";
        int age = 5;
        Cat cat = null;
        cat = createCat(name, age);
    }
}

class Cat {
    String name;
    int age;

    public Cat(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

1. main메서드가 실행되며 해당 메서드에서 사용할 기본형 및 참조형 변수를 저장하기 위해 stack 메모리에 공간을 생성합니다. 
   - primitive 값인 age 는 스택메모리에 직접 저장됩니다. 
   - Heap 공간의 실제 Cat을 참조할 참조형 변수 cat 또한 stack 메모리에 생성됩니다.
   - String Constant Pool에 있는 `"에디"`를 참조할 name 변수도 스택메모리에 저장합니다.

2. main 메서드 안에서 createCat 메서드를 호출하며 main은 이전 스택 위에 메모리공간을 할당합니다. 그 공간에는
   - primitive 값인 age가 저장됩니다.
   - Heap 공간의 실제 cat을 참조할 참조형 변수가 저장됩니다.
   - String Constant Pool에 있는 `"에디"`를 참조할 name 변수도 스택메모리에 저장합니다.
3. 마지막으로 `Cat(String, int)` 생성자가 호출되고 마찬가지의 일이 일어납니다.  각각의 변수 상태와 Heap 메모리는 아래 그림과 같습니다.

![stack-heap](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/primitive_wrapper.assets/stack-heap.png)

> 보이는 것처럼, 스택메모리에 primitive 변수는 실제 값이 저장되지만, 참조형 변수는 실제 값 대신 Heap 공간에 저장된 객체에 대한 참조가 저장됩니다.
>
> String Constant Pool 에 대해서 더 알고 싶다면 [Java) String의 Immutable이 의미하는 것](https://shanepark.tistory.com/330) 을 확인해주세요.

## Primitive wrapper class

### 소개

모두 아시는 것처럼 자바의 primitive type에 해당하는 8개의 모든 데이터타입은 각각에 매칭되는 Wrapper 클래스가 있습니다.

| 기본 타입 | 래퍼 클래스 |
| :-------- | :---------- |
| `byte`    | `Byte`      |
| `boolean` | `Boolean`   |
| `char`    | `Character` |
| `double`  | `Double`    |
| `float`   | `Float`     |
| `int`     | `Integer`   |
| `long`    | `Long`      |
| `short`   | `Short`     |

Wrapper class는 무엇이고, 무엇 때문에 필요할까요?

기본적으로, `Map<T>`, `Set<T>`, `Lint<T>` 등에서 사용하는 generic 클래스는 객체에서만 작동하며 primitive type들을 지원하지 않습니다. 그래서 래퍼 클래스는 실제 필요한 값 데이터를 감싸고 캡슐화하여 **객체로 사용 할 수 있게** 할 용도로 사용됩니다.

모든 래퍼 클래스는 일치하는 primitive type의 단일 값을 포함하고 있는데요, immutable 하기 때문에 객체가 일단 생성된 이후로는 상태가 절대 변하지 않습니다. 

또한, final로 선언되어 있어 상속이 불가능합니다.

![final](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/primitive_wrapper.assets/final.png)

> final class

### 메모리 공간

처음 발표된 JVM 스펙에서는 **boolean**을 고려하지 않았고, 두 번째 Edition 부터 boolean을 타입으로 다루기 시작했습니다. 메모리 접근의 최소 단위등의 문제로 boolean은 JVM 구현에 따라 int 혹은 byte array 등으로 인코딩되며 <u>단일비트만 사용하는 경우는 없습니다.</u>

기본 타입들은 **Stack Memory**에 저장되며 접근이 매우 빠르지만, 래퍼 클래스는 **Heap Memory**에 저장되기 때문에 오버헤드로 인해 상대적으로 접근 속도가 느립니다. 

| 기본 타입 | 크기 | 래퍼 클래스     |  크기    |
| :-------- | :---------- | ---- | ---- |
| `byte`    | 8 bits | `Byte` | 128 bits |
| `boolean` | 1 bit (8 bits) | `Boolean` | 128 bits |
| `char`    | 16 bits | `Character` | 128 bits |
| `double`  | 64 bits | `Double`     | 192 bits |
| `float`   | 32 bits | `Float` | 128 bits |
| `int`     | 32 bits | `Integer`     | 128 bits |
| `long`    | 64 bits | `Long`     | 192 bits |
| `short`   | 16 bits | `Short`     | 128 bits |

> JVM 구현에 따라 실제 크기는 달라질 수 있습니다.

primitive type인 `int` 변수의 크기가 4바이트였는데, `Integer` 클래스는 사용하는 크기가 그 4배인 32바이트가 되었습니다. 

그림으로 한번 살펴보겠습니다.

![Integer](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/primitive_wrapper.assets/Integer.png)

새로 생성한 Integer 객체에는 그 값 뿐만 아니라, 실제 타입정보(이 경우에는 `Integer.java`)와 각종 Flags, 락 정보 등이 추가로 있습니다.

- Flags: 객체의 해시코드, 배열 여부, 가비지 컬렉션 정보 등 객체의 상태를  설명하는 플래그들의 모임
- Locks: 동기화가 필요한 경우 동기화 정보

## 오토박싱과 언박싱

2004년 이전 까지는 `list.add(1)`과 같은 코드를 작성할 수 없었습니다. 그렇기 때문에 일일이 적절한 래퍼클래스로 변환 후 컬렉션에 추가를 해 줘야 했었는데요.

```java
public static void main(String[] args) {
    List<Integer> list = new ArrayList<>();

    // Before Java 5
    Integer one = Integer.valueOf(1);
    list.add(one);

    // After Java 5
    list.add(1);
}
```

Java 5 부터 오토박싱과 언박싱이 추가되며 상황이 바뀌었습니다.

자바 컴파일러는 기본 타입과 각각에 일치하는 래퍼 객체간의 변환을 자동으로 수행하며, 이를 오토박싱 및 언박싱이라고 합니다. 

### Autoboxing

오토박싱은 `int -> Integer`, `double -> Double` 처럼, 기본형 타입의 값을 래퍼클래스 변수에 할당 할 경우에 일어납니다.

예를 들어

```java
Character c = 'z';
```

이런 경우에 'z'는 char 값이지만, Character 타입의 c 변수에 할당 되며 자동으로 래퍼 클래스로 변환됩니다.

이번에는 다른 형태도 확인 해 보겠습니다.

```java
List<Integer>list = new ArrayList<>();
for(int i=0; i<100; i++) {
    list.add(i);
}
```

`List<Integer>` 타입인 list에 primitive type인 i 값을 계속 추가해주지만, 컴파일 에러가 발생하지 않습니다.

이 또한, i 값이 list에 추가되는 코드가 컴파일러에 의해 자동으로 아래와 같은 코드로 변환되기 때문 입니다.

```java
List<Integer>list = new ArrayList<>();
for(int i=0; i<100; i++) {
    list.add(Integer.valueOf(i));
}
```

이처럼, 자바 컴파일러는

- 값이 적절한 래퍼클래스 타입 변수에 할당될 때
- 래퍼 클래스 타입이 기대되는 파라미터로 기본형 타입이 전달될 때

오토 박싱을 수행합니다.

### Unboxing

이 반대의 경우는 언박싱이라고 불립니다.

예제를 몇 가지 들어 보자면

```java
int i = Integer.valudOf(1);
```

이렇게 기본형 타입 변수에 래퍼클래스를 할당할 때나

```java
int sum(List<Integer> list) {
	int sum = 0;
	for(Integer i : list) {
    	sum += i;
	}    
    return sum;
}
```

기본형 타입이 기대되는 자리에 적절한 래퍼 클래스가 있을 때 일어납니다.

위의 코드는 런타임에 아래와 같이 변합니다.

![int_value](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/primitive_wrapper.assets/int_value.png)

> IntelliJ IDEA는 불필요한 언박싱이라고 안내해 줍니다. 명시하지 않아도 자동으로 수행해 주기 때문이죠.

이처럼 자바 컴파일러는

- 적절한 기본형 타입값이 예상되는 메서드 파라미터로 래퍼클래스가 전달되었을 때
- 기본형 타입 변수에 값으로 적절한 래퍼클래스가 할당될 때

언박싱을 수행합니다.

### Performance

그렇다면 성능은 어떨까요? 자바에서 성능을 논하는건 참 미묘할 때가 많은데요, 실제 성능에 영향을 주는건 데이터베이스 접속과 같은 코드 외적인 곳에서 결정되는 경우가 많기 때문입니다. 오토박싱 언박싱이 실제 성능 문제를 일으킬 경우는 흔하지는 않다고 봐야겠습니다.

하지만 위에서 알아본 것처럼, 스택메모리에서 값에 바로 접근하는지 아니면 일정 오버헤드를 겪으며 힙 메모리를 통해 값에 접근하는지에 따라 서로 간의 성능 차이는 제법 큰 편 입니다.

예제 코드를 통해 테스트 해보겠습니다.

```java
public class WrapperClassSpeedTest {

    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch();
        final int LOOP_SIZE = Integer.MAX_VALUE;

        stopWatch.start("primitive");
        int sum = 0;
        for (int i = 0; i < LOOP_SIZE; i++) {
            sum += i;
        }
        stopAndPrintLastTask(stopWatch);

        stopWatch.start("autoBoxing");
        Integer num = null;
        for (int i = 0; i < LOOP_SIZE; i++) {
            num = i;
        }
        stopAndPrintLastTask(stopWatch);

        stopWatch.start("valueOf");
        for (int i = 0; i < LOOP_SIZE; i++) {
            num = Integer.valueOf(i);
        }
        stopAndPrintLastTask(stopWatch);

        sum = 0;
        stopWatch.start("Unboxing");
        for (Integer i = 0; i < LOOP_SIZE; i++) {
            sum += i;
        }
        stopAndPrintLastTask(stopWatch);

        List.of(sum, num); // 변수 소비. 이게 없으면 컴파일러가 최적화 시켜버려서 각 루프를 돌지 않음.
    }

    static void stopAndPrintLastTask(StopWatch stopWatch) {
        stopWatch.stop();
        StopWatch.TaskInfo task = stopWatch.getLastTaskInfo();
        System.out.println("----------------------------");
        System.out.println("TaskName = " + task.getTaskName());
        System.out.println("Total Execution time = " + task.getTimeMillis() + "ms\n");
    }

}
```

결과부터 확인 해보면 아래와 같습니다.

![result](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/primitive_wrapper.assets/result.png)

> primitive 태스크가 가장 짧고 나머지 작업들은 비슷비슷합니다.

이제 Task들을 하나씩 따로 떼어 놓고 확인 해 보겠습니다.

#### primitive : 698 ms

```java
stopWatch.start("primitive");
int sum = 0;
for (int i = 0; i < LOOP_SIZE; i++) {
    sum += i;
}
stopAndPrintLastTask(stopWatch);
```

무난하게 루프를 돌며 sum 변수에 값을 추가합니다.  당연히 overflow가 발생하긴 하지만, 지금의 성능 측정에선 중요하지 않기 때문에 다른 변수를 차단하기 위해 int로 선언했습니다. sum을 long 타입으로 변경해 테스트할 경우 항상 여기에서 약 50~100ms 가량의 시간이 더 걸리는 것을 확인했는데, 아마도 primivite 타입간(int->long) 형변환이 이루어지기 때문으로 추정됩니다.

#### autoBoxing: 5102 ms

```java
stopWatch.start("autoBoxing");
Integer num = null;
for (int i = 0; i < LOOP_SIZE; i++) {
    num = i;
}
stopAndPrintLastTask(stopWatch);
```

이제 오토박싱이 이루어집니다. 

위에서는 sum 연산이 있었지만, 여기에서는 단순히 변수에 값을 할당만 하는 것임에도 7배 이상 시간이 오래 걸렸습니다.

#### valueOf: 4815 ms

```java
stopWatch.start("valueOf");
// Integer num = null; 이 위에 있는 상태
for (int i = 0; i < LOOP_SIZE; i++) {
    num = Integer.valueOf(i);
}
stopAndPrintLastTask(stopWatch);
```

오토박싱 과정을 코드로 풀어 놓았습니다. 오토박싱보다 아주 미묘하게 빠르게 나오긴 했지만 거의 비슷하다고 봐야겠습니다.

#### Unboxing: 5132 ms

```java
sum = 0;
stopWatch.start("Unboxing");
for (Integer i = 0; i < LOOP_SIZE; i++) {
    sum += i;
}
stopAndPrintLastTask(stopWatch);
```

언박싱의 경우에는 오토박싱과 마찬가지로 비슷한 시간이 걸렸습니다. 

### 결론

테스트 결과 primitive 간의 값 할당만이 매우 빠르게 끝났고, 그 외에는 모두 비슷한 시간이 걸렸습니다.

오토박싱 / 언박싱이 이토록 자주 일어날만한 상황에서는 반드시 성능을 유의해야 함을 알 수 있었습니다.

이상입니다. 

**References**

- https://docs.oracle.com/javase/tutorial/java/data/autoboxing.html
- https://www.baeldung.com/java-stack-heap
- https://www.linkedin.com/pulse/java-primitives-versus-wrapper-classes-abid-anjum/?trk=portfolio_article-card_title
- https://www.theserverside.com/blog/Coffee-Talk-Java-News-Stories-and-Opinions/Performance-cost-of-Java-autoboxing-and-unboxing-of-primitive-types
- https://en.wikipedia.org/wiki/Primitive_wrapper_class_in_Java
- https://amanagrawal9999.medium.com/wrapper-classes-the-silent-killer-2056b917d98a#:~:text=Therefore%2C%20the%20total%20size%20of,class%20object%20is%20128%20bits.
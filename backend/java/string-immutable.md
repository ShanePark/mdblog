# Java) String의 Immutable이 의미하는 것

## Intro

Java에서 String이 immutable 하다는 것은 자바개발자라면 모두 잘 알고 있습니다. 

하지만 그 `불변`의 속성이라는게 이해하기에는 마냥 쉬운게 아닌데요, 혹자는

```java
String temp = "abc";
temp = "123"
```

의 예를 들며, String 타입 변수인 temp의 값이 변했는데요! 라고 주장 할 수도 있겠습니다. 

또한, String은 primitive Type(기본 타입)이 아닌 Reference Type(참조 타입) 인데요, 이게 참 헷갈리게 만들때가 많습니다. 참조타입이라서 String을 call by reference로 이용해보려고 하면 그건 또 안되거든요.

그래서 이번에는 String의 불변이 의미하는 것에 대해 알아보겠습니다.

## String Object의 생성

위에서 잠깐 살펴보았던 예제에 대해 자세히 알아 보겠습니다.

```java
String name = "Shane";
name = "ShanePark";
```

얼핏 보기에는 name에 `Shane` 값을 할당 한 후에, 해당 값을 `ShanePark`으로 변경 한 것 처럼 보이지만, 사실은 그렇지 않습니다.

String name 으로 생성한 name 변수자체는 String 객체가 아닙니다. name변수는 단지 메모리에 있는 "Shane"이라는 String 변수의 reference(참조) 일 뿐 입니다.

그렇기 때문에 그 다음 줄에 있는 `name="ShanePark"`의 경우에도 "Shane" 이라는 값을 가진 String Object에 새로운 값을 할당하는 것이 아닌, 메모리상에 "ShanePark" 이라는 String Object를 생성 한 후에 name 변수가 "Shane" 대신 "ShanePark" 오브젝트를 참조 하도록 변경 하는 것 뿐입니다.

정리하자면 자바에서 말하는 String의 `Immutable`은 `name` 변수에 대해 말하는 것이 아닌, 메모리상의 `"Shane"` 혹은 `"ShanePark"` 이라는 String Object에 대한 것 입니다. 해당 String Object들의 값은 절대 변하지 않습니다.

## String이 Immutable한 이유

### 1. 메모리 절약

하나의 예시를 들어 보겠습니다. 아래와 같이 각기 다른 세개의 변수에 모두 같은 문자열값을 할당 했다고 가정 합니다. 

```java
String one = "1";
String temp = "1";
String first = "1";
```

자바는 이때 "1" 이라는 값을 각기 다른 세개의 메모리 공간에 할당하는 대신, 처음에만 `"1"` 이라는 String Object를 생성해 Heap 메모리의 **String Constant Pool**에 할당 하고 나서 이후 String Pool에서 같은 값을 찾으면 새로 생성할 필요 없이 같은 String Object를 참조하는 방식으로 작동 합니다.

여기에서 문제가 있습니다. 

```java
first = "first";
```

마음이 바뀌어서 first에 first를 할당 하는 순간 메모리상에 `"1"` 이라는 값을 가지고 있던 String Object의 값이 `"first"`라는 값으로 변하게 되며, one과 temp의 문자열 또한 `"first"`라는 값을 참조하게 됩니다. 메모리 절약한 것 까진 좋았는데 의도하지 않은 side effect가 발생 해 버립니다.

다행스럽게도 java에서는 이런 걱정을 따로 할 필요가 없는데요, <u>String을  immutable하게 설계 했기 때문</u>에, 애초에 이런 문제가 발생하지 않습니다.

String Pool에 한번 등록 된 String Object의 값은 절대 변하지 않습니다. 100개든 1000개든 얼마의 String 변수를 생성해도 메모리상의 같은 값을 안심하고 참조 할 수 있습니다.

또한 String Pool에 등록 하지 않고 String을 생성 하는 방법도 있는데요. 이때는 new 키워드로 생성 해주면 됩니다.

```java
String newOne = new String("1");
```

간단하죠. 

그러면 의도한 대로 되었는지 확인 해 보겠습니다.

```java
package com.tistory.shanepark.string;

public class Immutable {
    public static void main(String[] args) {
        String one = "1";
        String temp = "1";
        String first = "1";
        String newOne = new String("1");

        printRef(one);
        printRef(temp);
        printRef(first);
        printRef(newOne);
    }

    static void printRef(String str) {
        System.out.println("value: " + str + ", ref: " + Integer.toHexString(System.identityHashCode(str)));
    }
}

```

같은 문자열을 뜻하는 네개의 각기 다른 변수가 있습니다. 마지막의 `newOne` 변수만 `new String()`으로 생성 해 주었는데요,

hashCode를 비교 해서 각각 같은 혹은 다른 메모리 주소를 참조 하고 있는지 확인 해 보았으며 그 결과는 아래와 같았습니다.

![image-20220206193230085](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/string-immutable.assets/image-20220206193230085.png)

> 위에서 세개의 변수가 참조하는 위치는 같지만, 마지막에 new 키워드로 생성한 String Object만이 다른 참조를 하고 있음을 확인 할 수 있습니다.

물론 `==`연산자를 통해 비교 해 볼 수도 있습니다.

```java
System.out.println("\none == temp : " + (one == temp));
System.out.println("temp == first : " + (temp == first));
System.out.println("first == newOne : " + (first == newOne));
```

![image-20220206194151725](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/string-immutable.assets/image-20220206194151725.png)

### 2. 보안

immutable과 보안이 어떤 관계가 있을까 의아 할 수 있는데요. 이 또한 예제 코드로 알아 보겠습니다.

```java
package com.tistory.shanepark.string;

public class Immutable2 {
    public static void main(String[] args) {
        String account="451234-56-789012";
        int amount = 10000;
        transferMoney(account, amount);
    }

    private static void transferMoney(String account, int amount) {
        System.out.println(account + "계좌에 대한 validaton 시작");
        // validation 코드
        System.out.println(account + "계좌에 대한 validaton 완료");

        // 취약 구간

        System.out.println(account + "계좌로 " + amount + "원 입금 시작");
        // 입금 코드
        System.out.println(account + "계좌로 " + amount + "원 입금 완료");
    }


}
```

간단한 송금 과정에 대한 코드 입니다. 물론 실제 송금과정에는 동시성 문제를 해결하는 등의 훨씬 복잡한 로직이 들어가겠지만 간단히 예를 들어 보았습니다.

transferMoney 메서드가 실행 되면, 처음에는 해당 계좌로 송금하는게 맞는지에 대한 validation을 먼저 할 테고, 송금받는 계좌 및 해당 거래에 대한 검증이 끝났다면 실제 해당 계좌로 입금을 하는 과정을 거치게 되는데요. account 변수는 참조 변수기 때문에, 해당 메서드 밖에서도 여전히 다른 thread및 메서드 등에 의해 실제 값에 대한 접근이 가능합니다. 

validation이 끝나는 타이밍에 맞춰 참조하고 있는 값이 다른 계좌로 변한다면 엉뚱한 계좌에 입금이 되는 불상사가 일어 날 수 있습니다. 

물론 다행히도 이것 또한 java가 메모리상의 String Object의 값을 변경하는 것을 허용 하지 않기 때문에 문제가 되지 않습니다. 덕분에 Security risk가 해소되었습니다.

### 3. Thread Safe

String 객체가 immutable함으로서 얻을 수 있는 장점이 하나 더 있는데요, 바로 Thread Safe 입니다.

수천 수만개의 Thread가 메모리상의 같은 String Object를 참조 한다고 해도, 어느 Thread에서도 값을 변경 할 수 없기 때문에 Thread Safe 가 보장됩니다.

## 마치며

이상으로 java에서의 String immutable이 의미하는 것에 대해 알아보았습니다.

처음 자바를 배워 프로젝트를 하던 때에는, String을 parameter로 넘기고 해당 메서드 내부에서 call by reference 방식으로 값을 변경하는 시도를 해 보기도 했었습니다. 

>  물론 가능하지 않습니다. StringBuilder를 사용 할 수는 있겠지만, thread safe 하지 않기 때문에 multi thread 환경에서는 동기화에 신경을 추가로 쓰거나 메서드에 synchronized가 붙은 StringBuffer를 대신 사용 해야 하겠죠.

심지어 자바 7 전에는 JVM이 Java String Pool을 고정된 사이즈의 PermGen space에 보관했었기 때문에 확장이 불가능 했을 뿐 더러 garbage collection의 대상이 될 수도 없었습니다. 이로인해 너무 많은 String들이 intern(String Pool에 등록) 되는 경우 OutOfMemory 에러를 발생 할 수 있기 때문에 StringPool에 대해서도 신경을 쓰며 코딩을 해야 했습니다. 왜 하필 String은 immutable 하여 개발자를 힘들게 하는가.. 라는 생각도 해보곤 했었는데요.

위에서 함께 알아 본 것 처럼 String이 immutable 함으로서 얻을 수 있는 장점이 정말 많으며, 어찌보면 어쩔 수 없는 선택이었다고도 생각됩니다.

자바 7 이후 부터는 Java String Pool이 Heap space에 보관되어 JVM으로부터 garbage collect도 가능해져 참조되지 않는 String들을 pool에서 제거 할 수 있게 되었기 때문에 그 단점들도 점점 사라져가고 있습니다. 이렇게, 매일 접하는 String 객체와 조금은 더 친해지는 시간을 가져 보았습니다. 

글 마치겠습니다.

> ref: https://www.youtube.com/watch?v=Bj9Mx_Lx3q4


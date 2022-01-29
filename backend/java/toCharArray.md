# JAVA) String 순회 - 배열만들기 vs charAt

## Intro

오래전부터 String 을 파싱할 때 마다 고민이 있었습니다. 

charAt으로 인덱스를 순회하며 해당 위치에 있는 문자를 확인 할 것인가. 아니면 해당 스트링을 캐릭터 배열로 만들어 둔 다음에 배열의 인덱스로 순회 할 것인가.

사실 개인적으로는 배열 인덱스를 찾아 가는게 빠를 거라고 생각해서 charAt을 잘 안썼었는데요. 문득 의문이 들어서 테스트를 해 봐야 겠다는 생각이 들었습니다.

## 코드 확인

### chatAt(index)

먼저 String의 charAt 함수를 확인 해 보았습니다.

![image-20220129101443634](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/toCharArray.assets/image-20220129101443634.png)

코드를 보는순간 너무 부끄러웠습니다. 분명 자바 기초를 처음 학습 할 때, String은 사실 캐릭터 배열이라는걸 배웠었습니다. 배웠다는건 기억 하는데 정작 String의 작동 원리를 여태 잊고 사용 해 왔다는게 내가 기초가 이정도밖에 안됐나 싶습니다. charAt 메서드는 내부적으로 String 객체가 가지고 있는 

![image-20220129101716214](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/toCharArray.assets/image-20220129101716214.png)

value 라는 이름의 프로퍼티에서 바로 해당 인덱스의 값을 꺼내어 옵니다. 그렇기 때문에 charAt은 결국 배열 인덱스를 불러오는 것과 똑같습니다.

그렇지만.. charAt 메서드를 호출 할 때에는 함수호출로 인한 오버헤드가 있고, index를 체크 하는 로직도 추가로 있기 때문에 그래도 약간의 성능차이는 있지 않을까 조심스럽게 생각 해 보았습니다.

### length()

![image-20220129102131139](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/toCharArray.assets/image-20220129102131139.png)

마찬가지로 length 메서드도 String 객체가 가지고 있는 캐릭터 배열의 길이를 반환 해 줍니다.

## 테스트

### 테스트 코드

바로 코드를 작성해서 테스트를 진행 해 보았습니다.

```java
package com.tistory.shanepark.string;

public class CharArray_VS_CharAt {

    public static void main(String[] args) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 200_000_000; i++) {
            sb.append('a');
        }

        String str = sb.toString();
        Character c;

        long start;

        start = System.currentTimeMillis();
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            c = chars[i];
        }
        System.out.println("toCharArray 소요시간: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
        }
        System.out.println("charAt 소요시간: " + (System.currentTimeMillis() - start) + "ms");

    }

}

```

코드에 대해 간단하게 요약 하자면.

길이가 2억인 String 객체를 하나 생성 합니다. 후에 한번은 toCharArray로 캐릭터 배열을 생성 한 후에 index로 순회를 하고, 또 한번은 chatAt 메서드를 호출 하며 소모 시간을 확인 해 보았습니다. 사실 String 길이를 훨씬 더 길게 하고 싶었는데.. 제 맥북에어가 램이 8기가 밖에 없어서 그런지 힙메모리 부족으로 그 이상 스트링을 길게 만들지 못했습니다.

> 자바에서 String의 최대 길이는 32비트로, Integer의 최대값과 같은 `2,147,483,647` 입니다.

그 결과는 어땠을까요?

### 결과

![image-20220129102712100](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/toCharArray.assets/image-20220129102712100.png)

테스트 할 때마다 조금의 차이는 있었지만, CharArray를 생성 해서 순회 하는게 항상 느렸습니다. 도대체 왜 그럴까요? 인덱스로 직접 접근하는게 더 빨라야 하는게 아닐까요?

### 결과분석

그래서 이번에는 혹시 배열을 생성하는데 비용이 크게 발생하는 게 아닌가 의심되어 배열 생성 시간과 순회 시간을 따로 나누어 계산 해 보았습니다.

```java
package com.tistory.shanepark.string;

public class CharArray_VS_CharAt {

    public static void main(String[] args) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 200_000_000; i++) {
            sb.append('a');
        }

        String str = sb.toString();
        Character c;

        long start;

        start = System.currentTimeMillis();
        char[] chars = str.toCharArray();
        System.out.println("array생성 소요시간: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < chars.length; i++) {
            c = chars[i];
        }
        System.out.println("array 순회 소요시간: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
        }
        System.out.println("charAt 소요시간: " + (System.currentTimeMillis() - start) + "ms");

    }

}

```

이번 코드는, array 생성에 걸리는 시간과 배열 순회에 걸리는 시간을 나누어 계산 해 보도록 하였습니다.

그 결과는 재밌었는데요

![image-20220129103038871](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/toCharArray.assets/image-20220129103038871.png)

캐릭터 배열을 생성하는데만 소모되는 비용이 chatAt으로 전체 순회 하는 비용 그 자체보다도 높았습니다. 반면 일단 생성 한 후에 index 만으로 배열을 순회하는 데에는 매우 짧은 시간이 소모되었습니다.

캐릭터의 인덱스를 순회 할 때에 한번 순회하고 마는게 아닌 여러번 재활용이 필요하다면 배열을 생성하는 것도 고려할 법 하긴 하지만, 대부분의 경우에서는 그냥 charAt을 사용 하는게 훨씬 효과적입니다. 특히 배열을 따로 생성하면 그만큼의 메모리가 추가로 필요 하기 때문에 여러모로 불리합니다.

![image-20220129103550530](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/toCharArray.assets/image-20220129103550530.png)

>  toCharArray() 메서드도 확인을 해 보니 단순하게 String 객체가 가진 문자 배열을 복사해서 반환 합니다.

### .length() VS final size

이번에는 String을 순회 할 때에, `.length()`로 사이즈를 고정 해 둘지, 아니면 final size 로 크기를 고정해두고 순회 할 때가 더 빠른지 테스트를 진행 해 보았습니다.

모두 아시는 것 처럼, for문을 돌 때 마다, 조건에 있는 .length() 메서드가 호출 되는데요. 메서드 호출이 아닌 고정된 값이 있다면 더 빠르지 않을까 싶었습니다.

```java
package com.tistory.shanepark.string;

public class StaticLength {

    public static void main(String[] args) {

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 200_000_000; i++) {
            sb.append('a');
        }

        String str = sb.toString();
        Character c;

        long start;

        start = System.currentTimeMillis();
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
        }
        System.out.println(".length() 소요시간: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        final int length = str.length();
        for (int i = 0; i < length; i++) {
            c = str.charAt(i);
        }
        System.out.println("final size 소모시간: " + (System.currentTimeMillis() - start) + "ms");

    }
}

```

코드는 위와 같습니다.

결과는 어땠을까요?

![image-20220129104151895](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/toCharArray.assets/image-20220129104151895.png)

미세하기는 하지만 size를 고정 해 두고 돌리는게 더 빨랐습니다. 하지만 차이가 너무 적으니 테스트를 여러 번 진행 해 보아야 겠습니다.

```java
package com.tistory.shanepark.string;

import java.util.ArrayList;
import java.util.List;

public class StaticLength {

    public static void main(String[] args) {

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 100_000_000; i++) {
            sb.append('a');
        }

        String str = sb.toString();
        Character c;

        long start;

        List<Long> list1 = new ArrayList<>();
        List<Long> list2 = new ArrayList<>();

        for (int j = 0; j < 100; j++) {
            start = System.currentTimeMillis();
            for (int i = 0; i < str.length(); i++) {
                c = str.charAt(i);
            }
            list1.add(System.currentTimeMillis() - start);

            start = System.currentTimeMillis();
            final int length = str.length();
            for (int i = 0; i < length; i++) {
                c = str.charAt(i);
            }
            list2.add(System.currentTimeMillis() - start);

            System.out.println(j + "번째 테스트 진행중...");
        }

        list1.stream().mapToLong(l -> l).average().ifPresent(avg -> System.out.println(".length()의 평균 소요시간: " + avg));
        list2.stream().mapToLong(l -> l).average().ifPresent(avg -> System.out.println("사이즈 고정시 평균 소요시간: " + avg));

    }
}

```

스트링 길이를 조금 줄이고, 100번의 테스트를 진행해 그 평균 소요시간을 확인 해 보았습니다.

![image-20220129104948613](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/toCharArray.assets/image-20220129104948613.png)

그 결과는, 예상했던 대로 .length()를 호출 하는 것 보다 사이즈를 고정 해 두는게 더 빨랐습니다. 하지만 그 차이가 굉장히 미묘하기 때문에 사실 크게 의미는 없습니다. 함수 호출시의 약간의 오버 헤드가 전부고 특별히 연산은 없기 때문에 그런 듯 합니다.

<br><br> 이상으로 몇가지 실험을 통해 String 순회 성능 테스트를 진행 해 보았습니다. 앞으로의 메서드 선택에 도움이 되었으면 좋겠습니다. 감사합니다.

 
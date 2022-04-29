# Java) new int[Integer.MAX_VALUE]

## Large arrays

```java
public static void main(String[] args) {
    int[] arr = new int[Integer.MAX_VALUE];
}
```

카운팅 정렬(Counting sort)를 실습해 보려고 이것 저것 배열을 만들어 보던 도중, 위의 코드를 실행하자 에러가 발생 했습니다.	

![image-20220429104452239](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/size_of_array.assets/image-20220429104452239.png)

> Exception in thread "main" java.lang.OutOfMemoryError: Requested array size exceeds VM limit

2,147,483,647 의 크기로 생성을 시도 했는데, Java Virtual Machine이 배열의 크기에 제한을 걸어 두었다며 에러가 던져졌습니다.

실제 openjdk 의 코드를 조금 살펴보니, VM마다 각기 다른 배열 크기 제한을 걸어 두고 있다며 안전한 MAX_ARRAY_SIZE로 `Integer.MAX_VALUE - 8` 을 선언해두고 사용하는걸 볼 수 있었습니다.

![image-20220429112655691](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/size_of_array.assets/image-20220429112655691.png)

> http://hg.openjdk.java.net/jdk7/jdk7/jdk/rev/ec45423a4700#l4.50

아니.. 배열 크기에 왜 제한을 걸어놓았을까 하며 관련 내용을 찾아보던 중, 영문 위키페디아의 자바에 대한 비판 중 관련 내용이 있었습니다.

![image-20220429111807307](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/size_of_array.assets/image-20220429111807307.png)

> https://en.wikipedia.org/wiki/Criticism_of_Java#Large_arrays

자바의 인덱스는 int 값만 가능하고, 자바 언어의 한계라고 하는데요. MAX_VALUE도 어쨌든 int는 int 인데 .. 숫자를 조금 줄여 보며 제가 사용하는 VM의 제한을 체크 해 보았습니다.

![image-20220429112232680](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/size_of_array.assets/image-20220429112232680.png)

하나를 줄였을땐 똑같이 안되다가, 2개를 줄이니 이제는 `Requested array size exceeds VM limit` 대신에 Java heap space 에러가 발생합니다. 

64비트 VM Temurin-17 에서는 배열 크기 제한을 2,147,483,645로 잡고 있네요.

사용중인 컴퓨터는 16GB 메모리를 가지고 있기 때문에 힙 공간이 충분하지 않을까? 생각하며 배열의 정확한 메모리 사용량에 대한 궁금증이 생겨 직접 계산 해 보기로 했습니다.

## int 배열의 메모리 사용량

### int type

일단 배열에 앞서 int 자료형의 크기를 알아보겠습니다. 

![image-20220429114246139](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/size_of_array.assets/image-20220429114246139.png)

모두 잘 알고 있는 것 처럼, short 는 2 byte, int는 4byte (32bit) 의 크기를 차지합니다.

> 래핑 객체인 Integer의 경우에는 스펙 구현에 24 바이트, 데이터에 4바이트를 합쳐 무려 28 바이트를 차지합니다. 오버헤드가 상당합니다.

### array

이번에는 int 배열의 크기를 측정 할 차례 입니다.

배열의 크기는 정말 단순한데요, 각 데이터타입의 크기에 배열의 크기를 곱하면 됩니다.

`32bit * 2,147,483,645 ` 를 하면 되겠네요. 

![image-20220429115522990](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/size_of_array.assets/image-20220429115522990.png)

> 68,719,476,640 라는 값이 나옵니다. 대략 68억 비트 정도 되네요.

8로 나누어 bit를 byte로 변환 해 봅니다. `8,589,934,580 byte` 가 되겠네요. 계산이 잘못되었나 싶을 정도로 큰 숫자입니다.

메모리 단위로 익숙한 GB로 변환하면 약 `8.59GB` 입니다. 이진법인 GiB로 표기시에는 딱 8GiB가 나오지만, 시중에서의 메모리 제조사들은 여전히 용량표시에 십진법을 사용 하고 있습니다.

![image-20220429122913165](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/size_of_array.assets/image-20220429122913165.png)

16GB 메모리를 사용하고 있다고 해도, `int[] arr = new int[Integer.MAX_VALUE-2];` 가 불가능 한 이유가 여기에 있었네요.

### 어떻게든 만들어보기

일단 IntelliJ IDEA가 메모리를 무쟈게 차지하기 떄문에 IDE를 통한 실행은 불가능 합니다. 인텔리제이도 끄고, 대부분의 브라우저도 종료해 메모리를 최대한 만들어 내 보았습니다.

![image-20220429123420607](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/size_of_array.assets/image-20220429123420607.png)

> 3.0GiB / 15.4 GiB

배열생성에만 8GiB 가 들어가긴 하지만, 그래도 이정도면 어느 정도 넉넉 해 졌습니다.

![image-20220429123535534](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/size_of_array.assets/image-20220429123535534.png)

> 이제 간단하게 테스트를 위한 자바 프로그램을 작성 해 줍니다.

```java
public class ArrayMemory {

    public static void main(String[] args) throws InterruptedException {
        int[] arr = new int[Integer.MAX_VALUE - 2]; 
        System.out.println("배열 생성 성공 + arr.length: " + arr.length);
    }   
}
```

위와 같이 작성 후 실행 해 줍니다. JDK 11 부터는 미리 컴파일 하지 않아도 java파일을 바로 실행 할 수 있습니다.

```java
java ./ArrayMemory.java
```

![image-20220429123633357](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/size_of_array.assets/image-20220429123633357.png)

여전히 힙메모리 부족 에러가 뜹니다. 최대 힙메모리 설정을 넣어 실행 해 보겠습니다.

```java
java -Xms9g ./ArrayMemory.java
```

![image-20220429123716843](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/size_of_array.assets/image-20220429123716843.png)

순간적으로 엄청난 양의 메모리를 끌어 쓰고는 배열을 성공적으로 생성 해 냅니다.

이상입니다.

오늘의 결론: 배열 크기가 크면 메모리 사용량도 꽤 커지기 때문에 크기를 신중하게 결정하자..


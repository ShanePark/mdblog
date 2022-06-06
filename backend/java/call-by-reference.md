# JAVA) 자바에서는 Call By Reference가 불가능 합니다.

## Intro

사실 자바를 처음 배우고나서 최근까지도 함수를 호출 하여 파라미터가 전달 될 때에 primitive 타입인 경우에는 value가, 그 외에는 reference가 전달 된다 라고 알고 있었습니다.

> 자바에서의 Primitive 데이터 타입들
>
> - byte
> - short
> - int
> - long
> - float
> - double
> - boolean
> - char

String의 경우에만 String pool을 통해 immutable로 관리되기 때문에 primitive가 아님에도 call by reference가 되지 않지만 

> [Java) String의 Immutable이 의미하는 것](https://shanepark.tistory.com/330?category=1166008)

그 외에는 reference가 넘어간다고 알고 있었는데요

그 개념 하에서는 LinkedNode 나 Trie 등을 다루는 메서드를 짤 때 마다 뭔가 생각한 대로 동작하지 않아서  정말 애를 많이 먹고는 했는데 얼마전 자세히 찾아보니 사실 자바에서는 **Call by Reference가 불가능하다** 라는 충격적인 결과를 듣게 되었습니다.

> https://stackoverflow.com/questions/6029012/how-to-do-call-by-reference-in-java

지금까지 이해하고 있던 개념과 상충되기 때문에 코드를 몇가지 작성 해 보며 테스트를 해 보았고 이제는 그 사실을 받아들일 수 밖에 없었습니다.

**자바는 어느 예외도 없이 파라미터를 항상 pass by value 로 전달합니다.**

## Test

테스트 코드 작성을 통해 실제 자바에서 Parameter에 Argument를 전달하는 방식을 확인 해 보겠습니다.

### changeMac()

저는 m1 맥북 에어를 사용 하고 있습니다. 

처음에 구입 했을때에는 정말 빠릿빠릿하고 쾌적하다고 느꼈지만 8GB 램으로 인한 만성 메모리 부족과 쿨러가 없기 때문에 과한 작업을 했을 때에는 스로틀링이 걸리는 문제로 불만을 호소하던 중 언제부턴가 최신의 맥북 프로가 눈에 아른거리기 시작했습니다. 

그래서 새로운 맥북을 위해 맥북을 바꿔치기 하는 `changeMac()` 메서드를 만들었습니다.

**Mac.java**

```java
class Mac {
  private final String name;
  private final String processor;
  private int screenSize;
  private int ramSize;

  public Mac(String name, String processor, int screenSize, int ramSize) {
    this.name = name;
    this.processor = processor;
    this.screenSize = screenSize;
    this.ramSize = ramSize;
  }

  @Override
  public String toString() {
    return "Mac{" +
      "name='" + name + '\'' +
      ", processor='" + processor + '\'' +
      ", screenSize=" + screenSize +
      ", ramSize=" + ramSize + "GB" +
      '}';
  }
}
```

> Mac 클래스 입니다. 이름, 프로세서, 스크린 사이즈, 램 크기를 프로퍼티로 가지고 있는 클래스 입니다.
>
> getter & setter 는 생략했습니다.

**changeMac()**

```java
private void changeMac(Mac myMac, Mac newMac) {
  myMac = newMac;
}
```

저의 맥북을 감쪽같이 새 맥북으로 바꿔줄 changeMac 메서드 입니다.

이제 맥북을 새걸로 바꿔 줄 모든 준비가 끝이 났으니 바로 테스트 코드를 작성해 새 맥북으로 바꿔 봅니다.

```java
@Test
public void changeMacTest() {
    Mac myMac = new Mac("Macbook Air", "m1", 13, 8);
    Mac macBookPro14 = new Mac("Macbook Pro", "m1 max", 14, 32);

    changeMac(myMac, macBookPro14);
    assertThat(myMac).isEqualTo(macBookPro14);
}
```

제 맥북의 이름은 Macbook Air. m1 프로세서를 가지고 있으며 13인치 디스플레이와 8GB 램을 가지고 있습니다.

반면 `macBookPro14` 는 14인치의 사이즈에 무려 32GB의 램을 가지고 있는 매력적인 제품 인데요.. `changeMac()`을 통해 제 맥북을 newMac으로 변경 하고 최종적으로 myMac이 변경 되었지는 확인해 보는 테스트 코드를 작성 해 보았습니다.

기대하는대로 새로운 맥북이 할당 되었을까요?

![image-20220606134914857](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/call-by-reference.assets/image-20220606134914857.png)

> 실행 결과

애석하게도 changeMac() 메서드 후에도 여전히 myMac은  m1 프로세서와 8GB의 램을 가진 맥북 에어 그대로의 상태 입니다.

print 메서드를 이용해 changeMac 전과 후의 각각의 맥북의 상태를 확인 해 보았습니다.

```java
@Test
public void changeMacTest() {
  Mac myMac = new Mac("Macbook Air", "m1", 13, 8);
  Mac macBookPro14 = new Mac("Macbook Pro", "m1 max", 14, 32);

  System.out.println("==BEFORE==");
  System.out.println("myMac = " + myMac);
  System.out.println("macBookPro14 = " + macBookPro14);
  changeMac(myMac, macBookPro14);

  System.out.println("==AFTER==");
  System.out.println("myMac = " + myMac);
  System.out.println("macBookPro14 = " + macBookPro14);
  assertThat(myMac).isEqualTo(macBookPro14);
}
```

![image-20220606135244332](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/call-by-reference.assets/image-20220606135244332.png)

> 실행 결과

그 결과 myMac도 macBookPro14도 실행 전 후 아무런 변화가 없었음을 알 수 있습니다.

그렇다면 제 맥북은 이렇게 평생 8GB의 m1 프로세서로 남아있어야 하는 걸까요?

### upgradeRam()

다른건 다 참아도 램이 부족한건 못참겠습니다. 이제 한발 양보해 램 용량이라고 구걸 해 보려고 합니다.

이번에는 upgradeRam 메서드를 조심 스럽게 작성 해 보았습니다.

> 사실 공식적으로 Apple Silicon 제품은 메모리가 Unified Memory Architecture로 칩셋에 통합되어 있어 사후 업그레이드가 불가능합니다.

**upgradeRam()**

```java
private void upgradeRam(Mac mac, int size) {
  mac.setRamSize(size);
}
```

테스트 코드를 작성 하고

```java
@Test
public void upgradeRamTest() {
  Mac myMac = new Mac("Macbook Air", "m1", 13, 8);
  upgradeRam(myMac, 32);
  assertThat(myMac.getRamSize()).isEqualTo(32);
  System.out.println("myMac = " + myMac);
}
```

changeMac도 안됐는데 메모리는 업그레이드가 되겠어.. 하며 혹시나 하는 기대감에 실행 해 봅니다.

![image-20220606140124339](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/call-by-reference.assets/image-20220606140124339.png)

> 실행 결과

앗. 실제 myMac의 메모리가 32GB로 업그레이드 되었습니다. 도대체 아까와는 어떤 차이가 있는 것 일까요?

<br>

이를 이해하기 위해서는 먼저 자바에서 parameter에 값을 전달하는 방식을 이해해야 합니다.

## Parameter에 값 전달

자바에서는 Pass-by-Value 로 argument를 parameter로 전달 합니다. parameter로 전달 된 건 실제 오리지널이 아닌, 복사된 값 입니다.

그렇기 때문에 호출된 메서드내에서 어떠한 변경이 이루어져도, 해당 메서드를 호출한 `caller method` 내 에서의 원본 파라미터들에는 어떠한 영향도 주지 않습니다. changeMac 메서드에서 파라미터인 myMac은 원본이 전달 된 것 처럼 보일지라도 실제로 전달된 것은 caller의 myMac의 복사 값 이라는 것이죠.

> 지금부터는 특정한 메서드를 호출한 상황에 호출 된 메서드를 callee, 호출 한 메서드를 caller라고 하겠습니다.
>
> 위에서의 경우에는 `changeMac()` 메서드가 callee, `public void changeMacTest()` 메서드가 caller 입니다.

여기까지 이야기 했을때는 머리속에 혼동이 오거나, 방금 말한 내용에 오류가 있을 거라고 느끼는 분들이 많을 거라고 생각 합니다.

왜냐면 뒤에서 실행한 `upgradeRam()` 메서드는 기대한 대로 작동했기 때문이죠.

> 아니, 어떠한 영향도 주지 못한다면서? 램이 바뀌었는데 무슨 소리야.

자바에서는 메서드 호출이 이루어질 때 각각의 argument 는 그게 `value` 이거나 `reference`든 그 여부에 상관 없이 각각의 **복사본**을 스택 메모리에 생성 한 뒤에 이걸 해당 메서드에 전달 합니다.

- primitive 타입이라면 간단하게 그 값(value)이 stack memory에 복사되고 callee 메서드에 그대로 전달됩니다.

- 그 외에 경우에는 스택메모리의 reference는 힙 메모리상의 실제 데이터를 가리키고 있는데요, callee 메서드에 객체를 전달 할 때에는 스택메모리에 있는 레퍼런스의 복사본이 생성되고, 그 복사본이 메서드로 전달됩니다.

그렇기 때문에 upgradeRam 메서드 내에서의 mac 은 myMac 과 같은 객체를 참조하고 있지만 실제로 서로 완전 동일한 건 아닌겁니다.

### upgradeRam

![image-20220606142542056](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/call-by-reference.assets/image-20220606142542056.png)

>  test 메서드 내의 `myMac`과 upgradeRam 메서드에서의 `mac` 은 Heap 메모리상의 같은  Mac 을 참조하고 있습니다.

그럼에도 같은 Mac을 바라 보고 있기 때문에, upgradeRam 메서드 내부에서 `mac.setRamSize()` 메서드를 호출 했을 때에는 원본 [Macbook Air] 의 setRamSize 메서드가 호출 되어 램 사이즈가 변경 되며, 추후 myMac에서 램 사이즈를 확인해도 원본 데이터가 변경 되었기 때문에 램 용량이 늘어 난 것을 확인 할 수 있는 것 입니다.

### changeMac

이제 전혀 다른 경우인 changeMac에서의 그림을 확인 해 보겠습니다.

![image-20220606145255018](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/call-by-reference.assets/image-20220606145255018.png)

> changeMac 이 호출된 시점의 myMac과 newMac의 상태

changeMac 메서드가 호출 된 시점에는 caller(changeMacTest) 의 myMac과 callee(changeMac)의 myMac은 맥북 에어를, caller의 macBookPro14과 callee의 newMac은 맥북 프로를 각각 참조하고 있는데요. 

여기에서 `myMac = newMac;` 을 할당 한 후 해당 메서드는 바로 종료됩니다.

이제 종료 시점의 그림을 확인 해 본다면

![image-20220606145357605](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/call-by-reference.assets/image-20220606145357605.png)

> 메서드 종료 시점의 상태

changeMac 메서드 내부에서 myMac은 newMac과 동일하게 맥북 프로를 참조하게 변경 되었습니다만..

사실 caller 입장에서 보면 changeMac은 아무 일도 하지 않은 셈 입니다. 내부에서 무슨 일이 있어났건간에 밖에서 볼땐 아무 변화가 없거든요. 

caller의 myMac은 여전히 맥북 에어를 가리키고 있습니다. 뭐, 서로의 입장 차가 있으니 양쪽 다 억울 할 수 있겠네요.

### changeMacAndChangeRam

changeMac 안에서의 myMac을 아무리 지지고 볶고 한다고 해도 caller의 myMac이 바라보고 있는 맥북에어의 램을 변경하고 스크린 사이즈도 변경할 수 있을 지언정 caller의 myMac을 맥북프로로 바꿀수 없습니다.

> name이 에어와 프로를 가르는 기준이며 변경 불가능하다는 전제를 위해 name을 private final로 immutable 하게 만들었습니다.

이처럼 파라미터로 받은 객체의 원본이 **다른 객체를 참조 하게** 바꾸는건 절대 **불가능** 합니다.

> intellij IDEA도 그걸 알고 있기 때문에 새로 할당한 myMac은 쓰이지 않아서 회색 표시를 하고 있으며
>
> ![image-20220606150629579](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/call-by-reference.assets/image-20220606150629579.png)
>
> 파라미터에서의 myMac은 전달된 값을 전혀 사용하지 않았기 때문에 회색 표시를 하고 있습니다.
>
> ![image-20220606150731345](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/call-by-reference.assets/image-20220606150731345.png)

오히려 `myMac = newMac;` 이후에 myMac의 프로퍼티를 변경하려 든다면. 내가 가지고 있는건 처음의 맥북 에어 그대로인데, 엉뚱한 남의 맥북 프로의 램만 변경되는 상황이 올 수 있습니다.

```java
private void changeMacAndChangeRam(Mac myMac, Mac newMac, int ramSize) {
  myMac = newMac;
  myMac.setRamSize(ramSize);
}

@Test
public void changeMacAndChangeRamTest() {
  Mac myMac = new Mac("Macbook Air", "m1", 13, 8);
  Mac macBookPro14 = new Mac("Macbook Pro", "m1 max", 14, 32);

  System.out.println("==BEFORE==");
  System.out.println("myMac = " + myMac);
  System.out.println("macBookPro14 = " + macBookPro14);
  changeMacAndChangeRam(myMac, macBookPro14, 4);

  System.out.println("==AFTER==");
  System.out.println("myMac = " + myMac);
  System.out.println("macBookPro14 = " + macBookPro14);
}
```

![image-20220606152738176](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/call-by-reference.assets/image-20220606152738176.png)

> 실행시 myMac은 처음 그대로인데 macBookPro14의 램만 억울하게 4GB가 된 상황

그렇기 때문에 예기치 못한 버그를 예방하기 위해서는 자바에서의 파라미터 전달 방식에 대한 올바른 이해가 필요합니다.

## changeMac FINAL

그렇다면 결국 myMac 자바의 파라미터 전달 방식으로 인해서 맥북 에어를 계속 써야 되는 상황에 놓였습니다.

이 상황에서는 어떤 해결책이 있을까요? 평생 맥북 에어만 써야 하는 걸까요..?

```java
@Test
public void changeMacTest() {
  Mac myMac = new Mac("Macbook Air", "m1", 13, 8);
  Mac macBookPro14 = new Mac("Macbook Pro", "m1 max", 14, 32);
  myMac = macBookPro14;
  assertThat(myMac).isEqualTo(macBookPro14);
}
```

![image-20220606153320284](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/call-by-reference.assets/image-20220606153320284.png)

> 사실 맥북을 변경하고 싶다면 다른 메서드를 호출 하지 말고 바로 myMac에 macBookPro를 할당해야 합니다.

그럼에도 불구하고 꼭 특정 메서드를 호출해서 원본 데이터의 참조를 변경 해야 하는 경우가 있다면 그땐 파라미터 전달로 해결하려 들면 안됩니다.

위에서 계속 알아 보았던 것 처럼 파라미터를 통해서는 `value` 가 전달 되기 때문이에요.

특히 알고리즘 문제 풀이중 Node 나 Trie 등 레퍼런스를 다뤄야 하는 문제의 경우에는 이런 일이 비일비재 한데요. 더미 객체를 생성해야 할 때도 왕왕 있고 제법 혼란스러운 일이 많이 발생합니다.

이때의 쉬운 해결책 중 하나는 메서드의 선언부를 끌어 올려서 caller 와 callee가 같은 변수를 사용하게 하는 것 입니다.

```java
public class CallByReferenceThis {
    Mac myMac = new Mac("Macbook Air", "m1", 13, 8);

    @Test
    public void changeMacTest() {
        Mac macBookPro14 = new Mac("Macbook Pro", "m1 max", 14, 32);
        changeMac(macBookPro14);
        assertThat(myMac).isEqualTo(macBookPro14);
    }

    private void changeMac(Mac newMac) {
        this.myMac = newMac;
    }
}
```

이 경우에는 callee 안에서도 myMac에 똑같이 접근 할 수 있기 때문에 (파라미터로 전달받은게 아님) 새로운 맥북을 할당 해 줄 수 있습니다.

다소 억지스러워 보이는 예제이기도 하고, 비슷하게 전역 변수로 보내는 상황에서는 다중 쓰레드 환경에서의 동기화에도 신경 써줘야 하기 때문에 그냥 참고용으로만 생각 해 주시면 감사하겠습니다.

## 마치며

자바에서는 call by reference가 불가능하다는 제 기준에서는 제법 충격적이었던 소식을 전하게 되었습니다.

다소 헷갈릴 수는 있지만 자바 프로그램을 작성하는데 있어서는 꼭 필요한 개념이기 때문에 이 글을 읽어주신 모든 분들에게 도움이 되었으면 합니다.

감사합니다. 

**references**

- https://stackoverflow.com/questions/40480/is-java-pass-by-reference-or-pass-by-value
- https://stackoverflow.com/questions/6029012/how-to-do-call-by-reference-in-java
- https://www.baeldung.com/java-pass-by-value-or-pass-by-reference
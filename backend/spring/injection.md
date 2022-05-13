## Spring) 필드 인젝션의 해로움 _생성자 주입을 사용해야 하는 이유

## Intro

스프링을 처음 접하며 **의존성 주입**이라는 개념을 배운 이후로 한참을 필드 인젝션만 사용 해 왔습니다. 

영한님의 스프링이나 JPA 강의에서는 생성자주입을 사용하라고 하고, 학습할때는 항상 생성자 주입을 사용하는 습관을 들여 왔지만 이미 기존에 `@Autowired` 로 작성된 프로젝트들을 손대기에는 충분한 명분도 스스로의 확신도 사실 없었습니다.

단일 테스트 작성에 어려움을 느꼈을때에도, 아직 테스트 코드를 본격적으로 작성하던 건 아니었기 때문에 스프링 컨테이너를 일일히 띄워가며 테스트를 진행 하기도 했었습니다.

### 생성자 주입으로 바꾼 계기

그러던 중 꽤나 오래된 프로젝트에서 필드 인젝션을 싹 다 걷어내고 생성자 주입으로 바꾸게 된 계기가 있었습니다. 스프링 부트 1.5 버전을 사용하며 여러가지 불편을 겪다가 결국 숙원사업이었던 2.X 버전으로의 마이그레이션을 결정한 제법은 큰 사건 이었는데요.

> [Spring Boot 1.5 -> 2.5 마이그레이션 회고](https://shanepark.tistory.com/341)
>
> 사실 기존에도 새로운 클래스를 추가 하는 경우에는 생성자주입을 조금씩 적용 해 왔었기 때문에 이때는 90% 이상의 Field Injection과 10% 미만의 생성자 주입이 혼재된 상황이었습니다. 

스프링부트 2.5 버전으로 마이그레이션을 진행하던 도중 처음으로 프로젝트를 띄우는 순간 처음 보는 순환 참조 에러가 발생했었습니다.

필드주입의 경우에는 의존성이 숨겨지기 때문에 해당 객체를 로드 할 시점이 되어서야 순환 참조 에러가 발생 했지만 생성자 주입은 빈 객체 시점에 순환참조를 바로 찾아낼 수 있기 때문에 Circular reference 되고 있는 있는 지점을 그때그때 캐치해 스프링이 바로 알려 주었습니다.

스프링 부트 2.6버전부터는 순환참조를 기본적으로 금지 시켰기 때문에 `spring.main.allow-bean-definition-overriding` 과 `spring.main.allow-circular-references` 을 허용함으로 의존성이 꼬인 상황에서도 이전처럼 빌드는 가능하게 만들 수는 있지만 이번 기회에 모든 순환참조를 풀어야 겠다고 판단했습니다.

순환 참조를 풀기 위해서는 문제가 되는 의존성 주입을 찾아 내야 했고, 이때 숨겨진 의존성을 모두 드러내기 위해 모든 의존성 주입을 **생성자 주입**으로 변경하는 작업을 마침내 진행하게 되었습니다.

## 의존성 주입 방법 3가지

의존성을 주입할 때는 보통 3가지 방법중 하나를 택하게 되는데요. 생성자 주입, Setter 주입, 그리고 마지막으로 필드 인젝션이 있습니다. 세가지 주입 방법에 대해 간단히 확인 해 보도록 하겠습니다.

### Constructor

```java
@Controller
public class InjectionController {
    private InjectionService service;

    @Autowired
    public InjectionController(InjectionService service) {
        this.service = service;
    }
}
```

생성자 주입 입니다. 말그대로 생성자를 통해 의존성을 주입 받습니다.

### Setter

```java
@Controller
public class InjectionController {
    private InjectionService service;

    @Autowired
    public void setService(InjectionService service) {
        this.service = service;
    }
}
```

setter 주입은 생성자 주입과 비슷 하게 생겼지만, 생성자가 아닌 setter 메서드를 통해 주입받는다는 차이가 있습니다.

### Field

```java
@Controller
public class InjectionController {
    @Autowired
    private InjectionService service;
}
```

필드 인젝션입니다. 단순하게 주입받을 객체에 `@Autowired` 어노테이션만을 붙이면 끝 납니다.

자바의 장황한 문법을 비웃기라도 하듯 더없이 아름다운 이 코드는 수많은 사람을 매혹 시켰고, 그 달콤함으로 한동안 의존성 주입 세계관을 완전히 장악해내었습니다.

## 필드 인젝션의 문제

그렇다면 이 아름다운 코드에는 어떠한 문제점이 숨겨져 있었을까요?

### SRP 위반

> Single Responsibility Principal

객체지향설계의 5대 원칙 SOLID의 첫 번째 원칙인 단일 책임 원칙을 쉽게 위반하게끔 만듭니다.

- 하나의 클래스는 오직 하나의 책임을 가진다.
- 클래스는 단 한가지의 변경 이유만을 가져야 한다.

필드인젝션으로 새로운 의존성을 추가하는건 쉽습니다. 쉬워도 너무나도 쉽습니다. 

수십개의 의존성을 추가한다고 해도, 정말 간단하게 해결 됩니다. `@Autowired`만 달면 묻지도 따지지도 않고 해당 객체를 가져와서 사용 할 수 있으니깐요.

```java
@Controller
public class InjectionController {
    @Autowired
    private InjectionService service;
    @Autowired
    private InjectionService2 service2;
    @Autowired
    private InjectionService3 service3;
    @Autowired
    private InjectionService4 service4;
    @Autowired
    private InjectionService5 service5;
    @Autowired
    private InjectionService6 service6;
    @Autowired
    private InjectionService7 service7;
    @Autowired
    private InjectionService8 service8;
}
```

> 필드 인젝션의 예

같은 코드를 생성자 주입으로 변경하면 어떻게 될까요?

```java
@Controller
public class InjectionController {
    private final InjectionService service;
    private final InjectionService2 service2;
    private final InjectionService3 service3;
    private final InjectionService4 service4;
    private final InjectionService5 service5;
    private final InjectionService6 service6;
    private final InjectionService7 service7;
    private final InjectionService8 service8;

    public InjectionController(
        InjectionService service, 
        InjectionService2 service2, 
        InjectionService3 service3,
        InjectionService4 service4, 
        InjectionService5 service5, 
        InjectionService6 service6, 
        InjectionService7 service7,
        InjectionService8 service8) {
        this.service = service;
        this.service2 = service2;
        this.service3 = service3;
        this.service4 = service4;
        this.service5 = service5;
        this.service6 = service6;
        this.service7 = service7;
        this.service8 = service8;
    }
}
```

> 생성자 주입의 예

생성자가 너무 지저분합니다. 

그렇다면 생성자가 그럼 지저분해 진 이유가 뭘까요? 너무 많은 의존성을 가지고 있기 때문이죠. 

생성자 주입이 문제가 아니고 **너무 많은 의존성**이 문제라는게 여기에서의 핵심 입니다.

하나 둘 씩 책임은 늘어나는데 그걸 눈치 채지 못하는 사이에, 어느 순간 해당 객체는 소위 `God Class` 라고 불리는 하나의 거대한 전지전능 객체가 되어 버렸습니다.

그러므로 생성자가 지저분해지고 있다는건 필드 인젝션으로 돌아가야 할 시점이 아닌, 뭔가가 잘못 되고 있다는 신호로 받아 들여야 합니다. 관심사의 분리가 필요 할 때 입니다.

### 의존성 감춤

> Hiding Dependencies

의존성이 눈에 보이지 않습니다. 의존성을 눈으로 확인 하기 위해서는 해당 bean의 구현을 하나 하나 뜯어보면서 의존성을 확인 해야만 합니다.

DI 컨테이너를 사용 한다는건 클래스들이 더이상 스스로의 의존성을 관리할 책임이 없음을 의미합니다.

그건 즉 다른 누군가가 의존성을 제공하는것에 대한 책임이 있음을 의미하는데요, 그 다른 누군가는 바로 스프링의 DI 컨테이너 혹은 테스트 상황에서는 개발자가 직접(new) 주입을 하게 됩니다. 

이처럼 객체 자신이 더이상 의존성 획득에 대한 책임이 없을 때에는 public으로 필요한 의존성에 대한 **정보를 제공**해야 하는데요. 드디어 이때 Setter 메서드나 생성자를 사용하게 됩니다.

이렇게 할 경우에는 해당 클래스가 무엇을 반드시 **필요**로하고 (생성자), 무엇을 **선택**사항으로 받는지(Setter) 비로소 명확하게 알 수 있습니다.

### immutable 불가

```java
@Controller
public class InjectionController {

    @Autowired
    InjectionService service;
    
    public void myFunction() {
        service.doSomething();
        service = new InjectionService();
    }
}
```

이건 Setter 주입에서도 마찬가지인데, 필드 인젝션으로 주입받는 클래스는 final로 선언 할 수 없기 때문에 `state safe` 하지 않습니다. 개발자가 실수로 해당 객체를 새로 할당하여도 눈치채기가 쉽지 않죠. final로 선언한 불변 객체였다면 코드 작성과정에서 바로 알아차릴 수 있었을 버그 입니다.

### 강한 결합

스프링 프레임워크를 사용함으로서 얻는 가장 큰 이점은 스프링의 IOC 를 쉽게 이용 할 수 있다는 점 입니다. 얼마전 시청한 Armeria 소개 영상에서도 스프링의 강력한 DI 컨테이너 사용을 위해 스프링 부트와 함께 사용하라고 제안하고 있었을 정도로 훌륭한 기능 입니다.

> [라인 개발자들이 Spring 대신 Armeria 쓴 이유는? | 라인개발실록](https://www.youtube.com/watch?v=aoQO_bkYW94)

스프링의 DI 컨테이너는 의존하는 bean 간에 느슨한 결합을 제공해주는데요, `@Autowired`를 이용한 필드 인젝션을 하면 **스프링을 통해서만** 의존성 주입이 가능하기 때문에 해당 Bean들이 스프링의 DI 컨테이너와의 **강한 결합**을 하게 됩니다. 모순적이게도 Spring DI container를 사용하는 이유와 정 반대인거죠.

### 단위 테스트

필드 인젝션으로 주입한 객체를 테스트 하려면 무거운 스프링 컨테이너를 띄워야 합니다.

물론 `Mockito`가 존재하기는 하지만...

```java
@Controller
 public class InjectionController {
   @Autowired
   private Service1 service1; 
}
```

위의 경우에서 누군가가 갑자기 Service1에 Dao2 라는 의존성을 추가했다면, Mockito는 Null Point Exception이 발생하며 실패하게 됩니다. 

이 경우 또 다시 `@Mock` 어노테이션을 사용하며 Dao2를 추가 해 주어야 합니다.

```java
@RunWith(MockitoJUnitRunner.class)
 public class InjectionControllerTest {
   @Mock
   private Dao dao; 
   @InjectMocks
   private Service1 service1; 
}
```

그렇다면 이번엔 dao의 구현체로 `DaoImpl` 대신 `MyDao` 를 사용해야 한다면 어떨까요? 점점 테스트가 더 복잡해 집니다. 결국 테스트가 불편해 질 수록 테스트코드 작성의 이점이 적어지기 때문에 더이상 테스트 코드를 작성 하지 않게 될 수도 있습니다.

### 여담

Spring Data 프로젝트의 리드인 Oliver Gierke는 어느날 아래의 트윗을 남겼습니다.	

![image-20220512135116369](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/injection.assets/image-20220512135116369.png)

> https://twitter.com/odrotbohm/status/320135513560465410

스프링에서 만약 딱 하나의 기능을 제거 할 수 있다면 필드 인젝션이라고 말하며 한숨을 쉽니다. 

댓글에서는 심지어 필드인젝션을 evil 이라고 하며 비판하는데요. 자기는 잘만 사용하고 있다는 몇몇 댓글 작성자들에게 필드 인젝션의 의존성을 감추는(Hiding dependencise) 문제가 스프링 DI가 원래 추구하고자 했던 방향과 정 반대라고 하며 해당 문제를 계속해 언급했습니다.

## 세터 주입 vs생성자 주입

자 이제 한명을 처리했으니 최종 승자를 가려야 할 때가 왔습니다.

세계관 최강자간의 싸움에 가슴이 웅장해질법도 한데, 이것도 사실 답이 쉽게 정해져 있습니다.

### Setter

Setter 주입은 **선택적인** 의존성을 주입하는데 사용되어야 합니다. 이 경우에 만약 해당 의존성이 주입되지 않았다고 하더라도 해당 클래스는 의도한 대로 작동될 수 있어야만 합니다. 

setter로 주입 된 의존성은 해당 클래스가 초기화 된 이후에 언제든지 변경 될 수 있는데요 이건 상황에 따라서 이점이 없을 수 있습니다.

![image-20220512142522539](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/injection.assets/image-20220512142522539.png)

> https://docs.spring.io/spring-framework/docs/3.1.x/spring-framework-reference/html/beans.html#d0e2778

스프링 프레임워크 3.1 버전의 공식 문서에서는 꼭 필요한 의존성에는 @Required 어노테이션을 붙이라며 의존성 주입 방법으로 setter 주입을 추천 합니다.

### Constructors

생성자 주입은 기능이 올바로 작동하도록 하는데 있어 **필수적인** 의존성들의 주입에 효과적입니다. 

생성자를 통해 필수 의존성들을 제공함으로서, 클래스의 생성 시점에는 해당 클래스가 올바르게 작동할 준비가 되었다는 걸 확신 할 수 있습니다. 

특히, 생성자를 통해 주입되는 필드들의 경우에는 final로 선언을 할 수 있기 때문에 확실하게 immutable(불변) 객체로 사용 할 수 있습니다. 또한 생성자 주입의 결과 서로 다른 객체들 간의 순환 참조를 막을 수 있는데요, 이건 필드 인젝션이나 세터 인젝션에서는 할 수 없었던 일입니다. 무의식적으로 생성 될 수 있던 순환참조 구조를 애초에 방지할 수 있습니다.

또한 스프링 4.3이상의 버전을 사용한다면, 해당 클래스의 DI 프레임워크와의 결합을 완전히 분리해낼 수 있습니다.

> [Implicit constructor injection for single-constructor scenarios](https://spring.io/blog/2016/03/04/core-container-refinements-in-spring-framework-4-3)

스프링이 단일 생성자를 가진 클래스에 대한 **암묵적 생성자 주입**을 지원하기 시작했기 때문인데요, 이 덕분에 더이상 클래스에 DI에 관련된 어노테이션을 작성하지 않아도 됩니다.

![image-20220512143912990](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/injection.assets/image-20220512143912990.png)

> https://docs.spring.io/spring-framework/docs/4.2.x/spring-framework-reference/html/beans.html#beans-constructor-injection

마침내 스프링 프레임워크 4 버전 이후로는 공식 문서에서 생성자 주입의 사용을 장려 합니다.

## 마치며

생성자 주입을 사용해야 하는 이유에 대해 자세히 알아보았습니다. 

생성자 주입과 메서드 주입(Setter)은 서로간의 장단점이 있는데요, 이건 선택의 문제가 아닌 서로 부족함을 보완 할 수 있는 관계이기 때문에 필요에 따라 둘 다 사용하시면 됩니다. 필수적인 의존성은 생성자를 통해 주입 받으며 불변 객체로 선언하고, Optional 한 의존성에는 setter 주입을 사용할 수 있습니다.

> 반면 Field Injection은 사용하면 안됩니다!!

필드 인젝션 쓰지 말라고 하더라 라며 생성자 주입을 사용하자고 할 수도 있겠지만 대부분의 개발은 협업으로 이루어지고 있습니다. 이미 필드주입을 편하게 사용 하고 있는데 그걸 다짜고짜 변경하라고 하면 아마 대부분의 개발자들은 쉽게 받아들일 수 없을 건데요. 

이 글을 읽고 스스로 설득이 되었다면 아마 다른 사람도 설득하실 수 있을 거라고 생각합니다.

![gif](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/injection.assets/gif.gif)

IntelliJ IDEA에서는 파일내의 모든 field injection을 아주 간단하게 생성자 주입으로 변경 할 수 있는 기능을 제공 하고 있습니다. 

또한 롬복 플러그인을 사용하고 있다면

```java
@Controller
@RequiredArgsConstructor
public class InjectionController {
    private final InjectionService service;
    private final InjectionService2 service2;
    private final InjectionService3 service3;
    private final InjectionService4 service4;
    private final InjectionService5 service5;
    private final InjectionService6 service6;
}
```

`@RequiredArgsConstructor` 어노테이션을 이용하면 생성자 주입을 사용하면서도 오히려 필드 인젝션 때보다 더 깔끔한 코드를 작성할 수 있게됩니다.

IntelliJ 와 Lombok의 도움을 받아 지금부터 의존성 주입 방법을 변경해보는건 어떨까요?

**references**

- https://www.vojtechruzicka.com/field-dependency-injection-considered-harmful/
- https://www.codecleaner.org/autowired-is-evil/
- https://www.petrikainulainen.net/software-development/design/why-i-changed-my-mind-about-field-injection/
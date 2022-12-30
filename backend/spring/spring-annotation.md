# 스프링 Stereotype 어노테이션 @Component @Controller @Service @Repository 차이점

## Intro

스프링기반 프로젝트를 만들다 보면 기계적으로 필요에 따라 각종 어노테이션을 붙이게 됩니다. 컨트롤러에는 `@Controller`를, 서비스 레이어에는 `@Service`를, 그리고 데이터베이스에 접근하는 퍼시스턴스 레이어에서는 `@Repository`를 붙였습니다. 그 외 어플리케이션 컨텍스트에 빈으로 등록 하고 싶은데 특별한 설정 절차가 필요 없어서 컴포넌트 스캔을 통한 자동 등록이면 충분 할 때에는 `@Component`를 붙여 왔습니다.

그런데, 사실 Bean 으로 등록되어 후에 Dependency Injection에 사용된다는 공통점을 생각 해 보았을 때는 "전부 @Component"로 해도 되는거 아니야?" 라는 생각이 들 수 있습니다. 각각의 차이도 모른채 습관적으로만 사용하고 있는게 사실입니다.

이번 글을 통해 그 물음에 대한 해답을 찾아보겠습니다.





## @Component

![image-20221230102552902](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-annotation.assets/image-20221230102552902.png)

위에서 언급한 네개의 어노테이션들은 모두 우리가 매일 하는 코딩에서 매우 흔하게 사용하고 있는 어노테이션이며,` org.springframework.streotype` 패키지 하위에 위치해 있다는 공통점이 있습니다. 

그 중 가장 대표적인건 `@Component`이며, 사실 @Controller, @Service, @Repository 모두 @Component 로 부터 파생되었습니다. `<context:component-scan>` 으로부터 스캔되는건 오직 `@Component` 임에도 불구하고 컨트롤러, 서비스, 리포지터리 모두 함께 스캔되는 이유 또한 `@Component`를 포함하고 있기 때문입니다.

**Component.java**

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface Component {

	String value() default "";

}
```

**Controller.java**

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Controller {

	@AliasFor(annotation = Component.class)
	String value() default "";

}
```

**Service.java**

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Service {

	@AliasFor(annotation = Component.class)
	String value() default "";

}

```

**Repository.java**

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Repository {

	@AliasFor(annotation = Component.class)
	String value() default "";

}
```

이처럼 각각의 코드를 확인 해 보면, `@Component` 어노테이션을 포함 하고 있음을 확인 할 수 있습니다. stereotype의 핵심인 `@Component` 어노테이션은 특정 자바 클래스를 스프링의 Component 라고 마크함으로서, 빈 스캐닝 과정에서 자동으로 어플리케이션에 등록될 수 있도록 도와줍니다. 

### @Bean과의 차이

- 빈 등록은 @Bean 어노테이션을 통해서도 할 수 있지만, `@Bean`의 경우에는 빈을 명시적으로 선언해주어야 하는 반면에 `@Component`의 경우에는 자동으로 설정되며 등록 됩니다. 또한, Bean의 선언과 클래스의 정의가 분리되어있는 `@Bean` 과는 다르게 `@Component`의 경우에는 클래스 정의 및 빈의 선언이 결합되어 있습니다. 이 덕분에 클래스를 스프링 의존성으로부터 완전히 분리해내는 것이 가능합니다.

- `@Component`는 **클래스레벨**의 어노테이션인 반면에, `@Bean`은 메서드 레벨의 어노테이션이라는 차이가 있습니다.

- 스프링 컨테이너 밖의 클래스는 `@Component`로 등록 할 수 없지만 `@Bean`은 가능하기 때문에, 후자의 경우는 주로 외부 의존성을 빈으로 등록 하기 위해 사용됩니다.

## @Controller

Spring MVC와 Spring WebFlux 에서 사용되며, 컨트롤러로서의 역할을 맡고 있음을 표기 해 주는 어노테이션 입니다. Controller 어노테이션을 `@Component`로 대체 할 수 없는 이유는 `@Controller` 어노테이션이 가지고 있는 특별한 기능 때문인데요.

Dispatcher는 @Controller 어노테이션이 있는 클래스를 스캔 한 뒤에 @RequestMapping 어노테이션이 달려 있는 메서드들을 확인 합니다. 그렇기 때문에 `@RequestMapping` 어노테이션은 `@Controller` 어노테이션이 달려있는 클래스 내에서만 작동한다고 볼 수 있습니다.

직접 그 과정도 눈으로 한번 확인 해 보는게 좋겠죠?

`AbstractHandlerMethodMapping.java` 의 253번 라인을 확인 해 보았습니다.

![image-20221230113621849](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-annotation.assets/image-20221230113621849.png)

beanName을 가지고 cancidateBean 들을 처리 하는 과정입니다. 해당 과정에서 beanType을 확인 한 뒤에 해당 빈 타입이 Handler 인게 확인 된다면 detectHandlerMethods 메서드 호출합니다.

`RequestMappinghandlerMapping.java`의 **isHandler()** 메소드를 확인 해 보면,

![image-20221230113637787](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-annotation.assets/image-20221230113637787.png)

해당 클래스가 `org.springframework.stereotype.Controller.class` 어노테이션을 가지고 있는지 확인하는게 눈에 띕니다. 이렇게 해서 Handler임이 확인 된 빈은, **detectHandlerMethods()** 를 통해 핸들러 메서드가 등록 됩니다.

![image-20221230113931847](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-annotation.assets/image-20221230113931847.png)

그럼 어노테이션 개념이 자바에 추가되기 전에는 어땠을까요? 기억하는 분들이 있겠지만

![image-20221230112315075](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-annotation.assets/image-20221230112315075.png)

서블릿을 상속해서 직접 구현 했을 때나

![image-20221230112204790](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-annotation.assets/image-20221230112204790.png)

혹은 `org.springframework.web.servlet.mvcController` 인터페이스를  구현해야 했을 때를 생각해보면, 하나의 클래스에서는 하나의 매핑정보만을 가질 수 있었습니다. JDK 1.5에서 추가된 어노테이션 덕분에 손쉽게 메서드별로 매핑 정보를 가지고 코드를 간단하게 작성 할 수 있게 되었습니다.

## @Repository

모두 아시는 것 처럼, @Repository 어노테이션은 Persistence Layer를 담당 하는 계층에 달아주고 있습니다. 그럼 이 어노테이션은 또 어떤 특별한 기능을 가지고 있을까요?

![image-20221230114712757](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-annotation.assets/image-20221230114712757.png)

스프링은 **DataAccessException** 을 통해 DB 벤더에 상관없이 독립적으로 적용 가능한 추상화된 런타임 예외 계층을 제공 합니다. 

Persistence Layer 에서는 사용하는 데이터베이스의 종류나 ORM에 따라 각기 다른 예외를 발생 하는데요, 이 때 어떤 데이터 액세스 기술을 사용하건 신경 쓸 필요 없이 기술에 독립적인 예외 추상화를 통해 다룰 수 있도록 예외 변환을 해 줍니다. 그리고 그 플랫폼 종속적인 예외들의 변환 작업이 필요한 클래스라는걸 마크하는게 `@Repository`의 역할입니다.

그 과정도 눈으로 확인을 해 보도록 하겠습니다.

![image-20221230120506256](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-annotation.assets/image-20221230120506256.png)

> org.springframework.boot.autoconfigure.dao.**PersistenceExceptionTranslationAutoConfiguration**

예외를 변환해주는 빈 후처리기 입니다. AOP를 통해 타겟으로 등록된 클래스들을 대상으로 후처리를 하며 예외를 변환 해 주는데요

AOP의 대상을 선택 해 주는 Advisor가 등록되는 과정을 확인 해 보면

![image-20221230120801831](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-annotation.assets/image-20221230120801831.png)

어드바이스로는 `PersistenceExceptionTranslationInterceptor`를 등록 하고, 포인트컷으로 `AnnotationMatchingPointcut` 생성자에 repositoryAnnotationType 을 전달 해서 생성 합니다.

AOP에 대한 이해가 없다면 이해하기 어려운 부분이고 용어가 생소 할 수 있는데, 간단히 설명하자면 <u>특정 어노테이션이 달려있는 클래스들을 빈 후처리기의 대상(예외 변환의 타겟) 으로 삼겠다</u>는 이야기 입니다.

그러면 repositoryAnnotationType으로 생성자에 전달되는 파라미터를 확인 해 보면 되겠죠

![image-20221230120704831](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-annotation.assets/image-20221230120704831.png)

예상 했던 것 처럼, `@Repository` 가 들어가고 있었습니다. 

결론적으로 @Repository 어노테이션이 달려있다면 빈 후처리기에서 에외 변환의 대상이 된다는 이야기 입니다. 이처럼 @Repository 도 정말 중요한 역할을 맡고 있습니다.

### JpaRepository

그렇다면 이쯤에서 중요한 물음이 있을 수 있습니다.

JPA를 사용하며 JpaRepository를 상속하는 Repository 인터페이스를 생성 할 때에도 `@Repository`를 일일히 달아주어야 할까요?

- 정답은 **No** 입니다.

![image-20221230131530217](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-annotation.assets/image-20221230131530217.png)

> 코드를 확인 해 보면, 오히려 @NoRepositoryBean 어노테이션이 달려 있습니다.

![image-20221230130406163](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-annotation.assets/image-20221230130406163.png)

> 다이어그램을 확인 해 보면 JpaRepository는 `Repository<T, ID>` 인터페이스를 상속 하고 있습니다.

JpaRepository를 상속해 만든 인터페이스 역시 구현체가 아닌 **인터페이스**에 불과합니다. 실제 구현체는 Spring Data JPA에 의해 프록시 객체로 동적으로 생성되고, 에러변환 또한 그 안에서 처리됩니다. `@NoRepositoryBean` 어노테이션은 특정 인터페이스를 스프링에서 그 자체가 Repository 인 것 처럼 다루는 것을 방지 하기 위해 등록 합니다. 만약 해당 인터페이스를 상속해서 개발자가 스스로의 Repository 계층을 직접 구현한다면 그때에 `@Repository`를 붙일 필요가 있지만, 인터페이스 그 자체로 사용 할 때에는 붙일 필요도 없고 붙여도 의미가 없습니다.

![image-20221230134554066](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/spring-annotation.assets/image-20221230134554066.png)

> 실제 JpaRepository의 구현체인 SimpleJpaRepository 에는 @Repository 어노테이션이 붙어 있습니다.

스프링에 의해 동적으로 생성되는 프록시는 아래와 같은 모습을 보입니다.

```java
public class UserRepositoryProxy implements UserRepository {
    private final SimpleJpaRepository<User, Long> delegate;

    public UserRepositoryProxy(EntityManager em) {
        this.delegate = new SimpleJpaRepository<>(User.class, em);
    }

    @Override
    public <S extends User> S save(S entity) {
        return delegate.save(entity);
    }

    @Override
    public User findById(Long id) {
        return delegate.findById(id).orElse(null);
    }
  
		...
}
```

자세히 보면, SimpleJpaRepository에 기본적인 CRUD 호출을 위임하는 것을 확인 할 수 있습니다.

## @Service

왜 컨트롤러를 소개 하고 그다음 바로 Repository를 소개했는지 궁금해 하는 분들이 있을거에요, 일반적인 계층 순서와는 안맞기 때문이죠.

사실 위의 Controller와 Repository에 비하면 Service 어노테이션은 특별한 기능이 없기 때문에 가장 마지막에 소개 했습니다.

`@Service`는 비즈니스 레이어를 담당하는 클래스라는 것을 표시하기 위한 용도로 사용하며 스프링 빈으로 등록되도록 마크 하는 것 외에는 다른 기능이 없습니다. 하지만, 최소 비즈니스 레이어를 담당하고 있음을 표시해 구분 할 수 있고 개발자가 필요에 따라 얼마든지 다양한 용도로 활용 할 수 있으며 추후에 스프링에서 @Service 레이어에 새로운 기능을 추가 해 줄지 아무도 모르죠. 

예를 들어 보통 선언적 트랜잭션은 Service 계층에서 선언해주는게 흔하기 때문에, 메서드 이름에 따라 규칙을 주고 read 혹은 find 등으로 시작하면 읽기 전용 트랜잭션으로, update 혹은 create 등이 들어간다면 읽기/쓰기가 가능한 하나의 트랜잭션으로 묶어주는 기능 등을 필요에 따라 충분히 작성 할 수 있습니다. 

> 물론 가능하다 뿐이지 적극적으로 추천하는 방법은 아닙니다.

## 마치며

지금 까지 스프링 stereotype 어노테이션들과 그 차이점에 대해 알아보았습니다.

사실 평상시에 큰 생각 없이 달아두고 사용하는 경우가 많은데, 각각의 역할에 대해 알아보며 그 동작 방식이 꽤나 섬세함에 놀라게 되었고 또한 스프링 개발자들이 저렇게 디테일한 동작들을 잘 설계 해 준덕에 우리가 편하게 프레임워크를 사용해 코드를 작성하고 있음을 느낄 수 있었습니다.

감사합니다. 

**References**

- https://stackoverflow.com/questions/6827752/whats-the-difference-between-component-repository-service-annotations-in
- https://www.geeksforgeeks.org/spring-stereotype-annotations/
- https://medium.com/javarevisited/spring-stereotype-annotations-1469ca0c3ad2
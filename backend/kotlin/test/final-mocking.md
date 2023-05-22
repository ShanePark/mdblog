# [Java/Kotlin] Final 클래스 Mocking 하기

## Intro

코틀린을 사용한 프로젝트에서 Mockito를 사용해서 테스트 코드를 작성했는데 아래와 같은 에러가 나며 코드가 동작하지 않았다.

```
Cannot mock/spy class com.tistory.shanepark.dutypark.security.domain.dto.LoginMember
Mockito cannot mock/spy because :
 - final class
org.mockito.exceptions.base.MockitoException: 
Cannot mock/spy class com.tistory.shanepark.dutypark.security.domain.dto.LoginMember
Mockito cannot mock/spy because :
 - final class
	at app//com.tistory.shanepark.dutypark.security.filters.AdminAuthFilterTest.should redirect for non-admin user(AdminAuthFilterTest.kt:69)
	at java.base@17.0.7/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base@17.0.7/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base@17.0.7/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base@17.0.7/java.lang.reflect.Method.invoke(Method.java:568)
	at app//org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:727)
	at app//org.junit.jupiter.engine.execution.MethodInvocation.proceed(MethodInvocation.java:60)
	at app//org.junit.jupiter.engine.execution.InvocationInterceptorChain$ValidatingInvocation.proceed(InvocationInterceptorChain.java:131)
	at app//org.junit.jupiter.engine.extension.TimeoutExtension.intercept(TimeoutExtension.java:156)
	at app//org.junit.jupiter.engine.extension.TimeoutExtension.interceptTestableMethod(TimeoutExtension.java:147)
```

그 이유는 대부분 잘 알고있겠지만, 코틀린에서는 기본적으로 모든 클래스가 final로, 상속이 불가능하기 때문이다.

프록시를 만들어서 기존의 클래스에 Mock 기능을 추가시켜줘야하는데, final 클래스다 보니 의도대로 동작하지 않는 문제가 발생한 것.

## 해결

Mockito 에서 기본적으로는 final 클래스를 Mock 할 수는 없다. 

해결 방법은 크게 두가지정도가 있는데

1. 해당 클래스를 상속 가능한 open class로 만들어 주는 것
2. MockMaker Extention을 활용

Kotlin을 사용하는데 테스트 코드 만들겠다고 open 붙이기는 좀 그렇다. 두번째 해결 방법을 이용해서 해결 해 보도록 하겠다.

### MockMaker

https://github.com/mockito/mockito/wiki/What%27s-new-in-Mockito-2#mock-the-unmockable-opt-in-mocking-of-final-classesmethods 를 읽어 보면, mock maker 엔진을 CGLIB 에서 ByteBuddy로 변경했다고 써있는데, 이말은 즉 상속을 통한 프록시 객체 생성을 할 수가 없으니 바이트코드를 조작한다는 뜻이다. 

> 스프링의 AOP 에서도, Hibernate에서도 바이트 조작을 이용한다. 

Mockito 1버전에서는 안되고, 2버전부터 추가된 기능이라는데, spring-boot-starter-test를 쓴다면 기본적으로 Mockito가 포함되어 있으니 버전을 한번 확인 해 보면 된다. boot 3.0.5 버전 기준으로는 mockito 4.8.1 버전에 의존하고 있었다.

![image-20230522213455390](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/kotlin/test/final-mocking.assets/1.webp)

자 이제 그럼 자랑스럽게 추가된 Mock maker를 사용해보자.

- `src/test/resources/`경로에 `mockito-extensions` 라는 이름의 폴더를 생성한다.
- 해당 폴더 내에 `org.mockito.plugins.MockMaker` 라는이름의 파일을 생성한다.
- 해당 파일에는 `mock-maker-inline` 라고 한줄만 작성 해 둔다.

완성하면 아래와 같은 구조가 된다.

![image-20230522213820408](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/kotlin/test/final-mocking.assets/2.webp)

![image-20230522213836315](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/kotlin/test/final-mocking.assets/3.webp)

그러고 나서 테스트를 다시 돌려보면 아무 문제 없이 Mocking을 해내는 것을 확인 할 수 있다.

![image-20230522213905239](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/kotlin/test/final-mocking.assets/4.webp)

**References**

- https://github.com/mockito/mockito/wiki/What%27s-new-in-Mockito-2#mock-the-unmockable-opt-in-mocking-of-final-classesmethods
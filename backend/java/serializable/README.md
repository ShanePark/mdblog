# [Java] Serializable 인터페이스 이해하기

## Intro

데이터를 한 시스템에서 다른 시스템이나 네트워크로 전송하거나 데이터를 파일에 저장할 때, 그 데이터를 `전송가능한 형태`로 변환해야 한다. 객체는 입체 형태지만, 데이터가 이동하는 통로는 0과 1로만 이루어진 일차원의 세상이기 때문이다.

이 과정을 **직렬화**(serialization)라고 하며, 데이터의 직렬화의 방법은 XML이나 JSON등 우리가 흔히 아는 것 외에도 정말 많은 방법들이 있다.

 자바에서는 `Serializable` 인터페이스를 구현하여 이를 수행할 수 있게 된다.

자바 내에서의 직렬화는 객체의 상태를 바이트 스트림으로 변환하여 파일에 저장하거나 네트워크를 통해 전송할 수 있도록 하는 과정이다. 자바 플랫폼 내에서만 데이터를 주고받는다면 아래의 강점을 지니고 있기 때문에 여전히 많이 사용된다.

- 편의성: 별도의 직렬화 코드를 작성할 필요 없이 객체 그래프를 자동으로 처리한다.

- 효율성: 데이터를 더 적은 크기로 표현할 수 있다.

그리고 `Serializable` 인터페이스는 아래의 특징이 있다.

- 영속화할 객체들은 Serializable 인터페이스를 구현하거나 이미 구현한 클래스를 상속해야 한다.
- Serializable 인터페이스를 구현하는 클래스의 서브클래스는 직렬화가 가능하다.
- 객체 그래프를 탐색하다가 Serializable 인터페이스를 지원하지 않는 객체를 만난다면, NotSerializableException이 발생하고 해당 클래스는 직렬화가 불가능 한 객체로 인식된다.

자바에서의 객체들은 JVM이 돌고 있을때에 메모리 내에서만 사용 가능하기 때문에 그들의 생명 주기를 JVM 밖으로 연장시키고 싶다면 직렬화가 필수다. 실제 사용 예시들을 알아보자.

### EJB3.0

[JSR 220](https://download.oracle.com/otndocs/jcp/ejb-3_0-fr-eval-oth-JSpec/) 스펙에는 엔티티 인스턴스가 detached 상태에서 값으로 전달되어야 하는 상황에서는 Serializable을 구현해야 한다고 작성되어 있다

![image-20231108102407800](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/serializable/serializable.assets/6.webp)

### Session

Tomcat의 `StandardSession` 코드를 확인해보면 Serializable을 구현하라고 한다. 분산 환경이 아니고 파일에 저장할 일이 없다면 괜찮을지 모르겠지만, 그래도 세션에 필요한 객체는 Serializable을 구현하는 것이 좋다.

> In order to successfully restore the state of session attributes, all such attributes MUST implement the java.io.Serializable interface.

![image-20231108103810202](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/serializable/serializable.assets/7.webp)

![image-20231108103830125](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/serializable/serializable.assets/8.webp)

### Cache

주로 스프링에서 RedisCache를 사용하고 있어서 관련 문서를 찾아보았다.

![image-20231108110944119](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/serializable/serializable.assets/10.webp)

> https://docs.spring.io/spring-data/redis/docs/current/reference/html/#redis:serializer

`RedisCacheConfiguration` 에서도 확인되는 것 처럼 기본 Serializer로 JdkSerializationRedisSerializer 를 사용하고 있다. 물론 원한다면 변경할 수는 있다.

![image-20231108110700039](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/serializable/serializable.assets/9.webp)

JdkSerializationRedisSerializer 를 확인해보면 자바 기반의 default serializer 라고 한다.

![image-20231108111053203](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/serializable/serializable.assets/11.webp)

`SerializingConverter` 는  `DefaultSerializer` 를 사용한다.

![image-20231108111216457](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/serializable/serializable.assets/12.webp)

그리고 `DefaultSerializer`는 직렬화 할 때 해당 객체가 **Serializable** 를 구현해야만 작동한다.

![image-20231108111309251](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/serializable/serializable.assets/13.webp)

## Serializable

자바에서의 직렬화는 java.io.ObjectOutputStream 객체를 통해 이루어지며, 직렬화나 역직렬화 과정에서 특별히 다루어야 하는 클래스들은 아래의 시그니처에 해당하는 메서드들을 직접 구현하면 된다.

```java
private void writeObject(java.io.ObjectOutputStream out) throws IOException
private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException;
private void readObjectNoData() throws ObjectStreamException;
```

### Sample Code

코드를 직접 작성해서 테스트해보자. 먼저 직렬화 할 대상인 `Phone` 클래스를 선언한다.

${code:Phone.java}

```java
public class Phone {
    final String model;
    final String number;
    final String password;

    public Phone(String model, String number, String password) {
        this.model = model;
        this.number = number;
        this.password = password;
    }
}
```

이제 메인클래스에, 간단하게 직렬화 해주는 `Serializer` 클래스를 선언하고 테스트를 해보자.

${code:Main.java}

```java
public class Main {

    public static void main(String[] args) throws IOException {
        Phone phone = new Phone("iPhone15", "010-1234-5678", "0000");

        Serializer<Phone> phoneSerializer = new Serializer<>();
        byte[] serialize = phoneSerializer.serialize(phone);

        System.out.println("Serialized byte array:");
        System.out.println(Arrays.toString(serialize));

        System.out.println();
        System.out.println("Serialized base64 string:");
        System.out.println(phoneSerializer.serializeBase64(phone));
    }

    static class Serializer<T> {
        private final Base64.Encoder base64Encoder = Base64.getEncoder();

        public byte[] serialize(T object) throws IOException {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)
            ) {
                objectOutputStream.writeObject(object);
                return byteArrayOutputStream.toByteArray();
            }
        }

        public String serializeBase64(T object) throws IOException {
            byte[] serialized = serialize(object);
            return base64Encoder.encodeToString(serialized);
        }
    }

}
```

실행해보면 `NotSerializableException`이 발생한다. 

![image-20231107173333724](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/serializable/serializable.assets/1.webp)

직렬화 대상인 Phone 클래스가 Serializable 인터페이스를 구현하지 않았기 때문이다. 이번에는 `implements Serializable` 를 추가해준다.

${code:Phone.java}

```java
public class Phone implements Serializable {

    final String model;
    final String number;
    final String password;

    public Phone(String model, String number, String password) {
        this.model = model;
        this.number = number;
        this.password = password;
    }
}
```

이제 다시 실행해보면 정상적으로 직렬화가 이루어 지는 것을 확인할 수 있다.

![image-20231107173712748](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/serializable/serializable.assets/2.webp)

### serialVersionUID

위에서 Serializable를 구현하긴 했지만, 따로 serialVersionUID를 정의해주지 않았는데도 불구하고 직렬화는 문제 없이 이루어졌다. 이는 클래스에 `serialVersionUID`가 명시되어 있지 않은 경우 클래스의 세부 사항(클래스명, 멤버변수, 메서드 등)을 기반으로 자동으로 serialVersionUID가 계산되기 때문인데 클래스가 직렬화되는 시점에 내부적으로 계산된다.

이 자동 생성 과정은 JVM에 내장되어 있고 컴파일러의 구현에 따라 달라지기 때문에 일관되지 않을 수 있다. 그래서 클래스의 **직렬화 버전**을 명확히 관리하고자 한다면 serialVersionUID를 명시적으로 선언해주는 편이 좋다. 

먼저 자동으로 생성된 serialVersionUID 를 확인해보자. 아래의 코드 세줄을 추가해 실행하면 된다.

```java
ObjectStreamClass objectStreamClass = ObjectStreamClass.lookup(Phone.class);
long serialVersionUID = objectStreamClass.getSerialVersionUID();
System.out.println("serialVersionUID = " + serialVersionUID);
```

![image-20231107174807246](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/serializable/serializable.assets/3.webp)

> 자동으로 생성된 `serialVersionUID`가 확인된다.

이번에는 Phone 클래스에 직접 serialVersionUID를 명시해보자.

> serialVersionUID를 통해 저장된 클래스와 불러온 클래스가 같은 속성을 가지고 있고, 직렬화가 가능하다는 것을 보장하기 때문에 이후에 객체의 명세가 변경된다면 그에 따라 `serialVersionUID` 도 반드시 변경해주어야 한다.

${code:Phone.java}

```java
public class Phone implements Serializable {

    private static final long serialVersionUID = 1L;

    final String model;
    final String number;
    final String password;

    public Phone(String model, String number, String password) {
        this.model = model;
        this.number = number;
        this.password = password;
    }
}
```

그러고 나서 다시 위의 코드를 실행하면 해당 serialVersionUID가 적용된 모습이 보인다.

![image-20231107175009130](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/serializable/serializable.assets/4.webp)

이번에는 역직렬화를 해보도록 하겠다. 편의상 같은 환경 내에서 역직렬화를 하고 있지만, 서로 다른 시스템 및 서로 다른 jdk 환경에서도 문제 없이 역직렬화가 이루어질 수 있어야 한다. 먼저 편의상 Phone.java 클래스에 **toString** 메서드를 추가해주었다.

${code:Phone.java}

```java
    @Override
    public String toString() {
        return "Phone{" +
                "model='" + model + '\'' +
                ", number='" + number + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
```

그러고 `Serializer<T>` 클래스에 이번에는 역직렬화를 할 수 있는 메서드를 추가해준다.

```java
public T deserialize(byte[] serialized) throws IOException, ClassNotFoundException {
    try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serialized);
         ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)
    ) {
        return (T) objectInputStream.readObject();
    }
}
```

위에서 직렬화 해낸 바이트 어레이로 역직렬화를 테스트 한다.

```java
System.out.println();
byte[] serialized = new byte[]{-84, -19, 0, 5, 115, 114, 0, 25, 105, 111, 46, 103, 105, 116, 104, 117, 98, 46, 115, 104, 97, 110, 101, 112, 97, 114, 107, 46, 80, 104, 111, 110, 101, 0, 0, 0, 0, 0, 0, 0, 1, 2, 0, 3, 76, 0, 5, 109, 111, 100, 101, 108, 116, 0, 18, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 76, 0, 6, 110, 117, 109, 98, 101, 114, 113, 0, 126, 0, 1, 76, 0, 8, 112, 97, 115, 115, 119, 111, 114, 100, 113, 0, 126, 0, 1, 120, 112, 116, 0, 8, 105, 80, 104, 111, 110, 101, 49, 53, 116, 0, 13, 48, 49, 48, 45, 49, 50, 51, 52, 45, 53, 54, 55, 56, 116, 0, 4, 48, 48, 48, 48};
Phone deserializePhone = phoneSerializer.deserialize(serialized);
System.out.println("deserializePhone = " + deserializePhone);
```

그 결과는 다음과 같다. 문제 없이 복원해냈다.

![image-20231107175713404](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/serializable/serializable.assets/5.webp)

### transient

보안상의 이유로 직렬화를 해서는 안되는 필드가 있을 수 있다. 그때는 **transient** 키워드를 사용한다.

어노테이션이 자바에 추가된 이후였다면 어노테이션으로 들어왔겠지만, transient는 그보다 먼저 키워드로 추가되었다.

![image-20231108112342572](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/serializable/serializable.assets/14.webp)

> https://docs.oracle.com/javase/specs/jls/se7/html/jls-8.html#jls-8.3.1.3

password 필드에 transient를 붙이고 다시 한번 실행해보자.

${code:Phone.java}

```java
public class Phone implements Serializable {

    private static final long serialVersionUID = 1L;

    final String model;
    final String number;

    transient final String password;

    public Phone(String model, String number, String password) {
        this.model = model;
        this.number = number;
        this.password = password;
    }

    @Override
    public String toString() {
        return "Phone{" +
                "model='" + model + '\'' +
                ", number='" + number + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
    
}
```

![image-20231108113151830](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/serializable/serializable.assets/15.webp)

역직렬화한 데이터에 Password가 비어 있다.

### Customize

Password 가 비어 있는걸 원하지 않는다면, 혹은 직렬화로 객체를 복원 할 때 특별한 행위를 하고 싶다면 처음 언급했던 것 처럼 `readObject` 메서드를 구현하면 된다.

>  예를 들어, 이미지와 썸네일을 `byte[]` 로 가지고 있는 객체라면, 썸네일 부분은 transient 처리 한 뒤에, readObject 단계에서 이미지 데이터를 기반으로 썸네일을 생성해서 thumbnail 프로퍼티를 채워 넣는 등의 활용이 가능하다.

${code:Phone.java}

```java
public class Phone implements Serializable {

    private static final long serialVersionUID = 1L;

    final String model;
    final String number;

    transient String password;

    public Phone(String model, String number, String password) {
        this.model = model;
        this.number = number;
        this.password = password;
    }

    @Override
    public String toString() {
        return "Phone{" +
                "model='" + model + '\'' +
                ", number='" + number + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.password = "0000";
    }

}

```

>  단순하게, password를 변경하도록 커스터마이징 한 readObject 메서드

![image-20231108115807851](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/serializable/serializable.assets/16.webp)

그 결과 의도대로 작동한다. 조금 응용하면 직렬화 과정에 커스터마이징으로 암호화 및 복호화 과정을 추가해서 transient 없이도 안전하게 데이터를 전송하고 복원하게 활용할수도 있겠다.

### 성능비교

마지막으로 자바 직렬화가 실질적으로 성능면에서 얼마나 우세한지를 확인해보자.

테스트에 앞서 Jackson 라이브러리가 JSON 형태로 직렬화해낼 수 있도록 Phone 클래스에 기본 생성자 및 Getter를 추가해주었다.

${code:Phone.java}

```java
public class Phone implements Serializable {

    private static final long serialVersionUID = 1L;

    private String model;
    private String number;
    transient String password;

    public Phone() {
    }

    public Phone(String model, String number, String password) {
        this.model = model;
        this.number = number;
        this.password = password;
    }

    @Override
    public String toString() {
        return "Phone{" +
                "model='" + model + '\'' +
                ", number='" + number + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    public String getModel() {
        return model;
    }

    public String getNumber() {
        return number;
    }

}
```

JSON 직렬화를 직접 구현하지는 않고 가장 많이 쓰는 **Jackson** 라이브러리를 사용하였다.

${code:build.gradle}

```groovy
implementation 'com.fasterxml.jackson.core:jackson-databind:2.15.3'
```

테스트 코드를 추가 하고 100만회 실행한 시간을 비교하였다.

```java
private static void performanceTest(Phone phone) throws IOException {
        System.out.println();
        System.out.println("test json vs java");

        int testCount = 1_000_000;
        System.out.println("Test Json " + testCount + " times");
        long start = System.currentTimeMillis();
        for (int i = 0; i < testCount; i++) {
            testSpeedJson(phone);
        }
        System.out.println("Test Json took " + (System.currentTimeMillis() - start) + "ms");

        System.out.println();
        System.out.println("Test Java " + testCount + " times");
        start = System.currentTimeMillis();
        for (int i = 0; i < testCount; i++) {
            testSpeedJava(phone);
        }
        System.out.println("Test Java took " + (System.currentTimeMillis() - start) + "ms");
    }

    private static void testSpeedJson(Phone phone) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(phone);
        Phone recovered = mapper.readValue(json, Phone.class);
        assert recovered != null;
    }

    private static void testSpeedJava(Phone phone) {
        Serializer<Phone> phoneSerializer = new Serializer<>();
        try {
            byte[] serialize = phoneSerializer.serialize(phone);
            Phone recovered = phoneSerializer.deserialize(serialize);
            assert recovered != null;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
```

![image-20231108140403964](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/serializable/serializable.assets/17.webp)

> JSON이 약 4배 가량 더 많은 시간이 걸렸다.

대부분 당연히 Jackson의 직렬화보다 Java보다 빠를거라고 생각했을테고 결과도 그리 말해주니 예상한 결과라고 생각할것이다.

하지만, 사실 **이 테스트는 잘못되었다.** 

**ObjectMapper** 를 생성하는 비용이 훨씬 크기 때문이다. 특히 GC의 압박때문으로 보이는데, 테스트 카운트가 크게 늘어날수록 소요되는 시간은 훨씬 더 오래 걸렸다.

아래는 새로 작성한 코드다. ObjectMapper 및 Serializer를 한번 생성해서 재활용하도록 했으며, 혹시나 같은 객체에 대한 연산을 캐시해낼걸 대비해서 Phone 객체도 매번 다르게 생성했다.

```java

    private static void performanceTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Serializer<Phone> phoneSerializer = new Serializer<>();

        System.out.println();
        System.out.println("test json vs java");

        int testCount = 1_000_000;
        long start;

        start = System.currentTimeMillis();
        for (int i = 0; i < testCount; i++) {
            testSpeedJava(new Phone("iPhone" + i, String.valueOf(i), ""), phoneSerializer);
        }
        System.out.println("Test Java took " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < testCount; i++) {
            testSpeedJson(new Phone("iPhone" + i, String.valueOf(i), ""), mapper);
        }
        System.out.println("Test Json took " + (System.currentTimeMillis() - start) + "ms");
    }

    private static void testSpeedJson(Phone phone, ObjectMapper mapper) throws IOException {
        String json = mapper.writeValueAsString(phone);
        Phone recovered = mapper.readValue(json, Phone.class);
        assert recovered != null;
    }

    private static void testSpeedJava(Phone phone, Serializer<Phone> phoneSerializer) {
        try {
            byte[] serialize = phoneSerializer.serialize(phone);
            Phone recovered = phoneSerializer.deserialize(serialize);
            assert recovered != null;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
```

그럼 그 결과는 어땠을까? 

 Java 소요시간은 차이가 거의 없지만, 놀랍게도 Jackson의 JSON 변환은 눈에 띄게 빨라져서 심지어 Java의 직렬화보다도 훨씬 빨랐다.

![image-20231108141755531](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/serializable/serializable.assets/18.webp)

실제 성능은 데이터의 구조, 객체의 크기 등에 따라 달라질 수 있지만 JSON 직렬화가 더 빠를거라고 예상한 사람은 많지 않았을 것이다. 심지어 직렬화된 데이터의 크기도 JSON이 훨씬 더 작다. 자바의 직렬화는 기본적으로 해당 클래스의 메타데이터도 함께 포함시키기 때문이다.

## 결론

자바 직렬화는 자바 시스템에 최적화되어있다. JVM에 내장되어 있기 때문에 별도의 라이브러리나 직렬화에 대한 코드를 작성할 필요 없이 객체의 상태를 바이트스트림으로 변환하고 이를 다시 객체로 복원하는 과정을 매우 손쉽게 만들어준다.

이러한 직렬화는 특히 자바 기반의 시스템 내에서 객체의 저장, 전송, 캐싱이 필요할 때 특히 유용하다. 이러한 장점들 때문에 Java 기본 직렬화는 여전히 분산 시스템, RMI, 세션, 캐싱 등에서 중요한 역할을 하고있다.

하지만 현대에는 실무에서 더 이상 객체 직렬화를 사용할 일이 없다. 더 나은 직렬화 방법들이 너무나도 많다.

목적에 따라 다양한 직렬화 방법 중 상황에 맞는 방법을 잘 선택하도록 하자. `이펙티브 자바`에서도 자바 직렬화에 많은 문제가 있으니 대안을 찾으라고 하며 차라리 JSON이나 프로토콜 버퍼를 쓰라고 한다.

위에서 작성한 샘플 코드는 아래의 Github 저장소에서 확인할 수 있다.

> https://github.com/ShanePark/mdblog/tree/main/backend/java/serializable

**References**

- https://docs.oracle.com/javase/8/docs/api/java/io/Serializable.html
- https://docs.oracle.com/en/java/javase/11/docs/specs/serialization/index.html
- https://techblog.woowahan.com/2550/
- https://techblog.woowahan.com/2551/
- https://stackoverflow.com/questions/2020904/when-and-why-jpa-entities-should-implement-the-serializable-interface
- https://download.oracle.com/otndocs/jcp/ejb-3_0-fr-eval-oth-JSpec/
- https://www.inflearn.com/questions/33629
- https://docs.spring.io/spring-data/redis/docs/current/reference/html/#redis:serializer
- https://stackoverflow.com/questions/910374/why-does-java-have-transient-fields
- https://docs.oracle.com/javase/specs/jls/se7/html/jls-8.html#jls-8.3.1.3
- https://www.oracle.com/technical-resources/articles/java/serializationapi.html
- https://rick-hightower.blogspot.com/2014/04/which-is-faster-java-object.html#:~:text=Most%20people%20assume%20that%20Java,built%20in%20Java%20object%20serialization.
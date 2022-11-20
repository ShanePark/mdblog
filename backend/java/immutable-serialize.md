# [Java] 불변객체(Immutable Object)의 JSON 직렬화 및 역직렬화

## Intro

DTO 혹은 VO 객체를 생성 할 때, Immutable 하게 생성 한다면 여러가지 장점이 있습니다.

멀티 쓰레드 환경에서 동기화를 고려 할 필요 없이 안전하게 사용 할 수 있으며, 캐싱을 하기에도 유리합니다. 특히 mutable 객체를 잘못 코딩함으로서 생기는 사이드이펙트는 예측하기도 어려울 뿐더러 오류가 발견되는것도 쉽지 않습니다. 한참이 지나서야 도대체 알 수 없는 오류가 발생해서 이것 저것 한참을 디버깅을 하다가 객체를 잘못 다룬게 발견될때는 거의 소름이 돋을 정도입니다.

프로퍼티의 값이 변화될 일이 없고, 값이 변경되어서는 안되는 객체를 방어적 복사를 하는 거 보다는 애초에 불변객체로 생성 하는것이 성능을 비롯한 여러 가지 장점이 있습니다. 재사용도 얼마든지 할 수 있습니다.

이전의 자바 버전에서는 컬렉션의 immutable한 상태를 유지하기가 까다로웠으나 자바 버전이 올라가며 그것 또한 간단하게 처리 할 수 있게 되었습니다. 특히, 코틀린을 사용하면 이러한 불변객체의 활용을 더욱 쉽고 확실하게 할 수 있습니다.

## 불변 객체 만들기

### Older JDK

클래스를 상속 할 수 없도록 final 클래스로 선언 하고, 모든 프로퍼티를 final로 선언 해 줍니다.

그러고 객체를 생성 할 때에 생성자를 통해 한번에 필요한 값을 주입 하거나, 빌더 패턴등을 활용해 immutable한 객체를 생성 할 수 있습니다.

```java
final public class ImmutableObject {
  private final String name;
  private final int hp;

  public ImmutableObject(String name, int hp) {
    this.name = name;
    this.hp = hp;
  }
}
```

여기에 간단히 Getter 정도만 추가 해 주고 사용 하면 됩니다.

### Java14+

사실 자바 14에서 소개된 record 클래스가 바로 이런 용도로 추가 되었습니다.

위의 코드를 record로 변경하면 정말 간단하게 정리가 됩니다.

![image-20221120222305825](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/immutable-serialize.assets/image-20221120222305825.png)

> IntelliJ IDEA에서 record로 변환하라고 제안 하고 있습니다.

```java
public record ImmutableObject(String name, int hp){}
```

record 로 변경하면 이렇게 간단해집니다. 하지만 JDK 14 이상을 사용 할 때만 활용 할 수 있다는 단점이 있습니다.

## JSON 직렬화

그런데 위의 코드를 JSON 으로 직렬화 하려고 하면 문제가 생깁니다.

한번 테스트 코드를 통해 확인 해 보겠습니다.

```java
package com.tistory.shanepark.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class SerializeImmutableObject {

    @Test
    public void test() throws JsonProcessingException {
        ImmutableObject immutableObject = new ImmutableObject("Shane", 100);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(immutableObject);
        Assertions.assertThat(json).isEqualTo("{\"name\":\"Shane\",\"hp\":100}");
    }

    final static public class ImmutableObject {
        private final String name;
        private final int hp;

        public ImmutableObject(String name, int hp) {
            this.name = name;
            this.hp = hp;
        }
    }

}

```

정말 간단하게 json 으로 직렬화 해서 출력 해 주는 코드인데요, 실행을 하면 아래와 같은 오류가 발생 합니다.

> com.fasterxml.jackson.databind.exc.InvalidDefinitionException: No serializer found for class com.tistory.shanepark.json.SerializeImmutableObject$ImmutableObject and no properties discovered to create BeanSerializer (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS)

객체의 serializer를 찾지 못했다고 하는데요, 이때는 간단하게 Getter를 생성 해 주면 문제가 해결 됩니다.

```java
    final static public class ImmutableObject {
        private final String name;
        private final int hp;

        public ImmutableObject(String name, int hp) {
            this.name = name;
            this.hp = hp;
        }

        public String getName() {
            return name;
        }

        public int getHp() {
            return hp;
        }
    }
```

다시 테스트를 돌려 보면 무사히 통과 됩니다.

![image-20221120223159007](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/immutable-serialize.assets/image-20221120223159007.png)

## JSON 역직렬화

하지만 역직렬화는 어떨까요?

위와 같이 만든 불변 객체에는 기본 생성자가 존재하지 않습니다.

private 이나 default 접근자로라도 기본 생성자를 만들어줘야 Jackson이 원래대로 리플렉션을 통해 객체를 생성 할 텐데, immutable하게 만들려다보니 기본 생성자를 만들 수 없는 상황 입니다.

테스트 코드를 작성 하여 문제 상황을 만들고 해결 해 보도록 하겠습니다.

```java
package com.tistory.shanepark.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class SerializeImmutableObject {

    @Test
    public void test() throws JsonProcessingException {
        ImmutableObject immutableObject = new ImmutableObject("Shane", 100);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(immutableObject);
        Assertions.assertThat(json).isEqualTo("{\"name\":\"Shane\",\"hp\":100}");

        ImmutableObject objectFromJson = objectMapper.readValue(json, ImmutableObject.class);
        Assertions.assertThat(objectFromJson).isEqualTo(immutableObject);
    }

    final static public class ImmutableObject {
        private final String name;
        private final int hp;

        public ImmutableObject(String name, int hp) {
            this.name = name;
            this.hp = hp;
        }

        public String getName() {
            return name;
        }

        public int getHp() {
            return hp;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ImmutableObject that)) return false;
            return hp == that.hp && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, hp);
        }

    }

}

```

이번에는 먼저 생성한 json 문자열을 `readValue` 메서드를 통해 통해 다시 객체로 만들어 주려고 합니다.

그 후에는 원본 객체와 비교 하는데요, 이를 위해 equals와 hashCode를 재 정의 해 주었습니다.

> equals를 재 정의 할 때는, hashCode도 반드시 재 정의 해 주어야 합니다. equals가 같은 객체로 인식함에도 hash 값이 다르다면 hash를 사용하는 Map이나 Set등에서 객체를 활용 할 때에 반드시 문제가 생깁니다.

### 오류

이제 테스트 코드를 실행 하면 아래와 같은 에러가 발생 합니다.

![image-20221120223930681](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/immutable-serialize.assets/image-20221120223930681.png)

> com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Cannot construct instance of `com.tistory.shanepark.json.SerializeImmutableObject$ImmutableObject` (no Creators, like default constructor, exist): cannot deserialize from Object value (no delegate- or property-based Creator)
>  at [Source: (String)"{"name":"Shane","hp":100}"; line: 1, column: 2]

에러를 자세히 읽어 보면 기본 생성자가 없기 때문에 값으로부터 객체로의 deserialize가 불가능 하다고 합니다.

### 해결

이러한 여러 가지 상황에서 사용 할 수 있도록 jackson은 JSON 직렬화에 사용 할 수 있는 편리하고 다양한 어노테이션들을 제공 해 줍니다.

어느 상황에서든 암묵적인 것 보다는 명시적인게  좋은데요, 특히 이렇게 암묵적인 방법에서 문제가 생겼을 때는 확실하게 그 방법을 명시 해 주어야 합니다.

Jackson에게 어떻게 ImmutableObject을 생성해야 할지에 대해 `@JsonCreator` 어노테이션과 `@JsonProperty` 어노테이션을 통해 알려주도록 하겠습니다.

```java
  @Getter
    final static public class ImmutableObject {
        private final String name;
        private final int hp;

        @JsonCreator
        public ImmutableObject(
                @JsonProperty("name") String name,
                @JsonProperty("hp") int hp) {
            this.name = name;
            this.hp = hp;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ImmutableObject that)) return false;
            return hp == that.hp && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, hp);
        }
    }
```

이제 테스트 코드를 실행 하면 정상적으로 통과가 됩니다.

![image-20221120225519838](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/immutable-serialize.assets/image-20221120225519838.png)

### Record

사실 record의 경우에는 immutable도 지켜 지면서 특별한 어노테이션 없이도 마샬링 및 언마샬링이 가능합니다.

```java
public class SerializeImmutableRecord {

    @Test
    public void test() throws JsonProcessingException {
        ImmutableRecord record = new ImmutableRecord("Shane", 100);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(record);
        Assertions.assertThat(json).isEqualTo("{\"name\":\"Shane\",\"hp\":100}");

        ImmutableRecord recordFromJson = objectMapper.readValue(json, ImmutableRecord.class);
        Assertions.assertThat(recordFromJson).isEqualTo(record);
    }

    public record ImmutableRecord(String name, int hp) {
    }

}
```

> 위의 테스트 코드는 아무런 문제 없이 통과 됩니다.

사실 이러한 편리함 때문에 Kotlin이나 최신의 JDK 버전을 사용하다가 레거시 코드를 작업 하려면 코드가 장황해지며 답답함이 생기기도 합니다.

지금까지 불변객체를 Jackson으로 직렬화 및 역직렬화 하는 방법에 대해 알아보았습니다.

이상입니다. 

**References**

- https://stackoverflow.com/questions/22162916/how-does-the-jackson-mapper-know-what-field-in-each-json-object-to-assign-to-a-c
- https://www.stackchief.com/blog/Java%20ObjectMapper%20%7C%20What%20it%20is%20%7C%20How%20it%20works
- https://stackoverflow.com/questions/30568353/how-to-de-serialize-an-immutable-object-without-default-constructor-using-object
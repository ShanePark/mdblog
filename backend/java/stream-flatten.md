# [Java] Stream을 활용해 중첩 컬렉션 및 Tree 평면화하기

## Intro

TREE 형태로 만들어둔 `메뉴` 목록을 평면화해야 하는 일이 있었습니다. Stream의 flatMap을 활용하면 이중 컬렉션의 평면화를 아주 쉽게 할 수 있는데, 트리 전체를 평면화 시키는건 코드를 깔끔하게 작성하는데 고민이 많았습니다.

스트림을 활용해 트리 형태의 그래프를 평면화 시키는 작업을 한번 해 보도록 하겠습니다.

## 이중 컬렉션 평면화

가장 먼저 이중 컬렉션의 평면화를 몸풀기로 진행 해 보도록 하겠습니다. 

이 작업은 필요한 경우가 잦아 모두 한번쯤은 작성해 해 보셨을거에요.

1. 일단 중첩 컬렉션을 평면화시키는 메서드를 가진 인터페이스를 선언 하고

```java
interface CollectionFlatter<T> {
  Collection<T> flatten(Collection<Collection<T>> collection);
}
```

2. 거기에 맞는 테스트 코드를 작성 해 줍니다.

```java
@Test
public void flattenCollection() {
  // Given
  String SUB1_TXT1 = "sub1_txt1";
  String SUB1_TXT2 = "sub1_txt2";
  String SUB2_TXT1 = "sub2_txt1";
  String SUB2_TXT2 = "sub2_txt2";

  List<List<String>> list = new ArrayList<>();

  ArrayList<String> sub1 = new ArrayList<>();
  sub1.add(SUB1_TXT1);
  sub1.add(SUB1_TXT2);

  ArrayList<String> sub2 = new ArrayList<>();
  sub1.add(SUB2_TXT1);
  sub1.add(SUB2_TXT2);

  list.add(sub1);
  list.add(sub2);

  // When
  CollectionFlatter flatter = null;
  Collection flatData = flatter.flatten(list);

  // Then
  Assertions.assertThat(flatData).containsExactlyInAnyOrder(SUB1_TXT1, SUB1_TXT2, SUB2_TXT1, SUB2_TXT2);
}
```

3. 이제 해당 인터페이스를 구현 해 줍니다. 두가지 방법으로 진행 해 볼텐데요, 전형적인 for 반복문과 스트림을 활용 해 보도록 하겠습니다.

### for loop

전형적인 이중 for문 구조 입니다. 반환할 컬렉션 (여기에서는 ArrayList)를 선언 해 준 후에 깊은 순회를 하며 요소를 모두 해당 컬렉션에 추가 해 준 뒤, 최종적으로 반환까지 해 줍니다.

```java
class CollectionForFlatter<T> implements CollectionFlatter<T> {
  @Override
  public Collection flatten(Collection<Collection<T>> collection) {
    Collection<T> result = new ArrayList<>();
    for (Collection<T> subCollection : collection) {
      for (T t : subCollection) {
        result.add(t);
      }
    }
    return result;
  }
}
```

### stream

Stream에는 flatMap 메서드가 있는데, 이를 활용하면 매우 간단하게 스트림을 평면화 할 수 있습니다.

```java
class CollectionStreamFlatter<T> implements CollectionFlatter<T> {
  @Override
  public Collection flatten(Collection<Collection<T>> collection) {
    return collection.stream()
      .flatMap(c -> c.stream())
      .collect(Collectors.toList());
  }
}
```

자바 8에서 추가된 메소드 레퍼런스를 활용할 수 있습니다.

```java
class CollectionStreamFlatter<T> implements CollectionFlatter<T> {
  @Override
  public Collection flatten(Collection<Collection<T>> collection) {
    return collection.stream()
      .flatMap(Collection::stream)
      .collect(Collectors.toList());
  }
}
```

### 테스트

이제 두가지 구현을 모두 테스트 해 봅니다.

```java
package com.tistory.shanepark.stream.flatten;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FlattenCollectionStream {

    @Test
    public void flattenCollection() {
        // Given
        String SUB1_TXT1 = "sub1_txt1";
        String SUB1_TXT2 = "sub1_txt2";
        String SUB2_TXT1 = "sub2_txt1";
        String SUB2_TXT2 = "sub2_txt2";

        List<List<String>> list = new ArrayList<>();

        ArrayList<String> sub1 = new ArrayList<>();
        sub1.add(SUB1_TXT1);
        sub1.add(SUB1_TXT2);

        ArrayList<String> sub2 = new ArrayList<>();
        sub1.add(SUB2_TXT1);
        sub1.add(SUB2_TXT2);

        list.add(sub1);
        list.add(sub2);

        // When
        CollectionFlatter flatter1 = new CollectionStreamFlatter();
        Collection flatData1 = flatter1.flatten(list);

        CollectionFlatter flatter2 = new CollectionForFlatter();
        Collection flatData2 = flatter2.flatten(list);

        // Then
        Assertions.assertThat(flatData1).containsExactlyInAnyOrder(SUB1_TXT1, SUB1_TXT2, SUB2_TXT1, SUB2_TXT2);
        Assertions.assertThat(flatData2).containsExactlyInAnyOrder(SUB1_TXT1, SUB1_TXT2, SUB2_TXT1, SUB2_TXT2);
    }

    class CollectionStreamFlatter<T> implements CollectionFlatter<T> {
        @Override
        public Collection flatten(Collection<Collection<T>> collection) {
            return collection.stream()
                    .flatMap(c -> c.stream())
                    .collect(Collectors.toList());
        }
    }

    class CollectionForFlatter<T> implements CollectionFlatter<T> {
        @Override
        public Collection flatten(Collection<Collection<T>> collection) {
            Collection<T> result = new ArrayList<>();
            for (Collection<T> subCollection : collection) {
                for (T t : subCollection) {
                    result.add(t);
                }
            }
            return result;
        }
    }

    interface CollectionFlatter<T> {
        Collection<T> flatten(Collection<Collection<T>> collection);
    }

}
```

![image-20221105233755235](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/stream-flatten.assets/image-20221105233755235.png)

> 실행 결과 아무런 문제 없이 성공합니다.

## 이중 트리 평면화

이번에는 아래와 같은 구조를 가진 트리를 평면화 시켜 보도록 하겠습니다.

```java
class Node {
  String value;
  List<Node> children = new ArrayList<>();

  public Node(String value) {
    this.value = value;
  }

  Node addChild(String value) {
    Node child = new Node(value);
    children.add(child);
    return child;
  }
}
```

그리고 마찬가지로 테스트 코드를 작성 합니다.

```java
@Test
public void flattenTest() {
  // Given
  Node node1 = new Node("node1");
  Node node2 = new Node("node2");
  List<Node> heads = List.of(node2, node1);
  Node node1_1 = node1.addChild("node1_1");
  Node node1_2 = node1.addChild("node1_2");

  // When
  NodeFlatter flatter = new StreamNodeFlatter();
  NodeFlatter flatter2 = new ForNodeFlatter();

  // Then
  assertThat(flatter.flatten(heads).stream()).containsExactlyInAnyOrder(node1, node1_1, node1_2, node2);
  assertThat(flatter2.flatten(heads).stream()).containsExactlyInAnyOrder(node1, node1_1, node1_2, node2);
}
```

하나씩 구현 해 봅니다.

### ForNodeFlatter

```java
class ForNodeFlatter implements NodeFlatter {
  @Override
  public List<Node> flatten(List<Node> nodes) {
    List<Node> result = new ArrayList<>();
    for (Node node : nodes) {
      result.add(node);
      for (Node child : node.children) {
        result.add(child);
      }
    }
    return result;
  }
}
```

사실 위에서 이중 컬렉션을 평면화 할 때도 가능하긴 했는데, addAll 메서드를 사용하면 코드상에서 indent depth를 하나 줄일 수 있습니다.

```java
class ForNodeFlatter implements NodeFlatter {
  @Override
  public List<Node> flatten(List<Node> nodes) {
    List<Node> result = new ArrayList<>();
    for (Node node : nodes) {
      result.add(node);
      result.addAll(node.children);
    }
    return result;
  }
}
```

트리 구조에서는 스스로 및 자식 노드들을 추가 하기 때문에 스스로를 추가하는 add가 하나 더 있는 모습 입니다.

### StreamNodeFlatter

이번에는 스트림을 이용 해 보도록 하겠습니다.

```java
class StreamNodeFlatter implements NodeFlatter {
  @Override
  public List<Node> flatten(List<Node> nodes) {
    return nodes.stream()
      .flatMap(node -> Stream.concat(Stream.of(node), node.children.stream()))
      .collect(Collectors.toList());
  }
}
```

스트림에서는 concat 메서드를 활용 하면 두개의 스트림을 합칠 수 있는데요, 그걸 활용해서 스스로와 자식노드들을 하나의 스트림으로 평면화 시킨 모습입니다.

![image-20221107205715585](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/stream-flatten.assets/image-20221107205715585.png)

> 역시 무사히 테스트 코드를 통과 합니다.

## 다중 트리 평면화

이번에는 해당 트리의 최고 depth를 알 수 없는 상태라고 가정 해 보겠습니다.

이때는 어떤식으로 평면화를 해야 할까요? 깊이우선이 되었건, 너비우선이 되었건 완전탐색을 해야 한다는건 변함이 없습니다.

이번에도 테스트 코드를 먼저 작성 하도록 하겠습니다.

```java
@Test
public void flattenTest() {
  // Given
  List<Node> heads = new ArrayList<>();
  Node node1 = new Node("node1");
  Node node2 = new Node("node2");
  heads.add(node2);
  heads.add(node1);

  Node node1_1 = node1.addChild("node1_1");
  Node node1_2 = node1.addChild("node1_2");

  Node node1_1_1 = node1_1.addChild("node1_1_1");
  Node node1_1_2 = node1_1.addChild("node1_1_2");

  Node node1_1_1_1 = node1_1_1.addChild("node1_1_1_1");
  Node node1_1_1_2 = node1_1_1.addChild("node1_1_1_2");

  Node node1_2_1 = node1_2.addChild("node1_2_1");
  Node node1_2_2 = node1_2.addChild("node1_2_2");

  // When
  NodeFlatter flatter = new StreamNodeFlatter();
  NodeFlatter flatter2 = new ForNodeFlatter();

  // Then
  assertThat(flatter.flatten(heads).stream())
    .containsExactlyInAnyOrder(node1, node1_1, node1_1_1, node1_1_1_1, node1_1_1_2, node1_1_2, node1_2, node1_2_1, node1_2_2, node2);
  assertThat(flatter2.flatten(heads).stream())
    .containsExactlyInAnyOrder(node1, node1_1, node1_1_1, node1_1_1_1, node1_1_1_2, node1_1_2, node1_2, node1_2_1, node1_2_2, node2);
}
```

다중 트리의 경우에는 재귀적으로 처리하면 코드를 깔끔하게 작성 할 수 있습니다. 

다만, 트리가 아니고 사이클이 존재하는 경우에는 무한재귀에 빠질 수 있으니 유의해야 합니다.

### ForNodeFlatter

```java
class ForNodeFlatter implements NodeFlatter {
  @Override
  public List<Node> flatten(List<Node> nodes) {
    List<Node> result = new ArrayList<>();
    for (Node node : nodes) {
      result.add(node);
      result.addAll(flatten(node.children));
    }
    return result;
  }
}
```

다중트리지만, 재귀 형태로 인해 코드 자체의 복잡성은 거의 차이가 없습니다.

### StreamNodeFlatter

```java
class StreamNodeFlatter implements NodeFlatter {
  @Override
  public List<Node> flatten(List<Node> nodes) {
    return nodes.stream()
      .flatMap(node -> Stream.concat(Stream.of(node), flatten(node.children).stream()))
      .collect(Collectors.toList());
  }
}
```

스트림의 경우에는 코드가 더욱 깔끔하게 떨어집니다.

물론 readability 측면에서는 조금 아쉽기도 한데요 메서드를 분리하는 방법이 있을 수 있겠네요.

```java
class StreamNodeFlatter implements NodeFlatter {
  @Override
  public List<Node> flatten(List<Node> nodes) {
    return nodes.stream()
      .flatMap(node -> flatStream(node))
      .collect(Collectors.toList());
  }

  private Stream<Node> flatStream(Node node) {
    return Stream.concat(Stream.of(node), flatten(node.children).stream());
  }
}
```

![image-20221107213725470](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/stream-flatten.assets/image-20221107213725470.png)

> 테스트 역시 성공합니다.

지금까지 스트림을 평면화 하는 방법에 대해 알아보았습니다.

감사합니다.  

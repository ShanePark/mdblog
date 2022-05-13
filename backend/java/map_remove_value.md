# Java) HashMap에서 특정 value를 제거하기

## Intro

Map에서 특정 key를 가진 EntrySet을 제거하는건 어렵지 않습니다.

```java
map.remove(삭제할key객체)
```

![image-20220513134921767](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/map_remove_value.assets/image-20220513134921767.png)

key로 제거 하는 것 뿐만 아니라, key와 value를 모두 인자로 넘겨서 해당하는 key-value 쌍이 있을때에만 제거하는 방법도 있습니다.

그렇다면 특정 값만 제거 하고 싶다면 어떻게 해야 할까요?

## values().remove(Object value)

![image-20220513135116559](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/map_remove_value.assets/image-20220513135116559.png)

단순 궁금증에 values()로 값들이 들어있는 컬렉션 객체를 불러 온 뒤에, 거기에서 remove 메서드를 호출 해 보았는데 정말 감쪽같이 key, value 쌍이 모두 사라졌습니다.

values는 제거가 될 거라고 생각했지만, keySet은 변화가 있기 때문에 오류가 발생하거나 혹은 문제가 생길 거라고 예상 했는데 기대한 대로 정확히 작동 해 주었습니다.

양방향 Map 을 만들 필요도 없이, O(N) 복잡도로 이런 깔끔한 코드로 값 삭제가 가능합니다!

## 작동원리

그렇다면 이제 `map.values().remove(값);`을 했을 때 의도한 대로 작동한 이유를 알아 보겠습니다. 혹시나 우연히 지금은 잘 작동한 것 처럼 보이지만 이걸 그대로 사용했다가 나중에 큰 문제가 생길 수 도 있기 때문에 확실히 하는게 좋겠죠?

### AbstractMap

일단 HashMap은 AbstractMap 을 상속 하고 있습니다.

![image-20220513135513884](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/map_remove_value.assets/image-20220513135513884.png)

AbstractMap의 values() 메서드 부분을 확인 해 보았는데요

AbstractCollection 을 반환 해 주는데, 이 컬렉션의 이터레이터로 Map이 가진 entrySet의 iterator를 필드로 가진 익명 객체를 반환 해 줍니다. 

그러면서 `remove()` 메서드를 호출 할 때에는 해당 entrySet의 이터레이터에 있는 remove 메서드를 호출 하도록 작성 되어 있네요

`next()` 도 확인 해 보았더니  EntrySet 이터레이터에서 value만을 반환 해 주도록 되어있습니다.

이쯤에서 어느정도 구조가 보이기 시작하며 기능이 동작한 이유가 조금씩 추측 가능합니다.

### AbstractCollection

이번에는 AbstractCollection 클래스로 넘어가 확인 해 보도록 합니다.

![image-20220513140338183](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/map_remove_value.assets/image-20220513140338183.png)

`remove(Object o)` 메서드를 호출 하면, AbstractCollection 에서는 이터레이터를 계속 순회하며 it.next()로 일치하는 값을 찾습니다. 위에서 미리 확인 했던 것 처럼, 여기에서 it.next()가 확인하는 값은 사실 `entrySet().iterator().next().getValue()` 입니다.

![image-20220513140928087](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/map_remove_value.assets/image-20220513140928087.png)

그러다가 일치하는 값을 찾았을 때에는 `it.remove()`를 호출 하고 return을 반환 하는데요, 여기서의 it.remove()는 사실 `entrySet().iterator().remove()`가 호출 되기 때문에 안전하게 key-value 쌍이 함께 제거 된 것 이었습니다.

이상으로 Map에서 특정 값을 가진 엔트리셋을 삭제하는 방법에 대해 알아보았습니다. 

다만 이미 예상하신 것 처럼 이 방법으로 삭제 할 경우에는 같은 value를 가진 모든 엔트리셋을 제거하는게 아닌 처음 해당 value를 찾으면 제거 후 즉각 종료가 되기 때문에 중복되는 value가 없다는 전제 하에서만 사용 할 수 있는 방법입니다.

value의 중복이 가능한 상황에서 모두 삭제 하고 싶다면 코드가 조금 더 복잡해지는데

```java
    @Test
    public void test2() {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");
        map.put(-1, "one");

        Set<Integer> deleteKeys = new HashSet<>();
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if ("one".equals(entry.getValue())) {
                deleteKeys.add(entry.getKey());
            }
        }

        deleteKeys.stream().forEach(k -> map.remove(k));

        assertThat(map.size()).isEqualTo(2);
    }
```

> deleteKeys 로 삭제할 키를 모두 기록 해 두었다가 순회하며 제거하는 코드

스트림으로만 표현 하면 아래와 같습니다.

```java
map.entrySet().stream()
    .filter(e -> "one".equals(e.getValue()))
    .collect(Collectors.toUnmodifiableSet())
    .forEach(e -> map.remove(e.getKey()));
```

> ConcurrentModificationException 이 발생하기 때문에 한번 따로 collect 해주어야 합니다.

이상입니다.
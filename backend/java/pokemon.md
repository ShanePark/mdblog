# java) 151개의 포켓몬을 모두 모으려면 몇개의 포켓몬빵을 사먹어야 하는가

## Intro

![](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/pokemon.assets/DBXICNNXCZFPPE67B66XNNMP5I.jpg)

> 출처: 온라인 커뮤니티

포켓몬 빵으로 인해 간만에 온라인이 시끌벅쩍 합니다. 몇 해 마다 이런 특정 아이템의 품귀 현상이 반복되는데, 꼬꼬면과 허니버터칩에 이어 포켓몬까지 셋 다 전혀 다른 성격의 객체들이지만 잠깐의 유행에 그치고 말 것이란건 모두가 경험을 통해 잘 알고 있습니다.

20여년 전에는 맛 없는 빵을 먹는 것도 좋았고 스티커를 모으는 것도 설레였지만 아쉽게도 이제 둘 모두에 흥미를 잃었습니다. 

다만 이번 상황을 접하며 모든 스티커를 다 모으려면 평균 몇개의 빵을 사야 하는지가 궁금해졌습니다. 실제 수많은 스트리머들이 수백개씩 빵을 벌크로 구매해서는 모든 스티커 모으기 컨텐츠를 진행하기도 합니다.

수학적인 풀이 방법도 있겠지만 컴퓨터에게 잘만 부탁하면 원하는 정보를 모두 계산해서 알려주니 자바로 시뮬레이션을 해 보겠습니다.

## Requirements

### 가격

검색해 보니 빵 하나의 가격은 1500원 이라고 합니다. 성심당 튀김소보로 가격인데 좀 비싸긴 하네요.

### 포켓몬 수

이게 좀 애매한데, 총 포켓몬은 151 마리지만 `피카츄, 이브이, 이상해씨, 파이리, 꼬부기, 잠만보, 뮤츠, 뮤` 이렇게 총 8종류가 오리지널 일러스트 외에 1가지씩 추가 되어 2종류로 존재한다고 합니다. 

151마리의 포켓몬을 모두 모으는 계산을 할지, 아니면 159종류의 스티커를 모두 모으는 계산을 할 지 고민이 되지만 그냥 두가지 상황 모두 계산 해 보려고 합니다.

### 확률

이게 가장 중요한 건데.. 삼립에서 뮤와 뮤츠를 제외하고는 모두 같은 확률로 제공하려고 노력했다고 합니다. 뮤와 뮤츠의 확률을 정확히 제공 해 주었다면 정확한 계산이 가능 하겠지만 당연하게도 공개된 확률은 없습니다.

그렇기에 159개의 스티커가 나올 확률이 모두 같은 경우와 임의의 확률을 넣어서 각각의 상황을 모두 계산 해 보겠습니다.

## 구현

### 랜덤 포켓몬 뽑기

`buyPokemon()` 함수를 먼저 구현하는데, 단순하게 0~158 중에 하나의 숫자를 같은 확률로 반환하면 됩니다.

![image-20220320174601275](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/pokemon.assets/image-20220320174601275.png)

`Math.random()` 함수는 0보다 크거나 같고, 1보다는 작은 수를 랜덤하게 생성 해 줍니다. 이럴 활용해 코드를 작성하면

```java
private static int buyPokemon() {
  return (int) (Math.random() * 159);
}
```

그런데 0번 포켓몬은 존재하지 않기 때문에.. 이게 참 배열을 쓸 때 마다 헷갈리기도 하고 고민되게 하는 부분이지만 이번에는 혼동을 줄이기 위해 1번 부터 생성 되도록 코드를 작성하기로 합니다.

```java
private static int buyPokemon() {
  return (int) (Math.random() * 159) + 1;
}
```

> 마지막에 1을 더해줍니다.

어느정도 균등하게 숫자가 나오는지 100만번 했을때 각각 몇번씩 숫자가 나오는지 확인 해 보겠습니다.

```java
@Test
void testBuyPokemon() {
  int[] arr = new int[160];
  for (int i = 0; i < 1_000_000; i++) {
    arr[buyPokemon()]++;
  }
  for (int i = 1; i <= 159; i++) {
    System.out.print(arr[i] + " ");
    if (i % 10 == 0)
      System.out.println();
  }
  Assertions.assertThat(arr[0]).isEqualTo(0);
  Assertions.assertThat(Arrays.stream(arr).sum()).isEqualTo(1_000_000);
}
```

1 ~159 index의 배열에 각각 숫자가 나올 때 마다 카운트를 하도록 해서 100만번 시행 했을 경우 각 숫자를 프린트 합니다.

그리고  마지막에는 1~159 에 해당하는 숫자만 빠짐없이 나왔는지 확인 하기 위해 arr[0]은 0인지를 확인 하고 원소를 합친 수는 다시 1,000,000이 맞는지 확인 하는 테스트 코드를 작성했습니다.

![image-20220320180431104](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/pokemon.assets/image-20220320180431104.png)

> 실행 결과

그 결과 비교적 균등하게 6천여번씩 나오는게 확인 되었으며, 오류가 없어 테스트 케이스로 무사히 통과 했습니다.

### 151마리 포켓몬 모두 모으기

151마리의 포켓몬을 모두 모으는것을 목표로 할 경우 입니다. Set 을 생성해 포켓몬을 밀어 넣는데, 151번 이후의 포켓몬은 새로 추가된 `피카츄, 이브이, 이상해씨, 파이리, 꼬부기, 잠만보, 뮤츠, 뮤` 지만 사실 해당 포켓몬들의 정확한 번호를 넣을 필요는 없습니다. 어느 포켓몬인지가 중요한게 아니고 8마리의 포켓몬은 중복해서 나온다는게 중요합니다.

151번 이후로는 모듈러 연산으로 처리하도록 했습니다.

```java
@Test
void test151() {
  Set<Integer> pokeDex = new HashSet<>();
  int cnt = 0;
  while (pokeDex.size() < 151) {
    pokeDex.add(buyPokemon() % 151);
    cnt++;
  }

  System.out.println("total loaf of bread = " + cnt);

}
```

![image-20220320181952362](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/pokemon.assets/image-20220320181952362.png)

> 실행 결과

827개의 빵을 사먹고 나서야 151마리를 다 모았습니다. 그런데 시행 할 때마다 편차가 제법 컸기 때문에 여러번 시행 하여 평균을 내보도록 하겠습니다.

```java
    @Test
    void test151() {
        List<Integer> result = new ArrayList<>();
        final int TRY = 100;
        for (int i = 0; i < TRY; i++) {
            Set<Integer> pokeDex = new HashSet<>();
            int cnt = 0;
            while (pokeDex.size() < 151) {
                pokeDex.add(buyPokemon() % 151);
                cnt++;
            }
            result.add(cnt);
        }

        int max = result.stream().mapToInt(Integer::intValue).max().getAsInt();
        int min = result.stream().mapToInt(Integer::intValue).min().getAsInt();
        double avg = result.stream().mapToInt(Integer::intValue).average().getAsDouble();

        System.out.println("max = " + max);
        System.out.println("min = " + min);
        System.out.println("avg = " + avg);

    }

```

![image-20220320182419838](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/pokemon.assets/image-20220320182419838.png)

> 실행 결과

100번을 시도 했더니, 최고 많이 사먹어야 했을 경우는 1391개. 최소는 470개 그리고 평균은 860개 정도 되었습니다.

보다 정확한 계산을 위해 100만번을 시도 해 보는데, 꽤나 오래 걸리기 때문에 수행 시간도 체크를 하도록 했습니다.

```java
@Test
void test151() {
  List<Integer> result = new ArrayList<>();
  StopWatch stopWatch = new StopWatch();
  stopWatch.start();
  final int TRY = 1_000_000;
  for (int i = 0; i < TRY; i++) {
    Set<Integer> pokeDex = new HashSet<>();
    int cnt = 0;
    while (pokeDex.size() < 151) {
      pokeDex.add(buyPokemon() % 151);
      cnt++;
    }
    result.add(cnt);
  }

  int max = result.stream().mapToInt(Integer::intValue).max().getAsInt();
  int min = result.stream().mapToInt(Integer::intValue).min().getAsInt();
  double avg = result.stream().mapToInt(Integer::intValue).average().getAsDouble();
  stopWatch.stop();

  System.out.println("max = " + max);
  System.out.println("min = " + min);
  System.out.println("avg = " + avg);
  System.out.println("stopWatch.getTotalTimeSeconds() = " + stopWatch.getTotalTimeSeconds());

}
```

![image-20220320183057065](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/pokemon.assets/image-20220320183057065.png)

> 실행 결과

26초 정도 걸려 계산을 해냈는데, 역시 최대치와 최소치가 제법 멀어졌습니다. 똑같은 챌린지를 100만명이 한다면 누군가는 377 개만 사고도 다 모으지만 누군가는 3000개를 사고도 미션을 마치지 못한다는 이야기 입니다.

어쨌든 평균치를 좀 더 정확히 하려고 표본을 늘린건데 100번 했을 때와 평균치가 그리 차이 나지는 않습니다.

<u>**결과: 151마리의 포켓몬을 모으려면 평균 881.5개의 빵을 사먹어야 합니다. (뮤와 뮤츠가 다른 포켓몬과 같은 확률로 나오는 경우에)**</u>

### 159개의 스티커 모두 모으기

오리지널 스티커와 새로 추가된 일러스트는 엄연히 다릅니다.

![image-20220320184004433](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/pokemon.assets/image-20220320184004433.png)![image-20220320184037880](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/pokemon.assets/image-20220320184037880.png)

> 사진 출처: 중고나라 피카츄 판매자

앉아있는 피카츄도 윙크하는 피카츄도 모두 갖고싶다면 159개의 스티커를 모두 모아야 합니다.

위에 했던 코드에서 모듈러 연산을 하지 않고 그대로 번호를 포켓몬 도감에 집어 넣어, 159마리의 포켓몬을 모두 모을 때 까지 계속해서 포켓몬 빵을 구입 하면 됩니다.

```java
@Test
void test159() {
  List<Integer> result = new ArrayList<>();
  StopWatch stopWatch = new StopWatch();
  stopWatch.start();
  final int TRY = 1_000_000;
  for (int i = 0; i < TRY; i++) {
    Set<Integer> pokeDex = new HashSet<>();
    int cnt = 0;
    while (pokeDex.size() < 159) {
      pokeDex.add(buyPokemon());
      cnt++;
    }
    result.add(cnt);
  }

  int max = result.stream().mapToInt(Integer::intValue).max().getAsInt();
  int min = result.stream().mapToInt(Integer::intValue).min().getAsInt();
  double avg = result.stream().mapToInt(Integer::intValue).average().getAsDouble();
  stopWatch.stop();

  System.out.println("max = " + max);
  System.out.println("min = " + min);
  System.out.println("avg = " + avg);
  System.out.println("stopWatch.getTotalTimeSeconds() = " + stopWatch.getTotalTimeSeconds());

}
```

똑같이 100만명이 포켓몬 빵 챌린지에 동참 했습니다.

![image-20220320184500668](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/pokemon.assets/image-20220320184500668.png)

> 실행 결과

<u>**그 결과 159개의 스티커를 모두 모으려면 평균 897.8개의 빵을 사먹어야 했습니다. (뮤와 뮤츠가 다른 포켓몬과 같은 확률로 나오는 경우에)**</u>

### 뮤와 뮤츠 확률 조정

1주일간 총 150만개의 빵이 팔렸다는데 그중 뮤와 뮤츠를 각 250개씩 총 1000개 준비했다고 가정을 해 보겠습니다.

정확한 정보를 제공하지 않으니 추측치로 계산하지만, 실제 확률도 근사치일 것으로 추정됩니다.

계산 편의를 위해 155개의 스티커는 각각 1만개, 그외 스페셜 스티커 각 1000개씩 해서 155만4천개의 빵이 있다고 가정하겠습니다.

```java
Stack<Integer> stack = new Stack<>();
for (int i = 1; i <= 155; i++) {
  for (int j = 0; j < 10000; j++) {
    stack.add(i);
  }
}
for (int i = 156; i <= 159; i++) {
  for (int j = 0; j < 1000; j++) {
    stack.add(i);
  }
}
```

재고를 stack에 쌓는데, `1~155`번 스티커는 만개, `156~159`번 스티커는 각 천개씩을 넣고

```java
Collections.shuffle(stack);
```

Collections의 shuffle 메서드를 활용 해 섞어 줍니다. 이제 stack에서 하나씩 꺼내서 빵을 사면 되겠네요.

```java
@Test
public void testSpecial() {
  Stack<Integer> stack = new Stack<>();
  for (int i = 1; i <= 155; i++) {
    for (int j = 0; j < 10000; j++) {
      stack.add(i);
    }
  }
  for (int i = 156; i <= 159; i++) {
    for (int j = 0; j < 1000; j++) {
      stack.add(i);
    }
  }
  Assertions.assertThat(stack.size()).isEqualTo(1554000);
  Collections.shuffle(stack);

  final int PEOPLE = 100;
  List<Integer> result = new ArrayList<>();
  for (int i = 0; i < PEOPLE; i++) {
    Set<Integer> pokeDex = new HashSet<>();
    int cnt = 0;
    while (pokeDex.size() < 159) {
      try {
        pokeDex.add(stack.pop());
      } catch (EmptyStackException e) {
        System.out.println((i+1) + "번째 사람이 살 빵이 더이상 남아있지 않습니다.");
        throw e;
      }
      cnt++;
    }

    result.add(cnt);
  }

  System.out.println("max = " + result.stream().mapToInt(Integer::intValue).max().getAsInt());
  System.out.println("min = " + result.stream().mapToInt(Integer::intValue).min().getAsInt());
  System.out.println("avg = " + result.stream().mapToInt(Integer::intValue).average().getAsDouble());

}
```

100명의 포켓몬 트레이너들이 챌린지에 참여 했습니다. 그 결과

![image-20220320192831158](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/pokemon.assets/image-20220320192831158.png)

가장 운이 나빴던 사람은 무려 8317개의 빵을 구입하고 나서야 도감을 모두 채웠네요. 1500원씩 주고 구입하면 무려 12,475,500 원을 지불해야 합니다. 평균적으로는 3264개씩 구입했네요.

이제 PEOPLE의 수를 1000으로 늘려 확인 해 보면.

![image-20220320193311271](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/pokemon.assets/image-20220320193311271.png)

몇번을 해봤는데 보통 400 번대 후반에서 빵이 다 팔려서 스티커 모으는데 실패합니다. 

이번에는 예외처리를 해서 빵이 다 팔리고 나면 그때까지의 결과를 출력 하도록 변경합니다.

```java
  @Test
public void testSpecial() {
  Stack<Integer> stack = new Stack<>();
  for (int i = 1; i <= 155; i++) {
    for (int j = 0; j < 10000; j++) {
      stack.add(i);
    }
  }
  for (int i = 156; i <= 159; i++) {
    for (int j = 0; j < 1000; j++) {
      stack.add(i);
    }
  }
  Assertions.assertThat(stack.size()).isEqualTo(1554000);
  Collections.shuffle(stack);

  final int PEOPLE = 1000;
  List<Integer> result = new ArrayList<>();
  loop:for (int i = 0; i < PEOPLE; i++) {
    Set<Integer> pokeDex = new HashSet<>();
    int cnt = 0;
    while (pokeDex.size() < 159) {
      try {
        pokeDex.add(stack.pop());
      } catch (EmptyStackException e) {
        System.out.println((i+1) + "번째 사람이 살 빵이 더이상 남아있지 않습니다.");
        break loop;
      }
      cnt++;
    }

    result.add(cnt);
  }

  System.out.println("max = " + result.stream().mapToInt(Integer::intValue).max().getAsInt());
  System.out.println("min = " + result.stream().mapToInt(Integer::intValue).min().getAsInt());
  System.out.println("avg = " + result.stream().mapToInt(Integer::intValue).average().getAsDouble());

}
```

![image-20220320195608731](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/pokemon.assets/image-20220320195608731.png)

운이 좋지 않은 경우에는 최대 14382개의 포켓몬 빵을 구입해야만 159개의 스티커를 모은 사람도 있었네요.

평균적으로는 **3313.4 개**의 빵을 사야 다 모을 수 있었습니다.

## 결론

159개의 스티커를 다 모으려면 모든 스티커가 나올 확률이 같을 경우에는 평균 `897.8`개의 빵을, 보정된 확률 에서는 평균 `3313.4`개의 빵을 사야 했습니다. 개당 1500원씩 따지면 500만원이 듭니다.

500만원은 둘째 치고 3천개의 빵을 먹으려면 하루 3개씩 먹어도 3년이 걸리겠네요. 

이상입니다. 
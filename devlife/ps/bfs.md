## 무한깊이 그리고 너비우선탐색 BFS

## Intro

### DFS

DFS는 트리나 그래프에서 한 루트로 검색하다가 특정 상황에서 최대한 깊숙히 들어가서 확인 한 뒤, 다시 돌아가 다른 루트를 탐색하는 방식입니다.

미로찾기를 생각하면 쉬운데요, 한 방향으로 끝까지 들어갔다가 막다른 길에 다다르면 (트리의 바닥에 도착) 왔던 길을 돌아가서 다른 방향으로 갑니다. 이 일을 찾는 값이 나올 때까지 혹은 모든 트리를 순회 할 때 까지 반복합니다.
DFS의 가장 큰 약점은 깊이가 무한으로 이어지면 빠져나올 수 없다는 점 입니다. 미로를 가다보면 왔던길이 또 나타나는 그런 미로도 있습니다. 가끔 등산을 할때도 그런 길에 들어갔다가 왔던 길에 다시 도착하는 경험을 해본 분들이 있을텐데요, 이럴 경우는 BFS를 이용해 해결 해야 합니다.

### BFS

BFS는 너비우선탐색 Breath First Search의 약자입니다.
BFS는 큐를 이용해 구현합니다. 각 경우를 검사하며 발생하는 새로운 경우는 큐에 집어넣고, 검사한 원소는 큐에서 제거합니다. DFS로 풀 수 없는 문제들을 풀 수 있으며 최단경로를 찾아내는데 사용 할 수 있습니다.단점으로는, 공간복잡도가 지수스케일로 커지기 때문에 overflow를 염두 해 두고 가지를 잘 쳐줘야 합니다.

## 단어 변환

프로그래머스 "단어 변환" 문제를 통해 BFS를 학습해보겠습니다.

> 문제 설명
> 두 개의 단어 begin, target과 단어의 집합 words가 있습니다. 아래와 같은 규칙을 이용하여 begin에서 target으로 변환하는 가장 짧은 변환 과정을 찾으려고 합니다.
>
> - 한 번에 한 개의 알파벳만 바꿀 수 있습니다.
>
> - words에 있는 단어로만 변환할 수 있습니다.
>   예를 들어 begin이 "hit", target가 "cog", words가 ["hot","dot","dog","lot","log","cog"]라면 "hit" -> "hot" -> "dot" -> "dog" -> "cog"와 같이 4단계를 거쳐 변환할 수 있습니다.
>   두 개의 단어 begin, target과 단어의 집합 words가 매개변수로 주어질 때, 최소 몇 단계의 과정을 거쳐 begin을 target으로 변환할 수 있는지 return 하도록 solution 함수를 작성해주세요.
> - 제한사항
>   각 단어는 알파벳 소문자로만 이루어져 있습니다.각 단어의 길이는 3 이상 10 이하이며 모든 단어의 길이는 같습니다.words에는 3개 이상 50개 이하의 단어가 있으며 중복되는 단어는 없습니다.begin과 target은 같지 않습니다.변환할 수 없는 경우에는 0를 return 합니다.

### Queue

일단 BFS는 Queue로 구현한다고 했는데요,

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devlife/ps/bfs.assets/img-20220116222346915.png)

> 문제 해결을 위해 Queue 객체를 하나 생성하려고 했는데, 생성이 되지 않습니다.
>
>  그 이유는 Queue가 구현체가 아닌 인터페이스 이기 떄문입니다.

![?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FEaNmY%2FbtraKD1xk5m%2FIa2tHGK77gRrcpjg5ixcg0%2Fimg](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FEaNmY%2FbtraKD1xk5m%2FIa2tHGK77gRrcpjg5ixcg0%2Fimg.png)

> since 1.5. 2004년에 Generics, Static import, 향상된for문, Enumuration, Autoboxing 등과 함께 추가되었습니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devlife/ps/bfs.assets/img-20220116222346916.png)

>  Queue를 구현한 목록들을 쭉 살펴봅니다. 이중 가장 익숙한 LinkedList 를 사용해보겠습니다.

### 코드

```cpp
public Node(String word, int depth) {
  this.word = word;
  this.depth = depth;
}
```

첫번째로 Node class를 선언 합니다.

```php
public static boolean oneDiff(String str1, String str2){
  int diff = 0;
  final int length = str1.length();
  for(int i=0; i<length; i++) {
    if(str1.charAt(i) != str2.charAt(i)) {
      diff++;
    }
  }

  return diff == 1;
}
```

글자가 딱 1개만 다를 경우에만 변환 할 수 있기 때문에 1개가 다른지 체크하는 메서드를 생성해둡니다. str1과 str2는 항상 길이가 같기 때문에 길이에 대해서는 신경을 쓰지 않아도 됩니다.

```php
public static int solution(String begin, String target, String[] words) {
  final int n = words.length;

  Queue<Node> q = new LinkedList<>();

  boolean[] visit = new boolean[n];
  q.add(new Node(begin, 0));

  while(!q.isEmpty()) {
    Node cur = q.poll();
    if (cur.word.equals(target)) {
      return cur.depth;
    }

    for (int i=0; i<n; i++) {
      if (!visit[i] && oneDiff(cur.word, words[i])) {
        visit[i] = true;
        q.add(new Node(words[i], cur.depth + 1));
      }
    }
  }

  return 0;
}
```

이번에는 최소 변환 횟수를 반환하는 solution 함수 입니다. BFS가 구현 된 부분입니다.
Node 객체를 제네릭으로 하는 Queue를 생성하고, 처음으로 받은 문자열(begin)과 기본 depth 0을 갖는 Node를 추가합니다.
이후 queue를 하나씩 꺼내면서 방문하지 않은 words의 부분들을 방문하며 oneDiff가 true인 경우(변환 가능한 경우) q에 다음 depth로 추가 합니다. 그러다가 찾고자 하는 target 문자열과 일치하는 경우가 오면, 해당 depth를 반환하며 함수를 종료시킵니다.
또한 q를 전부 소진시킬 떄 까지 값을 찾지 못한 경우에는 0을 반환합니다.

## 마치며

DFS와 BFS가 처음에는 낯설 지 모르지만, 한번 익혀두면 정말 많은 문제들을 마법같이 해결 할 수 있는 훌륭한 알고리즘 이라고 생각합니다.

DFS와 BFS가 어느정도 이해가 되어 실습을 하고 싶다면,

코딩테스트 연습 - 여행경로:  https://programmers.co.kr/learn/courses/30/lessons/43164

위의 문제를 해결 해 보시길 추천드립니다. 프로그래머스에 있는 DFS/BFS 문제중에 개인적으로 가장 흥미있는 문제였으며,

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devlife/ps/bfs.assets/img-20220116222346920-2339426.png)

정답자도 네개의 문제중에는 가장 적은 문제입니다. 저는 BFS 를 공부하는 김에 BFS로 해결했는데, 정답을 맞추고 나서 다른 사람들이 풀이를 보니 대부분 DFS로 접근하셨더라고요. 어느 걸로든 해결 할 수 있으니 공부하고 싶은 내용으로 접근해보세요. 이상입니다.
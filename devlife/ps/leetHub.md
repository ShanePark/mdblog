# Leetcode) 소개 및 풀이 코드 Github에 자동 커밋방법

## Intro

개발공부를 시작 한 이후로 오랜 취미중 하나였던 온라인 게임을 그만 두었습니다. 사실 온라인 게임을 오래동안 해 왔던 이유는, 그 자체가 재밌다는 이유도 조금은 있었지만 그보다는 주로 무료한 시간을 달래기 위함이었습니다. 그러면서 게임이라는 가상 공간에서 모르는 사람들과 만나 협력하고 경쟁하여 승리를 따냈을 때의 그 달콤한 성취감에 중독되어서 

- 시간이 남는다.
- 무료하다.

이 두가지 조건이 만족될때면 어김없이 게임을 하곤 했었습니다.

그러다 한국에 돌아와 2020년 11월. 개발 공부를 시작 한 이후로 게임을 하는 첫번째 조건이었던 `시간이 남는다` 로 로직을 타는 경우가 전혀 없게 되었습니다. 우선순위큐에 꾸준히 `다음 학습 해야 할 것` 이라는 항목으로 꾸준히 다음 할 일이 쌓이고 있으며 취업을 한 이후로는 단순히 공부만 하는게 아니고 내가 해야 할 업무까지 함께 쌓이다 보니 게임을 하고 싶다는 생각도 그닥 들지 않고, 할 여유도 없습니다.

처음 개발을 공부 할 때 학습을 하는게 곧바로 성취감으로 이어지는건 쉽지 않기 때문에 아무래도 잘 모를때라서 성취감에 목말라

> 코딩 문제를 풀면 코딩 실력이 늘거다.

라고 무작정 생각하고 무작정 프로그래머스에서 코딩 테스트 문제를 풀곤 했었습니다. 

그러다보니 무료함을 달랠 수 있고, 온라인게임을 통해 얻곤 했던  달콤한 성취감을 다시금 느낄 수 있게 되어 나름의 취미 생활이 되었습니다. 얼마전까지 Weekly Challenge도 있었는데 정말 즐겁게 참여해 한주도 빠짐 없이 모두 풀이 했었습니다. 끝나고 나니 너무 아쉽더라고요.

> [Programmers Weekly Challenge 12주 후기](https://shanepark.tistory.com/268)

![image-20220116165636741](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devlife/ps/leetHub.assets/image-20220116165636741.webp)

하지만 프로그래머스에는 문제가 그리 많지는 않기 때문에, 이쯤 풀고 나니 이제 남은 문제들은 무작정 풀기에는 난이도가 벅차고 한문제 한문제에 너무 많은 시간이 들기 시작했습니다. 학습이나 도전이 아닌 취미로 풀만한 문제는 더이상 남아있지 않았습니다.

![image-20220116165915011](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devlife/ps/leetHub.assets/image-20220116165915011.webp)

그래서 새로운 문제를 접해볼겸 다른 사람들과 경쟁도 조금 해볼겸 프로그래머스의 챌린지에도 접수하곤 했었습니다.

- 처음으로 참가해 보았던 네이버 웹툰 개발 챌린지 에서는 코딩테스트 합격이라는 즐거운 경험도 해보고

  >  [2021 네이버 웹툰 개발 챌린지 후기](https://shanepark.tistory.com/168)

- JetBrains 월간 코드 챌린지에서는 리더보드를 제공 하기 때문에 얼마나 괴물같은 개발자들이 많은지, 내가 아직 멀었다는 사실도 깨달을 수 있었습니다.

> 4문제 이상 풀면 해피해킹 키보드나 JetBrains 굿즈등을 추첨을 통해 주기 때문에 조건 달성을 하고는 어떤 상품을 받게 될지 행복한 상상에 빠지기도 했었는데 아쉽게도 당첨이 되지는 못했습니다.

그러던 중 얼마전 LeetCode를 알게 된 이후로는 알고리즘 문제 풀이를 LeetCode를 통해서만 하기 시작했습니다.

## Leetcode

Leetcode는 전세계적으로 가장 잘 알려진 Online Judge Platform 중 하나 입니다. 문제의 양도 많고, 조금 둘러보니 프로그래머스에서 풀었던 문제들도 대부분이 여기에서 약간씩 변형해서 출제 되는 듯 했습니다. 1000개 이상의 문제가 있으며 18개 이상의 프로그래밍 언어를 사용 할 수 있습니다.

몇가지 장점이 있는데요.

### Challenge 시스템

![image-20220116171146940](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devlife/ps/leetHub.assets/image-20220116171146940.webp)

매일매일 그날에 풀어 보라고 문제를 하나씩 골라 주기 때문에 골라준 문제를 숙제처럼 하나씩 할 수 있습니다. 물론 시간이 너무 없거나 혹은 너무 어려운 문제를 만나면 이렇게 중간중간 빵꾸를 내고는 합니다. Solution을 보거나 다른 사람들의 풀이를 보면 다 채울수도 있겠지만 자존심이 허락하지 않아 스스로 문제를 풀어내기 전까지는 확인하지 않습니다. 힘들게 풀어내고 나서 다른 사람이 쉽고 명쾌하게 푼 코드를 보면 허무하기도 놀라기도 합니다.

매일매일 이렇게 문제를 풀면 릿코드 포인트를 주는데, 부지런히 모으면 아래의 상품으로 교환도 가능합니다.

![image-20220116210318905](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devlife/ps/leetHub.assets/image-20220116210318905.webp)

### 다양한 문제

![image-20220116172017208](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devlife/ps/leetHub.assets/image-20220116172017208.webp)

무려 2141 개의 문제가 있습니다.

![image-20220116172114354](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devlife/ps/leetHub.assets/image-20220116172114354.webp)

 Easy 541개 , Medium 1143개, Hard 457개의 문제가 있는데요. 제 체감으로는 Easy는 프로그래머스 1단계 2단계 사이. Medium은 프로그래머스 2단계~3단계 문제 정도 되지 않을까 싶습니다. Hard문제도 하나 풀어 봤는데 프로그래머스 기준 3단계의 쉬운 문제에 걸려 간신히 풀 수 있었지만 다른 문제들은 많이 어려워 엄두가 안났습니다.

그 외에도 문제별로 다른 유저들의 통과율이나 어떤 문제인지 분류를 해 주기 때문에 필요한 문제를 충분히 찾을 수 있습니다.

### 결과 분석

![image-20220116172521145](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devlife/ps/leetHub.assets/image-20220116172521145.webp)

프로그래머스에서 처럼 단순하게 문제를 풀이해 통과하고 만족하고 끝나는 게 아니라, Runtime과 Memory 사용량을 다른 유저들의 제출결과와 비교해 어느 정도 효율적인 코드인지를 알려 주기 때문에 이게 정말 자극됩니다. 문제를 풀고도 하위 5%에 속했다는 결과를 받아들면 어느 누가 만족하고 다음 문제로 넘어 갈 수 있을까요? 그래서 풀이 후에도 시간 복잡도를 줄이기 위해 계속 문제에 도전 하게 됩니다.

### 훌륭한 자체 IDE

![image-20220116173037846](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devlife/ps/leetHub.assets/image-20220116173037846.webp)

Theme 설정, 폰트사이즈, Vim 혹은 Emacs 키 바인딩 등등 뿐만 아니라 심지어 LeetCode Primium에 가입하면 코드 자동완성마저도 됩니다.

심지어 Programmers 에서 너무 불편했던게 import 구문을 다 입력 해 줘야 했던건데 여기에서는 신경 쓸 필요가 없이 알아서 해줍니다. 심지어 디버깅도 가능 합니다.

### 테스트 케이스

틀렸을때는 틀린 테스트 케이스를 바로 바로 제공해줍니다. 그렇기 때문에 시간 낭비를 할 필요 없이 바로바로 해당 테스트 케이스에서 왜 실패했는지를 분석 하고 새로운 코드를 작성 할 수 있어 효율적입니다.

이토록 장점이 많기 때문에 꼭 해보기를 추천합니다. 꾸준히 한다면 개발자 인생에 분명 큰 힘이 되어 줄거라고 생각합니다.

## LeetHub

> https://github.com/QasimWani/LeetHub

한문제 풀 때마다 Github에 코드를 저장 하고 하는데요. 아무래도 커밋 할 때 작성할 게 제법 많습니다.

- 문제 내용
- 작성한 코드
- 코드의 실행 결과

모든 걸 작성해서 Commit 하고 Push 하고 아무래도 동일한 작업을 매번 반복하게 되는데요. 그걸 해결하기 위해 LeetHub 플러그인이 있습니다.

### 설치

설치에 앞서서 Leetcode 전용 저장소를 Github에 하나 만들어 줍니다.

![image-20220116211037259](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devlife/ps/leetHub.assets/image-20220116211037259.webp)

> 저는 leetcode 라는 이름으로 생성 했습니다.

 ![image-20220116210859098](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devlife/ps/leetHub.assets/image-20220116210859098.webp)

> https://chrome.google.com/webstore/detail/leethub/aciombdipochlnkbpcbgdpjffcfdbggi?hl=en

위의 링크에 들어가 Add to Chrome 으로 추가 합니다.

![image-20220116210941446](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devlife/ps/leetHub.assets/image-20220116210941446.webp)

> 그러면 Extention 목록에 추가 된 것을 확인 할 수 있습니다.  Authenticate를 클릭하고

![image-20220116211116424](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devlife/ps/leetHub.assets/image-20220116211116424.webp)

> Authorize 해 줍니다.

![image-20220116211335557](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devlife/ps/leetHub.assets/image-20220116211335557.webp)

> 그리고 사용할 저장소를 연결 해줍니다.

![image-20220116211402762](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devlife/ps/leetHub.assets/image-20220116211402762.webp)

모든 준비가 끝났습니다.

### 코드 제출

코드를 한번 제출 해 보겠습니다. 그냥 평소 leetcode 할 때 처럼 Submit을 해 주면

![image-20220116211512801](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devlife/ps/leetHub.assets/image-20220116211512801.webp)

코드 제출 결과가 나온 후에 플러그인을 확인 해 보았습니다.

![image-20220116211612159](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devlife/ps/leetHub.assets/image-20220116211612159.webp)

> Problems Solved가 1로 늘어 났습니다.

Github 저장소에 들어가서 확인을 해 보면

![image-20220116212357355](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devlife/ps/leetHub.assets/image-20220116212357355.webp)

위에 보이는 것 처럼, 각각 문제에 해당하는 폴더를 생성 해서 작성한 코드와 해당 문제의 내용을 `.md`파일로 저장 해 알아서 커밋을 해 준 것이 보입니다.

### 마치며

한번 설정 해 두면 후에 신경 쓸 필요도 없이 눈에 보이지 않은 곳에서 알아서 문제와 코드를 모두 백업 해주고 런타임과 메모리 사용량의 백분위까지 커밋 메시지로 저장을 해 주기 때문에 정말 좋습니다. 
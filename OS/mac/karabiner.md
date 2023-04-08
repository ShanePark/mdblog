# Karabiner 설치해 맥북 한영키 설정하기

## Intro

맥북은 처음에 컨트롤키 + space 버튼이 한/영 키로 설정되어 있습니다.

너무 불편해서 많은 분들이 좌측 Caps lock 키를 한영키로 설정해서 사용하시는데요. 몇가지 단점이 있습니다.

1. 반응속도.

\- 반응속도가 느립니다. 한영키 전환을 하고 타자를 빠르게 치는 분들은 분명 불편함을 느끼게 됩니다. Capslock 키를 오래 누르는지, 잠깐 누르는지에 따라 한영키를 할 건지 아니면 Capslock 키를 정말 toggle 할건지를 컴퓨터가 결정하는데, 그 찰나의 순간이 짧지가 않아서 캡스락 누르자 마자 입력을 하면 한영키가 안먹히는 경우가 많습니다. 저 또한 이것 때문에 너무 불편했습니다.

2. 윈도우와의 호환

\- 맥북을 사용해도 가끔씩은 주변 사람들때문에 윈도우 컴퓨터를 사용하게 됩니다. 그게 아니더라도 맥북을 사용하면서 외부 키보드를 연결해서 사용 하게 될 때가 있는데요. 평소의 한/영 키와 위치가 다르면 너무 불편합니다. 오른손 엄지로 한영키를 누르는 습관을 그냥 쭉 가져가는게 차라리 낫습니다. "난 벌써 왼쪽 소지(새끼손가락) 으로 누르는거에 익숙해졌어!" 라고 하는 분들도 못해도 십수년을 한/영키와 살아왔는데 그 잠깐 익숙해졌다고 그걸 계속 고집하시는 것보다는 차라리 앞으로 길게 보고 한/영키 위치를 선택하는 것을 추천합니다.

그래서 제가 선택한 키보드 위치는 우측 Command 키 입니다. 

<img src="https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/karabiner.assets/img.png" height=330 width=750 alt=keyboard>

물론 다른 키가 편한 분들도 많으시겠지만. 제가 생각하기에는 가장 좋은 키 입니다.

## 설치

>  설치에 앞서 brew 가 설치되어 있지 않은 분은 아래 링크를 통해 Homebrew를 먼저 설치해주세요. 

뭐야? Homebrew 말은 많이 들어봤지만 귀찮아! 하시는 분들도 눈 꼭 감고 5분만 투자하면 지금 맥북 못해도 5년은 사용하실텐데 .. 일단 한번 깔고 나면 왜 이걸 여태 안깔았지 하는 마음이 들면서 주변에 추천 하게 되실 겁니다. 꼭 필수에요.

Homebrew 설치는 https://shanepark.tistory.com/45 를 참고해주세요.

Karabiner 설치부터 시작 하겠습니다.

```bash
brew search karabiner
```



![img](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/karabiner.assets/img-20230408091438589.png)



search 해보니 딱 하나 나오네요. 설치합니다.

```bash
brew install karabiner-elements
```

이렇게 입력 하면 됩니다. 예전에는 cask 설치할땐 무조건 `brew install --cask karabiner-elements` 로 입력 해야 했었는데, 그냥 brew 해도 잘 되네요. 혹시 cask 어쩌구 에러가 나는 분은 --cask 해서 설치 하시면 됩니다.

![img](https://blog.kakaocdn.net/dn/TB0nn/btq9e57SPNj/KOQuuX6bKmpRQqLm6Xkpv0/img.png)



Karabiner 설치에는 비밀번호가 필요합니다. 비밀번호를 입력 해 줍니다.



![img](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/karabiner.assets/img-20230408091438587.png)



금방 설치가 완료 되었습니다.



![img](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/karabiner.assets/img-20230408091438613.png)



`Command + Spacebar` spotlight 혹은 Alfred로 검색을 해서 Karabiner-Elements 를 실행 해 줍니다.



![img](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/karabiner.assets/img-20230408091438898.png)

>  못찾는 분은 이렇게 Applications 에 가도 있습니다.

![img](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/karabiner.assets/img-20230408091438685.png)



일단 바로 경고가 나타나는데요. 카라비너가 키입력을 가로채서 변환 해 주다 보니 민감해서 그렇습니다.

Open Security Preference를 누릅니다.



![img](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/karabiner.assets/img-20230408091438745.png)



좌측 하단의 자물쇠를 클릭해서 풀면,



![img](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/karabiner.assets/img-20230408091438705.png)



이렇게 체크가 가능한 상태로 변하게 됩니다.



![img](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/karabiner.assets/img-20230408091438636.png)



두개 모두 체크 해줍니다.



![img](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/karabiner.assets/img-20230408091438809.png)



Driver Alert가 뜰 수 도 있는데요, 이때는 Security/Privacy 에 들어가면



![img](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/karabiner.assets/img-20230408091438780-0912878.png)



이렇게 Karabiner-Virtual Device가 block 되어있는것이 보입니다. 좌측 하단 좌물쇠를 클릭해서 따줍니다.



![img](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/karabiner.assets/img-20230408091438783.png)



그러고 이제 Allow 해주면 됩니다.



![img](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/karabiner.assets/img-20230408091438904.png)



이제 키 설정을 할 수 있는 준비가 되었습니다. 

Add item 을 클릭합니다.



![img](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/karabiner.assets/img-20230408091438780.png)



저는 right_command 키를 f18 로 변경합니다.  왜인지는 모르겠지만 f20은 하려고 했는데 안되었습니다.

 

이제 설정 -  키보드 - Shortcuts - input source 순서로 이동합니다.



![img](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/karabiner.assets/img-20230408091438855.png)

![img](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/karabiner.assets/img-20230408091438842.png)

![img](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/karabiner.assets/img-20230408091438899.png)



이제 둘중에 하나는 체크를 해지하고, 나머지 하나를 우측 커맨드 키로 설정 해 주어야 합니다.



![img](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/karabiner.assets/img-20230408091438902.png)



저는 위에 것을 체크 해제 하고, 아래 단축키를 선택하고 우측 커맨드 키를 눌러 F18 로 변경 했습니다. 

잘 변경 했다면 이제는 우측 커맨드 키로 한영 키가 변경 됩니다 !  



수고하셨습니다.


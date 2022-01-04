Terminator

## Intro

기존 터미널 창 분할 불편

## Install

```zsh
sudo apt install terminator
```

![image-20220104160052289](/home/shane/Documents/git/markdownBlog/OS/linux/terminator.assets/image-20220104160052289.png)

> 설치는 금방 됩니다.

설치를 완료 하고 Terminator를 실행 해 봅니다.

![image-20220104160119308](/home/shane/Documents/git/markdownBlog/OS/linux/terminator.assets/image-20220104160119308.png)

![image-20220104160148761](/home/shane/Documents/git/markdownBlog/OS/linux/terminator.assets/image-20220104160148761.png)

폰트도 깨지고 디자인이 엉망이 되어 있지만 실행은 됩니다.

## 설정

적당히 까만 화면을 우클릭 하고 Preferences 버튼을 눌러 설정 화면에 진입합니다.

![image-20220104160404978](/home/shane/Documents/git/markdownBlog/OS/linux/terminator.assets/image-20220104160404978.png)

설정을 Global에서 바꾸도 되지만 그럴 경우에는 Profiles의 설정이 우선이기 때문에 Profiles에서도 Global 설정을 따르겠다고 변경을 한번 더 해줘야 합니다.

저는 Profiles 에서만 설정 하도록 하겠습니다.

### 폰트

### ![image-20220104161020630](/home/shane/Documents/git/markdownBlog/OS/linux/terminator.assets/image-20220104161020630.png)

> 일단 Font를 깨지지 않는 폰트로 변경 해 주어야 하는데요, 저는 D2 Coding Font를 즐겨 사용합니다.

### 투명도

![image-20220104161241936](/home/shane/Documents/git/markdownBlog/OS/linux/terminator.assets/image-20220104161241936.png)

> 투명도는 개인적으로 0.8 정도가 적당한 것 같습니다.
>
> 0에 가까울 수록 투명하고, 1에 가까울 수록 불투명 합니다.

### 설정파일

`.config/terminator/config` 에 있는 설정파일을 수정할 수도 있습니다.

```zsh
vi ~/.config/terminator/config
```

![image-20220104161546609](/home/shane/Documents/git/markdownBlog/OS/linux/terminator.assets/image-20220104161546609.png)

## 단축키

### 수직분할

> Ctrl + Shift + E

![image-20220104161915204](/home/shane/Documents/git/markdownBlog/OS/linux/terminator.assets/image-20220104161915204.png)

### 수평분할

> Ctrl + Shift + O

![image-20220104161934386](/home/shane/Documents/git/markdownBlog/OS/linux/terminator.assets/image-20220104161934386.png)

### 현재 창 닫기

> Ctrl + Shift + W

### 터미널 내 검색

> Ctrl + Shift + F

### 터미널 윈도우 이동

> Alt + 방향기

![peek](/home/shane/Documents/git/markdownBlog/OS/linux/terminator.assets/peek.gif)

### 터미널 크기 조절

> Ctrl + Shift + 방향키



![p2](/home/shane/Documents/git/markdownBlog/OS/linux/terminator.assets/p2.gif)

### 탭간 이동

> Ctrl + PageUp / PageDown

기본 Terminal에서 Alt + 방향키로 전환하던 것도 사용하고 싶다면 KeyBindings에 추가 하면 똑같이 사용 할 수 있습니다.

![image-20220104162721288](/home/shane/Documents/git/markdownBlog/OS/linux/terminator.assets/image-20220104162721288.png)
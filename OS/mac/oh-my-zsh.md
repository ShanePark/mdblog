# MacOS) oh-my-zsh 설치하기

## Intro

Mac의 기본 터미널이 원래 bash 였는데 zsh 로 바꼈다고 합니다. 

저는 Mac을 사용한지가 얼마 안되서 처음부터 zsh를 사용 했는데요. 덕분에 bash 기준으로 인터넷에 널리 퍼져있는 많은 명령어들이 한번에 입력되지 않아 처음에 꽤나 고생을 했습니다.

zsh를 더욱 편하게 사용해주는 oh-my-zsh 가 있다고 해서 이번에 설치 해 보았습니다.

git을 terminal 에서 활용해보려고 하는데, 유투브 강의에 나오는 환경들이 뭔가 달라서 보니 oh-my-zsh 셋팅이 된 환경이었습니다.

설치하기전에 ~/.zshrc 에 있는 텍스트 내용들을 백업해두시길 추천합니다. oh-my-zsh가 설치되면서 싹 밀려버리는 듯 합니다. 

```cpp
vi ~/.zshrc
```

저는 아래 2줄만 있어서 복사해두었습니다.

```cpp
source ~/.bash_profile
export PATH=/opt/homebrew/bin:$PATH
```

 

## 설치

 Terminal은 iterm2를 추천합니다. brew 명령어를 이용해 쉽게 설치 할 수 있습니다.

이제 oh-my-zsh 설치를 시작해보겠습니다.

```cpp
$ sh -c "$(curl -fsSL https://raw.github.com/ohmyzsh/ohmyzsh/master/tools/install.sh)"
```

터미널에 위의 명령어만 복사해서 붙여넣기하면 설치가 됩니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oh-my-zsh.assets/img-20211231175817487.png)

설치가 금새 완료되고 터미널이 알록달록하게 변한 것을 확인 할 수 있습니다. 지금 사용중인 git의 branch 이름도 나옵니다.

## 설정

### Theme

```cpp
vi ~/.zshrc
```

를 누르고 ~/.zshrc 설정을 들어가면 정말 많은게 추가되어 있습니다.

11번 라인 정도에 ZSH_THEME="robyrussell" 이라고 되어 있습니다. 이것을 agnoster로 변경해줍니다.

```cpp
ZSH_THEME="agnoster"
```

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oh-my-zsh.assets/img-20211231175817516.png)



이제 또 저장후 나옵니다 :wq 를 누르면 됩니다. 

이후 터미널을 껐다 키면, 폰트가 깨지는 현상을 확인 할 수 있습니다. 폰트 설정을 해줘야 하는데요, 코딩용 폰트를 추천합니다.

### Font

아래 링크에서 naver에서 배포한 D2 코딩 폰트를 다운받을 수 있습니다.

>  [github.com/naver/d2codingfont](https://github.com/naver/d2codingfont)



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oh-my-zsh.assets/img-20211231175817468.png)



여러가지 버전이 있는데 가장 최신인 1.3.2 버전을 다운 받겠습니다.



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oh-my-zsh.assets/img-20211231175817545.png)



여러개 폴더가 있는데 All에 있는



![img](https://blog.kakaocdn.net/dn/UO8ne/btq9e7EEBou/N6pwNfJNcpN2ZrQEgbKFm1/img.png)



해당 폰트를 더블클릭해 설치해 줍니다. Ligature와 일반 폰트 둘다 설치 해 줍니다. Ligature 폰트를 프로그래밍 할때 조금 사용 해 보았는데 처음엔 어색한데 제법 재밌습니다. 

이제 , 터미널에서 CMD 키와 , (쉼표) 키를 눌러서 환경설정에 들어갑니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oh-my-zsh.assets/img-20211231175817463.png)

> Profile - Text - Font 에서 D2Coding 폰트로 변경해줍니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oh-my-zsh.assets/img-20211231175817543.png)

터미널이 몰라보게 변한것을 확인할 수 있습니다.

### 사용자 이름

.zshrc를 조금 더 수정하면  사용자 이름 부분도 달라지게 할 수 있습니다.

```cpp
vi ~/.zshrc
```

로 편집으로 들어갑니다.

```cpp
prompt_context() {
  if [[ "$USER" != "$DEFAULT_USER" || -n "$SSH_CLIENT" ]]; then
    prompt_segment black default "%(!.%{%F{yellow}%}.)$USER"
  fi
}
```

위의 내용을 마지막에 추가해줍니다. 사용자 이름이 아에 나오지 않게 하고 싶으면 prompt_context(){}로 하면 됩니다.

:wq로 저장하고 설정 후에는  터미널을 한번 껐다 다시 켜줘야 설정이 적용됩니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oh-my-zsh.assets/img.png)

>  Terminal 이 더 깔끔해졌습니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oh-my-zsh.assets/img-20211231175817465.png)

다만 zshrc 파일이 강제로 변경되면서 기존에 되던 명령어도 안되고 파이썬 버전도 기본 mac에 설치된 버전이 잡히는 휴유증이 있습니다.

zshrc 에 필요한 명령어를 다시 추가해줍니다.

```cpp
vi ~/.zshrc
```

저는 아래 두줄만 있었으므로 맨 아래에 두 줄을 다시 추가해 주었습니다.

```cpp
source ~/.bash_profile
export PATH=/opt/homebrew/bin:$PATH
```

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oh-my-zsh.assets/img-20211231175817466.png)

명령어들이 다시 정상적으로 잘 작동되는것을 확인 할 수 있습니다! 

수고하셨습니다.
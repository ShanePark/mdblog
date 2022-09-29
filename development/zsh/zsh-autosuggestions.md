# [Zsh] Oh My Zsh 자동완성 플러그인 추가 및 단축키 변경하기

## Intro

### Shell

**Unix Shell**은 MacOS나 리눅스 같은 유닉스 계열의 OS에서 Command  Line user Interface 를 제공해주는 Command Line Interpreter 입니다. 운영체제에서 쉘 스크립트를 사용하여 시스템의 실행을 제어 하기 위해 사용하는데요. 보통은 내장 Terminal 이나 iTerm 같은 터미널 에뮬레이터를 사용하지만, 보통 서버에서는 Secure Shell (SSH) 을 통해 이루어지기도 합니다.

Shell은 대표적으로 Bourne Shell(sh), Bourne Again Shell(bash), C Shell(csh), Z  Shell(zsh) 등이 있는데요,  그중 zsh는 sh 를 기반으로 Bash, ksh, tcsh 등의 기능을 포함하여 여러가지 개선 및 확장을 이루어 냈습니다.

### Zsh

특히나 2019년 MacOS Catalina 에서 zsh가 기본 쉘로 지정되고, Kali Linux에서도 2020.4 릴리즈 부터 Default Shell이 되며 사용자가 급격하게 늘어 나고 있는데요, 저도 개인적으로는 맥북을, 회사에서는 우분투로 작업을 하며 zsh에 oh-my-zsh를 얹어서 애용 하고 있습니다.

아직 oh-my-zsh 를 설치하지 않은 분들은 아래의 글을 참고해서 설치를 진행 해 주세요.

- MacOS : [oh-my-zsh 설치하기](https://shanepark.tistory.com/60)

- Ubuntu : [Ubuntu에 oh-my-zsh 설치](https://shanepark.tistory.com/248)

## zsh-autosuggestions

zsh 에는 여러가지 플러그인을 얹어 사용 할 수 있는데요, 그 중 제가 좋아하는건 하이라이팅을 해 주는 zsh-syntax-highlighting 과 자동완성을 제안 해 주는 zsh-autosuggestions 입니다. 

![image-20220929170420670](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/zsh/zsh-autosuggestions.assets/img1.png)

그 중 이번 글에서는 자동완성 플러그인을 설치 하고, 자동 완성 단축키를 변경하는 방법에 대해서 다루어보려고 합니다. 자동완성은 이전부터 큰 문제없이 사용 해 왔었는데요, 얼마전부터 Github Autopilot을 사용하면서 불편함을 겪었습니다. 깃헙의 오토파일럿은 `Tab` 키를 눌러 자동완성 문장을 채택하는데요, zsh의 자동완성은 오른쪽 방향키를 입력해서 채택하기 때문에 키보드 입력의 동선도 썩 좋지 않고 Tab을 누르는 실수도 잦았습니다.

### 설치

**Oh my Zsh를 사용하는 경우**

1. 아래의 깃 저장소를 `$ZSH_CUSTOM/plugins` 경로에 clone 해 줍니다. 기본 경로는 `~/.oh-my-zsh/custom/plugins` 입니다.

```bash
git clone https://github.com/zsh-users/zsh-autosuggestions ${ZSH_CUSTOM:-~/.oh-my-zsh/custom}/plugins/zsh-autosuggestions
```

2. 그러고 나서 `~/.zshrc` 파일에 플러그인을 추가 해 줍니다.

```bash
plugins=( 
    # other plugins...
    zsh-autosuggestions
)
```

![image-20220929170936275](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/zsh/zsh-autosuggestions.assets/img2.png)

**Oh-My-Zsh를 사용하지 않는 경우**

1. zsh-autosuggestons를 어딘가에 클론 해 줍니다. 아래의 예에서는 `~/.zsh/zsh-autosuggestions` 경로에 클론 하고 있습니다.

```bash
git clone https://github.com/zsh-users/zsh-autosuggestions ~/.zsh/zsh-autosuggestions
```

2. `~/.zshrc` 파일에 아래의 내용을 추가 해 줍니다.

```bash
source ~/.zsh/zsh-autosuggestions/zsh-autosuggestions.zsh
```

### 완료

이걸로 설정은 끝입니다. 이제 터미널을 새로 실행하면 zsh의 자동 완성 기능이 활성화 됩니다.

키보드 입력을 할 때, history를 기반으로 zsh가 사용자가 입력할 가능성이 높은 커맨드를 제안 해 줍니다.

![image-20220929171119279](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/zsh/zsh-autosuggestions.assets/img3.png)

> h 만 입력 했더니, 최근에 입력했던 history 명령어 및 옵션을 제안해주는 모습

이 때 자동완성 제안을 수락 하려면 우측 화살표키를 입력 하면 됩니다.

## 자동완성 단축키 변경

키보드로 입력을 하다 보면 방향키에 손이 가면 동선이 굉장히 꼬이게 되며 효율성이 떨어집니다. vim 에디터에서는 hjkl 로 이동을 하고, 심지어 해피해킹 키보드에서는 방향키마저 제거해버렸는데요.

이번에는 자동 완성 단축키를 변경 해 보도록 하겠습니다. 키 바인딩에 대해서는 repository에 잘 안내되어 있습니다.

![image-20220929171420580](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/zsh/zsh-autosuggestions.assets/img4.png)

> https://github.com/zsh-users/zsh-autosuggestions

### 변경

그러면 이제 `~/.zshrc` 파일을 열고 적당한 위치에 bindkey를 추가 해 줍니다.

![image-20220929171551835](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/zsh/zsh-autosuggestions.assets/img5.png)

```bash
bindkey '\t' autosuggest-accept
```

파일을 저장 한 후에는 터미널을 새로 켜거나 아래의 명령어를 입력 해서 설정을 새로 불러옵니다.

```bash
source ~/.zshrc
```

이렇게 변경 한 후에는 탭 키로 자동 완성이 가능합니다.

### 단점

그런데 tab 키를 바인딩 해 버리니 생각지 못했던 문제가 생겼습니다.

![image-20220929171757546](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/zsh/zsh-autosuggestions.assets/img6.png)

cd 를 입력 하고 tab 키로 폴더 이동할 폴더 목록을 보는 등, 실제 tab 키가 기존에 지원해주는 기능들을 사용 할 수가 없습니다. 사실 자동완성 못지않게 기존 tab키의 기능도 중요합니다.

tab키는 안되겠습니다.  보통 IDE에서 코드어시스트를 받는데 자주 사용하는 `Ctrl + Space` 키 조합으로 변경 하겠습니다.

```bash
bindkey '^ ' autosuggest-accept
```

이제 Tab 키의 원래 기능도 사용 하면서, 자동 완성은 Ctrl + Space 키로 사용 할 수 있게 되었습니다.

사실 기본적으로 `Ctrl+E` 키가 Move to End Of File 로 지정 되어 있기 때문에, 따로 설정 없이 `Ctrl + E` 를 입력해도 자동 완성 제안 수용이 가능 합니다.



이상입니다.

​	

**References**

- https://en.wikipedia.org/wiki/Z_shell
- https://github.com/zsh-users/zsh-autosuggestions/blob/master/INSTALL.md
- https://github.com/zsh-users/zsh-autosuggestions/issues/532
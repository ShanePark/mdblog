# Mac) 화면 분할을 위한 Rectangle

## Intro

MacOS에서 화면분할용 프로그램중에 가장 유명한 프로그램은 단연 Magnet 입니다. 종종 $0.99 세일도 한다고 해서 저도 구입을 하려고 Black Friday도 기다려보고, Boxing데이도 기다렸는데 세일을 할 생각을 안하더라고요...

<img src="https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/rectangle.assets/image-20220216193647633.png" width=750 height=500 alt=first>

> 혹시 내가 이걸 샀는데 Apple이 MacOS에서 정식 기능으로 제공하면 어떡하지? 하는 맘에 정가에 못사고 있는 Magnet

아쉬운 대로 지금까지는 오픈소스중에 가장 괜찮다는 Spectacle을 꾸준히 사용 해 왔습니다.

<img src="https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/rectangle.assets/image-20220216193746834.png" width=750 height=450 alt=img2>

나름 필요한 기능도 다 있고 속도도 빠릿빠릿 해서 단축키만 잘 설정 해 주면 괜찮게 사용 할 수 있습니다. Windows나 Linux에서 사용하던 창 분할 들에 비하면 부족한 느낌이 없지않긴 하지만 그래도 꾸준히 사용 해 왔는데요 Spectacle의 큰 단점이 있습니다.

![image-20220216193942012](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/rectangle.assets/image-20220216193942012.png)

> 최근 commit이 2년이 조금 넘었습니다.

바로 더이상 운영되지 않고 있다는 건데요. 그래서 조금씩 불편함이 있는 부분에 대해 개선을 전혀 기대 할 수가 없습니다.

그러던 중 Spectacle을 Base로 만들었다는 Rectangle을 사용 해 보았습니다. Spectacle Repository 에서도 Rectangle을 대안으로 추천하고 있습니다.

## Installation

Rectangle을 바로 설치 해 보도록 하겠습니다. Spectacle과는 확실히 다른게 제가 확인한 시점 기준으로 최근 커밋이 불과 몇시간 전 이더라고요.

Github repository는 아래에 남기도록 하겠습니다.

> https://github.com/rxhanson/Rectangle

설치는 brew를 통해 매우 간단하게 할 수 있습니다.

```bash
brew install rectangle
# 혹은
brew install --cask rectangle
```

![image-20220216204445620](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/rectangle.assets/image-20220216204445620.png)

금방 설치가 완료 됩니다. 실행해 보겠습니다.

![image-20220216194341310](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/rectangle.assets/image-20220216194341310.png)

## Execution

![image-20220216194403452](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/rectangle.assets/image-20220216194403452.png)

> 처음 실행을 하면 보안 알림이 한번 뜨는데, Open을 눌러 줍니다.

![image-20220216194420990](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/rectangle.assets/image-20220216194420990.png)

> 이번에는 window position을 컨트롤 하기 위해 권한을 부여해줘야 한다고 합니다.
>
> 아래 보이는 Check Rectangle.app 을 클릭 할 수 없다면, Open System Preferences에 들어가 설정을 해 줘야합니다.

![image-20220216204549674](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/rectangle.assets/image-20220216204549674.png)

> 좌측 하단의 좌물쇠를 풀어주고

![image-20220216204607554](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/rectangle.assets/image-20220216204607554.png)

> Rectangle을 체크 해 줍니다.

![image-20220216204619896](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/rectangle.assets/image-20220216204619896.png)

> 그럼 바로 준비가 됩니다.

단축키를 설정하는데, 추천하는 설정과 Spectacle 설정이 있습니다. Spectacle의 단축키가 굉장히 불편했기 때문에 Recommened 를 클릭하겠습니다.

![image-20220216204657990](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/rectangle.assets/image-20220216204657990.png)

단축키는 위와 같습니다. Spectacle 에서는 Option+Command 키를 주로 이용하는데요, Rectangle 에서는 Ctrl + Option 키를 사용합니다. Ctrl + Super를 사용하는 우분투 ShellTile의 창 분할과 키가 비슷해서 마음에 듭니다.

특히 Ctrl + Option + Backspace 로 호출하는 `Restore` 기능이 참 마음에 듭니다. 분할하다가 업무가 끝났을 때 혹은 실수로 이동시켰을 때도 간단하게 바로 복구할 수 있습니다.

![image-20220216205108115](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/rectangle.assets/image-20220216205108115.png)

설정에 보면 Snap windows by dragging이 체크 되어 있기 때문에, 간편하게 드래그를 통해 한쪽 끝에 창을 이동하면, 간편하게 분할도 가능합니다.

화려한 부가 기능이 있는건 아니지만, 기본적인 창 분할에 굉장히 충실 하고 있으며 유지 보수도 꾸준히 되고 있는 오픈 소스 소프트웨어이기 때문에 아주 마음에 듭니다.

추천합니다. 
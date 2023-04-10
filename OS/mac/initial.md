# 개발자를 위한 맥북 초기 설정

## 서론

저는 현재 M2 맥북에어 24GB 모델을 사용 중입니다. 크게 상관은 없지만 인텔 기반보다는 애플 실리콘 기반 맥북에 초점을 맞췄습니다.

처음부터 개발환경을 세팅하려고 하면 윈도우건 맥이건 리눅스건 간에 할일이 정말 많습니다. 가끔 업무 중에 급하게 개발환경을 꾸려야 할 일이 있으면 대충 필수적인 것만 대강 올려두고 업무를 하기도 하지만, 처음 컴퓨터를 샀을 때는 개발환경 세팅마저 즐겁습니다.

항상 TimeMachine 백업을 해두고는 있지만, 주변에 맥북을 새로 산 친구들 세팅을 여러 차례 도와주다 보니 이럴 거면 아예 처음부터 끝까지 한번 정리해두는 게 좋겠다 싶어 글로 작성해 보았습니다. 사실 블로그를 시작하게 된 것도 비슷한 이유였습니다.

제가 몇 번의 시행착오를 거쳐 얻은 개인적인 경험이 담겨있으며, 새로운 정보가 있을 때마다 한 달에 두어 번씩 본 포스팅을 업데이트하고 있습니다. 특히 `필수` 라고 언급한 목록들은 다른 많은 개발자분도 사용하시는 검증된 프로그램이며 거기에 제가 불편함을 해소하며 깨달은 유용한 팁들을 얹었으니 꼭 체크해보시면 좋겠습니다. 

이미 맥북에 익숙한 분들도 제가 소개하는 소프트웨어 중에 생소한 게 있다면 한 번쯤 사용해보세요. 추천하고 싶은 소프트웨어가 있다면 댓글로 달아주시면 많은 분께 도움이 되겠습니다.

## 필수 설치

> 필수 설치에 있는 목록들은 그 순서가 유의미합니다. 위에서부터 설치하시길 권장합니다.

### Homebrew

<img src="https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/beer-machine-alcohol-brewery-159291-20211101231216301.webp" width="700" height="500" alt="first">

<a href="https://shanepark.tistory.com/45" target="_blank">Mac 에 brew 설치하기</a>

위의 글을 쭉 따라가면 어렵지 않게 설치할 수 있습니다. 저도 간만에 해보는데 처음 상태에서 글만 쭉 따라가니 어려움 없이 설치 할 수 있었습니다. 사실상 brew만 설치하면 맥북 모든 설치는 정말 간편하게 할 수 있습니다. 설치 못하는 프로그램이 별로 없습니다.

### Alfred

> Spotlight 를 대신해 여러가지 편의 기능을 제공합니다. 익숙해지면 정말 유용한 기능이 많습니다.

![image-20211101231458490](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20211101231458490.webp)

https://shanepark.tistory.com/164

이왕 brew 설치 포스팅에 alfred 설치 하는 방법까지 같이 이어서 하니, alfred 까지 설치하시길 추천합니다. 

### Karabiner

> 한/영키를 윈도우에서 처럼 익숙한 키로 사용 하게 변경 할 수 있습니다. 
>
> Caps lock 키를 사용해 한/영 키를 변경하는건 정말 불편합니다.

[![Black and Silver Macbook Pro](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/pexels-photo-7662049.webp)](https://images.pexels.com/photos/7662049/pexels-photo-7662049.webp?cs=srgb&dl=pexels-szabó-viktor-7662049.jpg&fm=jpg)

https://shanepark.tistory.com/165

한영키때문에 정말 1초라도 빨리 설치하고 싶었습니다.  

어쩌다보니 Alfred에 밀렸지만 사실 brew 설치하자 마자 바로 설치해야 할 친구입니다.

### SDKMAN

SDKMAN은 MacOS나 Linux 같은 Unix 기반 시스템에서 SDK들을 관리 해주는 프로그램 입니다. 손쉬운 CLI 환경과 API를 통해 여러가지 SDK들을 설치, 전환, 삭제 할 수 있으며 가용 가능한 SDK들을 한눈에 확인 할 수도 있습니다. 

JDK, ant, Gradle, Maven 등등 자바 기반의 개발 도구를 간편하게 관리 하기 위해 설치하는데요, 여러가지 자바 버전을 설치 해 두고 전환하며 사용 할 필요성을 느끼는 분들은 당연히 설치하시겠지만 필요성을 느끼지 못한다면 꼭 지금 시점에서 설치하실 필요는 없습니다.

다만 추후에 분명 필요성을 느낄 때가 있을테니, 이왕 하는거 지금 설치하는 것도 나쁘진 않겠죠.

설치는 아래 명령어로 끝납니다.

```bash
curl -s "https://get.sdkman.io" | bash
```

![image-20220324232846059](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20220324232846059.webp)

```bash
source "/Users/shane/.sdkman/bin/sdkman-init.sh"
```

위의 커맨드를 입력 하라고 하니까 시키는 대로 합니다.

그리고 설치가 잘 되었는지 확인을 위해 `sdk version`을 입력 해 봅니다.

![image-20220324232923619](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20220324232923619.webp)

설치가 잘 되었네요.

###  java

> java 개발자뿐만 아니라, 대부분의 개발자에게 필요 할 거에요.

[![Text](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/pexels-photo-6190327.webp)](https://www.pexels.com/photo/creative-dark-internet-designer-6190327/)

#### SDKMAN 를 이용하지 않는 경우

개인적으로는 SDKMAN으로 자바 버전 관리하는 것을 추천합니다. 그래도 SDKMAN을 꼭 설치해야하는건 아닙니다.

저는 openjdk를 설치하겠습니다. oracle java를 설치 하고 싶은 분은 oracle 공식 홈페이지에서 다운 받아서 하시면 됩니다. 

```bash
brew tap AdoptOpenJDK/openjdk
```

일단 brew해서 tap 을 먼저 해줘야 합니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056849.webp)

이제 `brew search openjdk` 를 입력 하면 , 많은 버전의 openjdk를 보여줍니다. 

여기에서는 14를 설치하겠습니다.

```bash
brew install adoptopenjdk14
```

​	

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056887.webp)

왠만하면 설치가 될 텐데, 저는 m1 맥북인데 아직 Rosetta 2 를 설치하지 않아서 에러가 발생했습니다. 혹시 Rosetta 2가 설치되어 있지 않다면 에러 메시지를 보시고 그대로 설치 하셔도 되고, 위에 있는 Rosetta 2 설치하기 를 참고해주세요.

​			

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img.webp)

자바도 간단하게 설치 완료했습니다.

설치된 모든 자바 버전을 확인 할려면 Terminal을 켜고

```bash
/usr/libexec/java_home -V
```

를 입력 하면 설치된 모든 자바의 버전과 경로가 표시됩니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-16390941970761.webp)

> 11버전을 설치한 경우

#### SDKMAN 이용

위에서  SDKMAN을 설치했다면 여러가지 버전을 설치 하고 변경하며 사용 할 수도 있습니다.

아래의 명령어를 입력 하면

```bash
sdk list java
```

![image-20220324233217175](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20220324233217175.webp)

> Corretto(AWS), Dragonwell(Alibaba), Microsoft, Oracle, Temurin(Eclipse) 등 많은 Vendor들의 자바목록이 보입니다. 맨 우측의 Identifier가 중요한데요, 설치하고자 하는 버전의 Identifider를 복사 해 둡니다.

q를 눌러 나온 뒤, 

```bash
sdk install java 17.0.2-tem
```

으로 저는 Temurin JDK 17을 설치하겠습니다. 이전에는 AdoptOpenJDK 였는데 Temurin으로 리 브랜딩 되었습니다 

![image-20220324233424318](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20220324233424318.webp)

> 자바 설치도 완료 되었고, 17 버전이 기본 버전으로 설정 되었습니다.

여러 가지 버전을 설치했다면 자바 버전을 변경할때는 간단하게 use(이번 shell에서만 변경) 혹은 default(기본값 변경)을 통해 자바 버전을 변경 할 수 있습니다.

기본 자바 버전을 17.0.2-tem 으로 변경하려면 아래의 명령어를 입력 하면 됩니다.

```bash
sdk default java 17.0.2-tem
```

### Rosetta 2

> Apple Silicon 의 맥북을 사용하시는 분만 해당됩니다.

```xml
sudo softwareupdate --install-rosetta
```

m1 맥북 사용하신다면 Rosetta가 필수입니다. 처음 m1 맥북 샀을때는 Apple Silicon 최적화된 프로그램이 거의 없다 싶이 해서 컴퓨터 뭐 설치하면서 금방 설치했었는데, 왠만한 게 다 Apple Silicon 을 지원하는 지금에서는 아직 설치하지 않았다는걸 깨닫는데 한참 걸렸네요.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056818.webp)

동의하면 A 누르고 엔터 치라는데 . 당연히 동의합니다.

​	

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056803.webp)

그닥 오래 걸리지 않습니다.	

### iterm & zsh

> mac의 기본 terminal도 나쁘지 않지만 iterm은 더 좋습니다.

![image-20211101233257878](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20211101233257878.webp)

아래의 명령어만 입력하면 설치는 손쉽게 됩니다.

```xml
brew install iterm2	
```

몇가지 설정을 하겠지만, 저는 개인적으로 창 분할 설정을 제일 먼저 합니다. Linux를 쓸 때 Terminator 에서 창분할을 해서 사용했기 때문에 같은 단축키로 설정해서 사용합니다.

Preferences -> Keys -> Key Bindings에 `Split Vertically`와 `Split Horizontally ` 를 설정 해 주시면 되는데요 안타깝게도 한/영 전환이 된상태에서는 단축키를 인식하지 못하기 때문에 한/영 상태 둘 다 등록 해 주어야 불편함 없이 사용 할 수 있습니다.

![image-20220319101034561](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20220319101034561.webp)

> 위와 같이 설정 해 주시면 Terminator와 동일한 키로 창 분할을 하실 수 있습니다.

사실 iterm2 설치 자체는 굉장히 간단한데요.  oh-my-zsh 설치 하는게 조금 까다롭습니다. 

oh-my-zsh 설치는 아래의 링크를 참고해주세요. MacOS는 Catalina 부터 기본 shell이 zsh으로 변경되었습니다 

https://shanepark.tistory.com/60?category=1182535 

> 위의 글을 보고 따라하시면 어렵지 않게 설치 하실 수 있습니다.

### Rectangle

### ![mac512pts1x](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/mac512pts1x.webp)

윈도우에서는 기본으로 지원하는 창 분할 기능을 MacOS에서는 왜 안해주는지는 정말 의문입니다.

이때문에 Magnet이라는 훌륭한 소프트웨어가 나와있기는 하지만, 약 10,000원의 비용을 창분할에 선뜻 사용하기에는 망설여지는게 사실입니다. 그 대용으로 오랫동안 많은 분들이 Spectacle을 써왔지만 유지보수가 이루어지지 않고 있다는 단점이 있었는데요.

Spectacle을 베이스로한 Rectangle이라는 훌륭한 오픈소스 소프트웨어가 나왔고, 꾸준히 개선되고 있습니다. 저도 사용해보니 Spectacle을 사용했을 때의 부족함이 모두 메워져서 아주 만족하며 사용 하고 있습니다.

아래의 명령으로 설치 할 수 있습니다.

```bash
brew install --cask rectangle
```

Rectangle에 대한 자세한 내용을 확인 하고 싶다면 아래의 포스팅을 읽어주세요.

> [Mac) 화면 분할을 위한 Rectangle](https://shanepark.tistory.com/333)

### JetBrains ToolBox 

![image-20211210212629372](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20211210212629372.webp)

IntelliJ IDEA등 JetBrains사의 제품을 사용한다면, ToolBox를 사용해서 설치하는 것이 좋습니다. 간편하게 개발 툴들을 설치 할 수 있을 뿐만 아니라, 자동으로 업데이트 해주며 IDE와 함께 플러그인도 업데이트 할 수 있습니다. 심지어 롤백 및 다운그레이드도 지원해주기 때문에 단독으로 소프트웨어를 설치하는 것 보다 좋습니다.

![image-20211210212332631](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20211210212332631.webp)

> brew로 검색 해보니 jetbrains-toolbox라는 이름으로 있습니다.

아래의 명령어를 입력해 설치합니다.

```bash
brew install jetbrains-toolbox
```

혹은 brew에 익숙하지 않다면 https://www.jetbrains.com/ko-kr/toolbox-app/ 에서 다운받으실 수 있습니다.

m1을 비롯한 Apple Silicon 맥북 사용자라면, mac OS Apple Sillicon 을 선택해서 다운 받으세요.

![image-20211210213104689](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20211210213104689.webp)

![image-20211210212532158](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20211210212532158.webp)

금방 설치가 되었습니다. 실행해줍니다.

![image-20211210212811081](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20211210212811081.webp)

> 다양한 JetBrains 사의 제품들이 나옵니다. 

![image-20211210212829153](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20211210212829153.webp)

> 저는 IntelliJ IDEA Ultimate을 설치 합니다.

![image-20211210212933262](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20211210212933262.webp)

> 원한다면 여러가지 이전 버전중 골라서 설치 할 수도 있습니다.

그닥 어려울게 없습니다. 다만 툴박스는 창이 아니고 팝업이기 때문에 리사이즈나 이동이 안되어 정말 불편합니다. 

![image-20211210214449797](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20211210214449797.webp)

> https://toolbox-support.jetbrains.com/hc/en-us/community/posts/360000094690-Why-is-the-Toolbox-not-a-real-window-

저는 처음에 버그인줄 알고 재설치도 해봤었는데, 원래그런 거더라고요. 다른사람들도 불많이 많은데 몇년째 바뀌진 않고 있습니다.

## 필수 설정

### 배터리 표시 변경

![image-20211101231719474](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20211101231719474.webp)

https://shanepark.tistory.com/166

> 배터리 표시를 % 숫자로 나오게 변경하는 건데, 수치로 확인 되는게 아무래도 좋습니다.

### Dock & Menu bar 숨기기

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056834.webp)

Dock & Menu Bar 설정에 가면 맨 아래 체크박스로 메뉴바를 숨길 수 있습니다.  Dock 에서 아래서 세번째 버튼인 Automatically hide and show the Dock 을 체크해서 Dock 도 숨길 수 있습니다. 

저는 개인적으로 둘 다 숨겨서 사용하는게 모니터 활용 범위가 넓어져서 꼭 설정합니다.

>  Dock 체크박스중 맨 아래 있는 Show recent applications in Dock에 체크 되어있는것도 체크 해지 하는것을 강력 추천합니다. 자주 사용하는 앱은 어차피 바로가기를 등록하고 사용하니, 최근 사용 어플에 뜨는건 아이러니하게도 잘 안쓰는 앱 입니다.

### Dock 빨리띄우기

사실 처음부터 맥북을 사용했고, 다른 Dock을 사용해본 적이 없다면 MacOS에서의 독 시스템에 크게 불편함을 느끼지 않을 수 있지만

회사에서는 우분투로 개발을 하다 보니, 맥북에서의 Dock 뜨는 속도가 제게는 **너무 느리게** 느껴지더라고요.

 평소에도 독이 좀 더 빠릿빠릿 하게 올라왔으면 했던 분들도 이 설정을 변경 해 주시면 됩니다.

#### 설정 확인하기

```bash
defaults read com.apple.dock "autohide-delay"
```

설정된 `autohide-delay`를 확인 할 수 있습니다.

#### 딜레이 없이 바로 띄우기

0으로 설정 해 주면 바로 뜹니다.

```bash
defaults write com.apple.dock autohide-delay -float 0
killall Dock
```

#### 보다 자연스럽게

저는 조금 더 자연스럽게 하기 위해 0.2로 설정 해 두고 사용하고 있습니다.

```bash
defaults write com.apple.dock autohide-delay -float 0.2
killall Dock
```

#### 기본 설정으로 복구

기본설정은 0.5초로 되어 있습니다.

```bash
defaults delete com.apple.dock autohide-delay
killall Dock
```

좀 더 자세한 정보는 https://macos-defaults.com/dock/autohide-delay.html#requirements 에 자세히 나와 있습니다.

### 손가락 3개 드래그

> 터치패드를 쓸 때 불편한게 드래그가 어렵다는 건데, 해당 설정을 통해 편하게 할 수 있습니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101232235913.webp)

설정 -> 손쉬운 사용 (Accessibility) -> Pointer Control -> Trackpad Options 로 이동합니다.

​	

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056847.webp)

Enable dragging 을 클릭 하고 세 손가락으로 드래그 하기를 선택 한 뒤 OK를 눌러 저장합니다.

이제  세손가락으로 뭐든 드래그 할 수 있습니다.



### 키보드 백틱 설정

>  한글 키보드 상태에서 ` 키 눌렀을때 ₩ 입력 되지 않도록 설정

markdown으로 글을 작성하면 백틱 키입력을 많이 하게 되는데요. 한글상태에서는 ₩로 입력이 되어서 참 불편합니다.

그럴때는 `/Library` 에 keybinding을 시켜서 해결 할 수 있습니다.

1) cd ~/Library 입력해 Library 폴더로 이동 후 mkdir KeyBindings 입력해 KeyBindings 폴더를 만들어줍니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056827-5775856.webp)

​		

2) 아래와 같이 입력해 vi 에디터로 DefaultKeyBindg.dict 파일을 편집합니다.

```xml
vi ~/Library/KeyBindings/DefaultkeyBinding.dict
```

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056839.webp) 

에디터가 켜지면 다음과 같이 입력하고 :wq로 저장 합니다.

```
{
    "₩" = ("insertText:", "`");
}
```

​	

이제 사용중이던 프로그램을 재시작 한번 해주면, 한글 상태에서 ₩ 를 입력 해도 정상적으로 ` 가 입력 됩니다.



### Apple Watch

> 애플워치가 있다면 애플워치로 맥북 잠금해제 옵션을 설정합니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056857.webp)

Preference - Security & Privacy 에서 Use your Apple Watch to unlock 체크를 설정 해 두면 됩니다. Require password 설정도 기본 5분으로 되어있지만 보안을 위해서는 immediately 로 하는게 좋습니다. 애플 워치가 있으니 번거로울 일도 없어 즉시로 변경 해 두었습니다.



### Finder 설정

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056866.webp)

Finder 설정도 순정 상태에서 사용하기에는 조금 불편함이 있습니다. Finder를 켠 상태에서 View - > Show Path Bar ( 저는 이미 선택해서 Hide Path Bar 로 이름이 바뀌었습니다) 를 선택합니다.

​	

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056875.webp)

그러면 맨 아래 보이는 것 처럼 Path Bar가 생겨서 전체 경로를 한눈에 쉽게 보고 더블클릭으로 Navigate도 할 수 있습니다.

​	

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056865.webp)

개인적으로 키보드 설정에서 New Terminal at Folder도 켜두는걸 추천합니다. 저는 New iTerm2 를 대신 사용해서 체크가 해제 되어 있습니다.



### Safari 개발자 모드 표시

Chrome에서는 기본적으로 F12 를 누르거나 Command + Shift + C 가 먹히지만, Safari는 설정을 해 주기 전까지 개발자 모드가 안됩니다.

​	

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056901.webp)

메뉴바에서 Safari -> Preferences를 들어가거나 사파리 켠 상태에서 Command 키 + 쉼표 키를 입력합니다.

​	

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056891.webp)

이후 설정 윈도우가 뜨면 Advanced 에 들어가서 맨 아래 있는 Show Develop menu in menu bar를 체크 해 주시면 됩니다.

​	

설정을 켠 후에는 개발자 모드를 켜려면 Option + Command + i 키를 입력하면 됩니다. Chrome에서와 같이 Command + Shift + C 키입력도 작동 합니다. 개발자 모드는 크롬에서의 그것과 거의 같습니다.

### Minimize 단축키 비활성화

`Cmd + N `키를 누르려다 실수로 `Cmd+M`을 입력한다면 바로 창이 최소화 됩니다.

이게 한두번정도 이러면 그냥 창을 다시 띄우고 말겠는데, 너무 자주 창을 내려버리면 스트레스를 받게 되고 일의 효율성도 떨어지게 되죠. 

사실 `Cmd + H` 키도 창을 숨기는 단축키인데, 그 차이점이 조금 있거든요. 다시 창을 복원 할 때 `Cmd+M` 쪽이 좀 더 번거로워요.

사실 비활성화 하는 방법은 없고, Minimize 단축키를 다른 키로 변경 해 두고 사용합니다.

System Preferences > Keyboard > Shortcuts 메뉴에 들어가면 맨 아래에 App Shortcuts 가 있거든요.

그걸 누르고 `+` 버튼을 눌러서,  

![image-20220402184640432](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20220402184640432.webp)

>  Minimize를 추가 해 주고 누르기 어려운 단축 키로 변경해 줍니다.

Minimize만 하면, Chrome 이나 인텔리제이 등에서는 여전히 커맨드 M 키가 먹히거든요. 몇몇 어플리케이션들을 위해 Minmise를 입력하고 같은 작업을 반복 해 줍니다. 스펠링 맨 끝에서 2번째가 z 와 s 로 달라요.

![image-20220402184538909](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20220402184538909.webp)

> Minimize 와 Minimise가 모두 등록된 상태

이렇게 하면 이제 실수로 커맨드 M 키를 눌러도 창이 내려갈 걱정이 없습니다.

![image-20220402185325955](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20220402185325955.webp)

> 참고로 인텔리제이 사용자라면 KeyMap에도 Minimize가 설정 되어 있기 때문에 이 단축키도 비활성화 해주셔야 합니다.

### 마우스 관련 설정

맥북에서는 솔직히 트랙패드만 써도 충분 하긴 하지만, 마우스에 익숙한 분들은 마우스가 꼭 필요 하기도 합니다. 저도 트랙패드만으로 반년 정도 사용 해 오다가, 그래도 마우스를 완전 대체 할 수는 없다는 한계를 느껴서 요즘에는 두개 다 사용하고 있습니다. 그래도 마우스에 커맨드키들 몇개 넣어서 사용하니 트랙패드를 거의 안쓰게 되긴 하더라고요.

1. 마우스 가속 끄기

![](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20211206211559027.webp)

그런데 맥북만 쓸땐 상관 없지만 리눅스나 윈도우 컴퓨터와 번갈아가며 사용하다 보면 마우스 가속 기능때문에 참 어색한데, 이게 아무리 지나도 적응이 되지 않습니다. 그래서 마우스 가속을 꺼 보았는데요. 이게 상당히 괜찮아서 게임도 가능 할 정도더라고요.

맥북에서 마우스를 쓰고는 싶은데, 감도가 너무 어색하고 불편하다는 분들은 아래의 링크를 확인 해서 마우스 가속 기능을 끄고 사용해주세요.

스틸시리즈의 유틸이긴 한데, 저는 로지텍 마우스를 사용 하지만 아무 문제 없이 사용하고 있습니다. 마우스 제조사와 무관합니다.

> https://shanepark.tistory.com/297

혹은, 해당 스틸시리즈의 툴이 Apple Silicon을 아직도 지원 하지 않아서 저는 요즘 **LinearMouse** 라는 오픈소스를 소프트웨어를 대신 사용하고 있는데요. 만족스러워서 더욱 추천합니다.

![image-20220812222307221](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20220812222307221.webp)

> https://linearmouse.org
>
> https://github.com/linearmouse/linearmouse

Homebrew로 간편하게 설치해서 사용하시면 됩니다.

```bash
brew install --cask linearmouse
```

![image-20220812222423052](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20220812222423052.webp)

> 포인터 가속을 끄는 것 뿐만 아니라, 마우스 스크롤이 반대로 되는 문제도 해결 해 줍니다.

2. Mac Mouse Fix

![image-20220812222546132](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20220812222546132.webp)

> https://github.com/noah-nuebling/mac-mouse-fix

원래 한동안은 로지텍의 마우스 소프트웨어를 사용 했었는데 툭하면 설정이 초기화 되는 현상 때문에 결국 세번째에 인내심에 한계를 느껴 다른 툴을 찾았습니다. Mac Mouse Fix는 마우스 가운데 클릭을 활용해 스크롤 넘기기 및 미션 컨트롤이 가능합니다! 그리고 뒤로가기 앞으로 가기 버튼을 설정 해 주는것 뿐만 아니라 스크롤시 터치패드로 하는 것 처럼 부드럽게 만들어줍니다. Invert direction은 마우스 휠 방향을 반대로 해주는건데, 위의 LinearMouse와 함께 사용한다면 둘 중 하나에서만 켜주면 됩니다.

![image-20220812223046023](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20220812223046023.webp)

> 위와 같이 여러가지 설정이 가능 합니다.

이 역시 brew로 간편하게 설치 하시면 됩니다.

```bash
brew install --cask mac-mouse-fix
```

저에게는 꽤나 도움이 되어서 mac mousefix 3.0을 `$1.99 / onetime` 비용을 내고 사용중입니다.

## 개발 관련 소프트웨어

개발에 관련된 소프트웨어들을 한번 나열해 보았습니다. 당연히 모두 설치할 필요는 없고, 본인에게 필요한것만 설치하시면 됩니다.

### Docker 

docker에 대한 설명은 크게 필요 없을 것 같습니다. 한참동안 rosetta에서 조차 구동이 불가능 했는데 어느 순간 그걸 건너 뛰고 바로 native로 지원을 해 주더라고요. 간단한 예제와 함께 설치 방법을 작성 해 보았습니다. 아래 링크로 따로 준비했습니다.

> <a href="https://shanepark.tistory.com/194" target="_blank">MacOS ) m1 맥북 docker 설치하기 + 가상환경에 postgreSQL 띄워 보기</a>

### SourceTree

- 근 1년동안 배터리 문제로 쓸 수 없는 프로그램 이었는데 Apple Silicon Native 지원과 함께 부활 했습니다.
- 새로운 버전은 4.1.6 이며 Apple Native 지원에 관한 글은 [SourceTree Apple Silicon 지원 소식](https://shanepark.tistory.com/343) 를 참고해주세요.
- brew로 설치합니다. `brew info sourcetree` 해보니 최신 버전이 잘 등록 되어 있습니다. 

```bash
brew install sourcetree
```

### Slack

사실 말이 따로 필요 없는 협업툴이죠.  brew로 설치하면 됩니다.

```bash
brew install slack
```

### Github Desktop

-  SourceTree가 좀 더 파워풀 하긴 하지만 대안으로 Github Desktop도 있습니다.
-  SourceTree 의 배터리 문제 때문에 대안으로 어쩔 수 없이 선택했던 소프트웨어지만, 1년동안 정말 많은 업데이트와 기능추가를 거쳐 지금은 굉장히 쓸만한 Git GUI가 되었습니다.  둘다 설치해도 좋고 둘중 하나를 선택해도 좋습니다.

```bash
brew install github
```

### Google Chrome

- 사파리만으로도 좋긴 하지만 가끔 크롬이 필요 할 때가 있습니다.

```bash
brew install google-chrome
```

### KEKA

- keka는 MacOS에서 가장 많이 쓰이는 압축 & 압축 해제 프로그램 입니다.

```bash
brew install keka
```

### Microsoft remote desktop

windows 컴퓨터를 원격 조정 할 일이 있으면 다운 받아주세요.

- microsoft remote desktop을 다운 받습니다. 이거 정말 좋습니다. Teamviewer 를 거들떠도 안보게 됩니다.

```bash
brew install microsoft-remote-desktop
```

### Oracle Database

m1 맥북에서 오라클 데이터베이스를 사용하려면 꽤나 골치가 아픕니다. 아마 가장 큰 골칫덩이 였던 것 같은데요, 특히나 많은 국비학원에서 오라클 데이터베이스를 위주로 수업을 진행 하다 보니, 처음에 컴퓨터에 익숙하지 않은 분들은 많이 좌절을 하게 됩니다. 그래도 해결방법은 다 있으니 차근 차근 따라하시면 될 거에요.

애플 실리콘 환경에서 오라클 데이터 베이스 사용에 대한 전반적인 내용은 아래의 글을 확인 해주세요.

> [[MacOS] M1 맥북 도커로 ORACLE DB 실행하기](https://shanepark.tistory.com/400)

아니면 서버는 필요 없고 SQL Developer만 있으면 된다는 분들은 아래의 글을 참고 해 주세요.

>  <a href="https://shanepark.tistory.com/87" target="_blank">MacOS) m1 맥북 Oracle SQL Developer 사용하기</a>

### Postman

- postman 을 설치합니다. 이제 Apple Silicon을 지원하네요.

```bash
brew install postman
```

### Sequal Pro

> MYSQL 혹은 MariaDB 클라이언트로 괜찮습니다.

- sequal pro를 설치합니다. 예전에 일반 버전 받았다가 m1 맥북에서 작동하지 않아서 그 후로 nightly 버전만 다운 받습니다. 정식 버전은 나중에 Apple Silicon 을 정식 지원할 때 받을 생각입니다.

```bash
brew install homebrew/cask-versions/sequel-pro-nightly
```

### Spring Tool Suite

- STS (Spring Tool Suite) 를 설치합니다. Eclipse 입니다.

```bash
brew install springtoolsuite
```

### Visual Studio Code

- 장르 불문 모든 프로그래머들의 메모장. vscode 를 설치합니다.

```bash
brew install visual-studio-code
```

## 개발 외

### Spotify

-  spotify 구독하신다면 설치하세요.

```bash
brew install spotify
```

> 혹은 저는 개인적으로 https://download.scdn.co/SpotifyBetaARM64.dmg 에서 Apple Silicon용 arm 64로 나온 베타 버전을 다운 받아 봤는데요. 
>
> 기존에 Rosetta로 실행되던 Spotify가 커널패닉 현상이 심했기 때문에 베타라도 더 안정적입니다. 아마 금방 정식버전이 배포되어서 지금 글을 읽으시는 시점에는 그냥 brew로 설치해도 native로 설치될겁니다.

### IINA

MacOS 에서는 가장 유명한 동영상 플레이어 입니다.

```bash
brew install iina
```

개인적으로 요즘에는 회사에서 리눅스를 메인으로 쓰다보니 VLC를 설치해서 사용하고 있는데 맥에서도 iina 보다는 VLC의 사용 비중이 늘었습니다. 그래도 보편적으로는 IINA가 널리 사용되는걸로 알고 있습니다.

### MonitorControl

개인적으로 제가 정말 좋아하는 소프트웨어 중 하나입니다.

모니터에 따로 연결하지 않고 맥북만 사용한다면 손쉽게 밝기 조정을 하실 수 있지만, 모니터에 연결해 사용중이라면 보통은 밝기 조절이 되지 않습니다. 물론 모니터에 있는 버튼을 눌러서 밝기를 수동으로 올릴 수는 있겠지만 참 번거롭습니다.

특히 밤에 어두운 환경에서 컴퓨터를 하고 있는데 `예를 들면 몰컴`, 다크모드가 안되는 페이지라도 띄우는 날엔 너무 밝아서 눈이 부담스럽습니다. 이럴 때 손쉽게 밝기를 조절 할 수 있는 소프트웨어 입니다.

brew로 쉽게 설치 할 수 있습니다.

```bash
brew install --cask monitorcontrol
```

![image-20230317222421612](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/initial.assets/image-20230317222421612.webp)

설치하시면, 위에 보이는 것 처럼 메뉴바에서 사용이 가능합니다. 놀라운건 여러개의 모니터를 연결 한 경우에도 각각의 모니터 밝기를 다르게 설정 할 수 있다는 겁니다.

기본적으로 기존에 맥북에서 사용하던것과 같은 `F2` 밝기 조정 단축키를 사용하면 되는데요 

![image-20230317222533094](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/initial.assets/image-20230317222533094.webp)

> Screen to control 에서 Depens on mouse pointer position 으로 설정해두면 마우스 포인터가 위치한 모니터의 밝기를 조절하게 됩니다.

거의 아무것도 안보일 만큼 어둡게도 설정이 가능하기 때문에 정말 좋습니다.

여러개의 모니터를 연결해두었는데 하나의 모니터로만 영상을 보고 있을때, 다른 모니터를 연결 해제하거나 끄기는 번거롭고 거슬려고 끄거는 싶다면 키보드 입력으로 간단하게 밝기만 최소로 줄이면 됩니다.

### Stats

![Stats](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/initial.assets/68747470733a2f2f7365726869792e73332e65752d63656e7472616c2d312e616d617a6f6e6177732e636f6d2f4769746875625f7265706f2f73746174732f706f7075707325334676322e332e322e706e673f7633.webp)

> https://github.com/exelban/stats

손쉽게 실시간으로 메모리나 CPU, Network 사용량 등을 메뉴바에서 모니터링 할 수 있는 소프트웨어 입니다.

아래의 명령어로 설치합니다.

```bash
brew install --cask stats
```

예전에 M1 맥북에어 8GB 짜리를 사용 할 때에 리소스 관리가 필요해서 설치 했었는데요, 꽤나 유용해서 24GB M2 맥북에어로 업그레이드 한 이후에도 계속 사용하고 있습니다.

꽤나 디테일한 설정이 가능하니 설치해서 사용해보시면서 본인에게 알맞은 셋팅을 찾아보세요

### Display Link

M1 혹은 M2 맥북에어를 사용한다면 외장 모니터를 1개밖에 사용하지 못하는데요, 정말 불편합니다.

사실 애플실리콘으로 넘어오며 맥북 에어만되어도 개발하기에 성능은 충분하게 되었는데, 모니터연결때문에 더 무겁고 비싼 맥북프로를 구매하기에는 고민이 좀 됩니다. 저에게는 휴대성이 가장 중요한 요소중 하나라서요.

이럴때에는 디스플레이링크를 지원하는 제품을 구입 하시고 [디스플레이 매니저 소프트웨어](https://www.synaptics.com/products/displaylink-graphics/downloads/macos)를 다운로드 하시면 됩니다.

기존에는 벨킨의 `INC002` 제품을 2년정도 사용했었는데요, 전반적으로 만족스럽고, 직접연결 모니터1개 + 독에 연결모니터 2개를 하면 외장 모니터 3개 까지 연결이 가능하기는 하지만, 디스플레이링크로 연결한 모니터의 최대 해상도가 FHD밖에 안되는 치명적인 단점이 있습니다. 그래서 최근에는 `INC007` 제품을 회사동료에게 중고로 구입해서 사용중인데 DP/HDMI 포트 모두 지원하고 외장 모니터도 3개까지 연결이 가능하며, 전부 4K 연결이 가능하기 때문에 정말 좋습니다. 

디스플레이 연결을 소프트웨어를 통해 가상으로 변환해주는 원리이기 때문에 부하가 크거나 마우스 움직임이 끊기지는 않을까 걱정이 많았었는데 정말 매끄럽게 잘 연결이 됩니다. 

![image-20230317224842982](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/initial.assets/image-20230317224842982.webp)

>  다만 4K 이상의 고해상도의 동영상을 시청할 때에는 위에 보이는 것 처럼 CPU 자원을 엄청나게 소모하는것을 감안은 하셔야 합니다. 그래도 CPU 온도가 65도 이상으로 올라가지도 않고 이상태에서 4K 영상을 하나 더 트는것도 가볍게 소화해내니 딱히 상관은 없습니다. 굳이 따지자면 고해상도 영상 편집작업쯤 되면 약간 무리가 오기 시작하지 않을까 싶긴 한데 그런 작업을 자주 한다면 맥북 프로를 구입하는 편이 좋겠습니다.

 디스플레이 링크 지원하는 장비가 비용은 좀 있는 편인데 맥북 에어 사용자라면 어쩔 수 없이 구입해야 합니다. 이 제품 구입까지 고려하는 단계에서는 내가 정말 프로가 아닌 에어를 선택할 타당한 이유가 있는가에 대해 충분한 고민을 해보셔야 합니다.

### Typora

![image-20220319095900973](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20220319095900973.webp)

수많은 markdown editor들이 있지만 제가 개인적으로 가장 좋아하는건 Typora 입니다. 2021년 말에 정식 버전을 릴리즈 하며 $14.99 의 비용이 책정되기는 했지만 여전히 매력적인 소프트웨어 입니다. 제가 작성하는 모든 블로그의 글들도 Typora로 작성하고 있습니다.

혹시 비용이 부담스러우신 분들은 지난 베타 버전을 사용하면 무료로 사용 할 수 있기 때문에 아래의 링크에서 `Beta 지속 방법` 부분을 확인 해서 설치하시면 됩니다. Typora 공식 사이트에서 제공해주는 Beta버전이기 때문에 문제될건 전혀 없지만 그래도 사용해보고 마음에 든다면 비용을 지불하고 사용하면 됩니다. 정식 버전에서도 15일간의 Free Trial 기간을 충분히 제공합니다.

> [Typora 정식 버전 오픈 소식](https://shanepark.tistory.com/287)

Typora를 Shell 이나 Cmd에서 바로 사용 하고 싶다면

```bash
alias typora="open -a typora"
```

위의 alias를 `~/.zshrc`에 추가 해 주시면 됩니다. 혹은 md 파일이 없을 때 즉시 생성하고 싶다면

```bash
alias typora="/Applications/Typora.app/Contents/MacOS/Typora"
```

위와 같이 등록 해 주시면 됩니다.

### Paintbrush

윈도우에서는 그림판이 정말 다용도로 사용되는데 맥북에서는 그만한 기본 소프트웨어가 없습니다. Preview가 그런 포지션이기는 한데 정말 그림판 같은 소프트웨어는 아닙니다.

저는 paintbrush를 사용하고 있습니다. 

![image-20230317225922431](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/initial.assets/image-20230317225922431.webp)

아래 명령어로 설치 합니다.

```bash
brew search paintbrush
```

그림판처럼 조악하고 엉성한 느낌이 나는게 딱 제가 생각하던 느낌이라서 종종 낙서할 때 사용하고 있습니다.

### qBittorrent

토렌트 클라이언트도 여러가지가 있는데 개인적으로 이것 저것 사용해 본 결과는 qbittorrent가 가장 마음에 들었습니다.

사실 요즘에는 토렌트 쓸일이 크게 줄어서 거의 켤일이 없긴 합니다. CLI 환경이 편하다면 aria2를 사용하는 것도 좋습니다.

```bash
brew install qbittorrent
```

### 프린터

가끔 맥북을 지원하지 않는 프린터들이 있어서 곤란한 경우가 있는데요, 컴퓨터를 바꿨다는 이유로 프린터기를 새로 구입하기엔 아깝습니다. 그럴 때에는 호환되는 드라이버를 이용하면 문제를 해결 할 수도 있는데요..

그 예로 저는 삼성 sl-j1660 프린터가 집에 있는데, MacOS를 정식으로 지원하지 않습니다. 그래서 여러가지 해결책을 찾아 본 끝에 결국 사용 할 수 있었는데요, 다른 프린터들도 아마 아마 비슷하게 사용 할 수 있을테니 한번 참고 해 보세요.

> <a href="https://shanepark.tistory.com/116" target="_blank">삼성 sl-j1660 프린터 m1 맥북에서 사용하기</a>

## 마치며

저도 많은 분과 마찬가지로 어려서부터 오랫동안 Windows를 사용해 왔으며, 지금은 회사에서는 Linux로 그 외 개인적으로는 맥북을 사용해 개발하고 있습니다.

여러 운영체제를 함께 사용해보니 제 개인적 의견으로는 MacOS가 개발에서는 가장 괜찮다고 생각합니다. Linux가 장점이 많기는 하지만 엔드투엔드 사용자 입장에서 특별한 고민 없이 편리하게 개발에만 집중하기에는 맥북만 한 게 없는 것 같습니다. 빌드 퀄리티도 훌륭하고요.

미국에서는 맥북을 보급한 회사가 비교적 직원들이 만족도와 충성도가 높으며 이직률이 낮은 것으로 조사되었다고 합니다.	

이상으로 초기 설정 글을 마칩니다. 추가로 필요한 게 있다면 계속해서 해당 포스팅에 추가하겠습니다.

맥북으로 즐거운 개발 하세요!
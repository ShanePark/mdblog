# 개발자를 위한 Mac 초기 설정 하기	

## 서론

드디어 저의 m1 맥북에어가 수리를 마치고 집으로 돌아왔습니다. 구매한지 5개월밖에 안됐는데 얼마나 혹사를 했는지 배터리에 이상이 생겼었습니다. 다행히도 무상으로 교체를 받게 되어 지금부터 처음부터 다시 셋팅을 해 보려고 합니다. TimeMachine 백업을 해 두긴 했었는데, 주변에 맥북을 새로 산 친구들 셋팅을 몇번 해주다 보니 한번 아에 처음부터 끝까지 글로 정리를 쉽게 해두는게 좋겠다 싶어 글을 작성합니다.

제가 맥북을 사용하며 여러 번의 시행 착오를 거쳐 얻은 나름의 노하우이며, 새로운 정보가 있을 때마다 최소 한달에 두어번씩은 본 포스팅을 업데이트 하고 있습니다. 특히 `필수` 라고 언급한 목록들은 다른 대부분의 개발자 분들도 이미 사용하고 계시며, 거기에 제가 불편함을 해결하며 깨달은 유용한 팁들을 얹었으니 꼭 한번씩 체크 해보시면 좋겠습니다.



## 필수 설치

> 필수 설치에 있는 목록들은 그 순서가 유의미 합니다. 위에서 부터 설치하시길 권장합니다.

### Homebrew

[![Stainless Steel Beer Dispenser](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/beer-machine-alcohol-brewery-159291-20211101231216301.jpeg)](https://images.pexels.com/photos/159291/beer-machine-alcohol-brewery-159291.jpeg?cs=srgb&dl=pexels-pixabay-159291.jpg&fm=jpg)

<a href="https://shanepark.tistory.com/45" target="_blank">Mac 에 brew 설치하기</a>

위의 글을 쭉 따라가면 어렵지 않게 설치할 수 있습니다. 저도 간만에 해보는데 처음 상태에서 글만 쭉 따라가니 어려움 없이 설치 할 수 있었습니다. 사실상 brew만 설치하면 맥북 모든 설치는 정말 간편하게 할 수 있습니다. 설치 못하는 프로그램이 별로 없습니다.



### Alfred

> Spotlight 를 대신해 여러가지 편의 기능을 제공합니다. 익숙해지면 정말 유용한 기능이 많습니다.

![image-20211101231458490](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20211101231458490.png)

https://shanepark.tistory.com/164

이왕 brew 설치 포스팅에 alfred 설치 하는 방법까지 같이 이어서 하니, alfred 까지 설치하시길 추천합니다. 



### Karabiner

> 한/영키를 윈도우에서 처럼 익숙한 키로 사용 하게 변경 할 수 있습니다. 
>
> Caps lock 키를 사용해 한/영 키를 변경하는건 정말 불편합니다.

[![Black and Silver Macbook Pro](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/pexels-photo-7662049.jpeg)](https://images.pexels.com/photos/7662049/pexels-photo-7662049.jpeg?cs=srgb&dl=pexels-szabó-viktor-7662049.jpg&fm=jpg)

https://shanepark.tistory.com/165

한영키때문에 정말 1초라도 빨리 설치하고 싶었습니다.  

어쩌다보니 Alfred에 밀렸지만 사실 brew 설치하자 마자 바로 설치해야 할 친구입니다.



###  java

> java 개발자뿐만 아니라, 대부분의 개발자에게 필요 할 거에요.

[![Text](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/pexels-photo-6190327.jpeg)](https://www.pexels.com/photo/creative-dark-internet-designer-6190327/)

저는 openjdk를 설치하겠습니다. oracle java를 설치 하고 싶은 분은 oracle 공식 홈페이지에서 다운 받아서 하시면 됩니다. 

```bash
brew tap AdoptOpenJDK/openjdk
```

일단 brew해서 tap 을 먼저 해줘야 합니다.

​	

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056849.png)

이제 `brew search openjdk` 를 입력 하면 , 많은 버전의 openjdk를 보여줍니다. 

​	

8, 11, 14 버전이 많이 쓰이는데. 개인적으로는 11 버전이나 14 버전을 추천합니다. 특히 8 버전은 SQL Developer 등을 사용할 때 문제가 많습니다. 저는 14를 설치하겠습니다. 

```zsh
brew install adoptopenjdk14
```

​	

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056887.png)

왠만하면 설치가 될 텐데, 저는 m1 맥북인데 아직 Rosetta 2 를 설치하지 않아서 에러가 발생했습니다. 혹시 Rosetta 2가 설치되어 있지 않다면 에러 메시지를 보시고 그대로 설치 하셔도 되고, 위에 있는 Rosetta 2 설치하기 를 참고해주세요.

​			

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img.png)

자바도 간단하게 설치 완료했습니다.

설치된 모든 자바 버전을 확인 할려면 Terminal을 켜고

```zsh
/usr/libexec/java_home -V
```

를 입력 하면 설치된 모든 자바의 버전과 경로가 표시됩니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-16390941970761.png)



### Rosetta 2

> Apple Silicon 의 맥북을 사용하시는 분만 해당됩니다.

```xml
sudo softwareupdate --install-rosetta
```

m1 맥북 사용하신다면 Rosetta가 필수입니다. 처음 m1 맥북 샀을때는 Apple Silicon 최적화된 프로그램이 거의 없다 싶이 해서 컴퓨터 뭐 설치하면서 금방 설치했었는데, 왠만한 게 다 Apple Silicon 을 지원하는 지금에서는 아직 설치하지 않았다는걸 깨닫는데 한참 걸렸네요.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056818.png)

동의하면 A 누르고 엔터 치라는데 . 당연히 동의합니다.

​	

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056803.png)

그닥 오래 걸리지 않습니다.	



### iterm & zsh

> mac의 기본 terminal도 나쁘지 않지만 iterm은 더 좋습니다.

![image-20211101233257878](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20211101233257878.png)

아래의 명령어만 입력하면 설치는 손쉽게 됩니다.

```xml
brew install iterm2	
```

사실 iterm2 설치 자체는 굉장히 간단한데요.  oh-my-zsh 설치 하는게 조금 까다롭습니다. 

oh-my-zsh 설치는 아래의 링크를 참고해주세요. MacOS는 Catalina 부터 기본 shell이 zsh으로 변경되었습니다.

https://shanepark.tistory.com/60?category=1182535 

> 위의 글을 보고 따라하시면 어렵지 않게 설치 하실 수 있습니다.

### JetBrains ToolBox 설치

![image-20211210212629372](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20211210212629372.png)

IntelliJ IDEA등 JetBrains사의 제품을 사용한다면, ToolBox를 사용해서 설치하는 것이 좋습니다. 간편하게 개발 툴들을 설치 할 수 있을 뿐만 아니라, 자동으로 업데이트 해주며 IDE와 함께 플러그인도 업데이트 할 수 있습니다. 심지어 롤백 및 다운그레이드도 지원해주기 때문에 단독으로 소프트웨어를 설치하는 것 보다 좋습니다.

![image-20211210212332631](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20211210212332631.png)

> brew로 검색 해보니 jetbrains-toolbox라는 이름으로 있습니다.

아래의 명령어를 입력해 설치합니다.

```zsh
brew install jetbrains-toolbox
```

혹은 brew에 익숙하지 않다면 https://www.jetbrains.com/ko-kr/toolbox-app/ 에서 다운받으실 수 있습니다.

m1을 비롯한 Apple Silicon 맥북 사용자라면, mac OS Apple Sillicon 을 선택해서 다운 받으세요.

![image-20211210213104689](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20211210213104689.png)

![image-20211210212532158](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20211210212532158.png)

금방 설치가 되었습니다. 실행해줍니다.

![image-20211210212811081](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20211210212811081.png)

> 다양한 JetBrains 사의 제품들이 나옵니다. 

![image-20211210212829153](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20211210212829153.png)

> 저는 IntelliJ IDEA Ultimate을 설치 합니다.

![image-20211210212933262](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20211210212933262.png)

> 원한다면 여러가지 이전 버전중 골라서 설치 할 수도 있습니다.

그닥 어려울게 없습니다. 다만 툴박스는 창이 아니고 팝업이기 때문에 리사이즈나 이동이 안되어 정말 불편합니다. 

![image-20211210214449797](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20211210214449797.png)

> https://toolbox-support.jetbrains.com/hc/en-us/community/posts/360000094690-Why-is-the-Toolbox-not-a-real-window-

저는 처음에 버그인줄 알고 재설치도 해봤었는데, 원래그런 거더라고요. 다른사람들도 불많이 많은데 몇년째 바뀌진 않고 있습니다.

## 필수 설정

### 배터리 표시 변경

![image-20211101231719474](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20211101231719474.png)

https://shanepark.tistory.com/166

> 배터리 표시를 % 숫자로 나오게 변경하는 건데, 수치로 확인 되는게 아무래도 좋습니다.

### Dock & Menu bar 숨기기

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056834.png)

Dock & Menu Bar 설정에 가면 맨 아래 체크박스로 메뉴바를 숨길 수 있습니다.  Dock 에서 아래서 세번째 버튼인 Automatically hide and show the Dock 을 체크해서 Dock 도 숨길 수 있습니다. 

저는 개인적으로 둘 다 숨겨서 사용하는게 모니터 활용 범위가 넓어져서 꼭 설정합니다.

>  Dock 체크박스중 맨 아래 있는 Show recent applications in Dock에 체크 되어있는것도 체크 해지 하는것을 강력 추천합니다. 자주 사용하는 앱은 어차피 바로가기를 등록하고 사용하니, 최근 사용 어플에 뜨는건 아이러니하게도 잘 안쓰는 앱 입니다.

### 손가락 3개 드래그

> 터치패드를 쓸 때 불편한게 드래그가 어렵다는 건데, 해당 설정을 통해 편하게 할 수 있습니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101232235913.png)

설정 -> 손쉬운 사용 (Accessibility) -> Pointer Control -> Trackpad Options 로 이동합니다.

​	

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056847.png)

Enable dragging 을 클릭 하고 세 손가락으로 드래그 하기를 선택 한 뒤 OK를 눌러 저장합니다.

이제  세손가락으로 뭐든 드래그 할 수 있습니다.



### 키보드 백틱 설정

>  한글 키보드 상태에서 ` 키 눌렀을때 ₩ 입력 되지 않도록 설정

markdown으로 글을 작성하면 백틱 키입력을 많이 하게 되는데요. 한글상태에서는 ₩로 입력이 되어서 참 불편합니다.

그럴때는 `/Library` 에 keybinding을 시켜서 해결 할 수 있습니다.

1) cd ~/Library 입력해 Library 폴더로 이동 후 mkdir KeyBindings 입력해 KeyBindings 폴더를 만들어줍니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056827-5775856.png)

​		

2) 아래와 같이 입력해 vi 에디터로 DefaultKeyBindg.dict 파일을 편집합니다.

```xml
vi ~/Library/KeyBindings/DefaultkeyBinding.dict
```

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056839.png) 

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

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056857.png)

Preference - Security & Privacy 에서 Use your Apple Watch to unlock 체크를 설정 해 두면 됩니다. Require password 설정도 기본 5분으로 되어있지만 보안을 위해서는 immediately 로 하는게 좋습니다. 애플 워치가 있으니 번거로울 일도 없어 즉시로 변경 해 두었습니다.



### Finder 설정

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056866.png)

Finder 설정도 순정 상태에서 사용하기에는 조금 불편함이 있습니다. Finder를 켠 상태에서 View - > Show Path Bar ( 저는 이미 선택해서 Hide Path Bar 로 이름이 바뀌었습니다) 를 선택합니다.

​	

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056875.png)

그러면 맨 아래 보이는 것 처럼 Path Bar가 생겨서 전체 경로를 한눈에 쉽게 보고 더블클릭으로 Navigate도 할 수 있습니다.

​	

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056865.png)

개인적으로 키보드 설정에서 New Terminal at Folder도 켜두는걸 추천합니다. 저는 New iTerm2 를 대신 사용해서 체크가 해제 되어 있습니다.



### Safari 개발자 모드 표시

Chrome에서는 기본적으로 F12 를 누르거나 Command + Shift + C 가 먹히지만, Safari는 설정을 해 주기 전까지 개발자 모드가 안됩니다.

​	

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056901.png)

메뉴바에서 Safari -> Preferences를 들어가거나 사파리 켠 상태에서 Command 키 + 쉼표 키를 입력합니다.

​	

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/img-20211101231056891.png)

이후 설정 윈도우가 뜨면 Advanced 에 들어가서 맨 아래 있는 Show Develop menu in menu bar를 체크 해 주시면 됩니다.

​	

설정을 켠 후에는 개발자 모드를 켜려면 Option + Command + i 키를 입력하면 됩니다. Chrome에서와 같이 Command + Shift + C 키입력도 작동 합니다. 개발자 모드는 크롬에서의 그것과 거의 같습니다.



### 마우스 관련 설정

1. 마우스 뒤로가기& 앞으로 가기 버튼 활성화

마우스를 사용한다면 웹 브라우징을 할 때 뒤로가기, 앞으로가기 버튼을 이용하는게 편한데 기본 세팅이 되어 있지가 않습니다. 일반적인 마우스를 사용하시는 분들은 Karabiner 를 먼저 설치 한 후에 아래 링크를 참고해 뒤로가기와 앞으로가기 버튼을 활성화 시킬 수 있습니다.

> https://shanepark.tistory.com/264

2. 마우스 가속 끄기

![](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/initial.assets/image-20211206211559027.png)

맥북에서는 솔직히 트랙패드만 써도 충분 하긴 하지만, 마우스에 익숙한 분들은 마우스가 꼭 필요 하기도 합니다. 저도 트랙패드만으로 반년 정도 사용 해 오다가, 그래도 마우스를 완전 대체 할 수는 없다는 한계를 느껴서 요즘에는 두개 다 사용하고 있습니다. 그래도 마우스에 커맨드키들 몇개 넣어서 사용하니 트랙패드를 거의 안쓰게 되긴 하더라고요.

그런데 맥북만 쓸땐 상관 없지만 리눅스나 윈도우 컴퓨터와 번갈아가며 사용하다 보면 마우스 가속 기능때문에 참 어색한데, 이게 적응도 잘 되지 않습니다. 그래서 마우스 가속을 꺼 보았는데요. 이게 상당히 괜찮아서 게임도 가능 할 정도더라고요.

맥북에서 마우스를 쓰고는 싶은데, 감도가 너무 어색하고 불편하다는 분들은 아래의 링크를 확인 해서 마우스 가속 기능을 끄고 사용해주세요.

스틸시리즈의 유틸이긴 한데, 저는 로지텍 마우스를 사용 하지만 아무 문제 없이 사용하고 있습니다. 마우스 제조사와 무관합니다.

> https://shanepark.tistory.com/297

## 개발 관련 소프트웨어

> 알파벳 오름차순 순서로 나열 하였습니다.

### Docker

docker에 대한 설명은 크게 필요 없을 것 같습니다. 한참동안 rosetta에서 조차 구동이 불가능 했는데 어느 순간 그걸 건너 뛰고 바로 native로 지원을 해 주더라고요. 간단한 예제와 함께 설치 방법을 작성 해 보았습니다. 아래 링크로 따로 준비했습니다.

> <a href="https://shanepark.tistory.com/194" target="_blank">MacOS ) m1 맥북 docker 설치하기 + 가상환경에 postgreSQL 띄워 보기</a>

### Github Desktop

-  Github Desktop을 설치합니다. 사실 sourcetree 쪽이 더 좋았었는데 배터리 이슈때문에 영 아닙니다.

```zsh
brew install github
```



### Google Chrome

- 사파리만으로도 좋긴 하지만 가끔 크롬이 필요 할 때가 있습니다.

```zsh
brew install google-chrome
```



### IINA

- iina를 설치합니다. 필수 동영상 플레이어 라고 생각합니다.

```zsh
brew install iina
```



### KEKA

- keka는 MacOS에서 가장 많이 쓰이는 압축 & 압축 해제 프로그램 입니다.

```zsh
brew install keka
```



### Microsoft remote desktop

windows 컴퓨터를 원격 조정 할 일이 있으면 다운 받아주세요.

- microsoft remote desktop을 다운 받습니다. 이거 정말 좋습니다. Teamviewer 를 거들떠도 안보게 됩니다.

```zsh
brew install microsoft-remote-desktop
```



### Oracle Database

m1 맥북에서 오라클 데이터베이스를 사용하려면 꽤나 골치가 아픕니다. 아마 가장 큰 골칫덩이 였던 것 같은데요, 특히나 많은 국비학원에서 오라클 데이터베이스를 위주로 수업을 진행 하다 보니, 처음에 컴퓨터에 익숙하지 않은 분들은 많이 좌절을 하게 됩니다. 그래도 해결방법은 다 있으니 차근 차근 따라하시면 될 거에요.

애플 실리콘 환경에서 오라클 데이터 베이스 사용에 대한 전반적인 내용은 아래의 글을 확인 해주세요.

> <a href="https://shanepark.tistory.com/208?category=1182535" target="_blank">Apple Silicon m1 맥북에서 Oracle Database 사용하기</a>

아니면 서버는 필요 없고 SQL Developer만 있으면 된다는 분들은 아래의 글을 참고 해 주세요.

>  <a href="https://shanepark.tistory.com/87" target="_blank">MacOS) m1 맥북 Oracle SQL Developer 사용하기</a>



### Postman

- postman 을 설치합니다. 이제 Apple Silicon을 지원하네요.

```zsh
brew install postman
```



### Sequal Pro

> MYSQL 혹은 MariaDB 클라이언트로 괜찮습니다.

- sequal pro를 설치합니다. 예전에 일반 버전 받았다가 m1 맥북에서 작동하지 않아서 그 후로 nightly 버전만 다운 받습니다. 정식 버전은 나중에 Apple Silicon 을 정식 지원할 때 받을 생각입니다.

```zsh
brew install homebrew/cask-versions/sequel-pro-nightly
```



### Spotify

-  spotify 구독하신다면 설치하세요.

```zsh
brew install spotify
```

> 혹은 저는 개인적으로 https://download.scdn.co/SpotifyBetaARM64.dmg 에서 Apple Silicon용 arm 64로 나온 베타 버전을 다운 받아 봤는데요. 
>
> 기존에 Rosetta로 실행되던 Spotify가 커널패닉 현상이 심했기 때문에 베타라도 더 안정적입니다. 아마 금방 정식버전이 배포될 듯 합니다.



### Spring Tool Suite

- STS (Spring Tool Suite) 를 설치합니다. Eclipse 입니다.

```bash
brew install springtoolsuite
```

### Visual Studio Code

- 장르 불문 모든 프로그래머들의 메모장. vscode 를 설치합니다.

```zsh
brew install visual-studio-code
```



## 개발 외

### 프린터

가끔 맥북을 지원하지 않는 프린터들이 있어서 곤란한 경우가 있는데요, 컴퓨터를 바꿨다는 이유로 프린터기를 새로 구입하기엔 아깝습니다. 그럴 때에는 호환되는 드라이버를 이용하면 문제를 해결 할 수도 있는데요..

그 예로 저는 삼성 sl-j1660 프린터가 집에 있는데, MacOS를 정식으로 지원하지 않습니다. 그래서 여러가지 해결책을 찾아 본 끝에 결국 사용 할 수 있었는데요, 다른 프린터들도 아마 아마 비슷하게 사용 할 수 있을테니 한번 참고 해 보세요.

> <a href="https://shanepark.tistory.com/116" target="_blank">삼성 sl-j1660 프린터 m1 맥북에서 사용하기</a>

## 마치며

오래동안 Windows를 사용해 왔으며, 지금은 회사에서는 Linux로 그 외 개인적으로는 맥북으로 개발 하고 있습니다.

여러 운영체제를 함께 사용해보니 개인적으로는 MacOS가 개발에서는 그 어느 운영 체제보다 생산성이 훨씬 좋다고 생각합니다.

실제 미국에서는 맥북을 보급한 회사가 비교적으로 직원들이 만족도와 충성도가 높으며 이직률이 낮은 것으로 조사되었다고 합니다.

여러분도 맥북으로 즐거운 개발 하세요!

## 		

이상으로 초기 설정 글을 마치겠습니다. 추가적으로 필요한게 있다면 계속해서 해당 포스팅에 추가하겠습니다.
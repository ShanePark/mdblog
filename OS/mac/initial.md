# 개발자를 위한 Mac 초기 설정 하기

​	

드디어 저의 m1 맥북에어가 수리를 마치고 집으로 돌아왔습니다. 구매한지 5개월밖에 안됐는데 얼마나 혹사를 했는지 배터리에 이상이 생겼었습니다. 다행히도 무상으로 교체를 받게 되어 지금부터 처음부터 다시 셋팅을 해 보려고 합니다. TimeMachine 백업을 해 두긴 했었는데, 주변에 맥북을 새로 산 친구들 셋팅을 몇번 해주다 보니 한번 아에 처음부터 끝까지 글로 정리를 쉽게 해두는게 좋겠다 싶어 글을 작성합니다.

​	 	

### 1. Homebrew 설치하기

[![Stainless Steel Beer Dispenser](/Users/shane/Documents/GitHub/mdblog/OS/mac/initial.assets/beer-machine-alcohol-brewery-159291-20211101231216301.jpeg)](https://images.pexels.com/photos/159291/beer-machine-alcohol-brewery-159291.jpeg?cs=srgb&dl=pexels-pixabay-159291.jpg&fm=jpg)

[Mac 에 brew 설치하기(https://shanepark.tistory.com/45?category=1182535)](https://shanepark.tistory.com/45?category=1182535)

위의 글을 쭉 따라가면 어렵지 않게 설치할 수 있습니다. 저도 간만에 해보는건데 처음 상태에서 글만 쭉 따라가니 이상 없이 설치 할 수 있었습니다. 사실상 brew만 설치하면 맥북 모든 설치는 정말 간편하게 할 수 있습니다. 설치 못하는 프로그램이 거의 없습니다.

​		 	

### 2. Alfred 설치

![image-20211101231458490](/Users/shane/Documents/GitHub/mdblog/OS/mac/initial.assets/image-20211101231458490.png)

https://shanepark.tistory.com/164

> 이왕 brew 설치 포스팅에 alfred 설치 하는 방법까지 같이 이어서 하니, alfred 까지 설치하시길 추천합니다. 

​	 	

### 3. Karabiner 설치

[![Black and Silver Macbook Pro](/Users/shane/Documents/GitHub/mdblog/OS/mac/initial.assets/pexels-photo-7662049.jpeg)](https://images.pexels.com/photos/7662049/pexels-photo-7662049.jpeg?cs=srgb&dl=pexels-szabó-viktor-7662049.jpg&fm=jpg)

https://shanepark.tistory.com/165

> 한영키때문에 정말 1초라도 빨리 설치하고 싶었습니다.  
>
> 어쩌다보니 Alfred에 밀렸지만 사실 brew 설치하자 마자 바로 설치해야 할 친구입니다.

 		

### 4. 배터리 표시 변경

![image-20211101231719474](/Users/shane/Documents/GitHub/mdblog/OS/mac/initial.assets/image-20211101231719474.png)

https://shanepark.tistory.com/166

> 배터리 표시를 % 숫자가 나오게 변경하는 건데, 원하는 분만 하면 되겠습니다. 저는 하는게 좋더라고요.

​	 	

### 5. Dock, Menu bar 숨기기

![img](/Users/shane/Documents/GitHub/mdblog/OS/mac/initial.assets/img-20211101231056834.png)

Dock & Menu Bar 설정에 가면 맨 아래 체크박스로 메뉴바를 숨길 수 있습니다.  Dock 에서 아래서 세번째 버튼인 Automatically hide and show the Dock 을 체크해서 Dock 도 숨길 수 있습니다. 

> 저는 개인적으로 둘 다 숨겨서 사용하는게 모니터 활용 범위가 넓어져서 좋습니다.
>
> 숨겨서 사용하면 다중 모니터를 사용할 때 특히 좀 더 편합니다. 

Dock 체크박스중 맨 아래 있는 Show recent applications in Dock 체크 되어있는것도 체크 해지 하는것을 강력 추천합니다. 

자주 사용하는 앱은 어차피 바로가기를 등록하고 사용하니, 최근 사용 어플에 뜨는건 아이러니하게도 잘 안쓰는 앱 입니다.

 	

### 6. java 설치하기

[![Text](/Users/shane/Documents/GitHub/mdblog/OS/mac/initial.assets/pexels-photo-6190327.jpeg)](https://www.pexels.com/photo/creative-dark-internet-designer-6190327/)

이제 개발에 필요한 것들을 하나씩 설치 해 보려고 합니다. 저는 openjdk를 설치하겠습니다. oracle java를 설치 하고 싶은 분은 oracle 공식 홈페이지에서 다운 받아서 하시면 됩니다. 

```bash
brew tap AdoptOpenJDK/openjdk
```

일단 brew해서 tap 을 한번해줘야 합니다.

​	

![img](/Users/shane/Documents/GitHub/mdblog/OS/mac/initial.assets/img-20211101231056849.png)

이제 brew search openjdk 를 입력 하면 , 많은 버전의 openjdk를 보여줍니다. 

​	

8, 11, 14 버전이 많이 쓰이는데. 개인적으로는 11 버전이나 14 버전을 추천합니다. 특히 8 버전은 SQL Developer 등을 사용할 때 문제가 많습니다. 저는 14를 설치하겠습니다. 

```zsh
brew install adoptopenjdk14
```



![img](/Users/shane/Documents/GitHub/mdblog/OS/mac/initial.assets/img-20211101231056887.png)

왠만하면 설치가 될 텐데, 저는 m1 맥북인데 아직 Rosetta 2 를 설치하지 않아서 에러가 발생했습니다. 혹시 Rosetta 2가 설치되어 있지 않다면 바로 아래 7번을 하거나 에러 메시지를 보시고 그대로 설치 하셔도 됩니다.

​	

![img](/Users/shane/Documents/GitHub/mdblog/OS/mac/initial.assets/img.png)

자바도 간단하게 설치 완료했습니다.

 	

### 7. (Apple Silicon 한정 ) Rosetta 2 설치하기

```xml
sudo softwareupdate --install-rosetta
```

m1 맥북 사용하신다면 Rosetta가 필수입니다. 처음 m1 맥북 샀을때는 Apple Silicon 최적화된 프로그램이 거의 없다 싶이 해서 컴퓨터 뭐 설치하면서 금방 설치했었는데, 왠만한 게 다 Apple Silicon 을 지원하는 지금에서는 아직 설치하지 않았다는걸 깨닫는데 한참 걸렸네요.

​	

![img](/Users/shane/Documents/GitHub/mdblog/OS/mac/initial.assets/img-20211101231056818.png)

동의하면 A 누르고 엔터 치라는데 . 당연히 동의합니다.

​	

![img](/Users/shane/Documents/GitHub/mdblog/OS/mac/initial.assets/img-20211101231056803.png)

그닥 오래 걸리지 않습니다.

 	

### 8. iterm2 설치

![img](/Users/shane/Documents/GitHub/mdblog/OS/mac/initial.assets/photo-1516031190212-da133013de50?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2-20211101232136840.1&auto=format&fit=crop&w=1000&q=80)

```xml
brew install iterm2
```

사실 iterm2 설치 자체는 굉장히 간단한데요.  oh-my-zsh 설치 하는게 조금 까다롭습니다.

https://shanepark.tistory.com/60?category=1182535 

위의 글을 보고 따라하시면 어렵지 않게 하실 수 있습니다.

​	 

### 9. 손가락 3개로 드래그 하기 

![img](/Users/shane/Documents/GitHub/mdblog/OS/mac/initial.assets/img-20211101232235913.png)

설정 -> 손쉬운 사용 (Accessibility) -> Pointer Control -> Trackpad Options 로 이동합니다.

​	

![img](/Users/shane/Documents/GitHub/mdblog/OS/mac/initial.assets/img-20211101231056847.png)

Enable dragging 을 클릭 하고 세 손가락으로 드래그 하기를 선택 한 뒤 OK를 눌러 저장합니다.

이제  세손가락으로 뭐든 드래그 할 수 있습니다.

 	

### 10. SQL Developer 설치

https://shanepark.tistory.com/87

혹시 Oracle 서버를 운용한다면 SQL Developer를 설치해야 합니다. 위 링크를 참고해 설치합니다.

 	

### 11. 한글 키보드 상태에서 ` 키 눌렀을때 ₩ 입력 되지 않도록 설정

markdown 을 쓸때 `를 참 많이 쓰게 되는데요. 한글상태에서는 ₩로 입력이 되어서 참 불편합니다.

Library 에 keybinding을 시켜서 해결 할 수 있습니다.

1) cd ~/Library 입력해 Library 폴더로 이동 후 mkdir KeyBindings 입력해 KeyBindings 폴더를 만들어줍니다.

![img](/Users/shane/Documents/GitHub/mdblog/OS/mac/initial.assets/img-20211101231056827-5775856.png)

​		

2) 아래와 같이 입력해 vi 에디터로 DefaultKeyBindg.dict 파일을 편집합니다.

```xml
vi ~/Library/KeyBindings/DefaultkeyBinding.dict
```

![img](/Users/shane/Documents/GitHub/mdblog/OS/mac/initial.assets/img-20211101231056839.png) 

에디터가 켜지면 다음과 같이 입력하고 :wq로 저장 합니다.

```
{
    "₩" = ("insertText:", "`");
}
```

​	

이제 사용중이던 프로그램을 재시작 한번 해주면, 한글 상태에서 ₩ 를 입력 해도 정상적으로 ` 가 입력 됩니다.

 

### 12. 애플워치가 있다면 애플워치로 맥북 잠금해제 옵션을 설정합니다.

![img](/Users/shane/Documents/GitHub/mdblog/OS/mac/initial.assets/img-20211101231056857.png)

Preference - Security & Privacy 에서 Use your Apple Watch to unlock 체크를 설정 해 두면 됩니다. Require password 설정도 기본 5분으로 되어있지만 보안을 위해서는 immediately 로 하는게 좋습니다. 애플 워치가 있으니 번거로울 일도 없어 즉시로 변경 해 두었습니다.

​	

### 13. Finder 설정

![img](/Users/shane/Documents/GitHub/mdblog/OS/mac/initial.assets/img-20211101231056866.png)

Finder 설정도 순정 상태에서 사용하기에는 조금 불편함이 있습니다. Finder를 켠 상태에서 View - > Show Path Bar ( 저는 이미 선택해서 Hide Path Bar 로 이름이 바뀌었습니다) 를 선택합니다.

​	

![img](/Users/shane/Documents/GitHub/mdblog/OS/mac/initial.assets/img-20211101231056875.png)

그러면 맨 아래 보이는 것 처럼 Path Bar가 생겨서 전체 경로를 한눈에 쉽게 보고 더블클릭으로 Navigate도 할 수 있습니다.

​	

![img](/Users/shane/Documents/GitHub/mdblog/OS/mac/initial.assets/img-20211101231056865.png)

개인적으로 키보드 설정에서 New Terminal at Folder도 켜두는걸 추천합니다. 저는 New iTerm2 를 대신 사용해서 체크가 해제 되어 있습니다.

 	

### 14. Safari 개발자 모드 표시 하기

Chrome에서는 기본적으로 F12 를 누르거나 Command + Shift + C 가 먹히지만, Safari는 설정을 해 주기 전까지 개발자 모드가 안됩니다.

​	

![img](/Users/shane/Documents/GitHub/mdblog/OS/mac/initial.assets/img-20211101231056901.png)

메뉴바에서 Safari -> Preferences를 들어가거나 사파리 켠 상태에서 Command 키 + 쉼표 키를 입력합니다.

​	

![img](/Users/shane/Documents/GitHub/mdblog/OS/mac/initial.assets/img-20211101231056891.png)

이후 설정 윈도우가 뜨면 Advanced 에 들어가서 맨 아래 있는 Show Develop menu in menu bar를 체크 해 주시면 됩니다.

​	

설정을 켠 후에는 개발자 모드를 켜려면 Option + Command + i 키를 입력하면 됩니다. Chrome에서와 같이 Command + Shift + C 키입력도 작동 합니다. 개발자 모드는 크롬에서의 그것과 거의 같습니다.

​	

​	 

### 그 외 잡다하지만 필수적인 설치

​	 

\- 사파리만으로도 좋긴 하지만 가끔 크롬이 필요 할 때가 있습니다.

```xml
brew install google-chrome
```

 	

-iina를 설치합니다. 필수 동영상 플레이어 라고 생각합니다.

```xml
brew install iina
```

​	 

\- keka를 설치합니다. 

```xml
brew install keka
```

​	 

\- spotify 를 설치합니다.

```xml
brew install spotify
```

혹은 저는 개인적으로 https://download.scdn.co/SpotifyBetaARM64.dmg 에서 Apple Silicon용 arm 64로 나온 베타 버전을 다운 받아 봤는데요. 기존에 Rosetta로 실행되던 Spotify가 커널패닉 현상이 심했기 때문에 베타라도 더 안정적입니다. 아마 금방 정식버전이 배포될 듯 합니다.

​	

\- 장르 불문 모든 프로그래머들의 메모장. vscode 를 설치합니다.

```xml
brew install visual-studio-code
```

 	

\- STS (Spring Tool Suite) 를 설치합니다. Eclipse 입니다.

```xml
brew install springtoolsuite
```

 	

\- Github Desktop을 설치합니다. 사실 sourcetree 쪽이 더 좋았었는데 배터리 이슈때문에 영 아닙니다.

```xml
brew install github
```

 	

\- postman 을 설치합니다. 이제 Apple Silicon을 지원하네요.

```xml
brew install postman
```

 	

\- sequal pro를 설치합니다. 예전에 일반 버전 받았다가 m1 맥북에서 작동하지 않아서 그 후로 nightly 버전만 다운 받습니다. 정식 버전은 나중에 Apple Silicon 을 정식 지원할 때 받을 생각입니다.

```xml
brew install homebrew/cask-versions/sequel-pro-nightly
```

 	

\- microsoft remote desktop을 다운 받습니다. 이거 정말 좋습니다. Teamviewer 를 거들떠도 안보게 됩니다.

```xml
brew install microsoft-remote-desktop
```
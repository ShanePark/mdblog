# AppleScript로 MacOS 자동 작업 스케줄 등록하기

## Intro

10년 전 쯤에 컴퓨터를 쓰다가 뭔가 자동화가 필요한 시점에서는 항상 AutoHotkey 라는 스크립트를 주로 사용했었습니다.

이 덕분에 정말 말할 수 없을 만큼 다양한 작업들을 스크립트 작성 후 자동으로 했었는데요. MacOS를 사용하면서 뭔가 스케줄을 등록해두고 자동으로 시행했으면 하는 일들이 생겼는데, 맥에서는 AutoHotkey가 지원이 되지 않아 대체제를 찾아보다가 애플에서 제공하는 AppleScript가 있기에 한번 사용을 해 보았습니다.

생각보다 문법이 직관적이고 어렵지 않았으며 의도한 대로 작동 했기 때문에 사용법을 한번 남겨 보려고 합니다.

## 특정 시간에 카카오톡으로 메시지 전송

어떤 작업을 자동으로 진행 해 볼까 하다가 카톡 메시지 전송을 한번 테스트 해 보았습니다. 

### script 작성

![image-20220604114213164](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/applescript.assets/image-20220604114213164.png)

> Script Editor 라는 어플리케이션을 기본으로 제공 합니다.

코드 작성에는 큰 불편함이 없었지만 에디터로서 기능은 굉장히 형편없는 편 입니다.

이제 간단하게 아래와 같이 카카오톡이 실행 중 이라면 활성화 시키고, my text라는 키를 전송 하는 스크립트를 작성 해 보았습니다.

```AppleScript
if application "KakaoTalk" is running then
	tell application "KakaoTalk" to activate
	tell application "System Events" to ¬
		tell application process "KakaoTalk"
			keystroke "My Custom Text"
			key code 36
		end tell
end if
```

![image-20220604114253600](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/applescript.assets/image-20220604114253600.png)

이후 카카오톡 어플리케이션을 켜 두고, 원하는 대화 방을 띄워 둔 상태에서

<img src="https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/applescript.assets/image-20220604114421266.png" alt="image-20220604114421266" style="zoom:50%;" />

![image-20220604114446453](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/applescript.assets/image-20220604114446453.png)

우측 상단의 Run the Script 버튼을 클릭 해 봅니다.

<img src="https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/applescript.assets/image-20220604114558316.png" alt="image-20220604114558316" style="zoom:50%;" />

> 카톡 채팅방에 원하는 텍스트가 입력 되었습니다.

다만, 한글 입력을 시도 할 경우에는 한글이 깨지는 문제가 발생 했는데요

```AppleScript
if application "KakaoTalk" is running then
	tell application "KakaoTalk" to activate
	tell application "System Events" to ¬
		tell application process "KakaoTalk"
			keystroke "한글 입력"
			key code 36
		end tell
end if
```

<img src="https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/applescript.assets/image-20220604114648425.png" alt="image-20220604114648425" style="zoom:50%;" />

이 경우에는 뚜렷한 해법은 없기 때문에 클립보드에서 붙여넣는 방법으로 우회 해야 합니다.

클립보드에 한글 텍스트를 복사 해둔 상태에서 아래의 스크립트를 실행 하면

```AppleScript
if application "KakaoTalk" is running then
	tell application "KakaoTalk" to activate
	tell application "System Events" to ¬
		tell application process "KakaoTalk"
			delay 0.1
			keystroke "v" using command down
			key code 36
		end tell
end if
```

<img src="https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/applescript.assets/image-20220604114845857.png" alt="image-20220604114845857" style="zoom:50%;" />

한글 입력이 됩니다.

`Command down` + `v` 키 입력이 바로 될 경우 키입력이 먹히는 경우가 있길래 `delay 0.1` 로 delay를 조금 줬더니 안정적으로 입력 되었습니다.

하지만 클립보드에 항상 원하는 텍스트가 있다는 보장은 할 수 없기 때문에 원하는 문자열을 클립보드에 넣어 두는 코드까지 함께 작성해 두었습니다.

```AppleScript
my stringToClipboard("한글 텍스트")
if application "KakaoTalk" is running then
	tell application "KakaoTalk" to activate
	tell application "System Events" to ¬
		tell application process "KakaoTalk"
			delay 0.1
			keystroke "v" using command down
			key code 36
		end tell
end if

on stringToClipboard(t1)
	do shell script "/usr/bin/python3 -c 'import sys;from AppKit import NSPasteboard, NSPasteboardTypeString; cb=NSPasteboard.generalPasteboard();cb.declareTypes_owner_([NSPasteboardTypeString], None);cb.setString_forType_(sys.argv[1], NSPasteboardTypeString)' " & quoted form of t1
end stringToClipboard
```

클립보드 이용에 python3 을 사용하는데, macOS 는 기본적으로 파이썬 2.x 버전이 설치되어 있습니다.  저는 python3 를 설치 해 두었기때문에 위와같이 `/usr/bin/python3` 를 사용하지만 python3가 없는 분은 저 부분을 수정 하고 `/usr/bin/python` 으로 수정하고 거의 끝 부분에 `sys.argv[1]` 을 `sys.argv[1].decode(\"utf8\")` 로 수정하거나 python3 를 설치해 주세요.

> 클립보드를 활용하는 파이썬 스크립트는 https://jiyeonseo.github.io/2019/12/28/applescript/ 를 참고하였습니다.

실행 했을 때, 아마 아래의 에러가 나올 수 있는데요

```
ModuleNotFoundError: No module named 'AppKit' (1)rror: Traceback (most recent call last):
```

pyobjc 패키지가 없기 때문입니다. 그때는 아래의 명령어로 pyobjc 를 설치 해 줍니다.

```
pip3 install pyobjc
```

<img src="https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/applescript.assets/image-20220604125422411.png" alt="image-20220604125422411" style="zoom:50%;" />

> 그러고 나서 실행 해 보면, 클립보드에 다른 텍스트가 있어도 지정한 텍스트를 입력하는 것을 확인 할 수 있습니다.

## 특정 시간에 실행

이번에는 해당 스크립트를 특별히 정해 둔 시간에 정확히 실행 되도록 해 보겠습니다.

일단 스크립트 파일은 `~/Downloads/Untitle.scpt` 경로에 저장을 해 두었는데요 Terminal에서 정상적으로 작동하는지 먼저 확인을 해 봅니다.

![image-20220604125746504](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/applescript.assets/image-20220604125746504.png)

```bash
osascript ~/Downloads/Untitled.scpt
```

![gif](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/applescript.assets/gif.gif)

> 터미널에 명령 실행 시, 카카오톡에서 메시지를 전송 하는걸 확인 했습니다.

이제 해당 명령을 정해진 시간에 실행 해 주기만 하면 되는데요, UNIX에서는 crontab이라는 작업 스케쥴러가 있기 때문에 쉽게 설정 할 수 있습니다.

```bash
crontab -e
```

![image-20220604130052087](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/applescript.assets/image-20220604130052087.png)

crontab -e 명령어를 실행 하면, crontab 설정을 하도록 vim 에디터가 실행 되는데요, 저는 일단 테스트를 위해 아래와 같이 입력 해 매 분마다 실행 되도록 설정 해 보았습니다.

```
* * * * * osascript ~/Downloads/Untitled.scpt
```

자세한 crontab 설정 법은 아래의 사이트를 참고 해 주세요.

![image-20220604130333886](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/applescript.assets/image-20220604130333886.png)

> https://crontab.guru/

설정 후에는 `:wq` 로 저장 후 나가주면

![image-20220604130431283](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/applescript.assets/image-20220604130431283.png)

`crontab: installing new crontab` 이라는 문구가 나옵니다. 이로서 모든 설정은 끝입니다.

이제 매 분 0초가 될 때마다 자동으로 카카오톡에서 지정한 메시지를 전송하게 됩니다.

## 마치며

![image-20220604130549782](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/applescript.assets/image-20220604130549782.png)

위의 이미지처럼 카카오톡에는 죠르디 비서 기능이 있기 때문에 굳이 이렇게 스크립트를 짜고 스케쥴을 등록 할 필요는 없습니다. 다만 AppleScript의 문법이 이렇고 스크립트 작성으로 이러한 동작을 지시할 수 있구나 정도로 이해 해 주시면 되겠습니다.

은근 작동까지 우회되는 부분도 많기 때문에 0.01초를 다투는 상황에서는 지금처럼 스크립트를 작성하면 안되고 최적화를 위해 상당한 애를 써야합니다. 

사실 스크립트를 매크로용도로 사용하는 것 자체가 복잡한 동작을 사람이 직접 입력하는 것 보다는 훨씬 빠르게 할 수 있을지언정 순간의 반응속도로 대결하는 부분에서는 스크립트 언어보다는 훨씬 로우레벨의 프로그래밍 언어가 적합하며 코드도 이렇게 복잡하면 좋지 않습니다. 10여년 전 쯤 오토핫키로 메이플스토리 상점 개설 스크립트를 작성 해 보았었는데 이미지 인식을 통해 반응하도록 했다가 C언어 기반으로 통신패킷을 가로채어 반응하는 코드를 작성한 유저에게 꼼짝도 없이 당했던 기억이 나네요.

단순한 작업은 AppleScript로 작성해 보시는것도 괜찮다고 생각합니다.

이상입니다.
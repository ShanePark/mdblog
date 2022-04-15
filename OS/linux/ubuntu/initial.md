# 개발자를 위한 Ubuntu 필수 설치와 설정

![How To Install Ubuntu Linux inside Windows - Techi Signals](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/Install-Ubuntu-Linux.jpg)	

> 꾸준히 최신 정보로 업데이트 되는 글 입니다.

## 들어가기전에 

### 서론

[개발자를 위한 Mac 초기 설정 하기](https://shanepark.tistory.com/167) 포스팅을 작성한지 두달이 조금 넘었습니다. Mac을 처음 구입하시는 분들이 처음에 세팅을 한번에 하기 위해 꾸준히 방문해 주시는 포스팅 인데, 다른 분들에게 얼마나 도움이 되는지 정확히는 모르지만 적어도 저에게는 처음부터 세팅을 다시 해야하거나 다른 분들의 세팅을 도와 줄때마다 다시 찾아보게 되어 스스로 큰 도움을 받고 있습니다. 

처음 Ubuntu 를 설치 하며 많은 어려움을 겪었었고, 후에도 집에서 개인 서버로 사용하던 윈도우 노트북도 윈도우를 아에 밀어버리고 우분투로 바꿔 보고, 또 스스로도 잘못 건들었다가 다시 설치하는 등 시행착오를 몇 번 겪다 보니 정리를 해 두는게 좋다는 생각이 들어 하나 둘 씩 정리하기 시작했습니다. 

최근에도 회사에서 우분투가 꼬여버려 개발환경을 다시 세팅해야 하는 일이 있었는데, 해당 글을 참고하며 금방 진행 할 수 있었습니다.

### 진행 순서

아래의 내용 중 본인에게 필요한 내용만 순서에 맞춰 설치하시면 됩니다.

최대한 초기 세팅 순서에 맞춰 배열을 해 두었지만, 우분투를 이미 몇번 설치 해 본 경험이 있는 분이라면 컨텐츠 테이블을 확인 하며 각자 필요하신 내용을 먼저 진행 하셔도 좋습니다.

선택 설정/ 선택 설치로 내려둔 것 들도, 필수가 너무 많으면 처음부터 너무 버겁게 느낄 수 있으니 나누어 둔 항목들이지만 사실 대부분이 필수에 가깝습니다.

## 필수 설정

### 한글 키보드 입력 설정

보통 한글 입력 때문에 키보드 설정을 제일 먼저 하는게 편합니다. 맥북을 설정할때도 Karabiner를 제일 먼저 설치하는 것 과 같은 맥락 입니다. iBus에 한계가 분명 있긴 하지만 기본적으로 iBus 설정은 무조건 하는게 낫다고 생각합니다. 

Ubuntu를 처음 설치한다면 iBus 키보드 설정만 하시면 되지만, 최종적으로는 KIME 입력기를 설치하는게 좋습니다. 일단 iBus를 쓰다가 불편함이 있을 때 KIME를 설치 하시면 됩니다.

- iBus 키보드 설정

> [Ubuntu 20.04 키보드 한글 입력 설정 하기](https://shanepark.tistory.com/231)

- fcitx 키보드 설정

>  [fctix입력기 설치해 intelliJ 한글입력 해결하기](https://shanepark.tistory.com/262?category=1222202)
>
>  iBus보다는 낫지만 Kime을 사용하는걸 추천합니다.

- KIME입력기 설치

> [Linux) KIME 한글 입력기](https://shanepark.tistory.com/318)
>
> 호동님이 수개월 진행하시던 Tian 프로젝트를 중단한 이상 현재로서는 KIME가 가장 완벽에 가까운 한글 입력기라고 생각합니다.

### 비프음 끄기

회사에서는 노트북 볼륨을 꺼 두고 써서 몰랐는데, 우분투에서의 비프음이 굉장히 거슬리더라고요. 해당 비프음도 꺼줍니다.

[Ubuntu Terminal에서 백스페이스/방향키 누를때 삐 하는 비프음 안나게 하기](https://shanepark.tistory.com/234)

### SSH key 생성

> 더 자세한 내용은 [SSH key 생성하고, 서버에 등록해서 비밀번호 없이 접속하기](https://shanepark.tistory.com/195?category=1222202) 글을 참고해주세요.

SSH 접속을 위한 key를 생성 해 둡니다. 처음에는 키를 저장할 경로를 묻는데요, default 경로가 (/home/{user}/.ssh/id_rsa) 입니다. 굳이 변경을 하지 않으려면 그냥 엔터키를 입력 하면 됩니다.

두번째, 세번째에서는 passphrase(추가로 사용할 암호, 기본값 없음)을 입력하는데 굳이 입력 하지 않아도 됩니다.

```bash
ssh-keygen -t rsa
```

![](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20210920101205629.png)

생성이 완료되었습니다.

```bash
cat ~/.ssh/id_rsa.pub
```

를 입력해서 public 키를 읽을 수 있구요, 원격 접속 할 컴퓨터에 등록해서 사용 하시면 편합니다.

> [SSH key 생성하고, 서버에 등록해서 비밀번호 없이 접속하기](https://shanepark.tistory.com/195?category=1222202) 

### 자동 잠금 방지

기본적인 설정으로 몇 분 동안 사용하지 않으면 잠금이 되어 버리는데, 사무실에서 사용할 땐 불편 할 수 있으니 설정을 풀어놓고 사용하는 편 입니다.

Settings -> Privacy -> ScreenLock에서

Blank Screen Delay를 Never로, Automatic Screen Lock을 체크 해제 합니다.

![a](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/a.png)

![b](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/b.png)

![c](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/c.png)

### Ctrl+Alt+방향키 키바인딩 삭제

워크스페이스간 이동은 `Super+PageUp/PageDown`이 이미 배정이 되어 있는데요, 추가로 Ctrl+Alt+방향키가 배정되어있는 바람에 이클립스라도 쓰는 날엔 라인 복사를 할 수 없어서 불편합니다. 텐키리스 키보드에서 PageUp/Down은 다 붙어 있으니, 굳이  Ctrl+Alt를 사용하는 단축키로 남겨 둘 필요도 없고, 저는 맥북에서도 데스크톱 전환을 컨트롤+방향키로 하다 보니 해당 키바인딩은 꼭 삭제 합니다.

- dconf Editor 를 켜고 org.gnome.desktop.wm.keybindings에 가서

![image-20211208095711980](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20211208095711980.png)

switch-to-workspace 를 검색합니다.

![image-20211124090259043](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20211124090259043.png)

위에 보이는 것 처럼 up과 down에 있는 모든 내용을 

![image-20211124090435799](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20211124090435799.png)

Use default value를 해제 한 후에, `, '<Control><Alt>Down'` 를 통째로 지우고 Apply 를 하면 됩니다. Up, Down 모두 번갈아 가며 지워 줍니다. 그러고 나면 Ctrl+Alt+방향키를 눌러도 workspace 전환이 되지는 않습니다. 자유롭게 해당 단축키를 다른 필요한 곳에 할당 해서 사용 할 수 있습니다.

마찬가지로 좌,우도 없애줘야 하는데요, intelliJ IDEA 에서 해당 키를 `Navigate back / forward`로 사용하고 있기 때문입니다.

위에서 처럼 눈으로 보며 삭제 할 수 있지만, 사실 아래의 명령어로 간단하게 없앨 수 있습니다.

```zsh
gsettings set org.gnome.desktop.wm.keybindings switch-to-workspace-left "[]"
gsettings set org.gnome.desktop.wm.keybindings switch-to-workspace-right "[]"
```



### 시스템 종료 단축키 만들기

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/img.png)

저는 종료할 때 윈도우의 Alt+F4 같은 간단한 종료 단축키가 필요한데 우분투에는 마땅히 없더라고요.

그래서 Super+F4 를 종료 단축키로 만들어서 사용하고 있습니다. 키보드 shortcut 설정에서 아래의 커맨드를 원하시는 Shortcut으로 설정 해서 사용하면 됩니다. `Super+F4`를 입력한다고 바로 꺼지는건 아니고, 입력하고 60초 후에 자동으로 종료되거나 혹은 Super+F4 를 누르고 지금 종료 버튼 한번 눌러주면 됩니다.

```zsh
gnome-session-quit --power-off
```

### Apt 저장소 미러 변경

기본 APT 저장소 미러 주소는 `kr.archive.ubuntu.com/ubuntu` 인데요.

![image-20220415162010647](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20220415162010647.png)

>  ping을 날려 보았을 때 응답 시간이 200ms 에 육박할 정도로 느리기 때문에 꽤나 답답합니다.

한국의 카카오 미러 서버로 변경을 해주면 훨씬 빨라지기 때문에 시간을 절약 할 수 있습니다.

```zsh
sudo vi /etc/apt/sources.list
```

sources.list 파일을 sudo 권한으로 열어서

```
:%s/kr.archive.ubuntu.com/mirror.kakao.com
```

찾아 바꾸기 명령을 이용해 카카오 미러로 변경 해 줍니다.

![image-20220415162415447](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20220415162415447.png)

> 변경 후에 위에 보이는 것 처럼 모든 미러가 카카오로 변경 되었으면 OK 입니다.

이어서 security.ubuntu.com 도 변경해줍니다.

```
%s/security.ubuntu.com/mirror.kakao.com/
```

![image-20220415162301748](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20220415162301748.png)

이후에는 이제 `sudo apt update` 등의 명령어를 날려보면 이전과는 비교할 수 없을 정도로 속도가 빨라진 것을 바로 체감 할 수 있습니다. 

아쉽게도 카카오 미러는 ping 응답을 막아두었기 때문에 확인은 할 수 없었지만 제 생각에는 5ms 정도 되지 않을까 싶습니다. Apt 저장소는 꼭 변경하시는게 좋습니다.

## 필수설치

### vim 편집기 설치

Ubuntu 환경에서 기본 설치되어있는 vim을 이용해보니 방향키를 누를 때 이상한 문자가 입력 된다던가 하는 불편함이 있었습니다. 그래서 기본으로 설치되어 있는게 향상된 vim이 아닌가 싶어 -version을 입력 해 보니

![image-20210920093024091](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20210920093024091.png)

Vi IMproved 라고 나오긴 했습니다. 방향키야 hjkl를 사용하면 된다지만 백스페이스 라던가 몇가지 불편한 점이 있어서 새로 설치를 해 보려고 합니다.

- 바로 설치를 해 보겠습니다.

```bash
sudo apt-get update
sudo apt-get install vim

```

![image-20210920093725359](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20210920093725359.png)

설치가 끝났습니다. 

-version 을 해보니 완전 똑같이 나와서. 뭐야, 이전이랑 똑같은게 아니야 ? 할 수 있는데요.

![image-20210920093946034](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20210920093946034.png)

<br><br>vi test.txt 입력해서 새로 편집기를 띄워 보니 백스페이스, 방향키 등이 의도한 대로 잘 동작이 됩니다.

![image-20210920094102444](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20210920094102444.png)	

<br><br>

이제 vim을 설치했으니 몇가지 설정을 해 두는게 좋습니다.

```bash
vi ~/.vimrc
```

![image-20210920094256866](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20210920094256866.png)

```bash
" Syntax Highlighting
if has("syntax")
        syntax on
endif

" 검색 관련 설정
set ignorecase "검색시 대소문자 무시
set hlsearch " 검색 단어 하이라이트

" 에디터 관련 설정
set number "Line Number 표시
set cindent "자동 들여쓰기
set autoindent
set ts=2 " Tab 너비(보여줄때)
set sts=2 " Tab 너비(작성할때)
set shiftwidth=4 " 자동 인덴트 너비
set showmatch "짝이 되는 괄호 하이라이트
```

>  간단하게 이정도 설정만 저장 해 두고 쓰면서 필요할 때 더 추가합니다.

### Terminator

처음에 설치되어 있는 Ubuntu Terminal도 사용하는데는 충분히 훌륭하지만 편의 기능면에서 많이 떨어지는게 사실 입니다.

특히 창 분할에 있어서 터미네이터는 정말 비교할 수 없을 정도로 높은 생산성을 보여줍니다. 여러 창을 띄워놓고 작업하기에 기본 터미널은 너무나도 부족합니다. 설치가 어렵지도 않기 때문에 이왕 설치 하면서 미리 설치 해 두는걸 추천 드립니다.

기본 터미널과 비교해 디자인도 다를 바 없으며 그냥 편의 기능만 추가 되기 때문에 단점은 전혀 없다고 봐도 무방합니다.

```zsh
sudo apt install terminator
```

위의 명령어로 설치 할 수 있으며, 자세한 설정법과 단축키는 아래의 링크를 통해 확인 해 주세요.

> [Ubuntu) Terminator 설치 및 사용법](https://shanepark.tistory.com/313)

### KAKAO TALK

![image-20220129171723784](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20220129171723784.png)

사실 카카오톡이 필수 설치인지에 대해서는 이견이 갈릴 수 있겠습니다.

저도 카카오톡이 굳이 필요 없다고 생각하여 한동안 시도를 하지 않다가, 그래도 종종 필요할 때가 있어서 설치를 시도 했었습니다.

그런데 인터넷에 나와 있는 많은 방법을 써도 설치에 실패했었고, 여러가지 정보를 확인 해 보아도 막히는 부분이 있었는데

이번에 작정하고 설치에 도전 한 끝에 성공 할 수 있었습니다. 과정은 제법 복잡하지만, 여타 카카오톡을 우분투에 설치해서 사용해 보았다는 분들이 사진첨부가 안된다거나 한글이 깨진다거나 이모티콘이 안보인다거나 하는 문제가 있었다는 후기가 많았는데 제가 설치한 방법대로 하면 거의 100% Windows나 Mac에서의 사용과 일치했기 때문에 이정도면 설치를 권장 드립니다.

다만 과정이 꽤나 복잡하기 때문에 Tweak 이나 Font, 그리고 한글 입력기에 대한 이해가 먼저 선행 되어야 합니다.

아래 링크를 참고 해 주세요.

> [Ubuntu) 우분투에서 카카오톡 실행 (100% 정상작동 방법)](https://shanepark.tistory.com/328)

### SDKMAN

> The Software Development Kit Manager

SDKMAN은 대부분의 Unix 기반 시스템에서 Soft Development Kits들을 병렬적으로 관리할 수 있게 해주는 프로그램 입니다. 간편한 CLI 환경과 API를 통해 각종 SDK들을 설치, 전환, 삭제 할 수 있으며 설치 가능한 SDK들을 한눈에 확인 할 수도 있습니다.  OpenJDK, ant, Gradle, Maven 등등 자바 기반 개발 도구를 간편하게 관리 하기 위해 설치합니다. SDK 를 여러가지 버전을 설치 해 두고 전환하며 사용 할 필요성을 느끼는 분들은 당연히 설치하시겠지만 필요성을 느끼지 못한다면 꼭 지금 시점에서 설치하실 필요는 없습니다.

다만 추후에 분명 필요성을 느낄 때가 있을테니, 이왕 하는거 지금 설치하는 것도 나쁘진 않겠죠.

![image-20220324144040544](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20220324144040544.png)

> https://sdkman.io/

아래의 명령어로 간편하게 설치 합니다.

```zsh
curl -s "https://get.sdkman.io" | bash
```

![image-20220324144152738](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20220324144152738.png)

설치 후에는 시키는대로 아래의 명령어를 한번 실행 해 줍니다.

```zsh
source "/home/shane/.sdkman/bin/sdkman-init.sh"
```

이제 잘 설치 되었는지 확인 해 봅니다.

```zsh
sdk version
```

![image-20220324144503252](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20220324144503252.png)

> 5.14.1 버전이 설치 되었네요.

### JDK 설치 

![black and silver laptop computer on table](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/photo-1517694712202-14dd9538aa97ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1000&q=80)

#### SDKMAN 미설치의 경우

SDKMAN이 없어도 JDK 단일 버전만 사용할 계획이라면 apt로 간단하게 설치 할 수 있습니다.

> 각자 필요한 버전에 맞춰 8 대신 넣으면 됩니다. 해당 명령어로 설치가 끝납니다.

```bash
$ sudo apt-get install openjdk-8-jdk
```

#### SDKMAN을 설치 한 경우

바로 위에서 SDKMAN을 설치 한 경우의 설치 방법입니다.

```zsh
sdk list java
```

 위의 명령어를 입력해 설치 가능한 자바 목록을 확인 합니다.

![image-20220324145930354](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20220324145930354.png)

Corretto(아마존), Dragonwell(알리바바), Microsoft, Oracle, Temurin(이클립스) 등등등 수많은 Vendor의 자바목록이 보입니다. 우측의 Identifier를 입력해 원하는 버전을 설치 할 수 있습니다.

이전의 AdoptOpenJDK가 Termurin 으로 리브랜딩을 하였는데요 저는 해당 버전을 설치 해 보겠습니다.

```zsh
sdk install java 17.0.2-tem
```

![image-20220324150041725](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20220324150041725.png)

설치 후에 자바 버전을 확인 해 보면

```zsh
java --version
```

![image-20220324150133845](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20220324150133845.png)

openjdk 17 버전으로 자바 설치가 잘 된 것이 확인 됩니다.

이어서 JDK8 버전도 설치 해 보겠습니다.

```zsh
sdk install java 8.0.322-tem
```

![image-20220324150335093](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20220324150335093.png)

![image-20220324150358592](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20220324150358592.png)

> 설치가 완료 되면 JDK 8.0.322 버전을 기본 자바로 설정할건지 물어보는데요, Y를 눌러줬습니다.

이제 자바 목록을 확인 해 보겠습니다.

```zsh
sdk list java
```

![image-20220324150607944](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20220324150607944.png)

> Temurin 8.0.322와 17.0.2가 설치 되어 있으며, 8 버전이 Default로 표시 되어 있습니다.

자바 버전 변경은 간단하게 아래의 명령어로 가능합니다.

```zsh
sdk use java 17.0.2-tem
```

![image-20220324150731317](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20220324150731317.png)

> 자바 버전이 손쉽게 변경됩니다. 하지만 Using java version 17.0.2-tem in this shell 에 나오는 것 처럼 지금의 shell 에서만 버전이 변경되었고, 새로 shell을 띄운다면 여전히 JDK 1.8을 사용 중입니다.

모든 Shell에서 동일하게 변경 하고 싶다면, default 명령을 사용 해야 합니다.

```zsh
sdk default java 17.0.2-tem
```

![image-20220324153033532](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20220324153033532.png)

> 기본 자바 버전이 변경되었습니다.

### LibreOffice

Linux의 Microsoft Office 입니다. 무료로 사용 할 수 있습니다.

![image-20211125144106550](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20211125144106550.png)

> https://www.libreoffice.org/download/download/

위의 링크에서 원하는 버전을 다운 받습니다.

다운을 받은 후에는 압축을 풀어 줍니다.

```zsh
tar -xvf LibreOffice_7.2.2_Linux_x86-64_deb.tar.gz
```

![image-20211125144209993](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20211125144209993.png)

압축이 풀렸으면 DEBS 내의 모든 `.deb`파일을 설치 해 줍니다.

```zsh
sudo dpkg -i *.deb
```

![image-20211125144336873](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20211125144336873.png)

잠시 기다리면 모든 설치가 완료됩니다.

![image-20211125144421314](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20211125144421314.png)

### zsh와 oh-my-zsh

bash 보다는 zsh가 많이 쓰이는 추세입니다.

> [Ubuntu에 oh-my-zsh 설치](https://shanepark.tistory.com/248)

### VLC Media Player

![image-20210920114245540](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20210920114245540.png)

요즘에는 동영상 파일을 직접 재생할 일이 잘 없기는 하지만 전혀 없진 않아서 필요 할 떄가 있습니다.

디자인이 다소 투박해 보이지만 호환성 면에서는 존재하는 미디어 플레이어 중 최고로, Linux, MacOS, Windows는 물론 심지어 Android 나 iOS에서도 돌아갈 정도로 포팅이 잘 되어있고 자체 코덱을 내장하여 코덱을 따로 받을 필요도 없습니다.

```bash
sudo apt install vlc
```

### Kolourpaint

![KolourPaint screenshot.png](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/300px-KolourPaint_screenshot.png)

Linux 에서의  Microsoft Paint (그림판) 입니다. 간단한 이미지 편집을 하기 위해 사용합니다.

```bash
 $ sudo apt-get install kolourpaint4<br>
```

## 선택 설정

### open 명령어로 nautilus 실행

아래의 명령어로 터미널에서 현재 작업중인 폴더를 탐색 할 수 있는데요

```zsh
nautilus .
```

nautilus 를 항상 입력하는건 귀찮으니 open 명령어로 alias를 지정 해 줍니다.

아래의 내용을 zsh 사용중이라면 `~/.zshrc`에 bash라면 `~/.bashrc`에 등록 해 줍니다.

```zsh
alias open="nautilus"
```

### SSH 접속 허용

![text](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/photo-1629654297299-c8506221ca97ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1000&q=80)

다른 컴퓨터에서도 접속 하고 싶다면 SSH 접속을 허용 해줍니다. 아래의 링크를 참고해주세요.

> [Ubuntu 20.04 LTS ) SSH 접속 허용하기](https://shanepark.tistory.com/239)

### Dock 커스터마이징

> 저는 키보드 설정 후에는 보통 dock을 입맛에 맞게 설정합니다. 아래 글을 참고해주세요. 
>
> 테마를 전부 macOS 처럼 바꾸고 싶다면 이번건 넘기고, 바로 아래의 MacOS 테마 입히기를 하시면 됩니다.

[Ubuntu 20.04 Mac OS 처럼 Dock 가운데 위치하게 변경하기](https://shanepark.tistory.com/233)

### MacOS 테마 입히기

![ubuntu](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/ubuntu.png)

개인적으로 macOS의 환경을 좋아해서 macOS 처럼 만들어두고 사용하고 있습니다. 아래 링크를 따라하면 어렵지 않게 가능합니다.

>  [Ubuntu MacOS 처럼 만들기 GTK Themes](https://shanepark.tistory.com/251)

### 상단 바 없애기

모니터가 크고 해상도가 넓으면 크게 상관 없겠지만 공간 활용을 최대한 하기 위해 상단 bar와 하단 dock을 모두 없애고 사용 하고 있습니다. 필요하시면 하시면 됩니다.

일단 Gnome-tweaks 가 없다면 먼저 설치 해 주어야 합니다.

```bash
$ sudo apt install gnome-tweaks
```

그러고 나서 topbar를 없앨 수 있는 extention을 설치 해 줍니다.

```bash
sudo apt install gnome-shell-extension-autohidetopbar
```

그러고 나서 재부팅을 하고 나서 Tweaks 를 실행해주면 아래와 같이 Extentions 에 hide top bar 옵션이 추가 된 것이 확인 됩니다.

![image-20211001144534045](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20211001144534045.png)

이 설정을 켜주면 이제 상단이 밀릴때 상단 바가 안으로 들어가는데요,

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/autohide-top-panel.gif)

> ref : https://fostips.com/auto-hide-top-panel-debian-ubuntu-fedora/

평소에 숨어있다가 마우스를 올릴 때만 나오게 하려면 아래와 같이 설정 하면 됩니다.

![image-20211001145441964](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20211001145441964.png)

가장 위에 있는 Show Panel when mouse approaches edge of the screen 옵션을 켜주고 가장 아래의 Intellihide 에 있는 두개의 옵션을 꺼주면 됩니다.

### 창 분할 - ShellTile

Gnome의 기본 창 분할도 사용하는데 꽤나 만족스러웠습니다만, 피벗 모니터에서 상/하로 창을 분할하지 못하는게 가장 큰 약점이었습니다. ShellTile Extention을 설치 하면 창분할을 굉장히 직관적이고 편하게 할 수 있습니다.

필수에 넣을까 고민 할 정도로 꼭 설치해야 한다고 생각하지만, 필요성을 먼저 느낀 후 설치하는것도 괜찮다고 봅니다.

![tiling windows](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/window_tiling.gif)

> https://github.com/emasab/shelltile

설치 방법은 간단합니다. 아래의 링크에 들어가서 ON으로 사용 여부를 토글 해 주면 설치가 금방 끝납니다.

![image-20220216153315020](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20220216153315020.png)

>  https://extensions.gnome.org/extension/657/shelltile/

설치 후에는 재부팅 없이 바로 기능을 사용 할 수 있습니다. Ctrl 키를 누른 상태로 창을 드래그 하면 창을 내려 둘 때 어디에 붙을지 블럭으로 표시되며 아래의 키보드 단축키를 이용해서도 창을 분할 할 수 있습니다.

- `Ctrl` `Super` `Left` : Tile to the left border
- `Ctrl` `Super` `Right` : Tile to the right border
- `Ctrl` `Super` `Up` : Tile to the top border
- `Ctrl` `Super` `Down` : Tile to the bottom border

방향키 두개를 동시에 눌러서 코너에 창을 위치 시키는 것도 가능 합니다.

### Notification Banner Position

알림 배너 위치를 커스터마이징 해주는 Extention 입니다.

Gnome을 사용하며 불편했던 것 중 하나가 한가운데에 푸시 알림이 오는 거였는데 한창 작업중일땐 꽤나 성가시기도 합니다. MacOS에서 처럼 우측 상단에 뜨도록 변경하기 위해 필요합니다.

GNOME EXTENTION에서 스위치를 ON으로 돌리는 것으로 간단하게 설치 합니다.

![image-20220323155051437](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20220323155051437.png)

> https://extensions.gnome.org/extension/4105/notification-banner-position/

![image-20220323155142403](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20220323155142403.png)

Install 을 눌러주면 설치는 바로 끝납니다.

![image-20220323155228357](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20220323155228357.png)

설치 후 Tweaks 를 켜서 Extensions에 보면 Notification banner position이 설치 되어 활성화 되어 있는게 확인 됩니다. 이제 Notification 이 오면

![image-20220323155409439](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20220323155409439.png)

> 우측 상단에 알림이 뜹니다. 

### 노트북 지문인식 등록

![round black and white light](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/photo-1585079374502-415f8516dcc3ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1000&q=80)

>  필요한 분만 하면 됩니다. 솔직히 굳이 하지 않아도 되는 기능이긴 합니다. 아래 링크를 참고해주세요.

[Ubuntu 20.04 Dell XPS 노트북 지문인식 로그인하기](https://shanepark.tistory.com/232)

### 바탕화면 아이콘 숨기기

지극히 개인적인 취향 이지만, 저는 바탕화면에 홈과 휴지통이 있는걸 좋아하지 않습니다. 휴지통은 dock에 달아서 사용합니다.

```zsh
gsettings set org.gnome.shell.extensions.desktop-icons show-trash false
gsettings set org.gnome.shell.extensions.desktop-icons show-home false
```

### Fusuma 설치

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/img.gif)

데스크탑이 아닌 노트북 사용자 라던가, 혹은 별도의 터치패드를 연결해서 사용한다면 터치 제스처를 다양하게 사용해 MacOS 만큼 생산성을 높일 수 있는 fusuma 라는 어플리케이션이 있습니다.

내용이 다소 간단하지만은 않아서 따로 포스팅을 작성 하였습니다. 아래 링크를 참고해주세요.

> [Ubuntu) 터치패드 및 트랙패드 활용하기 Fusuma](https://shanepark.tistory.com/257)

### 마우스 버튼 커스터마이징

로지텍사의 MX-master 처럼 버튼이 많은 마우스를 사용 한다면 버튼별로 기능을 따로 부여 해서 사용는데요, windows나 MacOS는 제조사에서 유틸성 프로그램을 제공해 주기 때문에 그냥 사용하면 되지만, 리눅스용으로는 거의 없습니다.

직접 버튼들을 하나하나 맵핑해서 사용해 줘야 하는데요, 제 마우스는 단순하게 버튼 5개 짜리지만 그래도 버튼들을 모두 사용하기 위해 설정을 해 두었습니다. 내용이 길어져 아래 링크에 따로 정리 해 두었습니다.

> [Ubuntu) 마우스 버튼 커스터마이징](https://shanepark.tistory.com/301)

### Airpod 연결

에어팟을 비롯한 Bluetooth 장비를 연결 할 때 도움이 되었으면 합니다.

처음에 블루투스가 기본 딸려 오길래 연결이 될 줄 알았는데 그냥은 연결이 안되더라고요.

아래 링크를 참고해서 연결 해 주세요.

> [Ubuntu) Airpod 연결 설정 하기](https://shanepark.tistory.com/346)

## 선택 설치

### Google Chrome

FireFox가 정말 좋긴 하지만, 개발할때 Chrome이 없으면 곤란합니다.. 사실 필수 설치로 보내야 하는 항목 입니다.

> https://www.google.com/intl/ko/chrome/

### Github Desktop

한동안 Linux에서는 쓸만한 Git GUI 클라이언트가 없다고 생각했었습니다. 원래는 GitKraken을 추천했었는데 사실상 유료가 되어 더이상 추천하지 않고, 그 외에 SmartGit이나 GitCola 등도 사용 해 보았었는데요.

그러다가 오픈소스인 기존의 Github Desktop을 포크 해서 만든 리눅스용 버전을 사용 해 보았는데 굉장히 만족 스러워서 계속 사용하고 있습니다. 개발자인 ShiftKey(Brendan Forster)도 Github의 엔지니어기 때문에 충분히 신뢰 할 수 있습니다. 다만 공식적으로 지원하는 버전은 아닙니다.

OAuth로 로그인 하는 대신 SSH 방식으로 저장소를 클론 하고 사용하면 회사에서 사용중인 저장소들도 무리없이 좋은 보안으로 사용 할 수 있기 때문에 추천합니다. 

Github Desktop의 설치는 아래 링크를 확인 해 주세요.

> [Ubuntu 20.04 우분투 Github Desktop 설치하기](https://shanepark.tistory.com/252)	

### Postman

![Using Variables and Chaining Requests in Postman - Vonage Developer Blog](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/Blog_Postman2_1200x600.png)

API 테스트를 편하게 할 수 있습니다.

**snap**(비추천)

```bash
sudo snap install postman
```

 사실 snap으로 설치하는게 쉽기는 하지만, 개인적으로 어떤 어플이든 작동이 굉장히 느려져서 현재는 snap으로 설치된 모든 어플들을 다 수동으로 설치해 사용하고 있습니다. 수동으로 설치하신다면..

1. 일단 먼저 https://www.postman.com/downloads/ 에서 다운받습니다. Linux 64-bit를 받으면 되겠네요.

   ![image-20211105155753299](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20211105155753299.png)

2. 다운받은 파일을 opt 폴더에 압축 해제합니다.

   ```zsh
   sudo tar -zxvf  ./Postman-linux-x86_64-8.12.5.tar.gz -C /opt/
   ```

3. 바로 가기 파일을 생성 해 줍니다.

   ```zsh
   vi ~/.local/share/applications/Postman.desktop
   ```

4. Postman.desktop 에는 아래의 내용을 넣습니다.

   ```properties
   [Desktop Entry]
   Encoding=UTF-8
   Name=Postman
   Exec=/opt/Postman/app/Postman %U
   Icon=/opt/Postman/app/resources/app/assets/icon.png
   Terminal=false
   Type=Application
   Categories=Development;
   
   ```

### Visual Studio Code

![image-20211123223857894](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20211123223857894.png)

간단히 메모장 용도로 사용하기에도 훌륭할 만큼 가볍습니다. `.deb` 파일을 다운 받아서 설치 하시면 쉽습니다.

> https://code.visualstudio.com/download

```zsh
sudo dpkg -i 파일명.deb
```

### Albert

![Untitled](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/Untitled.png)

MacOS에서의 Alfred를 정확히 따라하는 Albert 입니다. 이름부터가 노리고 지은걸 알 수 있습니다.

아래의 링크에 설치 방법이 나와 있습니다.

> https://albertlauncher.github.io/installing/

위의 링크에 나온 순서대로 설치를 진행 하겠습니다.

```zsh
curl "https://build.opensuse.org/projects/home:manuelschneid3r/public_key" | sudo apt-key add -
```

![Untitled](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/Untitled-7674177.png)

아래는 Ubuntu 20.04 기준의 설치 방법 입니다.

```zsh
echo 'deb http://download.opensuse.org/repositories/home:/manuelschneid3r/xUbuntu_20.04/ /' | sudo tee /etc/apt/sources.list.d/home:manuelschneid3r.list
curl -fsSL https://download.opensuse.org/repositories/home:manuelschneid3r/xUbuntu_20.04/Release.key | gpg --dearmor | sudo tee /etc/apt/trusted.gpg.d/home_manuelschneid3r.gpg > /dev/null
sudo apt update
sudo apt install albert

```

혹시 다른 버전을 사용 하신다면, 아래 링크를 확인하셔서 본인에게 알맞는 버전의 설치방법을 확인 해 주세요.

> https://software.opensuse.org/download.html?project=home:manuelschneid3r&package=albert

![Untitled2](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/Untitled2.png)

설치 된 후엔, 그냥 검색하면 아무것도 나오지 않기 때문에 Applications 를 체크 해야 합니다. 위에선 Files를 체크 했지만 FIles를 체크 하면 굉장히 불편하기 때문에 Applications랑 Calculator만 체크 하고 사용하시길 권장 합니다.

![image-20211124155400697](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20211124155400697.png)

General 설정에서 단축키로도 `Alt + Space`를 설정 해서 MacOS에서 Spotlight나 Alfred 쓰듯 사용 하면 됩니다. `Autostart on log`을 반드시 체크 해주세요. 그렇지 않으면 컴퓨터를 새로 켤 때 마다 일일히 Albert를 실행 해 주어야 합니다.

Extentions > WebSearch 에 등록을 해 두면 간편하게 네이버 검색, 카카오 지도 검색등도 가능합니다.

![image-20220324101106703](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20220324101106703.png)

카카오 지도 검색

```
https://map.kakao.com/?q=%s
```

네이버 검색

```
https://search.naver.com/search.naver?query=%s
```

한번 이렇게 등록 해 두고 이후 호출할 때는 

![image-20220324101225404](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20220324101225404.png)

등록해둔 Trigger 와 함께 검색어를 입력 하면 해당 명령이 호출 됩니다.

![image-20220324101304632](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20220324101304632.png)

> `kmap 대전맛집` 결과 대전 맛집을 카카오 지도에서 검색 합니다.

굉장히 편하기 때문에 몇개 등록하고 사용하시길 추천합니다.

### Docker

![Empowering App Development for Developers | Docker](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/docker_facebook_share.png)

Windows 에서 Ubuntu 로 넘어오게 된 가장 큰 계기입니다. Docker를 사용하면 정말 편하게 격리된 컨테이너들을 구성해 가상화의 장점을 살릴 수 있습니다. 사실상 업계 표준인 만큼 접근성이 높으며 사용에 굉장히 편리합니다. 관련 레퍼런스도 어렵지 않게 찾아 볼 수 있으며 사용자들이 작성해 둔 패키지/이미지들이 넘쳐나기 때문에 뭔가를 정말 간단하게 할 수 있습니다. 윈도우즈에서도 WSL2(Windows Subsystem for Linux)를 이용해 사용은 가능 했지만 메모리나 안정성 문제로 불편함이 있었습니다.

글이 길어져 링크를 나누었습니다. 아래 글을 확인해주세요.

> [Ubuntu 20.04 LTS ) Docker 설치하기](https://shanepark.tistory.com/237)

### JetBrains Toolbox

저는 IntelliJ IDEA 만 사용 하는데도, 종종 버전 문제로 번거로울 때가 있어 Mac에서도 Linux에서도 Toolbox를 설치해두고 사용합니다.

새로운 버전이 나왔다고 신나서 새 버전 깔았다가 기존의 플러그인들이 전부 죽어버리는 사태를 몇번 겪었는데 아마 다들 공감하실거에요.

![image-20220321165133106](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20220321165133106.png) 

> https://www.jetbrains.com/toolbox-app/

위의 링크에서 tar.gz 파일을 다운로드 합니다.

그 다음에는 압축을 풀어 줍니다.

```zsh
tar -xf jetbrains-toolbox-*
```

압축을 푼 뒤에는 그냥 압축이 풀린 파일을 실행 하면 설치가 됩니다.

압축이 풀린 폴더로 이동 후 `./jetbrains-toolbox`를 입력해 줍니다.

![image-20220321165428244](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20220321165428244.png)

반응이 꽤나 오래 없어서 설치가 안되는 건가 했는데

![image-20220321165628401](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20220321165628401.png)

잠시 기다리니 잘 뜹니다. Toolbox를 한번 사용 해 보면 계속 사용하게 되니 안써보셨다면 한번 써보는걸 권장합니다.

### IntelliJ IDEA 

![IntelliJ IDEA 2021.2 Release Candidate Is Out! | The IntelliJ IDEA Blog](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/BlogFeatured_IntelliJ-IDEA-2x-2400x1350.png)

위의  ToolBox를 설치하는걸 권장하지만, IntelliJ IDEA만 설치를 원하는 경우도 있으니 함께 올려둡니다.

저는 처음에는 snap 으로 설치 했었습니다.

```shell
sudo snap install intellij-idea-ultimate --classic
```

그런데 snap으로 설치하면 사용할때 이상하게도 로딩도 너무 느리고 사용하기가 불편 하더라고요.

![image-20211021084558418](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20211021084558418.png)

그래서 삭제 뒤 위에 안내된 것 처럼 공식 홈페이지에 나온 방법 대로 설치 해서 사용해보니, MacOS에서 경험했던 훌륭한 속도 그대로  잘 사용 하고 있습니다.

IntelliJ IDEA 다운로드 링크도 첨부 해 둡니다.

![image-20211021084925005](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20211021084925005.png)

> https://www.jetbrains.com/idea/download/#section=linux

### Eclipse

STS 4 를 설치할 경우 아래의 글을 참고해주세요.

>  [Ubuntu) STS4 (Spring Tools Suite 4 for Elipse) 설치하고 바로 가기 만들기](https://shanepark.tistory.com/236)

### DBeaver

![GitHub - dbeaver/dbeaver: Free universal database tool and SQL client](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/f3f5c080-808b-11ea-9713-2bea65875d95.png)

모든 데이터베이스를 한가지 클라이언트 만으로 관리 할 수 있으니 정말 편리합니다.

![image-20211123221145003](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20211123221145003.png)

> https://dbeaver.io/download/

Linux Debian package를 다운 받아서 설치 하면 됩니다.

dpkg 로 설치해 주시면 됩니다. 개인적인 취향 차이일 수 있지만 저는 snap은 최대한 지양합니다.

```zsh
dpkg -i ~/Downloads/dbeaver-ce_21.2.5_amd64.deb
```

### Typora

![image-20211105163432374](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20211105163432374.png)

마크다운 에디터로 Typora를 사용하고 있습니다.

 여러가지 마크다운 에디터를 사용 해 봤지만, 블로그 글을 작성하는데는 Typora가 가장 좋았습니다.

일단 공식 사이트에서 안내하는 방법은 아래와 같습니다.

```zsh
# or use
# sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys BA300B7755AFCFAE
wget -qO - https://typora.io/linux/public-key.asc | sudo apt-key add -

# add Typora's repository
sudo add-apt-repository 'deb https://typora.io/linux ./'
sudo apt-get update

# install typora
sudo apt-get install typora

```

하지만 제가 다운로드를 시도 해보니 key가 만료되어서 저장소에 접속이 되지 않았습니다.

> 글을 수정하며 확인해보니 지금은 위의 방법으로 설치가 되니 그대로 진행 해주세요.

![image-20211105163031170](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20211105163031170.png)

> https://typora.io/#linux

그래도 다행히도 download binary file 링크가 있어서 binary file을 다운 받아 설치 할 수 있었습니다. apt-get을 위와 같이 먼저 시도 해 보시고 안된다면 binary file을 다운 받아서 설치 하시면 됩니다.

다운로드 링크 : https://typora.io/linux/Typora-linux-x64.tar.gz

해당 파일을 다운 받으면 bin이라는 이름의 폴더가 생깁니다. 그 아래에  `Typora-linux-x64` 폴더가 있는데요, 해당 폴더를 `/opt` 경로에 풀어 주었습니다.

그러고는 아래와 같이 바로가기를 생성 해 주었습니다.

```zsh
vi ~/.local/share/applications/Typora.desktop
```

```properties
  1 [Desktop Entry]
  2 Encoding=UTF-8
  3 Name=Typora
  4 Exec=/opt/Typora-linux-x64/Typora %U
  5 Icon=/opt/Typora-linux-x64/resources/assets/icon/icon_128x128.png
  6 Terminal=false
  7 Type=Application
  8 Categories=Development;

```

유료화가 되긴 하였지만, 여전히 베타버전을 사용 할 수 있고 돈을 내고 쓰기에도 충분히 값어치를 하기 때문에 보다 자세한 정보를 확인하고 싶으면 아래 링크로 이동해주세요.

> [Typora 정식 버전 오픈 소식](https://shanepark.tistory.com/287)

### Notion 설치

![image-20211027092241360](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20211027092241360.png)

Notion에서 공식 우분투용 프로그램을 지원하지는 않지만, 감사하게도 개발자들이 힘을 모아 만든 Lotion 프로젝트 덕에 Notion을 native 앱으로 사용 할 수 있습니다. Notion 사용자라면, 아래의 링크에서 설치 방법을 확인 해 주세요.

> [Ubuntu) notion App 설치](https://shanepark.tistory.com/265)

### Spotify

![green and white logo illustration](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/photo-1611339555312-e607c8352fd7ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1000&q=80)

Spotify를 구독 중이라면 설치해주세요. Linux를 공식 지원하기 때문에 굉장히 좋습니다.

**snap**

```bash
$ sudo snap install spotify
```

**.deb**

```bash
curl -sS https://download.spotify.com/debian/pubkey_0D811D58.gpg | sudo apt-key add - 
echo "deb http://repository.spotify.com stable non-free" | sudo tee /etc/apt/sources.list.d/spotify.list
sudo apt-get update && sudo apt-get install spotify-client

```

### Apple Music(Cider)

![image-20220323153954522](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/initial.assets/image-20220323153954522-20220323213045396.png) 

Apple Music은 Linux를 정식 지원하지는 않지만, Cider라는 오픈 소스가 정말 훌륭한 UI/UX로 MacOS에서나 iOS에서의 애플 뮤직 경험 그대로 혹은 그 이상으로 사용 하실 수 있습니다. 

애플 뮤직이 6인 가족 플랜 사용시 1인당 2천원대 금액으로 가성비도 훌륭하기 때문에 iPhone을 사용하시는 분들은 고려해보세요.

관련 포스팅은 아래 링크를 확인 해 주세요.

> [Linux) 리눅스에서 Apple Music 듣기 Cider App](https://shanepark.tistory.com/347)

## The end

앞으로도 초기 설정에 더 필요한 내용이 있다고 생각될 때는 본 글을 꾸준히 업데이트 하도록 하겠습니다.

최근 개발 머신을 한번 날려먹은 덕에 글을 통째로 개선하며, 스스로 불편했던 부분들을 모두 개선 하였습니다.

질문이 있거나 불편함이 있는 부분은 편하게 피드백 해주시면 수시간 내 답글로 도와드리겠습니다. 감사합니다. 

# Ubuntu) 우분투에서 카카오톡 실행 (100% 정상작동 방법)

![image-20220129171723784](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129171723784.png)

## Intro

Windows나 Mac에서 되는데 Linux에서는 할 수 없는거야 셀수 없이 많지만 왠만한거는 다 괜찮습니다. 어딘가에 대체할 만한 소프트웨어가 다 있기 마련이며 크리티컬 한 소프트웨어 일수록 사용 가능 할 확률이 높습니다. 심지어 Third Party 소프트웨어들이 정말 많기 때문에 저는 Notion, Apple music, iCloud Drive, Github Desktop 등 공식 지원 하지 않는 소프트웨어들을 사용 하고 있으며 심지어 intelliJ IDEA, Visual Studio Code, Postman, Dropbox, Typora, Slack 등은 제조사에서 Linux를 공식적으로 지원을 해 줍니다.

하지만 그중 하나 한국인이라면 모두가 사용하지만 리눅스에서 사용 할 수 없는 소프트웨어가 있으니 그건 바로 카카오톡 입니다. 카카오톡은 Slack등의 사내 협업/메신저 프로그램이 따로 존재하지 않는다면 업무에서도 사용해야 할 정도로 한국인들에게는 필수 프로그램인데 Linux에서는 10년이 지나도 지원할 가능성이 0%에 수렴한다고 생각합니다. 당장 Mac 에서도 사용자들이 만들어서 쓰다가 한참 후에야 공식 버전이 나왔지만, Apple Silicon은 1년이 지나도 지원할 생각을 하지 않습니다.

> 필요한 소프트웨어들의 설치에 대해서는 [개발자를 위한 Ubuntu 필수 설치와 설정](https://shanepark.tistory.com/242) 포스팅을 참고해주세요.

사실 저는 그냥 핸드폰으로 카톡을 하는 방법으로 어찌어찌 해왔는데 가끔 스크린샷 파일을 전송 해야 하거나 업무중에도 카카오톡으로 소통 할 일이 있을 때에는 핸드폰을 들고 하는게 불편하기도 하고 업무중에 핸드폰을 사용하는게 효율도 굉장히 떨어진다는 생각이 들어서 필요할 때를 대비해 설치를 하긴 해야 겠다고 생각하고 있었습니다. 

인터넷에 나온 정보들을 참고 하니 막히는 것도 많고 오래되어 안맞는 정보가 많아서 그냥 안해야겠다고 생각했었는데,  이번에 9일간의 긴 연휴 기간이 되어서 한번 끝장을 보자는 마음가짐으로 이렇게 카카오톡 설치를 시도 했고 설치에 성공 할 수 있었습니다. 

지금부터 하나씩 잘 따라 하신다면 복잡하기는 해도 카카오톡을 잘 설치하실 수 있을거라고 생각됩니다. 

## Wine 설치

wine 패키지는 기본 우분투 리포지터리에 포함되어 있기 때문에 쉽게 설치 할 수 있습니다.

> ref: https://linuxize.com/post/how-to-install-wine-on-ubuntu-20-04/

### 설정

많은 윈도우 어플리케이션이 32비트 아키텍처로 개발되어있기 때문에 제일 먼저 64 bit Ubuntu 에서 32비트 아키텍처를 사용 할 수 있도록 설정 해 주어야 합니다.

```zsh
sudo dpkg --add-architecture i386
sudo apt update
```

![image-20220129153344039](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129153344039.png)

이제 와인을 설치를 위해 key 파일을 추가 합니다.

```zsh
wget -nc https://dl.winehq.org/wine-builds/winehq.key
sudo apt-key add winehq.key
```

저는 apt-key add 하는 과정에서 아래의 에러가 발생 했는데요

![image-20220129153845805](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129153845805.png)

> gpg: invalid key resource URL '/tmp/apt-key-gpghome.7h9nMv8Jp1/home:manuelschneid3r.asc.gpg'

home_manuelschneid3r.gpg가 Albert를 설치하는 과정에서 추가 했던 키 인데, 문제가 있다고 하네요.

문제가 있는 키를 제거 해 줍니다.

![image-20220129154039702](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129154039702.png)

> 이제 키를 추가 할 때 OK가 나옵니다.

### 다운로드 및 설치

이제 정말 Wine을 설치 해 줍니다.

![image-20220129154203576](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129154203576.png)

> https://wiki.winehq.org/Ubuntu

Ubuntu를 사용하지 않으셔도, 다른 배포판에 해당하는 wine 버전들이 있으니 맞는 버전으로 설치 하시면 됩니다.

저는 Ubuntu 20.04를 사용 하고 있기 때문에

```zsh
sudo add-apt-repository 'deb https://dl.winehq.org/wine-builds/ubuntu/ focal main'
```

를 입력해서 wine 저장소를 추가 해 주고

```zsh
sudo apt update
sudo apt install --install-recommends winehq-stable
sudo apt install playonlinux
```

를 입력해 wine 및 playonlinux를 설치 해 줍니다.

### unmet dependencies 해결

이번엔 저는 또 다른 에러가 발생했습니다. 대부분은 잘 되었겠지만 저와 같은 에러가 났을 분들을 위해 해결 방법을 함께 남겨 둡니다. 에러가 발생하지 않은 분들은 아래의 `Wine & PlayOnLinux 설치`로 쭉 내려가주세요.

> ref: https://askubuntu.com/questions/140246/how-do-i-resolve-unmet-dependencies-after-adding-a-ppa

![image-20220129154455046](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129154455046.png)

> Some packages could not be installed. This may mean that you have
> requested an impossible situation or if you are using the unstable
> distribution that some required packages have not yet been created
> or been moved out of Incoming.
> The following information may help to resolve the situation:

자세히 읽어 보니 hirsute 라는 단어가 보입니다.

Ubuntu 20.04에 맞는 focal main 저장소를 추가 했어야 하는데, 실수로 21.04 버전에 맞는 hirsute main 저장소를 추가 했기 때문 이었습니다.

이럴때는 focal 저장소를 제대로 추가 한 후에

```zsh
sudo add-apt-repository 'deb https://dl.winehq.org/wine-builds/ubuntu/ focal main'
```

저장소 목록 파일을 열어서

```zsh
sudo vi /etc/apt/sources.list
```

![image-20220129160932801](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129160932801.png)

아래쪽에 focal을 남겨 두고 위에 있는 hirsute 를 제거 해 줍니다.

설정을 바꾼 후에는 update 해 줍니다.

```zsh
sudo apt update && sudo apt upgrade
```

### Wine & PlayOnLinux 설치

![image-20220129161103854](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129161103854.png)

>  무려 1,953 MB 나 합니다. 엔터키를 입력 해 설치 해 줍니다. 용량이 용량이니 만큼 시간이 제법 걸립니다.

이어서 `sudo apt install playonlinux` 도 하는데, 75.8MB 라서 금방 설치 합니다.

## 환경 설정

### wine 버전 설치

PlayOnLinux 를 실행 해 줍니다.

![image-20220129161312060](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129161312060.png)

Manage Wine versions를 클릭 합니다.

![image-20220129161716231](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129161716231.png)

> 우측의 Tools 에서 첫번째 메뉴에 있습니다.

![image-20220129162135178](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129162135178.png)

그러면 이렇게 사용 가능한 와인 버전들이 나오는데요, 처음 wine을 설치한 직후에 목록이 나오지 않아 재부팅을 한번 했습니다. 그런데 후에도 패키지가 안보일때가 종종 있는걸로 봐서는 그냥 자체 버그가 있는 것 같습니다. 껐다 키면 나오더라고요.

여러가지 버전이 나오는데 7.0은 아직 출시되기 직전이라고 해도 배포 전이기 때문에 선택하지 않았습니다. 7.0 버전을 사용 하셔도 되지만 제가 사용을 해보지 않았기 때문에 어떤 문제가 있는지는 모릅니다.

저는 처음에 6.18-staging으로 시도 했다가 실패해서 6.14-staging 으로 다시 했습니다. 스샷이 처음 찍었던거라 6.18인게 몇개 포함 되어 있을 수 있지만, 6.18을 하면 나중에 로그인이 되지 않기 때문에 꼭 **6.14-staging**을 선택해주세요. 선택 하고 오른쪽 화살표를 클릭 하면 설치 합니다.

![image-20220129162339315](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129162339315.png)

> 다운을 받는데 속도가 굉장히 느려서 꽤 오래 걸립니다.

이왕 오래걸리는거 미리 PC 카카오톡을 다운 받습니다. Windows 버전을 받으면 됩니다.

![image-20220129162516111](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129162516111.png)

> https://www.kakaocorp.com/page/service/service/KakaoTalk

다운 받고 나서도 한참 시간이 남을 정도로 오래 걸리기 때문에 다른일을 하고 계시면 됩니다. 저는 5~10분 정도 걸렸습니다.

![image-20220129170905659](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129170905659.png)

> 정말 오래걸려 설치 되었습니다. 다시 한번 말씀드리지만, **6.14-staging** 버전을 선택하셔야 합니다.

### KakaoTalk 설치

![image-20220129163538854](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129163538854.png)

> 이제 좌측의 Install a program 을 클릭 하고

![image-20220129163643998](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129163643998.png)

> 좌측 하단의 Install a non-listed program을 클릭 합니다.

![image-20220129163709900](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129163709900.png)

처음에 설명이 나오는데요, 설치 폴더를 변경 하지 말라는 안내를 해줍니다. 윈도우를 재부팅 하라고 안내를 받으면 Yes를 누르지만, 호스트 시스템을 재부팅 할 필요는 없다고 하네요.

![image-20220129163839857](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129163839857.png)

> Next 누르고

![image-20220129163852374](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129163852374.png)

> 위에 있는 메뉴를 선택 합니다.

![image-20220129163915403](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129163915403.png)

> 어플리케이션의 이름을 적으라고 하는데, 원하는 이름을 적어 줍니다.

![image-20220129163948896](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129163948896.png)

> 선택 화면이 나오면 세개 모두 선택 해 줍니다.

![image-20220129171006142](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129171006142.png)

> 아까 설치한 와인 버전을 선택 합니다.

![image-20220129164024704](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129164024704.png)

> 32 bit 를 선택 합니다.

![image-20220129164100971](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129164100971.png)

> Mono 패키지가 필요하다고 하니 설치 해 줍니다.

![image-20220129164143752](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129164143752.png)

> 와인 설정 화면이 나오면 Windows 10을 선택 해 줍니다.

![image-20220129164248694](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129164248694.png)

> Libraires 에서 셀렉트바 목록을 펼쳐 `d3dx11_43`를 찾아 추가 해 줍니다.

이제 OK를 누르고 다음에 나오는 화면에서 아래의 총 4개를 선택 해줍니다.

> POL_install_로 시작하는 d3dx11, gdiplus, gecko, mono28

![image-20220129164443640](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129164443640.png)

![image-20220129164501167](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129164501167.png)

![image-20220129164552143](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129164552143.png)

모두 선택 했으면 Next를 클릭 합니다.

![image-20220129164748534](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129164748534.png)

> Gecko 파일이 네트워크에서 missing 이라 설치를 못했습니다. 일단 Next를 클릭 해 진행합니다. 
>
> 이후 실행 에 문제가 생긴다면 Gecko를 의심 하려고 했는데, 이렇게 에러가 떠도 아무 문제 없이 실행이 됩니다.

![image-20220129164859086](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129164859086.png)

이제 위의 화면이 나오면, Browse 를 클릭하고 아까 다운받아놓은 PC버전 카카오톡 설치파일을 실행 합니다.

![image-20220129165009348](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129165009348.png)

> 원하는 언어를 선택 하고 계속 진행 해 줍니다. 
>
> 저는 개발환경에서는 항상 언어는 영어로 해두는데요, 이렇게 해도 한글로 채팅하고, 한글 사용자명이 나오거나 하는데 아무런 문제가 없습니다.

![image-20220129165054592](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129165054592.png)

설치가 완료 되었습니다. Run KakaoTalk 는 체크 하지 말고 Finish 해 줍니다.

![image-20220129165147783](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129165147783.png)

> 카카오톡 Shrtcut을 만들기 위해 선택 해 줍니다.

![image-20220129165208997](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129165208997.png)

카카오톡이 설치 되었습니다. 바로 실행 해 봅니다.

![image-20220129165232847](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129165232847.png)

![image-20220129165341380](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129165341380.png)

> 로그인시 50151 에러가 뜬다면 Wine 버전을 변경 하고, 위의 wine 버전 설치 부분 부터 다시 해줍니다. 저는 처음에 6.18 버전으로 시도 했다가 실패해서 6.14 staging 버전으로 다시 했습니다. 50151 에러는 wine 버전을 변경하지 않으면 고쳐지지 않습니다.
>
> 로그인시 50114 에러가 뜬다면 `RECV_SOCKET_ERROR(err_code=336130329) (Error Code: 50114) FriendList. or LOCO protoco` 그때는 로그인 될 때 까지 다시 로그인 해주시면 됩니다. 재부팅 하거나 프로그램을 새로 실행해서 해결했다는 분들이 있는데 그냥 운 같습니다. 한번 로그인 하면서 Keep me logged in 을 해두면 그다음 부터는 따로 로그인 할 필요가 없습니다. 저는 50114 에러는 한번도 뜨지 않았는데 워낙 흔하다고 해서 함께 올려두었습니다.

이제 버전을 잘 맞췄다면 로그인이 잘 됩니다.

![image-20220129171723784](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129171723784.png)

> 정상적으로 로그인 됩니다! 

그런데 한글 폰트가 깨져서 채팅을 입력할 때 제가 입력한 메시지가 잘 보이지가 않습니다. 

![image-20220129172035762](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129172035762.png)

![image-20220129172047646](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129172047646.png)

> 입력 할 때는 한글이 깨져 보이지만 Send를 누르면 정상적으로 보내지긴 하더라고요.

### Font 변경

이때는 해당 폰트가 없기 때문인데요, 폰트를 변경하면 해결 됩니다. Settings -> Display -> Font에 있습니다.

![image-20220129172349753](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129172349753.png)

일단 제가 자주 사용하는 D2 Coding 포트로 변경 해 보았습니다. 코딩 전용 폰트기 때문에 일반적인 사용을 하기에는 딱히 가독성이 좋지는 않지만 한글이 잘 나오는지 일단 확인해 보기 위해 선택 했습니다.

![image-20220129172512596](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129172512596.png)

> 이제 한글 입력을 할 때 잘 보입니다. 다른 Font들도 테스트를 해 보았는데 JetBrains 나 Apple의 샌프란시스코 폰트 등을 사용 해 보았을때는 한글이 깨졌습니다.

### Wine 기본 폰트 변경

D2 Coding Font가 없거나 근본적인 해결을 원하시는 분들을 위해

일단 첫번째로 ~/.wine/drive_c/windows/Fonts 폴더에 원하는 한글 폰트를 넣습니다. 저는 맑은고딕 폰트를 다운받아 넣어 뒀습니다.

폴더에 넣는것 뿐만 아니라 Ubuntu 자체에서도 해당 폰트를 Install 해주셔야 합니다. 우측 상단 버튼을 눌러 설치합니다.

![image-20220404100747297](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220404100747297.png)

> 해당 폰트를 다운 받았습니다. 위에 보이는 Malgun Gothic 이라고 표시되는 폰트 명이 나중에 필요합니다.

![image-20220404100045028](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220404100045028.png)

> ~/.wine/drive_c/windows/Fonts 경로에 폰트 파일을 넣어두었습니다.

후에는 레지스트를 변경 해 줍니다. `~/.wine/system.reg` 파일 입니다.

![image-20220404100148531](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220404100148531.png)

> 변경 전

65911 라인과 65912 라인에 보이는 `MS Shell Dlg`를 변경 해 주어야 합니다. 둘다 Tahoma로 되어 있는데 위에서 확인했던 `Malgun Gothic` 으로 변경 하였습니다.

![image-20220404100258814](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220404100258814.png)

> 변경 후

적용 후에는 저는 재부팅을 한번 했었는데, wine 재시작 등이 필요하기 때문에 최소 로그오프는 해야 할 것 같습니다.

이렇게 하고 나서 카카오톡을 다시 켜면 더이상 한글이 깨지는 곳이 없습니다.

![image-20220404101622502](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220404101622502.png)

> 기본 폰트를 Malgun Gothic으로 설정한 모습

### 입력기

> iBus나 fcitx 등에서는 입력기 문제가 아마 있을 것으로 추정 되는데 그 경우에는 KIME나 tian 혹은 nimf 입력기를 설치하시기를 권장합니다. 제가 사용중인 KIME 에서는 한글자 한글자가 완성 되어야 보이기는 했지만 그래도 사용하는데 불편함은 없었습니다. KIME 입력기 설치는 아래의 링크를 참고해주세요.
>
> [Linux) KIME 한글 입력기](https://shanepark.tistory.com/318)

![image-20220129172938162](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129172938162.png)

> 심지어는 기대도 하지 않았는데, 드래그앤드롭으로 사진 파일 첨부까지 가능 했습니다. 

![image-20220129173431680](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129173431680.png)

> 보이스톡 / 영상통화도 가능한데, wine에서 카메라나 마이크 리소스를 접근하지 못해서 상대방의 영상, 음성만 보고 들을 수 있었습니다. 필요하면 몇가지 설정을 추가로 하면 이것도 가능 할 거 같긴 했지만 사용하지 않아 시도하지 않았습니다.

이정도면 보통의 카카오톡 사용에 필요한건 100% 동일하게 사용 가능하다고 판단됩니다.

### 바로가기 이동

![image-20220129180515432](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129180515432.png)

바로가기 파일이 떡하니 Desktop에 추가 되어 있는데, 저는 Desktop에 아이콘을 두고 사용하지 않기 때문에 파일을 이동 시키겠습니다.

어플리케이션 바로가기들은 `~/.local/share/applications/` 에 추가해주면 됩니다.

```zsh
mv ~/Desktop/KakaoTalk.desktop ~/.local/share/applications/
```

이동이 잘 되었는지 확인 해 보겠습니다.

![image-20220129180809784](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129180809784.png)

> KakaoTalk 이 어플리케이션 목록에 추가 되었습니다.

어플리케이션 목록에 추가 후에는 Dock에 고정 할 수 있습니다.

![image-20220129181738675](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129181738675.png)

#### * 주의사항

![image-20220223112542261](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220223112542261.png)

다만, 무작정 `KakaoTalk.desktop` 파일을 옮겼다가는 위와같은 불상사가 일어 날 수도 있습니다.

StartupWMClass 를 잘 맞춰 줘야 하는데요. WMClass를 확인하는 방법은 두가지가 있습니다.

1. Terminal 을 켜고

```zsh
xprop WM_CLASS
```

를 입력 후, 마우스가 십자 표시가 되면 실행중인 어플리케이션을 클릭해 해당 프로그램의 WM_CLASS를 확인한다.

![image-20220223112735972](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220223112735972.png)

> 그러면 위에서 보이는 것 처럼 WM_CLASS 를 확인 할 수 있습니다.

2. 또 다른 방법으로는 `Alt + F2` 키 입력 후, lg 라고 입력해 엔터키를 칩니다. 그 후에 윈도우가 뜰때 우측 상단의 Windows를 클릭 하면

   ![image-20220223112822629](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220223112822629.png)

실행중인 어플리케이션들의 wmclass를 한번에 확인 할 수 있습니다.

![image-20220223112906300](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220223112906300.png)

확인 후에는 KakaoTalk.desktop 파일의 StartupWMClass 변수를 거기 맞춰 변경해 줍니다. 저는 기존에 WMClass가 `Kakaotalk.exe`로 작성되어 있어서 문제가 있었습니다. 사용자마다 다를 수 있기 때문에 확인해주세요.

## TopIcon Plugin

마지막으로 하나의 플러그인을 추가하겠습니다.

![image-20220129173829524](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129173829524.png)

> https://extensions.gnome.org/extension/1031/topicons/

OFF 를 ON으로 돌리면 간단하게 설치 됩니다.

설치 후에 Tweak을 실행 하면

![image-20220129174033048](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129174033048.png)

> Topicons plus가 추가 되어 있습니다.

여기에서 톱니바퀴 아이콘을 눌러 상세설정을 할 수 있습니다.

![image-20220129174211426](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129174211426.png)

> 다른건 손대지 않고 Tray horizontal alignment만 Right로 변경 해 주었습니다.

![image-20220129174247074](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129174247074.png)

이제 우측 상단에 카카오톡 트레이 아이콘이 생겼습니다. 그러면 이제 카카오톡을 X 버튼으로 최소화 해도

![image-20220129174325830](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kakaoTalk.assets/image-20220129174325830.png)

> 다시 클릭 해서 원상 복귀 할 수 있기 때문에 정말 편합니다.

## 글 마침

이상으로 우분투에 카카오톡 설치하는 방법에 대해 알아보았습니다.

굳이 업무에서 카카오톡을 쓸 일이 많지는 않아서 계속 미뤄 왔는데, 긴 연휴를 이용해서 끝장을 보자는 각오로 시작 했고 생각보다는 어렵지 않게 금방 설치에 성공 했습니다.

기대했던 것 보다 훨씬 우분투에서 정상적으로 작동하며 모든 기능을 사용 가능하기 때문에, 다소 과정이 길기는 하지만 차근차근 따라하신다면 설치에 성공해서 유용하게 사용 하실 수 있을 거라 생각합니다.




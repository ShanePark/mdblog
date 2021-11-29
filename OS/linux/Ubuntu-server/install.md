# Ubuntu Server 20.04 설치하기

## Intro

집에서 서버로 사용중이던 우분투 노트북이 있습니다. 처음에 설치할 때 CLI의 압박으로 Ubuntu Desktop으로 설치를 해 두었었는데, 어차피 ssh로만 접속해서 사용하는데 굳이 Ubuntu Desktop으로 이용할 필요가 없다는 생각이 들었습니다.

사실 Ubuntu Desktop과 Ubuntu Server는 커널이 동일하다고 하는데요, 그래도 우분투 서버로 설치해보고 어떤 차이가 있는지 직접 살펴보고 싶어 우분투 서버를 설치하기로 했습니다. 

시작에 앞서 `~/.ssh/authorized_keys` 파일과, 서버에 띄워두었던 모든 도커 컨테이너를 이미지로 만들어 tar파일로 저장 해서 백업 해 두었습니다.

혹시 도커 컨테이너 백업이 필요한 분은 https://shanepark.tistory.com/285 를 참고해주세요.

## 설치 디스크 생성

### Ubuntu 서버 이미지 다운로드

모든 우분투 릴리즈는 아래의 링크에서 다운 받을 수 있습니다.

https://releases.ubuntu.com

![image-20211129203010989](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/image-20211129203010989.png)

> server 이미지를 다운 받습니다.

속도가 썩 빠르지는 않지만, 용량이 애초에 1.26GB로 매우 작기 때문에 굳이 다른 mirror 를 찾지 않고 받을 만 합니다.

![image-20211129203223945](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/image-20211129203223945.png)

### 부팅 USB 준비

저는 집에 있는 USB가 이전에 친구 ubuntu 21.04를 설치 해 준다고 우분투 데스크탑용으로 만들어 두었기 때문에 포맷이 한번 필요 합니다.

![image-20211129203837537](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/image-20211129203837537.png)

> Disk Utility를 실행 합니다. Spotlight에 Disk Utility 치면 나옵니다.

우측 상단의 Erase를 누릅니다.

![image-20211129204022616](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/image-20211129204022616.png)

> FAT32 로 포맷을 해줍니다. Erase 버튼을 눌러줍니다.

![image-20211129204110919](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/image-20211129204110919.png)

> 포맷이 금방 되었습니다.

![image-20211129204124946](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/image-20211129204124946.png)

> 16GB의 깨끗한 디스크가 되었습니다.

### 부팅 USB 생성

Windows 에서는 Rufus 를 이용해서 쉽게 만들었었습니다. 지금은 MacOS로 작업을 할 것이기 때문에 balenaEtcher를 이용하겠습니다.

> Windows를 사용 해야 하는 분은, [우분투 Ubuntu 설치 USB 만들기 및 windows 멀티부팅 셋팅](https://shanepark.tistory.com/229#설치-usb-만들기) 에서 Rufus 관련 내용을 보고 Rufus를 설치 해 주세요. 참고로 해당 글은 Ubuntu Desktop 설치에 대한 글 입니다.

공식 홈페이지에서 다운 받아도 되지만,

![image-20211129203620461](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/image-20211129203620461.png) 

> https://www.balena.io/etcher

저희는 모두 brew가 있기 때문에 brew를 통해 설치하겠습니다.

```zsh
brew install balenaetcher
```

![image-20211129203654144](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/image-20211129203654144.png)

![image-20211129203719924](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/image-20211129203719924.png)

금방 다운로드가 완료 되고, 실행 합니다.

![image-20211129204149703](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/image-20211129204149703.png)

> Flash from file을 클릭합니다.

![image-20211129204207582](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/image-20211129204207582.png)

> 방금 받은 Ubuntu Server를 선택합니다.

![image-20211129204233187](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/image-20211129204233187.png)



> 이번에는 Select target을 누릅니다.

![image-20211129204249168](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/image-20211129204249168.png)

> 부팅USB로 만들 디스크를 선택합니다.

![image-20211129204303164](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/image-20211129204303164.png)

> Flash!를 누르면 바로 부팅  USB를 만들어 줍니다.

![image-20211129204337456](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/image-20211129204337456.png)

저희 집에 있던 USB가 3.0도 지원 안하는 엄청난 구식이었기 때문에 속도가 굉장히 느린데요, 보통은 1분 내외면 완료됩니다.



![image-20211129204759284](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/image-20211129204759284.png)

> Flash Complete!

![image-20211129204827236](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/image-20211129204827236.png)

위와 같은 에러가 뜨는 이유는 새로 만든  FAT32 형태를 MacOS 가 지원하지 않기 때문입니다. 그냥 Ignore 해서 무시 해 주시면 됩니다.

## Ubuntu Server 설치

지금부터는 스크린샷의 상태가 좋지 않으니 양해 바랍니다. 화면을 찍는건 제가 정말 좋아하지 않지만.. 가상 머신에 설치한게 아니고 집에 있는 노트북에 직접 설치하다 보니 이렇게 되었습니다..ㅜㅜ

### Bios 설정

![IMG_3847](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/IMG_3847.jpeg)

> 설치에 앞서 Bios에서 Boot Priority를 USB 우선으로 변경 해 줍니다. 지금의 Boot Priority 상태에서 당연히 위에 있는 Ubuntu 가 설치 디스크인줄 알고 계속 껐다 켜도 기존의 Ubuntu Desktop으로 부팅이 되어서, 혹시나 하고 순서를 바꿔보니.. UEFI:Sandisk 가 USB더라구요.. USB와 SSD의 제조사가 같으면 이렇게 혼란스럽습니다..

### 설치 시작

![IMG_3848](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/IMG_3848.jpeg)

> USB로 부팅 합니다. Install ubuntu Server를 선택해줍니다.

### 언어 설정

![IMG_3849](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/IMG_3849.jpeg)

![IMG_3850](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/IMG_3850.jpeg)

> 언어 선택 및 키보드 레이아웃은 그냥 영어로 해줍니다.

### Network 설정

![IMG_3851](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/IMG_3851.jpeg)

> Network 설정을 해 주는데요, 저는 wifi 로 연결 하겠습니다.

![IMG_3852](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/IMG_3852.jpeg)

> Choose a visible network 를 선택하면 와이파이 목록이 나옵니다. 비밀번호를 입력 해 주면 됩니다.
>
> 와이파이 비밀번호를 입력 하고 연결 될 때까지 조금 기다려야 다음으로 넘어가는 버튼이 활성화 됩니다.

![IMG_3854](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/IMG_3854.jpeg)

> Wifi 에 필요한 패키지들을 설치한다는 안내가 나옵니다.

![IMG_3855](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/IMG_3855.jpeg)

> Proxy 설정이 필요하다면 Proxy 설정을 해 주고, 아니면 그냥 넘어갑니다.

![IMG_3856](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/IMG_3856.jpeg)

> Ubuntu Mirror를 변경하려면 변경 해 줍니다. kakao 미러가 있긴 한데, 원한다면 조금 더 빨라지긴 하니 바꿔줍니다.

```zsh
kr.archive.ubuntu.com/mirror.kakao.com
```

저는 변경하지 않고 그대로 하겠습니다.

### Storage 설정

Storage 설정을 해 줍니다. 저는 기존에 ubuntu 설치 되어 있던게 있어서 포맷 해 주고 새로 파티션을 할당 하였습니다.

![IMG_3857](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/IMG_3857.jpeg)

![IMG_3858](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/IMG_3858.jpeg)

![IMG_3859](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/IMG_3859.jpeg)

![IMG_3864](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/IMG_3864.jpeg)

![IMG_3861](/Users/shane/Desktop/untitled folder/IMG_3863.jpeg)

![IMG_3861](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/IMG_3861.jpeg)

각자 본인이 원하시는 대로 셋팅 해 주시면 됩니다. 포맷도 파티션 할당도 많이 해 봤지만 Ubuntu server에서는 처음 해 보는데, 왠지 낯설어서 한눈에 들어오지 않아 조금 헤맸습니다.

### Profile 설정

이제 아이디와 비밀번호, 그리고 서버 이름 등을 설정 해 줍니다.

![IMG_3862](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/IMG_3862.jpeg)

### SSH Setup

![IMG_3865](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/IMG_3865.jpeg)

> SSH 설정을 해 두면 Github 에서 SSH key들을 불러올 수 있더라고요. `~/.ssh/authorized_keys`를 일부러 백업 해 두고 시작 했는데.. 제가 사용하는 공개키들을 Github에 설정을 저장 해 두면 되겠네요. 어쨌든 지금은 준비된게 없어 No를 합니다..

![IMG_3866](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/IMG_3866.jpeg)

> Server snap 을 선택 하라고 합니다. 확실히 Ubuntu Server가 이런 면에서 잘 되있다는걸 느꼈습니다. docker를 선택 할 까 하다가 어떤 환경으로 스냅이 되어 있을 지 몰라서 일단 스냅을 선택 하지 않고 진행 하였습니다.

![IMG_3867](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/IMG_3867.jpeg)

> 부지런히 설치가 됩니다.

![IMG_3869](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/IMG_3869.jpeg)

> 설치가 완료 되면 Reboot Now를 해 줍니다.

![IMG_3870](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/IMG_3870.jpeg)

> 설치가 완료되었습니다. 로그인도 잘 되네요!

## 서버 접속

맥북에서 접속 해 봅니다.

![image-20211129212140767](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/install.assets/image-20211129212140767.png)

아주 문제 없이 잘 연결이 되었습니다. 백업 해 두었던 authorized_keys 파일을 옮기니 비밀번호 없이 로그인도 바로 되었으며, docker를 설치 해서 백업해둔 도커 이미지들을 run 하니 바로 도커들도 기존과 동일하게 실행 되었습니다.

## 마치며

Ubuntu를 처음 사용 해 볼 때는 GUI의 낮은 진입을 적극 활용했지만, 이제 조금은 익숙 해 진 후에는 확실히 Ubuntu Server를 통해 셋팅 하니 서버 설정에 필요한 패키지들이 이미 많이 준비가 되어 있고, 많은 설정이 이미 되어 있기 때문에 아주 간단하게 셋팅할 수 있었습니다.

처음에는 서버 설정하는게 너무 어려웠고, 며칠에 걸려 간신히 하나의 서버를 셋팅 한 뒤에 다시 설정할 일이 없기를 바라곤 했었는데 그래도 몇번 날려먹고 새로 만들고 하다 보니 점점 더 쉬운 방법을 알게 되고, 조금은 더 익숙하게 할 수 있게 되는 듯 합니다.

모두 즐거운 개발 되세요!

 
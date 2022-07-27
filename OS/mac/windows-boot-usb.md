# MacOS 에서 Windows11 설치 USB 만들기

## Intro

집에 새로운 노트북이 생겼습니다. 사실 저랑 와이프는 둘다 맥북을 사용하고 있고, 회사에서는 리눅스 (우분투) 환경에서 개발을 하고 있다 보니, 일년에 한번정도 있을 법한 아주 가끔씩 윈도우가 필요할 때는 제법 곤란한 상황에 놓이곤 했습니다.

예전에는 집에 남던 노트북 하나가 있었지만, 약 1년 전부터 우분투 서버를 설치해두고 홈 서버로 운영을 하고있다보니 어쩌다 저희 집은 Windows Free Zone 이 되어 있었습니다.

새로 생긴 노트북에 또 우분투를 올려서 서브 컴퓨터로 쓸지, 우분투 서버를 올려서 여분의 서버로 쓸지 고민했는데 그러기에는 새로 온 컴퓨터의 성능이 너무 좋기 때문에 일단 윈도우를 올려 사용 해 보기로 했습니다.

예전에는 항상 윈도우 설치하는 USB도 필통에 넣고 다녔었는데 언제부턴간 해당 USB도 우분투 설치용 USB가 되어 버렸기에 오랜만에 새로 윈도우 설치용 USB를 만들어 보려고 합니다. 이왕 하는거 한번도 안써본 Windows 11로 하겠습니다.

## Windows 11 ISO 다운로드

https://www.microsoft.com/software-download/windows11 에서 적당한 ISO 파일을 다운 받습니다.

![image-20220727193325339](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/windows-boot-usb.assets/image-20220727193325339.png)

> Windows 11 을 선택 하고, 언어는 한국어를 선택 한 뒤에 64-bit Download 를 클릭 해 다운받아 줍니다.

## Terminal 활용해 USB 만들기

> 이전에는 Boot Camp 를 이용해서 윈도우 설치 USB를 만들 수 있었지만, 애플이 인텔에서 애플 실리콘으로 넘어오며 이용할 수 없게 되었습니다. 대신 터미널 환경에서 부팅가능한 USB를 만들 수 있기 때문에 그 방법을 이용 해 보겠습니다.

Windows와 macOS 모두에서 작동하는 유일한 포맷은 Fat 32인데, 해당 포맷의 드라이브에서는 4GB 이상의 파일을 만들 수가 없습니다. 

### wimlib 설치

그 문제를 해결 하기 위해, 설치 파일을 작은 파일들로 나누어야 하는데요, 그것을 위해서 wimlib 라는 패키지를 설치해야 합니다. wimlib는 homebrew를 통해 설치하기 때문에 제일 먼저 Homebrew 가 없는 분은 아래의 링크를 확인해 homebrew를 먼저 설치 해 줍니다.

> [Mac 에 brew 설치하기](https://shanepark.tistory.com/45)

이제 brew가 준비되었으면 아래의 명령어로 간단하게 wimlib을 설치 해 줍니다.

```bash
brew install wimlib
```

![image-20220727193912324](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/windows-boot-usb.assets/image-20220727193912324.png)

> 설치가 완료되었습니다.

### 드라이브 포맷

이제 USB가 연결 되었으면, 아래의 명령어를 실행 해 연결된 드라이브들을 확인 해 줍니다.

```bash
diskutil list
```

![image-20220727193957811](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/windows-boot-usb.assets/image-20220727193957811.png)

저는` /dev/disk4` 에 있는 UBUNTU 20_0 dlfksms dlfmadml 30.8GB 짜리 디스크가 이번에 사용할 USB 입니다.

아래의 명령어를 실행 해 디스크를 포맷하고, WINDOWS11 이라는 이름으로 변경 해 줍니다. 저는 아래에 disk4 라고 써 있지만, 각자 본인의 USB 드라이브에 해당하는 번호로 변경 해 주시면 됩니다.

```bash
diskutil eraseDisk MS-DOS WINDOWS11 GPT /dev/disk4
```

![image-20220727194140057](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/windows-boot-usb.assets/image-20220727194140057.png)

> 진행 중

완료되기 까지는 5분 정도 이상이 걸린 듯 합니다.

![image-20220727194609601](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/windows-boot-usb.assets/image-20220727194609601.png)

> 완료

### 설치 파일 준비

이제 미리 다운받은 Windows 11 ISO 파일을 마운트 해 줍니다.

![image-20220727194301263](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/windows-boot-usb.assets/image-20220727194301263.png)

> 다운받은 `Win11_Korean_x64v1.iso` 파일을 더블클릭 해서 마운트 해 줍니다.

![image-20220727194351136](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/windows-boot-usb.assets/image-20220727194351136.png)

> 바탕화면에 **CCCOMA_X64FRE_KO-Rf_DV9** 라는 이름으로 마운트된게 보입니다.

설치 파일이 4GB 를 넘기 때문에 설치파일 생성을 2개의 커맨드로 나눌 것 입니다.

아래의 명령어를 입력 해 첫번째 내용을 기록 해 줍니다. CCCOMA_X64FRE_KO-KR_DV9 는 선택한 언어가 다르면 다를 수 있기 때문에 정확한 이름을 입력 해 주세요.

```bash
 rsync -vha --exclude=sources/install.wim /Volumes/CCCOMA_X64FRE_KO-KR_DV9/* /Volumes/WINDOWS11
```

![image-20220727194824117](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/windows-boot-usb.assets/image-20220727194824117.png)

> 1번 작업 끝

이어서 2번 작업도 진행 해 줍니다.

```bash
wimlib-imagex split /Volumes/CCCOMA_X64FRE_KO-KR_DV9/sources/install.wim /Volumes/WINDOWS11/sources/install.swm 3000
```

![image-20220727194909588](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/windows-boot-usb.assets/image-20220727194909588.png)

> 2번 작업은 시간이 꽤 걸립니다.

인고의 시간을 거치면..

![image-20220727195533630](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/windows-boot-usb.assets/image-20220727195533630.png)

부팅 가능 드라이브가 만들어졌습니다!

## 윈도우 설치

이제 새로 설치할 컴퓨터를 켜고, 방금 만든 USB를 통해 윈도우를 설치 해 줍니다.

![Elite 8300 BIOS settings to not attempt USB boot.jpg](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/windows-boot-usb.assets/large.jpeg)

> https://h30434.www3.hp.com/t5/Business-PCs-Workstations-and-Point-of-Sale-Systems/Cannot-disable-boot-on-HP-Compaq-Elite-8300/td-p/7947154

제일 먼저 해야 할 일은, 바이오스에 들어가서 부팅 순서를 변경 해 줘야 합니다. 이걸 안해주면 아무리 껐다 켜도 USB로 부팅이 되지 않습니다. 

![USB windows 10 setup installer, Can't use mouse or click "next" on setup  screen - Microsoft Q&A](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/windows-boot-usb.assets/149034-win-20211112-15-48-46-pro.jpg)

> https://docs.microsoft.com/en-us/answers/questions/625875/usb-windows-10-setup-installer-can39t-use-mouse-or.html

마침내 이런 비슷한 화면이 나오면서 윈도우 설치가 가능합니다.!

### 마치며

다만, 몇몇 컴퓨터에서는 USB는 GPT 형식이고 SSD는 MBR 형식이기 때문에 `새 파티션을 만들거나 기존파티션을 찾을 수 없습니다.` 라는 에러가 발생 할 수 있습니다. 사실 이때는 diskpart 로 포맷을 하고 파티션을 부여 해도 안되기 때문에 어쩔 수 없이 Windows 에서 간편하게 설치 USB를 만드는게 좋습니다.

이상입니다.


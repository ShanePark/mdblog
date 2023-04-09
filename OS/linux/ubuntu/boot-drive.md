# [Ubuntu] 우분투에서 우분투 설치 USB 드라이브 만들기

## Intro

전에는 MacOS 및 Windows 에서 우분투 설치 드라이브를 만들었었는데, 이번에 우분투 설치 드라이브를 만들어 야 할 일이 생겼는데 사용중인 컴퓨터가 마침 우분투라서 그 과정을 정리 해 보았습니다.

윈도우나 맥북에서 했던 것 보다 훨씬 쉽고 간단하게 생성이 가능했습니다.

## 1. 우분투 다운로드

https://ubuntu.com/download/desktop 링크에 접속해 원하는 버전의 우분투를 다운 받습니다.

<img src="https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/boot-drive.assets/image-20221229100830293.png" width=750 height=600 alt=down> 

저는 Ubuntu 22.04.1 LTS Desktop 버전을 다운 받았습니다.

서버용도로 사용할 게 아니라면 Desktop 버전을 다운 받으면 됩니다.

<img src="https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/boot-drive.assets/image-20221229100927783.png" height=550 width=750 alt=size>

> 다운받은 파일은 총 3.6 GB 입니다.

## 2. Startup Disk Creator 설치

부팅 가능한 설치 디스크를 생성 하기 위해서 해당 소프트웨어를 설치 합니다. 

```bash
sudo apt install usb-creator-gtk
```

![image-20221229101017373](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/boot-drive.assets/image-20221229101017373.png)

## 3. 설치 드라이브 생성

설치 드라이브 생성을 위해 방금 설치한 소프트웨어를 실행 해 줍니다.

![image-20221229101106996](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/boot-drive.assets/image-20221229101106996.png)

실행하면 알아서 `.iso` 파일과 USB 드라이브를 감지 해서 선택 해 주는데 정말 간편합니다. USB는 미리 포맷을 해 두지 않아도 알아서 해 줍니다.

혹시 자동으로 적당한 파일이 선택이 되지 않았다면, Other 를 눌러 선택 해 주도록 합니다.

![image-20221229101219255](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/boot-drive.assets/image-20221229101219255.png)

생성 정보를 확인 후 `Make Startup Disk` 를 클릭 하면

![image-20221229101309764](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/boot-drive.assets/image-20221229101309764.png)

> USB 드라이브의 모든 파일이 삭제된다는 경고가 나옵니다. Yes를 눌러 진행 합니다.

USB 드라이브의 속도에 따라 걸리는 시간은 다른데, 제 개인 USB3 지원 드라이브로 했을 때에는 5분 정도 걸렸지만 좋지 않은 USB 드라이브로 했을 때에는 20분 가까이 걸렸습니다. 읽기/쓰기 속도가 느리면 생성 할 때 뿐만 아니라 나중에 우분투를 설치 할 때에 마찬가지로 오래 걸립니다.

![image-20221229101847270](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/boot-drive.assets/image-20221229101847270.png)

> 진행중...

조금 기다리면 완료가 됩니다.

![image-20221229101937894](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/boot-drive.assets/image-20221229101937894.png)

![image-20221229102018033](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/boot-drive.assets/image-20221229102018033.png)

> 총 3.8 GB의 용량을 사용하니 USB는 넉넉하게 8GB 정도는 준비하는게 좋을 것 같습니다.

이후 USB를 이용한 설치 과정은 [링크](https://shanepark.tistory.com/230) 를 참고 해 주세요.

이상입니다. 
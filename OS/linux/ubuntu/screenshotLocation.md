# Ubuntu 20.04) 스크린샷 저장 폴더 변경

## Intro

우분투에서 스크린샷을 찍을때 기본 저장 폴더는 `/home/사용자/Pictures` 입니다. 하지만 해당 경로는 스크린샷을 찍을 때 외에는 들어갈 일이 없기 때문에 관리하기가 번거로운데요. 스크린샷 저장 폴더를 원하는 폴더로 변경 하고자 합니다.

특히 저는 Dropbox에 바로 저장하도록 해서 스크린샷을 찍었을 때 다른 디바이스와 빠르게 공유하며 작업 하는데 스크린샷의 공유에 들어가는 번거로운 작업들이 줄어들어 매우 만족스럽습니다.

## Screenshot Locations

### 설치

아래의 링크에서 Screenshot Locations 라는 GNOME Extention을 설치하면 매우 편하게 필요할 때 마다 스크린샷의 저장 경로를 변경 할 수 있습니다.

![image-20220125093534017](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/screenshotLocation.assets/image-20220125093534017.png)

> https://extensions.gnome.org/extension/1179/screenshot-locations/

설치 후에는 Tweaks에서..

### Tweaks

tweaks가 없다면 먼저 설치가 필요합니다.

```zsh
sudo apt install gnome-tweaks
```

tweaks를 설치 한 후에 재부팅이 필요한지 아니면 Extention을 설치 한 후에 재부팅이 필요한지 기억이 가물가물 한데.. 혹시 중간에 보여야 할 메뉴가 보이지 않는다면 로그아웃이나 재부팅을 한번 해주시면 됩니다.

![image-20220125093619405](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/screenshotLocation.assets/image-20220125093619405.png)

> Tweaks를 실행 해 줍니다.

![image-20220125093635302](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/screenshotLocation.assets/image-20220125093635302.png)

> Tweaks > Extentions에 Screenshot locations 가 생겼습니다.

체크박스를 클릭해서 사용 하도록 해주고, 옆의 톱니바퀴 버튼을 클릭해 설정 화면으로 들어갑니다.

![image-20220125093933332](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/screenshotLocation.assets/image-20220125093933332.png)

> 셀렉트 바를 선택해 스크린샷 경로를 설정 할 수 있습니다.

![image-20220125093926204](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/screenshotLocation.assets/image-20220125093926204.png)

> 저는 Dropbox 에 자동 저장되는 경로에 screenshot 폴더를 생성 해서, 스크린샷 저장 경로로 만들어 두었습니다.

### 마치며

정말 간단하게 이게 전부 입니다. 변경 즉시 스크린샷 키를 입력 해 보면 새로 설정한 폴더로 스크린샷이 저장되는 것을 확인 할 수 있으며, 저처럼 Dropbox 경로로 설정 한 경우에는 스크린샷을 찍자 마자 바로 드랍박스에 해당 파일이 업로드 됩니다.

이상입니다.
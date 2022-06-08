# Ubuntu) Display Link 활용한 모니터추가 연결

## Intro

그래픽카드 스펙에 따라, 모든 컴퓨터는 스스로가 최대로 연결 할 수 있는 외장 모니터 갯수의 제한을 가지고 있습니다. 회사에서 지급받아 사용하고 있는 Dell XPS 15 시리즈는 매뉴얼에 따르면, Dell에서 나온 도킹 스테이션을 사용하지 않으면 최대 3개의 모니터까지 연결을 할 수 있다고 적혀 있습니다. 내장 디스플레이를 사용하지 않는 조건이 달리기 때문에, 실제 내장모니터 포함 최대 3개의 화면을 사용 할 수 있습니다. 이정도면 그래도 부족함이 없는 편 입니다.

![image-20220128173048404](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/displaylink.assets/image-20220128173048404.png)

하지만 저는 개인적으로 퇴근 후에는 집에서 M1 맥북 에어를 사용 하고 있는데요. m1 시리즈는 최대 연결할 수 있는 외장 디스플레이가 딱 1개 입니다. 이게 정말 치명적인게. 13인치 디스플레이는 실질적으로 사용하기에 매우 불편하기 때문에 디스플레이 한개로 코딩을 하게 되는데. 특히 웹 개발할때는 너무 불편합니다. 그래서 저는 아이패드를 추가로 연결해서 작은 디스플레이 2개와 큰 디스플레이 한개로 하곤 했었는데 그럼에도 불편함이 완전 해소 되지는 않았습니다.

## Display Link

그런 문제를 해결 해주는게 바로 DIsplay Link 입니다. 여러개의 디스플레이를 소프트웨어적으로 하나의 컴퓨터에 추가로 연결 하는 방법을 사용했으며, 심지어는 USB 뿐만 아니라, 이더넷이나 와이파이를 통해서도 연결 하는 기술을 가지고 있는 회사 입니다.

![DisplayLink Plug and Play](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/displaylink.assets/1.png)

특정 허브에 이런 마크가 있다면, 해당 허브가 Display Link를 지원 한다는 의미입니다.

그래서 저도 m1 맥북 에어에 여러개의 디스플레이를 연결 하기 위해서 DisplayLink를 지원 하는 장비들을 찾아 보았으며, 그중 Belkin 사의 제품이 마음에 들어 구입을 해서 사용 하고 있습니다.

![image-20220128174126229](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/displaylink.assets/image-20220128174126229.png)

가격은 제법 비싼 편 이지만, USB 3.0 포트 3개, HDMI 포트 2개, LAN 케이블 까지 있으며 최대 135W(랩탑에는 85W) 의 충전기까지 포함 하고 있기 때문에 충분히 그 값어치를 한다고 봅니다.

![1bbdaa72-e204-4cf6-abaa-8e08e2c9cf75](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/displaylink.assets/1bbdaa72-e204-4cf6-abaa-8e08e2c9cf75.jpg)

> 해당 제품을 사용해  내장 디스플레이 포함 총 4개의 디스플레이를 사용 하고 있습니다.

마침 설 명절 9일동안 집에만 있게 되어 이것 저것 작업을 해 보려고 회사 컴퓨터를 집에 가져왔습니다. 가져온 김에 사용중인 Display Link에 연결을 해서 모니터 4개를 사용하기 위해 드라이버 설치를 해 보겠습니다.

## 설치

### 다운로드

![image-20220128180418117](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/displaylink.assets/image-20220128180418117.png)

> https://www.synaptics.com/products/displaylink-graphics/downloads

공식 사이트에서 해당하는 인스톨러를 다운 받아 줍니다. Ubuntu도 지원을 당당하게 해 주고 있습니다.

![image-20220128180507664](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/displaylink.assets/image-20220128180507664.png)

5.5 베타 버전과 5.2.1 정식 버전이 릴리즈 되어 있는데요. 선택을 위해 Release Note를 읽어 보았습니다.

```
C.3  Changes in 5.5 release
-----------------------------
- Added support for kernel 5.14 and 5.15
- Added preliminary support for kernel 5.16
- Community fixes for evdi kernel module
- DL-6xxx series: adding RTL8211FD-VX and RTL8211E Ethernet PHYs (WS1597)
- DL-41xx: improved firmware flashing time, visible from following update onwards

C.2  Changes in 5.4.1 release
-----------------------------
- Added support for kernel 5.13
- Added beta support for kernel 5.14 beta (33194)
- DL-6xxx series: improved 5120x1440 - 5K Super Ultrawide resolution selection (33186)
- Improved performance: added support for damage regions interface from Linux DRM interface (26378)
- Improved performance in Wayland session with AMD GPU machines (33151)
- DL-6xxx series: fixed blinking mouse cursor while moving across displays (32987)
```

기능적인 추가 보다는 새로운 커널 버전에 대응을 하는 패치 입니다. 

```bash
uname -a
```

![image-20220128180738851](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/displaylink.assets/image-20220128180738851.png)

제 커널 버전을 확인 해 보니 5.13 이라서, 굳이 Beta를 사용 하지 않고 정식 릴리즈 버전을 다운 받습니다.

![image-20220128180923496](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/displaylink.assets/image-20220128180923496.png)

> 다운로드 속도가 끔직할 정도로 느려서 용량이 작은게 천만 다행이었습니다.

### 설치

![image-20220128181004460](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/displaylink.assets/image-20220128181004460.png)

> 다운 받은 압축 파일을 확인 해 보니 .run 파일이 하나 딸랑 있습니다. 압축을 풀어 줍니다.

![image-20220128181100783](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/displaylink.assets/image-20220128181100783.png)

> 파일을 확인 해 보니 설치하는 스크립트가 들어 있습니다.

아래 명령어를 해당 파일이 있는 곳에서 실행 해 줍니다. sudo 권한이 필요하더라고요.

```bash
sudo sh ./displaylink-driver-5.4.1-55.174.run
```

![image-20220128181257870](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/displaylink.assets/image-20220128181257870.png)

> Y 로 동의합니다.

![image-20220128181346530](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/displaylink.assets/image-20220128181346530.png)

> 설치 후에는 Reboot이 필요 합니다.

### 완료

컴퓨터를 재 시작 한 후에 확인 해보면

![image-20220128181717870](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/displaylink.assets/image-20220128181717870.png)

Display Link로 연결 한 2개의 디스플레이를 포함한 총 4개의 디스플레이가 연결이 되었습니다. 개인적으로 모니터는 최소 3개 이상은 있어야 답답하지가 않더라고요. 크기보다는 몇대인지가 더 중요하다고 생각합니다. 물론 워크스페이스를 잘 사용하면 1개로도 잘 하는 분들은 많이 있던데 저는 모니터가 적으면 너무 답답하더라고요..

이상으로 Display Link를 우분투에서 활용하는 방법에 대해 알아 보았습니다.

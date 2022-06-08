# Ubuntu Server) 우분투 서버 와이파이 설정

## intro

집 와이파이 비밀번호를 변경한지가 오래 되어서 비밀번호를 오랜만에 변경 해 주었습니다. 

그런데 집에서 운영중인 우분투 서버 랩탑 컴퓨터가 와이파이로 연결이 되어 있다보니, 바로 연결이 끊어지고 말았습니다.

 집에있는 다른 모바일 디바이스들은 모두 GUI 환경이라 그냥 와이파이 선택 하고 바뀐 비밀번호를 입력 하면 됐는데.. 우분투 서버는 생각보다 까다로웠습니다. 와이파이 설정을 했던 내용을 기록 해 보았는데, 한국어로된 한명의 포스팅에서 다들 퍼온듯 한 획일적인 방법이 있었는데 그 블로그들에서 설명한 `/etc/network/interfaces` 를 변경하는 방법으로는 해결이 되지 않았었고 https://websetnet.net 사이트의 [`How to Connect to WiFi from the Terminal in Ubuntu Linux`](https://websetnet.net/how-to-connect-to-wifi-from-the-terminal-in-ubuntu-linux/) 글이 도움이 많이 되었습니다.

## wifi 설정

### wireless-tools 설치

와이파이에 대한 명령어들을 사용하기 위해서는 일단 wireless-tools가 있어야 합니다. 

인터넷이 안되면 프로그램 설치도 불가능 하기 때문에 일단 와이파이 비밀번호를 이전으로 돌리고 wireless-tools를 설치 해 줬습니다. 와이파이 연결이 불가능한 환경이라면 유선으로라도 연결을 해서 먼저 설치를 해야 할 듯 합니다.

```bash
sudo apt install wireless-tools
```

<br><br>

### 네트워크 상태 확인 

`wireless-tools` 설치 후에는 iwconfig 명령어를 입력해 와이파이 상태를 확인 할 수 있습니다.

```bash
iwconfig
```

![IMG_4142Large](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/wifi.me.assets/IMG_4142Large.jpeg)

>  `wlp2s0` 네트워크에서 kkobuk이라는 ESSID에 접속 중인게 확인 됩니다.
>
> 여기에서 좌측에 제 컴퓨터의 wlp2s0 자리에 있는 네트워크 이름이 중요합니다. 아마 저와 다르실거에요.

파일로 확인 할 수도 있습니다. 

```bash
ls /sys/class/net
```

![IMG_4146Large](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/wifi.me.assets/IMG_4146Large.jpeg)

혹은 아래의 명령어로 모든 네트워크 정보를 확인 할 수 있습니다.

```bash
sudo lshw -C Network
```

![IMG_4145Large](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/wifi.me.assets/IMG_4145Large.jpeg)

> Wireless interface 라고 써있는 무선 네트워크를 확인 합니다.
>
> 제 와이파이는 logical name에 `wlp2s0` 라고 써있는 것을 확인 할 수 있는데요, 본인의 네트워크 이름을 잘 기억해주세요.

만약 사용할 네트워크가 DISABLED되어 있다면 up 시켜줘야 합니다. 예를 들어 스크린샷에서 저의 network:2 DISABLED 는 비활성 화 된 상태 이며 logical name은 enx00... 입니다. 

본인의 wifi 네트워크가 DISABLED 상태라면 아래의 명령어로 활성화 시켜 줍니다. `wlp2s0` 자리에는 본인의 와이파이 네트워크 이름을 써 주셔야 합니다.

```bash
sudo ifconfig wlp2s0 up
```

이제 와이파이를 이용해 주변의 wifi 정보를 검색 해 줍니다.

```bash
sudo iwlist wlp2s0 scan|more
```

![IMG_4144Large](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/wifi.me.assets/IMG_4144Large.jpeg)

> 여러가지 와이파이가 나오는데 저는 여기 나온 ESSID: "kkobuk"에 연결 할 것 입니다.

### 네트워크 연결

`/etc/netplan` 경로에 필요한 설정 파일이 있습니다.

```bash
ls /etc/netplan
```

![IMG_4147Large](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/wifi.me.assets/IMG_4147Large.jpeg)

여기에 보이는두 파일중 00-installer-config-wifi.yaml 에 설정 정보가 있습니다. 수정해줍니다.

```bash
sudo vi /etc/netplan/00-installer-config-wifi.yaml
```

![IMG_4148Large](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/wifi.me.assets/IMG_4148Large.jpeg)

파일을 열었더니 저는 이전에 사용하던 와이파이 정보가 기록 되어 있는데요,  전에 사용하던게 있으면 좀 더 쉽게 변경 할 수 있겠죠. access-points 하위의 kkobuk 자리가 와이파이 SSID 이름입니다.  yaml 파일이니 주석에 반드시 신경 써서 해주셔야 합니다. 와이파이 ssid를 작성 하고 그 다음줄에 비밀번호 작성할 때에는 들여쓰기가 한개 필요 합니다. 이름, 비밀번호가 같은 depth가 아닙니다.

기본 구조는 아래와 같으니 아래 코드를 복사해서 사용하시는 와이파이에 맞게 변경해주세요.

`wlp2s0` 는 분명 다들 다르실건데 wlan0 일 수도 있고, wlp1s0 일 수도 있습니다.

```
wifis:
    wlp2s0:
        dhcp4: true
        optional: true
        access-points:
            "SSID_name":
                password: "WiFi_password"
```

편집이 완료되었으면 `:wq!`로 저장을 하고 나와서..

### 네트워크 재시작

재시작 해줍니다.

```bash
sudo netplan generate
sudo netplan apply
```

generate 만 하면 안되고 apply 까지 해줘야 되더라고요.

이제 완료 되었으니 curl을 사용하건 ping을 쏘건 각자 원하시는 방법으로 네트워크 연결을 확인 해 보면 됩니다.

저는 `ping`을 보내서 확인 했습니다.

![IMG_4149Large](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/Ubuntu-server/wifi.me.assets/IMG_4149Large.jpeg)

이상으로 Ubuntu Server 와이파이 설정법에 대해 알아보았습니다. 겨우 변경된 와이파이 비밀번호 하나 입력하는 건데 과정이 꽤나 복잡해서 당황스러웠습니다.
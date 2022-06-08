# Ubuntu)  Airpod 연결 설정 하기

> 우분투 20.04 에어팟 연결 설정 하는 방법

## Intro

우분투에 에어팟을 연결 해 보려 하니 일반적인 Bluetooth 장비 메뉴로는 가능하지가 않았습니다. 

일단 Unknown 장비들이 무지막지하게 뜨는데 그 중 어떤게 에어팟인지도 모르겠고, 연결도 되지 않았습니다.

![image-20220323142544239](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/Airpod.assets/image-20220323142544239.png) 

## Bluetooth 설정

### ControllerMode 변경

ControllerMode를 bredr로 변경 해 줍니다.

```zsh
sudo vi /etc/bluetooth/main.conf
```

![image-20220323143010680](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/Airpod.assets/image-20220323143010680.png)

> 51 번 라인에 주석 처리된 걸 풀고 `bredr` 입력 후 저장 해 줍니다.
>
> 처음 에어팟에 연결 할 때는 bredr 로 변경해야 쉽게 에어팟을 찾아 연결을 할 수 있긴 한데 한번 연결 한 이후에는 추후 다시 dual로 돌리는것을 권장합니다. 
>
> ![image-20220608143631337](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/Airpod.assets/image-20220608143631337.png)
>
> 저같은 경우에는 bredr 로 설정을 하면 리얼포스 R3 키보드가 아무리 해도 블루투스로 연결이 되지 않았습니다. BR/EDR 은 블루투스 2.x 기반이지만 LE는 블루투스 4.x이후 기반이거든요.
>
> 아래까지 다 진행 해서 에어팟 연결을 한번 성공한 후에 설정을 돌려주시면 되는데, 굳이 bredr로 변경하지 않아도 에어팟을 쉽게 찾아 연결 가능하다면 이 과정을 건너 뛰셔도 됩니다.

### Bluetooth 서비스 재시작

```zsh
sudo /etc/init.d/bluetooth restart
```

블루투스 서비스를 재 시작 해 줍니다.

![image-20220323143128303](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/Airpod.assets/image-20220323143128303.png)

## Airpod 연결

이제 Settings > Bluetooth > Devices를 보면 블루투스 장비 목록이 정상적으로 뜹니다.

에어팟의 가운데 버튼을 2~3초간 꾹 눌러서 페어링 모드에 진입 하면 디바이스를 인식 합니다.

> 너무 오래 누르고 있으면 에어팟의 모든 설정을 reset 합니다. 적당히 누르다 떼면 불이 잠깐동안 깜빡 거립니다.
>
> 그러면 Bluetooth Devices 목록에 에어팟이 뜨는데, 바로 클릭해서 연결 해 주면 됩니다.

![image-20220323143210423](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/Airpod.assets/image-20220323143210423.png)

> 연결이 끝났습니다! 이제 에어팟으로 리눅스의 모든 사운드를 잘 즐길 수 있습니다.

이제 신나게 음악을 들을 수 있습니다!  

> 혹시 Apple Music을 구독하신다면 Cider 라는 정말 훌륭한 Apple Music Client가 있으니 다운 받아서 사용 해 보시길 추천 드립니다. 관련글을 링크 달아두겠습니다. [Linux) 리눅스에서 Apple Music 듣기 Cider App](https://shanepark.tistory.com/347)
>
> ![image-20220323153954522](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/Airpod.assets/image-20220323153954522.png)

## 마이크 설정

![image-20220323143941208](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/Airpod.assets/image-20220323143941208.png)

에어팟을 연결 해도 Output만 사용이 가능하고 Input은 사용이 불가능합니다.  Zoom 회의 등을 할 때 에어팟을 사용하고 싶다면 마이크도 연결을 해 주어야 하는데, 연결 방법에는 Pipewire를 이용하는 방법과 HSP/HFP 를 이용하는 방법이 있습니다.

무작정 처음의 설치부터 진행 하지 말고, 어떤 어떤 작업들을 하는지 끝까지 먼저 쭉 읽어보고나서 아.! 내가 이렇게까지 해서라도 에어팟 마이크 기능을 사용해야 겠다는 분들만 시작하시는게 좋습니다. 굉장히 복잡하고 어렵고 우회하는 방법을 사용하거든요.

### HSP/HFP

> ref: https://reckoning.dev/blog/airpods-pro-ubuntu/

Airpod을 마이크로 사용하고 싶다면 `HSP/HFP` 프로필을 활성화 시켜 진행하는 방법이 있습니다.

Pulseaudio는 기본값으로 HSP만을 지원하고 있는데요, `HSP/HFP`를 모두 사용하기 위해 pulseaudio에 있는 HFP를 활성화 시키기 위해서는 `ofono`가 필요 합니다.

다만, HSP/HFP 모드를 사용하면 원래 사용하는 에어팟 마이크 음질에 비해 현저하게 퀄리티가 떨어지기 때문에 꼭 필요한 경우에만 사용하는 것이 좋습니다. 

1. ofono 설치

```zsh
sudo apt install ofono
```

2. pulseaudio가 ofono를 사용하도록 설정

```zsh
sudo vi /etc/pulse/default.pa
```

![image-20220323144855065](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/Airpod.assets/image-20220323144855065.png)

71 번 라인의 `load-module module-bluetooth-discover` 를 `load-module module-bluetooth-discover headset=ofono` 로 변경해줍니다.

유저 `pulse` 를 bluetooth 그룹에 추가해 권한을 부여합니다.

```zsh
sudo usermod -aG bluetooth pulse
```

* 중요: 권한부여를 위해 아래의 코드조각을 `/etc/dbus-1/system.d/ofono.conf`에서 `</busconfig>` 바로 전에 추가 해 줍니다.

```
<policy user="pulse">
<allow send_destination="org.ofono"/>
</policy>
```

3. ofono의 작동을 위해 modem을 제공해줘야 합니다. phonesim 라는 이름의 모뎀 에뮬레이터를 추가 합니다. 

```zsh
sudo add-apt-repository ppa:smoser/bluetooth
sudo apt-get update
sudo apt-get install ofono-phonesim
```

아래 라인을 `/etc/ofono/phonesim.conf`에 추가해 `phonesim`을 설정해줍니다.

```zsh
[phonesim]
Driver=phonesim
Address=127.0.0.1
Port=12345
```

이제 ofono 서비스를 재 시작 해 줍니다.

```zsh
sudo systemctl restart ofono.service
```

`ofono-phonesim` 를 서비스로 시작하기 위해서는 몇개의 서비스를 선언 해 주어야 합니다.

컴퓨터를 켤 때 `ofono-phonesim -p 12345 /usr/share/phonesim/default.xml` 를 자동으로 systemd unit으로 실행 하기 위해 `/etc/systemd/system/ofono-phonesim.service`를 루트 권한으로 생성해 아래의 내용을 작성 해 줍니다.

```
[Unit]
Description=Run ofono-phonesim in the background

[Service]

ExecStart=ofono-phonesim -p 12345 /usr/share/phonesim/default.xml
Type=simple
RemainAfterExit=yes

[Install]

WantedBy=multi-user.target
```

`ofono-phonesim`를 실행 한 후에는 phonesim을 모뎀을 활성화하고 연결 해 주어야 합니다.

ofono의 특정 Git 커밋을 사용합니다.

```
cd /tmp
git clone git://git.kernel.org/pub/scm/network/ofono/ofono.git
git checkout    b3682c6bab99cbe301fa9bf4a2416c3f730d8bfd
sudo mv ofono /opt/
```

이제는 위의 `ofono-phonesim` systemd 유닛에 의존하는 다른 systemd 유닛을 생성해 phonesim 모뎀을 온라인으로 활성화 할 수 있습니다.

아래의 내용을 `/etc/systemd/system/phonesim-enable-modem.service` 에 작성해 줍니다.

```
[Unit]
Description=Enable and online phonesim modem
Requires=ofono-phonesim.service

[Service]

ExecStart=/opt/ofono/test/enable-modem /phonesim
ExecStart=/opt/ofono/test/online-modem /phonesim
Type=oneshot
RemainAfterExit=yes

[Install]

WantedBy=multi-user.target
```

이제 daemon 들을 둘다 실행하기 위해 아래의 명령어를 실행해줍니다.

```zsh
sudo systemctl daemon-reload
sudo systemctl enable ofono-phonesim.service
sudo systemctl enable phonesim-enable-modem.service
sudo service phonesim-enable-modem start
```

서비스가 잘 돌아가고 있는지 확인합니다.

```zsh
sudo service phonesim-enable-modem status
```

마지막으로 pulseaudio를 재 시작 해 줍니다.

```zsh
pulseaudio -k
```

이제는 에어팟을 input device로 사용 할 수 있습니다. 

### Pipewire

HSP/HFP 를 말고 Pipewire를 사용해 Airpod 마이크 연결을 하면 훨씬 나은 16k 의 음질을 이용할 수 있다고 합니다.

설명이 잘 되어있는 링크가 있어 아래에 링크를 첨부합니다.

> https://askubuntu.com/questions/922860/pairing-apple-airpods-as-headset/1350854#1350854

사실 저도 내용이 너무 복잡해서 마이크 연결까지는 진행하지 않았습니다. 들을 수만 있으면 되죠.. 노트북에 마이크도 보통 달렸구요.

이상입니다.
# [Ubuntu] 멀쩡하던 한글 입력기가 갑자기 문제라면

## Intro

Ubuntu를 쓰다보면 습관적으로 `sudo apt update` 와 `sudo apt upgrade` 를 입력하고는 한다.
그런데, 출근하고 보니 IntelliJ에서 한글 입력이 엉망이 되는 기묘한 현상이 시작되었다.

- 의도한 텍스트: `한글 띄어쓰기가 이상하게 됩니다.`

- 입력된 텍스트: `한 글띄어쓰기 가이상하 게됩니다.`

잘 알려진 한글 끝 글자 이슈인데, KIME 한글 입력기를 사용하고부터는 좀처럼 겪지 않았던 문제다. 

사실 이게 전에도 한번 이런 일이 있었는데, 그때는 처음이라 해결하느라 너무 고생했었다. 이번에는 같은 문제를 다시 겪기도 했으니 글로 정리해두어 다음 번에 같은 상황이 왔을 때 낭비하는 시간을 줄이고자 한다.

**환경 요약**

- **OS**: Ubuntu 22.04 LTS
- **GPU**: NVIDIA GeForce GTX 1650 Ti (모바일)
- **세션 타입**: Wayland (`echo $XDG_SESSION_TYPE → wayland`)
- **입력기**: KIME
- **IDE**: IntelliJ IDEA (모든 버전에서 재현)

## 문제

이상하게도 IntelliJ에서만 띄어쓰기와 자모 결합이 비정상적으로 작동했다. Chrome, Firefox, Slack, Terminal 등 다른 애플리케이션은 문제 없다.

입력기를 바꿔보고, 인텔리제이 버전도 여러가지 받아서 확인해보았지만 모두 해결되지 않았다. 

마침 오늘 아침에도 `apt upgrade`로 몇몇 패키지가 업데이트 되었기에 거기부터 문제를 추척해보기로 한다.

## 원인 추적

아래의 명령어를 입력하여 apt history 를 확인할 수 있다.

```bash
cat /var/log/apt/history.log
```

오늘 오전

```
Start-Date: 2025-10-22  09:23:26
Commandline: apt upgrade
Requested-By: shane (1000)
Upgrade: google-chrome-stable:amd64 (141.0.7390.107-1, 141.0.7390.122-1), distro-info-data:amd64 (0.52ubuntu0.9, 0.52ubuntu0.11)
End-Date: 2025-10-22  09:23:33
```

Chrome 과 distro-info-data 가 업그레이드 되었다. 여기에는 의심할 만한 게 없다.

하루 전 히스토리를 확인해본다.

```
Start-Date: 2025-10-21  09:41:20
Commandline: /usr/bin/unattended-upgrade
Install: nvidia-firmware-570-570.195.03:amd64 (570.195.03-0ubuntu0.22.04.1, automatic), libnvidia-egl-wayland1:amd64 (1:1.1.9-1.1, automatic), libnvidia-egl-wayland1:i386 (1:1.1.9-1.1, automatic)
Upgrade: libnvidia-fbc1-570:amd64 (570.172.08-0ubuntu1, 570.195.03-0ubuntu0.22.04.1), libnvidia-fbc1-570:i386 (570.172.08-0ubuntu1, 570.195.03-0ubuntu0.22.04.1), libnvidia-gl-570:amd64 (570.172.08-0ubuntu1, 570.195.03-0ubuntu0.22.04.1), libnvidia-gl-570:i386 (570.172.08-0ubuntu1, 570.195.03-0ubuntu0.22.04.1), libnvidia-extra-570:amd64 (570.172.08-0ubuntu1, 570.195.03-0ubuntu0.22.04.1), nvidia-compute-utils-570:amd64 (570.172.08-0ubuntu1, 570.195.03-0ubuntu0.22.04.1), nvidia-dkms-570:amd64 (570.172.08-0ubuntu1, 570.195.03-0ubuntu0.22.04.1), nvidia-driver-570:amd64 (570.172.08-0ubuntu1, 570.195.03-0ubuntu0.22.04.1), libnvidia-encode-570:amd64 (570.172.08-0ubuntu1, 570.195.03-0ubuntu0.22.04.1), libnvidia-encode-570:i386 (570.172.08-0ubuntu1, 570.195.03-0ubuntu0.22.04.1), nvidia-utils-570:amd64 (570.172.08-0ubuntu1, 570.195.03-0ubuntu0.22.04.1), xserver-xorg-video-nvidia-570:amd64 (570.172.08-0ubuntu1, 570.195.03-0ubuntu0.22.04.1), libnvidia-decode-570:amd64 (570.172.08-0ubuntu1, 570.195.03-0ubuntu0.22.04.1), libnvidia-decode-570:i386 (570.172.08-0ubuntu1, 570.195.03-0ubuntu0.22.04.1), nvidia-kernel-common-570:amd64 (570.172.08-0ubuntu1, 570.195.03-0ubuntu0.22.04.1), libnvidia-cfg1-570:amd64 (570.172.08-0ubuntu1, 570.195.03-0ubuntu0.22.04.1), nvidia-kernel-source-570:amd64 (570.172.08-0ubuntu1, 570.195.03-0ubuntu0.22.04.1), libnvidia-compute-570:amd64 (570.172.08-0ubuntu1, 570.195.03-0ubuntu0.22.04.1), libnvidia-compute-570:i386 (570.172.08-0ubuntu1, 570.195.03-0ubuntu0.22.04.1)
Error: Sub-process /usr/bin/dpkg returned an error code (1)
End-Date: 2025-10-21  09:41:38

Start-Date: 2025-10-21  10:22:12
Commandline: apt --fix-broken install
Requested-By: shane (1000)
Install: nvidia-driver-570-server:amd64 (570.195.03-0ubuntu0.22.04.2, automatic), libnvidia-gl-570-server:amd64 (570.195.03-0ubuntu0.22.04.2, automatic), libnvidia-decode-570-server:amd64 (570.195.03-0ubuntu0.22.04.2, automatic), libnvidia-fbc1-570-server:amd64 (570.195.03-0ubuntu0.22.04.2, automatic), nvidia-kernel-source-570-server:amd64 (570.195.03-0ubuntu0.22.04.2, automatic), libnvidia-cfg1-570-server:amd64 (570.195.03-0ubuntu0.22.04.2, automatic), libnvidia-extra-570-server:amd64 (570.195.03-0ubuntu0.22.04.2, automatic), nvidia-compute-utils-570-server:amd64 (570.195.03-0ubuntu0.22.04.2, automatic), libnvidia-encode-570-server:amd64 (570.195.03-0ubuntu0.22.04.2, automatic), nvidia-kernel-common-570-server:amd64 (570.195.03-0ubuntu0.22.04.2, automatic), libnvidia-compute-570-server:amd64 (570.195.03-0ubuntu0.22.04.2, automatic), xserver-xorg-video-nvidia-570-server:amd64 (570.195.03-0ubuntu0.22.04.2, automatic), nvidia-utils-570-server:amd64 (570.195.03-0ubuntu0.22.04.2, automatic), nvidia-firmware-570-server-570.195.03:amd64 (570.195.03-0ubuntu0.22.04.2, automatic), nvidia-dkms-570-server:amd64 (570.195.03-0ubuntu0.22.04.2, automatic), libnvidia-common-570-server:amd64 (570.195.03-0ubuntu0.22.04.2, automatic)
Remove: libnvidia-common-570:amd64 (570.133.20-0ubuntu1), libnvidia-fbc1-570:amd64 (570.195.03-0ubuntu0.22.04.1), libnvidia-fbc1-570:i386 (570.195.03-0ubuntu0.22.04.1), libnvidia-gl-570:amd64 (570.195.03-0ubuntu0.22.04.1), libnvidia-gl-570:i386 (570.195.03-0ubuntu0.22.04.1), libnvidia-extra-570:amd64 (570.195.03-0ubuntu0.22.04.1), nvidia-compute-utils-570:amd64 (570.195.03-0ubuntu0.22.04.1), nvidia-dkms-570:amd64 (570.195.03-0ubuntu0.22.04.1), nvidia-driver-570:amd64 (570.195.03-0ubuntu0.22.04.1), libnvidia-encode-570:amd64 (570.195.03-0ubuntu0.22.04.1), libnvidia-encode-570:i386 (570.195.03-0ubuntu0.22.04.1), nvidia-utils-570:amd64 (570.195.03-0ubuntu0.22.04.1), nvidia-firmware-570-570.195.03:amd64 (570.195.03-0ubuntu0.22.04.1), xserver-xorg-video-nvidia-570:amd64 (570.195.03-0ubuntu0.22.04.1), libnvidia-decode-570:amd64 (570.172.08-0ubuntu1), libnvidia-decode-570:i386 (570.172.08-0ubuntu1), nvidia-kernel-common-570:amd64 (570.195.03-0ubuntu0.22.04.1), libnvidia-cfg1-570:amd64 (570.195.03-0ubuntu0.22.04.1), nvidia-kernel-source-570:amd64 (570.195.03-0ubuntu0.22.04.1), libnvidia-compute-570:amd64 (570.195.03-0ubuntu0.22.04.1), libnvidia-compute-570:i386 (570.195.03-0ubuntu0.22.04.1)
End-Date: 2025-10-21  10:23:50

Start-Date: 2025-10-21  11:22:51
Commandline: apt autoremove
Requested-By: shane (1000)
Remove: libxcb-present0:i386 (1.14-3ubuntu3), libxcb-dri3-0:i386 (1.14-3ubuntu3), libnvidia-egl-wayland1:i386 (1:1.1.9-1.1), nvidia-firmware-570-570.172.08:amd64 (570.172.08-0ubuntu1), nvidia-modprobe:amd64 (575.57.08-0ubuntu1)
End-Date: 2025-10-21  11:22:51
```

그렇다 전날에는 많은 일들이 있었다. 이걸 보자마자 확실한 용의자로 특정하고 즉시 검거했다.

## 원인 분석

자동 업데이트에 실패하고, `apt --fix-broken install` 과정에서 서버용 그래픽카드 드라이버가 데스크톱용 드라이버를 대체해버렸다.

문제는, 그래픽카드 드라이버가 Wayland 기반 입력기 클라이언트 창 렌더링에 관여하는데, 서버용 드라이버는 GUI 용도가 아니라서 그런지 아니면 새로 배포되는 버전이 문제였는지.. 어쨌든 이슈가 있던 모양이다.

## 해결

### 1) 현재 상태 점검

먼저 현 상황을 확인했다.

```bash
nvidia-smi
echo $XDG_SESSION_TYPE
```

```
Wed Oct 22 09:49:36 2025 
 +-----------------------------------------------------------------------------------------+
 | NVIDIA-SMI 570.195.03 Driver Version: 570.195.03 CUDA Version: 12.8 |
 |-----------------------------------------+------------------------+----------------------+
 | GPU Name Persistence-M | Bus-Id Disp.A | Volatile Uncorr. ECC |
 | Fan Temp Perf Pwr:Usage/Cap | Memory-Usage | GPU-Util Compute M. |
 | | | MIG M. |
 |=========================================+========================+======================|
 | 0 NVIDIA GeForce GTX 1650 Ti Off | 00000000:01:00.0 Off | N/A |
 | N/A 57C P8 4W / 50W | 5MiB / 4096MiB | 0% Default |
 | | | N/A |
 +-----------------------------------------+------------------------+----------------------+
 
 +-----------------------------------------------------------------------------------------+
 | Processes: |
 | GPU GI CI PID Type Process name GPU Memory |
 | ID ID Usage |
 |=========================================================================================|
 | 0 N/A N/A 17686 G /usr/bin/gnome-shell 1MiB |
 +-----------------------------------------------------------------------------------------+
 
 
 $ echo $XDG_SESSION_TYPE
 wayland
```

### 2) 일반 드라이버로 복귀

설치된 서버 드라이버를 지우고 일반 버전으로 되돌렸다.

```bash
sudo apt install -y nvidia-driver-570
sudo apt remove -y nvidia-driver-570-server
sudo reboot
```

재부팅 후 `nvidia-smi`로 재확인:

```
$ nvidia-smi
+-----------------------------------------------------------------------------------------+
| NVIDIA-SMI 570.172.08             Driver Version: 570.172.08     CUDA Version: 12.8     |
|-----------------------------------------+------------------------+----------------------+
| GPU  Name                 Persistence-M | Bus-Id          Disp.A | Volatile Uncorr. ECC |
| Fan  Temp   Perf          Pwr:Usage/Cap |           Memory-Usage | GPU-Util  Compute M. |
|                                         |                        |               MIG M. |
|=========================================+========================+======================|
|   0  NVIDIA GeForce GTX 1650 Ti     Off |   00000000:01:00.0 Off |                  N/A |
| N/A   51C    P8              4W /   50W |       8MiB /   4096MiB |      0%      Default |
|                                         |                        |                  N/A |
+-----------------------------------------+------------------------+----------------------+
                                                                                         
+-----------------------------------------------------------------------------------------+
| Processes:                                                                              |
|  GPU   GI   CI              PID   Type   Process name                        GPU Memory |
|        ID   ID                                                               Usage      |
|=========================================================================================|
|    0   N/A  N/A            2611      G   /usr/lib/xorg/Xorg                        4MiB |
+-----------------------------------------------------------------------------------------+

```

## 검증 결과

| 상태      | 드라이버            | 세션       | 결과                               |
| --------- | ------------------- | ---------- | ---------------------------------- |
| 문제 상태 | 570.195.03 (server) | Wayland    | `한 글띄어쓰기 가이상하 게됩니다.` |
| 해결 상태 | 570.172.08 (일반)   | X11 (Xorg) | 한글 입력 정상                     |

재부팅 후 인텔리제이에서도 문제 없이 한글 입력이 된다. 



글을 정리하고 보니 드라이버 문제 라기 보다는 wayland 문제였던 것 같아 테스트를 위해 로그아웃 후, wayland 로 부팅을 해보니 한글이 입력이 되지 않는다. wayland로 갔다가 다시 x11로 와도 문제는 로그아웃 만으로는 해결이 안되는데, 재부팅 까지 해야 해결되니 굳이 테스트하지 말 것.

찾아보니 우분투같은 gnome은 wayland 입력을 ibus를 내장해서 하기 때문에 kime를 사용할수 없다고 한다. 

추가로, wayland 로 테스트를 할 때 그래픽 문제까지 발생하였기 때문에 앞으로 한글입력기 문제가 아니어도 계속 x11을 써야겠다.

끝

**References**

- https://github.com/Riey/kime/issues/604

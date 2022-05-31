# Linux) 스왑(swap) 메모리 설정 변경

## Intro

### 메모리 부족

![image-20220531151051588](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/swap-memory.assets/image-20220531151051588.png)

Windows 에서는 WSL 위에 도커 컨테이너 몇개 띄우고, 인텔리제이에 브라우저 탭 몇개만 켜면 메모리가 바닥나버리는 바람에 Linux 사용을 시작한지도 어느덧 1년이 다 되어갑니다.

처음 우분투로 옮겼을때는 평소 윈도우에서는 메모리를 다 잡아먹었을 만큼 어플리케이션을 띄워도 거의 절반의 메모리 만을 사용하기 때문에 굉장히 쾌적했는데 그 사이 도커 컨테이너도 몇개 늘고 프로젝트에서 사용하는 어플리케이션도 몇개 늘어났습니다.

메모리 부하가 조금만 심해졌다 하면 컴퓨터가 그대로 멈춰버리는 바람에 그때마다 Ctrl + Alt + F6 으로 터미널 모드에 들어가 돼지 프로세서들 몇개를 kill 해낸 후 `Alt + F1` 로 돌아오는 방식으로 해결을 하고는 있지만 여간 번거로운 일이 아닙니다.

메모리를 확인해보니 Swap 메모리 설정이 2.0GiB로 되어 있고 거의 항상 100%로 사용하고 있기에 스왑 메모리를 늘리는 방법으로 어느정도 해결을 해 보려고 합니다.

### 적당한 스왑 공간

![image-20220531152017317](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/swap-memory.assets/image-20220531152017317.png)

> https://access.redhat.com/documentation/en-us/red_hat_enterprise_linux/8/html/managing_storage_devices/getting-started-with-swap_managing-storage-devices

스왑 메모리 크기 변경에 앞서, 어느정도로 변경을 할 지 정해야 합니다.

Redhat 에서는 8~64GB 사이의 메모리르 사용 하고 있으면 최소 4GB 이상을 할당하라고 추천 하고 있으며 hibernation(최대절전모드) 를 위해서는 메모리의 1.5배를 할당하라고 안내하고 있습니다.

4GB 만 할당하기에는 이미 부족한 정도가 심한 편이고, 1.5배로 맞추기 위해 24GB의 메모리를 할당하기에는 과하기도 하지만 저장공간이 그렇게 여유있는 편도 아니기 때문에 일단 8GiB 정도로 할당을 하고 추후 조절을 해 볼 생각 입니다.

## Swap 메모리 설정

### Swap 파티션 확인

설정에 앞서 현재 사용하고 있는 스왑 파티션을 확인 해 봅니다.

```zsh
sudo swapon --show
```

![image-20220531153122036](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/swap-memory.assets/image-20220531153122036.png)

`/swapfile` 이라는 이름으로 2GB 바이트가 할당 되어 있으며 대부분이 사용 되고 있는데요, 여기에서 아무것도 나오지 않는다면, 스왑 공간을 사용하지 않고 있는 것 입니다. 

> 가능하기는 하지만 한개의 머신에서 여러개의 스왑 공간을 할당해 사용하는 일은 흔하지 않습니다.

### Swap File 제거

스왑 파일이 이미 있기 때문에 아래와 같이 Text file busy 에러가 발생합니다.

```zsh
sudo fallocate -l 8G /swapfile
```

![image-20220531153431540](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/swap-memory.assets/image-20220531153431540.png)

여기에서 스왑 파일을 제거 하거나 이미 있는 ` sudo dd if=/dev/zero of=/swapfile bs=1G count=8 oflag=append conv=notrunc` 명령으로 스왑파일의 끝에 zero bytes를 붙여 크기를 늘리는 방법이 있습니다.

이번 글에서는 깔끔하게 제거 후 새로 생성하는 방법으로 진행 하겠습니다. 기존의 스왑공간이 없는 분은 건너띄고 다음 내용인 Swap File 생성 쪽으로 넘어 가 주시면 됩니다.

1. 먼저, 사용하고 있는 Swap File을 비 활성화 해 줍니다.

```zsh
sudo swapoff -v /swapfile
```

![image-20220531155152469](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/swap-memory.assets/image-20220531155152469.png)

> Swap 공간이 꺼진 상태

2. `/etc/fstab` 파일에서 스왑 파일 entry를 제거 해 줍니다.

이 과정은 나중에 Swap File 생성에서 다시 그대로 살리기 때문에 지금 스왑 파일 용량 변경만을 진행하시는 중이라면 굳이 12번 라인을 제거 하지 않으셔도 됩니다. 스왑 공간 제거가 목적이라면 지워주시면 됩니다.

```zsh
sudo vi fstab
```

![image-20220531155749473](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/swap-memory.assets/image-20220531155749473.png)

> 12번 라인의 `/swapfile swap swap defaults 0 0` 부분을 제거 해 줍니다.

3. 이제 마지막으로 rm 커맨드로 실제 swapfile을 제거 해 줍니다.

```zsh
sudo rm /swapfile
```

스왑 파일이 깔끔하게 제거 되었습니다.

### Swap File 생성

이제 새로운 Swap File을 생성 해 보도록 하겠습니다. 저는 8GB의 용량을 할당 하지만, 필요에 맞춰 8로 작성된 숫자를 변경 해 주시면 됩니다.

1. 파일 생성

```zsh
sudo fallocate -l 8G /swapfile
```

> 만약 `fallocate`가 설치 되지 않아 `fallocate failed: Operation not supported` 메시지가 나온다면 아래의 명령으로 대신 스왑 파일을 생성하실 수 있습니다.
>
> ```
> sudo dd if=/dev/zero of=/swapfile bs=1024 count=1048576
> ```

2. root 사용자만이 swap file을 작성 할 수 있도록 권한 설정을 변경 해 줍니다.

```zsh
sudo chmod 600 /swapfile
```

3. `mkswap` 유틸리티를 이용해 해당 파일로 Linux 스왑공간을 설정 합니다.

```zsh
sudo mkswap /swapfile
```

![image-20220531160220765](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/swap-memory.assets/image-20220531160220765.png)

4. 이제 아래의 명령어로 Swap file을 활성화 해 줍니다.

```zsh
sudo swapon /swapfile
```

이후 `free -h` 명령으로 확인 해 보면 스왑공간을 사용하고 있는걸 확인 할 수 있습니다.

![image-20220531160310424](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/swap-memory.assets/image-20220531160310424.png)

> Swap: 8.0Gi

이제 재부팅 후에도 메모리 스왑이 자동으로 설정 되도록 `/etc/fstab` 파일을 수정 해 줍니다.

```zsh
sudo vi fstab
```

제일 아래에 아래의 내용을 작성 해 줍니다. 아까 Swap File 제거 부분에서 해당 내용을 제거 하지 않으셨다면 그대로 두시면 됩니다.

```
/swapfile swap swap defaults 0 0
```

![image-20220531160708672](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/swap-memory.assets/image-20220531160708672.png)

> 맨 아래줄의 내용이 추가됩니다.

### Swap value 설정

Swappiness는 시스템이 얼마나 자주 스왑 공간을 사용할 지 설정하는 리눅스 커널 속성 입니다. 0 ~ 100 사이의 값을 지정 할 수 있으며 값이 낮을수록 커널은 가능한 스왑공간을 사용하지 않으려 하며 값이 클수록 커널은 더 적극적으로 스왑 공간을 사용 합니다.

```zsh
cat /proc/sys/vm/swappiness
```

대부분의 Linux System에서는 기본값인 60이면 충분 합니다.

만약 해당 값을 10으로 변경 하고 싶다면 

```zsh
sudo sysctl vm.swappiness=10
```

위의 명령어를 실행 해 주면 됩니다. 리부팅 후에도 이 설정을 유지하고 싶다면

`/etc/sysctl.conf` 파일에 vm.swappiness 값에 대한 내용을 작성 해 주면 됩니다.

```
vm.swappiness=10
```

## 변경 결과

### 테스트

이제 스왑 메모리를 변경 했으니 그 효과가 있는지 확인을 해보려 합니다.

부하 테스트를 위해 도커컨테이너 및 스프링 부트 프로젝트를 20개가량 띄운 후 IntelliJ IDEA와 그 외 크롬 브라우저 윈도우도 40개 가량 띄워 보았습니다. 

![image-20220531161638048](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/swap-memory.assets/image-20220531161638048.png)

> 그 외에도 카카오톡, PostMan, FireFox, 등등 많이 띄워 보았습니다.

![image-20220531161707332](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/swap-memory.assets/image-20220531161707332.png)

그 결과 예전같았으면 바로 메모리가 가득 차서 컴퓨터가 휘청 거렸을 상황임에도 스왑 메모리를 바짝 당겨 쓰며 아무 문제없이 버텨 내 주었습니다. 스왑메모리 변경의 효과를 확실하게 체감 할 수 있습니다.

### 마치며

Swap Memory는 실제 메모리는 아니고 저장장치의 공간을 끌어다 쓰는 것 이기 때문에 속도가 약간 떨어 질 수 있습니다. 그렇기 때문에 메모리 공간이 넉넉하다면 굳이 Swap Memory 공간을 할당하지 않는게 속도측면이나 디스크 수명 측면에서 유리합니다.

하지만 절대적인 메모리 공간이 수요에 비해 부족한 경우에는 저처럼 그 공간을 적극적으로 사용해서 컴퓨터 환경을 보다 쾌적하게 만들어 보시길 추천합니다.

이상입니다.

ref: https://linuxize.com/post/how-to-add-swap-space-on-ubuntu-18-04/
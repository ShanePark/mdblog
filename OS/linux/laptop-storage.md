# (Linux 서버로 사용중인) 노트북 SSD/하드 추가 및 마운트

## Intro

지난번에는 노트북에 램을 추가시켰습니다. 본격적으로 이것 저것 다양한 용도로 사용하려고 하는데 저장공간이 이미 거의 다 차버렸습니다.

![labtop](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-storage.assets/laptop.jpeg)

> 하단의 비어있는 2.5인치 SATA 슬롯

다행히도 램을 추가하면서 확인해보니 여분의 2.5인치 sata 슬롯이 비어있는걸 확인 했었기에 저렴한 하드를 하나 구입하려고 알아보던 차에 쿠팡에서 512GB SSD가 하드보다도 싼 가격에 반짝 딜이 올라왔고, 바로 구입을 해서 설치 해 보았습니다.

### SSD 설치

![IMG_0108 Large](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-storage.assets/IMG_0108Large.jpeg)

SSD는 설치는 그냥 비어있는 슬롯에 꼽기만 하면 되기 때문에 정말 쉽습니다. 같은 공간에 2.5인치 하드디스크를 구입해서 설치 해도 됩니다. 

SSD는 그냥 꼽고 땡이지만 하드디스크는 물리적 충격에 민감하기 때문에 고정하는 작업이 보통 추가로 필요합니다.

## 파티션 설정

일단 구입한 SSD를 장착하기 전에 fdisk -l 을 입력해서 디스크 정보를 확인 해 보았습니다.

```bash
sudo fdisk -l
```

![image-20221201200200520](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-storage.assets/image-20221201200200520.png)

> 238.47GiB 용량을 가진 SanDisk 디스크가 하나 보입니다.

이제 새로운 SSD를 장착한 이후에 같은 명령어를 입력 해 보았습니다.

```bash
sudo fdisk -l
```

![image-20221201200907418](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-storage.assets/image-20221201200907418.png)

> 맨 아래에 476.94 GiB의 디스크가 추가된게 확인됩니다.

이제 파티션 설정을 해보겠습니다.

위에 보이는 Disk 이름을 넣어 fdisk 명령을 실행 해 줍니다.

```bash
sudo fdisk /dev/sda
```

![image-20221201202020234](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-storage.assets/image-20221201202020234.png)

> fdisk가 실행 된 상태. 설정한 정보는 저장하기 전까지는 메모리에만 남아있다고 합니다.

명령어 목록을 확인 하기 위해 일단 `m`을 입력 해 봅니다.

![image-20221201202048933](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-storage.assets/image-20221201202048933.png)

> 다양한 명령어가 나옵니다. 

파티션을 생성 할 목적이기 때문에 `n` 을 입력해 add a new partition을 합니다.

![image-20221201202118690](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-storage.assets/image-20221201202118690.png)

> 파티션  타입 선택

파티션 타입을 선택 하라고 하는데요, 한개만 생성할 것 이기 떄문에 default인 primary 로 해 주었습니다.

하나의 디스크로 여러개의 파티션을 생성 한다면 그때는 extended도 사용하겠네요

![image-20221201202346539](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-storage.assets/image-20221201202346539.png)

이후에는 

- Partition 번호
- 첫번째 섹터
- 마지막 섹터

순서대로 입력을 하는데요, 파티션을 한개만 만들 예정이라서 전부 다 기본 값으로 지정 해 주었습니다.

파티션 설정 후에는 `p`를 입력 해서 Print the partition table 명령을 실행 합니다.

![image-20221201202400073](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-storage.assets/image-20221201202400073.png)

> /dev/sda1에 476.9G 용량의 Linux 타입 파티션이 생성된 상태

이제 정보가 잘 입력 되었다면 `w`를 입력 해 저장 해 줍니다.

![image-20221201202422700](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-storage.assets/image-20221201202422700.png)

> 정상적으로 파티션 정보가 Sync 됩니다.

## 마운트

이제 다시 `fdisk -l` 명령을 실행 해 보면 생성된 파티션이 확인 됩니다.

```bash
sudo fdisk -l
```

![image-20221201203101156](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-storage.assets/image-20221201203101156.png)

### 포맷

파티션을 사용 할 수 있도록 포맷 해 주어야합니다. 

포맷을 하지 않고 마운트를 시도 한다면 아래와 같은 에러가 발생합니다.

```
mount: wrong fs type, bad option, bad superblock on /dev/sda1,
       missing codepage or helper program, or other error
```

포맷할 Device 의 파티션 명을 정확히 입력해 주세요. 

사용중인 디바이스를 포맷하지 않도록 꼭 주의 해 주세요. 저는 `/dev/sba1` 이 파티션 이름 입니다. 최신의 ext4 파일 시스템으로 포맷 해 줍니다.

```bash
 sudo mkfs.ext4 /dev/sda1
```

![image-20221201203824644](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-storage.assets/image-20221201203824644.png)

> done 이 연속으로 모두 나오며 정상적으로 포맷이 완료 됩니다. Filesystem UUID가 보이는데, 저걸 나중에 마운트 할 때 사용 할 수 있으니 필요하다면 이 때 복사 해 둡니다.

### 마운트 포인트 생성

마운트 할 폴더를 `mkdir`로 적당한 위치에 생성 해 줍니다.

```bash
sudo mkdir /mnt0
```

![image-20221201203410674](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-storage.assets/image-20221201203410674.png)

> 폴더가 생성되었습니다.

### 마운트

이제 해당 경로에 마운트 해 줍니다.

```bash
mount /dev/sda1 /mnt0
```

![image-20221201203938545](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-storage.assets/image-20221201203938545.png)

> 마운트 완료

이제 정상적으로 마운트가 되었는지 확인 해 봅니다.

```bash
df -h
```

![image-20221201204002885](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-storage.assets/image-20221201204002885.png)

> 가장 아래에 /dev/sda1이 추가되었고 /mnt0에 마운트 된 것이 확인 됩니다.

마운트 된 폴더에 샘플 파일을 하나 만들어 보고 확인 해 봅니다.

```bash
sudo touch /mnt0/hello
```

![image-20221201204146764](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-storage.assets/image-20221201204146764.png)

> 파일이 생성 되었습니다.

### 마운트 정보 저장

하지만 이렇게 마운트을 해도 컴퓨터를 재부팅 하고 나면 마운트 정보가 사라집니다. 바로 한번 재부팅을 해 보겠습니다.

```bash
sudo reboot
```

![image-20221201204344783](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-storage.assets/image-20221201204344783.png)

> 마운트 정보도, mnt0의 hello 파일도 사라진게 확인 됩니다.

마운트 정보는 `/etc/fstab`에 입력해주면 됩니다.

```bash
sudo vi /etc/fstab
```

![image-20221201204643417](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-storage.assets/image-20221201204643417.png)

가장 아래에 아래와 같이 마운트할 파티션 정보를 입력 해 줍니다.

```
/dev/sda1       /mnt0   ext4    defaults        0       0
```

왼쪽부터 순서대로

- file system: 마운트할 파일 시스템 이름
- mount point: 마운트 포인트
- type: 파일 시스템 타입. ext4 로 설정
- options: 마운트 옵션. default로 진행
- dump
- pass

다섯번째 여섯번째는 잘 몰라서 `man fstab`을 입력해서 확인 해 보았습니다.

![image-20221201210716334](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-storage.assets/image-20221201210716334.png)

> 다섯번째는 dump 여부 설정이고, 여섯번째 필드는 부팅시 파일시스템 체크를 하는 순서를 정하는 옵션이라고 하네요. 둘 다 default 값으로 설정 해 주었습니다.

이후에는 아까 했던 것 처럼 마운트를 다시 해 주거나 확인을 위해 컴퓨터를 재 부팅해서 확인 합니다.

**마운트**

```bash
sudo mount /dev/sda1 /mnt0
```

혹은 **재부팅**

```bash
sudo reboot
```

이후에 다시 확인 해 보면

![image-20221201205800790](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-storage.assets/image-20221201205800790.png)

> 마운트가 정상적으로 되어 있습니다. `/mnt0` 경로에 아까 만든 hello 파일도 보입니다.

이제 마운트할 경로를 사용하기 좋게 적당히 권한변경 및 소유권을 변경해서 사용 해 주면 됩니다.

```bash
sudo chmod -R 775 /mnt0
```

![image-20221201210000603](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-storage.assets/image-20221201210000603.png)

이제 사용 할 준비가 완료 되었습니다! 용량이 늘어 난 만큼 파일 서버 등으로 다양하게 사용 할 수 있을 것 같습니다. 

이상입니다.

**References**

- https://linuxize.com/post/fdisk-command-in-linux/
- https://m.blog.naver.com/kimmingul/220639741333
- https://askubuntu.com/questions/125257/how-do-i-add-an-additional-hard-drive
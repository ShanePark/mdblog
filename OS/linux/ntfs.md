# 리눅스에서 하드디스크 NTFS 시스템 으로 포맷

## Intro

윈도우와 리눅스 두가지 OS에서 모두 사용할 수 있는 파일 시스템으로 하드디스크를 포맷을 해야 할 필요가 있었다.

FAT32, exFAT, NTFS 등 선택할 수 있는 몇가지 파일시스템이 있지만 각각의 장단점이 있다.

- FAT32는 거의 모든 기기와 호환되는 장점이 있지만, 4GB 이상의 큰 파일을 저장할 수 없으며 파티션 크기에도 제한이 있다.
- 그래서 exFAT과 NTFS 중 하나를 선택해야 하는데 안정성이 중요한 상황이라 NTFS를 선택했다. 

## 파티션 생성

먼저, 연결된 모든 디스크 목록을 확인한다. 디스크 이름을 알아내기 위함이다.

```bash
sudo fdisk -l
```

![image-20230807111514923](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/ntfs.assets/9.webp)

여기 보이는 14.57 TiB짜리 하드디스크 `/dev/sda`를 NTFS 파일 시스템으로 포맷할 예정이다.

이어 아래의 명령어를 입력한다. 

>  **주의: 반드시 본인이 포맷을 원하는 디스크 이름을 정확히 확인해야 한다. **
>
> **예시에 작성된 그대로 사용할 경우 멀쩡히 사용 중인 하드를 포맷해 데이터를 유실 할 수 있다.** 

```bash
sudo fdisk /dev/sda
```

![image-20230807103608007](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/ntfs.assets/1.webp)

> 여기에서 GPT로 바꾸라는 경고에 주목한다. 경고가 없다면 무시해도 좋다.

m을 입력하면 도움말이 표시된다. 어떤 명령어를 사용 가능한지 미리 확인한다.

![image-20230807103634688](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/ntfs.assets/2.webp)

이제 디스크를 생성해보자. 기존에 생성된 파티션이 있다면 `d`를 입력해 먼저 모두 제거해준다.

이후 `n`을 입력 해 파티션 생성을 시작한다. 

### 1차 시도 실패

> 일단 따라하지 말고 확인만 해보자

![image-20230807103936575](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/ntfs.assets/3.webp)

파티션 타입, 파티션 넘버, 첫번째 섹터, 마지막 섹터를 선택하는데 모두 기본값으로 입력했다. 

그런데 위에 보이는 것 처럼 2TiB로 생성된걸 확인 할 수 있다. 현재 포맷중인 하드디스크의 용량은 14.6TB다. 뭔가 문제가 있다는 것.

맨 처음에 `sudo fdisk /dev/sda` 했을때 GPT로 바꾸라고 경고가 나왔는데 그걸 따라야한다.

이제 다시 해보겠다. 방금 만들었던 파티션은 `d`를 입력해 제거해준다.

### 2차 시도

> 여기에서부터는 따라해도 좋다

먼저 `g`를 입력해 GPT 디스크 라벨을 생성해준다.

![image-20230807104514308](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/ntfs.assets/4.webp)

> 기존에 하드디스크에 xfs 시그니쳐가 등록되어 있다는 경고가 나온다.

이제 다시 아까 했던 것 처럼 `n`을 눌러 파티션을 만들어준다. 선택은 모두 엔터만 입력해서 기본값으로 입력한다.

![image-20230807104547703](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/ntfs.assets/5.webp)

>  이번에는 파티션을 제대로 생성하긴 했는데, 파티션 타입이 Linux filesystem이다. 

윈도우와의 호환을 위해 NTFS 로 포맷중이기때문에, 이왕이면 파티션 타입도 변경해보자. 

타입 변경을 위해 `t`를 입력한다.

![image-20230807104941965](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/ntfs.assets/6.webp)

그러면 파티션 타입 혹은 alias를 입력하라고 하는데, `L`을 입력해서 확인 해 보면

![image-20230807105021952](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/ntfs.assets/7.webp)

다양한 파티션 타입이 나온다. 이 중 `11`번에 있는 Microsoft basic data 로 변경을 해보겠다.

`q`를 입력 해 빠져나오고, `11`을 입력해 타입을 선택한다.

![image-20230807105141003](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/ntfs.assets/8.webp)

> Changed type of partition 'Linux filesystem' to 'Microsoft basic data'.

타입이 정상적으로 변경되었다. 그러면 이제 `p`를 입력해서 현재의 파티션 테이블을 출력해보자.

![image-20230807111803137](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/ntfs.assets/10.webp)

`/dev/sda` 파티션이 `Microsoft basic data` 타입으로 잘 생성되었다.

이제 `w`를 입력해서 저장해준다. 

> fdisk는 실제로 `w`를 입력해 저장하기 전까지는 메모리에만 변경사항이 저장되며 실제로 파티션을 변경하지는 않는다.

![image-20230807111842755](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/ntfs.assets/11.webp)

> 파티션 테이블 변경 완료.

이제 `sudo fdisk -l`을 입력해 파티션을 확인해본다.

![image-20230807111917335](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/ntfs.assets/12.webp)

> 정상적으로 파티션이 생성됨

## NTFS로 포맷

이제 파티션을 생성했으니 NTFS 타입으로 포맷을 해 보겠다.

`-f` 옵션(fast)을 주지 않으면 정말 오래걸리기 때문에 깜빡하고 빼먹지 않도록 유의한다.

```bash
sudo mkfs.ntfs -f /dev/sda1
```

![image-20230807111953899](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/ntfs.assets/13.webp)

> 만약 위에 보이는 것 처럼, mkfs.ntfs 커맨드를 못찾는 경우에는 `ntfs-3g` 를 설치해줘야 한다.

### ntfs-3g 설치

```bash
# Ubuntu 
sudo apt install ntfs-3g

# rhel
sudo yum install ntfs-3g
```

### 포맷

그러고 나서 다시 `sudo mkfs.ntfs -f /dev/sda1`를 입력 해준다.

![image-20230807112202971](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/ntfs.assets/14.webp)

> NTFS로 포맷 완료

NTFS 타입으로 잘 포맷 되었는지 확인을 해본다.

```bash
lsblk -f
```

![image-20230807112401873](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/ntfs.assets/15.webp)

> sda1 이 ntfs인 상태

## 마운트

이제 NTFS로 포맷한 드라이브를 시스템에 마운트 해보자

방금까진 `/dev/sda` 였지만, 편의상 다른 PC에서 작업을 했고, 실제 사용할 서버에서 마운트해 사용할 예정이다.

새로 연결하니 디바이스 이름이 변경되었다. 확인해본다. 작업 PC가 변경되지 않았다면 그대로 기존의 이름을 사용한다.

```bash
sudo fdisk -l
```

![image-20230807113226003](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/ntfs.assets/16.webp)

> 지금부터는 `/dev/sdc`이다.

`/ntfs` 라는 경로로 방금 포맷한 하드디스크를 마운트 해 보겠다.

```bash
sudo mkdir /ntfs
sudo mount /dev/sdc1 /ntfs
```

마운트가 잘되었는지 확인해본다.

```bash
df -h
```



![image-20230807114033298](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/ntfs.assets/17.webp)

> /ntfs 에 /dev/sdc1이 정상적으로 마운트 된 상태.

### 재부팅 마운트 유지

만약 재부팅 후에도 마운트 정보를 유지하고 싶다면 `etc/fstab` 파일에 입력해두면 된다.

```bash
sudo vi /etc/fstab
```

- file system: 마운트할 파일 시스템 이름 (UUID={UUID정보} 혹은 `/dev/sdc1`)
- mount point: 마운트 포인트
- type: 파일 시스템 타입.
- options: 마운트 옵션. default로 진행
- dump
- pass

위와 같은 순서대로 입력하면 되지만 해당 내용은 여기에서 자세히 다루지 않겠다.

아래의 몇가지 샘플을 참고하여 등록하면 된다.

${code:/etc/fstab}

```
/dev/sda1       /mnt0   ext4    defaults        0       0
UUID=186186eb-7386-4437-a6bb-9261c8c581e5 /data1 xfs    defaults        0 0
```

이렇게 저장 해두면 재부팅 후에도 마운트 정보가 유지된다.

끝.

**References**

- https://manpages.ubuntu.com/manpages/focal/en/man8/fdisk.8.html
- https://manpages.ubuntu.com/manpages/focal/en/man8/mkfs.ntfs.8.html
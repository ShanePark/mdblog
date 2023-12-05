# [Linux] 디스크 xfs로 포맷하기

## Intro

새로추가한 하드디스크를 xfs 파일시스템으로 포맷할 일이 있었는데 생각보다 간단해서 정리해두려 한다. 

몇달에 한번씩만 하게 되는 일들은, 할 때마다 좀처럼 생각이 안난다. 그래서 문서화를 해두지 않으면 쓸데없는 시간 낭비 및 시간착오가 있을 수 있는데 그게 참 아까워서 두번이상 한 일들은 꼭 문서화를 하려고 하는 편이다.

## 파일시스템 생성

### 디스크 목록 확인

먼저 대상 디스크의 디바이스 식별자를 확인한다. 아래 명령어를 입력하면 디스크 목록을 확인 할 수 있다.

```bash
sudo fdisk -l
```

![image-20231204094001948](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/xfs.assets/1.webp)

> 맨 아래의 20.1TiB 짜리 디스크 `/dev/sda` 가 오늘의 타겟이다.

### xfsprogs

먼저 xfsprogs를 설치한다. xfsprogs는 XFS 파일 시스템을 관리하기 위한 유틸리티 모음이다.

아래의 예시는 우분투지만, Redhat 계열 OS를 사용한다면 yum을 사용하면 되겠다.

```bash
sudo apt install xfsprogs
```

### 파일 시스템 생성

타겟 디스크에 xfs 파일 시스템을 생성한다.

```bash
sudo mkfs.xfs /dev/sda
```

![image-20231204094221833](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/xfs.assets/2.webp)

이렇게 하면 별도의 파티션을 생성하지 않고 전체 디스크가 하나의 파일시스템으로 포맷된다. 

파티션을 여러개로 나눠야할 필요가 없을때는 전체 디스크를 단일 파일시스템으로 만들면 단순한 맛이 있어 좋다.

### 마운트

이렇게 만든 파일시스템은 바로 마운트가 가능하다. 샘플로 `~/Downloads/tmp` 폴더에 마운트하는 예시를 적어둘테니 각자 필요에 따라 폴더를 생성하자. 루트 폴더에 마운트한다면 권한때문에 귀찮을순 있는데, sudo 권한으로 폴더를 생성하고, `sudo chown $USER:$USER 폴더명` 하면 된다.

```bash
mkdir ~/Downloads/tmp 
sudo mount /dev/sda ~/Downloads/tmp 
```

이제 디스크를 확인해보자. 몇가지 방법이 있지만 `df`와 `findmnt`를 써보겠다.

**df**

```bash
df -h
```

![image-20231204094437987](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/xfs.assets/4.webp)

> 맨 아래에 마운트된 21T 짜리 경로가 보인다.

**findmnt**

```bash
findmnt
```

![image-20231204094420979](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/xfs.assets/3.webp)

> findmnt 명령으로 확인하면 파일시스템도 확인할 수 있다.

findmnt 했을때 뎁스때문에 보기가 안좋으면 `findmnt -D` 하면 더 깔끔하게 확인 가능하다.

## 마치며

다들 알고있겠지만, 마운트를 해도 재부팅시에는 유지되지 않는다. 재부팅해도 유지되는 마운트를 원한다면 fstab 파일을 수정하면 된다.

```bash
sudo vi /etc/fstab
```

다만, 외장하드등은 꼈다뺐다하면서 주소가 수시로 달라지기때문에 마운트해둔채로 꼈다 빼거나하는 일이 있다면 곤란한 상황이 일어날 수 있으니 주의하자.
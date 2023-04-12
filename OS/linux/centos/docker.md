# CentOS) Docker 및 Compose 설치

## Linux 배포 및 버전 확인

자연스럽게 `sudo apt update` 를 입력 했는데, apt가 안먹히더라고요. 당연히 배포판이 우분투라고 생각했는데 아니었습니다. 일단 어떤 배포판을 사용하는지 먼저 확인 합니다.

```bash
grep . /etc/*-release
```

<img src=https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/centos/docker.assets/image-20211118123434384.webp width=750 height=410 alt=1>

> CentOS 7.9 버전 입니다.

## Docker 설치

> https://docs.docker.com/engine/install/centos/

Docker Engine을 설치 하기 전에 먼저 Docker Repository 를 셋업 해야 합니다. 그리고 그걸 위해 `yum-utils`를 먼저 설치하겠습니다.

```bash
sudo yum install -y yum-utils
```

![image-20211118114637276](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/centos/docker.assets/image-20211118114637276.webp)

> 금방 설치가 됩니다.

이번에는 `yum-config-manager` 를 설치 해 줍니다.

```bash
sudo yum-config-manager \
  --add-repo \
  https://download.docker.com/linux/centos/docker-ce.repo
```

![image-20211118114751166](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/centos/docker.assets/image-20211118114751166.webp)

> 역시 금방 설치 됩니다.

### 최신버전 설치

> 특정 버전을 설치 하고 싶다면 조금 아래로 스크롤을 내려주세요.

```bash
sudo yum install docker-ce docker-ce-cli containerd.io
```

![image-20211118114821706](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/centos/docker.assets/image-20211118114821706.webp)

> 94MB 의 파일을 다운로드 하고, 총 설치 공간은 382MB를 차지합니다. y를 입력해 진행합니다.

![image-20211118114838871](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/centos/docker.assets/image-20211118114838871.webp)

> 저장소를 신뢰하는지 확인합니다. y 를 입력해 계속 진행 해 줍니다.

![image-20211118114930988](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/centos/docker.assets/image-20211118114930988.webp)

> 설치가 완료 되었습니다.

### 특정버전 설치

> 최신 버전이 아닌 특정 버전의 설치가 필요 한 경우는 이렇게 설치합니다.

일단 설치 가능한 목록을 확인 해 보겠습니다.

```bash 
yum list docker-ce --showduplicates | sort -r
```

![image-20211118115158944](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/centos/docker.assets/image-20211118115158944.webp)

정말 다양한 버전이 나오는데요, 그 중 설치할 버전을 고른 다음에는 아래의 명령어를 입력해 설치 합니다. `VERSION_STRING` 자리에 설치할 버전을 기입 해 줍니다.

```bash
sudo yum install docker-ce-<VERSION_STRING> docker-ce-cli-<VERSION_STRING> containerd.io
```

### 실행

설치가 완료되었으니 docker를 실행 해 줍니다.

```bash
sudo systemctl start docker
```

설치가 잘 되었는지 확인 하기 위해 ps 명령어를 써 보았습니다.

```bash
docker ps
```

<br><br>

![image-20211118115030463](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/centos/docker.assets/image-20211118115030463.webp)

docker명령어는 처음에는 sudo 권한이 있어야 실행이 됩니다. 나중에는 docker를 쓸 때마다 `sudo`를 쓰기 번거로우니 유저를 docker 그룹에 추가하면 sudo 권한 없이 docker 명령어를 사용 할 수 있습니다. 해당 방법은 아래 링크에 따로 포스팅 해 두었습니다.

> [Linux, sudo 없이 명령어 실행하기 (예:docker)](https://shanepark.tistory.com/250) 를 확인해주세요.

### 도커 버전 확인

설치가 잘 진행 되었다면, 버전도 확인 해 봅니다. 저는 현재 최신 버전인 20.10.11 버전이 설치 되었습니다.

```bash
docker --version
```

```
Docker version 20.10.11, build dea9396
```

## Compose 설치

Docker를 설치 했으면, 필요에 따라 Compose도 설치 해 줍니다. Compose는 배포판과 상관 없이 설치 방법이 동일 합니다.

> https://docs.docker.com/compose/install/

### 다운로드

Docker Compose 안정화 버전을 다운 받기 위해 아래의 명령어를 입력합니다:

```bash
 sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
```

![image-20211118115703371](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/centos/docker.assets/image-20211118115703371.webp)

> 바로 다운로드가 진행 됩니다. 용량이 작기 때문에 금방 끝납니다.

### 실행 권한 부여

아래 명령어로 binary에 실행 권한을 부여 합니다

```bash
sudo chmod +x /usr/local/bin/docker-compose
```

### 설치 확인

아래의 명령어를 입력 해 잘 설치가 되었는지 확인 해 봅니다

```bash
docker-compose --version
```

![image-20211118115819859](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/centos/docker.assets/image-20211118115819859.webp)

Docker Compose도 설치가 완료 되었습니다. 이상입니다.

## sudo 없이 docker 명령어 실행

docker 명령어를 쓸 때마다 sudo 를 입력하기 번거로워서 유저그룹을 추가하는 방법을 따로 포스팅 해 두었습니다.

docker를 쓴다면 사실상 꼭 설정해야 한다고 생각하니, 아래 글을 클릭해서 따라 설정 해 주세요.

> [Linux, sudo 없이 명령어 실행하기 (예:docker)](https://shanepark.tistory.com/250)

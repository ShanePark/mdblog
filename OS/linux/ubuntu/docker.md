# Ubuntu 20.04 LTS ) Docker 설치하기 

## Intro

Docker는 제가 회사에서 사용하는 노트북의 OS를 Windows 에서 Ubuntu 로 변경하게 된 트리거 였습니다.

Docker를 사용하면 정말 편하게 격리된 컨테이너들을 구성해 가상화의 장점을 정말 잘 살릴 수 있습니다. 사실상 업계 표준인 만큼 접근성이 높으며 사용에 굉장히 편리합니다. 관련 레퍼런스도 어렵지 않게 찾아 볼 수 있으며 사용자들이 작성해 둔 패키지/이미지들이 넘쳐나기 때문에 뭔가를 정말 간단하게 할 수 있습니다. 윈도우즈에서도 WSL2(Windows Subsystem for Linux)를 이용해 사용은 가능 했지만 메모리나 안정성 등 여러가지 문제로 사용하는데 불편함이 많았습니다. 개발환경에서야 윈도우 가끔 쓸 수도 있지만 어쨌든 결국 도커를 운영하게 되는 서버는 리눅스 환경이 되겠죠.

https://docs.docker.com/engine/install/ubuntu/

위의 링크에서 도커에 대한 자세한 설명을 확인 하실 수 있습니다.

## Docker 설치

### 오래된 버전 삭제하기

혹시나 기존의 오래된 버전이 있는지 확실히 할 수 있으며, 있다면 최신 버전 설치를 위해 삭제 해줍니다.

```bash
sudo apt-get remove docker docker-engine docker.io containerd runc
```

### repository 설정하기

apt package index를 업데이트 하고 HTTPS를 통해 repository 를 이용하기 위해 pakcage 들을 설치 해줍니다.

```bash
sudo apt-get update
```

```bash
sudo apt-get install \
    ca-certificates \
    curl \
    gnupg \
    lsb-release
```

Docker의 Official GPG Key 를 등록합니다.

```bash
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
```

stable repository 를 등록해줍니다.

```bash
 echo \
  "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
```

### Docker Engine 설치하기

아래 명령어를 입력하면 자동으로 최신 버전이 설치 됩니다.

```bash
sudo apt-get update
sudo apt-get install docker-ce docker-ce-cli containerd.io
```

### 설치 완료

설치가 완료된 후에는

```bash
docker --version
```

를 입력해서 버전을 확인 하거나

![image-20210919181521058](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/docker.assets/image-20210919181521058.png)

 혹은 hello-world 이미지를 실행 시켜 잘 설치되었는지 확인 할 수 있습니다.

```bash
 sudo docker run hello-world
```

![image-20210919181654082](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/docker.assets/image-20210919181654082.png)

## Compose 설치

Docker Compose는 여러개의 도커 어플리케이션 컨테이너들을 정의하고 실행 할 수 있게 도와주는 툴 입니다. YAML 파일을 사용해 어플리케이션의 서비스를 설정하고 하나의 커맨드만으로 여러개의 도커 컨테이너들을 사용 할 수 있습니다.

Docker 를 설치 해도 Compose 가 딸려 오는 것은 아니기 때문에 따로 설치 해 주어야 합니다. 이전에는 stand-alone 으로만 제공되었지만 이제는 플러그인 형태로 지원되고 있습니다.

```bash
sudo apt-get update
sudo apt-get install docker-compose-plugin
```

잘 설치 되었는지 확인해봅니다.

```bash
docker compose version
## Docker Compose version v2.12.2
```

### Compose 를 standalone 형태로 설치 원할 경우

플러그인 형태가 아닌 이전처럼 standalone 형태로의 설치를 원할 경우는 아래와 같이 할 수 있습니다.

```bash
curl -SL https://github.com/docker/compose/releases/download/v2.12.2/docker-compose-linux-x86_64 -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

docker-compose --version # 설치 여부 확인
```

### sudo 없이 docker 명령어 실행

docker 명령어를 쓸 때마다 sudo 를 입력하기 번거로워서 유저그룹을 추가하는 방법을 따로 포스팅 해 두었습니다.

docker를 쓴다면 사실상 꼭 설정해야 한다고 생각하니, 아래 글을 클릭해서 따라 설정 해 주세요.

> [Linux, sudo 없이 명령어 실행하기 (예:docker)](https://shanepark.tistory.com/250)

### 마치며

Windows 나 Mac 에서는 Docker Desktop 이라는 GUI Tool 이 따로 존재하지만, Linux 환경에는 제공되지 않아왔었습니다.

하지만 2022년 5월 Docker Desktop for Linux가 세상에 나왔기 때문에,  원한다면 Mac 혹은 Windows 에서의 익숙했던 Docker Desktop을 사용 하실 수도 있습니다. https://docs.docker.com/desktop/install/debian/

다만 개인적으로 도커를 사용하며 GUI가 필요한 일은 딱히 없었습니다. 필요한 명령어들을 익히는데 약간의 시간은 걸리겠지만, 훨씬 더 빠르고 간단하게 여러가지 기능들을 활용 할 수 있기 때문에 커맨드라인을 통해 Docker를 사용하시는걸 추천드립니다.

심지어 Docker Desktop을 이용할 경우에는 리눅스 운영체제에서도 KVM/QEMU 기반의 가상머신으로 docker를 운용하기 때문에 자원을 훨씬 많이 사용합니다. 

> Linux에서도 VM으로 작동하는 이유는 몇 가지가 있지만, 그 중 첫번째는 <u>platform에 상관 없이 동일한 경험을 주기 위해서</u> 라고 합니다.

이상입니다.
# Netdata 를 활용한 시스템 모니터링

## Intro

![image-20220726103836013](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/netdata.assets/image-20220726103836013.png)

> https://github.com/netdata/netdata

Netdata는 특별한 설정이 필요 없이 실시간 분산 모니터링을 제공해주는 오픈소스 모니터링 도구 입니다. 

시스템, 하드웨어, 컨테이너, 어플리케이션들로부터 수천개의 데이터를 실시간으로 수집하며 물리/ 가상 서버 및 컨테이너, 클라우드 환경, IOT 장비등 에서 영구적으로 동작 합니다.

대부분의 Linux 배포판 뿐만 아니라 Kubernetes나 Docker 등의 컨테이너 플랫폼 및 MacOS 등에서도 `sudo` 권한 없이 설치 할 수 있습니다.

Netdata는 아래와 같은 특징들을 가지고 있습니다.

- 설정이 필요없음
- 관리가 필요없음
- 최소한의 Disk I/O 및 메모리 사용. 싱글코어 1% 만의 CPU 점유
- 빠르고 인터렉티브 한 시각화

## 설치

설치 방법은 로컬에 설치 하거나 도커를 이용해 컨테이터에 설치 할 수 있습니다.

두가지 설치 방법을 모두 알아 보겠습니다.

### 로컬에 설치

대부분의 Linux 환경에서 쉽게 설치 할 수 있는 script를 공식 제공 하고 있습니다.

![image-20220727160401846](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/netdata.assets/image-20220727160401846.png)

```bash
wget -O /tmp/netdata-kickstart.sh https://my-netdata.io/kickstart.sh && sh /tmp/netdata-kickstart.sh
```

스크립트를 실행 하면 이것 저것 필요한 패키지들을 알아서 설치 합니다.

![image-20220727160506533](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/netdata.assets/image-20220727160506533.png)

> 중간중간 설치할지 물어볼 때 마다 Y 혹은 엔터키를 입력 해 줍니다.

![image-20220727160636252](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/netdata.assets/image-20220727160636252.png)

> 설치 완료

오래 걸리지 않고 설치가 완료 되었습니다.

NetData는 19999 포트를 통해 접근 할 수 있는데요, 외부에서 보통 방화벽 때문에 접근이 안될 수 있으니 먼저 telnet으로 방화벽 여부를 확인 해 줍니다.

![image-20220727160908955](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/netdata.assets/image-20220727160908955.png)

> 19999 포트로 외부에서 접속이 안되는 상태

방화벽을 여는건 여러 가지 방법이 있습니다. 권장하지는 않지만 잠깐 테스트 하고 말 서버라면 ` iptables -F` 로 모든 규칙을 제거하는 것도 방법입니다.

![image-20220727160946019](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/netdata.assets/image-20220727160946019.png)

> 해당 포트의 방화벽을 해제하고 나서 접속이 되는 상태

이제 포트가 열려있는걸 확인 했으면, 브라우저를 이용해 19999 포트로 요청을 보내 봅니다.

![image-20220727161148087](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/netdata.assets/image-20220727161148087.png)

> 정상 작동하고 있는 모습

저는 우분투를 사용하고 있어 삭제시에는 아래의 명령어롤 이용해 제거 했습니다.

```bash
sudo apt remove netdata
```

### Docker

도커 환경에도 설치 할 수 있는데요, 일단 도커가 설치되어 있지 않은 분은 [Ubuntu 20.04 LTS ) Docker 설치하기](https://shanepark.tistory.com/237) 글을 참고해 먼저 설치 해 주세요.

도커가 설치 되었다면 아래의 명령어를 실행 해, 도커 컨테이너에서 Netdata를 실행 하실 수 있습니다.

```bash
docker run -d --name=netdata \
  -p 19999:19999 \
  -v netdataconfig:/etc/netdata \
  -v netdatalib:/var/lib/netdata \
  -v netdatacache:/var/cache/netdata \
  -v /etc/passwd:/host/etc/passwd:ro \
  -v /etc/group:/host/etc/group:ro \
  -v /proc:/host/proc:ro \
  -v /sys:/host/sys:ro \
  -v /etc/os-release:/host/etc/os-release:ro \
  --restart unless-stopped \
  --cap-add SYS_PTRACE \
  --security-opt apparmor=unconfined \
  netdata/netdata
```

![image-20220727164650039](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/netdata.assets/image-20220727164650039.png)

> 설치 완료 후 19999 포트로 접속 한 모습

### Docker-compose

위에서 실행했던 명령어를 바탕으로 compose 파일도 작성 할 수 있습니다.

```yaml
services:
  netdata:
    image: netdata/netdata
    container_name: netdata
    restart: unless-stopped
    ports:
      - 19999:19999
    volumes:
      - netdataconfig:/etc/netdata
      - netdatalib:/var/lib/netdata
      - netdatacache:/var/cache/netdata
      - /etc/passwd:/host/etc/passwd:ro
      - /etc/group:/host/etc/group:ro
      - /proc:/host/proc:ro
      - /sys:/host/sys:ro
      - /etc/os-release:/host/etc/os-release:ro
```

다만, compose 실행시 `Cannot create directory '/var/lib/netdata/registry'. # : Invalid argument` 에러가 발생하는 경우가 있는데 그럴때는 볼륨 혹은 권한설정이 잘못 되었을 수 있으니 한번 확인 해 보세요.

![image-20220727172326753](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/netdata.assets/image-20220727172326753.png)

> 함께 실행중인 각각의 container도 조회가 됩니다. 컨테이너 정보를 불러오는데는 조금 시간이 걸리기때문에 바로 안보인다고 당황하지 말고 조금 기다렸다가 새로고침 해 보세요.

### Docker Container 이름 표기

컨테이너들의 이름이 해시코드로 표현되면 각각 어떤 컨테이너인지 알아보기가 참 곤란합니다.

이때는 몇가지 방법이 있는데 아래의 링크를 참고 했습니다.

> https://learn.netdata.cloud/docs/agent/packaging/docker#docker-container-names-resolution

1. Docker socket proxy (safest option)

socket proxy를 사용하는 좀 더 안전한 방법을 가장 추천한다고 합니다. 다만 컨테이너가 하나 더 추가되는 단점은 있습니다.

```yaml
version: '3'
services:
  netdata:
    image: netdata/netdata
    # ... rest of your config ...
    ports:
      - 19999:19999
    environment:
      - DOCKER_HOST=proxy:2375
  proxy:
    image: tecnativa/docker-socket-proxy
    volumes:
     - /var/run/docker.sock:/var/run/docker.sock:ro
    environment:
      - CONTAINERS=1
```

>  2375 로 설정되어있는 port를 본인의 proxy 포트로 변경해주라고 합니다.

2. Docker socket 에 그룹 접근권한 부여

이 경우에는 가장 손쉽게 설정이 가능하지만 netdata 유저에게 모든 도커 서비스의 소켓 연결 권한을 부여하는 것이기 때문에 신중하게 고민하고 사용해야 합니다.

```yaml
version: '3'
services:
  netdata:
    image: netdata/netdata
    # ... rest of your config ...
    volumes:
      # ... other volumes ...
      - /var/run/docker.sock:/var/run/docker.sock:ro
    environment:
      - PGID=[GROUP NUMBER]
```

> docker.sock 경로를 볼륨 지정 해 주기만 하면 됩니다.



![image-20220727173326986](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/netdata.assets/image-20220727173326986.png)

설정해 준 이후에는 컨테이너 이름이 정상적으로 표기되는 것이 확인 됩니다!

## 마치며

![image-20220727175645617](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/netdata.assets/image-20220727175645617.png)

설치시 기본적으로 타임존이 UTC로 되어 있기 때문에, seoul을 검색해서 타임존을 UTC+9 으로 변경 해 주시고 사용 하면 됩니다.

브라우저의 개발자 도구를 켜보셨다면 눈치 채셨을텐데요

![image-20220727175820389](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/netdata.assets/image-20220727175820389.png)

끊임없이 API 요청을 보내고, 받아온 JSON 정보로 차트를 계속 새로 그려주고 있습니다.

이처럼 netdata는 REST API도 제공을 하고 있는데요.

https://editor.swagger.io/?url=https://raw.githubusercontent.com/netdata/netdata/master/web/api/netdata-swagger.yaml

위의 Swagger 문서에 모든 API가 정리 되어 있기 때문에, 참고를 해서 필요하다면 필요한 API를 요청해 통계정보를 그리는 것도 가능하겠습니다.

이상입니다.

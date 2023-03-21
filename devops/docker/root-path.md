# Docker 용량 부족할 때 저장 경로 변경

## Intro

>  no space left on device

도커 컨테이너를 띄우려는데 용량이 없다는 에러가 발생했습니다. 

사실 로컬에서는 평소에 용량 관리가 어느 정도 된다면 만나지 않을 상황인데, 클라우드에서는 메인 SSD 용량은 적게 가져가고 하드디스크나 NAS를 추가로 마운트하는 식으로 관리하다 보니 금방 찰 수 있습니다.

`No space left on device` 에러를 처음 만난다면 일단 정리를 한번 해 주는 게 좋습니다.

```bash
docker system prune
```

이렇게 하면 모든 멈춰있는 컨테이너, 네트워크, 사용하지 않는 이미지나 빌드캐시 등을 제거해줍니다. 불필요하고 반복된 작업으로 쓸모없는 데이터들이 많이 쌓였었다면 이 명령 하나로 당분간은 해결되기도 합니다.

하지만 전체적으로 용량 확인 해 보면

```bash
df -h
```

![image-20230321150936482](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/docker/root-path.assets/image-20230321150936482.png)

메인 SSD에 3.5G뿐이 남지 않은 상황이고 정리 정돈만으로는  지속이 불가능합니다.

이럴때는 도커의 이미지,컨테이너,볼륨 등을 저장하는 경로를 용량이 넉넉한 다른 경로로 변경해주는 것이 필요합니다. 위 사진에서 보면 /data 경로에 100G의 하드디스크를 마운트 해 둔 상황입니다.

## 변경

### 기존 경로 확인

변경에 앞서 기존에 도커에 관련된 데이터들이 저장되던 경로를 확인해 보겠습니다.

```bash
docker info
```

![image-20230321151127822](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/docker/root-path.assets/image-20230321151127822.png)

Docker Root Dir 이 `/var/lib/docker` 로 되어 있습니다.

아래 명령어로도 쉽게 RootDir을 확인 할 수 있습니다.

```bash
docker info -f '{{ .DockerRootDir }}'
```

![image-20230321151408596](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/docker/root-path.assets/image-20230321151408596.png)

### 새로운 경로 생성

- 먼저 도커 서비스를 전부 중단 해 줍니다.

```bash
sudo systemctl stop docker
sudo systemctl stop docker.socket
sudo systemctl stop containerd
```

- 필요하다면 새로운 Docker Root 를 저장할 곳의 상위 폴더를 생성해 줍니다.

``` bash
mkdir -p /data
```

- 기존 데이터를 새로 생성한 폴더로 모두 이동해줍니다. 

```bash
sudo mv /var/lib/docker /data/
```

이후 저는 docker 폴더 이름을 좀 더 명확히 하기 위해 docker-root 라는 이름으로 변경해주었지만, 꼭 그럴 필요는 없습니다. `mv /data/docker /data/docker-root`

이제 해당 폴더 안에 어떤 구조로 파일들이 저장되어있는지 확인 해 보겠습니다.

![image-20230321152305120](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/docker/root-path.assets/image-20230321152305120.png)

> `/data/docker-root`로 옮겨진 상태

### 경로 설정

이제 새로 생성한 경로를 docker에게 알려주어야 합니다.

`/etc/docker` 경로에 daemon.json 파일을 생성해 아래와 같이 새로운 data-root에 대한 정보를 입력 합니다.

```bash
sudo vi /etc/docker/daemon.json
```

![image-20230321151700762](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/docker/root-path.assets/image-20230321151700762.png)

```json
{
  "data-root": "/data/docker-root"
}
```

모든 설정은 이걸로 끝입니다.

### 확인

docker를 재실행합니다.

```bash
sudo systemctl start docker
```

RootDir 설정이 제대로 변경되었는지 아래의 명령어를 입력해 확인해봅니다.

```bash
docker info -f '{{ .DockerRootDir}}'
```

![image-20230321152427394](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/docker/root-path.assets/image-20230321152427394.png)

> 정상적으로 잘 변경된 상태

이제는 용량 걱정 없이 도커를 사용할 수 있습니다.

원래 설정대로 돌리고 싶다면 지금 했던 역순으로 서비스 중단 후 `/etc/docker/daemon.json` 파일을 제거하고 다시 서비스를 실행해주기만 하면 됩니다.

이상입니다. 

**References**

- https://www.ibm.com/docs/en/z-logdata-analytics/5.1.0?topic=compose-relocating-docker-root-directory
- https://www.baeldung.com/ops/docker-image-change-installation-directory
# Docker 로그 파일 용량 제한

## Intro

Docker 컨테이너를 오래 실행하다 보면 로그 파일이 계속 쌓이면서 디스크 공간을 차지하는 문제가 발생한다.

Docker의 기본 로그 드라이버는 `json-file`이며, 별도로 설정하지 않으면 로그 파일 크기 제한 없이 계속 증가한다. 결국 서버의 디스크가 꽉 차서 장애가 발생할 수도 있다. 이를 방지하려면 로그 파일의 크기를 제한하는 설정을 적용하는 것이 중요하다.

![image-20250312095617219](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/docker/docker-logfile.assets/1.webp)

> 끝없이 커진 로그파일이 결국 장애를 일으켜버렸다.

이번 글에서는 Docker 컨테이너의 로그 파일 크기를 제한하는 방법과 기존 로그 파일을 정리하는 방법, 그리고 Docker Compose에서 설정하는 방법까지 알아본다.

## Docker 로그 크기 제한

Docker 로그 크기 제한 방법은 크게 3가지로 나뉜다.

1. 개별 컨테이너 실행 시 로그 제한 설정
2. Docker 데몬 전체 설정 (`daemon.json` 수정)
3. Docker Compose에서 설정 적용

### 1. 개별 컨테이너 실행 시 로그 제한

컨테이너를 실행할 때 `--log-opt` 옵션을 추가하면 특정 컨테이너의 로그 크기를 제한할 수 있다.

```bash
docker run -d \
  --log-driver=json-file \
  --log-opt max-size=100m \
  --log-opt max-file=3 \
  --name my_container \
  my_image
```

- `max-size=100m` → 로그 파일 최대 크기를 100MB로 제한
- `max-file=3` → 최대 3개의 로그 파일 유지

즉, 로그 파일 크기가 100MB를 넘으면 새 파일이 생성되며, 가장 오래된 파일은 삭제된다.

하지만 이 방법은 특정 컨테이너에만 적용되므로, 전체 컨테이너에 적용하려면 Docker 데몬 설정을 수정해야 한다.

### 2. Docker 데몬 전체 설정 (`daemon.json` 수정)

모든 컨테이너에 대해 기본적으로 로그 크기 제한을 적용하려면 `/etc/docker/daemon.json` 파일을 수정해야 한다.

> `daemon.json` 파일이 없을 수도 있으니 먼저 확인하고, 없으면 새로 생성한다.

```bash
sudo vi /etc/docker/daemon.json
```

아래 내용을 추가한다.

```json
{
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "100m",
    "max-file": "3"
  }
}
```

이제 Docker 데몬을 재시작해서 설정을 적용한다.

```bash
sudo systemctl restart docker
```

설정이 적용되었는지 확인하려면 아래 명령어를 실행해 보자.

```bash
docker run --name log-test hello-world
docker inspect --format='{{.HostConfig.LogConfig}}' log-test

# 출력 결과 {json-file map[max-file:3 max-size:100m]}
```

하지만 이 방법도 기존의 컨테이너에는 적용되지 않고 새로 생성하는 컨테이너에만 적용된다.

### 3. Docker Compose에서 로그 제한 설정

Docker Compose를 사용할 경우, `docker-compose.yml` 파일에서 `logging` 옵션을 추가하면 된다.

```yaml
services:
  my_service:
    image: my_image
    container_name: my_container
    logging:
      driver: "json-file"
      options:
        max-size: "100m"
        max-file: "3"
```

이 설정을 적용한 뒤, 기존 컨테이너를 다시 실행해야 한다.

```bash
docker-compose up -d
```

마찬가지로 적용이 잘 되었는지도 한번 확인해준다.

```bash
docker inspect {컨테이너명} | grep -A5 "LogConfig"
```

## 기존 로그 파일 정리

이미 쌓여 있는 로그 파일을 정리하려면 `truncate` 명령어를 사용하는 것이 가장 안전한 방법이다.

### `truncate` vs `rm`

로그 파일을 정리할 때 `rm`을 사용하면 곤란한 상황이 온다.

`rm` 명령어로 로그 파일을 삭제하면 Docker가 여전히 삭제된 파일을 잡고 있어서 디스크 공간이 해제되지 않는 문제가 발생해 기껏 삭제해도 디스크에 빈 공간이 늘어나지 않는다. 

대신 `truncate`를 사용하면 파일을 삭제하지 않고 크기만 0으로 만들어 안전하게 정리할 수 있다.

```bash
sudo truncate -s 0 /var/lib/docker/containers/<컨테이너ID>/*-json.log
```

이 방법을 사용하면 Docker가 기존 파일을 유지하면서도 새로운 로그를 정상적으로 기록할 수 있다.

### 현재 로그 파일 크기 확인

어떤 컨테이너의 로그가 많이 쌓였는지 확인하려면 다음 명령어를 사용한다.

```bash
sudo sh -c 'du -sh /var/lib/docker/containers/*/*-json.log'
```

이제 용량이 큰 로그 파일을 `truncate`를 사용해 정리하면 된다.

## 결론

Docker 컨테이너의 로그 파일이 무제한으로 증가하는 것을 방지하려면 다음과 같은 방법을 적용하면 된다.

1. 개별 컨테이너 실행 시 `--log-opt` 옵션 사용
2. 모든 컨테이너에 적용하려면 `/etc/docker/daemon.json` 수정
3. Docker Compose를 사용할 경우 `logging` 옵션 추가
4. 기존 로그 파일 정리는 `truncate -s 0`을 사용하여 안전하게 정리

특히 운영 환경에서는 로그 파일이 디스크를 꽉 채우는 불상사를 방지하도록 `max-size`와 `max-file`을 설정하는 것이 필수다.
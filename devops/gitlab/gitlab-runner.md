# Gitlab Runner 등록

## Intro

Gitlab 에서 CI/CD 파이프라인을 이용해 Merge Request를 할 때 마다 자동으로 빌드를 실행해서 문제가 없는지를 자동으로 테스트 하려는 **그럴싸한 계획**을 가지고 있었습니다. 

테스트 과정에서 꽤나 무거운 스프링 컨테이너를 띄우지만 ~~로컬에서는 어렵지 않게 돌렸기 때문에~~ 큰 걱정은 안했습니다. 야심차게 Merge Request와 함께 파이프라인이 작동을 시작했고, CPU와 메모리 사용량이 급격히 증가하며 결국 버티지 못하고 사내 깃랩서버는 그대로 뻗어버렸습니다.

## Gitlab Runner 

> GitLab Runner is an application that works with GitLab CI/CD to run jobs in a pipeline.

그리하여 같은 잘못을 되풀이 하지 않기 위해 CI를 작동할 어플리케이션을 외부에 따로 두기로 했습니다.

자원이 넉넉한 외부의 자원에 Gitlab Runner를 설치 및 실행 시키고 CI/CD 작업이 필요할 때 대신 그 일을 해줍니다.

### 설치

외부의 적당히 여유있는 리소스를 가지고 있는 서버에 Gitlab Runner를 설치 합니다.

리눅스 패키지, 도커, 소스코드, GitLab Environment Toolkit 등 다양한 설치 방법이 존재하지만 저는 도커를 이용하겠습니다. 나중에 Executor를 설정할 때 Docker를 이용하고 싶다면, 도커 소켓도 볼륨으로 등록해주어야 합니다.

```bash
docker run --restart unless-stopped -d --name gitlab-runner -v /var/run/docker.sock:/var/run/docker.sock gitlab/gitlab-runner:latest
```

![image-20230329170517216](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/gitlab/gitlab-runner.assets/image-20230329170517216.png)

> gitlab-runner:latest 가 정상적으로 실행된 상태

### 설정

이제 설치한 Gitlab Runner를 등록해주어야 합니다. 컨테이너에 접속 후 register 하는 방법을 이용합니다.

 runner를 등록하는 방법이 Deprecated 되어 있고 추후에 [Gitlab Runner token](https://docs.gitlab.com/ee/architecture/blueprints/runner_tokens/) 을 추가한다고 하는데 당장은 `devops` 스테이지에 있어서 기존의 방법을 사용하도록 하겠습니다.

Gitlab 홈페이지에 등록된 예시는 아래와 같습니다.

```bash
docker run --rm -v /srv/gitlab-runner/config:/etc/gitlab-runner gitlab/gitlab-runner register \
  --non-interactive \
  --executor "docker" \
  --docker-image alpine:latest \
  --url "https://gitlab.com/" \
  --registration-token "PROJECT_REGISTRATION_TOKEN" \
  --description "docker-runner" \
  --maintenance-note "Free-form maintainer notes about this runner" \
  --tag-list "docker,aws" \
  --run-untagged="true" \
  --locked="false" \
  --access-level="not_protected"
```

> https://docs.gitlab.com/runner/register/

하지만 굳이 한번에 하지 않고 `register` 명령을 띄우고 하나씩 입력해 보겠습니다. 

1. Gitlab Runner 컨테이너에 접속

```bash
docker exec -it gitlab-runner bash
```

2. `register` 명령 실행

```bash
gitlab-runner register
```

이제 Gitlab 프로젝트에서 `Settings` > `CI/CD` > `Runners` > `Expand` 에 작성되어있는 URL 및 등록 토큰 정보를 확인하고

![image-20230329171000454](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/gitlab/gitlab-runner.assets/image-20230329171000454.png)

위의 정보를 토대로 하나씩 입력해줍니다.

![image-20230329172629632](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/gitlab/gitlab-runner.assets/image-20230329172629632.png)

> GitLab 인스턴스의 URL, 등록 토큰, 러너에 대한 설명, 태그명, 추가 노트를 입력 한 후에는 Executor를 선택합니다. Executor로 Docker를 선택한 경우에는 기본 Docker image도 선택 합니다.

executor는 `docker`, 기본 Docker image는 많이 쓰이는 `alpine:latest`를 사용했습니다.

등록을 마친 후에 다시 확인해보면 방금 등록한 runner가 확인됩니다.

![image-20230329172913941](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/gitlab/gitlab-runner.assets/image-20230329172913941.png)

참고로 러너를 잘못 등록했을 경우에는 아래의 명령어들을 이용해 제거해줍니다.

- 등록된 gitlab-runner 확인

```bash
gitlab-runner list
```

- 등록된 gitlab-runner 제거

```bash
gitlab-runner unregister --name <러너 이름>
```

## CI/CD

### 테스트

이제 등록한 Runner를 이용할 `.gitlab-ci.yml` 파일을 작성해 줍니다.

```yaml
stages:
  - build

build_job:
  stage: build
  image: maven:latest
  script:
    - mvn clean install
  tags:
    - idr_gitlab_runner
  rules:
    - if: $CI_PIPELINE_SOURCE == "merge_request_event"
```

> Merge Request 이벤트가 발생하면 메이븐 빌드를 하는 간단한 예제

위의 예시처럼 `tags`에 방금 등록한 Runner의 태그를 작성해주면, 해당 러너에서 CI/CD 파이프라인이 동작하게 됩니다.

### Docker daemon 에러

그런데 첫번째 테스트에서 저는 에러가 발생했습니다.

![image-20230329180252001](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/gitlab/gitlab-runner.assets/image-20230329180252001.png)

> ERROR: Preparation failed: Cannot connect to the Docker daemon at unix:///var/run/docker.sock. Is the docker daemon running? (docker.go:754:0s)

일단 상단을 보면 `Running with gitlab-runner` 가 보입니다. 새로 설치한 Runner가 동작중인건 확인이 되었습니다. 하지만 에러가 발생한 이유는 에러 메시지처럼 docker 호스트의 docker daemon에 접속하지 못했기 때문입니다. 때문에 Executor를 docker로 선택할 거라면 반드시 `-v /var/run/docker.sock:/var/run/docker.sock` 옵션으로 볼륨을 걸어서 호스트 시스템의 Docker 데몬소켓에 접근할 수 있게 해 주어야 합니다.

### 성공

이제 다시 테스트를 해 보면..

![image-20230329174412020](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/gitlab/gitlab-runner.assets/image-20230329174412020.png)

> CPU와 메모리를 엄청나게 끌어다쓰며 열심히 작업중인 Gitlab Runner의 모습

도커 러너가 열일을 하고 있습니다.

![image-20230329174905792](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/gitlab/gitlab-runner.assets/image-20230329174905792.png)

> Jobs에서 확인 했을 때, 테스트도 문제없이 성공

![image-20230330152815740](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/gitlab/gitlab-runner.assets/image-20230330152815740.png)

새로 생성한 Gitlab Runner에서 작업을 성공적으로 완료했습니다.

이상입니다. 

**References**

- https://docs.gitlab.com/runner/install
- https://docs.gitlab.com/runner/configuration/
- https://docs.gitlab.com/runner/commands/
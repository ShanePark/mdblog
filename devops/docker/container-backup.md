# Docker Container 백업

> 도커 컨테이너 백업하기

## Intro

최근 집에서 서버로 사용중이던 컴퓨터가 알수없는 이유로 자꾸 다운이 되는 바람에 우분투 서버를 한번 새로 깔아보려고 합니다. 저번에 아무 대책 없이 다 날리고 우분투를 깔았다가 서버를 다시 셋팅하느라 참 고생을 했었는데.. 그 때 그 고생을 줄이기 위해 로컬에는 아무것도 올리지 않고 도커만 사용하고 있습니다.

사실 회사에서는 능력이 출중하신 선배님께서 docker-compose 를 이용해 모두 세팅을 마쳐 두시고, 볼륨도 다 지정 해 두었기에 서버를 옮기거나 한다고 해도 `docker-compose.yml` 을 비롯한 셋팅 파일과 볼륨 폴더만 쏙 빼가면 되는데.. 제가 세팅했을 때에는 그런 환경을 몰랐을 때라 도커도 힘겹게 셋팅했던 기억이 납니다. 지금이라도 compose로 설정을 하는게 맞긴 하지만 당장에 툭하면 서버가 죽는 상황에서 평일에 잠깐 시간내어 살려내는 와중에 여유가 없어 주말까지 임시 방편으로라도 버티기 위해 컨테이너를 통째로 백업 해 보려고 합니다.

## 컨테이너 백업

### commit

도커 상태보기

```bash 
docker ps
```

도커 컨테이너를 도커 이미지로 저장하기 위해 commit

``` zsh
docker commit -p [컨테이너 ID] [저장할 이름]
```

> -p 옵션은 커밋을 위해 컨테이너를 일시 정지 시키는 옵션

![image-20211125210633380](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/container-backup.assets/image-20211125210633380.png)

두개의 컨테이너 중 하나 남은 elasticsearch 컨테이너도 마저 이미지로 만들겠습니다.

```bash
docker commit -p 01c2df64c1d0 elastic-container
```

이제 이미지가 잘 만들어 졌는지 확인 해 보겠습니다.

```bash
docker images
```

![image-20211125211708753](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/container-backup.assets/image-20211125211708753.png)

아.. 툭하면 다운 되던 이유를 알 법도 합니다. 엘라스틱서치 컨테이너가 왜 20.2GB를 차지하고 있는지 이유는 아직 모르겠지만 분명 엘라스틱 서치가 문제였겠네요.. 기껏해야 오라클에 있는 데이터만 있는데 말이죠.. 어쨌든 문제를 알았으니 다행입니다.

각설하고.. 방금 만든 두 개의 이미지가 잘 생성 된 것이 확인 됩니다.

### 이미지 저장

도커 이미지를 이제 따로 저장 해야 하는데요, 두가지 방법이 있습니다.

1. 하나는 도커 호스트 시스템에 이미지를 배포 하는 건데요, 

```bash
docker push elastic-container
```

이런 명령어로 하면 되는데, 로그인도 해야 하고 일단 저 이미지가 20GB가 넘다보니 좋은 방법이 아닙니다.

2. 또 다른 방법으로는 tar파일로 저장하는 방법 입니다. 이때는 save 명령어를 사용 합니다.

용량이 20기가가 넘는 이미지를 백업하려니 이게 뭐 하는 짓인가 싶긴 하지만.. 이왕 시작한거 하던건 마쳐야겠죠..

```bash
docker save -o ~/elastic-container.tar elastic-container
```

```bash
docker save -o ~/oracle-container.tar oracle-container
```

이미지가 잘 생성 되었습니다.

![image-20211125220352644](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/container-backup.assets/image-20211125220352644.png)

## 컨테이너 복원

이제 앞서 이미지로 만들어 둔 컨테이너를 복원 하는 방법에 대해 알아보겠습니다.

이미지를 배포해 push 한 경우에는 간단하게 run 명령어를 사용해서 해당 이미지로부터 새로운 인스턴스를 시작하면 됩니다.

하지만 저는 tar로 묶어서 백업 했기 때문에, 먼저 load 명령어를 이용해 이미지로 불러와야 합니다. load 명령어를 사용하기에 앞서 혼동을 피하기 위해 방금 만들었던 이미지를 먼저 삭제 하게습니다.

```bash
 docker rmi elastic-container
 docker rmi oracle-container:latest
```

![image-20211125220541583](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/container-backup.assets/image-20211125220541583.png)

> 확실히 이미지가 삭제 된 것을 확인 했습니다.

이제 방금 저장했던 tar 파일들을 이미지로 불러 오겠습니다.

```bash
docker load -i ~/elastic-container.tar
```

![image-20211125220911191](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/container-backup.assets/image-20211125220911191.png)

> 19.18 GB의 이미지를 불러오는 과정은 정말 지옥입니다. 교훈삼아 다시는 이런일이 없도록 해야합니다.

워낙 오래 걸려서 그동안 엘라스틱 컨테이너에 접속 해서 도대체 어떤 일이 일어나고 있는지 알아봐야 겠습니다..

컨테이너에 접속 할 때는 아래의 명령어를 사용 하면 됩니다. elastic 자리에는 접속할 컨테이너 아이디나 이름을 입력합니다.

```bash
docker exec -it elastic /bin/bash
```

이미지  load가 완료 되면, 이제 이미지가 잘 불러 와 졌는지 확인 해봅니다.

```bash
docker images
```

![image-20211125221354608](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/container-backup.assets/image-20211125221354608.png)

위와 같이 이미지 목록에 방금 백업해두었던 컨테이너의 이미지가 나온다면, 이제 run 명령어로 해당 이미지를 다시 컨테이너로 불러오면 됩니다.

## 마치며

어렵지 않게 컨테이너를 백업 하고 다시 불러오는 과정을 해 보았습니다. 다른 머신에서 컨테이너를 띄우는 과정 이었다면 .tar 파일만 이동식 디스크를 사용하거나 scp 등을 이용해 전송 하고 다시 불러오면 되겠네요.

사실 저는 컨테이너를 백업 하고 우분투를 포맷 하려고 했는데, 컨테이너가 이 모양인걸 확인 했으니 포맷할게 아니고 컨테이너를 고쳐야 겠네요! 이상입니다. 
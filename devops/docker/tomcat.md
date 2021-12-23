# Docker) 도커로 Tomcat 컨테이너 실행

## Intro

log4j 취약점 테스트 진행 중 로컬에서 테스트하기에는 부담이 있어서 도커를 이용해 가상환경에서 하고자 합니다.

간단하게 톰캣만 돌리면 되는데, 우분투 컨테이너를 생성해서 JDK 설치하고 jar파일을 바로 실행 해 돌리다가, 이제 war로된 프로젝트로 테스트를 하려니 외장 톰캣이 또 필요해져서

어차피 이렇게 하나씩 올리면서 할꺼, 그냥 톰캣 컨테이너 만들어진걸 쓰게 되었습니다.

제가 나중에 필요할때 쉽게 찾아 할 수 있게, 또한 혹시나 또 필요한분이 있으시면 도움이 되었으면 하는 마음에 글로 간단하게 정리 해 올려 둡니다.

## Tomcat

### 공식 Tomcat 이미지

> https://hub.docker.com/_/tomcat

딱히 복잡할 건 없고

```zsh
docker run tomcat:9.0
```

이면 간단하게 공식 이미지를 받아 와 실행 합니다.

하지만,  톰캣을 쉽게 테스트 하기 위해서는 로컬 포트를 연결 해 주는게 좋겠죠?

이왕 하는거 컨테이너에 이름도 붙여서 생성 하겠습니다. 아래의 명령어를 입력 합니다.

```zsh
docker run --name tomcat -p 8080:8080 tomcat:9.0
```

아시겠지만,

- --name 은 컨테이너의 이름을 정해주는 옵션 입니다.

- -p 옵션은 컨테이너의 포트를 로컬과 연결 해 줍니다.

마지막 tomcat:9.0은 사용하고자 하는 도커 이미지를 적어 주면 됩니다.

### 테스트

간단하게 브라우저에 localhost:8080을 요청하면 됩니다. 

![image-20211223220223195](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/tomcat.assets/image-20211223220223195.png)

404 Not Found가 뜨니 톰캣이 제대로 안뜬건가? 싶을 수도 있지만. 톰캣이 떴기 때문에 해당 응답을 보내 준 것입니다.

톰캣이 잘 작동하고 있다는 뜻이며, 단지 WAS가 처리할 웹 어플리케이션이 하나도 없기 떄문에 404가 뜨는 것 입니다.

### 컨테이너에 접속

```zsh
docker exec -it tomcat /bin/bash
```

![image-20211223220524717](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/tomcat.assets/image-20211223220524717.png)

>  도커 컨테이너의 이름을 설정 해 주지 않았다면 `docker ps`를 입력하고 왼쪽에 나오는 CONTAINER ID를 제가  tomcat이라고 쓴 자리에 대신 쓰면 접속 할 수 있습니다.

![image-20211223220642861](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/tomcat.assets/image-20211223220642861.png)

> 접속하면 자동으로 /usr/local/tomcat 폴더가 열리며, 해당 폴더에 webapps 등 필요한 폴더가 모두 있습니다.
>
> webapps폴더에 아무것도 없으며, 그렇기 때문에 localhost:8080 접속시 404 에러가 뜬 것 입니다.

### 컨테이너에 파일 복사

로컬에 있는 파일을 컨테이너에 복사 할때는 docker cp 명령어를 사용합니다.

`docker cp 로컬파일위치 컨테이너명:컨테이너내부주소` 형식으로 명령어를 입력 해 주면 됩니다.

```zsh
docker cp gaia-0.0.1-SNAPSHOT.war tomcat:/usr/local/tomcat/webapps
```

![image-20211223221502193](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/tomcat.assets/image-20211223221502193.png)

명령어를 입력 한 후에

![image-20211223221611810](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/tomcat.assets/image-20211223221611810.png)

컨테이너 내부에서 webapps 폴더를 확인 해 보니 해당 war 파일이 들어 와 있습니다.

톰캣은 기본적으로 어플리케이션이 들어오면 자동으로 실행 해 주기 때문에..

`http://localhost:8080/gaia-0.0.1-SNAPSHOT/` 주소를 치고 확인 해보면

![image-20211223221711732](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/tomcat.assets/image-20211223221711732.png)

해당 톰캣에서 등록한 어플리케이션이 잘 실행 되는것을 확인 할 수 있습니다.

## 도커 타임존 변경

Docker 컨테이너의 시간대는 기본적으로 UTC로 되어 있습니다.

이게 Oracle Database와 연결을 할라고 하면 시간존 문제가 있기 때문에 맞춰줘야 합니다. 

run 할 때 부터 `-e TZ=Asia/Seoul` 옵션으로 환경 변수를 주는 방법도 있지만,

tzdata를 이용해 쉽게 변경 할 수 있습니다.

```zsh
apt-get install tzdata
```

배포판을 확인해보니 Debian Linux 입니다. apt를 이용해서 설치하는데, 이미 설치가 되어 있었습니다.

```zsh
 export TZ=Asia/Seoul
```

로 쉽게 변경하고 `date` 명령어를 쳐 보면 시간대가 변경된 것을 확인 할 수 있습니다.

![image-20211223231744856](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/tomcat.assets/image-20211223231744856.png)

하지만 해당 방법으로 변경해보니, 도커를 재 시작 할 때 마다 바로 다시 UTC 타임존으로 돌아왔습니다.

어쩔 수 없이 컨테이너를 다시 생성해 줍니다. 삭제를 먼저 하고..

```zsh
docker stop tomcat
docker rm tocat

```

새로 생성 해 줍니다.

```zsh
docker run --name tomcat -p 8080:8080 -e TZ=Asia/Seoul tomcat:9.0
```

![image-20211223232738294](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/tomcat.assets/image-20211223232738294.png)

새로 생성한 컨테이너에 들어가보니 시간대가 정상적으로 잘 설정 되어 있습니다. war파일만 다시 `docker cp`로 복사해주면 끝 입니다.

시간대 변경 후에는 오라클 DB와 연결이 안되던 문제도 정상적으로 해결 되었습니다.

![image-20211223232915796](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/tomcat.assets/image-20211223232915796.png)

이상입니다.

 
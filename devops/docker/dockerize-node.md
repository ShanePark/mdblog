# Nojde.js Web app을 도커 이미지로 만들고 Docker Hub에 올리기

## Intro

Node.js로 작성한 웹 어플리케이션을 간단하게 도커 이미지로 만든 후에 해당 이미지를 도커 허브에 push 해서 어디서나 받아서 사용하는 방법을 알아 보도록 하겠습니다.

## 1. Node.js 웹 어플리케이션을 도커 이미지로

### Node.js App 생성

일단 준비된 Node.js 웹 어플리케이션이 없다면 테스트를 위해 생성 해 줍니다.

준비된 어플리케이션이 따로 있다면 이 과정은 생략 하실 수 있습니다.

> Node.js 앱을 생성 할 빈 폴더를 생성 한 뒤 해당 폴더에 `package.json` 파일을 생성 해 줍니다.

```json
{
  "name": "docker_web_app",
  "version": "1.0.0",
  "description": "Node.js on Docker",
  "author": "First Last <first.last@example.com>",
  "main": "server.js",
  "scripts": {
    "start": "node server.js"
  },
  "dependencies": {
    "express": "^4.16.1"
  }
}
```

`package.json` 파일을 작성 한 후에는 `npm install`을 생성 해 줍니다. npm 버전이 5 이상이라면 해당 명령을 통해 `package-lock.json` 파일을 생성 하는데, 이는 도커 이미지에 복사 됩니다.

![image-20220530135138486](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/dockerize-node.assets/image-20220530135138486.png)

`server.js` 파일도 이어서 생성 해 줍니다.

> 이 역시 준비된 어플리케이션이 있다면 생략 가능합니다.

```js
'use strict';

const express = require('express');

// Constants
const PORT = 8080;
const HOST = '0.0.0.0';

// App
const app = express();
app.get('/', (req, res) => {
  res.send('Hello World');
});

app.listen(PORT, HOST);
console.log(`Running on http://${HOST}:${PORT}`);
```

작성 후에는 `node server.js` 를 실행 해 서버가 동작하는지 테스트 해볼 수 있습니다.

![image-20220530135319239](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/dockerize-node.assets/image-20220530135319239.png)

### Dockerfile 생성

이제 위에서 작성한 Node.js 앱을 도커 컨테이너 내부에서 실행시키기 위해 이미지를 생성 해야 합니다.

이를 위해서 제일 먼저 Dockerfile을 생성 하도록 하겠습니다.

```bash
vi Dockerfile
```

Docker 파일을 생성하기 위해 vim 에디터를 이용 하도록 하겠습니다.

```bash
FROM node:16
```

이제 어떤 이미지로 부터 빌드를 해낼 건지 정해야 하는데, node 16 버전의 이미지를 선택 하였습니다.

다음으로는 이미지 내부에서 어플리케이션 코드들을 저장 해 둘 경로를 설정 해 줍니다.

```bash
# Create app directory
WORKDIR /usr/src/app
```

node:16 이미지는 Node.js 와 NPM이 이미 설치 되어 오기 때문에 `npm` binary를 이용해 의존성을 설치 해 주면 됩니다. 참고로 npm 버전이 4보다 낮다면 `package-lock.json` 파일을 생성되지 않습니다.

```bash
# Install app dependencies
# A wildcard is used to ensure both package.json AND package-lock.json are copied
# where available (npm@5+)
COPY package*.json ./

RUN npm install
```

모든 폴더를 복사 하는 대신, package.json 파일만 카피 함으로서 캐시되어 있는 도커 계층의 이점을 활용 할 수 있습니다.

이제 COPY 명령을 통해 어플리케이션의 소스코드를 Docker 이미지 내부에 복사 합니다.

```bash
# Bundle app source
COPY . .
```

위에서 8080 포트를 bind 해 두었기 때문에, EXPOSE를 이용해 docker daemon과 매핑 해 줍니다.

```bash
EXPOSE 8080
```

마지막으로, CMD로 app을 실행 할 명령어를 설정 해 줍니다.

```bash
CMD [ "node", "server.js" ]
```

완성된 `Dockerfile` 은 아래와 같습니다.

```dockerfile
FROM node:16

WORKDIR /usr/src/app
COPY package*.json ./
RUN npm install
COPY . .

EXPOSE 8080
CMD [ "node", "server.js" ]

```

### .dockerignore file

`.dockerignore` 파일을 생성 해 줍니다. 

```bash
vi .dockerignore
```

```
node_modules
npm-debug.log
```

이렇게 설정 함으로서 Docker image에 설치된 모듈을 덮어 쓰거나 디버그 로그가 복사되는 것을 방지 할 수 있습니다.

### 이미지 생성

이제 지금까지 작성한 `Dockerfile`로 이미지를 생성 해 보겠습니다.  

`docker build . -t 이미지이름 ` 으로 생성 하면 됩니다. 

이미지 이름은 <도커허브 유저명>/앱이름 으로 하셔야 합니다. 유저 명이 일치하지 않으면 push가 되지 않습니다.

`-t` 옵션은 `--tag` 와 같은 역할을 하며, name:tag 포맷으로 태그를 달 수 있습니다. 

```bash
docker build . -t shane/node-web-app
```

![image-20220530140847877](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/dockerize-node.assets/image-20220530140847877.png)

> 이미지를 생성 해 내었습니다.

이제 도커 이미지 목록을 확인 해 보면

```bash
docker images
```

![image-20220530141013844](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/dockerize-node.assets/image-20220530141013844.png)

> 방금 생성한 이미지가 보입니다.

### 이미지 실행

이제 이미지가 준비 되었으니 컨테이너로 실행 해 보도록 하겠습니다.

```bash
docker run -p 49160:8080 --name shane-node-app -d shane/node-web-app
```

![image-20220530141548119](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/dockerize-node.assets/image-20220530141548119.png)

잘 돌아가고 있고, 로그도 확인이 됩니다.

```bash
curl -i localhost:49160
```

![image-20220530141653211](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/dockerize-node.assets/image-20220530141653211.png)

> 방금 매핑한 포트로 요청을 보내니, Hello World를 정상적으로 응답합니다.

## 2. 도커 이미지를 Docker hub에 올리기

### Docker 로그인

https://hub.docker.com/ 에 회원 가입을 하고 로그인을 해 줍니다.

```bash
docker login
```

![image-20220530141846629](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/dockerize-node.assets/image-20220530141846629.png)

> 저는 한번 이미 로그인을 했었기 때문에 자동으로 로그인이 진행 되었습니다.

### DockerHub에 Push

위에서 `shane/node-web-app` 라는 이미지를 생성 했지만, 이번에는 실제 제가 사용할 `kkobuk/url-to-pdf` 라는 이미지를 push 해 보도록 하겠습니다. 이미지 이름은 각자 사용하시는 이름에 맞춰 주세요.

`docker images ` 명령어를 쳤을때 push할 이미지가 목록에 나와야 합니다.

```bash
docker push kkobuk/url-to-pdf
```

![image-20220530142708526](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/dockerize-node.assets/image-20220530142708526.png)

> Docker Hub에 push가 진행 되고 있습니다.

![image-20220530142801425](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/dockerize-node.assets/image-20220530142801425.png)

완료 된 후에 Docker Hub에 들어가보면 해당 이미지가 업로드 된게 확인 됩니다. 

이제 rmi 명령어로 기존의 도커 이미지를 삭제 한 후에

```bash
docker rmi kkobuk/url-to-pdf
```

Docker Hub에서 이미지를 내려 받아 사용해보면 정상적으로 받아와지는게 확인 됩니다.

```bash
docker run -p 8030:8080 --name url-to-pdf -d kkobuk/url-to-pdf
```

![image-20220530143118409](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/dockerize-node.assets/image-20220530143118409.png)

도커 컨테이너가 실행이 된 후에 작동을 테스트 해 봅니다.

![image-20220530145104807](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/dockerize-node.assets/image-20220530145104807.png)

> 이제 매핑해둔 8030 포트로 접속 시 해당 어플리케이션이 정상적으로 작동 됩니다

조금만 응용하면 다양한 활용이 가능하며, 특히 프로젝트 내에서 보조용으로 작성하는 작은 유틸성 프로젝트들은 이미지로 만들어두고 간단하게 불러와 사용 할 수 있습니다.

이상입니다.

ref: https://nodejs.org/en/docs/guides/nodejs-docker-webapp/
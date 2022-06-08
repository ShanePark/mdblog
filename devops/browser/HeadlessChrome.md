# Headless Chrome 브라우저 테스트

> https://developers.google.com/web/updates/2017/04/headless-chrome

## Intro

Headless Chrome은 Chrome 59 버전에서 추가되었습니다. Headless Chrome이 추가되기 이전에는 보통 `PhantomJS` 를 이용해서 Headless 웹 테스팅을 했다고 하는데요, Chrome이 Headless Chrome 기능을 업데이트 하자, PhantomJS는 개발 중단을 선언 했습니다. 아무래도 크롬이 직접 제공하다 보니 서드파티 소프트웨어를 사용할 필요가 줄어들 수 밖에 없습니다.

## @Before

### Chrome alias 설정 

Chrome 명령어를 쉽게 사용하기 위해 alias를 선언 해 줍니다.

저는 MacOS나 Linux에서 모두 zsh를 사용하다 보니 `.zshrc` 파일을 수정합니다.

```bash
vi ~/.zshrc
```

`.zshrc` 에 아래의 내용을 추가 해 줍니다. 저는 크롬이 해당 위치에 설치 되어 있지만, 사용하시는 운영체제나 환경에 따라 달라질 수 있습니다. 먼저 크롬이 설치된 위치를 확인 해 주세요.

```
alias chrome="/usr/bin/google-chrome-stable"
```

## 사용 예

Chrome의 headless 모드는 `-- headless` 옵션으로 실행 할 수 있습니다.

### Printing the DOM

현재 작업중인 위치에 지정한 주소를 Headless Chrome으로 방문해 `output.pdf` 파일을 생성 합니다.

```bash
chrome --headless --disable-gpu --dump-dom https://shanepark.tistory.com
```

![image-20211130090313772](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/browser/HeadlessChrome.assets/image-20211130090313772.png)

> 실행 결과

### 스크린 샷 저장

마찬가지로, 해당 사이트를 방문해 현재 작업중인 위치에 `screenshot.png` 파일을 생성 합니다.

`--window-size` 옵션으로 윈도우 크기도 설정 할 수 있습니다.

```bash
chrome --headless --disable-gpu --screenshot --window-size=1920,1280 https://shanepark.tistory.com
```

![image-20211130090425282](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/browser/HeadlessChrome.assets/image-20211130090425282.png)

> 명령어를 실행 한 위치에 파일이 생성됩니다. 저는 찾기쉽게 하려고 `cd ~/Downloads`를 먼저 입력 하고 했습니다.

![image-20211130090354455](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/browser/HeadlessChrome.assets/image-20211130090354455.png)

### PDF로 저장

```bash
chrome --headless --disable-gpu --print-to-pdf https://shanepark.tistory.com
```

![image-20211130090443858](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/browser/HeadlessChrome.assets/image-20211130090443858.png)

> 마찬가지로 명령어를 실행 한 위치에 `output.pdf` 라는 이름의 파일이 생성됩니다.

![image-20211130090454093](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/browser/HeadlessChrome.assets/image-20211130090454093.png)

### 리모트 디버깅

```bash
chrome --headless --remote-debugging-port=9222 --disable-gpu \--window-size=1920,1280 https://shanepark.tistory.com
```

![image-20211130090649883](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/browser/HeadlessChrome.assets/image-20211130090649883.png)

> 명령어 실행 시 지정한 포트를 리스닝 하기 시작 합니다.

크롬 브라우저를 실행 해서 `localhost:9222`에 접속 해 봅니다.

![image-20211130090758815](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/browser/HeadlessChrome.assets/image-20211130090758815.png)

이제 Deprecated 되었으니 Chrome에서 `chrome://inspect`를 대신 사용 하라고 합니다. 아직 사용은 할 수 있으니 아래의 파란색 링크를 클릭 해서 이동 해 보면,

![image-20211130091010967](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/browser/HeadlessChrome.assets/image-20211130091010967.png)

headless 크롬에 원격으로 접속 해 브라우징을 하며 디버깅을 할 수 있습니다.

## Puppeteer

Puppeteer는 Chrome 팀이 개발한 Node 라이브러리 입니다.

Headless 혹은 머리가 있는(?) 크롬도 컨트롤 할 수 있는 High-level API를 제공 하며, Panthom이나 NightmareJS와 같은 이전의 자동화 테스트 라이브러리들과 비슷하게 동작 합니다.

Puppeteer를 활용 하면 스크린샷을 찍거나, PDF파일을 생성하고, 페이지들을 스스로 찾아 다니고, 해당 페이지들로 부터 정보를 끌어 올 수도 있습니다. 자동화된 브라우저 테스트를 위해 만들어 졌다고 하니 바로 사용을 해 보아야 겠습니다. 안그래도 지금 테스트중인 프로젝트가 로그인이 필요해서 Headless Chrome만으로는 한계가 명확했거든요.

Puppeteer 관련 포스팅은 내용이 길어 따로 포스팅 해 두었습니다. 아래 링크를 확인 해 주세요.

> https://shanepark.tistory.com/291
# Puppeteer 활용 브라우저 테스트 자동화

<img src=https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/Puppeteer.assets/puppeteer.webp width=290 height=422 alt=1>

> https://developers.google.com/web/tools/puppeteer

## 소개

Headless 브라우저에 대한 이해가 필요 합니다. 

> 해당 내용은 https://shanepark.tistory.com/290 에서 확인 하실 수 있습니다.

### Puppeteer

Puppeteer는 Chrome 팀이 개발한 Node 라이브러리 입니다.

Headless 혹은 온전한 크롬도 컨트롤 할 수 있는 고차원 API를 제공 하며, Panthom이나 NightmareJS 등 이전의 자동화 테스트 라이브러리들과 비슷하게 작동합니다.

### 특징

Broswer에서 여러분이 수동으로 하는 대부분의 일들을 Puppeteer를 통해 할 수 있습니다.

몇가지 사용 예제

- 페이지의 스크린샷 혹은 PDF 파일 생성

- 싱글 페이지 어플리케이션 크롤링 및 미리 렌더링 된 컨텐츠 생성

- 폼 제출, UI 테스트, 키보드 입력등 자동화

- 최신의 자동화된 테스트 환경 생성

  > 테스트를 작성 하고, 최신의 크롬에서 최신 자바스크립트와 브라우저 기능들을 돌려 볼 수 있습니다.

- 시간대별 추적을 통해 웹사이트의 성능 문제를 해결 할 수 있습니다

- 크롬 확장 플러그인들을 테스트 할 수 있습니다

## 설치

> https://developers.google.com/web/tools/puppeteer/get-started

혹시 npm이 아직 없다면 먼저 `brew install npm` 혹은 `sudo apt install npm`을 해 줍니다.

![image-20211130093755211](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/Puppeteer.assets/image-20211130093755211.webp)

> brew 로 `brew install npm`을 하면 node를 비롯해 관련된 패키지를 알아서 다 설치 해 줍니다.

- npm 혹은 yarn으로 puppeteer를 설치 해 줍니다.

```bash
npm i puppeteer
# or "yarn add puppeteer"
```

![image-20211130094028491](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/Puppeteer.assets/image-20211130094028491.webp)

- 만약 기본 브라우저가 포함되지 않은 가벼운 버전의 Puppeteer를 원한다면 core만 설치 하세요.

> 위에서 `npm i puppeteer` 를 했다면 무시하세요

```bash
npm i puppeteer-core
# or "yarn add puppeteer-core"
```

## 테스트

테스트는 공식 Github 저장소의 샘플 코드들을 그대로 따라했습니다.

> https://github.com/puppeteer/puppeteer

### 스크린샷

example.js 파일을 만들어 줍니다.

goto 사이트 주소나 스크린샷 저장 경로는 제가 사용한 그대로 코드에 적어 두었으니, 변경해서 사용하시면 됩니다.

```javascript
const puppeteer = require('puppeteer');

(async () => {
  const browser = await puppeteer.launch();
  const page = await browser.newPage();
  await page.goto('https://shanepark.tistory.com');
  await page.screenshot({ path: '/home/shane/Downloads/example.webp' });

  await browser.close();
})();

```

이후 해당 파일을 node로 실행 해 줍니다.

```bash
node example.js
```

![image-20211130094819722](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/Puppeteer.assets/image-20211130094819722.webp)

지정한 폴더에 example.webp 파일이 생성 되었습니다. 파일을 열어 확인 해 보겠습니다

![image-20211130094934652](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/Puppeteer.assets/image-20211130094934652.webp)

> example.webp

800 x 600 사이즈의 스크린샷이 저장 되었습니다. Puppeteer의 기본 설정이며, `Page.setViewport()` 함수로 스크린샷 사이즈를 지정 할 수 있습니다.

### PDF 파일 생성

hn.js 파일을 생성 합니다.

```bash
const puppeteer = require('puppeteer');

(async () => {
  const browser = await puppeteer.launch();
  const page = await browser.newPage();
  await page.goto('https://shanepark.tistory.com', {
    waitUntil: 'networkidle2',
  });
  await page.pdf({ path: '/home/shane/Downloads/hn.pdf', format: 'a4' });

  await browser.close();
})();

```

이후 마찬가지로 node로 실행 해 줍니다.

```bash
node hn.js
```

![image-20211130095326966](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/Puppeteer.assets/image-20211130095326966.webp)

> hn.pdf 파일이 생성 되었습니다.

파일을 열어서 확인 해 보니 PDF 파일이 잘 생성 되었습니다.

![image-20211130095350186](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/Puppeteer.assets/image-20211130095350186.webp)

### Page Context의 viewport 확인

get-dimensions.js 파일을 생성 합니다.

```bash
const puppeteer = require('puppeteer');

(async () => {
  const browser = await puppeteer.launch();
  const page = await browser.newPage();
  await page.goto('https://shanepark.tistory.com');

  // Get the "viewport" of the page, as reported by the page.
  const dimensions = await page.evaluate(() => {
    return {
      width: document.documentElement.clientWidth,
      height: document.documentElement.clientHeight,
      deviceScaleFactor: window.devicePixelRatio,
    };
  });

  console.log('Dimensions:', dimensions);

  await browser.close();
})();

```

![image-20211130095819008](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/Puppeteer.assets/image-20211130095819008.webp)

## 기본 런타임 설정값

### Headless 모드 사용

Puppeteer는 Chromoum을 Headless mode로 실행 합니다. 헤드리스가 아닌 온전한 버전의 Chromium을 실행 하려면 브라우저를 실행 할 때, headless 옵션을 false 로 변경해주면 됩니다.

> default is true

```bash
const browser = await puppeteer.launch({ headless: false });
```

### 다른 버전의 Chromoum 사용

기본 설정으로, Puppeteer는 특정 버전의 Chromium을 다운 받아 사용 합니다. 특정 버전의 Chromium을 사용하기 위해서는 executablePath를 전달 해 주어야 합니다.

```bash
const browser = await puppeteer.launch({ executablePath: '/path/to/Chrome' });
```

### 새로운 User profile 생성

Puppeteer는 매번 실행 할 때마다 새로운 유저 프로필을 생성 하며, 해당 프로필을 남겨두지 않습니다.

## 실제 적용

그외 다양한 API 들을 활용해서 실제 자동화 테스트를 만들 수 있습니다.

아래의 두 API Document는 같은 내용이지만, 아래의 링크가 조금 더 보기 좋습니다.

> https://github.com/puppeteer/puppeteer/blob/v12.0.1/docs/api.md
>
> https://pptr.dev/#?product=Puppeteer&version=v12.0.1&show=outline

위에 설명된 API들을 활용해 페이지에 접속 하고, 스스로 로그인 하는 테스트를 만들어 보겠습니다.

잘 작동하고 있다는 것을 눈으로 확인 하기 위해, headless를 false로 두었으며 페이지를 이동 할 때 마다 스크린샷을 찍어서 남기도록 하였습니다. 코드로 보는게 이해가 빠르기 때문에 코드를 첨부합니다.

```javascript
const puppeteer = require('puppeteer');
let count = 0;

(async () => {
  const browser = await puppeteer.launch({
    headless: true,
    args: [`--window-size=1920,1080`],
    defaultViewport: {
      width:1920,
      height:1080
    }
  });
  const page = await browser.newPage();
  const loginId = 'admin';
  const loginPass = '1234';

  await page.goto('http://localhost:10000');
  await page.screenshot({path: path('main')});

  // 페이지로 이동
  await page.goto('http://localhost:10000/auth/login');
  await page.screenshot({path: path('login-screen')});

  // 로그인
  await page.evaluate((id, pw) => {
    document.querySelector('input[type="text"]').value = id;
    document.querySelector('input[type="password"]').value = pw;
    }, loginId, loginPass);

  // 로그인 버튼 클릭
  await page.click('button[type="submit"]');
  await page.waitForSelector('.main_visual_wrap');
  await page.screenshot({path: path('after-login')});

  // 테스트 종료
  await browser.close();
})();

function path(fileName) {
  let numStr = (++count).toString().padStart(3, "0");
  return './test-result/'+numStr+"-"+fileName+'.webp';
}

```

> node로 위의 코드를 실행 하면 테스트 진행 사항을 눈으로 확인 할 수 있으며

![image-20211130120014748](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/Puppeteer.assets/image-20211130120014748.webp)

> 테스트를 마친 후에는 모든 테스트 기록이 저장됩니다.

![image-20211130115700226](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/Puppeteer.assets/image-20211130115700226.webp)

> IntelliJ IDEA에 Node.js 와 NPM 설정을 해주고 프로젝트로 불러온다면

![image-20211130115841635](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/testing/Puppeteer.assets/image-20211130115841635.webp)

> Code assistance 도 받을 수 있습니다

이상으로 Puppeteer 를 활용한 브라우저 자동 테스트를 해 보았습니다.

처음에는 다소 시간이 걸릴 수 있지만, 한번만 코드를 작성 해 둔다면 변경 사항이 있을 때에도 일일히 클릭해보며 시간을 쓰지 않아도 Headless 브라우저를 통해 자동으로 테스트를 진행 하여 시간을 절약 할 수 있습니다.
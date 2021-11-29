# Headless Chrome

> https://developers.google.com/web/updates/2017/04/headless-chrome

## Intro

## @Before

### Chrome alias 설정 

`vi ~/.zshrc` 

```
alias chrome="/usr/bin/google-chrome-stable"
```

## 사용 예

### Printing the DOM

현재 작업중인 위치에 `output.pdf` 파일을 생성 합니다.

```zsh
chrome --headless --disable-gpu --dump-dom https://shanepark.tistory.com
```

### 스크린 샷 저장

현재 작업중인 위치에 `screenshot.png` 파일을 생성 합니다.

```zsh
chrome --headless --disable-gpu --screenshot --window-size=1920,1280 https://shanepark.tistory.com
```

### PDF로 저장

```zsh
chrome --headless --disable-gpu --print-to-pdf https://shanepark.tistory.com
```

### 리모트 디버깅

```zsh
chrome --headless --remote-debugging-port=9222 --disable-gpu \--window-size=1920,1280 https://shanepark.tistory.com
```

## Puppeteer
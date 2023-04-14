# iPhone 사파리 페이지를 개발자모드 열기

## Intro

개발중인 토이프로젝트의 css가 아이폰으로 확인 했을 때, 의도치 않은 모습으로 보였습니다. 버튼 및 input의 텍스트 색상이 자꾸 파랑색으로 표시되는데 개발자 모드로 띄워 놓고 확인을 하고 싶었습니다.

macOS 의 Safari로 띄워놓고 같은 사이즈로 줄여도 같은상황이 재현되지 않는걸 보면 iOS Safari의 특징 같았습니다. 사파리 페이지를 테스트 하다 보면 스택오버플로에서 흔히들 말하는 `Safari is new IE` 라는 말에 뭔가 수긍되는 경우도 종종 있긴 합니다.

## iPhone Safari 디버그

**iPhone 에서 Web Inspector 켜기**

설정 > Safari > Advanced > Web Inspector 순서로 들어가서 설정을 켜 줍니다.

<img src=https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/ios_safari_dev.assets/IMG_1378%20Large.webp width=591 height=1280 alt=1>

![IMG_1379 Large](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/ios_safari_dev.assets/IMG_1379%20Large.webp)

![IMG_1380 Large](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/ios_safari_dev.assets/IMG_1380%20Large.webp)

이후 이제 맥북에서 Safari를 실행 합니다. 혹시 MacOS의 Safari에서 개발자 툴을 활성화 해 두지 않았다면, `Preferences > Advanced` 에 들어가서 맨 아래 보이는 Show Develop menu in menu bar 체크를 해 줍니다.

![image-20230128063655896](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/ios_safari_dev.assets/image-20230128063655896.webp)

이제 맥북과 아이폰을 케이블로 연결 해 줍니다.

그러고 MacOS의 사파리에서 Develop 메뉴를 보면 연결한 iPhone이 추가 되어 있는데요.

![image-20230128064132161](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/ios_safari_dev.assets/image-20230128064132161.webp)

검사하고자 하는 탭을 선택 하면..

Mac에는 Web Inspector가 뜨고

![image-20230128064307297](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/ios_safari_dev.assets/image-20230128064307297.webp)

모바일 화면에는 선택한 엘리먼트가 표시 됩니다. 협동하는 모습이 매우 보기 좋네요 

![IMG_1381](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/ios_safari_dev.assets/IMG_1381.webp)

css 속성을 MacOS에서 띄워놓은 사파리에서 변경 하고, 아이폰에 뜨는 화면으로 변경사항을 확인 한 덕에 의도대로 css를 변경 할 수 있었습니다.

덕분에 의도치 않게 파랑색으로 표시되었던 글자들도 모두 정상적으로 변경 한 모습입니다.

![IMG_1382](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/ios_safari_dev.assets/IMG_1382.webp)

이상입니다. 

**References**

- https://www.browserstack.com/guide/how-to-debug-on-iphone
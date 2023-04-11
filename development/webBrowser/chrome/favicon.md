# Chrome) 즐겨찾기 아이콘 변경 하기

## Intro

<img src=https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/webBrowser/chrome/favicon.assets/image-20220115190803851.webp width=750 height=660 alt=1>

위의 제 Chrome 브라우저에서 보이는 것 처럼, 저는 Bookmark bar에 추가해 둔 사이트가 제법 많아지면서, 즐겨찾기 사이트들의 이름을 지우고 아이콘만 남겨 사용 하고 있습니다.

하지만 favicon이 없는 웹사이트를 즐겨 찾기에 추가 하는 경우에는 아래 보이는 것 처럼, 지구본 모양(?) 아이콘이 남게 되는데요

> ![image-20220115192617042](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/webBrowser/chrome/favicon.assets/image-20220115192617042.webp)

아이콘만으로 웹사이트를 구분하는 경우에는, 이렇게 아이콘이 겹치는 몇개가 생겨버리면, 어떤 사이트인지 한눈에 알 방법이 없습니다.

## Bookmark Favicon Changer

이 경우 Chrome 익스텐션을 활용 해서 즐겨찾기에 있는 favicon을 변경 할 수 있습니다.

### 설치

![image-20220115193155440](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/webBrowser/chrome/favicon.assets/image-20220115193155440.webp)

> https://chrome.google.com/webstore/detail/bookmark-favicon-changer/acmfnomgphggonodopogfbmkneepfgnh?hl=en-US

위의 링크에서 Extention을 설치 해 줍니다.

### 적용

일단 즐겨찾기 아이콘을 변경할 페이지로 이동 후, 해당 Extention을 실행 합니다.

![image-20220115203101099](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/webBrowser/chrome/favicon.assets/image-20220115203101099.webp)

> 우측 상단의 Extention 목록에서 방금 설치한 플러그인을 선택합니다.

![image-20220115203124995](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/webBrowser/chrome/favicon.assets/image-20220115203124995.webp)

> 그러고는 적용 할 domain을 선택 해 줍니다.

`Change icon and add rule` 을 클릭 후

![image-20220115202715868](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/webBrowser/chrome/favicon.assets/image-20220115202715868.webp)

> 미리 준비 해둔 이미지를 선택 합니다.

![image-20220115203210607](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/webBrowser/chrome/favicon.assets/image-20220115203210607.webp)

> 제일 우측 네이버 아이콘이 변경 되었습니다.

그러면 즐겨찾기 아이콘이 원하는 이미지로 변경 된 것을 확인 할 수 있습니다.

## 마치며

naver 처럼 누구나 알 수 있는 아이콘이 정해져있는 웹사이트의 즐겨찾기 아이콘을 변경하는 예시를 들었지만, 실제로 이럴 때에 사용하는건 크게 의미가 없고. 제 개인적인 경우에는 일단 두가지 경우에서 꼭 필요 했는데요

### 운영중인 사이트를 로컬에서도 띄우며 테스트 할 때

이 경우에는 운영중인 사이트와 로컬에서 띄워서 테스트중인 사이트 모두 즐겨찾기 했을 경우 아이콘이 같기 때문에 정말 헷갈립니다. 그렇다고 favicon.ico 파일을 운영용/개발용 따로 둘수도 없으니 이럴 때에는 해당 extension으로 내 컴퓨터에서만 아이콘이 다르게 보이게 변경 하면 정말 간단하게 구별 해낼 수 있습니다.

### favicon이 없는 사이트를 즐겨 찾기 할 때

이 경우에는 favicon이 없으면 이름으로 구분을 해야 하는데 저는 아이콘으로 구별하는 걸 선호 하기 때문에 꼭 필요 했습니다.

추가로 이미 있는 favicon이 혐오스럽거나 너무 보기 싫거나 알아보기 힘든 경우에도 사용 할 수 있겠네요.

알아두면 굉장히 유용하기 때문에 본인에게 필요한 팁이라면 한번씩 꼭 사용해보세요!

 
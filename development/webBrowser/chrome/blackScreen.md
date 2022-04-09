# Chrome 에서 Netflix 혹은 인프런 검은화면에 소리만 나올때 해결방법

## Intro

![blackscreen](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/webBrowser/chrome/blackScreen.assets/blackscreen.png)

> 지금은 해결을 했지만 대충 까만색 네모를 그려 재현을 한 모습.

인프런 영상을 듣는데, 이전에는 아무 문제가 없었는데 며칠 전 부터 크롬으로만 켜면 영상이 까만 화면만 나오며 소리만 나온다.

현재 Belkin사의 Display Link 독을 활용해 외부 모니터 3개를 맥북에 연결해 사용하고 있는데, 기억상 처음 해당 제품을 샀을때 넷플릭스 영상을 볼 때도 같은 증상이 있었던 걸로 기억한다.

곧바로 넷플릭스를 틀어봤더니 역시나 영상은 까만화면만 나오고 소리만 들리는 같은 문제가 발생중.

## 원인

원인이 좀 복합적이긴 한데 근본적인 원인은 DRM 이다.

넷플릭스와 인프런의 공통점을 곰곰히 생각 해 보니 콘텐츠의 불법 사용과 유출을 방어하기 위해 DRM 기술을 적용 중이라는 점 이다.

DRM은 Digital Rights Management의 약자로 컨텐츠를 보호하기 위한 기술인데, 특히 영상 레코딩을 방어하는 기술이 들어간 것. Display Link 자체가 소프트웨어적으로 영상을 녹화해서 디지털 신호로 변경해 HDMI 를 통해 모니터에 쏴 주는 건데 아무래도 서로 원리상 상극일 수 밖에 없는 것.

## 해결

일단 당연히 Display Link를 사용하지 않으면 정상적으로 영상 관람이 가능하다. 하지만 모니터를 여려개 연결하지 못하면 불편이 이만 저만이 아닌데 다행히도 Safari 로 재생했을 경우 영상이 정상적으로 나왔다.

하지만 인프런의 경우 특히 Safari에 대한 대응이 충분히 이루어져 있지 않기 때문에 Chrome을 사용하는게 정신 건강에 이로운데..

여러 가지 해결 방안을 찾아 보던 도중 Reddit에서 원하는 해답을 얻어냈다.

![image-20220409202938677](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/webBrowser/chrome/blackScreen.assets/image-20220409202938677.png)

> https://www.reddit.com/r/discordapp/comments/gz5q6t/streaming_a_chrome_tab_with_netflix_black_screen/

Chrome 브라우저의 하드웨어 가속 기능을 껐더니 아주 잘 되었다라는 건데 보는 순간 아 이거다 싶었다.

해결방법은 단순하게 크롬브라우저의 하드웨어 가속 기능을 끄는건데, DRM의 방어 기술의 동작 과정은 자세히 모르지만 하드웨어 가속 기능을 통한 영상 재생 과정에서 발생하는 GPU의 특정 동작을 잡아 내는 것 같습니다.

![image-20220409203027867](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/webBrowser/chrome/blackScreen.assets/image-20220409203027867.png)

> Use hardware acceleration을 끈다.

하드웨어 가속 기능을 끄고 나서는 인프런의 강의와 넷플릭스 영상 모두 정상적으로 재생되는 것이 확인된다.

![image-20220409211230761](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/webBrowser/chrome/blackScreen.assets/image-20220409211230761.png)

인프런에서 Safari 사용시, 일시 정지 한 상황에서도 내부적으로는 영상이 계속 진행되는 버그가 있어서 굉장히 불편하기 때문에 사파리를 사용 할 수도 없었고 그렇다고 Display Link 기능을 사용 하지 않을 수도 없는 상황이었는데 해결방법을 찾아서 참 다행이다.

검색을 했을때 뚜렷한 해결 방법이 나오지 않았었기 때문에 혹시나 같은 상황으로 고통을 겪고 있는 분들이 있으면 도움이 되었으면 하는 마음과 나중에 같은 상황이 생겼을 때 시간낭비를 줄이기 위해 글로 남깁니다. 이상입니다.

 
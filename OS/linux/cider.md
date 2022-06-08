# Linux) 리눅스에서 Apple Music 듣기 Cider App

## Intro

저희집은 iPhone, MacBook, Apple TV, HomePod, iPad 등등 가족들이 사용하는 Apple 디바이스들이 많다보니 편의상 애플 뮤직을  꽤 오래 전 부터 구독 하고 있습니다.

![image-20220323151555440](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/cider.assets/image-20220323151555440.png) 

13,500원인 Family Plan을 구독하면 무려 6명까지 제한 없이 음악을 들을 수 있습니다. 무려 <u>세금도 포함</u>한 금액으로, 꽉 채워 구독시 인당 2,250원이라는 믿기지 않는 가성비로 인해 잠시 Spotify 를 구독 하다가 다시 Apple Music으로 돌아온지도 이제 제법 되었습니다.

그런데 애플 기기에서는 Apple Music이라는 훌륭한 네이티브 앱이 있기 때문에 불편 없이 사용 할 수 있지만 윈도우즈에서 사용시 `iTunes`라는 멸종 위기의 소프트웨어를 사용 하거나 웹으로 접속을 해야 했습니다.

심지어 요즘 주로 사용하고 있는 Linux 에서는 아이튠즈 마저 없기 때문에 선택의 폭이 없습니다. 핸드폰으로 듣는게 차라리 편합니다.

이 상황을 해결해 내기 위해

![image-20220323152012888](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/cider.assets/image-20220323152012888.png)

Snap 에 있는 Apple Music 소프트웨어를 다운 받아 사용 해 보았는데

> 당연히 Apple이 만든건 아닙니다.

겉 보기와는 다르게 소프트웨어의 상태가 너무도 조악하고 에러도 심각하며 단순하게 웹 브라우저를 내장한 소프트웨어에 불과하기 때문에 도저히 사용 할수가 없어서 음악을 며칠 들어 보고는 바로 지워버렸었습니다. Snap에 있는 Apple 관련 앱들은 다 비추천합니다.

![image-20220323152234076](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/cider.assets/image-20220323152234076.png)

> https://music.apple.com/browse

웹 브라우저로 Apple Music 을 사용하는 방법도 있지만 도저히 납득이 되지 않을 정도로 느렸고 웹 브라우저의 한계로 실 사용을 하기에 부적합하다고 느껴져서 이 방법도 배제했습니다. 유일한 방법인데 쓸수가 없었습니다..

그래서 리눅스 때문에 다시 Spotify 로 넘어가야 하는지 고민하던 찰나에 (Spotify는 Linux를 공식적으로 지원합니다) 우연히 Cider를 찾아냈고, 사용해보니 굉장히 놀랍고 드디어 구원을 받은 느낌입니다. 

## Cider

> https://github.com/ciderapp/Cider

### 소개

![image-20220323153020866](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/cider.assets/image-20220323153020866.png)

> 오픈소스라는게 믿기지 않는 UI/UX

Cider는 Windows, Linux, macOS, 등등 거의 모든 OS에서 사용 할 수 있는 오픈소스 애플뮤직 클라이언트 입니다. 단순하게 웹브라우저로 브라우징 하는 방식이 아니기 때문에 믿기지 않을 정도로 빠르며, 디자인도 훌륭하기 때문에 애플의 Apple Music 앱에 비해도 전혀 꿀리지 않습니다. 애플이 만들었다고 해도 믿겠습니다. 아니 애플이 만든게 아니라는게 믿기지가 않습니다.

- 속도가 아주 빠르고
- 기존 Apple Music 의 모든 기능을 담았으며
- 가볍습니다. iTunes로 노래를 들을떄랑 비교가 안됩니다.

![image-20220323153247393](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/cider.assets/image-20220323153247393.png)

- 심지어 독자적인 추가 기능들도 있습니다.

### 다운로드 및 설치

개인적으로 컴파일 해서 사용하려면 Github 저장소를 클론해서 yarn으로 빌드 하시면 됩니다.

> https://github.com/ciderapp/Cider

```bash
git clone https://github.com/ciderapp/Cider.git
cd Cider
yarn install
yarn run dist
```

하지만 굳이 컴파일 하지 않아도 다운로드 링크를 제공 하고 있습니다.

![image-20220323153637903](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/cider.assets/image-20220323153637903.png)

> https://download.cider.sh/?utm_source=homepage&utm_medium=cta

`.deb` 파일을 받아서 `sudo dpkg -i cider*` 로 설치 해 주면 됩니다. 이제 실행을 하면

![2](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/cider.assets/2.png)

> 애플 아이디로 로그인 하라고 나옵니다.

![3](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/cider.assets/3.png)

> 로그인 후 Continue를 누르는데 안넘어가길래 그냥 창을 닫으니 로그인 되었습니다.

![image-20220323153954522](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/cider.assets/image-20220323153954522.png)

>  사용 화면입니다.

감히 완벽하다는 평가를 내리고 싶을 만큼 정말 훌륭한 애플뮤직 클라이언트 입니다.

애플뮤직 구독자라면 리눅스 뿐만 아니라 윈도우 사용자들도 꼭 사용해야 하는 프로그램이라고 생각합니다.

> 다만 Windows 에서는 $0.99에 스토어에서 판매를 하고 있더라고요
>
> ![image-20220323212220504](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/cider.assets/image-20220323212220504.png)
>
> 코드가 공개되어 있으니 개인적으로 Yarn 으로 컴파일 해서 실행하시면 무료로도 사용 할 수 있습니다.

추가로, 에어팟을 사용 하고 계신다면 에어팟도 리눅스에 연결 해서 사용하시면 맥북이 부럽지 않겠네요!

> [Ubuntu) Airpod 연결 설정 하기](https://shanepark.tistory.com/346)

이상입니다.
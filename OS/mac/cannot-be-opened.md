# [MacOS] The application can't be opened. 게이트키퍼 해제

## Intro

인터넷에서 다운로드 받은 어플리케이션을 설치 할 때 마다 자주 겪는 문제가 있습니다.

바로 The application can't be opened 인데요. 

![image-20220917211515992](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/cannot-be-opened.assets/image-20220917211515992.png)

보통은 간단하게 해결 되지만 그렇지 않은 경우도 있기 때문에 한번 다루어 볼 까 합니다.

![image-20220917211552354](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/cannot-be-opened.assets/image-20220917211552354.png)

보통은 동시에 위와 같은 경고가 뜨게 되는데요. Apple이 악의적인 프로그램인지를 체크 할 수가 없으니 맥북이 스스로 방어를 해 내는 것 입니다. 

일반적인 소프트웨어들을 다운받아서 사용할 때는 왠만해서는 볼 일이 없지만, 개발자용 베타 프로그램등을 사용 하다 보면 종종 볼 수 있습니다. 저도 이번에 사용하던 [Mac Mouse Fix](https://github.com/noah-nuebling/mac-mouse-fix) 가 새로운 3.0.0 BETA 버전이 나왔길래 테스트 해 보려고 하다보니 해당 문제에 봉착 했습니다.

## 해결

### Open Anyway

보통은 Open Anyway 에서 대부분 해결 됩니다. System Preferences 를 띄우고 

![image-20220917211613551](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/cannot-be-opened.assets/image-20220917211613551.png)

그다음에 Security & Privacy 에 들어 갑니다

![image-20220917211632187](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/cannot-be-opened.assets/image-20220917211632187.png)

"Mac Mouse Fix" 를 설치하고 있으니, 앱 이름을 확인 한 후에 Open Anyway 버튼 클릭 합니다.

![image-20220917211647471](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/cannot-be-opened.assets/image-20220917211647471.png)

악의적인 프로그램 일 수 있다고 경고 합니다. 신뢰 할 수 있는 개발자 혹은 단체로부터 다운로드 받았다면 Open 으로 실행 해 주시면 됩니다.

보통은 여기에서 해결이 대부분 되는데요.

하지만 이번처럼 Open Anyway를 해도 해결되지 않는 경우가 있습니다.

![image-20220917211724031](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/cannot-be-opened.assets/image-20220917211724031.png)

환경 설정의 Security & Privacy 에도 더이상 아무것도 뜨지 않는데 여전히 실행이 되지 않습니다.

![image-20220917211806115](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/cannot-be-opened.assets/image-20220917211806115.png)

그래서 Allow apps downloaded from 을 Anywhere로 변경 해 보려고 합니다.

### Allow apps downloaded from 변경

Anywhere로 변경 하고 싶은데, 메뉴가 없습니다. 이 때는 게이트키퍼를 비활성화 해주면 됩니다.

게이트키퍼는 기본적으로 알려진 멀웨어를 확인 하고, 개발자 서명 및 변조여부를 확인 하고 실행 여부를 판가름 해줍니다. 

게이트 키퍼는 아래와 같은 단계로 비활성 화 시킬 수 있습니다.

1. 제일 먼저 System Preferences 창을 닫아 줍니다.

2. 그 후에 터미널을 열고 아래와 같이 입력 해 줍니다.

```bash
sudo spctl --master-disable
```

![image-20220917211935575](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/cannot-be-opened.assets/image-20220917211935575.png)

비밀번호를 입력 하고 나서 확인 해 보면

![image-20220917212005698](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/cannot-be-opened.assets/image-20220917212005698.png)

이제 Allow apps downloaded from: Anywhere 이 추가 되었고, 선택까지 되어 있습니다.

이제부터는 Anywhere 어플리케이션을 설치 할 수 있지만 보안상 바람직하지 않기 때문에 원하는 소프트웨어를 설치 한 후에는 바로 App Store and identified developers 로 설정을 변경 하는걸 추천합니다.

게이트 키퍼 원래 설정으로 복원 방법은 아래와 같습니다. 

```bash
sudo spctl --master-enable
```

이렇게 해도 설치가 되지 않는다면 파일의 손상을 의심 해 보세요.

이상입니다.

**References**

- http://www.phy.ohio.edu/~hadizade/blog_files/tag-how-to-allow-apps-from-anywhere-in-macos-gatekeeper-.html
# Chrome 브라우저 업로드 / 다운로드 속도 제한 걸기

## Intro

웹 애플리케이션의 파일 업로드와 다운로드 기능을 구현하는 과정에서 테스트를 하다 보면 꽤나 당황스러울 때가 많습니다.

웹 브라우저를 사용하고 있기는 하지만, 사실 브라우저를 통해 업로드 되는 파일은 네트워크 망을 전혀 타지 않고 디스크 내부에서만 COPY 행위가 일어나기 때문입니다.

이때의 속도는, 인터넷 속도에 영향을 받지 않고 디스크의 읽기/쓰기 속도를 따라 가기 때문에 일반적으로 상상하기 어려운 300 MB/s 가 나오곤 합니다.

> 요즘에는 보통 기가 인터넷을 쓰기 때문에 1Gbps 가 흔한데 300 MB/s가 대수냐 싶을 수도 있지만, 1MB/s == 8Mbps/s 이기 때문에 (bit와 Byte의 표기 차이) 300 MB/s 는 결국 2.4Gbps 의 속도인 것입니다. 

물론 여전히 흔하지는 않지만 이미 상용화 된 인터넷 속도임은 부정 할 수 없지만 네트워크를 전혀 타지 않음으로 실질적인 테스트가 이루어지지 못합니다.

![image-20220617151745559](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/webBrowser/chrome/throttling.assets/image-20220617151745559.png)

> 동시에 여러가지 다운로드/ 업로드 작업이 이루어짐에도 224MB/s 라는 속도로 10 기가바이트 파일을 순식간에 다운로드 해내는 모습

이때, 파일 업로드 하는 순간에 대해 디버깅 하거나 업로드가 진행 중일 때의 UI/UX 테스트라도 하려면 업로드가 순식간에 끝나버려 곤란한 상황에 처하게 됩니다.

이때, 두 가지 방법이 있겠는데요. 수백 기가의 무지막지한 크기의 더미 파일을 준비하거나, 그러고 싶지 않다면 속도 제한을 거는 겁니다.

## Throttling

고맙게도 최근의 웹 브라우저들은 자체적으로 쓰로틀링 기능을 제공하고 있습니다.

### Firefox

![image-20220617152351997](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/webBrowser/chrome/throttling.assets/image-20220617152351997.png)

> Firefox의 옵션

Firefox 에서는 미리 정해져 있는 몇 개의  Throttling 전략 중에 골라서 만 사용할 수 있기 때문에 불편함이 있습니다. 가장 빠른 옵션인 Wi-fi도 세계적인 표준 와이파이 속도를 말하는 것 이기 때문에 30Mbps 정도의 속도가 나오며, 그럴싸해 보이는 Regular LTE도 5Mbps 의 끔찍한 속도가 나옵니다.

한국에서 일반적으로 사용하는 환경을 테스트 하기에는 옵션이 적절하지 않습니다.

### Chrome

다행히도 크롬 브라우저는 사용자가 원하는 쓰로틀링 프로필을 설정 할 수 있습니다.

설정 방법을 함께 알아 보겠습니다.

1. F12 키를 눌러 개발자 도구를 켜고, Network 탭에 들어갑니다. 

![image-20220617153032854](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/webBrowser/chrome/throttling.assets/image-20220617153032854.png)

2. 그 아래 줄에 No throttling 이라고 써있는 셀렉트바를 클릭 합니다.

![image-20220617153903808](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/webBrowser/chrome/throttling.assets/image-20220617153903808.png)

3. 기존에는 Fast 3G, Slow 3G, Offline 옵션밖에 없는데 Add.. 버튼을 클릭 해 원하는 속도를 몇개 넣어 주면 됩니다. Add... 버튼 클릭 후 Add custom profile...을 클릭 합니다.

![image-20220617154012960](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/webBrowser/chrome/throttling.assets/image-20220617154012960.png)

4. 원하는 프로필명과 다운로드/업로드 속도 제한 및 레이턴시 설정을 넣고 Add 버튼을 클릭 해 저장 해 줍니다.

![image-20220617154139509](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/webBrowser/chrome/throttling.assets/image-20220617154139509.png)

![image-20220617154146570](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/webBrowser/chrome/throttling.assets/image-20220617154146570.png)

> 저장 후 방금 입력한 1000Mbps 프로필이 추가 된 모습이 보입니다.

## 테스트

실제 속도가 적용 되었는지 테스트를 해 보겠습니다. 100Mbps 프로필로 쓰로틀링을 설정 한 뒤에

![image-20220617154305956](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/webBrowser/chrome/throttling.assets/image-20220617154305956.png)

> 100 Mbps

로컬 환경에서 다운로드를 해 봅니다.

![down](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/webBrowser/chrome/throttling.assets/down.gif)

> localhost 경로임에도 불구하고 매우 현실적인 다운로드 속도가 나옵니다.

이제 편하게 테스트를 진행 하시면 됩니다.

이상입니다.

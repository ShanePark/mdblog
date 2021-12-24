# Eclipse Apple Sillicon 지원소식 

![image-20211224220326989](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/eclipseM1.assets/image-20211224220326989.png)

## Intro

길고 긴 기다림 끝에 Eclipse가 애플 실리콘을 지원 하기 시작했습니다.

사실 저는 그 길고 긴 기다림에 지쳐 4개월 전에 IntelliJ IDEA로 넘어갔습니다. 비용의 부담이 전혀 없었던 건 아니지만, 2021년 8월에 한국어 언어팩 출시 기념 30% 할인을 해 준 덕에 용기내어 넘어 갈 수 있었습니다.

인텔리제이를 본격적으로 사용하기 시작 한 이후로는 이클립스의 필요성을 전혀 느끼지 않아 한참동안 사용 할 일이 없었는데요..

그래도 한 때 m1 맥북의 초창기 사용자로서, 이클립스를 사용하며 정말 크나큰 불편을 느꼈었기 때문에 애플 실리콘 출시와 함께 그 불편함들이 얼마나 해소되었는지 궁금함에 확인을 해보지 않을 수 없었습니다.

## 설치

> https://www.eclipse.org/downloads/packages/

![image-20211224220806486](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/eclipseM1.assets/image-20211224220806486.png)

> 애플 실리콘을 지원하는 버전은 2021-12R 입니다.
>
> macOS에 새로 추가된 AArch64 를 클릭해서 다운 받아 줍니다. 제가 기억하기로는 2020-12 버전 이후로 jre를 포함해서 배포되고 있습니다.
>
> m1 맥북에서는 그 이전의 버전을 사용할 경우 정말 많은 버그를 경험 하며 사용이 거의 불가능 합니다. 아주 다행히도 맥북을 처음 구입했을때에 2020-12 버전을 사용 할 수 있었기 때문에 개발이 가능 했습니다. 그 이전 버전을 사용해야 했다면.. 정말 아찔합니다.

![image-20211224220902385](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/eclipseM1.assets/image-20211224220902385.png)

> 카카오 서버를 통해 다운 받기 때문에 속도도 괜찮습니다. 용량은 319.3MB 입니다.

![image-20211224221209299](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/eclipseM1.assets/image-20211224221209299.png)

익숙한 설치 과정을 거쳐 설치 후 실행 해 줍니다.

## 실행

![image-20211224221238686](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/eclipseM1.assets/image-20211224221238686.png)

2021-12의 새로운 로고 입니다. 개인적으로는 마음에 드네요.

![image-20211224221314141](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/eclipseM1.assets/image-20211224221314141.png)

> 일단 디자인부터 기대를 확 무너뜨립니다만.. 혹시 모르죠

드디어 이클립스가 실행 되었습니다.

![image-20211224221408469](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/eclipseM1.assets/image-20211224221408469.png)

윈도우가 하나 떴을 뿐인데, 속도가 이전과 차원이 다른게 느껴집니다.

![image-20211224221519734](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/eclipseM1.assets/image-20211224221519734.png)

> Apple native로 실행 되고 있음이 확인 됩니다. 그 얼마나 기다렸던 순간입니까.. 조금 늦었지만 말이죠

### 한글 마지막 글자 잘림 해결여부

새로운 이클립스 버전이 나올때마다 항상 궁금했던 내용입니다. 특히 Apple Sillicon을 지원한다고 하니 더욱 기대가 되는데요.

이클립스를 포기하게 만든 트리거 였는데, 과연 해결 되었을까요?

![gif](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/eclipseM1.assets/gif.gif)

> 여전합니다. 이거 하나만으로도 맥북에서 이클립스의 매력이 그냥 바닥으로 떨어집니다.

## 총평

구동 속도를 보면 정말 시원 시원 해 졌습니다. 진작에 제가 이클립스를 사용 할 때에 지원 해줬다면 어땠을까 아쉬움이 많이 남습니다.

하지만 한글 마지막 글자 잘리는 현상을 해결하지 못한 이상 여전히 맥북에서의 메인 IDE로 사용하기에는 무리가 있다는 생각이 듭니다.

사실 이클립스의 문제라기 보다는 한글 입력기의 문제입니다. 컴퓨터에게 한글 입력은 정말 쉽지 않은 난제 입니다.

인텔리제이에서는 이 문제로 맥에서의 한글 입력기를 아에 하나 만들었다고 들었는데요, 사실 이클립스가 오픈소스며 한국인 이용자가 정말 많은데 아직도 해결이 되지 않았다는건 의문입니다. 

불편함을 해결해내지 않고 인텔리제이로 넘어온 제가 불평할 자격은 없지만, 후에 꼭 실력을 키워 이러한 불편을 해결해 나가는 개발자중 한명이 되겠습니다.
# IntelliJ IDEA Copilot 한글 깨짐 문제 해결

>  본 증상은 IntelliJ IDEA 2024.3.5 로 업데이트 되며 해결되었으나 추후 재발에 대비하여 글을 남겨둠

## Intro

인텔리제이에서 Copilot을 사용할 때, 버전 업그레이드 후 한글 자동완성이 깨지는 문제가 발생했다. 이는 fallback font가 올바르게 불러와지지 않아서 발생하는 것으로 보인다. 자동완성 미리보기는 한글이 다 깨져서 나오는데, 막상 `tab`을 누르면 제대로 입력된다. 

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/development/intellij/Intellij-copilot-font.assets/2.webp)

![3](https://raw.githubusercontent.com/ShanePark/mdblog/main/development/intellij/Intellij-copilot-font.assets/3.webp)

현 개발 환경은 다음과 같다.

- Ubuntu 22.04
- IntelliJ IDEA Ultimate 2024.3.4.1
- Github Copilot 1.5.37-242

## 해결

원래 JetBrains Mono는 한글을 지원 하지 않는다. 

![4](https://raw.githubusercontent.com/ShanePark/mdblog/main/development/intellij/Intellij-copilot-font.assets/4.webp)

> https://www.jetbrains.com/ko-kr/lp/mono/

그래서 적당한 Fallback이 이루어져야 하는데 Copilot 과 IntelliJ IDEA 둘이서 잘해보려다가 충돌이 발생한 모양이다.

Fallback font 도 설정 해보고 Color Scheme Font 에 fall back 설정도 해보았는데도 효과가 없었다.

물론 해결방법은 간단한데, 한글을 지원하는 폰트를 사용하면 된다.

![5](https://raw.githubusercontent.com/ShanePark/mdblog/main/development/intellij/Intellij-copilot-font.assets/5.webp)

> D2Coding font 로 변경

이렇게 간단하게 해결하면 되지만 이미 `JetBrains Mono` 폰트에 길들어져서 폰트를 변경하고 싶지는 않다.

그렇다면 이제 JetBrains Mono 폰트에 한글을 입혀 사용하면 되겠는데 마침 찾아보니 친절하게도 누군가가 만들어두었다.

>  https://github.com/Jhyub/JetBrainsMonoHangul

위의 페이지에 방문해서 Releases 페이지에서 폰트 파일을 다운 받고 설치하면 된다. 

폰트 설치후에는 IntelliJ IDEA 가 자동으로 인식을 하진 못해서 한 번 재시작해야 한다.

**Settings(환경설정) → Editor → Font**

![6](https://raw.githubusercontent.com/ShanePark/mdblog/main/development/intellij/Intellij-copilot-font.assets/6.webp)

이렇게 설정하고 나면 JetBrains Mono 폰트를 사용하며 한글영역만 D2Coding Font를 사용할 수 있다.

![7](https://raw.githubusercontent.com/ShanePark/mdblog/main/development/intellij/Intellij-copilot-font.assets/7.webp)

> 코파일럿의 한글 자동완성이 정상적으로 표시된다.
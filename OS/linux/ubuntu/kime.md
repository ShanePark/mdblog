# Linux) KIME 한글 입력기

> 우분투 IntelliJ IDEA 한글 입력 문제 해결하기

## Intro

한글을 입력 하기 위해서는 한글 입력기가 필요합니다. 다른 여타의 언어와는 다르게 한글은 초성, 중성, 종성으로 이루어지는 특이한 구성을 가지고 있으며, 종성이 있을때도 없을때도 있을 뿐만 아니라 자음이나 모음 각각 마저 여러개의 자음 혹은 모음이 모여 하나의 초성이나 중성 혹은 종성이 되는 경우가 있기 때문에 컴퓨터 입장에서는 참 입력을 받기 난해한 문자 입니다.

`다` 라는 입력을 했을때, 이것이 입력이 끝난 것인지 혹은 `닭`을 입력 하기 위한 과정일 뿐인지 컴퓨터는 물론이고 한국어를 모국어로 사용해온 사람이라도 예측 하기는 어렵습니다. 이로인해 수많은 운영체제에서는 한글 끝 글자 문제가 고질병이고, 그나마 마이크로소프트의 Windows 시리즈의 한글 입력기가 정말 훌륭하지만 아쉽게도 완벽하다고는 하기 어렵습니다.

특히나 MacOS 나 리눅스 사용자가 한글입력기로 고통을 받고 있는데요, MacOS는 일반 사용자라면 크게 불편함을 느끼지 않을 정도가 되었고 이클립스 정도를 제외하면 입력기가 문제가 되는 경우는 잘 없는데, 문제는 리눅스 입니다.

iBus의 한글 입력기가 상당히 발전을 해서 왠만한 상황에서는 문제가 없지만, 메인으로 두고 쓰기에는 금새 한계를 드러냅니다. 

저도 여러가지 한글 입력기를 사용 해보며 포스팅도 해 왔는데요

- [Ubuntu 20.04 키보드 한글 입력 설정 하기ㅣ iBus](https://shanepark.tistory.com/231)
- [Ubuntu 20.04) fctix입력기 설치해 intelliJ 한글입력 해결하기](https://shanepark.tistory.com/262)
- [Ubuntu) 끝판왕 한글 입력기 Tian (nimf)](https://shanepark.tistory.com/293)

이렇게 총 3가지의 입력기에 대해 포스팅도 했었고, 최근까지 Tian을 만족스럽게 사용 해 왔습니다. 제가 리눅스를 사용한지는 아직 반년이 채 되지 않았지만, 한글 입력기에 대해 찾아보니 nimf의 원 제작자 호동님과, nimf를 fork해서 프로젝트를 지속해오던 하모니카 팀 등에 걸친 오랜 여러가지 역사가 있었습니다. 결국 호동님이 따로 nimfsoft 홈페이지를 만들고 소스를 비공개 하는 방식으로 Tian 입력기를 공개 하셨고, 모든 사용에서 거의 완벽에 가까웠기 때문에 `끝판왕` 이라는 수식까지 달며 포스팅 하고 저도 꾸준히 사용 해 오고 있었습니다.

하지만 지난 주 부터 갑자기 apt update가 되지 않기 시작하고, nimfsoft 의 홈페이지도 접근 불가능 한 상태가 되었습니다. 홈페이지에 접속이 안되어 google에 아카이브된 페이지를 들어 가 보니, 호동님이 tian 으로 인해 Nimf의 유지 보수에 악영향을 줄 것이라는 우려를 불식 시키기 위해 Tian의 개발 중단을 선언 하셨더라고요.

![image-20220114094547933](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kime.assets/image-20220114094547933.png)

> 혹시 Tian을 삭제해야 하는 분은 위의 글을 참고해서 삭제 해 주세요. 
>
> `sudo apt purge tian` 후 `.profile`, `.xinitrc` `.xinputrc` `.xprofile` `.xsessionrc` 파일에서
>
> `[ -f /usr/local/etc/input.d/tian.conf ] && . /usr/local/etc/input.d/tian.conf` 내용을 삭제 하면 됩니다.

지금 사용하기에는 더없이 만족 하고 있지만 제가 사용하는 걸 넘어 개발이 중단된 입력기를 추천하는 것 까진 힘들기 때문에 다양한 입력기를 찾아 본 결과  KIME 입력기가 있어 한번 사용 해 보았고, Tian 만큼 만족도가 높았습니다.

그 외 다양한 한글 입력기의 장/단점에 대해서는 아래 링크에 잘 정리 되어 있습니다

> https://dawoum.ddns.net/wiki/한글_입력기

## 설치

![image-20220114090702330](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kime.assets/image-20220114090702330.png)

설치에 앞서 kime 또한 개발하시는 분이 1년간 개발 불가를 선언 하셨음을 말씀드립니다. 최소한 다운로드도 받을 수 없는 상태의 Tian 보다는 미래가 투명 한 상태이기 때문에, 사용하는데 둘이 같다면 KIME를 선택하는게 낫겠습니다.

![image-20220114090959101](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kime.assets/image-20220114090959101.png)

> https://github.com/Riey/kime/releases

일단 위의 releases 페이지에서 ubuntu20.04에 해당하는  `.deb` 파일을 다운 받습니다.

![image-20220114091058125](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kime.assets/image-20220114091058125.png)

그리고 설치 해 줍니다. `sudo dpkg -i 파일명.deb`



![image-20220114091259460](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kime.assets/image-20220114091259460.png)

설치 후에는 Preferences > Region & Language 에 들어가면 Keyboard input method system 에 kime가 추가되어 있는 것을 확인 할 수 있습니다. kime로 설정을 변경 후 재부팅 하면 자동으로 설정 됩니다.

![image-20220114095037662](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kime.assets/image-20220114095037662.png)

> xinputrc 에서 kime를 기본 입력기로 사용 하도록 되어 있습니다.

설치가 잘 되었는지 확인 하려면 LibreOffice 혹은 intelliJ IDEA 처럼 끝 글자 문제가 있었던 소프트웨어를 실행 해서 한글 입력을 해 보시면 됩니다. 모든 입력기 중에 Tian 만이 해결했던 문제들을 KIME도 잘 해결 해 주고 있는 것을 확인 할 수 있었습니다.

## 설정

### 기본설정

![image-20220114093724758](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kime.assets/image-20220114093724758.png)

- `/etc/xdg/` 경로에 `kime`폴더를 생성 해 줍니다.
- `/usr/share/doc/kime/default_config.yaml` 파일을 `/etc/xdg/kime/config.yaml` 경로에 복사 해서 기본 전역 설정 파일을 생성합니다.
- 설정에 대한 자세한 내용은 https://github.com/Riey/kime/blob/develop/docs/CONFIGURATION.ko.md 를 참고해주세요.

저는 일단 기본 설정만으로 기존에 사용하던 내용과 일치 하기 때문에 따로 설정을 변경하지는 않았습니다.

### 한글2020 베타 에서 한글 입력

한글입력이 되지 않기 때문에 약간의 설정이 필요합니다.

![image-20220114095625852](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kime.assets/image-20220114095625852.png)

`/opt/hnc/hoffice11/Bin/qt` 폴더를 삭제 하거나 이름을 다른 이름으로 변경 하면

![image-20220114095656071](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/kime.assets/image-20220114095656071.png)

## 마치며

Tian 한글 입력기가 마지막 희망이라고 생각 했었는데, 다행히도 KIME 입력기가 그에 못지 않게 훌륭합니다. 사실 한글 입력기 같은 분야에서는 오픈소스 생태계에 의존 하는 것 보다, 국가 차원에서 적극적인 투자로 모두가 사용할 수 있는 훌륭한 입력기를 만들거나 혹은 정부차원에서의 이러한 형태의 오픈소스 생태계에 대한 지원이 필요 하다고 개인적으로 생각합니다. 다른 것도 아니고 `한글`에 관한 문제니깐요.

Rust 로 작성된 빠른 속도와 적은 메모리 사용이 장점인 kime. 아직까지 뚜렷한 단점을 찾지 못한 훌륭한 입력기 입니다. 
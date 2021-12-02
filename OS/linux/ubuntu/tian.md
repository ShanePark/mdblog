# Ubuntu) 끝판왕 한글 입력기 Tian (nimf)

> 우분투 IntelliJ IDEA 한글 입력 문제 해결하기

## Intro

여러가지 한글 입력기를 다 사용 해 보았습니다만 마음에 드는 입력기를 찾기까지 정말 오래 걸렸습니다.

iBus로 솔직히 왠만한 입력 문제는 모두 해결이 되지만, snap의 몇몇 어플리케이션이나 intelliJ IDEA에서 한글을 입력 할 때에 문제가 있었습니다.

그러다가 `fctix` 입력기를 찾아 한동안 큰 문제 없이 사용했는데요. 우분투에서 주로 사용하는 IDE를 이클립스에서 IntelliJ IDEA로 변경하고 나니 끝 글자가 잘리거나 하는 소소한 문제들이 더 발견되어 도저히 안되겠다는 생각이 들었습니다.

그러다가 nimf 를 사용한 분들의 만족도가 아주 높다는 것을 확인하고는, 한번 설치 해 보았습니다. 이전에는 하모니카 리눅스에서 관리해주는 nimf를 설치해보려다가 여러가지 문제가 있었기때문에 안쓰고 말았었는데, 우연히 `nimfsoft`라는 페이지를 발견 했고, tian 이라는 이름으로 새로이 관리가 되고 있다는 것, 그리고 최근까지 꾸준히 업데이트가 진행 되고 있다는 걸 확인 하고는 한번 설치 해서 사용 해 보았고, 드디어 완벽이라는 단어가 어울리는 리눅스 한글 입력기를 발견 했단 생각이 듭니다.

## 다운로드

>  https://www.nimfsoft.com/

![image-20211202113721913](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/tian.assets/image-20211202114209166.png)

> https://www.nimfsoft.com/downloads/

위의 우분투 20.04 용 버전을 다운로드 했습니다.

안내해주는 대로 apt로 설치를 해 주었습니다.

```zsh
sudp apt install ./tian*
```

![image-20211202113721913](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/tian.assets/image-20211202113721913.png)

> 금방 설치가 됩니다.

## 설정

Tian Settings 라는 프로그램이 추가되었습니다. 실행 합니다.

![image-20211202113812265](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/tian.assets/image-20211202113812265.png)

Tian Settings 를 켜고는 한/영 전환 키를 Hangul로 변경 해주고, 아래의 옵션들을 켜 줍니다.

- Prevent last character bugs
- Setup environment variables
- Use memory saving mode

![image-20211202113845186](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/tian.assets/image-20211202113845186.png)

설정이 끝났으면 재부팅 혹은 로그아웃해 줍니다.

## 확인

![image-20211202114103249](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/tian.assets/image-20211202114103249.png)

로그인을 다시 하고 확인하니 알아서 키보드 설정이 되어 있습니다.

### Intellij IDEA

인텔리제이에서 한글을 사용해보니 아주 완벽하게 작동합니다.

### 한컴오피스에서 사용

한컴오피스를 실행 했더니 한글이 입력되지 않았습니다. nimfsoft 홈페이지에 해당 정보가 있습니다.

> https://www.nimfsoft.com/support/how-to-use-tian-in-hoffice/

우분투에 한컴오피스를 설치한 후에 tian 을 사용하기 위해서는 입력 모듈(플러그인)에 대한 링크를 만들어 주어야 한다고 합니다. 다음 명령으로 링크를 만들어줍니다.

```
sudo ln -sf /usr/lib/x86_64-linux-gnu/qt5/plugins/platforminputcontexts/libqt5im-tian.so /opt/hnc/hoffice11/Bin/qt/plugins/platforminputcontexts/libqt5im-tian.so

```

그 외 카카오톡 등에서 사용하는 방법도 모두 기술지원에 안내되어 있습니다. 저는 카카오를 사용하지 않아 생략합니다.

> https://www.nimfsoft.com/support/

이상입니다.

우분투를 사용하며 항상 고민했던 입력기 문제를 더이상 생각하지 않아도 된다는게 너무 좋습니다.

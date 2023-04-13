# Ubuntu) 마우스 버튼 커스터마이징

<img src=https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/mouseMapping.assets/image-20211220124140207.webp width=750 height=378 alt=1>

> https://www.logitech.com/en-us/products/mice/mx-master-3.html

## Intro

 MX-master와 같이 몇몇 버튼이 많은 마우스를 사용 한다면 버튼별로 기능을 커스터마이징 해서 사용하게 되는데요, 보통 윈도우나 맥용으로는 제조사에서 유틸성 프로그램을 제공해 주는 편이지만 소수의 사용자까지 배려해주지는 못하다보니 리눅스용으로는 없는게 현실입니다.

그래서 직접 버튼들을 하나하나 맵핑해서 사용해 줘야 하는데요, 저는 단순한 마우스이지만 맥북에서 마우스 가운데 버튼을 미션컨트롤용으로 사용하다 보니 우분투에서도 같은 용도로 사용하고 싶어서 설정하게 되었습니다.

## 설치

xbindkeys 와 xdotool 등이 필요합니다.

아래의 명령어를 입력해 한번에 설치 해 줍니다. 저는 `xdotool`이 이미 있기때문에 앞의 두가지만 설치 했습니다.

```bash
sudo apt install xautomation xbindkeys xdotool
```

![image-20211220112736393](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/mouseMapping.assets/image-20211220112736393.webp)

## 설정

### 입력장치 확인

혹시 모르니 시작에 앞서 입력장치들을 한번 확인 해 줍니다.

```bash
xinput --list
```

![image-20211220112845243](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/mouseMapping.assets/image-20211220112845243.webp)

> 사용중인 키보드, 마우스 및 각종 입력장치들이 표기됩니다. `Logitech G304` 마우스를 사용 하고 있는데, id 가 11번이네요.

### 테스트

`xev`를 실행 해서 맵핑할 마우스 버튼의 키 입력 값을 확인 해 줍니다. 터미널에 xev를 입력해 실행하면 이벤트 테스터가 동작합니다.

```bash
xev
```

![image-20211220113745173](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/mouseMapping.assets/image-20211220113745173.webp)

이제 Event Tester 안에서 각각의 키를 눌러보며 확인합니다.

![image-20211220113947594](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/mouseMapping.assets/image-20211220113947594.webp)

> 하나씩 눌러보며 제 마우스에서는 좌클릭은 `button 1`, 우클릭은 `button3`, 중간 버튼은 `button 2` 인 것을 확인 했습니다.

### 설정

이제 버튼 번호들을 알아 냈으니 원하는 키로 맵핑 해 줍니다.

일단 `xbindkeys` 를 실행 해보니 설정파일이 없다는 에러가 나옵니다.

```bash
xbindkeys
```

![image-20211220114248358](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/mouseMapping.assets/image-20211220114248358.webp)

> xbindkeys를 실행 해 보니 ~/.xbindkeysrc 경로에 기본 설정 파일을 만들어 달라고 합니다.

안내해준 대로 기본 xbindkeys 설정 파일을 생성 해 줍니다.

```bash
xbindkeys --defaults > ~/.xbindkeysrc
```

생성이 되었으면 파일을 열어 수정 합니다.

```bash
vi ~/.xbindkeysrc
```

![image-20211220114629106](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/mouseMapping.assets/image-20211220114629106.webp)

적당한 위치에 필요한 내용을 입력 합니다. 저는 가운데 버튼을 Super키에 맵핑 시키려고 합니다.

`xdotoll` 외에도 `xte` 혹은 `xvkbd` 등을 이용 할 수 있습니다. 편한 방법으로 매핑 해 주시면 됩니다.

```bash
# Mouse middle button
"xdotool key super"
b:2 + Release
```

![image-20211220114818089](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/mouseMapping.assets/image-20211220114818089.webp)

> 저는 처음에 위의 스샷과 같이 Release를 안넣고 하니 어떨땐 되다 안되다 하는 불편함이 있었습니다. 
>
> 이후 Release를 더해주니 의도대로 잘 작동 합니다.

이제 `wq!`로 저장하고 나와서 xbindkeys를 재 시작 해 줍니다.

```bash
killall xbindkeys
xbindkeys -f ~/.xbindkeysrc 

```

<br><br>

이제는 마우스 중간 버튼을 누르면 Super 키가 작동 합니다.

이 외에도 원하시는 버튼으로 각자 필요에 맞춰 매핑 해 주시면 됩니다.




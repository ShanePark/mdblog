# Ubuntu) notion App 설치

​	

## Notion

노션은 굉장히 유용한 노트 앱 입니다. iPhone, Android의 모바일 어플리케이션 뿐만 아니라 Windows, Mac용 프로그램도 존재 합니다. 굳이 프로그램을 깔지 않더라도 애초에 웹 기반으로 프로그램이 만들어 졌기 때문에 웹브라우저 에서도 완벽한 사용성을 보장 합니다. 

심지어 요즘에는 `Notion Clipper` 라는 웹브라우저용 플러그인을 설치해서 사용 하고 있는데요, 만족도가 상당히 높습니다. 즐겨찾기를 하는 대신 해당 페이지를 갈무리 해서 내 노션에 저장하는건데. 해당 글이 삭제 된 경우에도 확인 할 수 있고 원본 링크도 언제든 확인 가능하게 연결이 되어 있으니 정말 좋습니다.

![image-20211027091148062](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/notion.assets/image-20211027091148062.png)

> 최근에는 저장해뒀다가 나중에 더 깊게 공부하고 싶은 내용들을 `Clip` 해 두고 있습니다.

하지만 아쉽게도 Linux용 공식 어플리케이션은 아쉽게도 찾아 볼 수 없었는데요

![image-20211027090548669](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/notion.assets/image-20211027090548669.png)

> Notion은 Linux 지원 계획이 없다고 합니다.

Chrome이나 Firefox 등의 브라우저를 따로 띄워서 사용해도 완벽하게 동일한 사용자 경험을 할 수 있지만, 그래도 브라우저 따로 띄우고, 즐겨찾기에서 해당 페이지 방문 하고 하는, 또 다른 탭들과 따로 관리하는 번거로움보다는 어플리케이션이 있는 편이 확실히 편합니다.

Lotion과 Notion-enhancer가 있으니 읽어보시고 더 마음에 드는걸 설치하시면 됩니다. 특별한 경우가 아니라면 후자를 선택 하시는게 좋습니다.

## Lotion

Linux에서 Notion 어플리케이션을 쓰고싶은 사용자들의 많은 노력이 있었고, 실제로 여러가지 프로젝트들이 존재 했지만 대부분의 경우는 개발을 중단 한 상태 입니다. 그 중 사용 가능한게 Lotion 입니다.

> https://github.com/puneetsl/lotion

### 7Zip 설치

![image-20211027091811073](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/notion.assets/image-20211027091811073.png)

설치시 7z wget을 요구합니다. 저도 그냥 설치하려고 했다가 필요한 프로그램이 없어서 설치가 안되어 확인을 해 보니 7z를 깔아야 되더라구요.

> 7Zip은 LZMA 압축 알고리즘을 기반으로 개발된 훌륭한 압축 프로그램 입니다. 개인이나 단체 모두 무료로 사용 할 수 있습니다. 자세한 정보는 https://www.7-zip.org/7z.html 를 참고해주세요.

설치 자체는 간단합니다.

```bash
sudo add-apt-repository universe
sudo apt update
```

```bash
sudo apt install p7zip-full p7zip-rar
```

### Lotion 설치

설치 방법도 굉장히 간단 합니다.

![image-20211027092002449](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/notion.assets/image-20211027092002449.png)

curl 혹은 wget으로 setup.sh 파일을 다운 받습니다.

```bash
curl https://raw.githubusercontent.com/puneetsl/lotion/master/setup.sh > setup.sh
```

실행 가능하게 권한 설정을 해 주고요

```bash
# Make the script executable
chmod +x setup.sh

```

설치 해 주시면 됩니다.

 native와 web중 골아야 하는데 저는 native로 설치 했습니다.

```bash
# Run (with sudo for global installation, without sudo for local installation)
sudo ./setup.sh native
# Or for web installation
sudo ./setup.sh web

```

설치가 된 후엔 Notion native 라는 이름으로 저장이 됩니다. dock 에 favourite 추가를 해 두고 사용하시면 됩니다.

![image-20211027092241360](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/notion.assets/image-20211027092241360.png)

​		

![image-20211103153758235](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/notion.assets/image-20211103153758235.png)

참고로 로그인시에는 `Continue with Google`을 눌러 하는 Google SSO login을 사용할 수 없는데요, 구글이 인가받지 않는 브라우저들에 대한 로그인 지원을 중단했기 때문입니다.

간단하게 아래의 Continue with email을 사용해서 이메일 인증을 통한 로그인 한다면 문제는 간단하게 해결됩니다.				

![image-20211027092451478](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/notion.assets/image-20211027092451478.png)

어플리케이션을 실행 한 모습입니다. dark 모드 설정도 가능 하며 속도도 빠르고 굉장히 만족스럽게 사용 할 수 있습니다. 

## Notion Enhancer

> https://notion-enhancer.github.io/getting-started/installation/

Lotion은 개발이 끊겼기 때문에 최근에도 꾸준히 개발중인 Notion-Enhancer가 좀 더 나은 선택 입니다.

Lotion 쓰며 거의 완벽한 사용성이 보장되지만 가끔 불편한 점이 몇가지 있었는데요 (이메일 로그인만 됨, 가끔 페이지가 안떠서 Ctrl + R 로 새로 고침 필요)  Notion-Enhancer는 거의 완벽합니다. SSO 로그인도 잘 됩니다.

설치는 간단 합니다.

```bash
echo "deb [trusted=yes] https://apt.fury.io/notion-repackaged/ /" | sudo tee /etc/apt/sources.list.d/notion-repackaged.list
sudo apt update
sudo apt install notion-app-enhanced
```

이상입니다.
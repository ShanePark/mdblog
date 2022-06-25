# [Ubuntu 20.04] 우분투 서버에 Plex 미디어 스트리밍 서버 구축기

## Intro

집에서 와이프가 넷플릭스로 드라마를 보다가 스트리밍 서비스에 올라와있지 않은 영상은 애플티비로 볼 수 없는지 투덜대었습니다.

집에 남는 노트북에 우분투 서버를 올려 개인 서버 역할을 한지 어느덧 1년이 넘어가고 있는데, 그걸 이용하면 될 것 같아서 일단 된다고 대답을 했습니다.

그리하여 시작된 Plex 미디어 스트리밍 서버 구축기를 시작 해 보겠습니다.

## PLEX 설치

### 패키지 업그레이드

일단 시작에 앞서 모든 패키지를 업그레이드 해 줍니다.

```ㅠㅁ노
sudo apt update && sudo apt upgrade -y
```

### Plex 공식 저장소 등록

![image-20220625085736404](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/plex.assets/image-20220625085736404.png)

> https://support.plex.tv/articles/235974187-enable-repository-updating-for-supported-linux-server-distributions/

plex 사이트에 안내된대로 저장소를 등록 해 줍니다.

```bash
echo deb https://downloads.plex.tv/repo/deb public main | sudo tee /etc/apt/sources.list.d/plexmediaserver.list
curl https://downloads.plex.tv/plex-keys/PlexSign.key | sudo apt-key add -
```

### plex 서버 설치

저장소가 등록이 되었으면 설치 해 줍니다.

```bash
sudo apt update
sudo apt install plexmediaserver
```

![image-20220625090012343](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/plex.assets/image-20220625090012343.png)

> 종속성 설치에 316 MB의 추가 디스크가 필요 하다고 합니다.

![image-20220625090114808](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/plex.assets/image-20220625090114808.png)

> 설정 파일이 생성 되었는데 패키지 안에도 있다며 어떤걸 이용할 지 물어봅니다. 저는 만든 적 없는데 있다고 하니 알아서 패키지에 포함된걸로 해 달라고 Y 를 입력 합니다.

설치가 끝난 후에는 plex 서버가 돌아가고 있는지 확인 해 봅니다.

```bash
sudo systemctl status plexmediaserver
```

![image-20220625090355975](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/plex.assets/image-20220625090355975.png)

> 233MB의 메모리를 사용 하며 잘 돌아가고 있다고 합니다.

32400 포트를 사용한다고 알고 있는데, 혹시나 랜딩페이지나 헬스포인트가 있을까 싶어 내부아이피로 요청을 보내 보았더니

![image-20220625090704237](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/plex.assets/image-20220625090704237.png)

XML로 응답이 정상적으로 옵니다! 서버가 잘 돌아가고 있습니다.

랜딩페이지를 찾아보니 `/web` 이라고 하길래 `192.168.0.10:32400/web` 로 요청을 보내 보았습니다.

![image-20220625091855889](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/plex.assets/image-20220625091855889.png)

> 로그인 하는 페이지가 나옵니다! 저는 구글 아이디로 로그인을 해 보았습니다.

![image-20220625092402188](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/plex.assets/image-20220625092402188.png)

> 로그인 후에는 정상적으로 Plex 서버에 접속 하게 됩니다. 혹시 저처럼 외부망이 아니고 내부 망으로 접속했다면 연결이 바로 되지 않고 무한 로딩이 될 수  있습니다. 그럴때는 아이피주소:포트/web 주소를 다시 입력해 접속 해 줍니다.

![image-20220625092430225](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/plex.assets/image-20220625092430225.png)

> 요금제에 대한 정보가 나오지만, 무료로도 충분히 이용 할 수 있습니다. 우측상단의 X를 클릭해 닫아줍니다.

## PLEX 설정

x 를 눌러 요금제 팝업을 닫고 나면 설정 화면이 나옵니다.

### 이름 설정

![image-20220625092542796](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/plex.assets/image-20220625092542796.png)

원하는 이름을 설정 하고 NEXT를 눌러 줍니다. 외부 네트워크에서도 접속 할 수 있게 하려면 체크박스도 표시 해 줍니다.

NEXT를 누르고는 제법 설정에 시간이 걸립니다.

### Media Library

![image-20220625093005432](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/plex.assets/image-20220625093005432.png)

이번에는 미디어 라이브러리를 설정 해야 하는데요. 아직 준비 한 파일이 없으니 준비를 해야 합니다.

아래는 설치 가이드에서 알려주는 폴더, 파일명 규칙 입니다.

![image-20220625093058828](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/plex.assets/image-20220625093058828.png)

> https://support.plex.tv/articles/200288586-installation/

이를 활용해서 테스트용으로 영화 트레일러를 하나 넣어 보겠습니다.

```bash
mkdir -p  plexmedia/{movies,series}
```

![image-20220625093401489](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/plex.assets/image-20220625093401489.png)

일단 적당한 위치에  plexmedia 라는 폴더를 생성하고, 그 아래 movies, series 라는 폴더를 만들었습니다.

이제 탑건 영화 예고편을 movies 하위에 `Top Gun(2022).mov` 라는 이름으로 다운 받아봅니다.

```bash
wget https://movietrailers.apple.com/movies/paramount/top-gun-maverick/top-gun-maverick-trailer-3_h1080p.mov ~/plexmedia/movies
```

![image-20220625093850443](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/plex.assets/image-20220625093850443.png)

```bash
mv top-gun-maverick-trailer-3_h1080p.mov 'Top Gun(2022).mov'
```

파일명은 plex 가 원하는 대로 맞춰 줍니다.

이제 미디어 라이브러리가 준비 되었으니 등록 해 봅니다.

![image-20220625094147714](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/plex.assets/image-20220625094147714.png)

> Type은 Movies로

![image-20220625094218311](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/plex.assets/image-20220625094218311.png)

> `/home/shane/plexmedia/movies` 경로를 추가 합니다.

![image-20220625094332685](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/plex.assets/image-20220625094332685.png)

같은방법으로 Tv Show도 추가 해 주었습니다. 이제 NEXT를 클릭 합니다.

![image-20220625094357699](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/plex.assets/image-20220625094357699.png)

> 모든 설정이 준비 되었다고 합니다.

![image-20220625094412774](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/plex.assets/image-20220625094412774.png)

> 사이드바에 나올 메뉴를 선택 하고 FINISH SETUP을 클릭 해 줍니다. 맨위에 Movies, Tv Shows 만 빼고 다 선택 해제하는걸 추천합니다.

![image-20220625094431382](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/plex.assets/image-20220625094431382.png)

드디어 구축한 서버가 준비 되었습니다! 

이제 Movies 폴더를 클릭 해 보면..

![image-20220625094712566](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/plex.assets/image-20220625094712566.png)

> 제가 준비해둔 Top Gun 예고편이 들어 가 있습니다.

제목과 년도를 파일명에 넣어 줬을 뿐인데, 알아서 Plex가 영화 제목과 영화 포스터를 넣어 주네요.

이제 재생을 해 보려 하는데..

![image-20220625100056495](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/plex.assets/image-20220625100056495.png)

> Error code: s1001 (Network)

저는 에러가 발생 했습니다. 여기서 무사히 재생이 된다면 그걸로 완료지만 s1001 에러가 발생했을때는 대부분 DB가 꼬여서 그렇다고 `com.plexapp.plugins.library.db' 파일을 초기화 해 주면 된다고 합니다.

 저는 그냥 서버를 재설치 해 보았고, 해결이 되었습니다.

## Plex Media Server 삭제

![image-20220625103440565](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/plex.assets/image-20220625103440565.png)

> https://support.plex.tv/articles/201941078-uninstall-plex-media-server/

공식 홈페이지에 나와있는 삭제 방법을 따르면 됩니다.

```bash
dpkg -r plexmediaserver
```

```bash
sudo rm -rf /var/lib/plexmediaserver/Library/Application Support/Plex Media Server/
```

이후 재설치는 위로 다시 올라가 PLEX 설치의 저장소 등록, APT 업데이트 부터 다시 똑같이 진행 하면 됩니다.

![image-20220625103326164](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/plex.assets/image-20220625103326164.png)

> 재 설치 후에 영상이 정상적으로 재생 되는것이 확인 됩니다.

## 마치며

처음 인트로에서 말했던 것 처럼, 이제 Apple TV로 재생을 해주기만 하면 됩니다.

별로 어렵지 않게, Apple TV 에서 PLEX 어플을 다운 받고, 방금 PC에서 로그인 했던 아이디로 로그인만 해주면 자동으로 로컬 서버로 접속을 해줍니다.

지금까지 구축한 PLEX 서버를 통해 와이프는 바로 거실에서 재밌게 티비를 보고 있습니다.

이렇게 PLEX를 활용하면 집에 자신만의 동영상 스트리밍 서버를 간단하게 구축 할 수 있으니 한번 해두는것도 제법 괜찮습니다.

이상입니다.

ref

- https://linuxize.com/post/how-to-install-plex-media-server-on-ubuntu-20-04/

- https://support.plex.tv/articles/201941078-uninstall-plex-media-server/
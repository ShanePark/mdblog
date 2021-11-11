# MacOS) m1 맥북 Oracle SQL Developer (Docker 없이) 사용하기 및 the directory is not accessible 에러 해결하기

<br><br>

## 시작하기 앞서

> 꾸준히 최신 정보로 수정 중입니다. 최근 수정일자를 확인해주세요.

이미 SQL Developer 를 실행하는건 성공을 했는데 데이터베이스 구축을 하는 방법을 찾고 있는 분은 두 가지 방법이 있습니다.

1. Docker를 이용한 가상 환경에 DB 구축

> 현재 (2021. 08. 14) m1 맥북에서 Oracle 데이터베이스를 가동할 수 있는 방법은 전혀 없습니다. 수많은 해외 포럼도 찾아봤지만 그 누구도 Apple Silicon에서 OracleDB 구동한 사람은 아직 없습니다. arm64 방식으로 칩셋이 전혀 다르기 때문에 Docker를 사용해도 불가능합니다. 저도 여러가지 방법을 다 해봤는데 불가능 했습니다. 

2. 외부 서버에 DB 구축

> 저는 무조건 이 방법을 권장합니다. 더 쉬운 AWS RDS를 사용할 수도 있지만 아마존은 잘못 사용했다가는 요금 폭탄을 맞을 수 있습니다. 반면 오라클 클라우드는 free tier가 애초에 나뉘어 있기 때문에 과금의 위험성이 전혀 없습니다.
>
>  무료로 외부 DB를 구축해두고 사용하고 싶다면 아래 링크를 확인 해서 따라하면 하실 수 있습니다. 초보자에게는 다소 난이도는 있을 수 있지만 한번 DB를 구축해둔다면 앞으로 개발하시면서 평생 정말 유용하게 사용하실 수 있습니다. 몇단계 성장의 계기가 될 수도 있습니다.

[ Mac) Oracle FreeTier 이용해 웹 어플리케이션 배포하기 4) 무료 데이터 베이스 만들기 및 데이터 이관](https://shanepark.tistory.com/173)

<br><br>

## SQL Developer

> 지금부터 SQL Developer를 실행 하는 방법에 대한 글을 시작하겠습니다. 서버 구축과는 별개입니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/m1oracle.assets/img-20211111203540321.png)

Docker 가 Apple Silicon을 정식으로 지원하기 시작했습니다.

제가 처음 맥북을 샀을때만 해도 Apple Silicon은 고사하고, Rosetta 환경에서도 정상적으로 작동하지 않아서 고생을 했는데요, 

그때부터 사용하던 방식을 Docker를 지원하기 시작한 지금도 여전히 사용중입니다.

<br><br>

저는 팀원들과 만들었던 프로젝트들을 항상 구동해두기 위해 24시간 켜두는 노트북 서버가 집에 있기때문에  맥북에서 따로 서버를 켤 필요가 없었습니다. 

하지만 항상 켜두는 컴퓨터가 없더라도 AWS에서 Free Tier RDS 를 생성하거나 Oracle Cloud 에서 무료로 Instance를 생성해 본인만의 서버를 만든다면 매우 유용하게 사용하실 수 있습니다. 처음엔 꽤 어려울 수 있긴 한데 충분히 도전해 볼 가치가 있다고 생각합니다.

<br><br>

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/m1oracle.assets/img-20211111203540454.png)

아쉽게도 Oracle Database는 공식적으로 Windows 와 Linux 에서만 구동이 가능합니다. 그래서 Mac OS 에서는 Docker를 이용해서 서버를 켜는 건데, 반대로 서버가 준비되어 있고 SQL Developer만 사용한다면 굳이 Docker를 사용할 전혀 필요가 없습니다.

SQL Developer는 Java로 구동되기 때문에 어떤 플랫폼에서든 문제없이 얼마든 실행 할 수 있습니다.

<br><br>

### SQL Developer 다운로드 

[www.oracle.com/tools/downloads/sqldev-downloads.html](https://www.oracle.com/tools/downloads/sqldev-downloads.html)

일단 SqlDeveloper를 다운 받습니다. 최신 버전을 다운 받으면 됩니다.

<br><br>

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/m1oracle.assets/img-20211111203540346.png)

<br><br>Mac OSX 용을 다운 받으면 되는데 , JDK 8 OR 11 Required 라고 써 있습니다.

오라클 JDK 8, 11 버전과 모두 사용해봤는데 굉장히 커넉 패닉 현상이 빈번하게 일어 났습니다. 

심지어 14 혹은 16 버전만 설치하면 JDK8 버전 이상을 설치하라고 하며 실행도 되지 않습니다.

한동안은 저는 adoptopenjdk 14 버전을 설치해서 잘 사용 해 왔는데, 특정 SQL Developer 버전부터는 Oracle JDK 가 아니면 JDK 8 이상 버전을 설치하라는 에러 메시지가 뜨면서 실행이 되지 않았습니다.

사실 저는 여러번 해당 문제를 해결을 했었는데요. SQL Developer가 새로운 버전이 나오면 Oracle 사에서는 구 버전에 대한 다운로드 링크를 아에 삭제해 버리다 보니 구 버전에서의 해결책이 신 버전에서 안되는 경우가 잦았습니다. 그래서 다소 번거로울 수 있지만 어떤 버전에서든 통했던 방법을 알려드리겠습니다.

<br><br>

### 실행 방법1

그냥 다운받아 실행해도 문제 없이 실행 되는 분들은 상관 없지만, m1 환경에서 자주 SQL developer가 튕기거나 하는 분들은 새로운 실행 방법을 추천드리겠습니다.

<br><br>

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/m1oracle.assets/img-20211111203540324.png) 

자바 버전을 한번 확인 해 보았습니다.

<br><br>

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/m1oracle.assets/img-20211111203540357.png)

다운받은 SQLDeveloper.app 파일을 

<br><br>

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/m1oracle.assets/img-20211111203540484.png)

Applications 폴더에 넣어줍니다.

실행해보면

<br><br>

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/m1oracle.assets/img-20211111203540375.png)

뭐 의심 된다고 실행을 안해줍니다.

<br><br>

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/m1oracle.assets/img-20211111203540342.png)

설정 - > Security & Privacy에서 Open Anyway를 클릭 하면 실행 됩니다.

<br><br>

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/m1oracle.assets/img-20211111203540334.png)

<br><br> 

자바 버전이 어쩌구 하면서 켜지지가 않는데요 여기에서 두가지 방법이 있습니다.

1. 자바가 없다면 자바를 해당 링크에서 받아 설치합니다. 자바는 있긴 있어야 합니다. 

   자바 설치하고 실행 하면 실행은 잘 될 수 있는데 m1 맥북 이라면 아마 자주 튕길 수도 있습니다.

<br><br>

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/m1oracle.assets/img-20211111203540351.png)

### 실행방법2

> 자바가 있어도 저런 에러가 뜬다거나, 혹은  SQL developer가 너무 많이 튕긴다면 사용 해 볼수 있는 방법입니다.

```zsh
cd /Applications/SQLDeveloper.app/Contents/resources/sqldeveloper
zsh sqldeveloper.sh 
```

터미널에 위의 명령을 입력하면 바로 SQL Developer가 켜집니다.

<br><br>

![img](https://blog.kakaocdn.net/dn/bWB9Wb/btq9jGLVd6z/RSIXp42Ck2mEjOC8yPqkU0/img.png)

자바 버전이 높아서 경고가 나옵니다. 위에서 나온 경로에 찾아가보겠습니다.

<br><br>

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/m1oracle.assets/img-20211111203540356.png)

여기에서 product.conf 파일을 수정할 수도 있고, 위의 명령어를 항상 입력해서 실행 하실 수도 있습니다.

<br><br>

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/m1oracle.assets/img-20211111204707454.png)

위의 product.conf 내용을 보면, SetJavaHome 부분이 원래는 주석 처리 되어있는데요, 본인의 jdk 경로를 넣으시고 주석을 풀으면 됩니다.

저의 경우에는

```zsh
SetJavaHome /Library/Java/JavaVirtualMachines/jdk-14.0.2.jdk/Contents/Home
```

이런 식으로 작성 하였습니다.

이렇게 한번 설정해주시면 SQL Developer를 실행할 때 실행이 안되던 문제가 말끔하게 해결됩니다.

<br><br>

혹시 본인의 자바 경로를 모르신다면,

```zsh
/usr/libexec/java_home -V
```

위의 명령어로 설치되어있는 자바 경로를 확인 하실 수 있습니다.

<br><br>

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/m1oracle.assets/img-20211111203540463.png)

> 실행이 잘 되는 모습

<br><br>

product.conf 를 수정하고 싶지 않고 명령어를 입력하기 번거롭다면  위의 명령어를 자동으로 실행하도록 .app 파일도 직접 만들어 보았습니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/m1oracle.assets/img-20211111203540464.png)

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/m1oracle.assets/img-20211111203540460.png)

혹은 `~/.zshrc`에 alias를 걸어 두고 실행 해도 됩니다.

<br><br>

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/m1oracle.assets/img-20211111203540394.png)

처음 실행하면 이런식으로 Apple이 체크하지 못한 앱이라 실행이 안된다는 문구가 나오는데요

System Preferences - > Security & Privacy -> General에 가서

<br><br>

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/m1oracle.assets/img-20211111203540482.png)



Open Anyway를 클릭하면

<br><br>

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/m1oracle.assets/img-20211111203540455.png)

바로 실행이 됩니다. 하지만 저는 product.conf 파일을 수정해서 실행하시길 권해드립니다.!

<br><br>

### 둘중 어떤 방법으로?

위에서 제시한 여러 가지 방법을 사용해 보시고 본인에게 편한 방법을 사용하시면 됩니다.

저는 개인적으로 JAVA 버전만 잘 관리 해낼 수 있다면 자바를 여러가지 버전을 사용해보는게 제일 좋다고 생각합니다. JAVA8 에서만 돌릴 수 있는 오래된 프로그램들도 있고, ( 전자정부 프레임워크 Eclipse는 자바16 에서 에러가 굉장히 심해 JDK 8을 지정해서 구동해야 했습니다) 반대로 최신 자바에서 힘을 쓰는 프로그램들도 있기 때문입니다. ( SQL Developer는 JDK 8 환경에서 구동하기가 극악입니다)

저는 자바를 2가지 버전 설치해두고 필요에 따라 사용하고 있습니다.

<br><br>

## the directory is not accessible 해결

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/m1oracle.assets/img-20211111203540564.png)



Sql Developer 안에서 File이나 Folder에 접근하려고 할때 sql developer the directory is not accessible 에러가 난다면,

bash의 권한이 없기 때문입니다.

<br><br>

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/m1oracle.assets/img-20211111203540597.png)

<br><br>

bash 에 Full Disk Access를 부여하면 문제없이 잘 사용할 수 있습니다!

bash 에 권한 부여는  아래 링크를 참고했습니다.

<br><br>

[community.oracle.com/tech/developers/discussion/4477224/sql-developer-cant-read-files-from-the-users-documents-folder-in-mac-os](https://community.oracle.com/tech/developers/discussion/4477224/sql-developer-cant-read-files-from-the-users-documents-folder-in-mac-os)

간단히 설명하자면, Security&Privacy - > Full Disk Access 에서

자물쇠 풀고

`+` 버튼 누르고

` /` 누르고

`go to Folder` 팝업창이 뜨면 `/bin/bash` 라고 입력합니다.

<br><br>

이제 아래 처럼 나왔을때 OPEN 눌러서 bash에 + 체크가 되면 끝 입니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/m1oracle.assets/img-20211111203540518.png)

<br><br>

이제 폴더들을 잘 탐색 할 수 있습니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/m1oracle.assets/img-20211111203540527.png)

<br><br>

## 마치며

위에 있는 내용들을 한번 쭉 읽어보고 차근 차근 따라하신다면 어느 맥북에서건 큰 무리 없이 Sql Developer를 구동하실 수 있을 거라 생각됩니다. 혹시 막히는 부분이 있다면 댓글이나 이메일 보내주시면 최대한 빨리 도와드리겠습니다.

<br><br> 

Sql developer를 실행하는 것을 넘어서, DB를 구축하고 싶은분은 아래 링크를 참고해서 무료 DB 를 하나 여는걸 추천합니다.

[Mac) Oracle Cloud FreeTier 이용해 웹 어플리케이션 배포하기 4) 무료 데이터 베이스 만들기 및 데이터 이관하기](https://shanepark.tistory.com/173)

<br><br>

또한 마지막으로 SQL Developer 실행하는데 위 글을 읽어도 불편함을 겪는 분들은 더 쉽게 사용할 수 있는 SQL Client가 있기 때문에 DBeaver를 추천 해 드리겠습니다. 기존에 SQL Developer를 사용하시던 분들도 한번 써보시길 강력 추천합니다.

[ DBeaver 설치 하고 Oracle, MariaDB 접속해보기](https://shanepark.tistory.com/180) 
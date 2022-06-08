# Mac) Oracle Cloud FreeTier 이용해 웹 어플리케이션 배포하기 2) 톰캣 설치하기 및 배포 + 외부 접속 허용

|관련 글 목록|
| ------------------------------------------------------------ |
| [Mac) Oracle FreeTier 이용해 웹 어플리케이션 배포하기 1)인스턴스 만들고 접속하기.](https://shanepark.tistory.com/170) |
| [Mac) Oracle FreeTier 이용해 웹 어플리케이션 배포하기 2) 톰캣 설치하기 및 배포 + 외부 접속 허용](https://shanepark.tistory.com/171) |
| [Mac) Oracle FreeTier 이용해 웹 어플리케이션 배포하기 3) 타임존 문제 해결](https://shanepark.tistory.com/172) |
| [Mac) Oracle FreeTier 이용해 웹 어플리케이션 배포하기 4) 무료 데이터 베이스 만들기 및 데이터 이관하기](https://shanepark.tistory.com/173) |
| [Mac) Oracle Cloud FreeTier 이용해 웹 어플리케이션 배포하기 5) 톰캣 도메인 연결해 배포하기](https://shanepark.tistory.com/174) |
| [Mac) Oracle Cloud FreeTier 이용해 웹 어플리케이션 배포하기 6) 오라클 전자지갑 사용해 프로젝트와 연결하기](https://shanepark.tistory.com/207) |<br><br>

일단 ubuntu에 접속을 합니다. 이미 해보셨듯이 ssh ubuntu@ip 로 접속 하시면 됩니다.

## Tomcat 9

### JDK 설치

설치에 앞서 apt 업데이트를 해 줍니다.

```xml
sudo apt update
```

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img.png)

이제 JDK를 설치 해 줍니다.

```xml
sudo apt install default-jdk
```



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-16378983907701.png)



304MB 를 다운 받는다고 합니다. Y 혹은 엔터키를 쳐서 설치 합니다.

### Tomcat 설치

#### tomcat 8 설치

https://tomcat.apache.org/download-80.cgi

톰캣 홈페이지에 방문해서 다운받을 버전을 선택 합니다. 저는 8.5 버전을 설치할 예정입니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-16378983907702.png)



그래서 설치할 버전의 tar.gz의 link를 copy 합니다.

```xml
https://dlcdn.apache.org/tomcat/tomcat-8/v8.5.73/bin/apache-tomcat-8.5.73.tar.gz
```

위와 같은 주소가 복사 되는데요, wget 명령어를 이용해 다운 받습니다. opt 폴더에 다운 받아 보도록 하겠습니다.

폴더가 없다면 미리 mkdir /opt를 해 주세요.

```xml
sudo wget https://dlcdn.apache.org/tomcat/tomcat-8/v8.5.73/bin/apache-tomcat-8.5.73.tar.gz -P /opt/
```

![image-20211126215544291](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/image-20211126215544291.png)

금방 다운이 완료 됩니다. 

다운이 완료 되면  /opt 디렉터리에 Tomcat 아카이브를 추출 합니다.

```bash
sudo tar xf /opt/apache-tomcat-8.5.73.tar.gz -C /opt/
```

![image-20211126215703288](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/image-20211126215703288.png)

> 아카이브를 추출 했습니다.



![image-20211126215741619](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/image-20211126215741619.png)

> 제대로 압축이 풀린것을 확인 할 수 있습니다.

####  tomcat 9 설치

tomcat 9를 사용하는 분은 조금 더 쉽게 설치할 수 있습니다.

```xml
sudo apt install tomcat9
```

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-16378983907716.png)



위의 명령어를 입력 하고, 14.9MB의 용량이 사용된다는 안내에 y를 입력해 동의 하면

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-16378983907717.png)

금방 톰캣 설치가 완료 됩니다.

이제 아래의 명령어를 입력 하면,

```xml
sudo systemctl status tomcat9
```



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-16378983907718.png)



톰캣 서버가 잘 실행 되어 있는 것도 확인 할 수 있습니다.

curl로 접속을 해 봅니다.

```xml
curl localhost:8080
```



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-16378983907719.png)



서버가 잘 켜있는 걸 확인 했습니다. 외부에서 접속을 해 봐야 겠습니다.

### 외부 접속 허용



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077110.png)

VM 의 ip를 확인 해서,

```xml
http://132.226.232.81:8080
```

에 접속을 하려고 하는데, 응답이 계속 펜딩 상태 입니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077111.png)



방화화벽 설정을 위해 ufw 를 설치합니다

```xml
sudo apt install ufw
```



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077112.png)

금방 설치 됩니다.

아래 명령어를 입력해 8080 포트의 방화벽을 해제 합니다.

```xml
sudo ufw allow 8080/tcp
```



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077213.png)



Rules updated가 됩니다. 80 port도 열겠습니다.

```xml
sudo ufw allow 80/tcp
```

그래도 접속이 안되길래 ping을 쏴보니 애초에 연결이 안됩니다. 서버 보안 설정을 확인 해 보겠습니다.

Primary VNIC 의 subnet 설정에 들어갑니다.



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077214.png)

>  subnet : public Subnet .. 을 클릭 하면

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077215.png)

> 보안설정이 한개 보입니다. Default Security list를 클릭 합니다.



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077216.png)

> Add ingress Rules를 눌러 수신 규칙을 추가 해 줍니다. 사실 80과 443은 이미 추가가 해두었네요.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077217.png)



모든 규칙에 대해 오픈 하겠습니다. Add Ingress Rules를 눌러 저장합니다.

이후 ping을 쏴보면

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077218.png)



이제 응답이 잘 넘어옵니다! 응답 속도도 굉장히 괜찮네요.

netstat 명령어를 이용해서 포트를 확인 하려는데 에러가 발생합니다.

 netstat: command not found

이때는 net-tools 패키지를 설치 해야 합니다.

```xml
sudo apt install net-tools
```

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077219.png)



금방 완료가 됩니다.

```xml
netstat -atn
```

을 입력해서 port listen을 하고 있는지 확인을 해 봅니다.



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077220.png)



8080 port를 잘 LISTEN 중 입니다.

 iptables를 확인 해 봅니다. 규칙에 80 포트를 여는 내용이 없습니다. 하나 하나 설정 할 수 있는 분들은 설정을 변경하면 되구요

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077221.png)



아래 명령어로는 iptables 규칙을 초기화 할 수 있습니다.

```xml
sudo iptables -F
```

저는 iptables의 규칙을 초기화 해서 해결 하겠습니다.

보통 여기에서 모두 해결 됩니다.

이제 server.xml 파일을 편집 합니다. 저는 vim 에디터가 아직 없어서 vim 에디터도 설치를 먼저 합니다.

```xml
sudo apt-get install vim
```

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077222.png)



server.xml 을 편집 해 보겠습니다.. tomcat 을 sudo 권한으로만 접근하려니 너무 귀찮네요. 

```xml
sudo vi server.xml
```



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077323.png)



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077324.png)

vim 에디터로 server.xml을 편집합니다. 저는 포트만 간단하게 80 으로 변경합니다.



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077325.png)



wq를 입력해서 저장 합니다.

```xml
sudo systemctl stop tomcat9
```

이제 톰캣 서버를 종료 한 뒤에.

 

다시 톰캣을 실행시켰습니다.

```xml
sudo systemctl start tomcat9
```

 

이제 외부 ip로 접속을 해 보겠습니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077326.png)

> 이제 잘 작동합니다... 아 정말 간단하게 작성 했지만 방화벽 때문에 멀고 먼 길 힘들었습니다.

 

## Tomcat9 -> 8로 변경

Tomcat 8 버전으로 배포 하기 위해 테스트를 위해 설치& 실행했던 tomcat9 를 종료하고

```xml
sudo systemctl stop tomcat9
```

혹시 삭제하고 싶다면 삭제도 합니다

```xml
$ sudo apt-get remove tomcat9
```

### Tomcat8 실행

tomcat 8.5 버전을 실행하기 위해 bin 폴더로 들어가려는데 권한이 없습니다.

`-bash: cd: bin: Permission denied `에러가 발생합니다.

![image-20211126220242125](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/image-20211126220242125.png)



sudo -i 명령어로 쉽게 root 권한을 얻을 수 있습니다.



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077328.png)

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077329.png)





![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077330.png)



그대로 톰캣을 실행 합니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077331.png)

귀여운 고양이가 반겨 줍니다! 일단 위에서 했던 대로 서버를 종료 하고 port 를 80으로 변경합니다.

```xml
/shutdown.sh
```

서버 종료 명령어 입니다.



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077432.png)



 



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077433.png)



vi server.xml 에서 아까처럼 port를 변경 해 줍니다.



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077325.png)



이후 서버를 다시 켜면



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077434.png)



tomcat 8.5 버전에서 80port 로의 배포 준비가 완료 되었습니다.

 

## SSH로 파일 전송 후 war 파일 배포

### scp

이제 war파일을 한번 배포 해 보겠습니다.

ssh로 간단하게 파일을 전송 할 수 있습니다.scp 명령어를 사용 하면 되며 기본적인 사용법은 아래와 같습니다. 

1) 원격 서버에서 로컬로 파일 받아오기 명령

```bash
scp [옵션] [계정명@ip주소]:[원본 경로 혹은 파일] [저장할 경로]
```

2) 로컬에서 원격 서버로 파일 전송 명령

```bash
 scp [옵션] [원본 경로 혹은 파일] [계정명@ip주소]:[전송할 경로]
```

 

옵션으로는 -P ( 기본포트 22가 아닐 경우 포트 번호 지정), 

-p (원본 파일 수정/삭제 권한 유지),

-r ( 하위 폴더 및 파일 모두 복사) 가 있습니다. 

제 mac의 바탕화면에 있는 /Users/shane/Desktop/gaia.war 파일을 원격서버의 /opt/apache-tomcat-8.5.73/webapps

로 전송하겠습니다.

```bash
scp /Users/shane/Desktop/gaia.war ubuntu@146.56.191.188:/opt/apache-tomcat-8.5.73/webapps/ROOT.war
```

![image-20211126220808352](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/image-20211126220808352.png)

뭐하나 쉽게 되는게 없군요.. 이번엔 Permission denied (publickey). 에러가 뜹니다.

해당 폴더에 접근 할 수 있는 권한이 없기 때문입니다.

### 권한설정

root 권한으로 /opt 폴더의 권한을 777로 변경하겠습니다.

```xml
chmod 777 /opt
```

이후 ls-l로 확인을 해 보면, opt 폴더의 권한이 drwxrwxrwx 로 변해 있는 것을 확인 하실 수 있습니다.

하위에 있는 모든 폴더와 파일까지 한번에 권한 변경을 하려면

```xml
 chmod -R 777 폴더명
```

형식으로 하면 한번에 하실 수 있습니다.



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077436.png)



이제 로컬에서 scp 명령어를 다시 입력하면

```xml
scp /Users/shane/Desktop/gaia.war ubuntu@146.56.191.188:/opt/apache-tomcat-8.5.73/webapps/ROOT.war
```

![image-20211126221211606](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/image-20211126221211606.png)

전송이 시작됩니다! 

![image-20211126221235521](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/image-20211126221235521.png)

의도한 폴더에 ROOT.war 파일을 집어 넣었습니다.

서버가 켜 있다면 톰캣을 새로 실행 하지 않아도 자동으로 재시작 됩니다.

이제 해당 ip에 잘 배포가 되었는지 주소를 입력해서 접속 해 보겠습니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077441.png)

길고긴 여정의 끝 입니다... 

## 로그 확인

마지막으로 실행 중인 tomcat의 로그를 실시간으로 확인 하고 싶다면, logs 폴더에서 

```xml
tail -f catalina.out
```

명령어를 입력하면 실시간으로 톰캣 에러를 점검 하실 수 있습니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud2-tomcat.assets/img-163789839077442.png)

이상입니다. 정말 수고하셨습니다! 위에 보이는 DB 문제는 길이 글어져 바로 다음글에서 해결합니다.

https://shanepark.tistory.com/172
# SSH를 이용한 Proxy, Dynamic Port Forwarding (SOCKS) 

## Intro

Dynamic Port Forwarding 을 이용하면 ssh client의 로컬에 SOCKS 프록시 서버로 동작하는 소켓을 만들 수 있습니다. 

클라이언트가 해당 포트에 접속하면, 그 연결은 리모트(ssh server) 머신으로 포워딩 되며, 목적지의 dynamic port로 전달 됩니다.

이 때, SOCKS proxy를 사용하는 모든 어플리케이션은 해당 SSH 서버에 접속되며, 서버는 모든 트래픽을 실제 목적지로 전달 합니다.

Linux, macOS 등 Unix 시스템에서는 아래와 같은 방법으로 dynamic port forwarding(SOCKS)을 생성 할 수 있습니다.

```bash
ssh -D [로컬아이피:]로컬포트 [USER아이디@]SSH서버
```

## 실습	

바로 실습 해 보겠습니다. 아래의 주소의 curl을 보내면 외부 아이피 주소를 알아 낼 수 있는데요

```bash
curl ifconfig.me	
```

일단 해당 명령을 보낸 뒤에, 본인의 외부 아이피 주소를 기억 해 주세요.

그 다음에, Dynamic Port Forwarding 을 위해 ssh 서버에 -D 옵션으로 접속 합니다. 저는 gaia 프로젝트를 실행 하고 있는 Oracle Cloud 상의 인스턴스에 접속 해 보겠습니다.

`~/.ssh/config` 에 미리 접속 정보를 저장 해 두어서 ssh gaia 만으로 접속 할 수 있습니다.

```
Host gaia
	HostName [ip address]
	Port 22
	User shane
```

동적 포트포워딩

```bash
ssh -D localhost:9999 gaia
```

![image-20211028224844246](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/proxy.assets/image-20211028224844246.png)

> 이렇게만 하면 연결은 끝 입니다. 해당 터미널 창은 닫지 말고 연결을 유지해주세요.

연결을 했으니 프록시 연결을 해서 정말 프록시 서버를 통해 접속이 되는지를 확인 해 보겠습니다.

아까 지정한 포트를 이용해서 socks5 프록시를 사용해 접속 합니다.

```bash
curl --socks5-hostname localhost:9999 ifconfig.me
```

![image-20211028225053051](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/proxy.assets/image-20211028225053051.png)그 결과 `curl ifconfig.me` 를 했을 때와는 다른 아이피 주소를 받는 것을 확인 할 수 있습니다. sock5를 통해 프록시 연결이 잘 수립 되었습니다.

## 다양한 프록시 접속 방법

SSH 동적 포워딩을 설명해두었따면, SOCKS 프록시를 설정해 요청이 프록시 서버를 거치도록 할 수 있습니다. 이를 활용하는 몇가지 방법을 소개해드리겠습니다.

### 1. 브라우저를 이용한 프록시 접속

Firefox 브라우저를 켜고 Settings 에 들어갑니다.

![image-20211028225445080](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/proxy.assets/image-20211028225445080.png)

​	

그 다음 아래로 스크롤을 쭉 내리면 Network Settings 가 있습니다.

![image-20211028225556298](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/proxy.assets/image-20211028225556298.png)

Settings... 를 클릭합니다.

​	

![image-20211028225639600](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/proxy.assets/image-20211028225639600.png)

Manual proxy configuration 에서 SOCKS Host에 localhost를 넣고, Port에는 아까 기입한 9999를, SOCKS v5 체크를 하고 OK 를 클릭 합니다.

이제 브라우저에서 ifconfig.me 에 접속 해 봅니다.		

![image-20211028225750512](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/proxy.assets/image-20211028225750512.png)

프록시 서버의 ip 주소가 나옵니다. 프록시 설정이 잘 적용 되었네요.

![image-20211028225832204](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/proxy.assets/image-20211028225832204.png)프록시 설정을 끄고 다시 새로고침 해 보면 다시 원래의 아이피주소가 나옵니다.

### 2. OS 설정

![image-20211028230104006](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/proxy.assets/image-20211028230104006.png)

Chrome은 Settings > Advanced > System 에서 proxy settings 옵션이 있긴 한데 그걸 누르면	

![image-20211028230137602](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/proxy.assets/image-20211028230137602.png)

기본 OS의 프록시 설정으로 연결되더라고요.. 물론 여기에서도 설정 할 수 있습니다.

![image-20211028230316277](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/proxy.assets/image-20211028230316277.png)

SOCKS Proxy를 찾아서, localhost:9999를 넣고 OK 버튼 누르고 Apply 를 하면 됩니다. 아까 파이어폭스에서는 브라우저에만 프록시 적용이 되었지만 이렇게 설정을 한다면 모든 인터넷 연결에 적용이 됩니다.

이 상태에서는 Safari 와 Chrome 모두 ifconfig를 해 보았을 때 프록시 서버의 아이피 주소가 나왔습니다.

### 3. FoxyProxy 플러그인 사용해 특정 사이트에만 적용

하지만 대부분의 경우에는 원하는 특정 사이트에 접속할때만 프록시 설정이 필요합니다. 

이때는 잘 안쓰는 브라우저를 해당 사이트 접속 전용으로 프록시 설정을 해두고 사용하곤 했었는데, 사실 플러그인을 사용하면 간단합니다. 저는 주로 `FoxyProxy`를 사용합니다. 

Chrome을 예로 들면 https://chromewebstore.google.com/detail/foxyproxy/gcknhkkoolaabfmlnjonogaaifnjlfnp?pli=1 에서 설치가 가능합니다.

`Add to Chrome`을 눌러 플러그인을 설치합니다.

![image-20231222133708337](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/proxy.assets/image-20231222133708337.png)

설치 후에는 아래 보이는 것 처럼 플러그인이 생기는데요,

![image-20231222133846869](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/proxy.assets/image-20231222133846869.png)

하단의 Options 를 클릭합니다. 그러면 아래와 같이 옵션 화면이 나옵니다

![image-20231222134838274](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/proxy.assets/image-20231222134838274.png)

`Proxy DNS`가 체크된 것을 확인합니다. Firefox Only 라고 써있지만, Chrome에서도 잘 동작 합니다.

두번째 탭인 Proxies를 클릭 합니다.

![image-20231222135026085](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/proxy.assets/image-20231222135026085.png)

> 이제 여기에서 원하는 패턴을 추가하면 되는데요. 저는 https://www.findip.kr/ 를 대상으로 추가하겠습니다.

Title에는 원하는 타이틀을 지정하고, Type은 `SOCKS5`, Hostname에는 `localhost` 그리고 Port 에는 아까 지정한 `9999`를 설정 합니다.

그리고 아래의 `Add` 버튼을 눌러 패턴을 추가하는데요. `*://www.findip.kr/` 라고 패턴을 설정하면 해당 URL로 들어가는 모든 요청은 해당 SOCKS5 프록시를 통하게 됩니다.

![image-20231222135237226](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/proxy.assets/image-20231222135237226.png)

> 추가 한 이후에는, 플러그인에서 Proxy by Patterns를 선택해줍니다. 만약 맨 아래의 방금 추가한 설정 자체를 선택하면, 사이트에 상관 없이 해당 설정이 모두 적용되기때문에 패턴에 따르길 원한다면 반드시 `Proxy by Patterns`를 선택해야합니다.

설정 이후 테스트해보면 `https://ifconfig.me/` 등의 사이트에서는 원래의 아이피가 나오지만 `https://www.findip.kr/` 에 접속할때는 프록시 설정한 아이피주소가 나오는 것을 확인 할 수 있습니다. 설정한 사이트만 프록시를 통해 접속이 된 것 입니다.

이상입니다.

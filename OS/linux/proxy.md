# SSH를 이용한 Proxy, Dynamic Port Forwarding (SOCKS) 

​	

Dynamic Port Forwarding 을 이용하면 ssh client의 로컬에 SOCKS 프록시 서버로 동작하는 소켓을 만들 수 있습니다. 

클라이언트가 해당 포트에 접속하면, 그 연결은 리모트(ssh server) 머신으로 포워딩 되며, 목적지의 dynamic port로 전달 됩니다.

이 때, SOCKS proxy를 사용하는 모든 어플리케이션은 SSH 서버에 접속되며, 서버는 모든 트래픽을 실제 목적지로 전달 합니다.

Linux, macOS 등 Unix 시스템에서는 아래와 같은 방법으로 dynamic port forwarding(SOCKS)을 생성 할 수 있습니다.

```bash
ssh -D [로컬아이피:]로컬포트 [USER아이디@]SSH서버
```

​	

바로 실습 해 보겠습니다. 아래의 주소의 curl을 보내면 외부 아이피 주소를 알아 낼 수 있는데요

```bash
curl ifconfig.me
```

​	

일단 해당 명령을 보낸 뒤에, 본인의 외부 아이피 주소를 기억 해 주세요.

이제 Dynamic Port Forwarding 을 위해 ssh 서버에 -D 옵션으로 접속 합니다. 저는 gaia.best 프로젝트를 실행 하고 있는 Oracle Cloud 상의 인스턴스에 접속 해 보겠습니다.

`~/.ssh/config` 에 미리 접속 정보를 저장 해 두어서 ssh gaia 만으로 접속 할 수 있습니다.

```
# Home Server
Host gaia
	HostName [ip address]
	Port 22
	User shane
	
```

​	

```bash
ssh -D localhost:9999 gaia
```

![image-20211028224844246](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/proxy.assets/image-20211028224844246.png)

> 이렇게만 하면 연결은 끝 입니다. 해당 터미널 창은 닫지 말고 연결을 유지해주세요.

​		

연결을 했으니 프록시 연결을 해서 정말 프록시 서버를 통해 접속이 되는지를 확인 해 보겠습니다.

아까 지정한 포트를 이용해서 socks5 프록시를 사용해 접속 합니다.

```bash
curl --socks5-hostname localhost:9999 ifconfig.me
```

​	

![image-20211028225053051](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/proxy.assets/image-20211028225053051.png)	그 결과 `curl ifconfig.me` 를 했을 때와는 다른 아이피 주소를 받는 것을 확인 할 수 있습니다. sock5를 통해 프록시 연결이 잘 되었습니다.

​	

## 브라우저를 이용한 프록시 접속

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

​		

![image-20211028225750512](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/proxy.assets/image-20211028225750512.png)

프록시 서버의 ip 주소가 나옵니다. 프록시 설정이 잘 적용 되었네요.

​		

![image-20211028225832204](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/proxy.assets/image-20211028225832204.png)프록시 설정을 끄고 다시 새로고침 해 보면 아이피가 바로 바뀝니다.

​	

## Chrome

![image-20211028230104006](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/proxy.assets/image-20211028230104006.png)

Chrome은 Settings > Advanced > System 에서 proxy settings 옵션이 있긴 한데 그걸 누르면

​	

![image-20211028230137602](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/proxy.assets/image-20211028230137602.png)

기본 OS의 프록시 설정으로 연결되더라고요.. 물론 여기에서도 설정 할 수 있습니다.

​	

![image-20211028230316277](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/proxy.assets/image-20211028230316277.png)

SOCKS Proxy를 찾아서, localhost:9999를 넣고 OK 버튼 누르고 Apply 를 하면 됩니다. 아까 fireFox 에서는 브라우저에만 프록시 적용이 되었지만 이렇게 설정을 한다면 모든 인터넷 연결에 적용이 됩니다.

이 상태에서는 Safari 와 Chrome 모두 ifconfig를 해 보았을 때 프록시 서버의 아이피 주소가 나왔습니다.

이상으로 마치겠습니다.	


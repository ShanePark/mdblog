# SSH를 이용한 Proxy. Dynamic Port Forwarding (SOCKS) 

​	

Dynamic Port Forwarding 을 이용하면 ssh client의 로컬에 SOCKS 프록시 서버로 동작하는 소켓을 만들 수 있습니다. 

클라이언트가 해당 포트에 접속하면, 그 연결은 리모트(ssh server) 머신으로 포워딩 되며, 목적지의 dynamic port로 전달 됩니다.

이 때, SOCKS proxy를 사용하는 모든 어플리케이션은 SSH 서버에 접속되며, 서버는 모든 트래픽을 실제 목적지로 전달 합니다.

Linux, macOS 등 Unix 시스템에서는 아래와 같은 방법으로 dynamic port forwarding(SOCKS)을 생성 할 수 있습니다.

```zsh
ssh -D [로컬아이피:]로컬포트 [USER아이디@]SSH서버
```

​	

바로 실습 해 보겠습니다. 아래의 주소의 curl을 보내면 외부 아이피 주소를 알아 낼 수 있는데요

```zsh
curl ifconfig.me
```

​	

일단 해당 명령을 보낸 뒤에, 본인의 외부 아이피 주소를 기억 해 주세요.

이제 Dynamic Port Forwarding 을 위해 ssh 서버에 -D 옵션으로 접속 합니다. 저는 asus 라는 이름으로 저희 집에서 동작 중인 서버에 접속 해 보겠습니다. 

`~/.ssh/config` 에 미리 접속 정보를 저장 해 두어서 ssh asus 만으로 접속 할 수 있습니다.

```
# Home Server
Host asus
	HostName [ip address]
	Port 22
	User shane
	
```





​	![image-20211027111026223](/home/shane/Documents/git/mdblog/OS/linux/proxy.assets/image-20211027111026223.png)
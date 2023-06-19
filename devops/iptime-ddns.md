# 공유기 DDNS 설정으로 아이피 변경 대비하기 (ipTIME)

## Intro

집에서 안쓰는 노트북에 우분투서버를 설치해 홈 서버로 운영한지 이제 2년이 넘어갑니다. 

처음에는 클라우드에 비용을 투자하기에는 부담스럽고, 개발한걸 서버에 배포는 해보고 싶은 마음에 전기요금이 적게 드는 노트북으로 절충을 했는데, 필요에 맞춰 스토리지와 램도 증설을 하다보니 이제는 제법 쓸만 해 졌습니다.

무료 클라우드를 몇개 함께 사용하고는 있지만, 이처럼 넉넉한 메모리 및 저장공간에 그래픽카드까지 갖춘 고사양 컴퓨팅 환경을 약간의 전기요금만으로 사용할 수 있다는건 굉장한 이점이 있습니다. 

하지만 홈 서버의 장점이 명확한 만큼 운영할때의 불편함 또한 여러가지가 있습니다. 그 중 가장 치명적인것 중 하나가 아마 유동 아이피 문제가 아닐까 생각됩니다. 

오래전에는 가정용 인터넷은 IP주소가 자주 변경되곤 했었습니다. 사실 요즘은 아이피주소가 좀처럼 바뀌지 않아 지금 서버는 2년넘게 사용하며 아이피주소가 바뀐 적은 단 한번도 없었습니다. 그래도 공유기를 껐다 켠다거나 특별히 문제가 발생했을 때 혹시 변경될때를 대비해서 DDNS 설정을 해 두려고 합니다.

## DDNS

DDNS는 Dynamic DNS의 약자로서, public IP 주소의 변경을 감지하고 자동으로 업데이트 해주는 서비스 입니다. 대체로 공유기에서 설정 할 수 있으며 제가 사용하고 있는 ipTIME 공유기의 공유에는 무료로 해당 서비스를 제공합니다.

### 공유기 관리 페이지 접속

일단 브라우저에 `192.168.0.1` 을 입력 해서 공유기 설정 화면에 진입합니다.

![image-20230619212110028](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/iptime-ddns.assets/1.webp)

그러면 위에 보이는 것 처럼 로그인 화면이 나오는데요, 초기 아이디와 비밀번호는 각각 `admin`/`admin` 인데, 반드시 변경해주셔야 합니다.

로그인 하고 나서

### DDNS 설정

`Advanced Setup` > `Utility` > `DDNS` 설정에 가면 DDNS 를 설정 할 수 있습니다. 

![image-20230619212229152](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/iptime-ddns.assets/2.webp)

> 참고로 대부분 아시겠지만, 홈 서버로 사용하려면 `NAT/Routing` 에서 반드시 필요한 포트에 대해 포트포워딩도 해주셔야 합니다.

여기에 호스트 이름 및 User ID를 입력하게끔 되어 있습니다. Host Name에는 원하는 호스트명을 입력 하고, User ID 에는 이메일 주소를 입력하라고 합니다. 입력 후, `+Register` 버튼을 클릭 해 줍니다.

그러면 간단하게 등록이 됩니다.

![image-20230619212659059](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/iptime-ddns.assets/3.webp)

### 확인

정상적으로 잘 되었는지 ping을 쏴봅니다.

![image-20230619212803477](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/iptime-ddns.assets/4.webp)

> 해당 도메인을 통해 정확히 현재 아이피 주소에 찾아 들어갑니다.

SSH 접속도 문제 없습니다.

```bash
ssh shane@[Host Name].iptime.org
```

![image-20230619213058397](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/iptime-ddns.assets/5.webp)

조만간 집에 메시 아이피를 구축하며 공유기를 변경하려고 했는데, 그때도 DDNS 설정만 한번 해 두면 이후 아이피 변경에 대한 걱정은 하지 않아도 되겠습니다.

### 외부에서 공유기 관리

`Advanced Setup` > `Firewall` > `Mgmt Access List`에서 `Remote Mgmt port`를 설정 해 둔다면 웹 브라우저에 `http://Host-Name.iptime.org:59999` 이런식으로 지금 보고있는 공유기 관리 화면에 접근할 수도 있습니다.

보통은 쓸 일이 많지 않지만 WOL(Wake On Lan) 기능을 쓸 때는 정말 유용하게 사용 할 수 있습니다. 그게 아니라면 기능을 꺼두시길 권장합니다.

![image-20230619213813709](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/iptime-ddns.assets/6.webp)

이상입니다.
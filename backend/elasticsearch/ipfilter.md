# [도커 방화벽 설정] 엘라스틱서치 특정 IP 클라이언트 접속만 허용하기

## Intro

국비학원 최종 프로젝트때 잘 알지도 못하면서 검색 엔진을 도입 했었습니다. 물론 지금도 검색엔진에 대해 잘 모르다보니, 회사에서 진행중인 프로젝트에서 검색엔진을 활용하는 기능을 추가 할때면 SolrJ API를 이리저리 뒤져가며 낑낑대며 간신히 병목이 생기지 않을 정도만 하고 있습니다.

잘 알지도 못하는 검색엔진 도입의 대가는 실로 커서, 장점이라면 그때 당시 도와줄 사람이 없어 처음으로 공식 문서들을 뒤져가며 스스로 무언가를 바닥부터 만들어 내며 맨땅에서도 뿌리를 내릴 수 있는 야생성을 기르고 어떻게든 하면 된다는 자신감을 얻었습니다.

반면 단점도 만만치 않은데 일단 국비학원을 졸업한지 1년 4개월쯤 지난 지금도 그때 당시의 프로젝트를 여전히 띄워 두고 있습니다. 처음엔 집에 남는 노트북으로 윈도우 환경에 WAS도, DB도, 엘라스틱 서치도 다 띄워놨었는데 일반 윈도우로 서버를 운영하는 한계와 불편함을 느껴 리눅스 서버로 전환했습니다. 전부 클라우드로 돌려 집에 있는 노트북을 끄고 싶었으나 클라우드에서 무료로 제공하는 인스턴스들은 램이 아무리 많아야 1GB로 굉장히 적은데, 엘라스틱 서치는 띄웠다 하면 별로 색인된것도 없는데 기본 4~5GB 는 거뜬하게 잡아 먹어서 무료 클라우드로는 어림도 없었습니다.

그래서 지금은 왠만한건 다 여러개로 분산된 무료 클라우드로 옮겼으나 엘라스틱서치 하나를 위해 집에 있는 컴퓨터가 안식을 찾지 못하고 있습니다.

## IP 제한의 필요

문제는, 엘라스틱 서치가 띄워져 있는 서버와 WAS 서버를 각기 분리시키다 보니 엘라스틱서치 서버의 외부 접속을 허용 할 수 밖에 없었는데, 이게 보안상 아주 취약합니다. 어느 누구든 자기 맘껏 자료를 편집 할 수 있습니다.

별것도 없고 중요한 자료도 없는 작은 프로젝트지만, 국비 학원들 다니며 최종 프로젝트를 진행하려고 주제를 찾거나 이미 진행되었던 레퍼런스들을 찾는 분들이 꾸준히 있다보니 서버는 유지하고 싶고, 그 기능을 유지하기 위해서는 데이터를 지킬 필요가 있습니다.

혹시나 싶어 엘라스틱 컨테이너의 로그를 확인 해 보니 알 수 없는 해외 ip 에서의 알 수 없는 접속이 꽤나 많이 찍혀 있었습니다. 이건 실제로 무엇이든 서버를 띄워 보기 전에는 몰랐던 정보인데 뭐든 인터넷 세상에 포트를 열고 나간 아기 서버는 세계 각지에서 와서 여기 저기 어슬렁거리며 툭툭 치고 지나가는 깡패들들 정말 많이 만나게 됩니다. 

![download](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/elasticsearch/ipfilter.assets/download.jpeg)

> 여긴 어디고 나는 누구인가..
>
> created by DALL·E

그래서 IP 제한을 하기로 했습니다. 그러나 그 과정에서 엘라스틱서치의 IP 제한보다는 방화벽 설정으로 해결하기로 계획을 변경했고, 그 중 도커의 예상치 못했던 방화벽 우선순위로 고생을 많이 하기도 했습니다.

이리 저리 문제해결을 위해 부딪치는 과정을 여과없이 담았기 때문에 무작정 따라하는 것 보다는, 같은 문제를 해결중이시라면 한번 흐름을 쭉 읽고 나서 어떻게 할지 결정 하시는게 좋을 것 같습니다.

## IP 제한하기

### 테스트 준비

IP 필터링에 대한 정보는 엘라스틱 서치 공식 문서를 확인 했습니다.

> https://www.elastic.co/guide/en/elasticsearch/reference/current/ip-filtering.html#ip-filtering

일단 제한에 앞서 테스트를 할 수 있도록 준비를 해 두겠습니다.

외부접속을 여는 방법은 `elasticsearch.yml` 파일의 network.host 를 `localhost`가 아닌 `0.0.0.0` 으로 변경 하는 건데요 

저는 도커 컨테이너로 띄워 놓았기 때문에 컨테이너 내부로 들어가 파일을 확인 해 보면

![image-20221028215307473](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/elasticsearch/ipfilter.assets/image-20221028215307473.png)

> 위에 보이는 것 처럼 network.host: 0.0.0.0 으로 되어 있습니다.

이처럼 외부 접속이 열려 있다면 어디서든 아래와 같이 요청을 하면

```bash
curl {엘라스틱서치 IP주소}:9200
```

![image-20221028215905268](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/elasticsearch/ipfilter.assets/image-20221028215905268.png)

위와 같은 응답을 받게 됩니다.

이제 위의 요청을 엘라스틱 서치를 사용할 서버에서 보낼때는 응답이 가능하고, 그 외에서는 요청을 거부하도록 설정 하면 되겠습니다.

### 제한 설정

![image-20221028220036603](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/elasticsearch/ipfilter.assets/image-20221028220036603.png)

> https://www.elastic.co/guide/en/elasticsearch/reference/current/ip-filtering.html#ip-filtering

엘라스틱 서치 공식 문서에 설정 방법이 나와 있습니다. 

**elasticsearch.yml** 파일에 해당 설정을 추가 해 주라고 합니다.

```
xpack.security.http.filter.allow: "허용ip"
xpack.security.http.filter.deny: _all
xpack.security.http.filter.enabled: true
```

그런데 위와 같이 추가 해도 여전히 접속이 문제 없이 되더라고요

그래서 고민을 하다가 그냥 방화벽에서 처리를 해야 겠다는 생각을 했습니다.

## 방화벽 설정

### Firewalld

```bash
netstat -tnlp
```

일단 리스닝중인 포트들을 먼저 확인 해서 방화벽을 어떻게 구성할 지 계획을 하고

#### 설정과정

```bash
# firewalld 설치
sudo apt install firewalld -y

# 1521(Oracle) 포트 열기
sudo firewall-cmd --zone=public --permanent --add-port=1521/tcp

# 삭제
# sudo firewall-cmd --zone=public --permanent --remove-port=1521/tcp

# firewalld 재시작 
sudo firewall-cmd --reload
# firewalld 상태 확인
sudo firewall-cmd --list-all

# elastic-access 라는 이름의 zone 추가 
sudo firewall-cmd --new-zone=elastic-access --permanent
sudo firewall-cmd --reload
sudo firewall-cmd --get-zones
# 해당 zone에 적용할 IP 주소와 허용할 포트 추가
sudo firewall-cmd --zone=elastic-access --add-source=146.56.191.188 --permanent
sudo firewall-cmd --zone=elastic-access --add-port=9200/tcp  --permanent
sudo firewall-cmd --zone=elastic-access --add-port=1521/tcp  --permanent # Zone 등록시, 해당 zone에서 처리할 경우에는 public 설정이 먹히지 않기 때문에 public에서 추가 했더라도 별개로 추가 해 줘야 함.

# 재시작 후 해당 zone의 설정 확인
sudo firewall-cmd --reload
sudo firewall-cmd --zone=elastic-access --list-all

```

사실 처음에는 ufw 로 설정을 이리 저리 해봤는데 영 원하는 대로 동작 하지가 않았습니다. 

그래서 ufw를 삭제하고 firewalld를 설치해 방화벽 설정을 했더니 이번에는 완벽히 원하는 대로 작동이 잘 되었습니다.

이후 방화벽 설정이 잘 되었는지를 확인 하려면, 접속을 허용한 서버와 허용하지 않은 서버 양쪽에서 elastic search 요청을 보내보거나, `telnet 아이피 9200` 명령으로 연결이 되는지 확인을 해 보면 됩니다.

![image-20221029003419566](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/elasticsearch/ipfilter.assets/image-20221029003419566.png)

> 방화벽 설정 후, 허용을 한 서버에서만 해당 요청에 대한 처리를 해 주고, 그렇지 않은 경우에는 refused가 됩니다.

### 그런데 Docker 는..

**재시작** 후에도 문제 없이 적용되는지 확인을 위해 재시작 후  `sudo firewall-cmd --list-all-zones` 로 모든 존의 방화벽 설정을 확인 해 보는데 `docker`라는 zone이 임의로 생성되어 active 상태로 되어 아까 설정한 `public`이 사용되지 않고 있습니다. 아마 도커 사용자들이 방화벽 설정때문에 불편함을 겪는 상황을 방지하기 위해서인지.. 

```bash
firewall-cmd --get-active-zone
```

![image-20221029010021074](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/elasticsearch/ipfilter.assets/image-20221029010021074.png)

> default-zone은 public으로 설정이 잘 되어 있는 상태 임에도 Active zone에 public이 빠지고 docker 가 들어가 있는 상태.

도커 공식 문서를 찾아 보니 아래와 같이 작성 되어 있었습니다.

>  Usually the firewall rules Docker creates will have precedent because they are inserted before the rules managed by your user-friendly firewall management tool.
>
> https://docs.docker.com/network/iptables/

다 끝났다고 생각했는데 의외의 복병이 있었던거죠. 도커는 기본적으로 서비스를 구동 하면, 기존의 방화벽 설정이 있더라도 기본적으로 다 허용 하도록 iptables에 별도 체인을 생성합니다. 이걸 해결하고자 자칫하면 iptables 옵션을 false 로 변경 해서 해결하려고 할 수 있지만 문서에서는 좋은 방법이 아니라고 하며 권장하지 않습니다.

![image-20221029135829669](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/elasticsearch/ipfilter.assets/image-20221029135829669.png)

일단 firewalld로 해결이 안되겠다 싶어 다시 제거해버렸습니다.

```bash
sudo apt remove firewalld
```

그러고 나서 Docker가 추가한  iptables 룰을 확인 해 보았습니다.

```bash
sudo iptables -L
```

![image-20221029144958008](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/elasticsearch/ipfilter.assets/image-20221029144958008.png)

Chain FORWARD를 보면 DOCKER-USER 가 가장 먼저 등록 되어 있고, 아래쪽에 보면 DOCKER 체인이 등록 되어 있습니다.

DOCKER 체인에는 도커 컨테이너들의 포트들이 보입니다.

### Iptables 설정

우선 DOCKER_USER에서 9200 포트로 들어오는 모든 패킷을 드랍 하도록 설정을 추가 해 보았습니다.

```bash
sudo iptables -I DOCKER-USER -p tcp --dport 9200 -j DROP
```

iptables 설정은 따로 재시작 등이 필요 없이 바로 작동 합니다.

![image-20221029145911149](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/elasticsearch/ipfilter.assets/image-20221029145911149.png)

바로 확인을 해 보면 9200 포트로 요청을 보냈을 때 응답이 오지 않고 타임아웃이 발생 합니다. 

하지만 이렇게 하면, 필요한 서버에서의 요청에 대해서도 응답을 해주지 못하기 떄문에 허용할 IP에 대해 추가가 필요 합니다.

```bash
sudo iptables -I DOCKER-USER -p tcp -s 146.56.191.188 --dport 9200 -j ACCEPT
```

이렇게 적용 하고 나면 이제 등록한 아이피에서 들어오는 9200 포트의 요청은 허용을 해 줍니다.

이제 다시 iptables 설정들을 확인 해 봅니다.

```bash
sudo iptables -L
```

![image-20221029150225692](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/elasticsearch/ipfilter.assets/image-20221029150225692.png)

> DOCKER-USER에 추가한 두개의 룰이 보입니다.

하지만 또 재부팅을 하고 나서 iptables 룰을 확인 해 본다면 ..

![image-20221029150436950](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/elasticsearch/ipfilter.assets/image-20221029150436950.png)

>  아까 등록한 설정이 모두 사라져 있습니다.

iptables 설정은 재부팅 후에는 날아가기 때문인데요..

삭제된 룰들을 다시 추가 해주고

```bash
sudo iptables -I DOCKER-USER -p tcp --dport 9200 -j DROP
sudo iptables -I DOCKER-USER -p tcp -s 146.56.191.188 --dport 9200 -j ACCEPT
```

재부팅 후에도 iptables 룰이 저장이 되어 있게 하기 위해 `iptables-persistent` 라고 불리는 패키지를 설치 했습니다.

```bash
sudo apt install iptables-persistent
```

그러면 IPv4 룰 및 IPv6 룰을 저장 할 건지 물어 봅니다. Rules은 패키지 설치 중에만 자동으로 저장된다고 합니다.

![image-20221029151438943](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/elasticsearch/ipfilter.assets/image-20221029151438943.png)

YES 라고 응답을 하고 나면

```bash
ls -al /etc/iptables
```

![image-20221029151604691](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/elasticsearch/ipfilter.assets/image-20221029151604691.png)

`/etc/iptables` 경로에 두개의 룰이 생긴 것이 확인 됩니다.

나중에 변경된 iptables 룰을 저장 하려면 아래의 명령어를 직접 실행 해 주어야 합니다.

```bash
sudo iptables-save > /etc/iptables/rules.v4
sudo ip6tables-save > /etc/iptables/rules.v6
```

그런데 이 경우에는 iptables 규칙을 출력하는 `iptables-save` 명령만 sudo 권한으로 실행되고 파일 작성에는 적용이 되지 않아 권한 문제가 발생 할 수 있습니다. 이럴때는 아래처럼 실행해줍니다.

```bash
sudo sh -c "iptables-save > /etc/iptables/rules.v4"
sudo sh -c "iptables-save > /etc/iptables/rules.v6"
```

이제 다시 재부팅을 한 후에 확인을 해 봅니다.

```bash
sudo iptables -L
```

드디어 변경한 방화벽 룰도 그대로 적용 되어 있고, 그 덕분에 테스트 결과도 성공적으로 원하는 서버에서의 요청만을 처리 해 줍니다.

## 마치며

의도한대로 방화벽 설정을 마쳤습니다. 처음에는 elastic search에서 설정을 하려고 했으나 의도대로 작동하지 않아 방화벽으로 했는데, 엘라스틱에서도 애초에 필터링을 성능저하를 이유로 권장하지 않는다고 했으니 애초에 방화벽을 통해 해결하는게 맞았겠다는 생각이 듭니다.

간단하게 처리 할 수 있을 거라고 생각 했는데 예상치 못했던 Docker의 방화벽 설정으로 인해서 꽤나 어려움을 겪었습니다. 방화벽 문제는 언제 겪어도 참 골치아프긴 한데 이번에는 더 어려웠네요.

이상입니다.

**References**

- https://www.tecmint.com/open-port-for-specific-ip-address-in-firewalld/
- https://linuxwizardry.com/how-to-make-iptables-persistent-after-reboot-on-ubuntu-and-centos-system/
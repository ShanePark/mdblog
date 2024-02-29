# 구글 클라우드 무료 인스턴스(가상 서버) 만들기
## Intro

오라클 클라우드에서는 램 1GB 짜리 무료 인스턴스를 무려 2개나 준다. 그 덕에 3년간 잘 사용해왔는데 갑자기 인스턴스가 먹통이되었다.

오라클 클라우드 로그인도 안되길래 오라클 기술지원에 문의를 하니 이유는 따로 말해줄 수 없지만 계정을 terminate 시켰단다. 단순하게 톰캣 띄워놓고 개발용으로만 꾸준히 써왔을 뿐이니 특별히 약관을 위배한건 없었을텐데 카드 등록도 필요 없을때부터 시작한덕에 계속 공짜로 써왔던게 밉보였나 싶다.

![26](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/cloud/google-cloud.assets/26.webp)

한참 전부터 위 이메일같이 말도안되는 cpu 점유율을 운운하며 (램은 항상 80% 이상 사용 했으니, cpu로 핑계를 댔나. 솔직히 웹 어플리케이션 서버에 CPU 계속 뛰는 일이 있을 수가 없다) 사용안하니 끌께~라고 메일보내놓고 기습적으로 서버를 강제 종료 해대도 외부에 관제 서비스들 달아두고 버텼는데 이번에는 계정 자체를 없애버렸다. 덕분에 부랴부랴 SSL 인증서 새로 발급 받고 CI/CD 파이프라인 다 손보느라 진땀뺐다. 

본인이 운영하는 서비스에 장애가 났다며 접속이 한동안 안될 것을 사용자들에게 공지하는 것은 개발자로서 참 쑥스러운 순간이다.

그래도 비용들이지 않고 그동안 오라클덕에 잘 공부했기에 더이상 원망은 안하려 한다. 같은 기간동안 동일 스펙의 유료 클라우드를 썼으면 100만원은 족히 청구되었을 것이다. 이 일을 겪고 부랴부랴 오라클을 외의 클라우드 서비스들을 찾아보았다.

**무료** 클라우드 인스턴스를 지원하면서, 향후 유료 사용도 고려할 수 있게 일정량의 **크레딧**도 지급했으면 했고 요금폭탄의 위험에서 **안전**한 곳 위주로 비교해보았다. 본 글에서는 따로 클라우드별 비교내용을 작성하지는 않을 예정이지만, 얼마간의 고민 결과 구글 클라우드를 사용하기로 했다.

## 회원가입

구글 계정은 누구나 있을테니 Google Cloud 가입 절차는 매우 간단하다. 아래의 주소에 접속한다.

https://cloud.google.com/free

![27](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/cloud/google-cloud.assets/27.webp)

> Free Tier products 항목에서 Compute Engine 를 보면 한개의 e2-micro 인스턴스를 제공한다고 써 있음

`Get started for free` 를 클릭한다.

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/cloud/google-cloud.assets/1.webp)

Terms of Service 항목들에 동의.

90일 동안 사용할 수 있는 $300 의 무료 크레딧을 준다고 한다.

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/cloud/google-cloud.assets/2.webp)

가입할 때는 은근 입력할것들이 많다.

- 한글 이름
- 주민등록 번호 앞 6 자리 + 뒷 1자리 
- 핸드폰번호 인증
- 카드번호 인증

모든 것을 입력 하면 회원 가입에 성공한다. 기존에 사용하던 Adsense의 Payment Profile 이 있어서 좀 더 간단했을지도 모르겠는데, 입력하라는 건 다 입력해줘야 한다.

![3](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/cloud/google-cloud.assets/3.webp)

> 4개의 질문에는 답을 해도 좋고 안해도 좋다. free trial $300 크레딧이 들어왔다고 한다.

회원 가입을 마치고 `Billing` 메뉴를 확인 해 보면 해당 크레딧이 들어와있다.

![4](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/cloud/google-cloud.assets/4.webp)

> $300 이고 한화로 40만원 가량이 등록되어있다. 정확히 90일간 사용 가능하다

## 인스턴스 생성

### 생성

가입을 했으니 인스턴스를 생성해보자. 메인 메뉴로 돌아가서 `Create a VM`을 클릭한다

![5](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/cloud/google-cloud.assets/5.webp)

그러면 프로젝트를 선택하라고 하는데, 자동으로 생성된 `My First Project`에 생성하도록 하겠다.

`Enable` 버튼을 클릭해 활성화해준다.

![6](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/cloud/google-cloud.assets/6.webp)

Enable 한 뒤에는 `Create instance` 가 가능하다

![7](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/cloud/google-cloud.assets/7.webp)

> `CREATE INSTANCE`를 클릭한다

이제 인스턴스의 스펙을 정해야 한다. 이 부분이 중요하다

![8](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/cloud/google-cloud.assets/8.webp)

위 그림 처럼 처음에 기본적으로 선택되어있는 2 vCPU + 4 GB memory 스펙을 선택하면, 월 $28.65가 청구된다. 

사실 같은 스펙으로 네이버 클라우드에서 사용하면 월 69,000원이라서 여전히 경쟁력 있는 가격이라고 생각된긴 하지만, 어쨌든 처음에는 무료 인스턴스를 생성하려고 한다. 3달동안 무료 크레딧이 있는건 후에 인스턴스를 추가로 생성하여 소진하도록 하고, 우리에게 필요한건 무제한으로 무료로 사용할 수 있는 인스턴스다.

먼저 무료 사용에 대한 조건을 찾아보자

![9](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/cloud/google-cloud.assets/9.webp)

> https://cloud.google.com/free/docs/free-cloud-features?hl=ko#compute

오리건, 아이오와, 사우스캐롤라이나 중 하나의 리전을 선택하면 무료로 사용할 수 있다고 한다. 물리적으로 가장 가까운 미국 서부가 좋겠다.

![10](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/cloud/google-cloud.assets/10.webp)

- Region: **Oragon** 
- Machine configuration: **EC2**
- Machine type: e2-micro(2 vCPU, **1 core**, **1 GB** memory)

> 우측 상단을 보면 월 $7.11 의 요금이 청구된다고 되어있지만 `월 31일 x 24시간 = 744 시간` 을 모두 소진할 때 까지는 무료로 사용할 수 있다고 한다. 무료등급은 외부 IP 주소에도 비용을 부과하지 않는다고 한다.
>
> 설마 하고 비용이 부과될까 겁먹을 필요는 없다. 우리에겐 90일간 사용할 수 있는 든든한 $300의 크레딧이 있다

그리고 Boot disk 부분이 중요한데 처음에는 `New balanced persistent disk`로 되어 있다. 이 역시 은근 함정이다.

무료로 제공하는건 30GB 까지의 표준 영구 디스크다. 알맞게 변경해주자.

![11](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/cloud/google-cloud.assets/11.webp)

> Boot disk type은 `Standard persistent disk`, 사이즈는 30GB로 설정한다.
>
> OS 타입이나 버전은 각자 원하는걸 선택하자. 잘 모른다면 Ubuntu 나 CentOS를 선택하면 된다.

![12](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/cloud/google-cloud.assets/12.webp)

웹서버 용도라면 방화벽에 HTTP 및 HTTPS 도 미리 허용해준다. HTTPS만 쓸거라고 해도 SSL 인증서 할라면 결국 HTTP도 열어줘야한다.

모두 준비가 되었으면 가장 하단의 `Create` 버튼을 클릭해 인스턴스를 생성해준다.

![13](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/cloud/google-cloud.assets/13.webp)

> 인스턴스가 생성되었다.

### 요금에 대해 

![25](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/cloud/google-cloud.assets/25.webp)

>  실수로 무료 인스턴스가 아닌 비용이 청구되는 스펙의 인스턴스를 만든다고 해도 free trial 기간동안은 크레딧에서 나가고, 그 이후에도 `ACTIVATE FULL ACCOUNT`를 하지 않는 이상 비용이 청구되지는 않다고 하니 안심하고 생성해도 좋다.

## 인스턴스 접속 

### 접속

생성된 서버에서 `Connect` 에 있는 `SSH` 버튼을 클릭 하면 자동으로 접속을 수립한다.

![24](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/cloud/google-cloud.assets/24.webp)

잠시 기다리면

![14](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/cloud/google-cloud.assets/14.webp)

접속이 되었다.

재밌는건 내 컴퓨터의 `~/.ssh/id_rsa.pub` 파일이 해당 인스턴스의 `~/.ssh/authorized_keys`에 자동으로 등록되어 있다는 것 이다. `Transferring SSH Keys to the VM.`의 과정에서 크롬 브라우저가 알아서 해준 모양이다.

![16](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/cloud/google-cloud.assets/16.webp)

> 자동으로 등록된 authorized_keys

ssh 접속을 할 때는 기본적으로 등록된 공개키로만 접속이 가능하다.

그런데, 자동으로 등록되었다고 해서 그대로 외부 접속이 가능한건 아니었다. 위에 나와있듯이 `expireOn` 시간이 3분 내외로 매우 짧다. 

심지어 자동으로 Google이 등록한 공개키는 일정시간이 지나면 사라졌다. 아마 웹에서 접속할 때는 일회용으로만 임시 등록되는 모양이다.

외부키를 등록해주면 외부에서 접속이 가능하다. 이후 로컬 PC에서도 접속 해 보았다.

![17](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/cloud/google-cloud.assets/17.webp)

> 잘 접속 된 상태

하지만, 직접 추가한 키도 구글 클라우드가 금새 자동으로 삭제해댔다. 찾아보니 구글 어카운트 데몬이 백그라운드에서 계속 삭제한다고 한다.

일반적인 `~/.ssh/authorized_keys`를 수정하는 방법으로는 공개키를 등록할 수 없다.

> https://groups.google.com/g/gce-discussion/c/m6iZ1GWem8Q?pli=1

### VM 에 SSH 키 추가

키 추가는 구글 클라우드 문서에 자세히 나와있는데 아래와 같이 주의 항목에 관련 내용이 역시 기재되어 있었다.

![18](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/cloud/google-cloud.assets/18.webp)

> https://cloud.google.com/compute/docs/connect/add-ssh-keys?hl=ko 

이때는 프로젝트 메타데이터에 SSH 키를 추가하는 방법을 써야한다. 좌측 메뉴에서 쭉 내리다보면 `Metadata` 메뉴가 있다.

![19](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/cloud/google-cloud.assets/19.webp)

메타데이터 메뉴에서 `ADD METADATA`를 클릭한다

![20](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/cloud/google-cloud.assets/20.webp)

이제 여기에서는 `SSH KEYS`탭을 선택 하고 `ADD ITEM`을 클릭한다.

![21](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/cloud/google-cloud.assets/21.webp)

여기에 ssh 키를 입력 하고 저장해준다.

![22](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/cloud/google-cloud.assets/22.webp)

여기에 SSH 키를 입력할 때의 주의사항은, SSH 키 마지막의 USERNAME 부분을 접속할 **인스턴스의 유저이름**으로 적어야 한다는 것 이다.

예를 들어 내 SSH 공개키는 아래와 같은데

```
ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQCxTDELKxPG3SwgOsl3oCyUaiuZ0whQkYEhKa4djB+wxRri/e6EgEVlOFNkLf/a3PGhhyUnygnfTxQqKioEJppL1LRtvWZ1Mtp4lB68CFhlmUu51PY7y+b33bc19SuhWVuBMD4PWt5E+F4U9IR9JyQBLjegj9+2LDbfPuDyn/9ClcfXqtzeG+AbhfedhHSsoOKjUxMjfCei97umPWYy/4lCqufBSIwIA8NnUfBbGq/p0GAkiZBRPz480GYmZIVyEHBSoYmsqthvJMZqlCFNrzSDPS6ofglDvrfO1vV4nslnmpN91QsEqwLMlm3b12ksb5qXUgjk5OtRMPCV8AfJX6qZsKuvqAXoSGy+m52w9Sx4ge7IqqeSltLX9PVoxbcgP/FKjHaHp9JzLhKhu0+Gek/3RE8XHjbw4Mg6XcoRBCyOpq3fJXSuyWhONlYfFJHu2Vh8C1OL01oyjgHF37vE3Ho6gpyt+RIvptaFjT2D128lSdVth0komhUgOEJ5Varp9/U= shane@shane
```

가장 마지막의 `shane@shane` 부분을 `shanepark_dev`로 변경한 후 저장해야 접속이 가능했다. 

헷갈린다면 아래 사진을 참고하자

![23](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/cloud/google-cloud.assets/23.webp)

> https://cloud.google.com/compute/docs/connect/add-ssh-keys?hl=ko

이후에는 GCP 데몬이 삭제 하면서도 꼬박꼬박 자동으로 추가해준다.

![15](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/cloud/google-cloud.assets/15.webp)

> Added by Google 주석이 달려있다.

### 비밀번호 변경

암호는 그냥 `passwd` 를 입력 하면 기존 암호를 입력해야 해서 변경이 불가능하다. 우리는 기존 암호를 모르기때문에 sudo 권한으로 기존암호 없이 새 암호를 설정한다.

`$(whoami)` 대신 변경하고자 하는 유저네임을 직접 입력해도 되지만 번거로우니 `$(whoami)` 를 입력하자.

```bash
sudo passwd $(whoami)
```

끝

**References**

- https://cloud.google.com/compute/docs/connect/add-ssh-keys?hl=ko

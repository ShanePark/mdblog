# Mac) Oracle Cloud FreeTier 이용해 웹 어플리케이션 배포하기 1)인스턴스 만들고 접속하기.

| 관련 글 목록                                                 |
| ------------------------------------------------------------ |
| [Mac) Oracle FreeTier 이용해 웹 어플리케이션 배포하기 1)인스턴스 만들고 접속하기.](https://shanepark.tistory.com/170) |
| [Mac) Oracle FreeTier 이용해 웹 어플리케이션 배포하기 2) 톰캣 설치하기 및 배포 + 외부 접속 허용](https://shanepark.tistory.com/171) |
| [Mac) Oracle FreeTier 이용해 웹 어플리케이션 배포하기 3) 타임존 문제 해결](https://shanepark.tistory.com/172) |
| [Mac) Oracle FreeTier 이용해 웹 어플리케이션 배포하기 4) 무료 데이터 베이스 만들기 및 데이터 이관하기](https://shanepark.tistory.com/173) |
| [Mac) Oracle Cloud FreeTier 이용해 웹 어플리케이션 배포하기 5) 톰캣 도메인 연결해 배포하기](https://shanepark.tistory.com/174) |
| [Mac) Oracle Cloud FreeTier 이용해 웹 어플리케이션 배포하기 6) 오라클 전자지갑 사용해 프로젝트와 연결하기](https://shanepark.tistory.com/207) |

## Intro

안녕하세요. 모두 아시는 것 처럼, 요즘에는 클라우드를 이용해 배포하는게 대세입니다. 보통은 AWS를 많이들 사용하시는데요 한순간 방심하다가는 요금 폭탄을 맞을 수 있습니다. 무료로 사용하고 싶은데, 과금의 위험에서 벗어나고 싶은 분들에게 Oracle Cloud를 추천합니다.

Amazon의 AWS, Microsoft의 Azure, Google의 Google Cloud등이 이미 주름잡고 있는 Cloud 시장에 후발 주자로 들어가서 그런지 꽤나 파격적인 정책을 펼칩니다.

처음 Free tier에 가입하면 $300 만큼의 Credit을 지급하기도 하니 충분히 해볼 만 한 가치가 있습니다.

### 가입

https://www.oracle.com/kr/cloud/free/

가입은 위의 링크에서 하시면 됩니다. 가입은 어렵지 않으니 차근 차근 하시면 됩니다. 꼭 Free tier 를 선택하세요.

## Instance 생성 

### Create VM

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud1-instance.assets/img-20211126213240515.png)

>  회원 가입 후 클라우드 페이지에 접속 했을떄 볼수 있는 화면입니다. 

처음 계정을 생성했다면 우측에는 무료로 받은 Credit이 표시됩니다. 저는 몇달 전에 만들어 놨다가 간만에 들어와봤습니다. 데이터 베이스 생성 , VM 인스턴스 등을 생성 할 수 있는데 저는 VM 인스턴스를 생성 해 보도록 하겠습니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud1-instance.assets/img-20211126213240467.png)



> Create a VM instance 버튼을 클릭합니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud1-instance.assets/img-20211126213240492.png) 

> 여러가지 설정을 할 수 있는 화면이 나옵니다. 

기본 적으로 OS가 Oracle Linux로 설정되어 있습니다. Edit을 눌러 변경 할 수 있습니다.



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud1-instance.assets/img.png)



> 이때는 오라클 리눅스를 선택하면 메모리를 6기가까지 공짜로 줬었나본데요, 지금은 어림도 없습니다.

여기에서 Change Image를 누르면

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud1-instance.assets/img-20211126213240447.png)

 

>  Always Free Eligible 붙어있는 OS들을 무료로 사용 할 수 있습니다. 저는 Ubuntu로 해보겠습니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud1-instance.assets/img-20211126213240471.png)

> Shape 는 메모리 1기가 짜리 이 옵션만 선택이 가능합니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud1-instance.assets/img-20211126213240477.png)

### SSH 키 생성

SSH key 를 추가해야 합니다. 발급한 SSH key는 다시 발급 받을 수 없으니 잘 챙겨 두어야 합니다.

MacOS 를 사용 중이라면 가지고 계신 SSH를 사용하셔도 됩니다. 저는 Generate 하지 않고, 제 맥북에서 생성해서 등록하겠습니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud1-instance.assets/img-20211126213240458.png)

> 확인 해보니 저는 ssh키가 없기 때문에 제 맥에 하나 생성 하도록 하겠습니다.



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud1-instance.assets/img-20211126213240449.png)



>  맥이나 리눅스 에서는 ssh-keygen 명령으로 간단하게 키를 생성 할 수 있습니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud1-instance.assets/img-20211126213240485.png)



>  그럼 위와 같이 저장할 파일 명을 입력하라고 합니다. 저는 그대로 엔터를 쳐서 id_rsa를 사용하겠습니다.



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud1-instance.assets/img-20211126213240478.png)



>  그다음에는 비밀번호를 입력하라고 나오는데, 저는 비밀번호를 따로 입력하지 않겠습니다.

 



![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud1-instance.assets/img-20211126213240473.png)



>  키가 생성 되었습니다 ! 이제 생성한 키를 등록 해보도록 하겠습니다.

```xml
pbcopy < ~/.ssh/id_rsa.pub
```

위의 명령어를 입력 하면 클립보드에 ssh 키를 복사 합니다.

### SSH 키 등록

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud1-instance.assets/img-20211126213240494.png)



그래서 여기에 붙여 넣기 하면 됩니다.

혹은

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud1-instance.assets/img-20211126213240513.png)

직접 경로를 찾아 가서 .pub 파일을

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud1-instance.assets/img-20211126213240494-7929960.png)

이렇게 등록 하셔도 됩니다. 편한 방법을 하시면 됩니다.

![image-20211126214050257](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud1-instance.assets/image-20211126214050257.png)

> 여러개도 등록 할 수 있으니, 할 때 해두면 나중에 귀찮게 추가 하지 않아도 됩니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud1-instance.assets/img-20211126213240502.png)

이제 저장을 해서 인스턴스를 생성 해보겠습니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud1-instance.assets/img-20211126213240528.png)

> 인스턴스가 열심히 생성되고 있습니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud1-instance.assets/img-20211126213240514.png)

금방 준비가 되었네요 !

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud1-instance.assets/img-20211126213240507.png)

public IP를 확인 하고, 접속을 하도록 하겠습니다.

`ssh ubuntu@ip주소` 를 입력 하면 접속 할 수 있습니다.

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud1-instance.assets/img-20211126213240516.png)

첫 접속이기 때문에 보이는 것 처럼 경고가 나오는데요.  yes를 입력합니다. 

![img](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/cloud/oraclecloud1-instance.assets/img-20211126213240530.png)

> 성공적으로 생성한 instance에 접속했습니다 !

다음번에는 생성한 인스턴스를 이용해 서버를 구동 해 보도록 하겠습니다. 
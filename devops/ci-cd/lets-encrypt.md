# Let's encrypt로 SSL 인증서 발급받기

## Intro

기존에 ZeroSSL을 활용해서 SSL을 발급받았는데, 90일마다 제법 번거로운 과정을 통해 재발급을 하는건 그래도 견딜 수 있었지만, 다른 도메인 포함 총 3번의 90-Day Certificates 발급을 하고 나니 이제부터는 돈내고 인증서를 발급받으라고 한다.

앞으로 zeroSSL을 쓸 일은 없겠다. 

다른 무료 인증서 발급 기관을 찾아보다보니 Let's Encrypt가 사용방법도 편하고 무엇보다 비영리 단체이기 때문에 앞서 경험했던 것과 비슷한 상황을 걱정할 필요도 없고 갱신 방법이 간단하다는 점에서 선택하게 되었다.

심지어 대부분의 문서가 한국어로 번역도 잘 되어 있기 때문에 누구나 손쉽게 사용할 수 있을거라 생각한다.

## 발급

https://letsencrypt.org/getting-started/

Let's Encrypt의 Getting Started 탭을 확인 해 보면 Certbot 클라이언트를 이용해서 발급받으라고 한다. 클라이언트 옵션은 https://letsencrypt.org/docs/client-options/ 링크에서 확인 할 수 있는 것 처럼 굉장히 많지만 특별한 상황이 아니면 Certbot을 쓰면 되겠다.

### Certbot 설치

권유한대로 Certbot 설치 부터 시작해본다. 운영할 서버가 우분투라서 apt를 이용해 설치 했다.

그 외 다양한 상황에서의 설치 방법에 대해서는 https://certbot.eff.org/instructions 를 참고하도록 한다. 

![image-20230520160131523](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/lets-encrypt.assets/1.webp)

> 소프트웨어와 OS를 선택하면 설치 방법부터 발급까지 친절하게 알려준다.

```bash
sudo apt-get update
sudo apt-get install certbot
```

### Certificate 발급

설치가 완료되었다면 certbot에게 인증서 발급을 요청한다.

아래의 yourdomain.com 자리에 본인의 도메인 주소를 써주도록 한다.

```bash
sudo certbot certonly --standalone -d yourdomain.com
```

처음에는 이메일 주소를 입력하라고 한다.

![image-20230520160325301](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/lets-encrypt.assets/2.webp)

그다음에는 이용 약관에 동의한다.

![image-20230520160417034](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/lets-encrypt.assets/3.webp)

그다음에는 이메일 수신 동의를 하는데 거절했다.

![image-20230520160434456](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/lets-encrypt.assets/4.webp)

그러면 이제 발급이 된다.

![image-20230520160528349](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/lets-encrypt.assets/5.webp)

만료는 90일 이후다. 갱신은 단순하게 `certbot renew`만 하면 된다고 하니 번거롭지 않고 아주 좋다.

### 인증서 포맷 변경

생성된 파일을 확인해본다.

![image-20230520160755068](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/lets-encrypt.assets/6.webp)

`/etc/letsencrypt/live` 하위 경로에 도메인이름의 폴더를 생성 하여 키파일들을 저장해두었는데, sudo 권한이 필요하다.

그런데 Let's Encrypt로 생성한 키파일은 PEM 포맷이다. 자바는 PKCS12 를 사용하기 때문에 `.p12` 파일로 변환해준다.

```bash
sudo openssl pkcs12 -export -in /etc/letsencrypt/live/dutypark.o-r.kr/fullchain.pem -inkey /etc/letsencrypt/live/dutypark.o-r.kr/privkey.pem -out keystore.p12 -name tomcat -CAfile /etc/letsencrypt/live/dutypark.o-r.kr/chain.pem -caname root
```

> dutypark.o-r.kr 로 작성해둔 부분은 각자 본인의 도메인으로 변경하여 입력 한다.

암호를 입력하라고 하는데, 까먹지 않도록 유의한다.

![image-20230520161003650](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/lets-encrypt.assets/7.webp)

위의 명령어를 입력 하면, 명령어를 입력했던 경로에 `keystore.p12` 파일이 생성된다.

이 파일로 인증서를 갈아끼우면 된다. 

>  스프링부트 프로젝트에서의 SSL 적용방법이 필요하다면 https://shanepark.tistory.com/442 를 참고하도록 한다.
>
> 해당 글에서 `SSL 인증서를 Java keystore 로 변환` 이후 부분을 참고하면 될 것이다.

인상적인건 `.well-known/` 경로에 본인의 웹사이트임을 인증하는 과정이 필요할거라고 생각했는데 딱히 그런걸 요구하지는 않았다.

### 결과

![image-20230520162152545](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/lets-encrypt.assets/8.webp)

단순하게 `keystore.p12` 파일만 바꿔치기 후 서버를 새로 띄우니 정말 손쉽게 인증서가 갱신되었다. 

앞으로는 `sudo certbot renew` 로 손쉽게 갱신하거나 그것마저 cron job으로 자동화하면 되니 그 얼마나 편리한가. 

## 재발급

드디어 3개월이 지났다. 재발급이 가능할 때가 되니 lets encrypt에서 자동으로 이메일도 보내준다. 얼마나 간단한지 한번 인증서 재발급을 받아보자.

`sudo certbot renew`를 실행 한다.

![fail](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/lets-encrypt.assets/9.webp)

아쉽게도 한번에 하는건 실패했는데, 이를 해결하기 위해서는 certbot의 **standalone** 동작 방식에 대한 이해가 필요하다.

**standalone** 방식은 기존의 사이트 작동을 잠시 멈추고 80 포트로 가상의 인증서버를 띄워 확인하도록 되어 있는데, 이때 인증에 실패한것이다. 여러가지 원인을 의심 할 수 있겠지만 나의 경우에는 iptables 방화벽에서 443 포트만 열어두도록 설정해두었던게 문제였다. 80포트도 허용을 해 주어야 한다.

아래와 같이 입력해 포트 허용 및 설정값을 persist 했다.

```bash
# 80 포트 허용
sudo iptables -I INPUT 6 -p tcp --dport 80 -j ACCEPT

# iptables 설정값 저장
sudo sh -c "iptables-save > /etc/iptables/rules.v4"
```

이후 다시 시도하여 성공 하였다.

![success](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/lets-encrypt.assets/12.webp)

> 인증서 재발급 설공

하지만 아쉽게도 여기에서 끝은 아니다. 위에서 한번 해 봤던 것 처럼 인증서 포맷 변경이 필요하다. `renew`만 한 상태에서 여전히 예전의 `keystore.p12` 를 사용하고 있다면 인증서 유효기간은 예전 그대로다.

일단 아래의 명령어를 참고해 `.pem` 파일이 있는 장소에 `keystore.p12` 파일을 생성해준다.

```bash
sudo openssl pkcs12 -export -in /etc/letsencrypt/live/dutypark.o-r.kr/fullchain.pem -inkey /etc/letsencrypt/live/dutypark.o-r.kr/privkey.pem -out /etc/letsencrypt/live/dutypark.o-r.kr/keystore.p12 -name tomcat -CAfile /etc/letsencrypt/live/dutypark.o-r.kr/chain.pem -caname root
```

그러고 나서는 `key-store` 경로를 방금 생성한 키 파일 위치로 지정해준다. 원래는 `classpath:keystore.p12` 로 지정하고 secret repository에서 pull을 하도록 CI/CD 를 구성해두었었는데, 그러면 인증서를 갱신 할 때마다 커밋도 새로 해야하기 때문에 그럴필요가 없도록 파일경로로 지정해두었다.

물론 인증서가 갱신되었을 때 어플리케이션을 재시작하는건 필요하다.

${code:application.yml}

```yaml
server:
  ssl:
    key-store: /etc/letsencrypt/live/dutypark.o-r.kr/keystore.p12
```

이제 변경사항을 Merge 해서 새로운 인증서가 적용되었는지 확인 해 본다.

![image-20230729190322519](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/lets-encrypt.assets/10.webp)

> Github Action이 수행되었다.

사이트에 접속해서 인증서 정보를 확인 해본다.

![image-20230729190413137](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ci-cd/lets-encrypt.assets/11.webp)

인증서가 정상적으로 갱신되어 유효 기간이 늘어났다. 확실히 certbot을 사용하는 방법이 간편하다.

 3개월에 한번 하는 것도 귀찮고 모든 작업을 자동화하고 싶다면, cronjob으로 아래의 세가지 작업을 연달아 하는 쉘스크립트를 작성해 등록해주면 되겠다.

- certbot renew
- keystore.p12 파일 생성
- 어플리케이션 재시작

**References**

- https://letsencrypt.org/getting-started/
-  https://certbot.eff.org/instructions
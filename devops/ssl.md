# 무료 SSL 인증서 발급받아 HTTPS 적용하기

## Intro

보통 토이프로젝트를 개발 하면 사람마다 다르겠지만 보통은 아래와 같은 순서로 조금씩 발전하게 됩니다.

1. 로컬에 프로젝트를 띄워봅니다. 가장 기본적인 첫 단계 입니다.
2. 각자의 컴퓨터에서 친구들과 같이 개발하기 위해 외부 DB를 연동 합니다. AWS RDS가 가장 쉽지만 잘못하면 비용이 무지막지하게 청구 될 수 있으니 보통 안쓰는 컴퓨터를 서버로 사용하곤 합니다만 방화벽 및 포트포워딩과의 전쟁이 처음 펼쳐집니다. 2단계 치고는 꽤 매콤한 편 입니다.
3. 개발한 프로젝트를 계속 띄워 놓고 싶습니다. 어디서든 접근할 수 있었으면 해서 띄워둡니다. DB서버 띄워놓는 것 처럼 안쓰는 윈도우 PC에 IDE나 외장 톰캣 혹은 스프링 부트를 jar 파일로 바로 띄워놓고 24시간 컴퓨터를 켜 둡니다. 2단계에서 방화벽 및 포트포워딩을 물리쳤다면 크게 어렵지는 않지만 컴퓨터가 절전모드라도 들어가면, 네트워크가 끊기기라도 하면.. 윈도우 업데이트라도 자동으로 되었다간 서버가 쉽게 죽습니다.
4. 안전적으로 계속 띄워놓고 싶습니다. 리눅스를 공부해 무료 클라우드에서 어찌어찌 프로젝트를 반영해 띄워두면 외부에서 아이피로 접속을 할 수 있습니다. 2단계에서 겪어보지 못한 여러 레이어의 방화벽을 경험 할 수 있을 뿐더러 리눅스를 처음 접한다면 엄청난 벽을 느끼게 됩니다.
5. 어찌어찌 아이피로 접속만 할 수 있음 좋겠다고 생각했는데 인간의 욕심은 끝이 없습니다. 도메인을 갖고 싶습니다. 저렴한 가격에 `.net` 이나 요즘 유행하는 `.dev` 혹은 `.io` 등의 도메인을 저렴하게 구입하거나 Freenom 에서 `.ml` 혹은 `.tk` 같은 도메인을 구합니다. 처음에는 아무것도 몰라 포워딩으로만 연결을 하다가 나중에는 DNS 설정을 하면 이제는 좀 그럴 싸하게 접속 할 수 있습니다.

여기까지가 초보 개발자가 욕심내서 할 수 있는 어찌보면 토이프로젝트 배포의 종착역이었다고 생각합니다. 물론 DevOps의 영역으로 넘어가면 또 다른 차원의 세계가 펼쳐진다지만 "내가 개발한 작업물을 인터넷에 올리고 접속이 가능하게 해보고 싶어" 라는 소박한 꿈을 이루기 위해는 이쯤이면 충분합니다.

> 아니 충분 했습니다.. 

이제는 어느덧 HTTPS 라는 새로운 미션이 하나 추가되었습니다. HTTPS 라는 기술 자체는 나온지 오래 되었지만 정말 보안이 중요한 분야가 아니고서는 적용이 지지부진 했었는데.. 어느 순간부터 HTTP 사이트에 접속만 하면 크롬 브라우저가 **안전하지 않다며 온갖 난리를** 치기 시작하니 현재는 대부분의 사이트가 HTTPS를 지원하고 있습니다.

![image-20221214223225507](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/ssl.assets/image-20221214223225507.png)

> https://transparencyreport.google.com/https/overview?hl=ko

신나게 토이프로젝트를 개발 하고, IT 분야를 잘 모르는 친구나 가족에게 내 사이트에 방문 해 보라고 했는데

![image-20221214223617482](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/ssl.assets/image-20221214223617482.png)

> 이 사이트는 취약합니다.

이런 무시무시한 경고가 반긴다면 친구는 접속하기 영 찝찝 할테고, 신뢰도가 낮다면 얘가 내 핸드폰을 해킹하려고 하나 의심도 할 수 있을 겁니다.

그런 민망한 상황을 피하기 위해, 그리고 소박한 꿈의 1차 목표를 이루기 위해 무료로 SSL 인증서를 발급 하고 실제 적용하는 방법에 대해 알아보려고 합니다. 저는 스프링 부트 프로젝트에 적용을 해 보았는데, 다른 프레임워크를 써도 아니면 웹 서버를 쓴다고 해도 일단 인증서를 발급 하면 그 다음부터는 크게 어렵지 않습니다.

## 인증서 발급

무료 인증서를 발급 받을 수 있는 곳은 여러가지가 있습니다. 저는 제가 무료로 도메인을 사용중인 `https://내도메인.한국` 에서 인증서 발급용으로 SSL For Free를 추천하고 있기에 해당 사이트를 이용해 보았습니다.

일단 사이트로 이동 합니다.

https://www.sslforfree.com/

![image-20221213214956501](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/ssl.assets/image-20221213214956501.png)

SSL 인증서를 만들고 싶은 도메인 주소를 입력 합니다. 이후 Create Free SSL Certificate 를 클릭 합니다.

![image-20221213215105500](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/ssl.assets/image-20221213215105500.png)

> 회원 가입 

회원 가입을 하라고 합니다. 이메일 및 패스워드를 대충 입력하고 Register를 클릭 합니다. Domain 입력한 주소가 보이는데 한번 더 확인을 해 줍니다.

### Step1

wildcard certificate 를 하려면 체크하라고 하는데, `PRO` 마크가 붙은 거에서 돈 냄새가 납니다. Wildcard는 *.example.com 같은걸 말하는 건데 한가지 도메인에 여러가지 서비스를 연결 할 때 유용할 거 같긴 하지만 지금은 꼭 없어도 됩니다.

아래에 domain 주소를 하나 입력 하고, next Step을 눌러 줍니다. Add Domain 에도 `PRO` 마크가 붙어 있기 떄문에 `+` 버튼은 누르지 않습니다.

![image-20221213215258465](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/ssl.assets/image-20221213215258465.png)

> 따로 변경 할 것 없이 Nex Step

### Step2

90일짜리 인증서를 할 지, 1년짜리 인증서를 할 지 선택 해 줍니다. 귀찮은 사람은 1년짜리를 하라며 친절하게 `PRO` 마크를 붙여놨습니다. 여기도 사업인데 먹고 살긴 해야죠.. 우리는 귀찮은것 보다 무료인게 중요 하니 90일을 선택 합니다. 90일 후에 또 연장 하면 됩니다.

![image-20221213215637229](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/ssl.assets/image-20221213215637229.png)

> 무료로 하려면 90-Day Certificate 선택

### Step3

CSR 정보를 자동으로 생성 할 건지 아니면 수동으로 작성할 건지를 선택 합니다. 국가, 시, 도, 조직명, 부서명 등등을 입력하는건데 사업할 것도 아닌데 번거롭게 하지 않고 자동으로 생성 하도록 체크 해 둡니다.

![image-20221213215328487](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/ssl.assets/image-20221213215328487.png)

> Next Step

### Step 4

마지막으로 요금제를 선택합니다. 어떤것을 선택할지는 각자의 선택에 맡기겠습니다.

![image-20221213215717411](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/ssl.assets/image-20221213215717411.png)

> 저는 첫번째꺼를 했습니다.

### Verify

이제 실제 해당 도메인의 보유자임을 증명 해야 합니다. 총 세가지 방법이 있는데요

1. 이메일로 인증하기
2. DNS(CNAME)
3. HTTP 파일 업로드

![image-20221213220113320](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/ssl.assets/image-20221213220113320.png)

HTTP File Upload가 가장 만만합니다. 저는 세번째 방법을 선택 했습니다.

**Download Auth File** 버튼을 클릭해 인증 파일을 받고 인증 방법에서 요구하는 경로로 요청 할 때 해당 파일을 전달 하도록 합니다.

![image-20221213220937247](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/ssl.assets/image-20221213220937247.png)

> `/.well-known/pki-validation/` 경로에 다운받은 파일을 위치시켰습니다.

이후 로컬에서 `http://localhost:8080/.well-known/pki-validation/9A0687B207126BBCC54C5084871588A8.txt` 경로에 요청을 보내 응답을 정상적으로 하는지 확인 해 보았습니다.

![image-20221213220915271](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/ssl.assets/image-20221213220915271.png)

> 정상 응답

로컬에서 성공했으면, 이제 새로 빌드 후 배포해서 위의 3번 스텝에서 요청하는 정확한 경로로 요청이 되는걸 확인 한 후에

![image-20221213221214187](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/ssl.assets/image-20221213221214187.png)

> 인증 준비가 완료 되었습니다.

Next Step 클릭 해 Verify Domain 을 진행 합니다.

![image-20221213221330181](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/ssl.assets/image-20221213221330181.png)

> 아래의 Verify Domain을 클릭

그러면 진행 되는 동안 위의 메시지가 계속 변하며 발급 단계가 하나씩 진행 됩니다.

![image-20221213221402536](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/ssl.assets/image-20221213221402536.png)

> 시스템이 인증서를 issuing 중 입니다.

조금 기다리면 인증서 발급에 성공 합니다.

![image-20221213221818612](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/ssl.assets/image-20221213221818612.png)

> 인증서 발급이 완료 되었고 사용할 준비가 되었습니다.

## 인증서 적용

이제 인증서를 발급 받았으니, 해당 인증서를 적용 해서 HTTPS 연결이 가능하도록 만들 차례 입니다.

### SSL 인증서 다운로드

서버 타입을 선택하고 인증서를 다운로드 받습니다. Tomcat 을 선택하였습니다. 

`Download Certificate(.zip)` 을 클릭 했을 때 다운받는 파일은 같고, 서버 타입 선택은 후에 인증서 등록 방법을 알려 줄 때 쓰입니다.

![image-20221213221905772](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/ssl.assets/image-20221213221905772.png)

> 서버 타입 선택 후 Download Certificate(.zip) 클릭

인증서를 다운 받아서 압축을 풀어 보면 아래와 같이 세가지 파일이 들어 있습니다.

![image-20221213222056574](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/ssl.assets/image-20221213222056574.png)

이제 Follow the steps listed [on this page](https://help.zerossl.com/hc/en-us/articles/360060120393-Installing-SSL-Certificate-on-Tomcat). 를 클릭 하면 자세한 톰캣에서의 인증서 등록 방법을 알려주는데요, 외장 톰캣 사용법을 알려주지만 저는 jar 파일로 바로 띄우기 떄문에 스프링부트 프로젝트에 바로 적용을 해 보려고 합니다.

아래부터는 스프링부트 프로젝트에서의 SSL 인증서 적용 방법에 대한 안내 입니다.

### SSL 인증서를 Java keystore 로 변환

스프링부트에서 사용 할 수 있게 다운받은 인증서로 pkcs12 파일을 생성 합니다.

Terminal 을 켜고 아래와 같이 입력 합니다. 그러면 비밀번호를 입력 하라고 하는데 나중에 필요 하기 때문에 잊어버리지 않게 잘 입력 합니다.

```bash
openssl pkcs12 -export -out keystore.p12 -inkey private.key -in certificate.crt -certfile ca_bundle.crt
```

![image-20221213224622731](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/ssl.assets/image-20221213224622731.png)

> 비밀번호 입력

비밀번호를 총 두번 입력 하고나면 아래 보이는 것 처럼  `keystore.p12` 파일이 생성 됩니다.

![image-20221213224720832](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/ssl.assets/image-20221213224720832.png)

>  keystore.p12

### Spring Boot 에서 HTTPS 적용

이제 실제 스프링부트 프로젝트에서 적용 시켜 보겠습니다.

방금 생성한 p12파일을 classpath 에 넣어 둡니다.

![image-20221213224930787](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/ssl.assets/image-20221213224930787.png)

> resources/keystore.p12

application.yml 을 수정 해 줍니다. 아까 입력한 비밀번호를 까먹지 말고 정확히 입력 합니다.

```yaml
server:
  ssl:
    key-store: classpath:keystore.p12
    key-store-password: 입력한 비밀번호
    key-store-type: PKCS12
```

이제 그대로 로컬에서 다시 서버를 띄우고 https 로 접속을 시도 해 봅니다. 그러면 아래와 같이 경고가 나오는데요

![image-20221213225501013](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/ssl.assets/image-20221213225501013.png)

> 인증서에 문제가 있다는 경고

인증서에 등록된 주소와 일치하지 않기 떄문입니다. 여기에서 아래 보이는 view the certificate 를 클릭해서 인증서를 확인 해 보면..

![image-20221213225553628](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/ssl.assets/image-20221213225553628.png)

ZeroSSL에서 등록 한 dutypark.o-r.kr 인증서가 보이고, 등록한 대로 90일 후의 만료 일자도 표시가 됩니다.

정상적으로 인증서가 잘 등록된걸 확인 했으니 이제 새로 빌드 후 배포해서 확인 해 봅니다.

![image-20221213230418622](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/ssl.assets/image-20221213230418622.png)

> 드디어 자물쇠가 생겼습니다!

![image-20221213230407066](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/ssl.assets/image-20221213230407066.png)

> 인증서가 Valid 상태 입니다. SSL 적용이 정상적으로 완료되었습니다.

인증서 등록이 완료 되면, ZeroSSL 에서도 개별 체크 후 정상적으로 secured 되었다는 것을 확인 해 줍니다.

![image-20221213231313147](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/ssl.assets/image-20221213231313147.png)

### HTTP 요청을 HTTPS로 리다이렉트

그런데 이전에 http로 즐겨찾기 해두었다면 접속이 되지 않습니다. http://naver.com/ 로 접속을 하면 https 로 자동 리다이렉트 되는 것 처럼 저희도 고객들이 서비스 망한 줄 알고 딴데로 가지 않도록 특단의 조치가 필요 합니다.

![image-20221213231709636](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/ssl.assets/image-20221213231709636.png)

> 이미 기존의 주소로 등록해두고 접속 하는 사용자도 있을 수 있고, 역링크 된 경우도 혹은 이미 크롤링이 된 경우도 있을 수 있기 때문에 http로 접속 시 https 로 연결할 수 있도록 조치가 필요합니다.

몇가지 방법이 있겠지만, 특히 스프링 시큐리티에서 아주 쉽게 설정 할 수 있습니다.

시큐리티를 아직 사용하지 않고 있었다면 의존성을 추가 해 주고

```kotlin
implementation("org.springframework.boot:spring-boot-starter-security")
```

시큐리티 설정 코드를 추가 해 줍니다. requiresSecure 가 핵심 입니다.

#### Java

**SecurityConfig.java**

```java
@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
      .requiresChannel(channel -> 
          channel.anyRequest().requiresSecure())
      .authorizeRequests(authorize ->
          authorize.anyRequest().permitAll())
      .build();
    }

}
```

#### Kotlin

**SecurityConfig.kt**

```kotlin
@Configuration
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http.requiresChannel {
            it.anyRequest().requiresSecure()
        }.authorizeHttpRequests()
            .anyRequest()
            .permitAll()
            .and().build()
    }
}
```

이제는 http 요청시 자동으로 https로 리다이렉트 됩니다. 고객들이 헤멜 필요가 없습니다.

 `내가 개발한 작업물을 인터넷에 올리고 접속이 가능하게 해보고 싶어` 의 꿈을 이루셨네요 축하드립니다.

## 만료후 연장

SSL 인증서를 처음 발급받은지 어느덧 3개월이 금방 지났습니다. 만료 14일 전 부터 연장이 가능합니다.

![image-20230302223659120](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ssl.assets/image-20230302223659120.png)

> Expiring Soon

Renew 버튼을 눌러 연장을 해 보겠습니다.

![image-20230302223750100](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ssl.assets/image-20230302223750100.png)

처음에는 딱히 바꿀게 없습니다 Next Step을 누릅니다.

![image-20230302223808699](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ssl.assets/image-20230302223808699.png)

당연히 무료인 90Day Certificate를 선택 합니다.

![image-20230302223925945](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ssl.assets/image-20230302223925945.png)

CRS 은 역시 Auto-Generate를 그대로 합니다.

![image-20230302223955637](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ssl.assets/image-20230302223955637.png)

Renew 할 때도 처음 SSL 발급받을때와 거의 다를게 없습니다. Free를 선택 하고 Next Step을 눌러 줍니다.

![image-20230302224248676](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ssl.assets/image-20230302224248676.png)

처음에 했던 것 처럼 Verify Domain도 해줍니다. 다만, 위의 링크에 적혀 있는 것 처럼 http 링크로 인증을 해 주어야 하기 때문에 이게 좀 불편 했습니다. 잠시 SSL을 disable 해두지 않으면

![image-20230302225310485](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ssl.assets/image-20230302225310485.png)

이처럼 HTTP transport error가 나며 인증에 실패합니다. 포트도 80으로 돌리고, SSL도 끄고, http 요청을  https로 자동으로 돌려주는 기능도 꺼두어야 합니다.

그러고 나면 인증에 성공 합니다.

![gif](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ssl.assets/gif.gif)

> 잠시 기다려 줍니다

![image-20230302225838703](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ssl.assets/image-20230302225838703.png)

드디어 새로운 인증서가 발급 되었습니다. `Download Cetrificte(.zip)` 을 클릭해 다운받고 나서는 처음 발급했던 것과 똑같이 Java keystore로 변환 해서 등록 해 주면 됩니다. 비밀번호만 전에꺼와 똑같이 해 주면 굳이 설정파일을 변경하지 않아도 `keystore.p12` 파일만 갈아 끼우면 끝 입니다.

서버에 새로 반영해서 Certificate를 확인 해 봅니다.

![image-20230302230419112](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/ssl.assets/image-20230302230419112.png)

> issued On 및 Expires On 날짜가 새로 업데이트 되었습니다! 

이상입니다 

**References**

- https://docs.vmware.com/en/VMware-Horizon-7/7.13/horizon-scenarios-ssl-certificates/GUID-17AD1631-E6D6-4853-8D9B-8E481BE2CC68.html
- https://www.sslforfree.com/
- https://www.thomasvitale.com/https-spring-boot-ssl-certificate/
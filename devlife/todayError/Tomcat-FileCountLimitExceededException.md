# Tomcat FileCountLimitExceededException 해결

## 문제

새로운 서버에 애플리케이션을 배포하는 과정에서 예상하지 못한 오류가 발생했다. 로컬 머신과 개발 환경에선 전혀 문제가 없었고 지금까지 여러번 배포하며 같은 문제가 발생한 적이 없었는데 `FileCountLimitExceededException` 라는 처음 보는 에러가 발생했다.

POST 요청으로 multipart/form-data 를 사용해 데이터를 추가하는 엔드포인트였고, 이 폼은 몇개의 텍스트 필드와 파일 필드로 구성되어 있다.

> Failed to parse multipart servlet request; nested exception is org.apache.tomcat.util.http.fileupload.impl.FileCountLimitExceededException: attachment

전체 스택 트레이스는 아래와 같다.

```
adm  | AIP :07-01 09:39:25[o.s.b.w.s.s.ErrorPageFilter   ]ERROR- Forwarding to error page from request [/collection/] due to exception [Failed to parse multipart servlet request; nested exception is org.apache.tomcat.util.http.fileupload.impl.FileCountLimitExceededException: attachment]
adm  | org.springframework.web.multipart.MultipartException: Failed to parse multipart servlet request; nested exception is org.apache.tomcat.util.http.fileupload.impl.FileCountLimitExceededException: attachment
adm  | 	at org.springframework.web.multipart.support.StandardMultipartHttpServletRequest.handleParseFailure(StandardMultipartHttpServletRequest.java:124)
adm  | 	at org.springframework.web.multipart.support.StandardMultipartHttpServletRequest.parseRequest(StandardMultipartHttpServletRequest.java:115)
adm  | 	at org.springframework.web.multipart.support.StandardMultipartHttpServletRequest.<init>(StandardMultipartHttpServletRequest.java:88)
adm  | 	at org.springframework.web.multipart.support.StandardServletMultipartResolver.resolveMultipart(StandardServletMultipartResolver.java:122)
adm  | 	at org.springframework.web.servlet.DispatcherServlet.checkMultipart(DispatcherServlet.java:1205)
adm  | 	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1039)
adm  | 	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:963)
adm  | 	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1006)
adm  | 	at org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:909)
adm  | 	at javax.servlet.http.HttpServlet.service(HttpServlet.java:555)
adm  | 	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:883)
adm  | 	at javax.servlet.http.HttpServlet.service(HttpServlet.java:623)
... 이하생략
```

스택트레이스를 보면 파일을 너무 많이 첨부했다는 소리같이 보여 함정에 빠질 수 있다.

파일 첨부가 가능한 form이긴 했지만, 이번 요청에 딱히 파일데이터를 포함하진 않았다. 에러가 발생한 상황의 Request payload는 아래와 같다.

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/devlife/todayError/Tomcat-FileCountLimitExceededException.assets/2.webp)

## 원인

관련 이슈를 검색해보니 검색 결과가 매우 드물다. 대 AI의 시대에서 모두가 LLM에 질문을 던지지만, 학습되지 않은 정보에 대해 GPT는 응답을 해줄 수 없다. 비교적 최근의 문제라는건데 이때는 새로 패치가 적용된 부분들을 파보아야 한다.

Dockerfile을 확인하니 `FROM tomcat:9-jdk8` 라고 기입되어 있다. 마이너 버전까지 기입되어있지 않기 때문에 새로 생성한 컨테이너의 톰캣 버전을 확인해본다.

```bash
catalina.sh version
```

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/devlife/todayError/Tomcat-FileCountLimitExceededException.assets/1.webp)

 `Tomcat 9.0.106` 라고 나온다. 내가 모르는 사이에 새로운 버전이 나왔고, 해당 버전이 깔린 이미지를 내려받은 것이었다. 이제 톰캣의 릴리즈 노트를 살펴본다.

![3](https://raw.githubusercontent.com/ShanePark/mdblog/main/devlife/todayError/Tomcat-FileCountLimitExceededException.assets/3.webp)

> https://tomcat.apache.org/tomcat-9.0-doc/changelog.html

Coyote 쪽에 `maxPartCount`라는 의심스러운 항목이 보인다. 이어 Tomcat 9.x 버전의 [취약점 패치 내용](https://tomcat.apache.org/security-9.html#Fixed_in_Apache_Tomcat_9.0.106)을 확인 해보면 2025년 6월 10일에 적용된 `9.0.106` 패치에서 [CVE-2025-48988](https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2025-48988) 취약점으로 인해 최대 parts 수를 제한하는 `maxPartCount` 라는 설정값이 추가되었고 기본값이 10으로 적용되었다고 나와있다.

방금 에러가 터진 상황에서는 파일은 하나도 포함하지 않았지만 요청에 총 12개의 Part가 포함되어 있고 그로 인해 처음 화면과 같이`FileCountLimitExceededException`이 발생한 것이다.

## 해결

maxPartCount 를 늘려줘서 해결해주면 된다. 아래의 예시에서는 이전과 같이 무제한으로 설정하기 위해 -1 로 해두었지만, 이럴경우 기존처럼 DDOS 공격에 취약하다고 하니 적절한 수를 정해줘야 하겠다.

```xml
<Connector port="8080" protocol="HTTP/1.1"
           connectionTimeout="20000"
           maxPartCount="-1" 
           redirectPort="8443" />
```

Spring Boot에는 `v3.4.7` 부터 적용되었으며 `server.tomcat.max-part-count` 설정을 통해 변경할 수 있다.

이제 새로운 설정으로 도커 이미지를 다시 빌드하고 테스트 해보니 문제가 없었다.

## 마무리

톰캣에서 갑작스럽게 Part 수를 제한하고, 그 제한 수도 10개로 빡빡하게 둔건 예상하지 못했던 상황이라 당황스러웠다.

보안상 어쩔 수 없는 판단이었고 나름 fail-fast 전략을 취해서 많은 사람들에게 알리려고 시도했다고 이해하기로 한다.

끝

**References**

- https://tomcat.apache.org/security-9.html#Fixed_in_Apache_Tomcat_9.0.106
- https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2025-48988
- https://stackoverflow.com/questions/79670639/how-to-configure-tomcat-max-file-count-size
- https://github.com/spring-projects/spring-boot/issues/45881
- https://github.com/spring-projects/spring-boot/releases/tag/v3.4.7
- https://joon2974.tistory.com/entry/Tomcat-FileCountLimitExceededException-%EC%9D%B4%EC%8A%88-%EC%88%98%EC%A0%95
# JAVA) proxy 사용해 연결

​		

이전 글 [SSH를 이용한 Proxy, Dynamic Port Forwarding (SOCKS)](https://shanepark.tistory.com/266) 을 통해 proxy 서버를 열어 보았습니다.

거기에 이어서,  열어둔 서버로의 프록시 접속을 웹 브라우저 에서 뿐만 아니라 자바 어플리케이션을 통해 접속을 해 보겠습니다.

이미 설정 해 둔 프록시 서버가 있지 않다면 이전 글을 먼저 참고 해 주세요.

​		

크게 두 가지 방법이 있습니다.

​		

## 1.System.getProperties()를 이용한 설정

​	

일단 간단하게 UrlConnection을 연결 해서 `www.ifconfig.me` 주소로 신호를 보내는 프로그램을 작성 해 보았습니다.

```java
URL url = new URL("https://www.ifconfig.me");
StringBuffer sb = new StringBuffer();
URLConnection urlConn = url.openConnection();
try (InputStream is = urlConn.getInputStream();
     InputStreamReader isr = new InputStreamReader(is, "UTF-8");
     BufferedReader br = new BufferedReader(isr);
    ) {
  String str;
  while ((str = br.readLine()) != null) {
    sb.append(str + "\r\n");
  }

  System.out.println(sb.toString());
}
```

정말 간단하기 때문에 코드를 따로 설명 하지 않아도 아마 다들 이해하실 겁니다.

​	

대충 설명을 하자면 URL커넥션을 열고, 그 커넥션의 inputStream 으로 출력을 받아와서 버퍼로 읽어와 출력 해 주는 과정 인데요,

```bash
curl https://www.ifconfig.me
```

의 결과와 같습니다.

​	

이렇게 해서 실행 하게 되면 그 출력 결과로 현재 자바 어플리케이션을 실행 한 클라이언트 컴퓨터의 아이피 주소가 출력 되는데요.

socksProxy를 통해 연결 해 보겠습니다.

​	

```java
package com.tistory.shanepark.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Proxy {

    public static void main(String[] args) throws IOException {

        ifConfig();

        System.getProperties().put("proxySet", "true");
        System.getProperties().put("socksProxyHost", "localhost");
        System.getProperties().put("socksProxyPort", "9999");

        ifConfig();

    }

    static void ifConfig() throws IOException {
        URL url = new URL("https://www.ifconfig.me");
        StringBuffer sb = new StringBuffer();
        URLConnection urlConn = url.openConnection();
        try (InputStream is = urlConn.getInputStream();
             InputStreamReader isr = new InputStreamReader(is, "UTF-8");
             BufferedReader br = new BufferedReader(isr);
        ) {
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str + "\r\n");
            }
            System.out.println(sb.toString());
        }

    }
}

```

새로 작성 한 코드 입니다. 아이피 주소를 받아 오는 과정을 하나의 메서드로 선언 했습니다.

대신 두 번을 호출 하면서 그 중간에 System의 proxy 설정을 몇 개 해주도록 해 보았습니다.

​	

실행 결과

![image-20211029224829879](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/proxy.assets/image-20211029224829879.png)

​	두번의 실행에서 나오는 아이피 주소가 다릅니다. 두번째에는 프록시 서버를 통해 접속이 진행 된 것이 확인 됩니다.

​	

​	

## 2. java VM argument 설정

​	

프록시 설정 관련 내용을 코드에 넣지 않고 vm argument를 통해서도 가능합니다. 

프록시 설정을 하며 실행 할 자바 코드를 새로 작성 해 보았습니다.

```java
public class ProxyWithArgument {

    public static void main(String[] args) throws IOException {
        URL url = new URL("https://www.ifconfig.me");
        StringBuffer sb = new StringBuffer();
        URLConnection urlConn = url.openConnection();
        try (InputStream is = urlConn.getInputStream();
             InputStreamReader isr = new InputStreamReader(is, "UTF-8");
             BufferedReader br = new BufferedReader(isr);
        ) {
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str + "\r\n");
            }
            System.out.println(sb.toString());
        }
    }
}
```

​	

IDE 마다 설정 방법이 조금 씩 다른데 IntelliJ IDEA와 Eclipse 의 설정을 모두 한번씩 알아 보겠습니다.

​	

- IntelliJ IDEA

![image-20211026145103818](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/proxy.assets/image-20211026145103818.png)

Edit Configurations... 에 들어갑니다.

​	

![image-20211026145153972](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/proxy.assets/image-20211026145153972.png)

Run/Debug Confiurations > Add VM options`Alt+V` 를 선택 하면 하나의 필드가 추가됩니다.

![image-20211026145208614](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/proxy.assets/image-20211026145208614.png)

VM options 블록이 생겼네요. 아래의 내용을 입력 해 줍니다.

```
-DproxySet=true -DsocksProxyHost=localhost -DsocksProxyPort=9999
```

​	

사실 눈치 빠른 분들은 이미 아셨겠지만 

```java
System.getProperties().put("proxySet", "true");
System.getProperties().put("socksProxyHost", "localhost");
System.getProperties().put("socksProxyPort", "9999");
```

처음에 코드로 작성했던 이 내용을 단지 vm argument로 전달 하는 것 뿐 입니다. 



![image-20211026145341253](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/proxy.assets/image-20211026145341253.png)

작성이 완료 되었습니다.

​	

이제 실행 해 보면 코드에 따로 프록시 관련한 내용이 없지만, 프록시 서버의 아이피 주소가 확인 됩니다.

![image-20211029230105502](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/proxy.assets/image-20211029230105502.png)

​	

## Eclipse

이클립스는 조금 다르긴 한데 뭐 비슷 합니다. Edit Configuration 에서 VM arguments: 라는 항목을 찾아서 똑같이 넣어주면 끝 입니다.

![image-20211026144951353](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/proxy.assets/image-20211026144951353.png)

​	

이상으로 java 어플리케이션에서 프록시 서버를 통해 Http 접속을 하는 방법을 알아 보았습니다.

이를 이용하면 특정 아이피로 한정 되어있는 api 를 테스트 한다던가 혹은 엘라스틱 서치와 같이 로컬에서만 사용 할 수 있도록 설정 된 경우가 있을 때 원격지에서도 쉽게 접속 해서 사용 할 수 있습니다.



# IntelliJ IDEA) Path Variable 설정으로 VM Argument 저장해두기

## Intro

웹 어플리케이션을 개발 하다보면, 외부 API를 사용해야 할 때가 종종 있습니다. 

거기에 추가로, 드물기는 하지만 호출을 위한 API KEY가 특정 IP에 종속된 경우가 존재합니다. 그럴 경우에는 개발환경에서도 키를 하나 발급받아서 따로 사용하는게 가장 이상적이기는 하지만.. 그게 현실적으로 어려운 경우가 많습니다. API 키 발급 조건이 까다로울수록 더더욱 그렇습니다.

그럴 경우에 저는 보통 Dynamic Proxy를 활용하는 방향을 택합니다. 그러면 어플리케이션에서 외부 API를 호출 할 때에, 해당 프록시 서버의 ip 주소를 통해 요청되기 때문에 특정 IP Address에 종속되어 있는 API 키라고 해도 사용 할 수 있습니다.

> 자바 어플리케이션에서 프록시 서버를 통해 연결하는 방법에 대한 참고 게시글 목록
>
> - [SSH를 이용한 Proxy, Dynamic Port Forwarding (SOCKS)](https://shanepark.tistory.com/266)
>
> - [JAVA) proxy 사용해 연결](https://shanepark.tistory.com/267)

위 두 글의 내용을 요약하자면, `localhost:9999`로 Dynamic 포트포워딩을 해 두었을 경우 아래의 VM Argument 들을 입력 해주면 자바 어플리케이션을 해당 프록시를 통하도록 실행 할 수 있습니다.

```properties
-DproxySet=true -DsocksProxyHost=localhost -DsocksProxyPort=9999
```

![1](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/pathVariable.assets/1-16430081226331.png)

하지만 어떨때는 Proxy를 통해 어플리케이션을 실행 하지만 평소에는 Proxy를 사용하지 않기 때문에 자주 값을 지우게 되는데요. 매번 입력 할 때마다 어디에 저장해 두고 클릭 한번으로 입력할 수 있다면 참 좋겠다는 생각을 했습니다.

![2](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/pathVariable.assets/2.png)

그런데 우측에 두개의 버튼이 보입니다. 그중 왼쪽 버튼을 클릭 해 보니

![image-20220124160004598](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/pathVariable.assets/image-20220124160004598.png)

위에 보이는 것과 같은 매크로가 나왔습니다. 

이걸 이용하면 값을 저장 해 두고 필요할때마다 쉽게 불러와 사용 할 수 있겠다 싶어 시도 해 보았습니다.

## Path Variable 설정

`Settings` (Mac이라면 Preferences) 에 들어가서 `Apperance & Behavior` 하위에 있는 `Path Variables` 메뉴에 들어갑니다.

![image-20220124153945734](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/pathVariable.assets/image-20220124153945734.png)

그랬더니, 위의 MACRO에서 봤던 두개의 환경변수가 저장 되어 있습니다. 여기에 한번 원하는 데이터를 추가 해 보겠습니다.

`+` 버튼을 클릭 해서 Add Variable에 원하는 값을 입력 해 줍니다.

![image-20220124160307544](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/pathVariable.assets/image-20220124160307544.png)

그러고 `OK` 버튼을 누르면

![image-20220124160347641](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/pathVariable.assets/image-20220124160347641.png)

> 위에 보이는 것처럼 DPROXY 변수가 추가 된 것이 확인 됩니다.

이제 `OK`를 클릭 하고 나와서, 다시 Application 설정에 들어가 봅니다.

![image-20220124161056208](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/pathVariable.assets/image-20220124161056208.png)

> 이번에는 Macros에 방금 입력한 DPROXY가 추가 된 것을 볼 수 있습니다.

`Insert ` 버튼을 눌러 보면

![image-20220124161130038](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/pathVariable.assets/image-20220124161130038.png)

뭔가 살짝 찝찝하게 값이 그대로 입력되는게 아닌, 환경변수 형태로 들어와 있는걸 볼 수 있습니다.

그러면 이제 정말 프록시가 잘 설정 되는지 확인 해 보겠습니다. Terminal을 열고

```zsh
ssh -D localhost:9999 프록시접속할ip주소
```

를 실행해 Dynamic Proxy 연결을 수립 한 후에 아래의 코드를 실행 해 보겠습니다.

**ProxyWithArgument.java**

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

코드 실행 결과 프록시 연결을 한 ip 주소가 출력 되는 것을 확인 했습니다!

정상적으로 VM Argument 전달 되었네요.

## 마치며

이상으로 IntelliJ IDEA의 Path Variable 설정을 Run/Debug Configurations에서 활용하는 방법에 대해 알아보았습니다.

테스트를 할 때 상황에 따라 환경 변수를 다르게 해야 할 필요가 있는데 그때마다 값을 기억해서 입력 하는 것도 좋겠지만 일정 길이 이상의 문자를 항상 외우고 다니는건 말처럼 쉬운일이 아닙니다.

그럴때, 몇개의 설정값들을 미리 저장해 두고 필요에 맞춰 사용한다면 많은 시간을 절약 할 수 있겠습니다.

심지어 IntelliJ IDEA에서의 Path Variables는 특정 프로젝트에 속하는 값이 아니기 때문에 프로젝트를 생성 할 때마다 새로 설정 해 줄 필요도 없습니다. 한번만 입력 해 두면 개발중인 모든 프로젝트에서 활용 할 수 있습니다.

이상입니다. 읽어주셔서 고맙습니다.
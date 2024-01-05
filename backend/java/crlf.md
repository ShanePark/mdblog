# [Java] Carriage return 그리고 Line feed

## Intro

### HWPX 이야기

이 사건은 사용자가 요청하는 정보에 해당하는 HWPX 파일을 서버에서 실시간으로 만들어서 사용자가 다운로드 받을 수 있도록 제공하는 기능을 구현하던 중 발생했다.

`hwpx`파일은 엑셀처럼 기본적으로 zip 파일로 되어 있어, 파일 확장자를 `.zip`으로 변경한 뒤에 압축을 풀면 내부 파일 구조를 확인 할 수 있다. Contents 폴더, META-INF 폴더, Preview 폴더가 있는데 그 중 Contents 폴더의 `section0.xml` 이 내용물을 담고있는 핵심 파일이다. 

그런데 문제는 마음대로 바꾸고 싶은대로 바꾸면 한컴오피스에서 파일이 열리지 않았다. 줄바꿈, 탭문자등이 들어가면 어김없이 프로세스가 죽어버린다. 그들이 정의해놓은 포맷에 맞게 기입해야만 한다.

> 예) 줄바꿈 문자는 `<hp:lineBreak />` 로 표기해야만 한다.

심지어 표를 생성할떄는 `rowCnt` 라는 속성을 테이블 상위에 걸어두어서, 문서를 열 때 해당 카운트가 일치하지 않아도 바로 죽여버는게 참으로 냉혹하다.

### 발단

그래서 문제 해결을 위한 방안으로, xml을 생성 할 때 발생하는 줄바꿈 문자를 싹 다 없애버리기로 했다. 어차피 사람이 읽을게 아니고 컴퓨터가 읽을것이기 때문이다.

```
xml = xml.replaceAll("\n", " ");
```

> 그렇다 우리에겐 아주 강력한 replaceAll이 있다. 

비록 자바에서의 [String Immutable](https://shanepark.tistory.com/330) 정책과 정규표현식 처리로 인해 효율성은 눈물을 흘리겠지만, xml파일이 그정도로 커질 일은 전혀 없기 때문에 성능이슈가 발생하지 않을것으로 판단하고 강행했다.

- 그런데 분명 replaceAll을 했는데 여전히 텍스트에 줄바꿈이 포함되어있는지 에러가 발생했다. 

더 황당한건 내가 사용하고 있는 우분투 데스크탑의 한글리더에서는 문제 없이 생성한 hwpx 파일을 읽어내는데, 윈도우에서는 똑 같은 파일을 열었을때 한컴오피스가 죽었다.

> 심지어 개발 끝났다고 신나있다가 나중에야 이 사실을 알았다. 리눅스에서 개발하며 겪는 몇 안되는 단점 중 하나다

 이 이유를 디버깅하는데 제법 많은 시간을 쏟았는데, 매번 생성한 hwpx 파일을 열때마다 Windows를 사용해야 하니 VirtualBox 내에서 혹은 별도의 컴퓨터에서 파일을 확인해야 했기 때문이다. 일단 죽긴 죽는데 줄바꿈때문에 죽는건지 다른것때문인지도 알수가 없었다.

그래도 나중에는 우분투에 네이버 웨일 브라우저를 설치해 한글 파일을 읽게 할 때 윈도우에서 열었을때와  똑같이 반응하는걸 찾아내어 좀 더 쉽게 테스트 할 수 있었는데, 놀랍게도 결국 찾아낸 원인은  CRLF 였다. 

간단한 예제 코드로 해당 상황을 재현해보도록 하겠다.

## Hello CRLF

### LF 와 CR

개행을 표현하는 스타일이 OS에 따라 다른데, MS Windows, DOS 등 에서는 `\r\n`을 사용하고 그 외 최신 맥OS나 유닉스에서는 POSIX 스타일인 `\n`을 사용한다. 각각 유니코드에서는 10번째, 13번째에 위치해있다.

- `U+000A` Line feed (LF)
- `U+000D` Carriage return (CR)

![image-20240105161352080](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/crlf.assets/2.webp)

> https://en.wikipedia.org/wiki/List_of_Unicode_characters

### 사전 준비

일단 해당 코드를 포함한 자바프로젝트의 패키지 구조인데, 테스트를 위한 CarriageReturn 클래스를 생성하고, resources의 동일한 경로에 posix_style 및 windows_style의 텍스트 파일을 생성했다. 샘플파일을 관리하려고 resources에 넣었지만 한번 테스트 하고 말거라면 그냥 아무데나 파일을 작성해둬도 된다.

![image-20240105160436192](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/crlf.assets/1.webp)

이제 각 스타일로 작성한 텍스트 파일을 생성해야하는데, Visual Studio Code같은 최신의 코드에디터를 사용한다면 End Of Line Sequence를 설정해서 간단하게 만들어낼 수 있다.

![image-20240105162110967](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/crlf.assets/3.webp)

> Visual Studio Code

![image-20240105162231065](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/crlf.assets/4.webp)

> Intellij IDEA

윈도우 스타일은 CRLF, POSIX 스타일은 LF로 각각의 텍스트 파일을 생성하였다.

![image-20240105162342121](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/crlf.assets/5.webp)

### 코드 작성

이제 문제의 상황을 재현하기 위한 코드를 작성해보았다.

```java

public class CarriageReturn {
    final static String POSIX_STYLE_FILENAME = "posix_style.txt";
    final static String WINDOW_STYLE_FILENAME = "window_style.txt";

    public static void main(String[] args) throws FileNotFoundException, URISyntaxException {
        compareRemoveLineFeed(POSIX_STYLE_FILENAME);
        compareRemoveLineFeed(WINDOW_STYLE_FILENAME);
    }

    private static void compareRemoveLineFeed(String fileName) throws URISyntaxException {
        Class<CarriageReturn> curClass = CarriageReturn.class;
        URL resource = curClass.getResource("./" + curClass.getSimpleName());
        File resourceDir = new File(resource.toURI());
        File file = new File(resourceDir, fileName);

        String textOriginal = readFileToString(file);
        String textAfterReplace = textOriginal.replaceAll("\n", "");

        System.out.printf("== 변경 전 텍스트 (%s) ==\n%s\n==================================\n\n", file.getName(), textOriginal);
        System.out.printf("== 변경 후 텍스트 (%s) ==\n%s\n==================================\n\n", file.getName(), textAfterReplace);
    }

    private static String readFileToString(File posixStyleText) {
        StringBuilder sb = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(posixStyleText);
             BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        ) {
            int read = br.read();
            while (read != -1) {
                sb.append((char) read);
                read = br.read();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

}
```

위의 코드를 실행해보면 다음과 같은 출력 결과가 나온다.

![image-20240105164326703](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/crlf.assets/6.webp)

출력 결과를 양옆에 두고 비교해보면 다음과 같다.

![image-20240105165102676](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/crlf.assets/7.webp)

이상한 점을 발견했는가?

- `posix_style.text`는 replaceAll 이라는 풍파를 얻어맞고 나서 원하는대로 깔끔한 한줄 텍스트가 되었다. 

- 그런데 window 스타일의 텍스트는 줄바꿈만 제거한게 아니고, 그냥 <u>첫 줄이 사라져버렸다.</u> 

뭔가 버그가 발생한것일까? 사라진 한줄을 찾아나서보자.

이번에는 텍스트의 길이를 함께 출력해보았다. 

```java
System.out.printf("== 변경 전 텍스트 (%s, 길이:%d) ==\n%s\n==================================\n\n", file.getName(), textOriginal.length(), textOriginal);
System.out.printf("== 변경 후 텍스트 (%s, 길이:%d) ==\n%s\n==================================\n\n", file.getName(), textAfterReplace.length(), textAfterReplace);
```

새로운 출력 결과는 아래와 같다.

![image-20240105165347279](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/crlf.assets/8.webp)

화면에 표시된 것 처럼, 줄바꿈이 제거되며 각각의 길이는 2씩만 줄어들었다. `window_style.txt`  에 있던 첫줄의 행방은 도대체 어디로 갔을까?

### 원인 분석

그 원인은 `\r` 이다. 계속 언급했던 것 처럼 윈도우에서는 줄바꿈으로 `\n`이 아닌 `\r\n`을 사용한다.

파일에서 텍스트를 읽어 올 때, 유니코드 값을 함께 출력해보도록 `readFileToString` 메소드에 다음의 코드 한줄을 추가하고 다시 코드를 실행해보았다.

```java
System.out.printf("read = %d, char = %c\n", read, (char) read);
```

![image-20240105165759391](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/crlf.assets/9.webp)

먼저 POSIX로 작성된 텍스트를 확인해보자.

![image-20240105165819835](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/crlf.assets/10.webp)

줄바꿈이 일어날 때, `.`을 의미하는 Full Stop(46) 이후에 유니코드 10 으로 줄바꿈이 이루어진다.

이번에는 Windows 쪽을 확인해보자.

![image-20240105165948208](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/crlf.assets/11.webp)

차이가 보이는가? Full Stop 이후에 13번, 10번이 줄바꿈을 협업으로 진행한다.

위에서 확인했던 것 처럼 각각 CR과 LF를 의미한다.

![image-20240105161352080](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/crlf.assets/2.webp)

그러면 CRLF를 대상으로 `replaceAll("\n", "");` 을 때려버린다면 무슨일이 일어날까?

Line Feed는 사라지고 Carriage return 만이 홀로 남게된다. 줄바꿈은 하지 않고 커서만 맨 앞으로 가서 같은 라인에 새로운 텍스트를 출력하다보니 먼저 작성한 텍스트는 사라지고 `\r` 이후에 작성한 글만이 살아남은 것이다.

정말 간단하게 해당 상황을 재현할 수 있는데 아래의 코드를 실행해보면 된다.

```java
System.err.println("임차인100명\r임대인");
```

![image-20240105170643858](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/java/crlf.assets/12.webp)

> 임대인이 임차인 100명을 몰아내고 그 자리를 차지하였다.

실제 타자기에서의 캐리지 리턴 이었다면 같은 줄에 글씨가 겹쳐서 작성되며, 100명 이라는 글자라도 살아남았겟지만 컴퓨터에서는 그냥 기존의 라인을 통째로 덮어버렸다. 

사실 우리가 찾고있는 `Windows 스타일의 텍스트 파일 입니다.` 라는 텍스트 한줄은 실제로는 사라진건 아니고 어딘가 보이지 않는 곳에서 "18층에 사람 있어요!" 를 소리없이 외치고 있었던 것이다.

이렇게 눈에 잘 보이지 않는 문제들은 디버깅 하기가 참 까다롭다.

## 해결

LineFeed 뿐만 아니고 Carriage return, 그리고 보너스로 탭문자까지 모두 처형해냈고 마침내 세입자들을 구해 낼 수 있었다.

```java
xml = xml.replaceAll("[\\t\\n\\r]+", " ");
```

끝.

**References**

- https://en.wikipedia.org/wiki/List_of_Unicode_characters
- https://en.wikipedia.org/wiki/Carriage_return
# CSV 포맷 소개 및 자바로 CSV 파일 작성 하기

## CSV 파일형식

### 소개

CSV(Comma Separated Values)는 몇 가지 필드를 단순히 쉼표(,)로 구분한 **텍스트 데이터 및 텍스트 파일** 입니다. 

장점으로는 표의 형태를 직관적으로 나타내는 간단한 형식이기 때문에 이해하기가 쉬우며, 소프트웨어로 처리하는 것 또 한 쉽습니다. 데이터에 `,` 가 포함되지 않았다면 간단한 코드 한 만으로도 구현이 가능 할 정도 입니다. 또한 쉼표만으로 구분하며 이스케이프 문자는 선택 사양이니 매우 경량입니다.

```
이름, 나이, 주소
Shane, 10, ThornHill
Jenny, 20, Devonport
Ann, 30, Queenstown
```

> 한눈에 보기에도 정말 간단합니다.

그 단점으로는 데이터에 쉼표가 포함된다면 곤란해 진 다는 것 입니다. 예를 들어 데이터중에 금액 데이터가 있어서 100,000,000원의 데이터가 들어갔다면, 나중에 해석할 때 다른 열로 취급되지 않도록 문자열을 모두 쌍따옴표로 감싸야 합니다. 쌍따옴표가 기존에 들어갔을 경우는 이스케이프 까지 시켜줘야 겠네요.

이처럼 데이터 오염에 대단히 취약한 포맷이라서 XML이나 JSON이 데이터 교환에는 많이 사용 되고 있는데요, 그럼에도 불구하고 데이터의 크기가 작다는 절대적인 장점으로 인해 CSV는 지금도 널리 사용됩니다.

### 사용

데이터베이스 클라이언트인 DBeaver에서 Import Data를 누르면 아래와 같은 창을 볼 수 있습니다.

![image-20211117153328375](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/cvs.assets/image-20211117153328375.png)

> 데이터를 CSV 파일이나 특정 Database table에서 추출 할 수 있습니다.

이전에 한번 엑셀에 있는 데이터를 DB로 저장하려고 하니, CSV 형태를 거쳐 저장을 해야 했던 기억이 있습니다. 

CSV 는 오래전부터 스프레드시트나 데이터베이스에서 많이 쓰인 파일 형식인데요, 그 세부적인 구현은 소프트웨어에 따라 다릅니다.

###  RFC 4180

그러다 2015년 10월에 기술표준 RFC 4180을 통해 CVS 파일 형식을 공식화 하고, 처리를 위한 MIME 타입인 "text/csv"가 정의 되었습니다. 하지만 여전히 그 해석은 응용프로그램에 따라 다릅니다. 아래는 RFC 4180 Standard의 일부 내용입니다.

- MS-DOS-style lines that end with (CR/LF) characters (optional for the last line).
- An optional header record (there is no sure way to detect whether it is present, so care is required when importing).
- Each record *should* contain the same number of comma-separated fields.
- Any field *may* be quoted (with double quotes).
- Fields containing a line-break, double-quote or commas *should* be quoted. (If they are not, the file will likely be impossible to process correctly.)
- *If* double-quotes are used to enclose fields, then a double-quote in a field *must* be represented by two double-quote characters.

보다 자세한 RFC 4180은 아래의 링크를 통해 확인 할 수 있습니다.

![image-20211117153947745](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/cvs.assets/image-20211117153947745.png)

> https://datatracker.ietf.org/doc/html/rfc4180

## Java로 CSV 파일 작성

CSV 작성을 돕기 위한 몇가지 자바 라이브러리가 있습니다. 대표적인 두개의 라이브러리를 살펴보겠습니다.

### Apache Commons

> `org.apache.commons.lang3.StringEscapeUtils`

Apache Commons Lang은 CSV, EcmaScript, HTML, Java, Json, XML 등의 문자열 escape 혹은 unescape를 위한 몇가지 특별한 클래스들을 포함하고 있습니다. lang3를 이용 해 보겠습니다.

- Gradle

```groovy
// https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
implementation group: 'org.apache.commons', name: 'commons-lang3', version: '3.12.0'

```

일단 먼저 Dependency를 추가 해 주고..

![image-20211117160657306](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/cvs.assets/image-20211117160657306.png)

StringEscapeUtils의 escapeCsv 메서드를 사용 하려고 하니 `@Deprecated` 되었다고 나오네요.

commons-text의 StringEscapeUtils를 사용하라고 알려주네요. 바로 의존성을 바꿔보겠습니다.

- Gradle

```groovy
// https://mvnrepository.com/artifact/org.apache.commons/commons-text
implementation 'org.apache.commons:commons-text:1.9'

```

- Maven

```xml
<!-- https://mvnrepository.com/artifact/org.apache.commons/commons-text -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-text</artifactId>
    <version>1.9</version>
</dependency>

```

의존성을 추가 하고 다시 코드 어시스트를 받아 보면,

![image-20211117161122400](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/cvs.assets/image-20211117161122400.png)

이제 `org.apache.commons.text`에 있는 StringEscapeUtils를 사용 하면 될 것 같습니다.

<br><br>

`escapeCsv() 메서드` 테스트를 위한 자바 코드

```java
package com.tistory.shanepark.file.csv;

import org.apache.commons.text.StringEscapeUtils;

public class CsvEscape {

    public static void main(String[] args) {
        String str = "100,000,000원을 \"김두한\"에게 전달해주세요.";
        String escape = StringEscapeUtils.escapeCsv(str);
        System.out.println(escape);
    }
}

```

![image-20211117161453623](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/cvs.assets/image-20211117161453623.png)

> 실행 결과

텍스트 양쪽을 double quotation marks 로 묶고, 원래 있던 쌍따옴표는 두개를 쓰는 방법으로 이스케이프 하는게 확인됩니다.

<br><br>

![image-20211117161726400](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/cvs.assets/image-20211117161726400.png)

온라인에서 CSV String을 Escape / Unescape 해주는 툴을 확인 하니 같은 결과가 보입니다.

### OpenCSV

하지만 OpenCSV를 사용하면 escape에 대한 고려를 할 필요 없이 바로 컨텐츠를 읽거나 작성 할 수 있습니다. 굳이 Apache Commons Text 라이브러리를 불러 오지 않고도 CSV 작성을 편하게 할 수 있습니다.

- Gradle

```groovy
// https://mvnrepository.com/artifact/com.opencsv/opencsv
implementation group: 'com.opencsv', name: 'opencsv', version: '5.5.2'

```

- Maven

```xml
<!-- https://mvnrepository.com/artifact/com.opencsv/opencsv -->
<dependency>
    <groupId>com.opencsv</groupId>
    <artifactId>opencsv</artifactId>
    <version>5.5.2</version>
</dependency>

```

<br><br>

의존성을 추가 했으니 바로 코드를 작성 해서 파일을 만들어 보겠습니다.

```java
package com.tistory.shanepark.file.csv;

import com.opencsv.CSVWriter;

import java.io.*;

public class CsvFileWrite {
    final static String folder = System.getProperty("user.home") + "/Documents";

    public static void main(String[] args) {
        File file = new File(folder, "test.csv");
        try (
                FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                CSVWriter writer = new CSVWriter(osw);
        ) {
            String[] row = {
                    "shane",
                    "10,000원",
                    "ThornHill"
            };
            writer.writeNext(row);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

```

특별한 건 없고, FileOutputStream을 열어서 그걸로 OutputStreamWriter를 열고, 또 그걸로 CSVWriter를 엽니다.

그리고는 String 배열을 바로 `writeNext()`에 전달해서 작성 하면 파일에 바로 작성이 되는데요, 

![image-20211117163704893](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/cvs.assets/image-20211117163704893.png)

보이는 것 처럼 CSVWriter는 String 배열(배열 하나가 하나의 row) 하나 하나를 한 라인으로 추가 할 수도 있지만, writeAll 메서드를 이용해서 String배열의 List, Iterable 등을 한번에 작성 할 수도 있습니다. 심지어 ResultSet을 바로 기록 할 수도 있네요.

![image-20211117165431005](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/cvs.assets/image-20211117165431005.png)

두번째 파라미터로 받는 boolean은 무엇인지 확인 해 보니, 항상 모든 value에 쌍 따옴표를 붙일지, 아니면 필요할 때만 붙일지에 대한 옵션 입니다. 장단점이 있겠지만 저는 경량화 보다 무결성에 힘을 주기 위해 true로 하려고 합니다.

<br><br>

```java
package com.tistory.shanepark.file.csv;

import com.opencsv.CSVWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvFileWrite {
    final static String folder = System.getProperty("user.home") + "/Documents";

    public static void main(String[] args) {
        File file = new File(folder, "test.csv");
        try (
                FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                CSVWriter writer = new CSVWriter(osw);
        ) {
            List<String[]> list = new ArrayList<>();
            list.add(new String[]{
                    "shane",
                    "10,000원",
                    "ThornHill"
            });
            list.add(new String[]{
                    "Jenny",
                    "10,000,000",
                    "Queenstown"
            });
            writer.writeAll(list, true);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

```

![image-20211117164126930](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/cvs.assets/image-20211117164126930.png)

> 의도한 대로 test.csv 파일이 잘 작성 되었습니다.

### 파일에 추가하기

이미 작성된 CSV 파일에 새로운 내용을 추가만 하는 경우에는 어떻게 해야 할까요?

이때는, FileWriter를 활용하면 파일의 끝에서 부터 작성 할 수 있습니다.

![image-20211117165704619](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/cvs.assets/image-20211117165704619.png)

FileWriter를 생성 할 때, 두번째 파라미터를 확인 해 보니 append 여부 인데요, 여기에 꼭 true 로 해 두어야 파일의 끝에 새로운 내용을 추가 하며 작성 하게 됩니다. 바로 코드를 작성 해서 테스트 해 보겠습니다.

<br><br>

```java
package com.tistory.shanepark.file.csv;

import com.opencsv.CSVWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvAddToFile {
    final static String folder = System.getProperty("user.home") + "/Documents";

    public static void main(String[] args) {
        File file = new File(folder, "test.csv");
        try (
                FileWriter fileWriter = new FileWriter(file, true);
                CSVWriter writer = new CSVWriter(fileWriter);
        ) {
            List<String[]> list = new ArrayList<>();
            list.add(new String[]{
                    "Michael",
                    "20,000원",
                    "Devonport"
            });
            list.add(new String[]{
                    "Kohei",
                    "20,000,000",
                    "Ogaki"
            });
            writer.writeAll(list, true);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

```

이전과 별 차이가 없지만, 기존의 파일을 불러 와서 그 끝에 작성한다는 차이만 있습니다. 

이제 실행을 해 보면

![image-20211117165833202](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/cvs.assets/image-20211117165833202.png)

의도한 대로 기존의 내용에 Michael과 Kohei가 새로 추가 되었습니다.

![image-20211117170308956](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/cvs.assets/image-20211117170308956.png)

> 일반 Text Editor에서 파일을 열어 보면 이와 같습니다.

## 글 마침

어렵지 않게 간단한 코드들만으로 CSV 파일을 작성 해 보았습니다.

파일을 읽을 때는 반대 순서로 `CSVReader` 를 오픈 해서 `.readNext()` 혹은 `.readAll()` 메서드를 호출 하면 String 배열을 받아 오게 됩니다.

자세한 내용은 http://opencsv.sourceforge.net/#reading_into_an_array_of_strings 를 확인 해 보시면 간단한 매뉴얼이 있으니 필요한 분들은 참고 해 주시면 되겠습니다.

이상으로 CSV에 대한 포스팅을 마치겠습니다.
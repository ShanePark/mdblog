# Java) 클립보드에 텍스트 복사 / 붙여넣기

## Intro

저에게 필요한 기능이 있어 쉘에서 간단하게 만들어 보려고 했는데.. shell script에는 제가 익숙하지가 않아서 자바로 프로그램을 작성 하고 alias로 등록 해서 사용하기로 했습니다. 

해당 기능을 구현하며 클립보드로 텍스트를 복사해내거나 클립보드에 있는 텍스트를 꺼내오는 기능을 사용 해야 했는데 굉장히 생소해서 해당 내용을 정리 해 보았습니다.

> 복사, 붙여넣기 코드만 확인 하실분은 복사의 경우 `Copy to the clipboard` 부분의 코드를, 붙여넣기가 필요한 분은 맨 아래의 `argument 없이 실행` 부분의 코드만 확인 하시면 됩니다.

## Requirements

저는 LeetCode 문제풀이를 할 때 테스트 코드 작성이나 디버깅등을 위해 IDE로 옮겨와 작업을 합니다.

매번 적당한 클래스명을 만들어내는게 번거롭기는 하지만, 적당히 문제번호와 문제명을 조합한 클래스를 만들어 작업하고 있습니다.

![image-20220226155640937](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/clipboard.assets/image-20220226155640937.png)

그런데 매번 일일히 공백을 지우고, 클래스 이름이 숫자로 시작할 수 없기 때문에 맨 앞에 Q를 넣고, `.` 같은 사용 할 수 없는 문자들을 제외해 클래스명을 만드는건 여간 귀찮은 일이 아닙니다. 그래서 문제명을 넣으면 알아서 적당한 클래스 이름을 만들고 클립보드에 복사해 주는 자바 파일을 작성 해 보려고 합니다.

## getClassName()

![image-20220226155239543](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/clipboard.assets/image-20220226155239543.png)

보통 Leetcode 문제를 하나 열면, 위에 보이는 것 처럼 {`문제번호` `.` `제목`}의 규칙으로 타이틀이 적혀 있는데요. 이를 기준으로 만들어 보았습니다. 후에 Terminal을 통해 Shell 명령으로 실행 할 예정이기 때문에,  `.split(" ") `은 굳이 하지 않고 args로 바로 받아서 처리 합니다.

```java
public String getClassName(String[] args) {
  StringBuffer sb = new StringBuffer("Q");
  for (String s : args) {
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (i == 0 && 'a' <= c && c <= 'z') {
        c -= 'a' - 'A';
      }
      if (Arrays.binarySearch(invalid, c) < 0) {
        sb.append(c);
      }
    }
  }
  return sb.toString();
}
```

그러면 이제 처리결과의 input과 output은 아래와 같습니다.

- input: `"1315. Sum of Nodes with Even-Valued Grandparent".split(" ")`
- output: `QQ1315SumOfNodesWithEvenValuedGrandparent`

## Copy to the clipboard

이제 클래스 명을 만들었으면, 클립보드에 알아서 복사 되도록 코드를 작성 해 보겠습니다.

java.awt 패키지에 있는 Clipboard 클래스를 활용 합니다. AWT 는 Abstract Window Toolkit의 약자 입니다.

```java
String className = getClassName(args);
System.out.println("Class name : " + className);
System.out.println("class name has been copied to your Clipboard!");

Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
StringSelection stringSelection = new StringSelection(className);
clipboard.setContents(stringSelection, null);
```

이렇게 하면 새로 만든 클래스 명을 콘솔에 찍어 보여준 후, 클립보드에 알아서 복사 합니다.

코드 전문은 아래와 같습니다.

```java
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Arrays;

public class ClassNameFactory {

    private static Character[] invalid = {'-', '.'};

    public static void main(String[] args) {
        Arrays.sort(invalid);
        String className = getClassName(args);

        System.out.println("Class name : " + className);
        System.out.println("class name has been copied to your Clipboard!");

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(className);
        clipboard.setContents(stringSelection, null);
        return;
    }

    public static String getClassName(String[] args) {
        StringBuffer sb = new StringBuffer("Q");
        for (String s : args) {
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (i == 0 && 'a' <= c && c <= 'z') {
                    c -= 'a' - 'A';
                }
                if (Arrays.binarySearch(invalid, c) < 0) {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }
}

```

혹시 binarySearch를 사용하려면 꼭 배열을 오름차순으로 정렬 해 주어야 합니다. 

## Execution

### 테스트

이제 한번 작성한 파일을 실행 해 보겠습니다.

```zsh
 java /Users/shane/Documents/dev/ClassNameFactory.java 1315. Sum of Nodes with Even-Valued Grandparent
```

![image-20220226161202645](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/clipboard.assets/image-20220226161202645.png)

실행 즉시 새로 만든 클래스 이름을 보여 주었고, 클립보드에 해당 내용이 복사 되었습니다.

> java 11 부터는 컴파일을 하지 않고도 `.java` 파일을 바로 실행할 수 있는데요 JEP 330 에서 소개된 기능 입니다. https://openjdk.java.net/jeps/330
>
> `java HelloWorld.java` 는  `javac -d HelloWorld.java java -cp helloWorld` 명령과 같다고 합니다. 
>
> JDK8을 사용하고 있다면, javac 로 컴파일을 먼저 한 뒤에 classPath를 명시해서 실행 해주세요.
>
> `java -cp ~/Documents/dev ClassNameFactory` 

### alias 설정

`~/.zshrc` 파일에 leet 이라는 이름으로 alias를 설정 하였습니다.

JDK11

```zsh
alias leet='java ~/Documents/dev/ClassNameFactory.java'
```

JDK8

```zsh
alias leet="java -cp ~/Documents/dev ClassNameFactory"
```

이제는 `leet {문제타이틀}` 명령을 하면

![image-20220226161417110](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/clipboard.assets/image-20220226161417110.png)

바로 클래스 이름을 보여주고, 클립보드에 복사도 해주도록 했습니다.

다음번에는 아에 IntelliJ IDEA 플러그인으로 만들어서, 릿코드 문제 url을 입력 하면, 난이도별로 적당한 패키지에, 적절한 클래스 명으로 파일을 생성하고 문제풀이 및 테스트 까지 할 수 있도록 기본 템플릿을 작성 해 주도록 만들어 봐야겠습니다.

### argument 없이 실행

마지막으로..

굳이 leet 뒤에 argument를 붙이지 않고, Clipboard에만 릿코드 타이틀을 복사 해 두면 알아서 클립보드에서 꺼내서 만들고, 클립보드 데이터가 올바르지 않거나 없을 경우에는 사용자에게 입력을 받도록 코드를 조금 수정 해 보았습니다.

여기에서는 `clipboard.getContents`를 이용해 클립보드에 붙여 넣는 기능 도 사용합니다.

```java
package shane.leetcode.util;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class ClassNameFactory {

    public static Character[] invalid = {'-', '.'};
    public static String regex = "[0-9]+\\..*";

    public static void main(String[] args) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        if (args == null || args.length == 0) {
            Transferable contents = clipboard.getContents(clipboard);
            if (contents != null) {
                try {
                    String pasteString = ((String) contents.getTransferData(DataFlavor.stringFlavor)).trim();
                    if (pasteString.matches(regex)) {
                        args = pasteString.split(" ");
                    }
                } catch (UnsupportedFlavorException | IOException e) {
                    System.out.println("ClipBoard Data is invalid.");
                }
            }
        }

        if (args == null || args.length == 0) {
            args = getTitleInput();
        }

        String className = getClassName(args);

        System.out.println("Class name : " + className);
        System.out.println("class name has been copied to your Clipboard!");

        StringSelection stringSelection = new StringSelection(className);
        clipboard.setContents(stringSelection, null);

    }

    private static String[] getTitleInput() {
        System.out.println("Leetcode title: ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().split(" ");
    }

    public static String getClassName(String[] args) {
        StringBuffer sb = new StringBuffer("Q");
        for (String s : args) {
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (i == 0 && 'a' <= c && c <= 'z') {
                    c -= 'a' - 'A';
                }
                if (Arrays.binarySearch(invalid, c) < 0) {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }
}

```

이상입니다. 
# [인텔리제이] 한글이 들어간 파일 모두 찾기

## Intro

프로젝트에서 국제화 작업을 진행이 대부분 마무리되어, 이제는 모든 뷰단에서 완전하게 하드코딩된 한글 메시지가 제거가 되었는지 확인이 필요 했습니다. 눈에 보이는 페이지를 하나 씩 일일이 찾아서 변경을 했지만, 거의 완료가 되고 나서도 특정 이벤트에만 등장하는 숨겨진 텍스트들이 꽤나 있었습니다.

이때는 정규식 표현을 활용하여 손쉽게 모든 파일에서 한글이 입력된 부분만 찾아낼 수 있습니다.

## 한글 찾기

### 파일에서 찾기

`Shift` 키를 연속으로 두 번 누르고 `Find in Files` 를 검색 하시면, 단축키를 찾아 낼 수 있습니다.

Linux / Windows 에서는 `Ctrl+Shift+F` 키 이며, Mac 에서는 Ctrl 키 대신 커맨드 키가 들어갑니다. 

![image-20221115105627804](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/find-korean.assets/image-20221115105627804.png)

해당 단축키를 입력 해 보면

![image-20221115110755169](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/find-korean.assets/image-20221115110755169.png)

위에 보이는 것과 같은 검색 화면이 열립니다.

### 정규식 검색

![image-20221115105827149](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/find-korean.assets/image-20221115105827149.png)

우측상단에 보면 `.*` 아이콘을 가진 Regex 버튼이 있습니다. 토글 해 주면 정규식 검색이 가능합니다.

후에 아래의 검색어를 넣고 검색 해 봅니다.

```
[가-힣]+
```

![image-20221115105943208](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/find-korean.assets/image-20221115105943208.png)

> 보이는 것 처럼 모든 파일에서 한글이 들어간 부분들을 검색해서 보여 줍니다.

자음과 모음을 조합하지 않고 `ㅎㅎ` 혹은 `ㅠㅠ` 처럼 자음이나 모음만 들어간 한글도 검색을 하려면 정규식을 아래와 같이 변경 해 주셔야 합니다.

```
[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]
```

### 파일명 필터

특정 확장자로 파일명을 제한 할 수도 있습니다.

특히, 주석에 한글이 많을 때에는 java 코드를 제외하고 html 파일과 같은 뷰단에서만 검색을 하기에 용이 합니다.

![image-20221115110539921](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/find-korean.assets/image-20221115110539921.png)

> 우측상단의 File mask를 체크 하고 원하는 파일 명 필터를 걸어 줍니다.

이상입니다.
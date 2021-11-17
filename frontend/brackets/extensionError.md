# Brackets) Unable to access the extension registry. Please try again later. 에러 해결

## 문제

Brackets 에서 플러그인을 설치하려고 하는데, Available에 접속이 되지 않았습니다. 구글에 검색해보니 Adobe가 망했다는 등 말도 안되는 말이 많았는데요, 어쨌든 플러그인을 설치 해야 하는 상황이었기 때문에 문제를 해결 해야 했습니다.

![image-20211117212343400](https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/brackets/extensionError.assets/image-20211117212343400.png)

## 원인

최근 설치 파일에서 extention manager 부분의 기능이 제대로 동작하지 않고 있다고 하는데요, Adobe의 새로운 도메인이 SSL 구현으로 인해서 정상적으로 이전이 되지 않아서 생기는 문제 입니다.

## 해결

config.json 파일을 편집해서 간단하게 에러를 해결 할 수 있습니다.

단계별로 사진과 함께 올릴테니 천천히 따라하시면 누구든 어렵지 않게 해결 하실 수 있습니다.

### 1. config.json 파일 찾기

![image-20211117213043000](https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/brackets/extensionError.assets/image-20211117213043000.png)

> 일단 Applications 를 열어서 Brackets이 있는 위치로 찾아 갑니다.

![image-20211117213131422](https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/brackets/extensionError.assets/image-20211117213131422.png)

> Brackets 아이콘을 우클릭 하면 Show Package Contents 메뉴가 있습니다. 클릭해줍니다.

![image-20211117213200224](https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/brackets/extensionError.assets/image-20211117213200224.png)

> Contents 폴더에 가서

![image-20211117213210103](https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/brackets/extensionError.assets/image-20211117213210103.png)

> www 폴더에 들어갑니다.

![image-20211117213217764](https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/brackets/extensionError.assets/image-20211117213217764.png)

> config.json 파일을 찾았습니다.

### 2. config.json 파일 수정

해당 파일을 열어 줍니다. 아마도 Brackets 가 기본 에디터로 되어 있을텐데, 어느 에디터로 변경해도 상관 없습니다.

18번 라인의 `extension_registry`가 사진 처럼 s3.amazonaws 로 시작하는 주소로 되어 있을 텐데요.

![image-20211117213343798](https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/brackets/extensionError.assets/image-20211117213343798.png)

<br><br>

해당 내용을 `http://registry.brackets.s3.amazonaws.com/registry.json` 로 변경 해 줍니다.

그러면 18번 라인이 아래와 같이 되어야 합니다.

```json
"extension_registry": "http://registry.brackets.s3.amazonaws.com/registry.json",

```

이번에는 바로 그 아래 19번 라인의 `extention_url` 도 변경 해 주어야 합니다. `http://registry.brackets.s3.amazonaws.com/{0}-{1}.zip` 로 변경 해 줍니다.

<br><br>

그러면 변경 후의 18번, 19 번 라인이 아래와 같이 됩니다.

```json
"extension_registry": "http://registry.brackets.s3.amazonaws.com/registry.json",
"extension_url": "http://registry.brackets.s3.amazonaws.com/{0}-{1}.zip",
        
```

![image-20211117213836788](https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/brackets/extensionError.assets/image-20211117213836788.png)

<br><br>

이제 변경 사항을 저장 하고 나서 Brackets를 새로 켜 줍니다.

아까처럼 플러그인 메뉴를 누르면 이번에는

![image-20211117213936096](https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/brackets/extensionError.assets/image-20211117213936096.png)

> 정상적으로 목록이 잘 뜹니다!

![image-20211117214014762](https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/brackets/extensionError.assets/image-20211117214014762.png)

> Beautify 를 설치 해 보니 잘 설치도 됩니다.

Brackets 에서는 Beautify 플러인을 설치 해야만 코드 자동 정렬을 할 수 있습니다.

그 단축키는 `커맨드 + 쉬프트 + L` 입니다.

즐거운 코딩 되세요!

 

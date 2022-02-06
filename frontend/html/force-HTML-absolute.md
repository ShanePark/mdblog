# HTML) href 속성의 html 링크 절대 경로로 강제하기

## Intro

얼마전 프로젝트를 진행 하던 중에 하나 난관에 부딪친 일이 있었습니다.

유저가 URL을 입력 하면, DB에 저장 해 두었다가, 필요 할 때 해당 URL 주소로 연결되는 링크를 만들어 주는 기능을 만들고 있었는데..

테스트를 진행 하다가 주소로 제대로 연결이 되지 않는 문제가 있었습니다.

`https://` 라고 프로토콜을 정확하게 입력 하면 연결에 문제가 없었지만, 프로토콜을 생략하고 www.naver.com 과 같은 값을 넣었을 때는 절대경로가 아닌 상대경로로 연결을 시도 하는 문제가 있었습니다. 간단하게 해결 하는 문제지만 생각보다 검색했을 때 해결방법이 나오지 않아 고생을 조금 했지만 결국 고칠 수 있었습니다.

## 문제 재연

먼저 해당 문제를 똑같이 재연 해 보겠습니다.

이를 위해 아주 간단한 스프링 부트 프로젝트를 만들었습니다. 컨트롤러에서는 주소 전달만 하니 사실 html 파일만 참고하시면 됩니다.

### controller

```java
package shane.hreftest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

    @RequestMapping("/")
    public String main(Model model) {
        model.addAttribute("link", "www.naver.com");
        model.addAttribute("link2", "https://shanepark.tistory.com");
        return "main";
    }
}

```

root 페이지 요청을 하면, link에 `www.naver.com`을, link2에는 `https://shanepark.tistory.com`이라는 String value를 모델에 담아 main.html 파일을 렌더링 합니다.

### main.html

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
  <meta charset="UTF-8">
  <title>Title</title>
  <style>
    a {
      font-size: 3em;
    }
  </style>
</head>
<body>
<a th:href="${link}" th:text="${link}"></a><br/>
<a th:href="${link2}" th:text="${link2}"></a>
</body>
</html>
```

html 렌더링은 thymeleaf로 했습니다. 사실 프로젝트에서는 다른 템플릿엔진을 사용했었기 때문에 thymeleaf 에서는 해당 문제가 해결되었으면 어떻게 하나 싶었는데 다행(?)히도 같은 문제가 발생 했습니다.

### 페이지 확인

![gif](https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/html/force-HTML-absolute.assets/gif.gif)

마우스를 올렸을 때, 이동할 주소가 왼쪽 하단에 나오는데요.. 보이는 것 처럼 `www`로 시작하는 주소는 상대경로로 되어 버려서

![image-20220130184841294](https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/html/force-HTML-absolute.assets/image-20220130184841294.png)

클릭하면 이렇게, `localhost:8080/www.naver.com`이라는 요상한 주소로 이동을 시도합니다.

정확히 제가 겪었던 문제 입니다.

## 해결

여러 가지 방법으로 해결 시도를 해 보았는데요, 생각보다 간단하지는 않았습니다.

### 1차시도 - 주소앞에 `//`붙이기

```html
<a th:href="${'//'+link}" th:text="${link}"></a><br/>
<a th:href="${'//'+link2}" th:text="${link2}"></a>
```

href 속성값의 맨 앞에 `//`을 붙여서 해결을 시도 해 보았습니다.

![gif2](https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/html/force-HTML-absolute.assets/gif2.gif)

그랬더니 `www`로 시작하는건 문제가 해결 되었지만, `https://`가 붙어있던 링크는 `:`이 사라지면서 문제가 생겼습니다.

![image-20220130190521026](https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/html/force-HTML-absolute.assets/image-20220130190521026.png)

그래서 해당 링크를 클릭시에는 연결이 안되었습니다. 사실 회사에서 사용하던 템플릿에서는 `//`을 붙여서 해결이 되었는데 thymeleaf는 안되네요..

### 2차시도 - `http://` 붙이기

http로 만들어 둔 페이지를 https로 연결을 시도하는건 문제가 되지만, 대부분의 https 로 서비스하는 서버는 http로 요청이 들어와도 리다이렉트가 되기 때문에 `http://`를 일괄적으로 붙여 보았습니다.

```html
<a th:href="${'http://'+link}" th:text="${link}"></a><br/>
<a th:href="${'http://'+link2}" th:text="${link2}"></a>
```

테스트 결과는 1차 시도의 `//`을 붙였을때와 같았습니다. 사실 지금 localhost를 http 프로토콜로 요청했기 때문에, `//`를 붙이면 `http://`를 붙이는 것과 같기 때문에 이렇게 되는게 맞습니다..

### 3차시도 - 검증하기

변수를 검증해서 프로토콜 정보를 가지고 있을 경우에는 그대로 하고, 없을 때만 프로토콜 정보를 붙이게 해 보겠습니다.

Controller에서 미리 붙이거나(실제로는 비즈니스 로직쪽으로 빼 주어야 겠죠) 아니면 뷰단에서 템플릿엔진쪽에 로직을 넣을 수도 있겠네요. 두개 다 해보겠습니다.

1. Controller에서 검증

```java
package shane.hreftest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

    @RequestMapping("/")
    public String main(Model model) {
        String[] links = {"www.naver.com", "https://shanepark.tistory.com"};
        for (int i = 0; i < links.length; i++) {
            if (!links[i].contains("://")) {
                links[i] = "http://" + links[i];
            }
        }
        model.addAttribute("links", links);
        return "main";
    }
}

```

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
  <meta charset="UTF-8">
  <title>Title</title>
  <style>
      a {
          font-size: 3em;
      }
  </style>
</head>
<body>
<div th:each="link: ${links}">
  <a th:href="${link}" th:text="${link}"></a><br/>
</div>
</body>
</html>
```

`contains("://")` 로, 프로토콜 정보를 가지고 있는지 확인 하고 없으면 `http://`를 붙이게끔 해 보았습니다.

![gif3](https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/html/force-HTML-absolute.assets/gif3.gif)

거의 다 좋은데, 실제 입력한 `www.naver.com`이 아닌 http 프로토콜이 멋대로 붙어 출력되는게 마음에 들지는 않습니다.

2. view에서 검증

```java
package shane.hreftest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

    @RequestMapping("/")
    public String main(Model model) {
        String[] links = {"www.naver.com", "https://shanepark.tistory.com"};
        model.addAttribute("links", links);
        return "main";
    }
}

```

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
  <meta charset="UTF-8">
  <title>Title</title>
  <style>
      a {
          font-size: 3em;
      }
  </style>
</head>
<body>
<div th:each="link: ${links}">
  <a th:href='${link.contains("://") ? link : "//"+link }' th:text="${link}"></a><br/>
</div>
</body>
</html>
```

이번에는 컨트롤러에서는 데이터를 그냥 전달 하지만, view 단에서 contains 를 호출 해서, href 속성만 확인을 하고 `//`를 붙이도록 했습니다. 또한, text는 그냥 저장된 text를 변화 없이 그대로 출력하도록 해 보았습니다.

![4](https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/html/force-HTML-absolute.assets/4.gif)

원했던 결과가 나왔습니다. `//`를 붙인다면, 서버 페이지의 프로토콜 그대로 전달이 되기 때문에 https 프로토콜의 서버라면 https가 붙게 됩니다. 그렇기 때문에 `//` 대신 `http://`를 붙일 수도 있겠지만, 사실 요즘엔 http로 서비스 되는 사이트가 드물긴 합니다.

이상으로 href 속성의 주소로 이동할 때 상대 주소가 아닌 절대 경로로 보내는 방법을 알아 보았습니다.

 
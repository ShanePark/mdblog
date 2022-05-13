# Spring) 스프링의 "redirect:" 리다이렉트 처리

## Intro

Spring Framework 를 사용한다면 컨트롤러에서 리턴타입은 String으로 하고 view 이름 대신 "redirect:" 로 시작하는 문자열을 반환 하면 해당 주소로 리다이렉트를 시켜 줍니다.

"redirect:" 를 했는데 리다이렉트가 되지 않고 있다는 질문을 받아서 코드를 확인해보는데, 일단 redirect에 오타는 없었습니다. 그러면 혹시 @ResponseBody 어노테이션이 적용된건지 확인을 하는데 그렇지 않기에 코드를 맨 위로 올려보니 아니나 다를까 @RestController 어노테이션이 보였습니다.

이럴땐 간단하게 @Controller로 바꾸고 필요한 부분들에만 `@ResponseBody` 어노테이션을 작성 하거나 아니면 리턴타입을 ResponseEntity로 하는 방법이 있습니다. 그러다 스프링은 `redirect:` 라고 작성된 문자열을 어느 시점에서 읽고 리다이렉트를 시켜 주는지 궁금증이 생겨 디버깅 모드로 코드를 따라가며 확인 해 보니 제법 재밌었기에 글로 남겨 나누어 보려고 합니다.

## RestController 에서 리다이렉트 방법

```java
package com.tistory.shanepark.spring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedirectTestController {

    @GetMapping("/redirect")
    public String redirect() {
        return "redirect:/";
    }
}
```

일단 위에서 언급했던 문제를 먼저 해결 하고 가겠습니다.

코드를 일단 재현 해 보았습니다. `localhost:8080/redirect` 로 요청을 보내보면

![image-20220513152636744](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/redirect.assets/image-20220513152636744.png)

> Status: 200 OK 와 함께 redirect:/ 라는 문자열이 그대로 반환 됩니다.

이럴때는 @RestController나 @ResponseBody를 그대로 사용하려면 리턴타입을 ResponseEntity로 변경 해 주면 됩니다.

```java
@RestController
public class RedirectTestController {

    @GetMapping("/redirect")
    public ResponseEntity<?> redirect() {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/"));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }
}
```

> "/" 주소로 리다이렉트 하는 코드

![image-20220513154939747](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/redirect.assets/image-20220513154939747.png)

> 이후 `/redirect` 주소로 요청을 보내면 301 Status Code를 내며 문제없이 "/" 주소로 리다이렉트를 해 줍니다.

물론 ResponseBody가 아니라면 간단하게 `"redirect:/"` 으로 처리 할 수 있습니다.

```java
@Controller
@RequestMapping("/redirect")
public class RedirectTestController {

    @GetMapping("body")
    @ResponseBody
    public ResponseEntity<?> redirect() {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/"));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

    @GetMapping("prefix")
    public String redirectPrefix() {
        return "redirect:/";
    }

}
```

## 스프링의 redirect: 처리 과정

이번에는 스프링이 어떤 과정을 통해 "redirect:" 라고 작성된 부분을 리다이렉트로 인식하고, 리다이렉트를 보내주는지 확인 해 보도록 하겠습니다.

![image-20220513155529213](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/redirect.assets/image-20220513155529213.png)

> 브레이크 포인트를 찍고 디버깅을 시작합니다.

**ServletInvocableHandlerMethod.invokeAndHandle**(..)

![image-20220513155911559](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/redirect.assets/image-20220513155911559.png)

invokeForRequest의 결과로 받은 `returnValue` 객체에 저희가 반환한 `"redirect:/"`이 값으로 들어가 있는게 보입니다. 여기서부터 찾으면 되겠네요. 

여기서의 invokeAndHandle 메서드는 RequestMappingHandlerAdapter가 요청처리를 위한 수많은 선행 작업을 한 이후에 본격적으로 이루어 집니다.

![image-20220513160350839](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/redirect.assets/image-20220513160350839.png)

> 같은 메서드 안에서 handleReturnValue() 메서드를 호출 합니다.

**HandlerMethodReturnValueHandlerComposite.handleReturnValue(..)**

![image-20220513160448741](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/redirect.assets/image-20220513160448741.png)

> returnValue를 토대로 handler를 결정 한 뒤에, handleReturnValue를 해줍니다

**ViewNameMethodReturnValueHandler.handleReturnValue(..)**

![image-20220513160828734](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/redirect.assets/image-20220513160828734.png)

`returnValue` 결과를 토대로 ViewNameMethodReturnValueHandler가 핸들러로 결정 되었고, returnValue가 문자열인 걸 확인 한 뒤에, RedirectViewName(viewName)을 확인 후 true라면 mavContainer의 RedirectModelScenario값을 true로 변경 해 줍니다.

**ViewNameMethodReturnValueHandler.isRedirectViewName(String viewName)**

![image-20220513160954749](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/redirect.assets/image-20220513160954749.png)

> isRedirectViewName(String viewName) 메서드는 `viewName.startsWith("redirect:")` 로 리다이렉트에 관련된 문자열인지를 확인 합니다.

그러면 이제 mavContainer의 RedirectModelScenario가 true로 설정 된 상태 입니다.

이제 쭉쭉 진행해서 맨 처음의 invokeAndHandle가 호출된 지점으로 호출 스택이 돌려집니다.

**RequestMappingHandlerAdapter.invokeHandlerMethod(..)**

![image-20220513162226034](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/redirect.assets/image-20220513162226034.png)

> 위에 보이는 진한 파란색에서 invokeAndHandle 작업을 마쳤으며, 이제는 아래의 파란줄에서 invoke 된 결과를 토대로 ModelAndView를 만들어서 반환 하는 과정 입니다.

**RequestMappingHandlerAdapter.getModelAndView(..)**

![image-20220513171441058](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/redirect.assets/image-20220513171441058.png)

 isRequestHandled()를 확인 후, 이미 핸들되었다면 null을 반환하고, 그렇지 않다면 ModelAndView 객체를 만들게 되는데요, 미리 스포일러를 하자면.. @RequestBody 어노테이션이 작성된 경우에는 isRequestHandled()에서 걸려 ModelAndView가 null 상태로 반환됩니다.

![image-20220513171720682](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/redirect.assets/image-20220513171720682.png)

조금 더 내려와서 해당 mavContainer가 viewReference()가 아닌지를 확인 하는데요, 

![image-20220513171756138](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/redirect.assets/image-20220513171756138.png)

> mavContainer의 view 객체의 타입이 String이라면 true를 반환합니다.

![image-20220513172008118](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/redirect.assets/image-20220513172008118.png)

> mavContainer의 view는 `"redirect:/"` 인 상태기 때문에 mav.setView() 메서드를 그냥 건너뛰게 됩니다.

그렇게 전달된 ModelAndView는 이제 호출 스택을 다시 하나씩 치우고는 스프링의 얼굴마담인 **DispatcherServlet** 으로 전달됩니다. doDispatch() 를 진행중이었으니깐요.

![image-20220513172523081](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/redirect.assets/image-20220513172523081.png)

doDispatch 코드를 한 화면에 담아 봤는데요, 파란색 블록된 부분이 핸들러 어댑터로부터 ModelAndView를 획득 한 상태 입니다.

이제 마지막으로 ViewResolver로 부터 View를 획득해 View를 뿌려주는 일만 남았습니다.

![image-20220513173146172](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/redirect.assets/image-20220513173146172.png)

**DispatcherServlet.processDispatchResult(..)**

![image-20220513173314399](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/redirect.assets/image-20220513173314399.png)

Exception이 있는지 먼저 확인 한 후에 바로 획득한 ModelAndView로 렌더링을 시도합니다.

![image-20220513173652729](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/redirect.assets/image-20220513173652729.png)

> viewName이 null이 아니기 때문에 resolveViewName(..) 을 호출해서 View를 획득하는데요

**DispatcherServlet.resolveViewName(..)**

![image-20220513173909138](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/redirect.assets/image-20220513173909138.png)

4개의 viewResolver를 가지고 있고, 그 중 View를 획득 할 때 까지 resolveViewName(..)을 시도합니다.

제가 Pebble이라는 뷰 템플릿을 활용하는 다른 프로젝트에서 확인했을 때는 UrlBaseViewResolver가 뷰를 만들었었는데, 지금의 테스트용 프로젝트에서는 4개의 ViewResolver가 있고, ContentNegotiatingViewResolver를 제일 먼저 확인합니다.

**ContentNegotiatingViewResolver.resolveViewName(String viewName, Locale locale)**

![image-20220513174401734](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/redirect.assets/image-20220513174401734.png)

getCandiateViews를 호출 해서 viewName과 MediaType 등을 활용해 가능한 View 후보자들을 모아내고, 그 중 BestView를 반환하게 되어 있습니다.

**ContentNegotiatingViewResolver.getCandidateViews(..)**

![image-20220513174632967](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/redirect.assets/image-20220513174632967.png)

후보자들을 찾아내는 코드 입니다. 이제 가지고 있는 ViewResolver들을 활용해 viewName으로 후보자들을 찾는데요. 

그렇게 여러개의 ViewResolver들이 resolveViewName을 시도 하다가 결국 **AbstractCachingViewResolver** 에서 createView를 시도 하는데요

![image-20220513175508765](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/redirect.assets/image-20220513175508765.png)

> createView를 시도 합니다

본인은 캐싱관련한 내용을 담당하니 관련 부서로 일을 떠넘겨서 이번에는 UrlBasedViewResolver가 등판 합니다.

**UrlBasedViewResolver.createView(String viewName, Locale locale)**

![image-20220513175704371](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/redirect.assets/image-20220513175704371.png)

그리고 마침내 viewName이  REDIRECT_URL_PREFIX("redirect:") 로 시작하는지를 확인 하고는 RedirectView 객체를 생성 해서 반환 하게 되는 겁니다.

이제 다시 ContentNegotiatingViewResolver로 돌아와서, 여러개의 후보 View중에 bestView를 뽑는데

![image-20220513180043612](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/redirect.assets/image-20220513180043612.png)

> bestView 경연대회

![image-20220513180135960](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/redirect.assets/image-20220513180135960.png)

> RedirectView라니 우승은 따놓은 당상입니다. 특별 취급을 해주네요

![image-20220513180254998](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/redirect.assets/image-20220513180254998.png)

그렇게 힘들게 얻은 View를 가지고 render 처리 함으로서 길고 길었던 리다이렉트 요청 처리는 어느정도 마무리가 됩니다.

우리가 `"redirect:"` 라는 prefix 만으로 리다이렉트를 쉽게 해낼 수 있는건 그만큼 많은 개발자들의 노고가 뒤에 있었다는 사실을 알 수 있는 시간이었습니다.

감사합니다.








# IntelliJ IDEA) Devtools 사용해 HTML, CSS 등 정적자원 서버 재시작 없이 새로고침 

> 2022년 1월 1일 기준으로 새로 업데이트 합니다. 인텔리제이 버전이 2021.3으로 업데이트 되며 메뉴 구성이 약간씩 바뀌어서 거기에 맞춰 새로 변화 했습니다. 설정이 자주 변하다보니 인터넷에서 정보를 찾는 입장에서는 많이 불편 할 수 있지만 자세히 보면 결국 위치만 바뀐게 대부분이라서 최대한 변화에 맞춰 글을 업데이트 하는 방향으로 해보겠습니다. 
>
> 혹시 버전이 바뀌어 메뉴가 또 일치하지 않는 경우가 있다면 댓글로 달아주시면 바로 반영 하도록 하겠습니다. 

![image-20220101115042638](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/hot-reload.assets/image-20220101115042638.png)

> 아래의 문서를 참고 했습니다.
>
> https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.devtools

## Intro

스프링 부트 뿐만 아니라 웹 프로젝트를 할 때에 단순한 html 파일 혹은 css파일만 변경 하는데도 변경 상황을 반영 하려면 서버를 재 시작 해야합니다. html 파일이나 css파일에서 간단한 텍스트를 수정 할 때도 서버를 껐다 키는건 너무 번거롭기 때문에 개발 단계에서는 불편함을 겪게 되는데요..

Devtools를 활용 하면 굳이 서버를 껐다 켜지 않고도 정적 자원들만 업데이트 해줄 수 있습니다.

인터넷을 검색하면 옛날 정보라서 검색해서 체크하라던 내용도 없고, 하란 대로 해도 안되서 이것 저것 시도해보다가 힘들게 해결을 해서 정보를 나누려고 포스팅 합니다.

##  기본설정

### 1. Build Project automatically

Preferences 에서 Compirer에 Build Project automatically 를 체크해줍니다.

![image-20220101104536861](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/hot-reload.assets/image-20220101104536861.png) 

> 애플리케이션 실행 도중이어도 자동으로 재시작 할 수 있도록 해주는 설정 입니다.

### 2. Allow auto-make 

마찬가지로 Preferences 에서 Advanced Settings에 가면 제일 위에 Compiler 항목을 체크해줍니다.

> 구글에 검색 했을 때에는 registry에 가서 **compiler.automake.allow.when.app.running** 를 체크하라는 검색 결과가 상당히 많았는데, 지금의 최신 버전에서는 registry에 해당 내용은 없습니다. 아래의 경로로 변경되었습니다.

![image-20210825221549377](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/hot-reload.assets/image-20210825221549377.png) 

### 3. Run/Debug 설정

우측 상단에 있는 드롭박스 메뉴를 클릭하고 Edit Configurations 로 들어갑니다.

![image-20210825222055250](https://raw.githubusercontent.com/Shane-Park/markdownBlog/master/backend/spring/devtools.assets/image-20210825222055250.png)

 

그다음에 해당 Application을 선택 하고, Running Application Update Policies를 변경해줍니다.

![img](https://raw.githubusercontent.com/Shane-Park/markdownBlog/master/backend/spring/devtools.assets/image-20210825222201154.png)

> 과거에는 위와 같은 위치에 있었는데요,

![image-20220101104828624](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/hot-reload.assets/image-20220101104828624.png)

>  지금은 위치가 변경되었습니다. 우측의 Modify options를 클릭 해 줍니다.

![image-20220101105014494](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/hot-reload.assets/image-20220101105014494.png)

> On 'Update' action 을 클릭 합니다.

![image-20220101105051584](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/hot-reload.assets/image-20220101105051584.png)

Do nothing 으로 되어 있습니다. Update Classes and Resources로 변경 해 줍니다. 

이어서 On frame deactivation 설정도 변경 해 줍니다.

![image-20220101105910894](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/hot-reload.assets/image-20220101105910894.png)

> On 'Update' action 바로 아래 칸에 있습니다.

![image-20220101105958192](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/hot-reload.assets/image-20220101105958192.png)

> 설정이 완료되면 빨간색 표시한 부분이 추가 됩니다.

![image-20220101113413535](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/hot-reload.assets/image-20220101113413535.png)

> 사실 정적 파일만 Hot Reload를 시키려면 Update resources로만 해도 충분 합니다.

### Spring-boot-devtools 추가 

의존성에 spring-boot-devtools 를 추가해 줘야 합니다.

**maven**

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

 **Gradle**

```groovy
dependencies {
	compile("org.springframework.boot:spring-boot-devtools")
}
```

![image-20220101123908078](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/hot-reload.assets/image-20220101123908078.png)

의존성 변경 후에는 반드시 업데이트 해 주어야 합니다. 

### 설정 파일 변경

application.properties 에 아래 설정들을 추가 해 줍니다.

테스트용 프로젝트가 아니라면, application-dev.properties를 따로 만들어 dev 프로필을 사용 해 주세요.

```properties
spring.devtools.restart.enabled=false
spring.thymeleaf.cache = false
```

- restart.enabled는 개인적으로 선호하지 않아 false로 변경하였습니다. 기본 설정은 true 입니다. 변경이 있을 때 자동 재시작 하는 기능입니다.
- 개발할 떄 캐싱 기능도 불편을 초래 하기 때문에, 제가 사용하고 있는 템플릿엔진인 thymeleaf의 캐싱 기능도 false로 변경했습니다.

<br><br>

이제 서버를 한번 재시작 하고 나면, HTML 등 정적 자원을 변경 했을때는 서버를 굳이 껐다 켜지 않고 브라우저 새로고침만 해도 변경 사항이 적용 되는 것을 확인 할 수 있습니다. 리소스 업데이트에 시간이 필요하니 파일 저장 후 새로고침은 약 1초의 간격을 두고 해줘야 합니다.

그렇다면 브라우저도 자동으로 새로고침 된다면 얼마나 편할까요?

## LiveReload

> https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.devtools.livereload

위에서 `application.properties`에  미리 설정했던 LiveReload 기능 입니다.

Spring-boot-devtools 모듈은 LiveReload 서버를 내장 하고 있는데요. resource, 즉 정적 파일의 변경을 감지 했을 때 브라우저에 refresh를 trigger 하는 기능을 제공합니다. LiveReload 브라우저 extention은 크롬, 파이어폭스, 사파리 등에서 사용 가능하며 liveread.com에서 **무료**로 설치할 수 있습니다.

그래서 사실 live reload 기능도 시도를 해보았는데요.. 어째 아무리 매뉴얼 대로 해도 안되길래 플러그인 리뷰를 확인 해 보니

![image-20220101125805772](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/hot-reload.assets/image-20220101125805772.png)

> 현재로서는 안되고 있는게 맞나 봅니다. 저처럼 다른분들도 시간 낭비 하는 일 없었으면 해서 내용을 공유 해 드립니다.

그럼 이상으로 포스팅 마치겠습니다.

사실 정적 자원때문에 서버를 재시작 할 필요 없다는 것 만으로도 개발 생산성이 상당히 좋아집니다.
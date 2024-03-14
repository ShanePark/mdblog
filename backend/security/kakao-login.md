# 카카오 로그인 연동 과정 소개 및 스프링 예제 코드

## Intro

요즈음 간편 로그인은 대세를 넘어 필수로 자리잡았다. 

서비스를 개발하는 입장에서도 고객의 아이디와 암호를 보관하는 부담이나 복잡한 인증 시스템을 개발하는 수고를 덜을 수 있기 때문에 안쓸 이유가 없다.

다만, 간편 로그인의 워크플로는 서비스마다 조금씩 다르고 기존에 학습했던 인증 시스템과는 다른 개념이기 때문에 어느정도 전체적인 워크플로를 파악하는게 선행되어야 한다.

이번 글에서는 카카오 로그인을 활용한 간편 로그인을 개발하며 살펴본 전체적인 흐름에 대해 각 단계별로 간략이 정리해보려한다. 가장 좋은건 카카오가 제공하는 [문서](https://developers.kakao.com/docs/latest/ko/index) 를 참고하는 것이다. 카카오 공식 문서가 아주 잘 작성되어 있으나 본인이 초보자라 무슨말인지 잘 모르겠다 하면 이 글로 코드와 함께 전체적인 흐름을 파악해보자.

## 준비

카카오 로그인을 개발하기 위해서는 개발자 본인의 카카오 계정이 있어야하는건 당연하고, 그 외에도 몇가지 준비해야할 것이 있다.

### 카카오 디벨로퍼스 회원가입

기존에 Kakao developers 에 회원가입이 되어 있지 않다면 가입을 먼저 해야한다. 아래의 링크에 접속하면 된다.

> https://developers.kakao.com/

카카오 계정으로 이미 로그인 되어 있다면 디벨로퍼스 가입 자체는 간단하다.

### 어플리케이션 등록

가입 이후에는 우측상단의 `내 어플리케이션` 을 클릭하고 개발하고자 하는 애플리케이션을 추가한다.

아래 사진에 보이는 것 처럼, 단순하게 카카오 로그인을 테스트해보기 위한 학습 어플리케이션을 생성하는 것도 무방하다.

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/security/kakao-login.assets/1.webp)

> 생성한 어플리케이션 목록

어플리케이션을 생성하면, `일반` 메뉴에서 `테스트 앱`도 생성할 수 있는데, 후술할 리다이렉트 URI나 사이트 도메인 등록에 있어서 이점이 있으니 테스트 앱을 생성하는 것도 좋다.

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/security/kakao-login.assets/2.webp)

### 도메인 및 리다이렉트 URI 등록

도메인 및 리다이렉트 URI는 정상적으로 등록되어있지 않으면 절대 동작하지 않는다.

- 첫번째로, 사이트도메인은 `앱 설정` > `플랫폼` >. `Web` 에 있다.

![3](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/security/kakao-login.assets/3.webp)

> localhost:8080 을 사이트 도메인으로 등록 해 둔 상태

사이트 도메인은 최대 9개까지 등록이 가능하기 때문에, 굳이 테스트앱을 만들지 않고 원본앱에서 `localhost:8080`을 등록해두고 개발할수도 있다.

- 두번쨰로, Redirect URI는 `제품 설정` > `카카오 로그인` 에서 등록할 수 있다.

![4](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/security/kakao-login.assets/4.webp)

제일 먼저 카카오 로그인의 활성화 설정 상태를 `ON`으로 바꿔주고 간편 가입 사용여부를 `ON`으로 변경한 후에,  Redirect URI를추가해주면 된다. 총 10개가 등록 가능하다.

RedirectURI는 기본 host + context-path 이후에 인증을 처리할 핸들러 주소를 등록해주면 되는데, `/Oauth2ClientCallback/kakao` 가 카카오 인증에서 흔히들 사용된다.

이제 마지막으로 `앱 설정` > `요약 정보`에 있는 <u>**앱 키**</u>를 확인해준다. 다음 단계부터 필요하다

![5](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/security/kakao-login.assets/5.webp)

## 워크플로 및 개발

카카오 로그인을 활용한 간편 로그인의 전체적인 로그인 과정은 아래와 같다. 이 전체 워크플로를 먼저 몇번 읽어보고 진행하는걸 추천한다.

1. (JS) 카카오 로그인 요청
2. (JS) 인증 및 동의 요청
3. (사용자) 로그인 및 동의
4. (JS) 인가 코드 발급 후 앱에 등록된 Redirect URI로 리다이렉트
5. (서버) 인가 코드를 활용해 토큰 발급 요청
6. (서버) 토큰 발급
7. (서버) 토큰 정보로 사용자 정보 불러오기
8. (서버) 사용자 정보를 토대로 로그인 혹은 회원 가입 처리

한번에 펼쳐보면 다소 복잡해보이긴 하지만 생각보다 간단하다. 특히 JS 부분은 카카오에서 개발해둔 SDK가 있기 때문에 `앱 키`에 등록된 **JavaScript 키**만 등록해주면 알아서 다 해주어서 서버개발만 적당히 해주면 된다. 지금부터 하나씩 해 보자.

### JS 부분

카카오 로그인 Javascript SDK는 아래의 링크에서 확인할 수 있다.

> https://developers.kakao.com/docs/latest/ko/javascript/download

![7](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/security/kakao-login.assets/7.webp)

버전 옆의 `</>` 버튼을 클릭하면 CDN 스크립트 태그를 바로 복사할 수 있어 편리하다. 2.7.0 버전의 CDN은 아래와 같다.

```javascript
<script src="https://t1.kakaocdn.net/kakao_js_sdk/2.7.0/kakao.min.js" integrity="sha384-l+xbElFSnPZ2rOaPrU//2FF5B4LB8FiX5q4fXYTlfcG4PGpMkE1vcL7kNXI6Cci0" crossorigin="anonymous"></script>
```

로그인 구현에 필요한 리소스는 아래의 페이지에서 다운로드 받을 수 있다.

> https://developers.kakao.com/docs/latest/ko/kakaologin/js

![6](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/security/kakao-login.assets/6.webp)

해당 링크에 방문하면 카카오 로그인에 필요한 버튼 이미지등도 제공하고 있으니 `리소스 다운로드`를 클릭하여 다운로드 받으면 된다.

SDK를 추가 했다면 이제 자바스크립트 코드를 작성한다. 

```javascript
Kakao.init('Javascript 앱 키');
Kakao.isInitialized();

function kakaoLogin() {
    Kakao.Auth.authorize({
        redirectUri: window.location.origin + '/api/auth/Oauth2ClientCallback/kakao',
        state: JSON.stringify({referer: referer}),
    });
}
```

카카오 로그인 버튼을 누르면 `kakaoLogin` 함수가 실행되도록 해야 하는데 그 내용은 생략한다. 

> vuejs 라면 `@click="kakaoLogin"` jQuery 라면 `on('click')` 이벤트 등을 활용하면 되겠다.

자바스크립트 앱 키의 경우에는 `앱 설정` > `요약 정보` 에서 **<u>앱 키</u>** 부분을 확인하면 된다. ` Kakao.Auth.authorize` 함수로 카카오 로그인을 요청하는데, 리다이렉트 URI는 미리 등록되어있어야만 하고, state의 경우에는 request 와 callback 사이에 유지하고 싶은 정보를 등록하는데, 나의 경우에는 로그인이 필요한 페이지를 요청 했을 때 `referer` 변수에 요청한 페이지 주소를 기억하고 있다가 나중에 최종적으로 로그인 성공 후 리다이렉트 보낼 용도로 사용한다.

`state` 에 대해서는 OAuth 2.0 관련 규약인 **RFC 6749** 의 `state` 부분을 확인해보면 자세히 알아볼 수 있다. 

> https://datatracker.ietf.org/doc/html/rfc6749#page-72

SDK가 워낙 잘 되어 있기 때문에 이정도 코드만 등록해줘도 간단히 자바스크립트 구간은 끝이 난다.

### 서버 부분

이제 `kakaoLogin` 함수가 실행되면 자연스럽게 카카오 로그인 요청을 한다.

![8](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/security/kakao-login.assets/8.webp)

여기에서 동의 후 계속하기 버튼을 클릭하면 미리 등록했던 URI로 코드정보와 함께 리다이렉트가 진행된다.

Redirect 는 Get 요청으로 진행되며, 쿼리스트링으로 인가코드 및 state 정보가 전달된다.

아래는 해당 Redirect 를 처리하는 핸들러 코드 예제다. 스프링 + Kotlin 으로 작성하였다. 특별히 볼건 없고, `code`란 이름으로 인가 코드가 전달되었다는것만 확인하면 된다. 현재 url을 `curUrl`이란 변수에 담아 `redirectUrl` 파라미터로 전달하는 이유는 후에 토큰발급 요청을 할 때 해당 리다이렉트 URI가 필요하기 때문이다.

```kotlin
@Controller
@RequestMapping("/api/auth")
class OAuthController(
    private val kakaoLoginService: KakaoLoginService
) {
    private val objectMapper = ObjectMapper()

    @GetMapping("Oauth2ClientCallback/kakao")
    fun kakaoLoginCallback(
        @RequestParam code: String,
        @RequestParam(value = "state") stateString: String,
        httpServletRequest: HttpServletRequest
    ): String {
        val curUrl = httpServletRequest.requestURL.toString()
        kakaoLoginService.login(code, redirectUrl = curUrl)

        val state = objectMapper.readValue(stateString, Map::class.java)
        val referer = state["referer"]

        return "redirect:/$referer"
    }

}
```

이제 작성해둔 `kakaoLoginService.login` 부분에서 해당 인가코드를 가지고 로그인 부분을 구현하면 된다.

이번에는 서비스쪽 코드를 살펴보자.

```kotlin
@Service
class KakaoLoginService(
    private val kakaoTokenApi: KakaoTokenApi,
    private val kakaoUserInfoApi: KakaoUserInfoApi,
    @Value("\${oauth.kakao.rest-api-key}") private val restApiKey: String
) {
    fun login(code: String, redirectUrl: String) {
        // 1. get access token
        val kakaoTokenResponse = kakaoTokenApi.getToken(
            grantType = "authorization_code",
            clientId = restApiKey,
            redirectUri = redirectUrl,
            code = code
        )

        // 2. load user info with access token
        val userinfo = kakaoUserInfoApi.getUserInfo(accessToken = "Bearer ${kakaoTokenResponse.accessToken}")

        // 3. TODO: with user id, log in or sign up
        val kakaoId = userinfo.id
        // kakaoId를 가지고 로그인 혹은 회원가입 처리

    }

}
```

1. kakaoTokenApi 를 활용해 인가 코드로 토큰을 먼저 발급

2. 해당 토큰을 활용해 kakaoUserInfoApi를 통해 사용자 정보를 불러온다.

3. 사용자의 kakaoId를 가지고 로그인 혹은 회원가입 처리를 하면 끝

토큰 발급은 `https://kauth.kakao.com/oauth/token` 주소에 인가 코드 정보를 포함한 `POST` 요청을 해서 받아온다.

- Content-type: application/x-www-form-urlencoded;charset=utf-8
- body 에는 grant_type, client_id, redirect_uri, code(인가 코드) 정보가 필수다.

**요청 예시**

```bash
curl -v -X POST "https://kauth.kakao.com/oauth/token" \
 -H "Content-Type: application/x-www-form-urlencoded" \
 -d "grant_type=authorization_code" \
 -d "client_id=${REST_API_KEY}" \
 --data-urlencode "redirect_uri=${REDIRECT_URI}" \
 -d "code=${AUTHORIZE_CODE}"
```

그러면 아래처럼 응답이 온다

```json
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
{
    "token_type":"bearer",
    "access_token":"${ACCESS_TOKEN}",
    "expires_in":43199,
    "refresh_token":"${REFRESH_TOKEN}",
    "refresh_token_expires_in":5184000,
    "scope":"account_email profile"
}
```

참고로 인가 코드는 일회용이기 때문에, 한번 토큰 발급에 사용되면 또 다시 사용할 수 없다.

해당 요청은 스프링6의 HTTP interface를 통해 아래와 같이 구현하였다.

${code:KakaoTokenApi.kt}

```kotlin
interface KakaoTokenApi {

    @PostExchange(value = "/token", contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    fun getToken(
        @RequestParam("grant_type") grantType: String,
        @RequestParam("client_id") clientId: String,
        @RequestParam("redirect_uri") redirectUri: String,
        @RequestParam("code") code: String
    ): KakaoTokenResponse

}
```

${code:KakaoLoginConfig.kt}

```kotlin
  @Bean
  fun kakaoAuthApi(): KakaoTokenApi {
      val httpClient: HttpClient = HttpClient.create()
          .responseTimeout(Duration.ofSeconds(10))
      val connector: ClientHttpConnector = ReactorClientHttpConnector(httpClient)

      val client = WebClient.builder()
          .baseUrl("https://kauth.kakao.com/oauth")
          .clientConnector(connector)
          .build()

      return HttpServiceProxyFactory
          .builder(WebClientAdapter.forClient(client))
          .build()
          .createClient(KakaoTokenApi::class.java)
  }
```

${code:KakaoTokenResponse.kt}

```kotlin
data class KakaoTokenResponse(
    @JsonProperty("access_token")
    val accessToken: String,

    @JsonProperty("token_type")
    val tokenType: String,

    @JsonProperty("refresh_token")
    val refreshToken: String,

    @JsonProperty("expires_in")
    val expiresIn: Int,

    @JsonProperty("refresh_token_expires_in")
    val refreshTokenExpiresIn: Int
)
```

이렇게 인가코드를 통해 토큰을 발급했다면 바로 토큰정보를 통해 사용자 정보를 조회한다.

사용자 정보는 `https://kapi.kakao.com/v2/user/me` API로 액세스 토큰 정보를 포함한 GET 혹은 POST 요청을 해서 받아온다.

![9](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/security/kakao-login.assets/9.webp)

curl 요청 샘플은 아래와 같다.

```bash
curl -v -G GET "https://kapi.kakao.com/v2/user/me" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}"
```

응답 예제는 아래의 Postman 스크린샷으로 갈음한다. id 부분에 카카오 아이디정보가 포함된다.

본 요청도 스프링6의 HTTP interface를 통해 아래와 같이 구현하였다.

${code:KakaoUserInfoApi.kt}

```kotlin
interface KakaoUserInfoApi {

    @GetExchange(value = "/user/me")
    fun getUserInfo(
        @RequestHeader("Authorization") accessToken: String
    ): KakaoUserInfoResponse

}
```

${code:KakaoLoginConfig.kt}

```kotlin
@Configuration
class KakaoLoginConfig {
    @Bean
    fun kakaoUserInfoApi(): KakaoUserInfoApi {
        val httpClient: HttpClient = HttpClient.create()
            .responseTimeout(Duration.ofSeconds(10))
        val connector: ClientHttpConnector = ReactorClientHttpConnector(httpClient)

        val client = WebClient.builder()
            .baseUrl("https://kapi.kakao.com/v2")
            .clientConnector(connector)
            .build()

        return HttpServiceProxyFactory
            .builder(WebClientAdapter.forClient(client))
            .build()
            .createClient(KakaoUserInfoApi::class.java)
    }

}
```

${code:KakaoUserInfoResponse.kt}

```kotlin
data class KakaoUserInfoResponse(
    @JsonProperty("id")
    val id: Long,

    @JsonProperty("connected_at")
    val connectedAt: String,
)
```

이후 최종적으로 아래와 같이 카카오 아이디를 받아와서 내부적으로 로그인 및 회원가입 처리를 진행하면 된다.

```kotlin
val kakaoId = userinfo.id
```

DB에 등록 된 사용자중 `kakao_id` 컬럼이 방금 조회한 `kakaoId`와 일치하는 사용자가 있다면 해당 사용자로 로그인 처리를 하면 되겠고, 일치하는 사용자가 없다면 가입 페이지로 이동시키거나 혹은 추가로 입력할 정보가 없다면 즉각 계정을 생성해 로그인을 시켜버리면 되겠다.

최종적으로 서버에서의 로그인 처리는 세션기반, JWT 등 각자 처리하고 싶은 방식으로 하면 되는데 해당 부분에 대해서는 본 글에서는 다루지 않는다. 당신이 개발을 막 배운지 얼마 안되어 인증부분 개발이 어려운 개발자라면 쿠키방식으로 간단히 구현해보고 나중에 보강하는 것도 괜찮다. 인증은 원래 제대로 개발하려면 많이 어렵다.

끝

**References**

- https://developers.kakao.com/docs/latest/ko/kakaologin/common
- https://datatracker.ietf.org/doc/html/rfc6749#page-72


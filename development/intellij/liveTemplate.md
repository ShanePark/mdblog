# IntelliJ) 테스트 Live Templates 만들기

## Intro

매번 테스트 코드를 만들 때 마다 반복해서 입력하는

```java
@Test
void test() {
  Assertion.assertThat()
}
```

을 오늘도 여김없이 계속 입력하다가 이건 너무나도 비효율 적이란 생각이 들었습니다.

사실 클래스 생성시 애초에 기본 틀이 자동으로 만들어지도록 플러그인을 만들까 하다가 아직 거기까진 무리고.. 이클립스를 쓸 때에 이것 저것 추가해서 하던 것 처럼 인텔리제이에서도 당연히 기능이 있겠지 하며 찾아보니 Live templates이 눈에 띄었습니다.

그래도 여전히 커스텀 플러그인에 대한 필요성이 자주 느껴져서 올해 안에는 인텔리제이 플러그인을 만들어 보는게 목표 입니다.

## Live templates

Preferences > Editor > Live Templates에 해당 기능이 있습니다.

### 기존 템플릿들

![image-20220320220238924](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/liveTemplate.assets/image-20220320220238924.png)

> 여기에 보면 iter 라던가 

![image-20220320220259155](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/liveTemplate.assets/image-20220320220259155.png)

> soutv 라던가

![image-20220320220313678](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/liveTemplate.assets/image-20220320220313678.png)

> main

위와 같이 평소에 자주 쓰는 템플릿들이 눈에 들어왔습니다.

여기 작성된 내용들을 참고 해서 만들면 되겠네요.

### 템플릿 생성

![image-20220320220836940](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/liveTemplate.assets/image-20220320220836940.png)

우측에 있는 `+` 버튼을 누르고 `Live Template`을 선택 해 템플릿을 생성 합니다.

```java
@org.junit.Test
public void $EXPR$() {
    org.assertj.core.api.Assertions.assertThat($END$)
}
```

![image-20220320221021185](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/liveTemplate.assets/image-20220320221021185.png)

위와 같이 템플릿을 생성 해 보았습니다. `$EXPR$` 을 쓰면 처음 템플릿을 생성 했을때 바로 해당 내용을 작성 할 수 있고, 내용 입력 후 엔터키를 입력 하면 `$END`가 작성된 곳으로 커서가 이동 됩니다.

Abbreviation은 `test`라고 이름 지었습니다. assert와 test 중에 고민하다가 일단 test로 하였는데 assert 로 하여도 앞에 세글자만 입력 하고 tab 키를 누르면 자동 완성 되기 때문에 편한대로 이름 지으시면 됩니다.

또한, 우측의 Reformat according to style과 Use static import if possible을 체크 해 주었는데요 특히 static import를 자동으로 해주는게 마음에 들었습니다.

![image-20220320220722649](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/liveTemplate.assets/image-20220320220722649.png)

아래쪽의 Applicable은 Java에서 Declaration만 체크 해 주면 됩니다. 제가 메서드 이름을 빠르게 지을때는 그냥 test라고만 하는 경우가 잦은데, 그 때마다 그 안에 test 템플릿을 계속 생성하길래 보니 Applicable 범위를 너무 크게 잡아 두었었습니다. 범위 설정을 잘 해두어야 불편함이 없습니다.

### 템플릿 확인

![gg](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/liveTemplate.assets/gg.gif)

> 완성한 템플릿을 사용해 보았습니다.

평소같았으면 입력하는데 못해도 10초 걸리는 내용이 3초 정도면 완성 됩니다.

매우 만족스러워서 자주 반복해 입력하는 코드들을 모두 템플릿으로 등록 해 두려고 합니다.

이상입니다.

 
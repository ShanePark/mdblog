# Svelte kit 개발할때 implicitly has an any type 경고 끄기 

## Intro

Svelte 를 ts가 아닌 js로 사용하는데 사용중인 인텔리제이에 자꾸 ts 관련 경고가 나오는데 매우 성가시다. 빨간줄로 쫙 가있다.

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/frontend/svelte/sveltekit-implicitly.assets/1.webp)

> Svelte: Parameter description implicitly has an any type.

범인은 Intellij IDEA에 설치한 Svelte 플러그인으로 추정되는데, 인텔리제이에서 Svelte 플러그인 없이 개발하긴 참 불편하다.

그렇다고 타입을 명시해주면 해결이 되는가?

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/frontend/svelte/sveltekit-implicitly.assets/2.webp)

입을 명시하면 Typescript 파일에서만 사용할 수 있다고 하며 다른 오류를 또 낸다. 

그리고 애초에 타입을 쓸 생각도 없다.

## 해결

### 해결1

스벨트킷으로 개발할때는 `.svelte-kit/tsconfig.json` 파일이 자동 생성된다.

거기에 잘 찾아보면 `compilerOptions`가 있는데, 그 곳에 `"noImplicitAny": false` 를 한줄 추가해주면 된다.

![3](https://raw.githubusercontent.com/ShanePark/mdblog/main/frontend/svelte/sveltekit-implicitly.assets/3.webp)

색깔이 흐려진게 보이는가? 일단 애매하게나마 해결되었다는 소리다.

하지만 `.svelte-kit` 경로는 심지어 자동으로 생성되기 때문에 나중에 또 자동으로 지워진다.

그러므로 설정을 고정시켜두려면 `jsconfig.json` 파일에 추가해주면 된다.

![4](https://raw.githubusercontent.com/ShanePark/mdblog/main/frontend/svelte/sveltekit-implicitly.assets/4.webp)

이 파일은 우리가 먼저 열어봤던 봤던 `.svelte-kit/tsconfig.json`파일을 상속해 설정을 오버라이드 한다. 그리고 여기에 작성해주면 나중에 자동생성 할 때도 설정이 지워지지 않는다. 버전관리툴에 그대로 포함시키면 된다.

하지만 여전히 이 애매한 해결은 마음에 들지 않는다. 

애초에 이 문제는 실제로 타입체킹을 해서 문제가 발생한다기보다는 뭔가 IDE가 착각을 하고 있기 때문에 발생한것이다.

### 해결2

그러면 IDE가 착각하고 있는것을 역으로 이용하면 된다. jsconfig.json 파일이 있는 경로에 tsconfig.json 파일을 생성해준다.

${code:tsconfig.json}

```json
{
  "extends": "./.svelte-kit/tsconfig.json",
  "compilerOptions": {
    "noImplicitAny": false
  }
}
```

그리고 `jsconfig.json`에 추가했던 `"noImplicitAny": false` 부분은 제거해준다.

![5](https://raw.githubusercontent.com/ShanePark/mdblog/main/frontend/svelte/sveltekit-implicitly.assets/5.webp)

이제 해결이 잘 되어서 경고가 나오지 않는다.

그리고 애초에 `package.json` 에서는 `jsconfig.json` 파일만 보기 때문에 `tsconfig.json` 이 파일은 아무 일도 하지 않고 IDE가 혼동해서 타입체킹을 하려는 것만 방지해준다. 이제 상쾌한 마음으로 다시 열심히 코드를 작성해보자. 

끝




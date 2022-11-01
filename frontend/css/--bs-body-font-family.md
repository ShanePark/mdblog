# 부트스트랩 기본 폰트 --bs-body-font-family 변경하기

## Intro

부트스트랩을 사용 하고 있는데, 전체적으로 기본 폰트를 변경 하려고 하니 `--bs-body-font-family`가 적용 되어 있어서 변경하는데 까다로운 상황이 있었습니다.

물론 css를 덮어 쓰며 `!important;` 를 붙이는 방법이 있기는 하지만 애초에 important 는 css 작성 시 권장되지 않는 방법이기도 하고, 그렇게 한번 해 버리면 하위 요소들의 세부적인 폰트 설정이 어려워집니다.

이 문제 상황을 해결 해 보도록 하겠습니다.

bootstrap.min.css 인 경우와 scss 인 경우 모두 해결 방법을 작성 해 두었습니다.

## 문제상황

**main.ts**

```typescript
import { createApp } from "vue";
import { createPinia } from "pinia";

import App from "./App.vue";
import router from "./router";

import "bootstrap/scss/bootstrap.scss";

const app = createApp(App);

app.use(createPinia());
app.use(router);

app.mount("#app");

```

위에 보이는 것 처럼 `import "bootstrap/scss/bootstrap.scss";` 로 부트스트랩을 불러와서 사용 하고 있는데요

이후 body에 전체적인 font family 를 `Galmuri7`로 설정 해 두었습니다.

**App.vue**

```html
<script setup lang="ts">
import {RouterView} from "vue-router";
import Header from "@/components/Header.vue";</script>

<template>
  <Header/>
  <RouterView/>
</template>

<style>
body {
  font-family: 'Galmuri7';
}
</style>

```

그런데 확인해보면 폰트가 변경되지 않습니다. 개발자 모드를 확인 해 보면

![image-20221022135646475](https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/css/--bs-body-font-family.assets/image-20221022135646475.png)

> 개발자 모드에서 body 에 적용된 `font-family` 속성을 보면, 커스텀한 설정은 전부 취소선이 그어져 있고, 최상단의 `font-family: var(--bs-body-font-family);` 만이 남아 있습니다. 

그래서 이번에는 `--bs-body-font-family` 를 클릭 해서 확인 해 보았습니다.  

![image-20221022141653350](https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/css/--bs-body-font-family.assets/image-20221022141653350.png)

부트스트랩의 기본 설정을 보고 있습니다. --bs-font-sans-serif 설정으로 또 넘어갑니다.

그래서 `!important;` 를 걸지 않는 이상은 위의 속성이 먼저 걸리게 됩니다.

## 해결

### bootstrap.min.css

scss 가 아닌 일반적인 css 파일을 사용 하고 있는 경우는 보다 간단합니다. 아래와 같이 해당 변수를 적당한 위치에서 오버라이드 해 줍니다.

```css
--bs-body-font-family: Galmuri7;
```

### bootstrap.scss

일단 `import "bootstrap/scss/bootstrap.scss";` 로 부트스트랩을 바로 불러오는 대신에 `import "/src/assets/scss/main.scss";` 로 변경 해 줍니다.

main.ts

```typescript
import { createApp } from "vue";
import { createPinia } from "pinia";

import App from "./App.vue";
import router from "./router";

import "/src/assets/scss/main.scss";

const app = createApp(App);

app.use(createPinia());
app.use(router);

app.mount("#app");

```

그러고 나서 `/src/assets/scss/main.scss` 경로에 파일을 생성 해 주고

![image-20221022142121464](https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/css/--bs-body-font-family.assets/image-20221022142121464.png)

부투스트랩 import 문을 포함 해서 font 도 넣고, 변수를 변경 해 줍니다.

**main.scss**

```scss
@import "bootstrap/scss/bootstrap.scss";
@import url('https://cdn.jsdelivr.net/npm/galmuri@latest/dist/galmuri.css');

:root {
  --bs-body-font-family: "Galmuri7";
}

```

폰트가 바뀌었을때 눈에 바로 표시가 되게 Galmuri 폰트로 변경 해 보았습니다.

이제 해결이 되었는지 확인을 해 봅니다.

**변경 전**

![image-20221022142426475](https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/css/--bs-body-font-family.assets/image-20221022142426475.png)

**변경 후**

![image-20221022143136559](https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/css/--bs-body-font-family.assets/image-20221022143136559.png)

폰트가 정상적으로 적용이 되었습니다. 

이제 개발자 모드에서 확인을 해 봅니다.

![image-20221022142640340](https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/css/--bs-body-font-family.assets/image-20221022142640340.png)

> 여전히 body 의 font-family는 `var(--bs-body-font-family);` 로 설정이 되어 있고

클릭을 해서 확인 해 보면

![image-20221022143159584](https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/css/--bs-body-font-family.assets/image-20221022143159584.png)

> 부트스트랩의 기본 폰트가 변경 되어 있습니다.

이상입니다.

**Reference**

- https://www.youtube.com/watch?v=_7jrFQsaxT0&ab_channel=PixelRocket
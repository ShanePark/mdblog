# [IntelliJ IDEA] 파일 생성시 라이센스 정보 자동으로 입력하기

## Intro

정책상 모든 코드에 라이센스 정보를 입력 해야 하는데, 이게 여간 귀찮은 일이 아닙니다. 심지어 종종 까먹고 빼먹는 일도 있기 때문에 한번씩 모든 코드를 검사해서 일괄적으로 등록 하곤 했었는데 아에 파일 생성시 자동으로 입력 되도록 등록을 해 보려고 합니다.

## How to

다행히도, 인텔리제이에서 제공하는 File and Code Template 기능을 활용 하면 어렵지 않게 설정이 가능합니다.

`Setting` > `Editor` > `File and Code Template` 로 들어갑니다.

![image-20221223111820166](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/file-and-code-template.assets/image-20221223111820166.png)

그러면 위와 같은 화면이 나오는데, 가장 먼저 상단의 Scheme을 선택해서 File template이 적용될 스코프를 선택 합니다.

![image-20221223111927072](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/file-and-code-template.assets/image-20221223111927072.png)

- Default: 전체 어플리케이션에 설정합니다. 현재 사용하는 인텔리제이를 통해 띄우는 모든 프로젝트에 해당 템플릿이 적용 됩니다. 프로젝트에 상관 없이 사용할 경우 선택 합니다. `IDE configuration directory`에 저장 됩니다.
- Project: 현재 띄워둔 프로젝트에만 해당 설정을 적용 합니다.  본 설정은 `.idea/fileTemplates`에 저장됩니다.

저는 지금의 프로젝트에서만 적용시키기 위해 Project를 선택 하였습니다.

###  License

등록에 앞서 모든 자바파일의 맨 위에 등록할 라이센스 문구를 작성 하도록 하겠습니다.

예제로 Apache License 2.0 를 참고 했습니다.

![image-20221223113626124](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/file-and-code-template.assets/image-20221223113626124.png)

> https://www.apache.org/licenses/LICENSE-2.0

자바파일에 첨부할 것 이기 때문에, 위의 틀을 바탕으로 자바의 주석 형태로 작성 해 줍니다.

```
/*
 * Copyright 2022 ShanePark
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
```

### Includes

이제 두번째 탭인 Includes에 들어가, `+` 버튼을 누르고 Apache License라는 이름으로 방금 작성한 내용을 토대로 템플릿 파일을 작성 해 줍니다.  

![image-20221223115954004](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/file-and-code-template.assets/image-20221223115954004.png)

> Name에는 Apache License를, Extension 에는 java를 입력

이제 다시 Files로 돌아와, Class를 선택 후 맨 위에 `#parse("Apache License.java")` 를 입력 해 줍니다.

![image-20221223120313440](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/file-and-code-template.assets/image-20221223120313440.png)

> package 위에 입력 후 Apply 혹은 Ok 버튼을 클릭해 적용

이후 Interface, Enum, Record, Annotation 등에도 모두 똑같이 등록 해 줍니다.

이제 클래스 파일을 하나 생성 해 보면 자동으로 라이센스 정보가 입력 되는것이 확인 됩니다.

![image-20221223120512709](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/file-and-code-template.assets/image-20221223120512709.png)

> 방금 작성한 라이센스 내용이 정상적으로 입력 되어 있습니다.

심지어 IDE 에서 자동으로 위의 주석 부분을 접어 주기 때문에 라이센스 정보에 거슬릴 필요 없이 편안하게 코드를 작성 할 수 있습니다.

![image-20221223120950448](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/file-and-code-template.assets/image-20221223120950448.png)

마찬가지로 html 파일등 에도 적용하면 예외없이 필요한 모든 파일에 안심하고 라이센스 정보를 등록 할 수 있습니다.

이상입니다.

## References

- https://www.jetbrains.com/help/idea/settings-file-and-code-templates.html
- https://www.apache.org/licenses/LICENSE-2.0


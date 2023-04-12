# Eclipse) 자바스크립트 에러표시 제거

<img src=https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/eclipse-javascript-error.assets/image-20211130161550605.webp width=500 height=300 alt=1>

## Intro

자바스크립트 파일의 validation을 이클립스가 제대로 하지 못해서 Error로 인식하며 빨간불이 들어왔습니다.

당연히 코드를 구동하는데는 아무 문제가 없지만, 코드에 빨간불이 들어와있으면 굉장히 거슬립니다.

여러가지 편의성으로 인해 지금은 IntelliJ IDEA를 사용하기 때문에 이클립스를 사용할 때에 겪었던 여러 가지 불편했던 점을 모두 해결 한 상태 이지만, 혹시나 같은 문제로 스트레스 받고 있는 분들을 위해 설정했던 내용을 공유합니다.

## 해결

일단 아래의 순서로 이동해 설정 화면을 띄웁니다.

>  프로젝트 우클릭 -> Properties -> Client-side JavaScript > `Include Path`

<img src=https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/eclipse-javascript-error.assets/image-20211130161707081.webp width=750 height=520 alt=2>

위에 있는 Source 탭을 누르고 > Excluded 선택 > `Edit...` 버튼을 클릭 합니다.

![image-20211130162020800](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/eclipse-javascript-error.assets/image-20211130162020800.webp)

아래의 Inclusion and Exclusion Patterns가 뜨면 `Add...` 버튼을 클릭 합니다.

![image-20211130162236441](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/eclipse-javascript-error.assets/image-20211130162236441.webp)

![image-20211130162211420](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/eclipse-javascript-error.assets/image-20211130162211420.webp)

그냥 모든 js파일의 validation을 안하도록 하겠습니다. `**/*.js` 라고 입력 합니다.

![image-20211130162453269](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/eclipse-javascript-error.assets/image-20211130162453269.webp)

추가가 되었으면 Finish, Apply and Close 버튼 클릭 하여 마무리 합니다.

### 완료

작업이 완료되었으면 Project Clean을 한번 해줍니다.

![image-20211130162647276](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/eclipse-javascript-error.assets/image-20211130162647276.webp)

> 더이상 에러가 표시되지 않습니다.

이제 에러가 없습니다. 빨간불로 부터 해방되었습니다.
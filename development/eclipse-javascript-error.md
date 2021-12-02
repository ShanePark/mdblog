# Eclipse 자바스크립트 에러표시 제거

## 문제

![image-20211130161550605](/home/shane/Documents/git/mdblog/development/eclipse-javascript-error.assets/image-20211130161550605.png)

## 해결

프로젝트 우클릭 -> Properties -> Client-side JavaScript > Include Path

![image-20211130161707081](/home/shane/Documents/git/mdblog/development/eclipse-javascript-error.assets/image-20211130161707081.png)

Source > Excluded 선택 > Edit

![image-20211130162020800](/home/shane/Documents/git/mdblog/development/eclipse-javascript-error.assets/image-20211130162020800.png)

Add...

![image-20211130162236441](/home/shane/Documents/git/mdblog/development/eclipse-javascript-error.assets/image-20211130162236441.png)

![image-20211130162211420](/home/shane/Documents/git/mdblog/development/eclipse-javascript-error.assets/image-20211130162211420.png)

추가가 되었으며 Finish, Apply and Close 버튼 클릭

![image-20211130162453269](/home/shane/Documents/git/mdblog/development/eclipse-javascript-error.assets/image-20211130162453269.png)

작업이 완료되었으면 Project Clean

완료

![image-20211130162647276](/home/shane/Documents/git/mdblog/development/eclipse-javascript-error.assets/image-20211130162647276.png)

> 더이상 에러가 표시되지 않습니다.
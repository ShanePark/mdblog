# IntelliJ) 자동 임포트 정렬시 이클립스와 같은 규칙으로 설정하기

## Intro

<img src=https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/organize-imports.assets/image-20220517155040479.webp width=663 height=235 alt=1>

Eclipse IDEA를 사용하며 습관처럼 `Ctrl + Option(Alt) + O` 키를 눌러 Optimize import 기능을 사용 하다 보면, 혼자서 진행하는 프로젝트가 아닌 이상은 각자의 IDE 환경설정 등에 따라 import 문을 모두 재 정돈 해 줍니다.

매번 커밋 할 때 마다 import 문 변경 사항이 너무 많다보니 프로젝트의 코드변경 히스토리때문에 버전관리 용량도 커지고, 변경사항을 trace 할 때에도 쓸데없는 변경 사항들이 눈에 보이다 보니 통일을 위해 이클립스와 같은 방식으로 변경해야겠다는 생각이 들었습니다.

## import 설정 변경

<img src=https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/organize-imports.assets/image-20220517155512146.webp width=750 height=450 alt=2>

일단 `Settings` > `Editor` > `Code Style` > `Java` > `imports` 로 찾아 들어 가 줍니다.

Scheme이 보이는데, IDE 전체 설정으로 할지, 해당 프로젝트에만 설정 할지를 정해 줍니다.

저는 개인용도로 작업하는 프로젝트에서는 인텔리제이의 기본 규칙을 따르기 위해 해당 프로젝트에만 설정되도록 할 예정입니다

![image-20220517155722474](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/organize-imports.assets/image-20220517155722474.webp)

> 그것을 위해 일단 Scheme을 Project로 변경 해 주었습니다.

![image-20220517155741357](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/organize-imports.assets/image-20220517155741357.webp)

첫번째로, import 시 와일드카드를 사용하지 않도록 변경 해 주었습니다.

따로 기능을 끄는건 보이지 않아서 99로 지정 해 주었는데 이정도면 끈거나 다름 없습니다.

![image-20220517160212616](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/organize-imports.assets/image-20220517160212616.webp)

이후에는 아래쪽의  Import Layout을 Eclipse와 같은 순서로 맞춰 줍니다.

아래의 순서대로 하면 되고, 목록에 없는 패키지나 blank line 은 `+` 버튼을 클릭해서 추가 해 주도록 합니다.

```
  static all other, 
  blank, 
  java.*, 
  blank, 
  javax.*, 
  blank, 
  org.*, 
  blank, 
  com.*, 
  blank, 
  all other imports
```

변경 후 테스트를 해 보면

![image-20220517160410757](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/organize-imports.assets/image-20220517160410757.webp)

> 정확하게 추가되거나 제거된 패키지만 변경되는 것이 확인 됩니다.

## 마치며

그간 import 문이 자꾸 크게 바뀌는 것 때문에 신경이 제법 쓰였었는데 생각보다 어렵지 않게 해결 하였습니다.

Armeria 등 커미터가 많은 오픈소스의 경우에는 정해진 스타일로 기여를 받기 때문에, import 순서를 바꾸거나 할 경우에는 merge가 되지 않습니다. 코드리뷰에서 바로 지적을 받게 됩니다.

![image-20220517160714820](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/organize-imports.assets/image-20220517160714820.webp)

> https://armeria.dev/community/developer-guide/ 

생각보다 어렵지 않으니 필요하다면 설정을 해보시길 권장합니다.

이상입니다.

ref: https://stackoverflow.com/questions/14716283/is-it-possible-for-intellij-to-organize-imports-the-same-way-as-in-eclipse
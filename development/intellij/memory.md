# [IntelliJ IDEA] 메모리 설정 변경으로 Low memory 해결하기

## 문제

![image-20220815121217417](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/memory.assets/image-20220815121217417.png)

사실 인텔리제이를 사용하다보면 흔히 겪는 일인데, 힙메모리 부족으로 퍼포먼스가 느려진다는 경고가 뜰 때가 있습니다.

불과 얼마 전 까지만 해도 8GB 메모리의 M1 맥북에어를 사용 하고 있었기때문에 메모리를 많이 늘릴 생각보다는 사용중인 웹 브라우저들을 최대한 닫는 등의 방법으로 넘어 갔었는데요 

![image-20220815121331541](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/memory.assets/image-20220815121331541.png)

얼마전 큰맘 먹고 넉넉한 메모리의 새로운 맥북을 구입 하였기 때문에 이제는 해결이 가능 해 졌습니다.

> 다만, 물리적으로 메모리를 늘릴 수 없는 분들이라고 해도 아래 내용을 통해 힙메모리를 변경하고 테스트 해 보셔서 더 할당 가능한 여유범위를 한번 체크 해 보세요.

## 힙메모리 확인

일단 인텔리제이 아이디어가 사용하고 있는 물리적인 힙 메모리의 크기를 확인 해 보도록 하겠습니다.

`Shift` 키를 두번 누르고 memory indicator 라고 검색 하시면, 메모리 Indicator를 ON/OFF 할 수 있습니다. 

![image-20220815121651633](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/memory.assets/image-20220815121651633.png)

> Shift 두번 입력

혹은 하단 바를 우클릭 해서도, Memory Indicator를 켜고 끌 수 있습니다.

![image-20220815121731846](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/memory.assets/image-20220815121731846.png)

> 아래 우클릭

![image-20220815121817084](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/memory.assets/image-20220815121817084.png)

그러면 이제 사용중인 힙 메모리를 실시간으로 확인 할 수 있습니다.

총 1024M 중 830 MB를 사용 하고 있었네요.

## 힙 메모리 설정

메모리 설정은 `Help > Change Memory Settings`에서 하실 수 있습니다.

![image-20220815122100829](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/memory.assets/image-20220815122100829.png)

클릭을 한 뒤에,

![image-20220815122135988](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/memory.assets/image-20220815122135988.png)

> 원하는 Heap Size 를 설정 하고 저장 해줍니다. 

그냥 저장만 하면 바로 적용이 되지 않기 때문에 `Save and Restart`를 해주는 편이 좋아요.

혹은 Change Memory Settings 가 아니더라도 그 아래아래에 있는 `Edit Custom VM Options..`를 통해서도 설정하실 수 있습니다.

![image-20220815122347320](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/memory.assets/image-20220815122347320.png)

> Xmx 를 원하는 메모리 크기로 변경 해 주시면 됩니다.

![image-20220815122517963](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/intellij/memory.assets/image-20220815122517963.png)

그러면 재 시작 후에는, 메모리 크기가 변경 된 것을 확인 하실 수 있습니다.

이상입니다.
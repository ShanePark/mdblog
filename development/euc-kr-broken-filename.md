# 깨진 한글 파일 이름 복구하기 

![image-20220104141033328](/home/shane/Documents/git/mdblog/development/euc-kr-broken-filename.assets/image-20220104141033328.png)

## Intro

파일들을 주고 받다보면, 사용하고 있는 운영체제와 상관 없이 한글 파일명이 알아볼 수 없게 깨져있는 경우가 종종 있습니다. 특히 인터넷에서 파일을 다운받았을 때 그런 일이 많은데.. 모두 아시는 것 처럼 인코딩 문제 입니다.

파일명이 중요하지 않다면 그냥 한번 열어보고 말면 되지만, 파일명이 중요해서 꼭 알아야 할 때는 굉장히 절망적입니다.

다행인 것은 단방향 암호화처럼 원본 데이터가 손상된게 아닌 단지 인코딩문제로 못 알아보는 것 이기 때문에 다시 디코딩을 해 주면 원래의 파일 명을 알아 낼 수 있습니다.

## 원인

왠만한 인코딩 문제는 이렇게 못 알아 볼 정도는 아닌데. euc-kr로 인코딩 되어 있는 경우에는 이런 상황이 됩니다. 

요즘에는 흔하게 일어나지 않는데, 오래된 소프트웨어를 사용하다 보면 가끔 그럴 수 있습니다.

### EUC-KR

EUC-KR은 대표적인 `한글 완성형 인코딩`입니다. 보통은 `완성형`이라고 불리며 최근에는 확장된 CP949가 사용됩니다.

현대 한글에 사용되는 11,172자의 모든 글자 중에 사용 빈도가 높은 글자들을 추려내어 글자 코드 셋트에 전부 배당 해서 기록하는 방식인데요. 총 2,350자가 기록되어 있습니다.

> 아래 링크에서 11,172개의 현대 한글 음절 목록을 확인 할 수 있습니다.
>
> ![image-20220104142303084](/home/shane/Documents/git/mdblog/development/euc-kr-broken-filename.assets/image-20220104142303084.png)
>
> https://ko.wikipedia.org/wiki/%ED%95%9C%EA%B8%80_%EC%9D%8C%EC%A0%88

### 완성형의 단점

2,350 자 외의 다른 글자를 전혀 표기할 수 없다는게 정말 큰 단점 입니다. 이 때문에 종종 폰트가 깨지는 현상도 발생하고 인터넷에서도 특정 글자를 입력하면 표기가 되지 않는 등의 문제를 자주 목격 하게 됩니다.

아무리 빈도가 많은 글자들을 모아놓았다고 해도 전체의 20% 밖에 표현을 못하니 상당히 제한적입니다.

## 해결

EUC-KR로 디코드 한번 해 주고 다시 알아볼 수 있는 형태로 인코딩 해 주면 됩니다.

저는 보통 아래의 사이트를 이용합니다.

https://string-functions.com/encodedecode.aspx

![image-20220104144343544](/home/shane/Documents/git/mdblog/development/euc-kr-broken-filename.assets/image-20220104144343544.png)

> Encode with를 iso-8859로, Decode with를 euc-kr로 설정하면 원래의 파일 명을 알 수 있습니다.

혹시 단순한 String이 아니고, 텍스트 파일 전체의 인코딩이 깨졌다면 아래의 링크를 참고해주세요.

![image-20220104144127672](/home/shane/Documents/git/mdblog/development/euc-kr-broken-filename.assets/image-20220104144127672.png)

[Mac) 윈도우에서 작성된 텍스트파일 인코딩 변경하기.](https://shanepark.tistory.com/69)

<br><br>

이상입니다.
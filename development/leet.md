# 매일 LeetCode 문제 풀이, 터미널 한 줄로 간편하게

## Intro

LeetCode 문제를 매일 풀이하는 개발자라면 공감할 텐데.. 문제 링크를 찾아가고, 클래스 이름을 짓고, 파일을 생성하는 과정이 굉장히 번거롭다.  
그래서 이를 간단히 처리하기 위해 `leet.sh`와 `ClassNameFactory.java` 두 가지 프로그램을 작성했다.

이제 터미널에서 `leet` 한 줄만 입력하면, 오늘의 문제 링크가 뜨고, 문제풀이용 클래스 이름이 자동으로 클립보드에 복사된다.  
IDE에서 바로 붙여넣어 클래스를 생성한 뒤 코딩을 시작하면 된다. 비슷한 번거로움을 겪었던 개발자라면 한번 참고해보길 권한다.

## leet.bash

### 문제 정보 가져오기

`leet.sh`는 LeetCode의 GraphQL API를 이용해 오늘의 문제 정보를 가져온다.  
GraphQL API는 공식적으로 제공되지 않지만 LeetCode 웹사이트 내부적으로 사용하는 엔드포인트를 활용했다. 스키마가 공개되어 있지 않기 때문에 쿼리를 만드는데 어려움을 겪을 수 있다. 아래 스크립트는 `curl` 명령어로 문제 정보를 요청하고, 응답 데이터를 `jq`로 파싱한다.

```bash
response=$(curl -s -X POST \
     -H "Content-Type: application/json" \
     -H "Referer: https://leetcode.com/problemset/all/" \
     -H "User-Agent: Mozilla/5.0" \
     -d '{"query": "{ activeDailyCodingChallengeQuestion { date link question { title difficulty questionFrontendId } } }"}' \
     https://leetcode.com/graphql)

echo "$response" | jq -r '.data.activeDailyCodingChallengeQuestion | "Date: \(.date)\nProblem: [\(.question.difficulty)] \(.question.title)\nLink: https://leetcode.com\(.link)"'
```

> 오늘의 문제 제목, 난이도, 문제 링크를 출력해준다.

### 자동 클래스 이름 생성

문제 제목은 그대로 클래스 이름으로 사용할 수 없어서 문제 제목을 기반으로 자바 클래스 이름을 자동 생성하는 기능을 추가했다.  
`ClassNameFactory.java`를 호출해 클래스 이름을 생성하고 클립보드에 복사한다.

```bash
title=$(echo "$response" | jq -r '.data.activeDailyCodingChallengeQuestion | "\(.question.questionFrontendId). \(.question.title)"')
java ./src/main/java/shane/leetcode/util/ClassNameFactory.java "$title"
```

## ClassNameFactory.java

### 클래스 이름 생성 로직

클래스 이름 생성의 핵심은 다음 두 가지를 해결하는 것이다
1. 문제 번호와 제목을 기반으로 클래스 이름을 생성.
2. 공백 및 특수문자 제거, 단어의 첫 글자를 대문자로 변환하여 자바 네이밍 컨벤션 준수.

예를 들어, **"1. Two Sum"**이라는 제목은 다음과 같이 변환하도록 했다.
- 숫자: `Q1`
- 단어: `TwoSum`
- 최종 클래스 이름: `Q1TwoSum`

아래는 해당 로직을 처리하는 코드다

```java
public String getClassName(String str) {
    int firstDotIndex = str.indexOf(".");
    String number = str.substring(0, firstDotIndex);
    String title = str.substring(firstDotIndex + 1).trim();

    StringBuilder sb = new StringBuilder("Q" + number);
    if (Character.isDigit(title.charAt(0))) {
        sb.append("_");
    }
    for (String s : title.split(" ")) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (INVALID_CHARACTERS.contains(c)) continue;
            if (i == 0 && Character.isLowerCase(c)) {
                c -= 'a' - 'A';
            }
            sb.append(c);
        }
    }
    return sb.toString();
}
```

### 클립보드 복사 기능

생성된 클래스 이름은 클립보드에 자동 복사된다.  `Toolkit`과 `Clipboard`를 활용해 OS에 독립적으로 동작하도록 구현했다.

```java
StringSelection stringSelection = new StringSelection(className);
clipboard.setContents(stringSelection, null);
```

## 실행

1. **저장소 클론**
   
   ```bash
   git clone git@github.com:ShanePark/problem-solving.git
   cd problem-solving
   ```
   
2. **Alias 설정**
   
   ```bash
   alias leet='bash $(pwd)/leet.sh'
   ```
   
3. **실행**
   ```bash
   leet
   ```

위 명령어를 실행하면, 아래와 같은 출력이 나온다:
```
Date: 2024-11-16
Problem: [Easy] Two Sum
Link: https://leetcode.com/problems/two-sum
Class name : Q1TwoSum
class name has been copied to your Clipboard!
```

이제 IDE에서 바로 클래스 이름을 붙여넣고 코딩을 시작할 수 있다.

Alias를 영구적으로 사용하려면 `.bashrc`나 `.zshrc`에 추가해주면 된다.
```bash
alias leet='bash /your/absolute/path/to/leet.sh'
```

## 결론

이 프로그램을 활용하면 매일 LeetCode 문제 풀이 시작이 훨씬 간편해진다.  
자바 11 이상 환경에서는 컴파일 없이 `ClassNameFactory.java`를 바로 실행할 수 있어 설정도 간단하다.

작성한 코드는 https://github.com/ShanePark/problem-solving 에 공개되어있다.

끝

**References**

- https://github.com/shuzijun/leetcode-editor/blob/653345c110f6867e5824cbb28f63328894e80142/src/main/resources/graphql/questionOfToday.graphql#L4
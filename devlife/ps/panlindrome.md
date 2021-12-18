# 문제풀이: 가장 긴 팬린드롬(palindrome)

![image-20211218160705282](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devlife/ps/panlindrome.assets/image-20211218160705282.png)

## Intro

프로그래머스 3단계에 해당하는 가장 긴 팬린드롬 문제를 풀어보았습니다. 

팰린드롬은 앞으로 읽어도, 반대로 읽어도 똑같은 단어를 말하는데요, `기러기`, `스위스`등이 있습니다. 2020년에 개봉한 크리스토퍼 놀란 감독의 영화 `TENET`에서도 영화 전반에 걸쳐 palindrome의 이미가 녹아들었으며, 그 제목 자체도 팬린드롬 이였습니다.

문제가 워낙에 간단하기 때문에 금방 풀 거라고 생각 했는데, 몇가지 간과했던 점들이 있기 때문에 총 4번의 시도 끝에 풀이 하였습니다.

특별한 알고리즘이 필요한 문제는 아니지만 `효율성 체크`가 기다리고 있는 문제이기 때문에 제법 고민이 필요합니다.

## 문제

문제의 조건 자체는 굉장히 간단합니다. `leetcode.com` 에서는 공개된 2107개의 테스트 중 무려 5번째에 위치한 문제로 굉장히 오래되어 전세계의 많은 분들이 이미 풀이 하였습니다.

프로그래머스 문제

```
앞뒤를 뒤집어도 똑같은 문자열을 palindrome 이라고 합니다.
문자열 str이 주어질 때, str의 부분 문자열 중 가장 긴 palindrome의 길이를 반환하는 solution 함수를 완성하세요.
예를 들어 s가 "deleveled" 라면, 9을 반환하고, safari라면 3을 반환합니다.
- s의 길이는 2500 이하의 자연수
- 문자열 s는 알파벳 소문자로만 구성
```

> leetcode 에서의 문제와 아주 조금 다른데요, leetcode 에서는 가장 긴 panlindrom그 자체를 반환하는 문제 이지만, 프로그래머스에서는 그 길이를 반환하는 문제 입니다. 
>
> 프로그래머스: https://programmers.co.kr/learn/courses/30/lessons/12904
>
> leetcode: https://leetcode.com/problems/longest-palindromic-substring/

## 풀이

### 1차시도

첫번째에는, 부담 없이 바로 머리 속에 떠오른 방법을 구현 해 보았습니다. 

단순하게 주어진 문자열 `s`의 첫번째 index부터 시작해서 맨 끝 index까지 순회 하면서, 해당 인덱스 마다 좌,우로 최대한 늘릴 수 있는 데까지 확인 하며 해당 문자열이 palindrome의 조건을 만족하는지를 확인 했습니다. 

예를 들어 `deleveled`를 확인한다면, 맨처음 d는 앺 앞에 위치했기 때문에 펼치지 못하지만, 쭉쭉 지나 4번 인덱스인 `v` 자리를 순회 할 때에는 `v`, `eve`, `level`, `elevele`, `deleveled` 까지 펼치며 최대 7자리까지 만족하는 문자열을 구할 수 있습니다. 문자열을 좌 우로 늘리다가 팬린드롬을 만족하지 못할 때에는 즉각 해당 길이를 반환합니다.

그렇게 주어진 문자열로 만들 수 있는 모든 팬림드롬을 확인 하고, 최대의 길이를 반환 합니다. 사실 3단계기 떄문에 이렇게 쉽게 풀이되지 않겠다고 생각 하면서도 나름 효율성이 크게 떨어지지 않기 때문에 기대도 해 보았는데요.

```java
    public static int solution(String s) {
        int max = 0;
        for (int i = 0; i < s.length(); i++) {
            max = Math.max(max, calc(s, i));
        }
        return max;
    }

    private static int calc(String s, int k) {
        int length = 1;
        int i = 0;
        while (true) {
            i++;
            try {
                if (s.charAt(k + i) == s.charAt(k - i)) {
                    length += 2;
                } else {
                    return length;
                }
            } catch (StringIndexOutOfBoundsException e) {
                return length;
            }
        }
    }
```

```

/**
 * 채점을 시작합니다.
 * 정확성  테스트
 * 테스트 1 〉	통과 (0.11ms, 78MB)
 * 테스트 2 〉	통과 (0.11ms, 77.4MB)
 * 테스트 3 〉	통과 (0.18ms, 77.1MB)
 * 테스트 4 〉	실패 (0.17ms, 72.6MB)
 * 테스트 5 〉	통과 (0.18ms, 73.1MB)
 * 테스트 6 〉	실패 (0.17ms, 76.5MB)
 * 테스트 7 〉	실패 (0.15ms, 78MB)
 * 테스트 8 〉	통과 (0.15ms, 75.4MB)
 * 테스트 9 〉	통과 (0.70ms, 73MB)
 * 테스트 10 〉	통과 (0.34ms, 70.9MB)
 * 테스트 11 〉	통과 (0.63ms, 77.3MB)
 * 테스트 12 〉	실패 (0.73ms, 75.5MB)
 * 테스트 13 〉	통과 (0.16ms, 70.8MB)
 * 테스트 14 〉	통과 (0.22ms, 71.9MB)
 * 테스트 15 〉	통과 (0.23ms, 73.1MB)
 * 테스트 16 〉	통과 (0.29ms, 76.3MB)
 * 테스트 17 〉	통과 (0.09ms, 78MB)
 * 테스트 18 〉	통과 (0.10ms, 83.2MB)
 * 테스트 19 〉	통과 (0.19ms, 76.8MB)
 * 테스트 20 〉	통과 (0.40ms, 73.4MB)
 * 테스트 21 〉	통과 (0.35ms, 74.1MB)
 * 효율성  테스트
 * 테스트 1 〉	실패 (1.20ms, 52.1MB)
 * 테스트 2 〉	통과 (38.25ms, 54.3MB)
 * 채점 결과
 * 정확성: 56.1
 * 효율성: 15.3
 * 합계: 71.5 / 100.0
 */
```

정확성 `17/21` 효율성 `1/2` 로 총 71.5 점을 받았습니다.

효율성은 그렇다 쳐도 정확성은 통과 할 줄 알았는데 의외였습니다.

### 2차시도

그래서 문제의 조건을 다시 꼼꼼히 읽어보며 풀이한 방법에 어떤 문제가 있을 지를 고민 해 보았습니다. 

곰곰히 생각해보니 이 방법으로는, `ABBA`와 같이 문자열의 길이가 짝수로 나오는 팰린드롬을 전혀 잡아 낼 수 없다는걸 알아냈습니다.

그래서 이번에는 좌.우로 문자열을 늘려보며 확인하지 말고, 만들 수 있는 모든 문자열을 만들어 보면서 해당 문자열이 팰린드롬 조건을 만족하는지를 확인 해 보도록 코드를 작성 해 보았습니다.

 효율성은 전혀 기대할 수 없습니다만 일단 문제의 전제조건을 빠짐 없이 파악했는지를 확인 해 보기 위해 작성한 코드 입니다.

```java

    public static int solution(String s) {
        int max = 1;
        for (int i = 0; i < s.length(); i++) {
            for (int j = 0; j < s.length() - i; j++) {
                String str = s.substring(i, i + j + 1);
                if (str.length() == 1) continue;
                if (isPalindrome(str)) {
                    max = Math.max(max, str.length());
                }
            }
        }
        return max;
    }

    private static boolean isPalindrome(String str) {
        for (int i = 0; i < str.length() / 2; i++) {
            if (str.charAt(i) != str.charAt(str.length() - 1 - i)) {
                return false;
            }
        }
        return true;
    }
```

```
* 효율성  테스트
* 테스트 1 〉	실패 (시간 초과)
* 테스트 2 〉	실패 (시간 초과)
* 채점 결과
* 정확성: 69.3
* 효율성: 0
* 합계: 69.3 / 100.0
```

드디어 정확성 테스트는 모두 통과되었습니다.

### 3차시도

`substring`으로 새로운 문자열을 만들어 내는게 비용이 크니, 굳이 문자열을 새로 만들지 말고, 기존에 받은 파라미터에 잘라낼 문자의 시작, 끝 지점만 파라미터로 넘겨서 같은 작업을 해 보았습니다.

```java
    public static int solution(String s) {
        int max = 1;
        for (int i = 0; i < s.length(); i++) {
            for (int j = 0; j < s.length() - i; j++) {
                max = Math.max(max, isPalindrome(s, i, i + j + 1));
            }
        }
        return max;
    }

    private static int isPalindrome(String str, int start, int end) {
        int length = end - start;
        for (int i = 0; i < length / 2; i++) {
            if (str.charAt(i + start) != str.charAt(end - 1 - i)) {
                return 0;
            }
        }
        return length;
    }
```

```java
 * 채점 결과
 * 정확성: 69.3
 * 효율성: 15.3
 * 합계: 84.7 / 100.0
```

효율성 문제를 하나 더 넘길 수는 있었지만, 알고리즘을 그대로 가져가기엔 부족합니다. 만들 수 있는 문자열을 전부 다 확인 하지 않는 방법을 고안해야 합니다.

### 4차시도

그래서 이번에는 맨 앞에서부터 순회하는게 아니고, 주어진 문자열로 만들 수 있는 문자열의 최대 길이부터 시작해서 한개씩 줄여 가며 팰린드롬 조건을 만족 하는지를 확인 하도록 했습니다.

 이렇게 하면 굳이 최대 길이를 기록해가며 순회 할 필요가 없고, 만나자마자 그냥 반환하면 되니 모든 문자열을 뒤질 필요도 없습니다.

```java
public static int solution(String s) {
        for (int len = s.length(); len > 1; len--) {
            for (int j = 0; j + len <= s.length(); j++) {
                if (isPalindrome(s, j, j + len)) {
                    return len;
                }
            }
        }
        return 1;
    }

    private static boolean isPalindrome(String str, int start, int end) {
        int length = end - start;
        for (int i = 0; i < length / 2; i++) {
            if (str.charAt(i + start) != str.charAt(end - 1 - i)) {
                return false;
            }
        }
        return true;
    }
```

![image-20211218163722366](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devlife/ps/panlindrome.assets/image-20211218163722366.png)

드디어 4차 시도만에 통과 했습니다.

### leetcode

내친김에 바로 `leetcode.com` 에 들어가서 같은 알고리즘으로 문제 풀이를 해 보았습니다.

![image-20211218161257293](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devlife/ps/panlindrome.assets/image-20211218161257293.png)

통과를 하긴 했지만..

![image-20211218164011470](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devlife/ps/panlindrome.assets/image-20211218164011470.png)

![image-20211218164132227](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devlife/ps/panlindrome.assets/image-20211218164132227.png)

메모리 사용은 상위 5% 안에 들만큼 적게 사용 했지만 효율성이 한참 떨어집니다. 442ms가 나왔는데, 25ms대에 대부분의 사용자가 모여있는 걸 보면  `O(n^3)` 으로 풀지 말고  `O(n^2)`로 풀이 했어야 하는 듯 합니다.

### leetcode 2차

처음에 시도했던 좌우로 펼치며 풀이했던 방법을 응용해서 코드를 작성 해 보았습니다.

```java
    public static String longestPalindrome(String s) {
        int start = 0, end = 0;
        for (int i = 0; i < s.length(); i++) {
            int length1 = calc(s, i, i);
            int length2 = calc(s, i, i + 1);
            int length = Math.max(length1, length2);
            if (length > end - start) {
                start = i - (length - 1) / 2;
                end = i + length / 2;
            }
        }
        return s.substring(start, end + 1);
    }

    private static int calc(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        return right - left - 1;
    }
```

![image-20211218170318495](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devlife/ps/panlindrome.assets/image-20211218170318495.png)

> Runtime이 확연 하게 줄어들었습니다.

## 마치며

Manacher's algorithm 을 이용하면 심지어 O(n)으로도 풀이가 가능하다고 합니다.

> [https://en.wikipedia.org/wiki/Longest_palindromic_substring#Manacher's_algorithm](https://en.wikipedia.org/wiki/Longest_palindromic_substring#Manacher's_algorithm)

사실 열심히 읽어 보았지만 점점 골치가 아파오기에 나중에 알고리즘을 공부 할 때 한번 다시 둘러보려고 합니다.

 




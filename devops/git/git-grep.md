# [Git] 특정 코드 변경 사항 검색하기

## Intro

Pebble Template 에는 Macro라는 기능이 있습니다. 특정 콘텐츠 블록을 재 사용 가능한 함수처럼 만들어 주는 건데, 이전에 코드를 수정하다 보니 macro를 통째로 날려버렸는지 사용하는 부분만 남아있고 선언된 부분이 없어서 코드가 깨져 있는 부분을 발견했습니다.

`facet()` 이라는 이름의 매크로인데, 어디에 선언되어 있었는지를 정확히 알지 못해서 커밋 히스토리를 찾아내는데 어려움을 겪고 있었습니다. 

Git 로그에서 파일 변경 내용 중 특정 키워드를 검색하는 방법에 대해서 알아보겠습니다.

## Git grep

단순 `git grep <정규식표현>`을 활용하면 해당 검색 조건이 포함된 파일들을 찾아 줍니다.

예를 들어 아래와 같이 입력 하면

```bash
git grep shane
```

![image-20220915144900232](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/git-grep.assets/image-20220915144900232.png)

> shane 이라는 텍스트가 들어간 모든 파일을 찾아 줍니다.

하지만 이 방법으로는 이미 삭제된 코드를 검색 할 수는 없습니다.

## git log --grep

이번에는 로그에서 검색 하는 방법입니다.

깃 커밋 메시지에서 원하는 단어를 찾아 줍니다.

```bash
git log --grep test
```

![image-20220915145129087](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/git-grep.assets/image-20220915145129087.png)

이 방법은 로그가 찾고자 하는 키워드를 포함하고 있을 때에만 사용 할 수 있습니다.

## 깃 로그에서 변경 사항 검색

### git grep

이번에는 이번 글에서 목표로 하는 `이미 사라진 코드의 변경 검색`을 해 보도록 하겠습니다.

이 역시 `git grep`명령을 활용 하는데요, 현재 코드, 커밋 메시지 등등 모든 항목을 다 찾아 줍니다.

최근 커밋에서 제거된 `fun createQuiz(quiz: Quiz, arr: Array<String>): Quiz` 메서드를 grep 으로 검색해보면 아무 검색 결과가 나오지 않는 것을 확인 할 수 있는데요

```bash
git grep 'fun createQuiz(quiz: Quiz, arr: Array<String>): Quiz'
```

![image-20220915150025637](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/git-grep.assets/image-20220915150025637.png)

![image-20220915150019819](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/git-grep.assets/image-20220915150019819.png)

이 때, 마지막에 `$(git rev-list --all)` 를 추가 해 주면 

```bash
git grep 'fun createQuiz(quiz: Quiz, arr: Array<String>): Quiz' $(git rev-list --all)
```

![image-20220915150238172](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/git-grep.assets/image-20220915150238172.png)

전체 커밋 목록에서, 해당 코드가 존재했던 커밋들을 모두 찾아내기 때문에 어렵지 않게 삭제된 시점을 찾아 낼 수 있습니다. 물론 검색 시 정규식을 사용할 수 도 있습니다.

### log -G

이번에는 Git 1.7.4 버전에서 추가된 `log -G` 옵션을 활용해 보겠습니다.

G 옵션을 적용하면 정확히 해당 코드의 변경이 일어난 커밋을 찾아 낼 수 있습니다. 정규식을 받기 때문에 괄호 등 escape 처리 해야 하는 문자를 신경 써 주어야 합니다.

```bash
git log -G 'fun createQuiz\(quiz: Quiz, arr: Array<String>\): Quiz'
```

![image-20220915151021785](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/git-grep.assets/image-20220915151021785.png)

실행 결과, 해당 코드가 추가되었던 커밋과, 삭제된 커밋을 각각 정확히 찾아 내어 알려 주었습니다.

이상입니다.

**References**

- https://stackoverflow.com/questions/2928584/how-to-grep-search-committed-code-in-the-git-history
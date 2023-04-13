# [Git] lightweight 태그와 annotated Tags

## Intro

진행중인 프로젝트의 본격적인 버전 관리에 앞서 해당 스프링부트 어플리케이션에서 git 정보를 토대로 최근 커밋 해시값, 날짜, 버전(태그명) 등을 불러 올 수 있게끔 기능을 추가 해 두었습니다.

그런데 아무리 새로운 태그를 달아 주어도 이전의 태그명이 나오는 문제가 있었고 약간의 검색을 통해 그 차이가 lightweight 와 annotated 때문이었다는걸 알게 되었습니다.

<img src=https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/tag.assets/image-20220627142025709.webp width=633 height=316 alt=1>

> 분명 `describe --tags` 를 입력 할 때는 4.2 버전이 나오지만

<img src=https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/tag.assets/image-20220627142054144.webp width=633 height=316 alt=2>

> `git describe` 만 입력 했을때는 가장 가까운 3.2 버전으로부터 128 번째 커밋이라는 정보가 나왔습니다.

describe 명령은 커밋에서 도달 할 수 있는 가장 최근의 태그를 찾는데요, 태그가 커밋을 가리키는 경우에는 태그만 표시되지만, 그렇지 않을 경우에는 `<최근태그>-<커밋수>-g<현재 커밋 해시>` 형태로 표시 됩니다.

최근의 태그인 `v3.2` 로 부터 128 번째 커밋인 `046befa` 라는 뜻 인거죠.

그런데 왜 v4.2가 아닌 v3.2가 나오느냐 하면, `git describe` 명령은 기본적으로 annotated tag만 표시하기 때문입니다.

## Tags

Git은 lightweight 와 annotated 두가지의 태그를 지원 합니다. 

경량 태그(Lightweight tag)의 경우에는 이름만 붙일 수 있는 반면, 주석 태그(Annotated tag)의 경우에는 그 외에 태그에 대한 설명, 서명, 그리고 태그 생성자 및 날짜에 대한 정보도 포함 시킬 수 있습니다.

일반적으로는 annotated 태그를 생성하는게 권장되지만, 임시로 태그를 생성한다거나 추가적인 정보를 제공하고 싶지 않을 때에는 lightweight 태그를 사용 할 수도 있습니다.

### Annotated Tags

tag 명령을 실행 할 때  간단히 `-a` 옵션만 붙이면 주석 태그를 생성 할 수 있습니다.

```bash
git tag -a v4.0 -m "version 4.0"
```

`-m` 옵션으로 메시지를 함께 저장 할 수 있으며, 메시지가 따로 입력 되지 않으면 명령 실행 시점에 Git이사용자의 편집기를 실행해 메시지를 입력 받습니다.

이렇게 생성한 태그는 `git show` 명령을 실행 했을 때, 태그에 관련된 데이터를 확인 할 수 있습니다.

![image-20220627145952739](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/tag.assets/image-20220627145952739.webp)

> `git show v4.0`

### Lightweight Tags

또 하나의 태그 작성 옵션은 경량 태그 입니다. 단순하게 파일에 커밋 체크섬을 저장할 뿐 다른 정보는 저장하지 않습니다.

태그를 생성 할 때 `-a`, `-s`, `-m` 옵션을 따로 사용하지 않고 이름만 입력해 태그를 생성합니다

```bash
git tag v4.0
```

이렇게 생성한 태그는 `git show` 명령을 실행 해도 별도의 태그에 관한 정보는 확인 되지 않고 커밋 정보만 확인 할 수 있습니다.

처음 문제로 돌아가, git describe를 했을 때에 계속 태그를 3.2로 찾았던건, 제가 추가한 4.0 태그가 경량 태그인 반면, 3.2 태그는 주석 태그였기 때문입니다.

그럼 여러 가지 명령어를 실습 해보며 상황을 해결 해 보도록 하겠습니다.

## 명령어

### 태그정보 삭제

삭제를 원할 때에는 간단히 -d 옵션으로 태그를 삭제 할 수 있습니다.

```bash
git tag -d [삭제할 태그명]
```

![image-20220627144411160](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/tag.assets/image-20220627144411160.webp)

> 기존의 v4.0 태그를 제거 했습니다.

### 나중에 태그 추가

이번에는 원래 태그가 달려있었던 `0163921c` 커밋에 다시 4.0 태그를 달아 보도록 하겠습니다.

기존에는 lightweight 태그였지만 이번에는 잊지 않고 annotated tag로 달아야겠습니다.

```bash
git tag -a [태그명] [체크섬]
# git tag -a v4.0 0163921c -m "Spring Boot 2.x"
```

![image-20220627144845223](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/tag.assets/image-20220627144845223.webp)

> annotated tag 를 새로 달고 난 뒤에 `git describe` 를 하니 정상적으로 버전이 표시됩니다.
>
> 마찬가지로 4.2 버전도 경량태그를 삭제 하고 annotaed 태그를 생성 해 주면 됩니다.

### 태그 정보 Push

기본적으로 `git push` 명령은 리모트 서버에 태그에 대한 정보를 전송 하지 않습니다.

태그 정보를 공유 하려면 태그 생성 후에 별도로 push 해 주어야 하는데요, remote branch를 공유할 때와 비슷합니다.

```bash
git push origin [태그명]
```

한번에 여러개의 태그 정보를 push 하고 싶다면 간단하게 `--tags` 옵션을 붙여 주면 됩니다.

```bash
git push origin --tags
```

이상으로 Git 에서 제공하는 두가지 태그와 그 차이점에 대해 알아 보았습니다.

문제를 해결하시는데 도움이 되었으면 합니다. 감사합니다.

**references**

- https://git-scm.com/docs/git-describe/2.9.5

- https://git-scm.com/book/en/v2/Git-Basics-Tagging
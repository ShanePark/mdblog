# 원격저장소 추가 - git remote add

## 개요

`add remote`는 로컬 git 저장소에 원격 저장소를 추가 하는 명령입니다. 보통, git init 이후에 Github 이나 Gitlab의 원격 저장소를 추가하기 위해 사용하는 명령어 인데요, 이미 특정 원격 저장소와 연결이 되어 있을때, 해당 저장소와의 연결을 끊고 다른 저장소와 새로 연결을 하거나 혹은 한번에 여러 개의 저장소에 push 하는 등 여러가지 용도로 사용할 수 있는 명령어 입니다.

## Repository 생성

원격 저장소를 추가하기 위해서 그에 앞서 원격 저장소를 먼저 생성 하겠습니다.

아래 사진의 예제는 각각 Gitlab과 Github입니다. 어렵지 않게 새로운 저장소를 만드실 수 있습니다.

![image-20211125095445626](/home/shane/Documents/git/mdblog/devops/git/remote-add.assets/image-20211125095445626.png)

![image-20211125124244286](/home/shane/Documents/git/mdblog/devops/git/remote-add.assets/image-20211125124244286.png)

> Create repository를 눌러서 생성

Gitlab에서는 저장소를 생성 했을 때, readme 를 추가 하지 않고 완전 비운 상태로 생성하니 아래와 같은 instruction을 보여주었습니다.

덕분에 어렵지 않게 필요한 내용들을 찾아 보고 힌트를 얻을 수 있는데, 얼마 전 나스닥에 상장한 Gitlab의 센스가 돋보입니다.

![image-20211125100954245](/home/shane/Documents/git/mdblog/devops/git/remote-add.assets/image-20211125100954245.png)

## 원격저장소 추가

저는 이미 다른 원격 저장소와 연결 되어 있던 Git저장소를 가지고 새로운 원격 저장소를 추가 해 보겠습니다.

![image-20211125100212265](/home/shane/Documents/git/mdblog/devops/git/remote-add.assets/image-20211125100212265.png)

### 원격 저장소 추가 방법

```zsh\
git remote add origin [추가할 원격 git 저장소 주소]
# 아래의 두 예시는 각각 https 와 ssh 입니다.
git remote add origin https://github.com/Shane-Park/playddit.git
git remote add origin git@github.com:Shane-Park/playddit.git
```

이후 기존의 원격 저장소가 없어서 원격 저장소가 추가가 되었다면 아래와 같이 commit 및 push를 진행 해 주면 됩니다.

```zsh
git add .
git commit -m "커밋메시지"
git push origin master

```

하지만 이미 기존의 원격저장소가 등록 되어 있는 경우에는

![image-20211125125907662](/home/shane/Documents/git/mdblog/devops/git/remote-add.assets/image-20211125125907662.png)

> 이미 origin 이라는 이름의 remote가 있기 때문에 추가가 되지 않습니다.
>
> fatal: remote origina already exists.

### 원격저장소 목록 조회

아래의 명령어를 입력해 (-v 혹은 -verbose) 원격 저장소 목록을 확인 합니다.

```zsh
git remote -v
```

![image-20211125125947416](/home/shane/Documents/git/mdblog/devops/git/remote-add.assets/image-20211125125947416.png)

원격 저장소 추가는 크게 세가지 방법이 있겠네요.

1) 기존 원격 저장소 그대로 두고 추가하는 방법

> 또 다른 이름으로 remote를 추가 해 줍니다. second 라는 이름으로 추가 한다고 가정하면 아래 명령어 처럼 입력합니다.

```zsh
git remote add second git@github.com:Shane-Park/playddit.git
```

2. 기존 원격 저장소 이름을 old-origin으로 변경하고 origin으로 추가하는 방법

```zsh
cd existing_repo
git remote rename origin old-origin
git remote add origin git@github.com:Shane-Park/playddit.git
git push -u origin --all
git push -u origin --tags

```

3. 기존 원격저장소를 삭제하고 새로 추가하는 방법

```zsh
git remote remove origin
git remote add origin git@github.com:Shane-Park/playddit.git

```

저는 기존의 원격 저장소가 필요 없어서 3번을 선택 했습니다. 여러개의 원격 저장소가 추가된다면 혼동이 올 수도 있습니다.

![image-20211125101541590](/home/shane/Documents/git/mdblog/devops/git/remote-add.assets/image-20211125101541590.png)

## 소스 반영

그냥 git push를 했더니 upstream branch가 설정되어있지 않기 때문에 에러가 발생했습니다.

```zsh
git push
```

![image-20211125101643076](/home/shane/Documents/git/mdblog/devops/git/remote-add.assets/image-20211125101643076.png)

-u 옵션을 붙여 push 합니다. 

> -u는 --set-upstream과 같은 역할 을 합니다.
>
> For every branch that is up to date or successfully pushed, add upstream (tracking) reference, used by argument-less [git-pull[1\]](https://git-scm.com/docs/git-pull) and other commands.

```zsh
git push -u origin master
```

![image-20211125101839398](/home/shane/Documents/git/mdblog/devops/git/remote-add.assets/image-20211125101839398.png)

보통의 경우는 push가 됩니다. 아마 대부분 되셨을텐데요, 저는 `shallow update not allowed` 에러가 발생 했습니다.

그 이유는 해당 git repository 를 clone 할때 `git clone --depth <number>` 명령어를 이용했기 때문인데요,  이제 두가지 선택이 가능합니다.

1. full history를 지키고 싶은 경우

> 이때는 이전 remote를 다시 추가 한 뒤에 `fetch --unshallow` 를 해 줘야 합니다. fetch 후엔 push가 가능합니다.

```zsh
git remote add old <path-to-old-remote>
git fetch --unshallow old

```

2. history에 대해서 신경을 쓰지 않을 경우

> .git 폴더를 지워버리고 새로 git init 해서 push 합니다.

아무래도 새로운 저장소로 간다고 해도 이전의 버전관리도 함께 하는게 좋으니 과거의 원격 저장소에 접속을 하지 못하는 경우가 아니라면 1번 옵션으로 진행 하는게 좋겠습니다.

![image-20211125103534899](/home/shane/Documents/git/mdblog/devops/git/remote-add.assets/image-20211125103534899.png)

> 모든 작업을 마치고 Everything up-todate 상태가 되었습니다.

이상입니다.
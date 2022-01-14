# Git) 이전 커밋에 덧붙이기 amend

## Intro

Commit을 하다 보면 적당한 크기로 커밋하는게 쉽지 않다는 걸 많이 느낍니다. 커밋 하나하나의 크기가 너무 작으면, 후에 커밋을 추적하기가 어렵고 커밋의 크기가 너무 크면 중간에 문제가 생기거나 실수를 했을때 돌아가야 할 지점이 너무나도 멀다는 문제가 있습니다. 커밋 하나에 잡다한 여러가지 연관성 없는 기능에 대한 코드가 함께 있어도 코드리뷰를 하는데 불편함을 초래 합니다.

### Commit 을 하고 보니 이게 빠졌네

이건 모두가 한번씩 있는 경험이 아니고 거의 몇일에 한번씩 혹은 커밋이 잦은 편인 저의 경우에는 거의 매일 겪는 일 입니다. 특히 간단한 오타 수정이 가장 흔한 상황 인데요. 그렇다고 오타를 수정 했는데 커밋을 안하고 다음 커밋까지 미루기도 곤란하고, 오타 하나만 달랑 수정하는데 하나의 커밋을 차지하는 것도 참 곤란합니다. 이때, amend 옵션을 활용 하면 정말 깔끔하게 해결 됩니다.

## Amend

`commit --amend`을 활용하면 가장 최근의 커밋을 수정할 수 있습니다. 

stage된 환경과 가장 최근의 커밋을 병합해 하나의 커밋으로 만드는 기능 인데요, 이렇게 새로 생성된 Commit은 기존의 커밋을 완전히 대체 합니다.

### git log

일단 Git log를 확인 해 보겠습니다.

```zsh
git log
```

![image-20220114174831778](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/amend.assets/image-20220114174831778.png)

![image-20220114174849186](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/amend.assets/image-20220114174849186.png)

최근에 `Linux) Oracle VM Virtual Box 창 이동 안될때` 라는 이름으로 커밋을 했는데요, 커밋을 하고 보니 이미지들의 링크를 변경하지 않았다는게 생각 났습니다. 이미지가 로컬 경로로 되어 있다 보니, 글을 올려도 이미지를 불러 오지 못합니다.

### git diff

```zsh
git diff
```

![image-20220114175059526](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/amend.assets/image-20220114175059526.png)

![image-20220114175104576](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/amend.assets/image-20220114175104576.png)

`virtualbox-window-move.md` 파일에서 빨간색으로 표시된 이미지의 링크만 초록색의 새 링크로 변경 하면 되는데요.

이미지 링크만 수정한다고 커밋을 하나 추가할 수는 없습니다. 이전의 커밋에 들어갔어야 하는 내용입니다.

이때, amend 옵션을 통해 쉽게 해결 할 수 있습니다.

### git commit --amend

먼저 add 명령어로 스테이지에 원하는 파일을 올린 후에

```zsh
git add
```

amend 옵션을 붙여 커밋 해 줍니다.

```zsh
git commit --amend
```

![image-20220114175323653](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/amend.assets/image-20220114175323653.png)

> 위의 명령어를 치고 엔터 치면

![image-20220114175426854](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/amend.assets/image-20220114175426854.png)

보이는 것 처럼, 커밋 메시지를 수정 할 수 있는 화면이 나옵니다. 커밋 메시지를 수정 안해도 되지만 저는 약간만 수정 해 보겠습니다.

![image-20220114175504448](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/amend.assets/image-20220114175504448.png)

이후 `:wq`로 저장을 하고 나옵니다.

![image-20220114175538268](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/amend.assets/image-20220114175538268.png)

새로운 커밋으로 변경이 잘 되었습니다.

### push

이제 push를 해줘야 하는데, 문제가 있습니다. 이미 원격 저장소에는 이전의 커밋 내역이 있기 때문에 이상태에서 그냥 push 한다면, Pull(원격저장소에서 로컬로) 과 Push(로컬에서 원격저장소로)를 각각 한번씩 하게 됩니다.

지금 해야 할 일은 원격 저장소의 커밋 내역도 로컬의 커밋 내역과 맞추는 것 입니다. 이때는 강제 update 를 해줘야 합니다.

```zsh
git push -f
```

![image-20220114175758092](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/amend.assets/image-20220114175758092.png)

그러면 원격 저장소에도 원하는 대로 커밋 덮어쓰기가 이루어 진 것을 확인 할 수 있습니다.

amend 옵션을 몰랐을 때는 커밋 할 때마다 괜히 정말 다 끝난건가? 싶기도 하고, 괜히 나중에 오타 발견하면 무의미한 커밋 하나 하며 스트레스 받곤 했지만 `git commit --amend` 옵션을 알고 있다면 훨씬 깔끔한 Commit history를 작성 할 수 있습니다.

다만 주의할 점은 main branch의 경우에는 protected 해두면 force update가 되지 않기 때문에 여러명이서 함께 하는 프로젝트에서는 개별 branch에서 충분히 작업 한 후 모아서 Pull Request를 보내면 되겠습니다.


# Rebase 활용해 Commit 합치기

## Rebase

Rebase는 한 branch에 있는 내용을 다른 branch에 병합하는 두가지 Git 유틸리티 중 하나 입니다. 다른 하나는 이미 모두가 사용하고 있는 `git merge` 입니다. merge는 항상 이후로 향하는 변경인 반면에, rebase는 git의 history를 다시 작성 하는 강력한 기능을 가지고 있습니다. 

Rebase는 `manual`과 `interactive` 이렇게 두 가지 메인 모드를 가지고 있습니다.

여러가지 역할이 가능 한 rebase 지만 이번 posting에서는 이미 커밋한 몇개의 커밋을 하나로 합치고 새로운 커밋 메시지를 남기는 용도로 사용 해 보겠습니다.

<br/><br/>	

## 문제	

커밋을 했는데 깜빡한게 있는 경우가 생겼습니다.

<img src=https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/rebase.assets/image-20211103114930659.webp width=500 height=175 alt=1>

19 분 전에 커밋을 하자 마자 코드가 너무 지저분 하다는 생각이 들어 조금의 수정을 했는데요, 원래대로라면 하나의 커밋으로 남길 건데, 의도치 않게 두개가 되었습니다.

<br/><br/>

## 해결

Ubuntu 에서는 Ctrl + ` 를 입력하면 Github Desktop 에서 Terminal을 켤 수 있습니다. 어떤 방법으로도 상관 없으니 본인이 사용하시는 Ternimal을 켜시고, git 저장소가 있는 폴더로 이동해주세요.

![image-20211103115123407](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/rebase.assets/image-20211103115123407.webp)

<br/><br/>

![image-20211103115401522](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/rebase.assets/image-20211103115401522.webp)

Terminal이 커졌습니다. 작업중인 branch 이름을 확인 하고, 아래의 명령어를 입력 합니다.

병합하고 싶은 branch가 2개 보다 많다면 `~2` 대신 그 숫자를 기입 해 주시면 됩니다. 

<br/><br/>	

저는 2개의 미리 push까지 마친 커밋을 병합 할 예정입니다.

```bash
git rebase -i HEAD~2
```

![image-20211103115546309](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/rebase.assets/image-20211103115546309.webp)

기존에 git 편집기로 지정해 둔 편집기가 뜹니다. 예상하지 못했던 nano 에디터가 켜져서 당황했습니다.

<br/><br/>	

편집기가 뜨면 이제 합치거나 편집할 commit을 선택해줍니다.

![image-20211103115856622](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/rebase.assets/image-20211103115856622.webp)

아래 있는 커밋을 위에 있는 커밋으로 합치겠다는 의미에서 앞의 pick 을 s 로 바꿔줍니다. 커밋이 여러개라면, 하나의 커밋만 납두고 나머지 커밋들을 다 s 로 변경해주면 됩니다.

평소 vi 에디터를 쓰는데 nano가 켜져 단축키를 몰라 당황했지만 ctrl+s 로 저장하고, ctrl+x 로 종료 할 수 있었습니다. vim 이라면 `wq!`를 입력 해 주면 됩니다.

<br/><br/>![image-20211103115935727](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/rebase.assets/image-20211103115935727.webp)

이번에는 `COMMIT_EDITMSG`가 뜹니다.이제 새로운 커밋 메시지를 작성할 수 있습니다.

<br/><br/>

![image-20211103120034441](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/rebase.assets/image-20211103120034441.webp)

원하는 커밋 메시지를 작성 하고, 다시 ctrl+s 로 저장하고 ctrl+x 로 나가면 됩니다. 이미 알고 계시겠지만, 주석이 달린 부분들은 커밋 메세지에 포함되지 않습니다.

<br/><br/>

![image-20211103120111297](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/rebase.assets/image-20211103120111297.webp)

rebase가 완료 되었습니다.

<br/><br/>

push를 할 차례인데, 이미 push 되었던 커밋들이므로, 강제로 push 해줍니다. -f 혹은 --force 라고 옵션을 붙여줍니다.

```java
git push -f
```

<br/><br/>

![image-20211103120214153](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/rebase.assets/image-20211103120214153.webp)

push까지 마쳤습니다. 확인 해보니 두개의 commit이 하나로 합쳐졌습니다.

### 주의

![image-20211129142609379](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/rebase.assets/image-20211129142609379.webp)

참고로, Remote Repository 의 해당 Branch 가 보호되어 있으면 Push할 때 -f 옵션이 불가능 할 수 있습니다.

위의 스크린샷은  Gitlab의 예인데, Allowed to force push 를 허용하지 않은 상태 입니다. 이 경우에는 강제 Push가 불가능합니다.

해당 옵션을 잠깐 켜고 강제 푸쉬하는 방법이 있지만, 이 경우에는 브랜치를 보호하려는 목적에 위배 될 수 있으니, Remote Branch에 Push 하기 전에 최대한 신중하게 확인 하는 것이 좋겠습니다.

### Git 기본 편집기 변경

마지막으로 git 기본 편집기를 nano 에서 vim 으로 변경하고 마치겠습니다.

```bash
git config --global core.editor "vim"
```


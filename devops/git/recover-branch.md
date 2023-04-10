# Git) 실수로 삭제한 Branch 복구하기

## Intro

Pull Request를 기다리다가, merge가 되었다고 착각하고 커밋 했던 브랜치를 삭제해 버렸습니다..

로컬과 remote 모두에서 삭제 했기 때문에 원래대로라면 데이터를 날려먹은게 맞지만, 다행히도 복구하는 방법이 있습니다.

## branch 생성 및 삭제

같은 상황을 만들기 위해 branch를 생성 해서 커밋 한 후 삭제 하겠습니다. 이미 branch가 삭제되어 복구가 필요한 분은 아래로 스크롤을 내려 `삭제한 branch 복구` 를 확인하시면 됩니다.

### branch 생성 및 commit & push

`dev` 라는 branch를 만들어서, gitbranch.txt 파일을 추가해 commit 및 push 까지 마친 상태입니다.

![image-20220112133947192](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/recover-branch.assets/image-20220112133947192.webp)

이 상태에서는 dev에 커밋이 되었지만, 해당 사항이 master에 까지 반영은 되지 않았습니다.

### branch 삭제

삭제를 위해 master 브랜치를 체크아웃 한 뒤에 dev 브랜치를 삭제 합니다.

```bash 
git branch -d dev
```

![image-20220112134339113](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/recover-branch.assets/image-20220112134339113.webp)

그러면, 로컬에서는 dev branch가 삭제 되었지만, 아직 원격저장소에는 dev가 있습니다.

> checkout을 하려고 보면 origin/dev가 있는것을 확인 할 수 있습니다.
>
> ![image-20220112134424209](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/recover-branch.assets/image-20220112134424209.webp)

### remote branch 삭제

```bash
# git push -d <remote이름> <branch이름>
git push -d origin dev
```

위의 명령어를 입력해서 원격 branch도 삭제 할 수 있습니다.

![image-20220112134741139](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/recover-branch.assets/image-20220112134741139.webp)

> 삭제후에는 더이상 origin/dev가 보이지 않습니다.

## 삭제한 branch 복구

하지만 이렇게 원격 저장소에서 까지 깔끔하게 삭제 해 버린 브랜치가 사실 필요한 커밋을 가지고 있다면 참 곤란합니다.

이때는 재빨리 해당 branch를 완벽하게 복구 해 낼 수 있습니다.

`git reflog` 명령어를 입력 합니다.

```bash
git reflog
```

![image-20220112134914567](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/recover-branch.assets/image-20220112134914567.webp)

그러면 최근 작업내용들이 모두 보이는데요, 복구해야 할 commit 혹은 헤드 번호를 확인합니다.

저는 `HEAD@{1}` 에 있는 커밋이 삭제 하기 직전의 branch 입니다.  HEAD@ 옆에 있는 번호를 확인 한 후에, `q` 키를 눌러 reflog를 종료 합니다.

그러고는 아래의 명령어로 branch를 다시 살릴 수 있습니다. `HEAD@{}` 에 들어갈 숫자를 위에서 확인 한 숫자로 정확히 입력 해 주세요.

```bash
# git checkout -b <branch이름> <HEAD@{숫자}> 
git checkout -b 'dev' HEAD@{1}
```

![image-20220112135229989](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/recover-branch.assets/image-20220112135229989.webp)

`dev` branch를 체크 아웃 했다는 메시지가 나옵니다.

한번 commit 이 다시 돌아 왔는지 확인 해 봅니다.

```bash
git log
```

![image-20220112135329188](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/recover-branch.assets/image-20220112135329188.webp)

맨 위에 `branch 복구 테스트` 라는 이름으로 했던 커밋이 다시 돌아 와 있습니다. 

다시 날려 먹기 전에 push를 해줍니다.

```bash
git push --set-upstream origin dev
```

![image-20220112135414989](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/recover-branch.assets/image-20220112135414989.webp)

모든 복구가 완료되었습니다!

버전 관리를 하다가 실수로 날려먹었다고 해도 당황하지 말고 차근차근 복구 한다면 아무런 문제 없이 다시 살릴 수 있으니 너무 걱정 하지 않으셔도 되겠습니다. 하지만 애초에 이런 실수를 하지 않는 편이 조금 더 좋겠습니다.
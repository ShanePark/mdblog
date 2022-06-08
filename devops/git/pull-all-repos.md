# Git) 여러개의 Git 저장소 한번에 fetch / pull

## Intro

사용하는 PC 가 여러개 (Linux, MacOS, ...) 있다 보니, 혼자서만 사용하는 Git Repository라고 해도 `fetch` 혹은 `pull` 을 꼬박꼬박 진행 해 주어야 합니다.

가끔씩 fetch / pull 을 까먹은 상태로 커밋을 하고 거기에 `push -f`로 강제 푸시라도 하는 날에는 기존에 작업했던 내용을 잃기도 합니다.

- 코딩을 하다가 자바의 특정 기능을 테스트 해보고 그걸 나중을 위해 기록으로 남겨두는 저장소
- 시간 날 때 알고리즘 문제를 풀어보는 저장소
- 새로움 배움이 있거나 기록할 게 있을 경우 추후 블로그 작성을 위해 메모해 두는 저장소

이 세 저장소는 특히 여러개의 PC에서 commit이 자주 일어나는데요. 특히 출/퇴근 후에는 혹시 모를 실수를 방지하기 위해 모든 저장소를 일괄 fetch/pull 하긴 하는데 여간 번거로운게 아닙니다.

그래서 몇가지 alias와 function을 만들어 보며 테스트를 진행 해 보았고 어느정도 쓸만하게 완성이 된 듯 하여 필요한분들에게 공유 해 보려고 합니다.

## 한번에 여러개의 git 저장소 pull

단순하게 생각하면 `.git`폴더가 있는 디렉터리를 하나 하나 찾아 들어가서 `git fetch` 혹은 `git pull`명령을 실행하는 기능을 만들면 되는 건데요. 범위가 너무 많으면 시간이 오래걸리기 때문에 탐색할 디렉터리를 지정하게끔 만들려고 합니다.

### 현재 폴더내 모든 Git 저장소 pull

일단 기본적으로 현 디렉터리에 존재하는 모든 `.git`을 찾아 pull 하는 코드 입니다.

```bash
find . -type d -name .git -print 2>/dev/null -exec git --git-dir={} --work-tree=$PWD/{}/.. pull \;
```

제 기준으로는 find에 걸리는 시간이 매우 짧아 딱히 필요가 없었지만 sub-directory의 depth를 제한 하고 싶다면 find 명령어에 `-maxdepth 2` 등의 옵션을 줄 수도 있습니다. 

다만 이때는 의도치 않게 배제되는 저장소가 없도록 신경 써 주어야 합니다.

또한 find를 했을 때 특정 폴더들에는 권한이 없어 Permission denied 가 뜨는게 보기 좋지않아 `-print 2>/dev/null` 옵션도 추가했습니다.

> `2>/dev/null` 을 find 명령의 마지막에 붙이면 에러 메시지를 /dev/null로 리다이렉트 시키기 때문에 스크린에 표시되지 않습니다. /dev/null 경로에 작성된 파일들은 시스템에 의해 자동으로 삭제 됩니다.
>
> https://bash.cyberciti.biz/guide//dev/null_discards_unwanted_output

테스트 후에는 `~/.zshrc`에 function 으로 추가해서 간단한 명령으로 바로바로 호출 하게끔 해 보았습니다.

`vi ~/.zshrc` 후 적당한 위치에 작성 해 줍니다.

```bash
function pull {
  find . -type d -name .git -print 2>/dev/null -exec git --git-dir={} --wo    rk-tree=$PWD/{}/.. pull \;
 }
```

이렇게 해두면 이제 원하는 폴더에서 pull 이라고 입력 하면 해당 폴더내의 모든 git repo를 찾아 pull 해줍니다. pull을 원하지 않으면 fetch로 변경 하셔도 됩니다.

> `.zshrc` 파일을 변경 한 후에는 터미널을 껐다 키거나, 아니면 `source ~/.zshrc` 명령을 호출 해서 새로 불러와야 적용 됩니다.

![image-20220527103836750](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/pull-all-repos.assets/image-20220527103836750.png)

> 하위의 모든 git 저장소를 순회하며 pull 해냅니다.

### 특정 폴더하위의 모든 Git 저장소 pull

이번에는 경로를 파라미터로 받아서, 어느 경로에서 호출하든 상관 없이 지정한 폴더내의 깃 저장소들을 모두 pull 하도록 function을 변경 해 보았습니다.

```bash
function pull {
    cd $1;
    find . -type d -name .git -print 2>/dev/null -exec git --git-dir={} --work-tree=$PWD/{}/.. pull \; 
}
```

이렇게 하면 이제 파라미터로 원하는 경로를 받아서, 해당 경로 하위에 있는 저장소들을 모두 pull 해 줍니다.

![image-20220527104038092](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/pull-all-repos.assets/image-20220527104038092.png)

> 루트 경로에서 `pull`을 호출했지만 원하는 대로 작동하는 모습

### 원하는 경로를 환경 변수로 지정

마지막으로 원하는 git 저장소들이 모여있는 폴더를 환경변수로 지정 해 두어서 조금 더 편하게 하도록 해보겠습니다. 업무용 / 개인용으로 사용하는 git 저장소들이 각각 다른 폴더 내에 위치했을때 유용하게 사용 할 수 있습니다.

마찬가지로 `~/.zshrc` 파일을 수정 해 주면 됩니다.

```bash
export shane="/home/shane/Documents/git/shane"
export repos="/home/shane/Documents/git"

function pull {
    cd $1;
    find . -type d -name .git -print 2>/dev/null -exec git --git-dir={} --work-tree=$PWD/{}/.. pull \; 
}
```

> 저는 개인용 폴더인 shane 폴더와 모든 git 저장소를 모아둔 repos를 각각 환경변수로 지정했습니다.

![image-20220527104514344](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/pull-all-repos.assets/image-20220527104514344.png)

이렇게 환경변수 지정까지 완료 한다면, `pull $환경변수이름` 호출로 해당 폴더내의 모든 git 저장소들을 일괄 pull 할 수 있습니다. 

![image-20220528085919992](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/git/pull-all-repos.assets/image-20220528085919992.png)

> Linux 환경 뿐 만 아니라, MacOS 에서도 같은 명령 그대로 정상 동작을 확인 하였습니다.

마지막으로.. 모든 git 저장소가 한 폴더 내에 있고 저장소가 꽤 많다면 아래의 alias 를 참고해 병렬 처리를 고려해보세요. 훨씬 빠르게 처리 해 냅니다.

```bash
alias pull-shane="cd $shane; ls| xargs -P10 -I{} git -C {} pull"
```

이상입니다.

<br>

ref

- https://stackoverflow.com/questions/3497123/run-git-pull-over-all-subdirectories

- https://www.cyberciti.biz/faq/bash-find-exclude-all-permission-denied-messages/
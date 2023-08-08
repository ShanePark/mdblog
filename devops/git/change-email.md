# [Git] 과거 커밋 일괄 이메일 주소 변경

## Intro

가끔씩 Git을 사용하다 보면 이메일 주소가 잘못 입력되어 커밋되는 경우가 생길 수 있다. 

혹은 회사에서 개인 이메일로 잘못 커밋한 경우도 발생하는데 금방 알아차렸다면 수정하기 쉽지만 커밋을 꾸준히 잘못해왔다면 수정하기가 쉽지 않다.

이 글에서는 Git 저장소에서 **특정 이메일 주소로** 커밋된 목록을 확인하고, 해당 커밋들의 이메일 주소를 한번에 모두 변경하는 방법에 대해 알아볼 것이다. 단, 주의할 점이 많으니 명령어를 날리기 전에 항상 신중해야한다.

## 특정 이메일로 커밋한 목록 확인

먼저, 잘못 지정한 이메일 주소로 커밋한 내용이 얼마나 많은지 확인해보자.

```bash
git log --author="{찾아볼_이메일_주소@example.com}"
```

![image-20230808132429231](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/git/change-email.assets/1.webp)

지금 상황은 총 1건이 발견되었다. 해당 커밋의 해시값 `6b594224cf86845bf65ecd2ca898ea0e236f88b4` 을 복사해둔다.

## 해당 이메일로 커밋한 내용 모두 변경

> **참고:** `git filter-branch`는 오래된 명령어로, 이제는 `git filter-repo`라는 더 현대적인 도구가 권장된다. 
>
> git 공식 매뉴얼의 [git filter-branch](https://git-scm.com/docs/git-filter-branch) 부분에서도 해당 내용을 언급한다. 대용량 리포지터리의 경우 성능 문제가 있을 수 있으니 `git-filter-repo`를 사용하도록 권장한다.
>
> 다만, 대용량 리포지터리에서는 지금과 같은 이메일 일괄 변경을 권장하지 않으며 본 글은 `git filter-branch`를 기반으로 설명할것이다. `git filter-repo`에 대한 자세한 정보는 [이 곳](https://github.com/newren/git-filter-repo)에서 확인하길 바란다.

`filter-branch` 를 사용하는 방법은 아래와 같다. 각각 괄호 안의 내용을 알맞게 변경한 후 변경할 깃 저장소에서 명령어를 실행한다.

```bash
git filter-branch --env-filter '
OLD_EMAIL="{잘못 입력한 이메일}@example.com"
CORRECT_NAME="{새로운 이름}"
CORRECT_EMAIL="{새로 입력할 이메일}@example.com"
if [ "$GIT_COMMITTER_EMAIL" = "$OLD_EMAIL" ]
then
    export GIT_COMMITTER_NAME="$CORRECT_NAME"
    export GIT_COMMITTER_EMAIL="$CORRECT_EMAIL"
fi
if [ "$GIT_AUTHOR_EMAIL" = "$OLD_EMAIL" ]
then
    export GIT_AUTHOR_NAME="$CORRECT_NAME"
    export GIT_AUTHOR_EMAIL="$CORRECT_EMAIL"
fi
' --tag-name-filter cat -- --branches --tags
```

**실행결과**

![image-20230808133129731](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/git/change-email.assets/2.webp)

> 모든 커밋(2564개) 를 순회하며 필터를 적용하고 있다. 커밋 내역이 많을수록 더 오래 걸릴것이다.

완료가 되면, 이제 기록해두었던 해시값에 해당하는 커밋을 확인해서 커밋 제출자 이메일이 변경되었는지 확인해본다.

```bash
git log 6b594224cf86845bf65ecd2ca898ea0e236f88b4
```

![image-20230808133828569](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/git/change-email.assets/3.webp)

>  분명 변경을 했는데 확인결과 이메일 주소가 변경전 그대로다.

그런데 정말 바뀌지 않은걸까? 이번에는 커밋 메시지로 검색해본다.

```bash
git log --all --grep='나의 승인 이력 WIP'
```

![image-20230808134103918](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/git/change-email.assets/4.webp)

서로 다른 해시값을 가진 동일한 커밋이 2개가 보인다. 

**기존해시의 커밋**에서는 작성자 이메일이 그대로지만, 아래에 있는 **새로운 해시값의 커밋**에서는 변경된 이메일주소가 잘 적용되어있다.

그 이유는 `git filter` 를 사용하는 경우 **커밋의 해시값이 달라지기 때문**이다.

> Git의 커밋 해시는 커밋의 내용, 부모커밋, 작성자 정보, 커밋 메시지 등등 커밋의 모든 정보에 의해 생성되기 때문에 이메일주소만 변경해도 새로운 해시가 생긴다. 커밋 해시는 Git의 무결성을 보장하는 중요한 부분이기 때문에 해시를 유지한 채 커밋 이메일을 변경할 수는 없다.

새로 적용한 이메일이 마음에 든다면 이제는 기존 저장소 있는 커밋 히스토리를 강제로 덮어쓰면 된다.

> 주의: 혼자 작업하는 저장소가 아니라면 신중히 결정할 것. 
>
> 여러명이 함께 작업하는 프로젝트이며, 강제 푸시로 인한 사이드 이펙트 발생 범위에 대해 잘 모르겠다면 하지 않는것을 추천한다.

```bash
git push -f
```

이제 이렇게 하면 기존에 특정 이메일로 커밋한 모든 커밋들의 작성자가 새로 작성한 내용으로 변경된다.

## 결론

커밋 해시값이 변경되어도 괜찮은 상황이라면 위에서 안내한 `git filter-branch` 기능을 활용해서 전체적으로 한번에 이메일 주소를 변경하면 되겠다. 

다만, 혼자 사용하는 저장소가 아니며 이미 푸시가 된 상태라면 반드시 신중을 기해야한다.

이메일 주소를 반드시 지우고 새로 작성해야 하는 경우가 아니라면 [mailmap](https://git-scm.com/docs/gitmailmap) 기능을 활용해 `git log`, `git shortlog`, `git blame` 등의 명령어를 실행할 때 보여지는 이름과 이메일 주소를 변경할 수 있다.

> 해당 내용은 https://git-scm.com/docs/gitmailmap 참고할 것. 
>
> 예시
>
> ```bash
> {사용하고싶은이름} <{사용할이메일}@email.xx> {커밋이름} <{커밋이메일}@email.xx>
> ```

**References**

- https://git-scm.com/docs/git-filter-branch
- https://github.com/newren/git-filter-repo/
- https://www.git-tower.com/learn/git/faq/change-author-name-email/
- https://stackoverflow.com/questions/750172/how-do-i-change-the-author-and-committer-name-email-for-multiple-commits/9491696#9491696
- https://git-scm.com/docs/gitmailmap
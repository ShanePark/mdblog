# `git stash`로 변경 내용을 보관한 뒤에 브랜치를 삭제하면 어떻게 될까?

## Intro

개발하다 보면 작업 중인 변경 사항을 일시적으로 보관하고 브랜치를 이동하거나 삭제해야 할 때가 있다. 이때 보통 `git stash`를 이용해 변경 사항을 임시 저장하는데, 만약 스태시해 둔 상태에서 브랜치를 삭제하면 어떻게 될까? 혹시 스태시한 작업이 사라지지는 않을까? 

이번 포스트에서는 `git stash`와 브랜치 삭제의 관계를 알아본다.

## `git stash`

`git stash`는 현재 작업 중인 변경 사항(커밋되지 않은 파일 및 수정된 파일)을 임시 저장해 두는 기능이다. 이때 스태시는 <u>브랜치에 의존하지 않고</u>, 저장된 작업은 저장소 내에 독립적으로 유지된다. 

즉, 특정 브랜치에서 `git stash`를 사용해도 이 스태시 항목은 브랜치가 아닌 저장소 자체에 저장되기 때문에 다른 브랜치로 이동하거나 브랜치를 삭제하더라도 스태시 자체는 영향받지 않는다. 따라서 삭제된 브랜치에서 `stash`한 내용도 나중에 다른 브랜치에서 복원할 수 있다.

## 실습

간단한 실습으로 `git stash`가 브랜치 삭제에 안전한지 확인해보자.

1. **새 저장소 초기화 및 파일 생성**

   ```bash
   mkdir stash-test
   cd stash-test
   git init
   ```

   새로운 저장소를 만들고, `git init`으로 Git을 초기화한다.

2. **첫 번째 커밋 생성**

   ```bash
   echo "Initial content" > test.txt
   git add test.txt
   git commit -m "Initial commit"
   ```

   `test.txt` 파일을 생성하고 첫 번째 커밋을 만든다.

3. **새로운 브랜치 생성 및 변경 사항 추가**

   ```bash
   git checkout -b test-branch
   echo "New changes" > test.txt
   git add test.txt
   ```

   `test-branch`라는 새로운 브랜치를 만들고, `test.txt` 파일에 내용을 추가한다. 이 상태에서 커밋하지 않고 다음 단계로 넘어간다.

4. **`git stash`로 변경 내용 저장**

   ```bash
   git stash
   ```

   작업 중인 변경 사항을 스태시에 저장한다. 이 명령어를 사용하면 `test-branch`의 변경 내용을 임시로 저장하고, 워킹 디렉토리는 처음 커밋 상태로 되돌아간다.

5. **브랜치 삭제**

   ```bash
   git checkout main
   git branch -D test-branch
   ```

   `main` 브랜치로 이동한 뒤, `test-branch`를 삭제한다. 일반적으로 `git stash`는 저장소에 저장되기 때문에, 브랜치를 삭제해도 스태시는 영향을 받지 않는다.

6. **스태시 목록 확인 및 복원**

   ```bash
   git stash list      # 스태시가 여전히 남아있음
   git stash apply     # 복원
   ```

   `git stash list` 명령어로 저장된 스태시 항목이 그대로 남아 있는지 확인할 수 있다. 여기서 `git stash apply`를 사용해 스태시 내용을 다시 워킹 디렉토리로 복원할 수 있다.

위 과정에서 보듯이, 브랜치를 삭제해도 스태시 내용은 남아 있으며, 다른 브랜치에서도 그대로 복원할 수 있다.

## 결론

`git stash`로 보관한 변경 사항은 브랜치와 무관하게 저장소에 저장되기 때문에, 브랜치를 삭제하더라도 스태시 내용은 안전하게 유지된다. 브랜치를 이동하거나 삭제할 때 안심하고 `git stash`를 사용할 수 있다. 

끝.

**References**

- https://stackoverflow.com/questions/18866837/what-happens-if-i-stash-changes-in-a-branch-and-then-delete-that-branch
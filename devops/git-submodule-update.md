# Git Submodule 최신 커밋으로 업데이트

## Intro

Git submodule은 프로젝트 안에 다른 Git 저장소를 포함할 때 유용하게 쓰인다. 

하지만 서브모듈은 특정 커밋에 고정되기 때문에 메인 저장소를 업데이트해도 서브모듈이 자동으로 최신 상태로 업데이트 되지 않는다. 아래의 경우에 속한다면 불편함을 겪고 있었을 거라 생각한다.

- 프로젝트에 외부 라이브러리나 모듈이 포함된 경우
- 서브모듈이 자주 업데이트되는 상황에서 최신 상태를 유지하고 싶을 때
- 여러 서브모듈이 있어 일일이 들어가서 업데이트하기 번거로울 때

서브모듈을 최신 커밋으로 업데이트할 때, 하나씩 디렉터리에 들어가서 업데이트하지 않고, 명령어 하나로 깔끔하게 처리하는 방법을 알아보자.

## 서브모듈 업데이트

### 1. 서브모듈 초기화 및 업데이트

우선, 서브모듈이 아직 초기화되지 않았다면 초기화부터 해주자.

```bash
git submodule update --init --recursive
```

이 명령은 서브모듈 디렉터리를 생성하고, `.gitmodules` 파일에 정의된 URL에서 서브모듈 데이터를 가져온다. 서브모듈이 이미 초기화되었다면, 다음 단계로 넘어간다.

### 2. 최신 커밋으로 업데이트

모든 서브모듈을 최신 상태로 쉽게 업데이트하려면 `--remote` 옵션을 사용하면 된다.

```bash
git submodule update --remote --recursive
```

- remote: 서브모듈의 기본 브랜치(일반적으로 `main`이나 `master`)를 기준으로 최신 상태로 가져온다.
- recursive: 서브모듈 내부에 또 다른 서브모듈이 있을 경우에도 함께 업데이트된다.

이 명령 하나로 모든 서브모듈이 최신 상태로 정리된다. 서브모듈 디렉터리를 일일이 찾아가서 업데이트할 필요가 없다.

### 3. 변경사항 반영

서브모듈의 상태가 업데이트되면 메인 저장소에 그 상태를 반영해야 한다. 그렇지 않으면 다른 사람이 프로젝트를 클론할 때 서브모듈이 다시 이전 커밋 상태로 고정된다. 서브모듈을 쓰면서 처음에는 불편하다고 생각 했었는데, 사실 조금만 생각해보면 납득이 된다. 

서브모듈에 새로운 내용이 있다고 상위 모듈이 영향을 받는다면 그게 얼마나 더 불편하고 귀찮겠는가.

```bash
git add .
git commit -m "Update submodules to latest commits"
```

이 명령을 실행하면 메인 저장소가 서브모듈의 최신 커밋을 참조하게 된다.

## 결론

Git은 몇년을 사용하고 있는데도 아직도 많이 어렵다.

`git submodule update --remote --recursive` 명령은 서브모듈을 최신 상태로 관리하는 데 있어 간단하고 효과적인 방법이라 생각한다. 

이 명령어 하나로 모든 서브모듈을 최신 커밋으로 업데이트하고, 메인 저장소에서 이를 쉽게 반영할 수 있다.

다만, 서브모듈이 많이 있다면 최신 커밋으로 업데이트 하지 말아야 할 모듈이 있을 수 있으니 주의해서 사용하자.

> 특히 호환성 검증이 끝나지 않은 서드 파티 라이브러리..
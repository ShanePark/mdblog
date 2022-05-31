# Git) 서브모듈 추가 및 제거

## Intro

A 라는 프로젝트 아래에 B 라는 서브 모듈이 필요 하게 되었습니다.

이미 B 프로젝트는 C 프로젝트를 서브 모듈로 가지고 있는 상황이었는데, A 프로젝트에 B 프로젝트를 서브 모듈로 추가하고, 다시 삭제하는 과정을 진행 해 보도록 하겠습니다.

## 서브모듈 추가

`git submodule add 저장소주소 서브모듈경로` 로 추가 할 수 있습니다.

아래의 명령어는 메인 git 저장소의 /build 폴더에 url-to-pdf 라는 프로젝트를 url-to-pdf 라는 이름으로 서브모듈로 추가하는 예시 입니다.

```zsh
git submodule add git@github.com:Shane-Park/url-to-pdf.git /build/url-to-pdf
```

이후 확인해보면 `.gitmodules`에 `[submodule "build/url-to-pdf"]` 로 시작하는 서브모듈에 대한 경로 및 주소 데이터가 자동으로 작성 되어 있으며, 지정한 폴더에 해당 서브모듈이 클론 되어 있습니다.

서브 모듈의 상태를 확인 하려면 아래의 명령어를 입력 해 주면 됩니다.

```zsh
git submodule status
```

status 명령어 입력시, 서브모듈의 목록이 출력 됩니다.

지금의 구조는 A 프로젝트가 B 프로젝트를 서브 모듈로 가지고 있는 상태인데, B 프로젝트가 가지고 있는 C 프로젝트의 경로가 빈 폴더인 상태입니다. 서브모듈을 업데이트 시켜 줍니다.

```zsh
git submodule update --init --recursive
```

## 서브모듈 제거

이번에는 방금 추가했던 서브 모듈을 그대로 삭제 하도록 하겠습니다.

```zsh
git rm -f build/url-to-pdf
```

명령어를 실행 하면 .gitmodules 에서 작성되어 있던 `[submodule "build/url-to-pdf"]` 로 시작하는 서브모듈에 대한 내용과 해당 경로의 파일들도 함께 삭제가 됩니다.

이상입니다.
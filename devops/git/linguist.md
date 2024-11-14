# Github 저장소 언어 표기 설정

## Intro

Github 저장소의 언어 표기는 저장소의 주요 언어를 한눈에 보여주는 유용한 기능이다. 하지만 다양한 파일 포맷을 포함할 경우, 본래 의도한 언어와 다르게 표시될 수 있다. Github은 언어 통계를 위해 [Linguist](https://github.com/github-linguist/linguist)라는 오픈소스 도구를 사용한다. 

Linguist는 각 파일의 확장자와 내용에 따라 언어를 감지하고, 라인 수를 기준으로 언어 비율을 계산한다. 이때 코드베이스의 양적인 부분만을 반영하기 때문에 핵심 코드가 아닌 파일이 많다면 실제 프로젝트 언어 비율과 다르게 나타날 수 있다.

이번 글에서는 `.gitattributes` 파일을 통해 Github 언어 통계를 조정하고, 불필요한 언어가 표시되지 않도록 하는 방법을 설명하겠다.

## 특정 파일과 폴더 제외 설정

### .gitattributes

`.gitattributes` 파일을 저장소 루트에 생성하고 특정 파일이나 경로를 제외하는 설정을 추가할 수 있다. 아래 설정을 통해 특정 파일과 폴더를 제외할 수 있다.

1. 특정 폴더 제외

`node_modules`, `vendor` 폴더처럼 외부 라이브러리 코드가 많은 폴더를 제외하려면 다음과 같이 설정한다:

```text
vendor/* linguist-vendored
```
이 설정은 `vendor` 폴더를 통계에서 제외한다.

2. 특정 파일 확장자 제외  

예를 들어, HTML 파일을 통계에서 제외하려면 `.gitattributes` 파일에 다음과 같이 작성한다:

```text
*.html linguist-vendored
*.htm linguist-vendored
```
확장자별로 설정을 추가해야 하며, 다수의 확장자를 제외할 경우 각각 명시해줘야 한다.

3. 특정 파일 제외 

특정 파일을 제외하려면 아래와 같이 `.gitattributes` 파일에 설정을 추가한다:

```text
Gemfile linguist-vendored
Gemfile.lock linguist-vendored
```
주로 `Gemfile`과 같은 루트 파일이나 특정 환경 설정 파일 등을 제외하는 데 유용하다.

4. 특정 언어 강제 설정  

Linguist가 특정 파일의 언어를 잘못 인식할 경우, `.gitattributes` 파일에서 강제로 언어를 지정할 수도 있다. 예를 들어, Obj-C 파일을 정확히 표시하려면 아래와 같이 설정한다.

```text
*.h linguist-language=Objective-C
*.m linguist-language=Objective-C
```

### 고려 사항

1. 기본 브랜치에만 적용됨  
   Linguist 설정은 Github의 기본 브랜치에만 적용된다. 기본 브랜치가 `main`이 아닌 `develop` 브랜치라면 해당 브랜치에 설정해야 한다. 이로 인해 `.gitattributes` 설정이 기본 브랜치로 머지되기 전까지는 실제 언어 비율에 반영되지 않는다.
2. 언어 통계 반영 시간  
   `.gitattributes` 파일을 변경한 후 Github 언어 통계가 업데이트되기까지 오랜 지연이 발생할 수 있다. 반영되지 않은 것처럼 보일 때는 꽤 기다려야 할 수 있으며, 브라우저 캐시 문제로 인해 새로 고침이 필요할 수도 있다.

## 결론

이번 글에서는 Github 저장소의 언어 표기를 조정하여 불필요한 파일들이 언어 통계에 반영되지 않도록 설정하는 방법을 알아보았다. `.gitattributes` 파일을 통해 특정 파일 및 폴더를 제외하고, 특정 언어를 명시하여 Github 언어 표기를 보다 정확히 반영하도록 해보자.

**References**

- https://proandroiddev.com/removing-noise-from-your-github-language-stats-e96113f8183d
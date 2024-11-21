# macOS와 Linux에서 `sed -i` 옵션 동작 차이

## Intro

`sed`의 `-i` 옵션은 파일을 직접 수정(in-place)하는 기능을 제공한다. 하지만 GNU `sed`(주로 Linux에서 사용)와 BSD `sed`(macOS에서 사용) 간의 구현 방식 차이로 인해 같은 명령어라도 다른 결과를 초래할 수 있다. 그 차이는 다음과 같다:

- **GNU `sed`**: `-i` 뒤에 백업 확장자를 생략하면 원본 파일을 수정하며, 백업 파일을 생성하지 않는다.
- **BSD `sed`**: `-i` 뒤에 백업 확장자를 반드시 명시해야 한다. 백업을 원치 않으면 빈 문자열(`''`)을 사용해야 한다.

### 예제 비교

```bash
sed -i 's/hello/bye/g' example.txt
```

- **GNU `sed`**: 파일을 백업 없이 수정.
- **BSD `sed`**: `'s/hello/bye/g'`을 백업 확장자로 해석하려다 에러 발생(`invalid command code`).

---

## 명령어 차이

### Linux (GNU `sed`)

```bash
# 백업 파일을 생성하지 않음.
sed -i 's/hello/bye/g' example.txt

#example.txt.bak 백업 파일 생성 후 수정.
sed -i.bak 's/hello/bye/g' example.txt
```

### macOS (BSD `sed`)

```bash
# 백업 없이 원본 파일 수정.
sed -i '' 's/hello/bye/g' example.txt

#example.txt.bak 백업 파일 생성 후 수정.
sed -i .bak 's/hello/bye/g' example.txt
```

> **주의**: `-i` 뒤에 공백을 포함하지 않으면 macOS에서는 다음 내용을 백업 파일 확장자로 간주하여 엉뚱하게 작동할 수 있다.

---

## 해결 방안

macOS와 Linux에서 동일하게 작동하게 하고싶다. OS에 따라 서로 다른 스크립트가 존재한다면 작성할때도 실행할 때도 번거롭다.

### 1. GNU `sed` 사용

macOS에 `gnu-sed`를 설치하면 Linux와 동일한 방식으로 `sed`를 사용할 수 있다.

```bash
# Homebrew로 gnu-sed 설치
brew install gnu-sed

# gsed 명령어 사용
gsed -i '' -e 's/hello/bye/g' example.txt
```

> macOS 기본 `sed`를 대체하려면 `gsed`를 `PATH`에 추가하거나 alias로 설정할 수 있지만 `alias sed=gsed`
>
> macOS 기본 `sed`를 사용하는 기존 스크립트와 충돌할 위험이 있다.

### 2. 스크립트 분기

스크립트에서 `OSTYPE`을 감지하여 플랫폼에 따라 `sed` 옵션을 분기 처리할 수 있다.

```bash
#!/usr/bin/env bash
set -Eeuo pipefail

case "$OSTYPE" in
  darwin*|bsd*)
    echo "Using BSD sed style"
    sed_no_backup=( -i '' )
    ;;
  *)
    echo "Using GNU sed style"
    sed_no_backup=( -i )
    ;;
esac

sed "${sed_no_backup[@]}" -e 's/hello/bye/g' example.txt
```

그런데 MacOS 에서 -i '' 옵션을 주고 실행하면, `-E`로 끝나는 백업파일이 생성 되었다.

### 3. 백업 파일 생성 후 제거

그래서 결국엔 백업파일을 생성하게 그냥 두고 다음 코드에서 삭제하도록 하였다. 

MacOS 와 Linux 에서 -E 앞에 붙는 띄어쓰기 갯수가 각각 0개, 2개로 달랐다.

```bash
run_sed() {
  local pattern=$1
  sed '-i '' -E' "$pattern" "$markdown_file"

  # MacOS
  if [[ -e "${markdown_file}-E" ]]; then
    rm -f "${markdown_file}-E"
  fi

  # Linux
  if [[ -e "${markdown_file}  -E" ]]; then
    rm -f "${markdown_file}  -E"
  fi
}
```

**References**

- https://stackoverflow.com/questions/4247068/sed-command-with-i-option-failing-on-mac-but-works-on-linux

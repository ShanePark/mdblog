# Apple Silicon Mac 에서 Ruby 설치 문제 해결

## Intro

MacOS 에는 기본적으로 Ruby 가 설치되어 있다. 그런데 그걸 이용해서 바로 `gem install` 등의 명령어를 사용하려 하면

> You don't have write permissions for the /Library/Ruby/Gems/2.6.0 directory.

이라는 에러를 맞이하게 되는데, Ruby 환경을 별도로 분리해서 설치해줘야 한다.

그런데 Ruby 설치가 간단하게 되지는 않는다.

## 설치

일단 제일 먼저 rbenv 를 설치해야 한다.

```bash
# rbenv 설치
brew install rbenv ruby-build

# 셀 초기화 파일 수정
echo 'eval "$(rbenv init - zsh)"' >> ~/.zshrc
source ~/.zshrc
```

여기까지는  보통 아무 문제 없다.

그런데 여기에서 원하는 Ruby 버전을 설치 할 때 문제가 발생하는데

```bash
rbenv install 3.2.2
rbenv global 3.2.2
```

## 에러

에러 전문은 아래와 같다.

```
==> Installing ruby-3.2.2...
ruby-build: using readline from homebrew
ruby-build: using libyaml from homebrew
ruby-build: using gmp from homebrew
-> ./configure "--prefix=$HOME/.rbenv/versions/3.2.2" --with-openssl-dir=/opt/homebrew/opt/openssl@3 --enable-shared --with-readline-dir=/opt/homebrew/opt/readline --with-libyaml-dir=/opt/homebrew/opt/libyaml --with-gmp-dir=/opt/homebrew/opt/gmp --with-ext=openssl,psych,+

BUILD FAILED (macOS 15.5 on arm64 using ruby-build 20250610)

You can inspect the build directory at /var/folders/31/yp1smy8j3l3gqywfgs3pr1kr0000gn/T/ruby-build.20250614083423.69006.xwyxfr
See the full build log at /var/folders/31/yp1smy8j3l3gqywfgs3pr1kr0000gn/T/ruby-build.20250614083423.69006.log
rbenv: version 3.2.2' not installed
```

여기에서 보면 macOS 버전을 명시하기 때문에 OS 버전과 아키처의 문제로 보이며 OS 업데이트를 제때 안한 사용자에들이 찔려하며 OS 업데이트를 하는 일이 일어날 수 있는데. 일단 명시된 빌드 에러 로그를 확인해봐야 한다.

위에서는 `/var/folders/31/yp1smy8j3l3gqywfgs3pr1kr0000gn/T/ruby-build.20250614083423.69006.log` 로그를 확인하라고 써 있는데 사람마다 다를 수 있으니 각자 콘솔에 뜬 로그를 확인해본다.

```bash
view /var/folders/31/yp1smy8j3l3gqywfgs3pr1kr0000gn/T/ruby-build.20250614083423.69006.log
```

로그를 확인해보니 나의 경우는 아래와 같았다.

```
checking whether make sets $(MAKE)... yes
checking for a BSD-compatible install... /opt/homebrew/bin/ginstall -c
checking for a race-free mkdir -p... /opt/homebrew/bin/gmkdir -p
checking for dtrace... dtrace
checking for dot... no
checking for doxygen... no
checking for pkg-config... pkg-config
checking whether it is Android... no
checking for cd using physical directory... cd -P
checking whether CFLAGS is valid... yes
checking whether LDFLAGS is valid... no
configure: error: something wrong with LDFLAGS="-L/opt/homebrew/opt/node@14/lib"
external command failed with status 1
```

LDFLAGS 라는 환경변수가 node14 경로로 오염된걸로 보인다.

## 해결

문제가 된 환경변수들을 `unset LDFLAGS` 로 제거해서 재 시도 할 수 있지만 몇개의 환경변수가 오염된지 알 수 없으니 깨끗한 셀에서 시도해본다.

```bash
env -i HOME=$HOME PATH=/opt/homebrew/bin:/usr/bin:/bin:/usr/sbin:/sbin rbenv install 3.2.2
```

잘 설치가 되었다면 이어서 해당 버전을 기본(global)으로 사용하도록 설정한다.

```bash
rbenv global 3.2.2
```

터미널을 새로 켜서 버전을 확인해준다.

```bash
ruby --version
#ruby 3.2.2 (2023-03-30 revision e51014f9c0) [arm64-darwin24]
```

해결이 되는걸 볼 수 있다.

요즘엔 이러한 대부분의 문제는 검색하거나 LLM에 물어보면 손쉽게 해결되는데, 이상하게도 이 문제는 그렇지가 않았다. 

로그까지 파고 들어가서 에러 원인에 대한 분기를 시키지 않으면 문제 원인이 너무 다양한 모양이다.
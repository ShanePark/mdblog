# Linux) Homebrew와 Hugo 설치 및 실행

Homebrew를 사용하기 때문에 Linux 배포판은 어느 것이든 상관 없습니다.

> 저는 우분투 Ubuntu 20.04 를 사용합니다.

## Hugo와 Brew

### Hugo?

> The world’s fastest framework for building websites

![image-20211124105938278](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124105938278.png)

> https://jamstack.org/generators/

Hugo는 Go 언어로 작성된 정적 웹사이트 생성기 입니다. 

2013년 지금은 Google의 Go Language 리더인 Steve Francia에 의해 처음으로 개발되어, 노르웨이의 Bjørn Erik Pedersen를 비롯한 다양한 컨트리뷰터들 덕분에 성능과 기능들이 크게 향상되었으며, Apache License 2.0을 따르는 오픈 소스입니다.

속도가 굉장히 빠르고 유연하기 때문에 인기가 많습니다.

![image-20211124100033364](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124100033364.png)

> apt install은 버전이 제법 뒤쳐진다며 권장하지 않는 방법이라고 합니다.

![image-20211124100210366](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124100210366.png)

> 모든 배포판에서 Snap을 사용 할 수 있다고 합니다.

하지만 개인적으로 snap을 선호하지 않아서 다른 방법을 이용 해 보겠습니다.

![image-20211124100316125](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124100316125.png)

### Homebrew?

Homebrew 는 오픈소스 소프트웨어 패키지 매니지먼트 시스템으로, macOS와 Linux에서의 소프트웨어 설치 과정을 굉장히 간단하게 만들어 줍니다. 수제맥주라는 이름에 걸맞게, 유저들의 입맛에 맞게 소프트웨어를 빌드 할 수 있게 해줍니다.

사실 MacOS 를 사용하는 개발자들은 아마 대부분 이미 사용 하고 있을텐데, 리눅스에서도 사용 할 수 있습니다.

특히 가장 좋은건 패키지 관리를 Linux, MacOS에서 모두 동일한 경험으로 할 수 있다는게 정말 매력적입니다.

## Homebrew 설치

### Requirements

> https://docs.brew.sh/Homebrew-on-Linux

자세한 단계별 설치 방법이 위의 링크에 나와 있습니다. 요구 사항을 확인 해 보면 아래와 같습니다.

![image-20211124100951314](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124100951314.png)

그 외에도 상세한 설치 순서가 안내 되어 있는데요 사실 더 간단한 방법이 있습니다.

### Install Homebrew

사실 그렇게 복잡하게 다 확인 하며 설치 할 필요가 없습니다. MacOS 를 사용 해 본 분들은 알겠지만 이미 설치를 위한 자세한 script를 작성 해 두었기 때문에 실행만 하면 알아서 해 줍니다.

![image-20211124100718663](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124100718663.png)

시키는 대로 Terminal을 켜고 아래의 커맨드를 입력 합니다.

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

![image-20211124100818872](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124100818872.png)

> 비밀번호를 입력 합니다.

![image-20211124100841540](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124100841540.png)

> 어떤 파일이 설치 될 지 보여주며, 동의하면 엔터 키를 입력 하라고 합니다.

![image-20211124101210708](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124101210708.png)

> 설치가 금방 끝났습니다.

일단 next steps: 라고 하며 안내하는 사항들을 하나씩 해줘야 겠습니다.

- 일단 아래의 두 커맨드를 터미널에 입력 해서 Homebrew를 PATH에 등록 합니다.

```zsh
echo 'eval "$(/home/linuxbrew/.linuxbrew/bin/brew shellenv)"' >> ~/.zprofile
```

```zsh
eval "$(/home/linuxbrew/.linuxbrew/bin/brew shellenv)"
```

- sudo 권한이 있다면 Homebrew의 의존성을 추가 해 줍니다.

```zsh
sudo apt-get install build-essential
```

- gcc 설치도 권장한다고 합니다. 처음으로 brew 명령어를 사용하네요!

> GCC는 GNU 프로젝트의 오픈 소스 컴파일러 컬렉션 입니다. 유닉스 계열의 사실상 표준 컴파일러이며, GNU C Compiler의 약어 였지만, 다른 언어도 지원하게 되면서 GNU Compiler Collection 으로 이름을 변경 하였습니다.

```zsh
brew install gcc
```

![image-20211124101845586](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124101845586.png)

> MacOS 에서만 보던 맥주 아이콘을 Ubuntu Terminal에서도 만나니 반갑네요.

간단하게 Homebrew 설치가 끝났습니다.

### brew 명령어 등록(zsh)

> zsh 설치는 https://shanepark.tistory.com/248 포스팅을 참고해주세요.

지금은 brew 명령어가 잘 동작 하지만, zsh의 경우에는 터미널을 한번 껐다 켜면 동작하지 않을 확률이 높습니다. 

hugo 까지 설치를 다 했다고 해도 제대로 등록이 되지 않았기 때문에 hugo 명령어도 입력이 되지 않는데요.

![image-20211124111337297](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124111337297.png)

brew 를 실행하면 zsh: command not found: brew

hugo 를 실행하면 zsh: command not found: hugo 라고 나옵니다.

아까 메뉴얼에서 등록했던 `~/.zprofile`이 제대로 작동하지 않습니다. zsh 에서 프로필 관리를 `.zshrc` 에서 하기 때문인 것 같습니다.

`~/.zshrc`에 다시 PATH를 등록 하고, source 까지 해 줍니다.

```bash
echo 'eval "$(/home/linuxbrew/.linuxbrew/bin/brew shellenv)"' >> ~/.zshrc
```

```zsh
source ~/.zshrc
```

![image-20211124112010843](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124112010843.png)

> 이제 brew 명령어가 터미널을 껐다 켠다고 해도 잘 작동 합니다.

### 주의사항

![image-20211124132449151](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124132449151.png)

brew Casks 는 macOS 에서만 됩니다. 다운로드가 되니 설치가 되나 기대 할 수도 있지만, 아쉽게도 Formulae 로 등록된 소프트웨어만 brew를 이용해 설치가 가능합니다.

## Hugo

### 1. 설치

Brew가 준비 되었으니 바로 설치를 해 보겠습니다.

```zsh
brew install hugo
```

![image-20211124102041809](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124102041809.png)

설치가 끝났습니다. sudo 권한도 필요 없고, 과정이 복잡하지도 않습니다. Brew의 정말 큰 매력입니다.

### 2. 사이트 생성

잘 작동하는지 간단하게 테스트를 해 보겠습니다. 대충 원하는 위치에 `hugo new site {사이트이름}` 을 입력하면 바로 사이트가 생성 됩니다.

![image-20211124102414685](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124102414685.png)

사이트를 실행 하기 위해 3가지 스텝이 남았습니다.

### 1) 테마 다운로드

> https://themes.gohugo.io/ 에서 원하는 테마를 고릅니다.

![image-20211124102923143](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124102923143.png)

>  여러가지 테마가 나오는데, 테스트 용이니 그냥 아무거나 골라 봅니다.

선택한 테마를 git clone 해서 theme 폴더에 다운 받습니다.

```zsh
cd ./shane.blog/themes
git clone https://github.com/adityatelange/hugo-PaperMod.git

```

![image-20211124104711418](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124104711418.png)

> 금방 다운로드 됩니다.

이제는 테마 적용을 위해 config.toml 파일을 편집해야 합니다.

![image-20211124104809957](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124104809957.png)

> vim 으로 편집 합니다.

![image-20211124104828738](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124104828738.png)

> 방금 다운받은 theme의 폴더 명을 theme = 에 작성하고 저장 합니다.

### 2) 문서 생성

`hugo new <SECTIONNAME>/<FILENAME>.<FORMAT>` 형식으로 파일을 생성 합니다.

```zsh
hugo new home/main.md
```

![image-20211124103543832](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124103543832.png)

파일도 금방 생성되었습니다. 생성된 파일을 확인 해 보겠습니다.

```zsh
vi ./content/home/main.md
```

![image-20211124103641019](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124103641019.png)

> 뭔가 작성 되어 있네요. 메타 데이터를 저런식으로 관리 하는 듯 합니다.

![image-20211124103833503](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124103833503.png)

> 간단하게 몇 마디 작성 해 봅니다.

### 3) 서버 실행

모든 준비를 마쳤습니다. 서버를 실행 해 보겠습니다.

아래의 명령어만 입력 하면 서버가 작동 됩니다.

```zsh
hugo server
```

![image-20211124105008195](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124105008195.png)

서버가 실행 되었습니다. http://localhost:1313/ 로 접속 할 수 있습니다.

![image-20211124105030535](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124105030535.png)

서버는 실행 되었는데 아무것도 보이지 않네요. 

아까 글을 작성 할 때 draft를 true로 해 두어서 때문에 발행이 되지 않았기 때문입니다.

서버는 그대로 켜 둔 상태로 문서만 편집 합니다.

![image-20211124105105500](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124105105500.png)

> content 폴더에서 아까 작성한 파일을 찾아 가서

![image-20211124105119375](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124105119375.png)

> draft를 false 로 바꾸고 저장 해 줍니다.

그럼 서버를 다시 켜지 않아도, 웹사이트를 새로 고침 하지 않아도 바로 페이지가 갱신 됩니다.

![image-20211124105235889](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124105235889.png)

> md파일을 수정해서 저장 하는 순간 바로 생겼습니다.

이제 아까 작성한 Main을 클릭해서 확인 해 봅니다.

![image-20211124105300434](/home/shane/Documents/git/mdblog/OS/linux/ubuntu/brew-hugo.assets/image-20211124105300434.png)

> 상세 보기 화면 입니다.

간단하게 Homebrew 및 Hugo를 설치 하고 정적 페이지를 작성 해 보았습니다.

이상입니다.
# 우분투에 Openclaw 설치하기 및 후기

## Intro

2026년 몰트북 사태와 함께 Clawbot / Moltbot / OpenClaw (이름을 자주도 바꿨다) 가 바이럴되며 맥미니 품절사태까지도 있었다. 개인적인 생각으로는 아무리 통합 메모리라 한들 Mac Mini로 대형 로컬모델을 돌리는건 무리가 있으니 어차피 API 연결해서 상용모델 사용할거라면 굳이 비싼 돈 들여 맥미니 장만할 필요 없이 적당한 클라우드 인스턴스에 우분투 설치해서 하면 되지 않나 생각했다.

> 홈 서버로서의 맥미니의 역할은 개인적으로 매우 높게 평가한다.
>
> 지금 홈 서버로 사용하고 있는 10년차 노트북이 있는데, SSD도 새로 달아주고 램도 추가해주면서 서버로서의 임무를 오래 부여해 오고 있다. 이 컴퓨터의 수명이 다한다면 다음 홈서버로는 맥 미니를 생각하고 있으며 전력 소모나 발열관리등을 생각했을때는 지금이라도 바꾸고싶은 마음이 있다. 맥미니가 공짜로 생긴다면 당장이라도 바꿀것이다.

마침 NHN Cloud 에 사용하지 않고 있는 크레딧이 꽤 있다보니 클라우드에 한번 설치해보고 GPT의 Agent mode 와는 어떤 차별점이 있으며 어느정도의 활용 가능성이 있을지를 테스트해보기로 했다. 

## 설치

시스템 기본 도구 설치

```bash
sudo apt update && sudo apt upgrade -y
sudo apt install git curl build-essential -y
```

Node 설치 (v22.16.0 이상 필요, Node 24 권장)

```bash
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash
source ~/.bashrc
nvm install 24 # Node.js 24 버전 설치
node -v # 설치된 버전 확인 (v24.x.x가 나와야 함)
```

OpenClaw 설치

```bash
npm install -g openclaw@latest
# or: pnpm add -g openclaw@latest

#설치 잘 된 것 확인
openclaw --version # OpenClaw 2026.3.23-2 (7ffe7e4)
```

데몬 설치

```bash
openclaw onboard --install-daemon
```

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/openclaw.assets/1.webp)

> 동의

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/openclaw.assets/2.webp)

> Setup mode 선택

![3](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/openclaw.assets/3.webp)

> Model 제공자 선택

ChatGPT OAuth 를 사용할건데, 지금 시점에서는 최고의 선택일거라고 생각된다.

![4](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/openclaw.assets/4.webp)

> 브라우저에서 표시된 url만 붙여넣으면 되어 편하다.

이 때 localhost:1455 로 리다이렉트가 되는데 당황하지 말고 url의 callback 쿼리파라미터에 적힌 텍스트 혹은 전체 주소를 복사해서 ssh 접속한 터미널의 `Paste the authorization code (or full redirect URL):` 에 그대로 붙여넣기만 해주면 된다.

이후 GPT 모델까지 선택 하고 나면 채널선택을 한다.

![5](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/openclaw.assets/5.webp)

> 텔레그램이 가장 많이 추천된다.

![6](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/openclaw.assets/6.webp)

> 첫번째꺼가 쉽다.

그러면 토큰을 입력하라고 하는데, 텔레그램앱에서 BotFather 를 검색해 대화를 시작하고 `/newbot` 명령어를 입력하면 된다.

![7](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/openclaw.assets/7.webp)

![8](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/openclaw.assets/8.webp)

> 봇 이름을 지정하는데 자꾸 다 안 된단다.

대화방에 텔레그램 봇 토큰을 주면 그걸 복사해서 붙여넣기 해주면 된다.

![9](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/openclaw.assets/9.webp)

그다음은 Search Provider 를 고르는데 다른 브라우저들은 API key를 요구하는게 많으니 DuckDuckGo를 이용한다.

![10](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/openclaw.assets/10.webp)

그다음은 skills 설정을 하라고 하는데 추천이라고 하니 해준다.

![11](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/openclaw.assets/11.webp)

> 적당히 필요할법한 스킬들을 추가한다.

![12](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/openclaw.assets/12.webp)

> Skill 설치를 위한 node manager는 pnpm으로 설치했다.
>
> 대신 pnpm이 없으면 설치 해줘야한다. `npm install -g pnpm`

![13](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/openclaw.assets/13.webp)

> 필요한 hook 을 고른다 ` command-logger` 와 `session-memory`는 하는편이 좋아보인다.

![20](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/openclaw.assets/20.webp)

> 이제 bot을 실행한다. TUI 에서 그냥 바로 실행하면 된다.

![15](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/openclaw.assets/15.webp)

> 봇이 실행되었다.

이제 텔레그램에서 방금 등록한 bot을 찾아 `/start` 해준다.

![16](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/openclaw.assets/16.webp)

그런데 텔레그램 토큰입력과는 별개로 추가로 페어링이 필요하다.

![17](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/openclaw.assets/17.webp)

이제 이 페어링 코드를 이용해 아래와 같이 등록할 수도 있지만.

```bash
openclaw pairing approve telegram <페어링코드>
```

우리에겐 똑똑한 bot이 생겼다. 그냥 터미널에 떠있는 대화창에 해당 페어링 코드로 등록해달라고 한마디 해주면 알아서 해준다.

Approved 된 후에는 이제 대화가 가능해진다.

![18](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/openclaw.assets/18.webp)

> 연결 완료

이제 편하게 telegram을 이용해서 내 개인 비서에게 일을 시키면 된다.

![19](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/openclaw.assets/19.webp)

> 요청시 즉각 typing 중이라는 표시가 되며 금방 응답이 온다.

## 사용 후기

기대보다 활용도가 높아서 정말 인상적이었다. 카카오덕분에 저렴하게 구매한 ChatGPT Pro 플랜으로 토큰도 맘껏 사용할 수 있다.

웹에서 사용하는 LLM은 대단하지만 한계가 있다. ChatGPT나 Gemini, Claude 같은 채팅형 LLM 서비스 그 자체는 단지 사용자의 질문에 대답을 해 주는 것 이상으로 할 수 있는게 없다. 거기에서 이제 에이전트로 발전한게 Claude Code, Codex 같은 건데 코딩이라는 한정된 사용 목적 안에서만 활동한다.

물론 Claude Code 나 Codex 에도 권한을 충분히 부여하면 그 이상의 에이전트로서의 역할을 충분히 해낼 수 있지만 ClawBot은 거기에서 한단계 더 한계를 뛰어넘었다. 책상에 앉아 공부만 잘하던 범생이 친구가 그 비상한 머리를 가지고 벌떡 일어나 세상 밖으로 나온 느낌이다. 

물론 보안 문제로 당장 실현되긴 어렵겠지만 서버개발자의 새벽, 주말 장애 대응을 대신해주는것도 충분히 가능할거라 생각된다.

- 사람들이 말하는거 보니 호들갑이던데?
- 보안때문에 어차피 아무데도 못 쓰게 될 것.

위와 같은 평가를 내리고 설치할 생각을 안할 수 있다고 생각한다. 충분히 그럴 수 있다. 

하지만 클라우드에 작은 인스턴스 하나 띄우고, codex oauth 로 연결 하면 추가 비용 거의 없이 사용을 해 볼 수 있다. 설치 방법도 어렵지 않다 꼭 해보기를 추천한다. 내가 가진 경험과 직관만으로 아직 경험해보지 못한 세상을 상상하는건 분명한 한계가 있다.

개인적으로도 이것저것 재밌는 활용 방안이 많아서 즐겁다. 관심 미국주식들의 간밤 동향 및 상승/하락 이유에 대해 요약을 해주는 등 자동화가 어렵다고 여겨왔던 필드에서 자동화가 가능해지는게 굉장히 많다. 복잡한 설정도 필요없이 필요한것만 요청하면 뒤에서 일어나는 일들은 알아서 처리해준다. 사람이 하던 일을 상당부분 대신해주는데 여기에 실시간과 자율성이 부여된다.

ChatGPT의 Agent 기능을 개인적으로 좋아했었는데 OpenClaw를 써보니 앞으로 한동안은 해당 기능을 전혀 사용할 일이 없을 것 같다. Apple의 Siri가 스스로 되고자 했던게 이런게 아니었을까.

**References**

- https://github.com/openclaw/openclaw
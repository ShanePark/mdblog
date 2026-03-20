# Codex 서브에이전트 최대 스레드 수 늘리기

## Intro

`codex`에 서브에이전트가 생기면서 병렬 작업이 가능해졌다. 기본값은 최대 6개 스레드인데, 큰 작업을 여러 에이전트로 쪼개다 보면 금방 한계에 닿는다. 클로드코드에서는 별도의 제한이 없었는데, 기본적으로 병렬 상한이 10개로 순차 배치 처리한다고 알려져있지만 내 경험으로는 20개이상을 동시에 요청해도 동시에 돌아가는걸 확인했었다.

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/codex-subagents-max.assets/2.webp)

> codex 에서 서브에이전트 20개를 요청하자 6개까지 생성하고 14개는 생성에 실패한 상태

최근 출시한 M5 맥북 프로의 경우에는 CPU가 무려 15코어부터 시작하는데 서브에이전트 6개 제한은 많이 아쉽다.

이 경우 수치를 늘리고싶다면 `config.toml`에서 직접 조정할 수 있다.

## 설정 방법

`~/.codex/config.toml`에 아래 항목을 추가하면 된다.

```toml
[agents]
max_threads = 25   # 동시에 열 수 있는 에이전트 스레드 상한 (기본값: 6)
max_depth = 1      # 에이전트 중첩 깊이 (기본값: 1)
# job_max_runtime_seconds 는 서브에이전트의 타임아웃이고 기본값은 1800초
```

파일이 없다면 새로 만들면 되는데 내 경우는 이미 파일이 있었다.

```bash
mkdir -p ~/.codex
vi ~/.codex/config.toml
```

프로젝트마다 다른 설정을 적용하고 싶다면 레포 루트의 `.codex/config.toml`에 동일하게 작성하면 된다. 프로젝트 설정이 글로벌 설정보다 우선한다.

Codex cli 와 Codex App 모두 동일한 `~/.codex/config.toml`을 공유한다. macOS 앱 공식 문서에도 "앱의 에이전트는 CLI 및 IDE 익스텐션과 동일한 설정을 상속한다"고 명시되어 있다. 즉, `config.toml`을 한 번만 수정하면 CLI와 앱 모두에 적용된다.

`max_depth`는 에이전트가 또 다른 에이전트를 낳을 수 있는 중첩 깊이를 제한한다. 기본값 1은 메인 에이전트가 자식을 스폰하되, 그 자식은 다시 에이전트를 낳지 못하도록 막는 설정이다. 이 값을 높이면 재귀적인 위임이 가능해지지만, 토큰 소비와 예측 불가능한 fan-out이 급격히 늘어날 수 있어 특별한 이유가 없다면 기본값을 유지하는 편이 낫다

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/LLM/codex-subagents-max.assets/1.webp)

> 설정을 변경한 후 20개의 서브에이전트가 동시에 실행되는게 확인된다.

## 마치며

설정 자체는 단순하다. 다만 스레드를 늘릴수록 토큰 소비도 함께 늘어나니, 서브에이전트를 적극적으로 사용하는 상황에서는 `/fast` 모드를 잠깐 꺼두는걸 추천한다.

Codex Pro 플랜에서는 10시간씩 중단 없이 밤새 작업을 시켜도 주간 리밋을 아직 다 써본적이 없었는데, 서브에이전트 지원을 시작했으니 주간리밋도 알뜰하게 다 채워 쓸 수 있겠다.

**References**

- https://developers.openai.com/codex/subagents
- https://developers.openai.com/codex/config-sample
- https://developers.openai.com/codex/app/settings
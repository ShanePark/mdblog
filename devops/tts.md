# 무료 TTS 서비스 추천 클로바 vs Azure

## Intro

아이를 위한 어린이용 단어장을 만들면서 한국어와 영어 음성을 넣어야 할 일이 생겼다. 무료이거나 비용이 거의 들지 않으면서, 라이선스 문제 없이 상업적으로도 사용 가능한 TTS 서비스가 필요했다. 여러 서비스를 직접 비교해본 끝에 결론부터 말하면 Microsoft Azure Text-to-Speech를 선택했다.

## 비교해본 서비스들

TTS 서비스를 고를 때 가장 중요하게 본 기준은 세 가지였다. 음성 품질, 무료 사용량, 그리고 라이선스.

### 네이버 클로바더빙

한국어 음성 품질만 놓고 보면 클로바더빙이 꽤 괜찮았다. 자연스러운 한국어 발음과 다양한 목소리를 제공하고 있어서 첫인상은 좋았다. 다만 무료 사용 시 반드시 출처를 표기해야 하고, 상업 콘텐츠 제작에는 사용할 수 없다는 제약이 있다. 당장 수익화 계획이 없다고 해도 라이선스 제약이 있는 서비스를 선택하는 부담스러운 일이다.

> ※ 무료 서비스는 콘텐츠로 인한 수익이 발생하지 않는 채널 게시 용도로만 사용할 수 있습니다.
>
> [무료 사용 허용 범위 안내](https://help.naver.com/service/23823/contents/12463?lang=ko&osType=COMMONOS)

### Google Cloud TTS

Google Cloud TTS는 무료 제공량이 넉넉한 편이다. 월 100만 자(Neural/WaveNet)까지 무료로 사용할 수 있다. 반면 셋업 과정이 불필요하게 복잡했다. 프로젝트 생성, 서비스 계정 설정, 인증 키 파일 관리 등 실제로 API를 호출하기까지 거쳐야 할 단계가 많다. 하다보니 너무 불편했다.

### Azure Text-to-Speech

Azure는 셋업도 간결하고 무료 티어 설정도 명확했다. 리소스를 생성할 때 가격 계층에서 `Free F0`를 선택하면 그걸로 끝이다. 과금에 대한 걱정을 할 필요가 전혀 없다. 월 50만 자까지 무료로 제공되는데, 단어장 수천 개를 처리하기에 충분한 양이다. 음성 품질도 좋고, 특히 어린이 목소리 옵션이 만족스러웠다.

## Azure TTS 시작하기

### 리소스 생성

[Azure Portal](https://portal.azure.com/)에 접속해서 상단 검색창에 `Speech`를 입력한 뒤 Speech Services를 선택한다. 리소스를 만들 때 중요한 것은 가격 계층에서 반드시 **Free F0**를 선택하는 것이다. 이렇게 하면 유료 전환 없이 무료 범위 안에서만 사용하게 된다.

리소스가 생성되면 왼쪽 메뉴에서 `Keys and Endpoint`로 들어가면 API 키 두 개와 엔드포인트가 이미 만들어져 있다. 별도로 키를 생성하거나 서비스 계정을 설정할 필요 없이 바로 사용할 수 있다.

### Speech Studio에서 테스트

코딩 없이 음성을 확인해보고 싶다면 [Speech Studio](https://speech.microsoft.com/)에 접속하면 된다. Audio Content Creation 메뉴에서 언어와 보이스를 선택하고 텍스트를 입력하면 바로 들어볼 수 있다. MP3 파일로 내보내기도 가능하다.

### 추천 보이스

어린이용 콘텐츠에 적합한 보이스를 찾느라 여러 가지를 들어봤는데, 최종적으로 선택한 것은 다음 두 가지다.

- 한국어: `ko-KR-SeoHyeonNeural` — 자연스러운 어린이 목소리
- 영어: `en-GB-MaisieNeural` — 영국 영어 어린이 목소리

둘 다 발음이 또렷하고 톤이 부드러워서 단어장 용도로 잘 맞았다.

### API 연동

수천 개의 단어를 하나씩 Speech Studio에서 만들 수는 없으니 API를 사용했다. Python SDK를 설치하고 키와 리전만 넣으면 바로 동작한다.

```bash
pip install azure-cognitiveservices-speech
import azure.cognitiveservices.speech as speechsdk

speech_config = speechsdk.SpeechConfig(
    subscription="YOUR_KEY",
    region="koreacentral"
)
speech_config.speech_synthesis_voice_name = "ko-KR-SeoHyeonNeural"

audio_config = speechsdk.audio.AudioOutputConfig(filename="apple.mp3")
synthesizer = speechsdk.SpeechSynthesizer(
    speech_config=speech_config,
    audio_config=audio_config
)
synthesizer.speak_text_async("사과").get()
```

실제로 수백 개의 단어를 대상으로 음성 파일을 생성해봤는데, API 요청을 빠른 간격으로 보내도 별다른 문제 없이 모두 정상적으로 생성되었다. 무료 티어 사용량 안에서도 넉넉하게 처리할 수 있었다.

## 라이선스

Azure TTS의 라이선스가 깔끔한 편이다. [Microsoft Enterprise AI Services Code of Conduct](https://learn.microsoft.com/en-us/legal/ai-code-of-conduct)를 보면, 이 규정은 무료든 유료든 Microsoft AI 서비스의 모든 고객에게 동일하게 적용된다. 문서에 상업적 이용을 금지하는 조항은 없으며, 오히려 결과물에 대한 권리와 책임이 고객에게 있음을 명시하고 있다.

> 아래의 질문 답변도 무료티어의 상업적 활용에 대해 명확하게 설명해준다.
>
> https://learn.microsoft.com/en-us/answers/questions/1070009/usage-policy-limitation-for-free-tier

## 비교 정리

| 항목        | Azure TTS  | Google Cloud TTS | 클로바더빙    |
| ----------- | ---------- | ---------------- | ------------- |
| 무료 사용량 | 월 50만 자 | 월 100만 자      | 제한적        |
| 셋업 난이도 | 간단       | 복잡             | 간단          |
| 음성 품질   | 좋음       | 좋음             | 좋음 (한국어) |
| 상업적 이용 | 가능       | 가능             | 불가 (무료)   |
| 출처 표기   | 불필요     | 불필요           | 필수 (무료)   |

## 마치며

TTS 서비스를 고를 때 음성 품질만 보면 상당히 상향평준화가 되어 있다. 결국 차이를 만드는 것은 셋업의 간편함, 과금 구조의 명확함, 그리고 라이선스의 깔끔함이다. Azure TTS는 이 세 가지를 모두 만족시켜주기에 추천한다.

**References**

- https://learn.microsoft.com/en-us/answers/questions/1192398/can-i-use-azure-text-to-speech-for-commercial-usag
- https://learn.microsoft.com/en-us/answers/questions/1070009/usage-policy-limitation-for-free-tier
- https://help.naver.com/service/23823/contents/12463?lang=ko&osType=COMMONOS
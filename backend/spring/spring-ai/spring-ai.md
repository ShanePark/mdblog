# [Spring Boot] Spring AI 활용해 LLM과 연동하기

## Intro

Deepseek를 테스트 해보고 리소스 대비 대단한 성능에 감탄을 했고, 그 이후로 항상 개인적으로 진행 중인 프로젝트에서도 하나씩 LLM을 연동한 기능을 추가하려고 생각해왔다. LLM을 적용해 가치를 만들어낼 수 있는 분야는 너무나도 다양하다.

튜토리얼 수준으로 진행할 예정이기에 원래는 로컬에서 DeepSeek R-1의 8B 정도의 모델을 돌려서 처리하려 했지만, 아쉽게도 몇 번의 테스트 결과 해결되지 않는 한글 처리 이슈가 있었다. 응답이 오래 걸리는건 스케줄러로 처리하도록 하면 어느정도는 해결 가능하지만, 정확도는 타협할 수 없는 부분이다. 32B 이상 모델은 사용해야 한글도 원활하게 소화해내는걸로 보이는데 집에서 열심히 돌아가고 있는 서버 노트북 스펙은 그 정도를 감당할 수준이 아니다.

그래서 무료 API를 찾아 이것저것 비교해보고 여러 가지 테스트한 끝에 이번 튜토리얼에는 `Gemini 2.0 Flash`를 선택했다.

![4](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/spring-ai/spring-ai.assets/4.webp)

> https://ai.google.dev/gemini-api/docs

Gemini 2.0 Flash는 Free Tier에서 RPM(분당 최대 요청)이 15이고 Flash-Lite는 30으로 제법 넉넉하며, RPD(일 최대 요청)도 1500건으로 충분하다. 하지만 본 글에서는 어떤 LLM을 선택하는지 자체는 크게 중요하지 않으므로 각자 상황에 맞는 모델을 사용하면 된다. 나중에 교체도 쉽다.

지금부터 최대한 심플하게 코드를 작성하면서 프로젝트 생성부터 테스트까지 진행해볼 예정이다. 혹시 gemini 를 선택한다면 아래의 링크에서 미리 apiKey 를 생성해두자.

> https://aistudio.google.com/app/apikey

## 프로젝트 준비

### 프로젝트 개요

이번 프로젝트에서는 LLM을 활용해 사용자가 입력한 본인의 스케쥴에서 시간 데이터를 따로 추출하는 기능을 구현할 것이다.
예를 들어 `"친구들과 밤 11시에 만나기"`라는 문장이 들어오면, "밤 11시"라는 시간을 추출하여 `"2025-02-28T23:00:00"`과 같은 형식으로 반환하고 시간에 관련된 데이터가 제외된 텍스트인 `친구들과 만나기`를 따로 분리해내는 것이다.

> 개인 스케줄을 입력하는 사이드 프로젝트를 만들었는데, 약속 시간을 입력하는 UX는 언제나 번거롭기 때문에 사용자들은 그냥 내용을 입력할 때 시간도 텍스트로 기입하는 쪽을 선호한다. 정형화되지 않은 시간데이터는 활용도가 떨어지는데 LLM이 해결하기 좋은 문제라고 생각했다.
>
> 이 기능을 응용하면 일정 관리나 자연어 입력을 통한 일정 예약 등의 기능을 확장할 수도 있다.

### 프로젝트 생성

Spring Boot Initializr를 이용해 프로젝트를 생성했다. **JDK는 21**을 선택했다.

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/spring-ai/spring-ai.assets/2.webp)

Dependencies는 **Spring Web, Lombok, OpenAI**를 선택한다.

![3](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/spring/spring-ai/spring-ai.assets/3.webp)

### OpenAI 라이브러리 선택 이유

Spring AI는 여러 LLM(대형 언어 모델)과의 연동을 지원하지만, 여러가지 다른 모델들의 API와의 **호환성**을 고려해 OpenAI를 선택했다.

대부분의 후발 주자들이 OpenAI 호환 API를 제공하려고 하는 편이다. 따라서 OpenAI API와 호환되게 개발해두면 나중에 다른 벤더로 변경하더라도 코드 수정이 거의 필요 없다.

Google Gemini API도 쓰려면 Spring AI에서 기본 지원하지 않기에, [VertexAI Gemini Chat](https://docs.spring.io/spring-ai/reference/api/chat/vertexai-gemini-chat.html)을 추가하여 구성해야 하는데

무료 API(Google AI Studio)와 Vertex AI(Enterprise 플랫폼)는 별개의 서비스이기 때문에 무료 사용에 제약이 예상되고, 무엇보다 OpenAI 라이브러리를 활용하는 것이 가장 유연한 선택이기에 골랐다.

### 프로젝트 생성 후 dependencies 확인

프로젝트가 잘 생성된 것을 확인한다.

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.ai:spring-ai-openai-spring-boot-starter'
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}

ext {
    set('springAiVersion', "1.0.0-M6")
}

dependencyManagement {
    imports {
        mavenBom "org.springframework.ai:spring-ai-bom:${springAiVersion}"
    }
}
```

## 코드 작성

### 설정 파일

Spring AI에서 사용할 OpenAI 설정을 `application.yml`에 추가한다.

```yaml
spring.application.name: springAi

spring:
  ai:
    openai:
      api-key: "API_KEY_HERE"
      chat:
        base-url: "https://generativelanguage.googleapis.com/v1beta/openai/"
        options:
          model: "gemini-2.0-flash-lite"
          temperature: 0.0
        completions-path: "/chat/completions"
```

여기서 중요한 점은 `completions-path`를 명시해야 한다는 것이다.
이걸 생략하고 개발해봤더니 기본적으로 `/v1/chat/completions` 경로로 요청했고, Gemini의 OpenAI compatible API에서는 해당 경로를 지원하지 않아 **404 오류**가 발생했다. 이런 문제는 디버깅 모드에서 브레이크 포인트를 찍고 요청을 눈으로 확인해야 보여서 찾기가 번거롭다.

> 그래도 Gemini가 아닌 다른 LLM을 사용하더라도, OpenAI 호환 API를 따르도록 설정하면 최소한의 코드 수정으로 연동이 가능하다.

### 요청 및 응답 도메인

```java
public record TimeExtractionRequest(
        LocalDate date,
        String content
) {}
```

```java
public record TimeExtractionResponse(
        boolean result,
        boolean hasTime,
        String datetime,
        String content
) {}
```

### 테스트

도메인 모델을 먼저 만들고, 서비스 로직을 작성하기 전에 테스트 코드를 작성하면 개발이 수월하다.
특히 API 요청 및 응답을 주고받는 상황처럼 주고 받는 데이터가 아직 명확하지 않을 때는 테스트를 통해 확인 해 가면서 진행하는 것이 효과적이다.

```java
package shanepark.springai.service;

import org.assertj.core.api.Assertions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import shanepark.springai.domain.TimeExtractionRequest;
import shanepark.springai.domain.TimeExtractionResponse;

import java.time.LocalDate;

class TimeExtractionServiceUnitTest {

    String API_KEY = "API_KEY_HERE";

    //    @Test
    void extractTime() {
        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(API_KEY)
                .baseUrl("https://generativelanguage.googleapis.com/v1beta/openai/")
                .completionsPath("/chat/completions")
                .build();

        OpenAiChatOptions chatOption = OpenAiChatOptions.builder()
                .model("gemini-2.0-flash-lite")
                .temperature(0.0)
                .build();

        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(chatOption)
                .build();

        TimeExtractionService service = new TimeExtractionService(chatModel);
        TimeExtractionRequest request = new TimeExtractionRequest(LocalDate.of(2025, 2, 28), "친구들과 밤 11시에 만나기");
        TimeExtractionResponse response = service.extractTime(request);

        Assertions.assertThat(response.result()).isTrue();
        Assertions.assertThat(response.hasTime()).isTrue();
        Assertions.assertThat(response.datetime()).isEqualTo("2025-02-28T23:00:00");
        Assertions.assertThat(response.content()).doesNotContain("11시");
    }

}
```

> `temperature`는 답변의 일관성이 중요한 경우 **0에 가깝게** 설정하는 것이 좋다.
> 창의성과 다양성이 필요한 경우에는 **1 이상으로 올려야 한다.** (범위: 0~2)

잘 작동한다면 이제 통합 테스트를 통해 Spring 설정이 정상적으로 반영되는지도 확인한다.

```java
@SpringBootTest
@ActiveProfiles("test")
class TimeExtractionServiceIntegrationTest {

    @Autowired
    private TimeExtractionService timeExtractionService;

    @Test
    void extractTime() {
        TimeExtractionRequest request = new TimeExtractionRequest(LocalDate.of(2025, 2, 28), "친구들과 밤 11시에 만나기");
        TimeExtractionResponse response = timeExtractionService.extractTime(request);

        Assertions.assertThat(response.result()).isTrue();
        Assertions.assertThat(response.hasTime()).isTrue();
        Assertions.assertThat(response.datetime()).isEqualTo("2025-02-28T23:00:00");
        Assertions.assertThat(response.content()).doesNotContain("11시");
    }

}
```

### 컨트롤러 및 서비스 레이어

```java
package shanepark.springai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shanepark.springai.domain.TimeExtractionRequest;
import shanepark.springai.domain.TimeExtractionResponse;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class TimeExtractionController {
    private final TimeExtractionService timeExtractionService;

    @GetMapping("/")
    public TimeExtractionResponse extractTime(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int day,
            @RequestParam String content
    ) {
        LocalDate date = LocalDate.of(year, month, day);
        TimeExtractionRequest request = new TimeExtractionRequest(date, content);
        return timeExtractionService.extractTime(request);
    }

}

```

```java
package shanepark.springai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.stereotype.Service;
import shanepark.springai.domain.TimeExtractionRequest;
import shanepark.springai.domain.TimeExtractionResponse;

@Service
public class TimeExtractionService {

    private final ChatClient chatClient;

    public TimeExtractionService(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    public TimeExtractionResponse extractTime(TimeExtractionRequest request) {
        String prompt = generatePrompt(request);
        ChatResponse chatResponse = chatClient.prompt(prompt)
                .call()
                .chatResponse();

        if (chatResponse == null) {
            throw new RuntimeException("Chat response is null");
        }
        Generation result = chatResponse.getResult();

        AssistantMessage output = result.getOutput();
        String text = output.getText();

        return parseResult(text);
    }

    private String generatePrompt(TimeExtractionRequest request) {
        return String.format("""
                 Task: Extract time from the text and return a JSON response.

                 - Identify time and convert it to ISO 8601 (YYYY-MM-DDTHH:MM:SS).
                 - Remove the identified time from the text. The remaining text becomes `content`.
                 - If no time is found, return:
                   { "result": true, "hasTime": false}
                 - If multiple time exists, return:
                   { "result": false }

                 Respond in JSON format only, with the following fields:
                 - result
                 - hasTime
                 - datetime
                 - content

                 No explanations.

                 ===

                 input:

                 {
                     "date": "%s",
                     "content": "%s"
                 }
                """, request.date(), request.content());
    } // TODO: input 부분은 request 를 직접 json 객체로 변환한 후 넣도록 해야 적절한 escape 가 이루어진다.

    private TimeExtractionResponse parseResult(String text) {
        String jsonText = text.lines()
                .filter(line -> !line.startsWith("```"))
                .reduce("", (a, b) -> a + b);
        try {
            return objectMapper.readValue(jsonText, TimeExtractionResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}

```

> 튜토리얼 레벨 이상으로 적용하려면 적절한 예외 처리가 필요하다.

## 결론

### 결과

테스트 코드가 정상적으로 통과하는 것을 확인했다.

특이점으로, Gemini에서 기본적으로 제공하는 API를 사용할 때는 RPM 30을 넘는 순간 429 에러가 발생했지만

```json
{
  "error": {
    "code": 429,
    "message": "Resource has been exhausted (e.g. check quota).",
    "status": "RESOURCE_EXHAUSTED"
  }
}
```

OpenAI 호환 API에서는 여러 차례 요청해도 제한에 걸리지 않았다. OpenAI 호환 API는 별도의 RPM 제한을 두지 않거나 훨씬 관대한 정책을 적용하고 있을 것으로 추측된다.

### Spring AI

Spring AI를 사용하면 LLM과의 연동이 훨씬 단순해지고, 유지보수성이 높아진다. 다양한 LLM API와 호환되는 공통 인터페이스를 제공하기 때문에 특정 벤더에 종속되지 않고, 설정만 변경하면 손쉽게 모델을 교체할 수 있다. 또한 API 호출을 직접 다루는 번거로움을 줄이고, 프롬프트 체이닝을 활용해 더 정교한 요청을 설계할 수 있다. 로깅과 테스트 지원이 강력해서 디버깅이 수월하고, Mocking을 활용하면 네트워크 없이도 안정적인 테스트가 가능하다. 결과적으로, Spring Boot 애플리케이션에서 LLM을 활용할 때 좋은 선택이 될 것이다.

아직 정식출시 되지는 않았지만 [v1.0.0-M6](https://github.com/spring-projects/spring-ai/releases/tag/v1.0.0-M6) 까지 나와있으니 미리 익혀두자.

본 글에서 생성한 프로젝트의 전체 코드는 https://github.com/ShanePark/mdblog/tree/main/backend/spring/spring-ai/springAi 에서 확인 할 수 있다.

## References

- https://docs.spring.io/spring-ai/reference/api/chat/openai-chat.html
- https://ai.google.dev/gemini-api/docs/openai
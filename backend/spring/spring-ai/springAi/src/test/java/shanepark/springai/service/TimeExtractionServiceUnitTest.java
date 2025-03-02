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

        // @Test
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

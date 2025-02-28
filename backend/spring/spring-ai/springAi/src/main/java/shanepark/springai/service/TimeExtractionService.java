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
    }

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

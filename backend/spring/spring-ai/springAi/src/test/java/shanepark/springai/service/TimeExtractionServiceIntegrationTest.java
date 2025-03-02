package shanepark.springai.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import shanepark.springai.domain.TimeExtractionRequest;
import shanepark.springai.domain.TimeExtractionResponse;

import java.time.LocalDate;

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

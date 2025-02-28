package shanepark.springai.domain;

import java.time.LocalDate;

public record TimeExtractionRequest(
        LocalDate date,
        String content
) {
}

package shanepark.springai.domain;

public record TimeExtractionResponse(
        boolean result,
        boolean hasTime,
        String datetime,
        String content
) {
}

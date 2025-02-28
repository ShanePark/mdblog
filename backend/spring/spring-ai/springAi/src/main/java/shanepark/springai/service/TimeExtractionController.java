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

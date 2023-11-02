package io.shanepark.github.postgresarrayjpa;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SampleController {

    private final SampleRepository sampleRepository;

    @GetMapping("/samples")
    public List<Sample> findAll() {
        return sampleRepository.findAll();
    }

    @GetMapping("/samples/{id}")
    public Sample find(@PathVariable UUID id) {
        return sampleRepository.findById(id).orElseThrow();
    }

    @PostMapping("/samples")
    public Sample createSample(
            @RequestParam String name,
            @RequestParam(required = false) String[] memo
    ) {
        Sample sample = new Sample(name, memo);
        return sampleRepository.save(sample);
    }

    @DeleteMapping("/samples/{id}")
    public void deleteSample(@PathVariable UUID id) {
        sampleRepository.deleteById(id);
    }

}

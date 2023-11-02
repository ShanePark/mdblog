package io.shanepark.github.postgresarrayjpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SampleRepository extends JpaRepository<Sample, UUID> {
}

package io.shanepark.github.postgresarrayjpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.UUID;

@Entity
@Table(name = "sample")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class Sample {

    @Id
    private UUID id = UUID.randomUUID();

    @Column(name = "name")
    private String name;

    @Type(value = StringArrayType.class)
    @Column(name = "memo", columnDefinition = "text[]")
    private String[] memo = new String[0];

    public Sample(String name, String[] memo) {
        this.name = name;
        if (memo != null) {
            this.memo = memo;
        }
    }

}

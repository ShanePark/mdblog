package io.shanepark.github.postgresarrayjpa;

import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "sample")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
@TypeDef(name = "string-array", typeClass = StringArrayType.class)
public class Sample {

    @Id
    private UUID id = UUID.randomUUID();

    @Column(name = "name")
    private String name;

    @Type(type = "string-array")
    @Column(name = "memo", columnDefinition = "text[]")
    private String[] memo = new String[0];

    public Sample(String name, String[] memo) {
        this.name = name;
        if (memo != null) {
            this.memo = memo;
        }
    }

}

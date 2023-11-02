package io.shanepark.github.postgresarrayjpa;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sample")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
@TypeDef(name = "list-array", typeClass = ListArrayType.class)
public class Sample {

    @Id
    private UUID id = UUID.randomUUID();

    @Column(name = "name")
    private String name;

    @Type(type = "list-array")
    @Column(name = "memo", columnDefinition = "text[]")
    private List<String> memo = new ArrayList<>();

    public Sample(String name, String[] memo) {
        this.name = name;
        if (memo != null) {
            this.memo = List.of(memo);
        }
    }

}

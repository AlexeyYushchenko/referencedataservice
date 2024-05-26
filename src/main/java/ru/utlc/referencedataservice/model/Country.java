package ru.utlc.referencedataservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "country")
public class Country extends AuditingEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "code", unique = true, nullable = false, length = 3)
    private String code;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean active;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "country_localization", joinColumns = @JoinColumn(name = "country_id"))
    @MapKeyColumn(name = "language_code")
    @Column(name = "localized_name")
    private Map<String, String> nameLocales = new HashMap<>();

    public void setName(String name) {
        this.name = name != null ? name.toUpperCase() : null;
    }

    public void setCode(String code) {
        this.code = code != null ? code.toUpperCase() : null;
    }

    public void setActive(Boolean isActive) {
        this.active = isActive != null && isActive;
    }
}
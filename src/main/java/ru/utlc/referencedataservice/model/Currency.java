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
@Table(name = "currency")
public class Currency extends AuditingEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", nullable = false, unique = true, length = 3)
    private String code;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "enabled")
    private Boolean enabled;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "currency_localization", joinColumns = @JoinColumn(name = "currency_id"))
    @MapKeyColumn(name = "language_code")
    @Column(name = "localized_name")
    private Map<String, String> nameLocales = new HashMap<>();

    public void setCode(String code) {
        this.code = code != null ? code.toUpperCase() : null;
    }

    public void setName(String name) {
        this.name = name != null ? name.toUpperCase() : null;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled != null ? enabled : false;
    }

}

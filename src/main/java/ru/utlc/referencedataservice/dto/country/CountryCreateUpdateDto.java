package ru.utlc.referencedataservice.dto.country;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

public record CountryCreateUpdateDto(

        @NotNull(message = "validation.country.name.required")
        @Pattern(regexp = ".*\\S.*", message = "validation.country.name.pattern")
        @Size(min = 2, max = 100, message = "validation.country.name.size")
        String name,

        @NotNull(message = "validation.country.code.required")
        @Size(min = 2, max = 2, message = "validation.country.code.size")
        String code,

        Boolean isActive,

        Map<String, String> nameLocales
) {
    public CountryCreateUpdateDto {
        if (nameLocales == null) {
            nameLocales = new HashMap<>();
        }

        if (name != null) name = name.toUpperCase();

        if (code != null) code = code.toUpperCase();

        if (isActive == null) isActive = false;
    }
}
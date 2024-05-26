package ru.utlc.referencedataservice.dto.currency;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

import java.util.HashMap;
import java.util.Map;

public record CurrencyCreateUpdateDto(

        @NotNull(message = "validation.currency.code.required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "validation.currency.code.pattern")
        String code,

        @Size(max = 50, message = "validation.currency.name.size")
        String name,

        Boolean enabled,

        Map<String, String> nameLocales
) {
    public CurrencyCreateUpdateDto {
        if (nameLocales == null) {
            nameLocales = new HashMap<>();
        }

        if (name != null) name = name.toUpperCase();

        if (code != null) code = code.toUpperCase();

        if (enabled == null) enabled = false;
        //todo если при создании поле отсутствует, то оно true. А если при обновлении поле отсутствует, то должно остаться без изменений.
    }
}

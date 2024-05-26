package ru.utlc.referencedataservice.util;

import lombok.experimental.UtilityClass;
import ru.utlc.referencedataservice.dto.country.CountryReadDto;
import ru.utlc.referencedataservice.dto.currency.CurrencyReadDto;

import java.util.Locale;

@UtilityClass
public class LocalizationUtil {

    public static CountryReadDto toLocalizedDto(CountryReadDto dto, Locale locale) {
        return new CountryReadDto(
                dto.id(),
                dto.nameLocales().getOrDefault(locale.getLanguage(), dto.name()),
                dto.code(),
                dto.isActive(),
                dto.nameLocales(),
                dto.auditingInfoDto()
        );
    }

    public static CurrencyReadDto toLocalizedDto(CurrencyReadDto dto, Locale locale) {
        return new CurrencyReadDto(
                dto.id(),
                dto.code(),
                dto.nameLocales().getOrDefault(locale.getLanguage(), dto.name()),
                dto.enabled(),
                dto.nameLocales(),
                dto.auditingInfoDto()
        );
    }
}
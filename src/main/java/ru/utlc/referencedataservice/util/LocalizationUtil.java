package ru.utlc.referencedataservice.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.utlc.referencedataservice.dto.country.CountryReadDto;

import java.util.Locale;

@Component
@Slf4j
@RequiredArgsConstructor
public class LocalizationUtil {

    public CountryReadDto toLocalizedDto(CountryReadDto dto, Locale locale) {
        try {
            return new CountryReadDto(
                    dto.id(),
                    dto.nameLocales().getOrDefault(locale.getLanguage(), dto.name()),
                    dto.code(),
                    dto.isActive(),
                    dto.nameLocales(),
                    dto.auditingInfoDto()
            );
        }catch (Exception e){
            log.error("Exception in {} with cause: {}", LocalizationUtil.class.getSimpleName(), e.getMessage());
            return null;
        }
    }

//    public CurrencyReadDto toLocalizedDto(CurrencyReadDto dto, Locale locale) {
//        return new CurrencyReadDto(
//                dto.id(),
//                dto.code(),
//                dto.nameLocales().getOrDefault(locale.getLanguage(), dto.name()),
//                dto.enabled(),
//                dto.nameLocales(),
//                dto.auditingInfoDto()
//        );
//    }
}
package ru.utlc.referencedataservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.utlc.referencedataservice.dto.country.CountryCreateUpdateDto;
import ru.utlc.referencedataservice.dto.country.CountryReadDto;
import ru.utlc.referencedataservice.exception.CountryCreationException;
import ru.utlc.referencedataservice.mapper.CountryMapper;
import ru.utlc.referencedataservice.repository.CountryRepository;
import ru.utlc.referencedataservice.util.LocalizationUtil;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CountryService {
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;
    private final MessageSource messageSource;
    private final LocalizationUtil localizationUtil;
    private final CacheManager cacheManager;

    @Cacheable("countries")
    public List<CountryReadDto> findAll(Locale locale) {
        return countryRepository.findAll().stream()
                .map(country -> localizationUtil.toLocalizedDto(countryMapper.toDto(country), locale))
                .toList();
    }

    @Cacheable(value = "countries", key="#p0 + #p1.toString()")
    public Optional<CountryReadDto> findById(Integer id, Locale locale) {
        return countryRepository.findById(id)
                .map(country -> localizationUtil.toLocalizedDto(countryMapper.toDto(country), locale));
    }

    @Transactional
    @CachePut(value = "countries", key = "#result.id + #p1.toString()")
    public CountryReadDto create(CountryCreateUpdateDto createUpdateDto, Locale locale) throws CountryCreationException {
        return Optional.of(createUpdateDto)
                .map(countryMapper::toEntity)
                .map(countryRepository::save)
                .map(countryMapper::toDto)
                .map(dto -> localizationUtil.toLocalizedDto(dto, locale))
                .orElseThrow(() -> new CountryCreationException(
                        messageSource.getMessage("error.entity.country.creation", null, locale)));
    }

    @Transactional
    @CachePut(value = "countries", key="#p0 + #p2.toString()")
    public Optional<CountryReadDto> update(Integer id, CountryCreateUpdateDto dto, Locale locale) {
        return countryRepository.findById(id)
                .map(country -> {
                    Set<String> locales = new HashSet<>(country.getNameLocales().keySet());
                    locales.addAll(dto.nameLocales().keySet());
                    evictCountryFromCache(id, locales);
                    return country;
                })
                .map(entity -> countryMapper.update(entity, dto))
                .map(countryRepository::saveAndFlush) //no request to the db without 'flush', thus we can get exception on dif.level
                .map(country -> localizationUtil.toLocalizedDto(countryMapper.toDto(country), locale));
    }

    @Transactional
    @CacheEvict(value = "countries", allEntries = true)
    public boolean delete(Integer id) {
        return countryRepository.findById(id)
                .map(country -> {
                    evictCountryFromCache(id, country.getNameLocales().keySet());
                    countryRepository.delete(country);
                    countryRepository.flush();
                    return true;
                })
                .orElse(false);
    }

    private void evictCountryFromCache(Integer id, Set<String> locales) {
        var cache = cacheManager.getCache("countries");
        if (cache != null) {
            locales.forEach(language -> {
                Locale locale = Locale.forLanguageTag(language);
                String cacheKey = id + locale.toString();
                cache.evictIfPresent(cacheKey);
            });
        }
    }
}
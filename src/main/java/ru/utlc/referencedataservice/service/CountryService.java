package ru.utlc.referencedataservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.utlc.referencedataservice.dto.country.CountryCreateUpdateDto;
import ru.utlc.referencedataservice.dto.country.CountryReadDto;
import ru.utlc.referencedataservice.exception.CountryCreationException;
import ru.utlc.referencedataservice.mapper.CountryMapper;
import ru.utlc.referencedataservice.repository.CountryRepository;
import ru.utlc.referencedataservice.util.LocalizationUtil;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CountryService {
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;
    private final MessageSource messageSource;

    public List<CountryReadDto> findAll(Locale locale) {
        return countryRepository.findAll().stream()
                .map(country -> LocalizationUtil.toLocalizedDto(countryMapper.toDto(country), locale))
                .toList();
    }

    public Optional<CountryReadDto> findById(Integer id, Locale locale) {
        return countryRepository.findById(id)
                .map(country -> LocalizationUtil.toLocalizedDto(countryMapper.toDto(country), locale));
    }

    @Transactional
    public CountryReadDto create(CountryCreateUpdateDto createUpdateDto, Locale locale) throws CountryCreationException {
        return Optional.of(createUpdateDto)
                .map(countryMapper::toEntity)
                .map(countryRepository::save)
                .map(countryMapper::toDto)
                .map(dto -> LocalizationUtil.toLocalizedDto(dto, locale))
                .orElseThrow(() -> new CountryCreationException(
                        messageSource.getMessage("error.entity.country.creation", null, locale)));
    }

    @Transactional
    public Optional<CountryReadDto> update(Integer id, CountryCreateUpdateDto dto, Locale locale) {
        return countryRepository.findById(id)
                .map(entity -> countryMapper.update(entity, dto))
                .map(countryRepository::saveAndFlush) //no request to the db without 'flush', thus we can get exception on dif.level
                .map(country -> LocalizationUtil.toLocalizedDto(countryMapper.toDto(country), locale));
    }

    @Transactional
    public boolean delete(Integer id) {
        return countryRepository.findById(id)
                .map(country -> {
                    countryRepository.delete(country);
                    countryRepository.flush();
                    return true;
                })
                .orElse(false);
    }
}

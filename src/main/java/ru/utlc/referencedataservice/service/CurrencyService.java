package ru.utlc.referencedataservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.utlc.referencedataservice.dto.currency.CurrencyCreateUpdateDto;
import ru.utlc.referencedataservice.dto.currency.CurrencyReadDto;
import ru.utlc.referencedataservice.exception.CountryCreationException;
import ru.utlc.referencedataservice.mapper.CurrencyMapper;
import ru.utlc.referencedataservice.repository.CurrencyRepository;
import ru.utlc.referencedataservice.util.LocalizationUtil;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;
    private final MessageSource messageSource;

    public List<CurrencyReadDto> findAll(Locale locale) {
        return currencyRepository.findAll().stream()
                .map(currency -> LocalizationUtil.toLocalizedDto(currencyMapper.toDto(currency), locale))
                .toList();
    }

    public Optional<CurrencyReadDto> findById(Integer id, Locale locale) {
        return currencyRepository.findById(id)
                .map(currency -> LocalizationUtil.toLocalizedDto(currencyMapper.toDto(currency), locale));
    }

    @Transactional
    public CurrencyReadDto create(CurrencyCreateUpdateDto createDto, Locale locale) throws CountryCreationException {
        return Optional.of(createDto)
                .map(currencyMapper::toEntity)
                .map(currencyRepository::save)
                .map(currencyMapper::toDto)
                .map(dto -> LocalizationUtil.toLocalizedDto(dto, locale))
                .orElseThrow(() -> new CountryCreationException(
                messageSource.getMessage("error.currency.creation", null, locale)));
    }

    @Transactional
    public Optional<CurrencyReadDto> update(Integer id, CurrencyCreateUpdateDto dto, Locale locale) {
        return currencyRepository.findById(id)
                .map(entity -> currencyMapper.update(entity, dto))
                .map(currencyRepository::saveAndFlush) //no request to the db without 'flush', thus we can get exception on dif.level
                .map(currency -> LocalizationUtil.toLocalizedDto(currencyMapper.toDto(currency), locale));
    }

    @Transactional
    public boolean delete(Integer id) {
        return currencyRepository.findById(id)
                .map(currency -> {
                    currencyRepository.delete(currency);
                    currencyRepository.flush();
                    return true;
                })
                .orElse(false);
    }
}

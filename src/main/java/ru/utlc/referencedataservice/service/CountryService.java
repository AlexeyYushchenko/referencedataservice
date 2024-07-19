package ru.utlc.referencedataservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.utlc.referencedataservice.constants.CacheNames;
import ru.utlc.referencedataservice.dto.country.CountryCreateUpdateDto;
import ru.utlc.referencedataservice.dto.country.CountryReadDto;
import ru.utlc.referencedataservice.exception.CountryCreationException;
import ru.utlc.referencedataservice.mapper.CountryMapper;
import ru.utlc.referencedataservice.repository.CountryRepository;
import java.util.List;
import java.util.Optional;
import static ru.utlc.referencedataservice.constants.CacheNames.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CountryService {
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;
    private final CacheManager cacheManager;

    @Cacheable(value = COUNTRIES, key = "'all'")
    public List<CountryReadDto> findAll() {
        var countries = countryRepository.findAll().stream()
                .map(countryMapper::toDto)
                .toList();
        countries.forEach(country -> cacheManager.getCache(COUNTRIES).put(country.id(), country));
        return countries;
    }

    @Cacheable(value = COUNTRIES, key="#p0")
    public Optional<CountryReadDto> findById(Integer id) {
        return countryRepository.findById(id).map(countryMapper::toDto);
    }

    @Transactional
    @CacheEvict(value = COUNTRIES, allEntries = true)
    @CachePut(value = COUNTRIES, key = "#result.id")
    public CountryReadDto create(CountryCreateUpdateDto createUpdateDto) throws CountryCreationException {
        return Optional.of(createUpdateDto)
                .map(countryMapper::toEntity)
                .map(countryRepository::save)
                .map(countryMapper::toDto)
                .orElseThrow(() -> new CountryCreationException("error.entity.country.creation"));
    }

    @Transactional
    @CacheEvict(value = COUNTRIES, allEntries = true)
    @CachePut(value = COUNTRIES, key="#p0")
    public Optional<CountryReadDto> update(Integer id, CountryCreateUpdateDto dto) {
        return countryRepository.findById(id)
                .map(entity -> countryMapper.update(entity, dto))
                .map(countryRepository::saveAndFlush) //no request to the db without 'flush', thus we can get exception on dif.level
                .map(countryMapper::toDto);
    }

    @Transactional
    @CacheEvict(value = COUNTRIES, allEntries = true)
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
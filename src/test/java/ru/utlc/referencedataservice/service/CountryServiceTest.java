package ru.utlc.referencedataservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.utlc.referencedataservice.dto.country.CountryCreateUpdateDto;
import ru.utlc.referencedataservice.dto.country.CountryReadDto;
import ru.utlc.referencedataservice.exception.CountryCreationException;
import ru.utlc.referencedataservice.mapper.CountryMapper;
import ru.utlc.referencedataservice.model.Country;
import ru.utlc.referencedataservice.repository.CountryRepository;
import ru.utlc.referencedataservice.util.LocalizationUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    public static final int ID = 1;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CountryMapper countryMapper;

    @Mock
    private LocalizationUtil localizationUtil;

    @InjectMocks
    private CountryService countryService;

    @Test
    void testFindAll() {
        Locale locale = Locale.ENGLISH;
        Country country = mock(Country.class);
        CountryReadDto countryReadDto = new CountryReadDto(ID, "CountryName", "CN", true, new HashMap<>(), null);

        when(countryRepository.findAll()).thenReturn(List.of(country));
        when(countryMapper.toDto(country)).thenReturn(countryReadDto);
        when(localizationUtil.toLocalizedDto(countryReadDto, locale)).thenReturn(countryReadDto);

        List<CountryReadDto> result = countryService.findAll(locale);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(countryReadDto, result.get(0));

        verify(countryRepository, times(1)).findAll();
        verify(countryMapper, times(1)).toDto(country);
        verifyNoMoreInteractions(countryRepository, countryMapper);
    }

    @Test
    void testFindById() {
        Locale locale = Locale.ENGLISH;
        Country country = mock(Country.class);
        CountryReadDto countryReadDto = new CountryReadDto(ID, "CountryName", "CN", true, new HashMap<>(), null);

        when(countryRepository.findById(ID)).thenReturn(Optional.of(country));
        when(countryMapper.toDto(country)).thenReturn(countryReadDto);
        when(localizationUtil.toLocalizedDto(countryReadDto, locale)).thenReturn(countryReadDto);

        Optional<CountryReadDto> result = countryService.findById(ID, locale);

        assertTrue(result.isPresent());
        assertEquals(countryReadDto, result.get());

        verify(countryRepository, times(1)).findById(ID);
        verify(countryMapper, times(1)).toDto(country);
        verifyNoMoreInteractions(countryRepository, countryMapper);
    }

    @Test
    void testCreate() throws CountryCreationException {
        CountryCreateUpdateDto createUpdateDto = new CountryCreateUpdateDto("CountryName", "CN", true, new HashMap<>());
        Locale locale = Locale.ENGLISH;
        Country country = mock(Country.class);
        CountryReadDto countryReadDto = new CountryReadDto(ID, "CountryName", "CN", true, new HashMap<>(), null);

        when(countryMapper.toEntity(createUpdateDto)).thenReturn(country);
        when(countryRepository.save(country)).thenReturn(country);
        when(countryMapper.toDto(country)).thenReturn(countryReadDto);
        when(localizationUtil.toLocalizedDto(countryReadDto, locale)).thenReturn(countryReadDto);

        CountryReadDto result = countryService.create(createUpdateDto, locale);

        assertNotNull(result);
        assertEquals(countryReadDto, result);

        verify(countryMapper, times(1)).toEntity(createUpdateDto);
        verify(countryRepository, times(1)).save(country);
        verify(countryMapper, times(1)).toDto(country);
        verifyNoMoreInteractions(countryMapper, countryRepository);
    }

    @Test
    void testUpdate() {
        CountryCreateUpdateDto createUpdateDto = new CountryCreateUpdateDto("UpdatedCountryName", "UC", true, new HashMap<>());
        Locale locale = Locale.ENGLISH;
        Country country = mock(Country.class);
        CountryReadDto countryReadDto = new CountryReadDto(ID, "UpdatedCountryName", "UC", true, new HashMap<>(), null);

        when(countryRepository.findById(ID)).thenReturn(Optional.of(country));
        when(countryMapper.update(country, createUpdateDto)).thenReturn(country);
        when(countryRepository.saveAndFlush(country)).thenReturn(country);
        when(countryMapper.toDto(country)).thenReturn(countryReadDto);
        when(localizationUtil.toLocalizedDto(countryReadDto, locale)).thenReturn(countryReadDto);

        Optional<CountryReadDto> result = countryService.update(ID, createUpdateDto, locale);

        assertTrue(result.isPresent());
        assertEquals(countryReadDto, result.get());

        verify(countryRepository, times(1)).findById(ID);
        verify(countryMapper, times(1)).update(country, createUpdateDto);
        verify(countryRepository, times(1)).saveAndFlush(country);
        verifyNoMoreInteractions(countryRepository, countryMapper);
    }

    @Test
    void testDelete() {
        Integer id = ID;
        Country country = mock(Country.class);

        when(countryRepository.findById(id)).thenReturn(Optional.of(country));

        boolean result = countryService.delete(id);

        assertTrue(result);

        verify(countryRepository, times(1)).findById(id);
        verify(countryRepository, times(1)).delete(country);
        verify(countryRepository, times(1)).flush();
        verifyNoMoreInteractions(countryRepository);
    }
}
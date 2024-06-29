package ru.utlc.referencedataservice.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.utlc.referencedataservice.dto.country.CountryCreateUpdateDto;
import ru.utlc.referencedataservice.dto.country.CountryReadDto;
import ru.utlc.referencedataservice.exception.CountryCreationException;
import ru.utlc.referencedataservice.service.CountryService;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class CountryServiceIT extends IntegrationTestBase {

    @Autowired
    private CountryService countryService;

    private static final Integer COUNTRY_ID = 1;

    private final Locale englishLocale = Locale.ENGLISH;

    @Test
    void testFindAllCaching() {
        var all = countryService.findAll(englishLocale);
        System.out.println("Size: %d".formatted(all.size()));
    }

    @Test
    void testFindByIdCaching() {
        Optional<CountryReadDto> maybeCountry = countryService.findById(COUNTRY_ID, englishLocale);
        assertTrue(maybeCountry.isPresent());
        maybeCountry.ifPresent(country -> assertEquals("AUSTRALIA", country.name()));
    }

    @Test
    void testCreateCache() throws CountryCreationException {
        CountryCreateUpdateDto countryDto = new CountryCreateUpdateDto(
                "TEST COUNTRY",
                "TC",
                true,
                new LinkedHashMap<>()
        );
        CountryReadDto actualResult = countryService.create(countryDto, englishLocale);

        assertEquals(countryDto.name(), actualResult.name());
        assertEquals(countryDto.code(), actualResult.code());
        assertEquals(countryDto.isActive(), actualResult.isActive());
        assertEquals(countryDto.nameLocales(), actualResult.nameLocales());

        countryDto = new CountryCreateUpdateDto(
                "TEST COUNTRY 2",
                "T2",
                true,
                null
        );
        actualResult = countryService.create(countryDto, englishLocale);

        assertEquals(countryDto.name(), actualResult.name());
        assertEquals(countryDto.code(), actualResult.code());
        assertEquals(countryDto.isActive(), actualResult.isActive());
        assertTrue(actualResult.nameLocales().isEmpty());

        countryDto = new CountryCreateUpdateDto(
                "TEST COUNTRY 3",
                "T3",
                null,
                null
        );
        actualResult = countryService.create(countryDto, englishLocale);

        assertEquals(countryDto.name(), actualResult.name());
        assertEquals(countryDto.code(), actualResult.code());
        assertEquals(false, actualResult.isActive());
        assertTrue(actualResult.nameLocales().isEmpty());

        //Testing under different locale
        Locale russianLocale = Locale.forLanguageTag("ru");
        countryDto = new CountryCreateUpdateDto(
                "Тестовая страна",
                "ЩЩ",
                true,
                Map.of("en", "Test country", "it", "Il paese di test")
        );
        actualResult = countryService.create(countryDto, russianLocale);

        assertEquals(countryDto.name(), actualResult.name());
        assertEquals(countryDto.code(), actualResult.code());
        assertEquals(true, actualResult.isActive());
        assertEquals(2, actualResult.nameLocales().size());
    }

    @Test
    void testUpdateUpdatesCache() {
        CountryCreateUpdateDto countryDto = new CountryCreateUpdateDto(
                "TEST COUNTRY",
                "TC",
                true,
                null
        );

        Optional<CountryReadDto> actualResult = countryService.update(COUNTRY_ID, countryDto, englishLocale);

        assertTrue(actualResult.isPresent());
        actualResult.ifPresent(country -> {
            assertEquals(countryDto.name(), country.name());
            assertEquals(countryDto.code(), country.code());
            assertEquals(countryDto.isActive(), country.isActive());
            assertTrue(country.nameLocales().isEmpty());
        });

        CountryCreateUpdateDto countryDto2 = new CountryCreateUpdateDto(
                "TEST COUNTRY2",
                "T2", null, null
        );

        Optional<CountryReadDto> actualResult2 = countryService.update(COUNTRY_ID, countryDto2, englishLocale);

        assertTrue(actualResult2.isPresent());
        actualResult2.ifPresent(country -> {
            assertEquals(countryDto2.name(), country.name());
            assertEquals(countryDto2.code(), country.code());
            assertEquals(false, country.isActive());
            assertEquals(countryDto2.nameLocales(), country.nameLocales());
        });

        CountryCreateUpdateDto countryDto3 = new CountryCreateUpdateDto(
                "BY DEFAULT NAME",
                "T3",
                false,
                Map.of("en", "TEST COUNTRY 3", "ru", "ТЕСТ СТРАНА 3", "it", "IL PAESE PER IL TEST 3")
        );
        var actualResult3 = countryService.create(countryDto3, englishLocale);
        var actualId = actualResult3.id();
        assertNotNull(actualResult3);
        assertFalse(actualResult3.isActive());
        assertEquals("TEST COUNTRY 3", actualResult3.name());
        assertEquals(countryDto3.code(), actualResult3.code());
        assertEquals(countryDto3.isActive(), actualResult3.isActive());
        assertEquals(countryDto3.nameLocales(), actualResult3.nameLocales());

        var changedRuName = "ИЗМЕНЕННЫЙ ТЕСТ СТРАНА 3";
        var changedItName = "Il nome e' stato cambiato";
        var byDefaultName = "BY DEFAULT NAME";
        var changedListOfLocalizedNames = Map.of("ru", changedRuName, "it", changedItName);
        countryDto3 = new CountryCreateUpdateDto(
                byDefaultName,
                "T3",
                true,
                changedListOfLocalizedNames
        );

        Optional<CountryReadDto> actualResult4 = countryService.findById(actualId, englishLocale); //our request puts entity's value in cache.
        countryService.update(actualId, countryDto3, Locale.forLanguageTag("ru")); //updating under different locale.
        actualResult4 = countryService.findById(actualId, englishLocale); //while requesting under different locale

        assertTrue(actualResult4.isPresent());
        actualResult4.ifPresent(country -> {
            assertEquals(true, country.isActive());
            assertEquals(byDefaultName, country.name());
            assertEquals(country.nameLocales(), changedListOfLocalizedNames);
            assertNotNull(country.nameLocales().get("ru"));
            assertEquals(country.nameLocales().get("ru"), changedRuName);
            assertNotNull(country.nameLocales().get("it"));
            assertEquals(country.nameLocales().get("it"), changedItName);
            assertNull(country.nameLocales().get("en"));
        });
    }

    @Test
    void testDeleteEvictsCache() {
        assertFalse(countryService.delete(-124));
        assertTrue(countryService.delete(COUNTRY_ID));
    }
}
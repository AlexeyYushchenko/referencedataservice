package ru.utlc.referencedataservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.utlc.referencedataservice.dto.country.CountryCreateUpdateDto;
import ru.utlc.referencedataservice.dto.country.CountryReadDto;
import ru.utlc.referencedataservice.model.Country;

@Mapper
public interface CountryMapper {
        @Mapping(target = "auditingInfoDto", source = ".")
        CountryReadDto toDto(Country country);  // Entity to DTO

        Country toEntity(CountryCreateUpdateDto createUpdateDto);

        Country update(@MappingTarget Country country, CountryCreateUpdateDto countryCreateUpdateDto);
}
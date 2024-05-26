package ru.utlc.referencedataservice.dto.country;

import ru.utlc.referencedataservice.dto.auditinginfo.AuditingInfoDto;

import java.util.Map;

public record CountryReadDto(
        Integer id,
        String name,
        String code,
        Boolean isActive,
        Map<String, String> nameLocales,
        AuditingInfoDto auditingInfoDto
) {}
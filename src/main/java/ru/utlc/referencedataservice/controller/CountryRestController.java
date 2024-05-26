package ru.utlc.referencedataservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.utlc.referencedataservice.dto.country.CountryCreateUpdateDto;
import ru.utlc.referencedataservice.dto.country.CountryReadDto;
import ru.utlc.referencedataservice.exception.CountryCreationException;
import ru.utlc.referencedataservice.response.Response;
import ru.utlc.referencedataservice.service.CountryService;
import java.net.URI;
import java.util.List;
import java.util.Locale;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/countries")
public class CountryRestController {

    private final CountryService countryService;
    private final MessageSource messageSource;

    @Cacheable("countries")
    @GetMapping
    public ResponseEntity<List<CountryReadDto>> findAll() {
        return ResponseEntity.ok(countryService.findAll(getLocale()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CountryReadDto> findById(@PathVariable("id") final Integer id) {
        return countryService.findById(id, getLocale())
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> create(@RequestBody @Validated final CountryCreateUpdateDto dto,
                                           final BindingResult bindingResult) throws CountryCreationException {

        if (bindingResult.hasFieldErrors()){
            return handleValidationErrors(bindingResult);
        }

        final CountryReadDto localizedCountry = countryService.create(dto, getLocale());
        final URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(localizedCountry.id())
                .toUri();

        return ResponseEntity.created(location).body(new Response(null, localizedCountry));
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, value = "/{id}")
    public ResponseEntity<Response> update(@PathVariable("id") final Integer id,
                                           @RequestBody @Validated final CountryCreateUpdateDto dto,
                                           final BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors()){
            return handleValidationErrors(bindingResult);
        }

        return countryService.update(id, dto, getLocale())
                .map(localizedCountry -> {
                    return new ResponseEntity<>(new Response(localizedCountry), HttpStatus.OK);
                })
                .orElseGet(() -> {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") final Integer id) {
        if (countryService.delete(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private ResponseEntity<Response> handleValidationErrors(final BindingResult bindingResult) {
        final List<String> errorMessages = bindingResult.getFieldErrors().stream()
                .map(error -> messageSource.getMessage(error.getDefaultMessage(), null, getLocale()))
                .toList();

        final Response response = new Response(errorMessages, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

}

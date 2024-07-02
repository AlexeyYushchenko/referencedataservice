package ru.utlc.referencedataservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.utlc.referencedataservice.dto.country.CountryCreateUpdateDto;
import ru.utlc.referencedataservice.dto.country.CountryReadDto;
import ru.utlc.referencedataservice.response.Response;
import ru.utlc.referencedataservice.service.CountryService;
import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/countries")
public class CountryRestController {

    private final CountryService countryService;

    @GetMapping
    public ResponseEntity<List<CountryReadDto>> findAll() {
        return ResponseEntity.ok(countryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CountryReadDto> findById(@PathVariable("id") final Integer id) {
        return countryService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> create(@RequestBody @Validated final CountryCreateUpdateDto dto,
                                           final BindingResult bindingResult) {

        if (bindingResult.hasFieldErrors()){
            return handleValidationErrors(bindingResult);
        }

        final CountryReadDto createdDto = countryService.create(dto);
        final URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdDto.id())
                .toUri();

        return ResponseEntity.created(location).body(new Response(null, createdDto));
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, value = "/{id}")
    public ResponseEntity<Response> update(@PathVariable("id") final Integer id,
                                           @RequestBody @Validated final CountryCreateUpdateDto dto,
                                           final BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors()){
            return handleValidationErrors(bindingResult);
        }

        return countryService.update(id, dto)
                .map(updatedDto -> {
                    return new ResponseEntity<>(new Response(updatedDto), HttpStatus.OK);
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
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        final Response response = new Response(errorMessages, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}

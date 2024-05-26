package ru.utlc.referencedataservice.advice;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.utlc.referencedataservice.exception.CountryCreationException;
import ru.utlc.referencedataservice.exception.CurrencyCreationException;
import ru.utlc.referencedataservice.response.Response;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> messageSource.getMessage(error.getDefaultMessage(), null, LocaleContextHolder.getLocale()))
                .toList();
        Response response = new Response(errorMessages, null);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleBindException(BindException ex) {
        List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> messageSource.getMessage(error.getDefaultMessage(), null, LocaleContextHolder.getLocale()))
                .toList();
        Response response = new Response(errorMessages, null);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Response> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String errorMessage = messageSource.getMessage("error.database.agent.uniqueConstraintViolation", null, LocaleContextHolder.getLocale());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response(errorMessage));
    }

    @ExceptionHandler(CountryCreationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleEntityCreationException(CountryCreationException ex) {
        String errorMessage = messageSource.getMessage("error.entity.country.creation", null, LocaleContextHolder.getLocale());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(errorMessage));
    }

    @ExceptionHandler(CurrencyCreationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleEntityCreationException(CurrencyCreationException ex) {
        String errorMessage = messageSource.getMessage("error.entity.currency.creation", null, LocaleContextHolder.getLocale());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(errorMessage));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Response> handleGeneralException(Exception ex) {
        String errorMessage = messageSource.getMessage("error.general", null, LocaleContextHolder.getLocale());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(errorMessage));
    }
}

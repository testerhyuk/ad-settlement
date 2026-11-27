package io.github.hyuk.adsettlement.advertising.presentation.advice;

import io.github.hyuk.adsettlement.advertising.exception.DuplicateBusinessRegistrationNumberException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class AdvertiserExceptionHandler {
    @ExceptionHandler(DuplicateBusinessRegistrationNumberException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateBusinessRegistrationNumber(
            DuplicateBusinessRegistrationNumberException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("message", exception.getMessage()));
    }
}

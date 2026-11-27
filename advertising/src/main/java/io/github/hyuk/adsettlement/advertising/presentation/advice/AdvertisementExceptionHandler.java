package io.github.hyuk.adsettlement.advertising.presentation.advice;

import io.github.hyuk.adsettlement.advertising.exception.DuplicateAdvertisementNameException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class AdvertisementExceptionHandler {
    @ExceptionHandler(DuplicateAdvertisementNameException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateAdvertisementName(
            DuplicateAdvertisementNameException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("message", exception.getMessage()));
    }
}

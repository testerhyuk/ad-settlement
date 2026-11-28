package io.github.hyuk.adsettlement.publisher.presentation.advice;

import io.github.hyuk.adsettlement.publisher.exception.DuplicateSiteUrlException;
import io.github.hyuk.adsettlement.publisher.exception.PublisherIdNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class PublisherExceptionHandler {
    @ExceptionHandler(DuplicateSiteUrlException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateSiteUrl(
            DuplicateSiteUrlException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(PublisherIdNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePublisherIdNotFound(
            PublisherIdNotFoundException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", exception.getMessage()));
    }
}

package io.github.hyuk.adsettlement.publisher.presentation.advice;

import io.github.hyuk.adsettlement.publisher.exception.AdPlacementNotFoundException;
import io.github.hyuk.adsettlement.publisher.exception.DuplicatePublisherIdAndPlacementNameException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class AdPlacementExceptionHandler {
    @ExceptionHandler(DuplicatePublisherIdAndPlacementNameException.class)
    public ResponseEntity<Map<String, String>> handleDuplicatePublisherIdAndPlacementName(
            DuplicatePublisherIdAndPlacementNameException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(AdPlacementNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleAdPlacementNotFound(
            AdPlacementNotFoundException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", exception.getMessage()));
    }
}

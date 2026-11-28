package io.github.hyuk.adsettlement.publisher.presentation;

import io.github.hyuk.adsettlement.publisher.business.PublisherService;
import io.github.hyuk.adsettlement.publisher.presentation.dto.PublisherDetailResponse;
import io.github.hyuk.adsettlement.publisher.presentation.dto.PublisherRequest;
import io.github.hyuk.adsettlement.publisher.presentation.dto.PublisherResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PublisherController {
    private final PublisherService publisherService;

    @PostMapping("/api/v1/publishers")
    public ResponseEntity<PublisherResponse> register(@RequestBody @Valid PublisherRequest request) {
        PublisherResponse response = publisherService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/v1/publishers/{publisherId}")
    public ResponseEntity<PublisherResponse> getPublisher(@PathVariable("publisherId") Long publisherId) {
        PublisherResponse response = publisherService.getPublisher(publisherId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/v1/publishers/{publisherId}/ad-placements")
    public ResponseEntity<PublisherDetailResponse> getPublisherWithAdPlacementList(
            @PathVariable("publisherId") Long publisherId
    ) {
        PublisherDetailResponse response = publisherService.getPublisherWithAdPlacementList(publisherId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

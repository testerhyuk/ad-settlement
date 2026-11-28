package io.github.hyuk.adsettlement.publisher.presentation;

import io.github.hyuk.adsettlement.publisher.business.AdPlacementService;
import io.github.hyuk.adsettlement.publisher.presentation.dto.AdPlacementDetailResponse;
import io.github.hyuk.adsettlement.publisher.presentation.dto.AdPlacementRequest;
import io.github.hyuk.adsettlement.publisher.presentation.dto.AdPlacementResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdPlacementController {
    private final AdPlacementService adPlacementService;

    @PostMapping("/api/v1/publishers/{publisherId}/ad-placements")
    public ResponseEntity<AdPlacementResponse> register(
            @RequestBody @Valid AdPlacementRequest request,
            @PathVariable("publisherId") Long publisherId
    ) {
        AdPlacementResponse response = adPlacementService.register(request, publisherId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/v1/ad-placements/{placementId}")
    public ResponseEntity<AdPlacementDetailResponse> getAdPlacement(@PathVariable("placementId") Long placementId) {
        AdPlacementDetailResponse response = adPlacementService.getAdPlacement(placementId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

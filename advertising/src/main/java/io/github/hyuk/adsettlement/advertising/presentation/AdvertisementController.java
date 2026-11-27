package io.github.hyuk.adsettlement.advertising.presentation;

import io.github.hyuk.adsettlement.advertising.business.AdvertisementService;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertisementRequest;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertisementResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdvertisementController {
    private final AdvertisementService advertisementService;

    @PostMapping("/api/v1/advertisements")
    public ResponseEntity<AdvertisementResponse> register(@RequestBody @Valid AdvertisementRequest request) {
        AdvertisementResponse response = advertisementService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

package io.github.hyuk.adsettlement.advertising.presentation;

import io.github.hyuk.adsettlement.advertising.business.AdvertiserService;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertiserDetailResponse;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertiserRequest;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertiserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdvertiserController {
    private final AdvertiserService advertiserService;

    @PostMapping("/api/v1/advertisers")
    public ResponseEntity<AdvertiserResponse> register(@RequestBody @Valid AdvertiserRequest request) {
        AdvertiserResponse response = advertiserService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/v1/advertisers/{advertiserId}")
    public ResponseEntity<AdvertiserResponse> getAdvertiser(@PathVariable("advertiserId") Long advertiserId) {
        AdvertiserResponse response = advertiserService.getAdvertiser(advertiserId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/v1/advertisers/{advertiserId}/advertisements")
    public ResponseEntity<AdvertiserDetailResponse> getAdvertiserWithAdvertisementList(
            @PathVariable("advertiserId") Long advertiserId
    ) {
        AdvertiserDetailResponse response = advertiserService.getAdvertiserWithAdvertisementList(advertiserId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

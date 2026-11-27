package io.github.hyuk.adsettlement.advertising.presentation;

import io.github.hyuk.adsettlement.advertising.business.AdvertiserService;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertiserRequest;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertiserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdvertiserController {
    private final AdvertiserService advertiserService;

    @PostMapping("/api/v1/advertisers")
    public ResponseEntity<AdvertiserResponse> register(@RequestBody @Valid AdvertiserRequest request) {
        AdvertiserResponse response = advertiserService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

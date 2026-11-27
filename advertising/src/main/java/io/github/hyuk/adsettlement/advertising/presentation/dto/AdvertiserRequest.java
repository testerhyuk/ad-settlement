package io.github.hyuk.adsettlement.advertising.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdvertiserRequest(
        @NotBlank
        @Size(max = 100)
        String advertiserName,
        @NotBlank
        @Size(max = 10)
        String businessRegistrationNumber
) {
}

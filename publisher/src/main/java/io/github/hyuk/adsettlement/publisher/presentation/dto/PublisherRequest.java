package io.github.hyuk.adsettlement.publisher.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublisherRequest(
        @NotBlank
        @Size(max = 100)
        String publisherName,

        @NotBlank
        @Size(max = 255)
        String siteUrl
) {
}

package io.github.hyuk.adsettlement.publisher.presentation.dto;

import io.github.hyuk.adsettlement.publisher.persistence.AdPlacement;
import io.github.hyuk.adsettlement.publisher.persistence.Publisher;

import java.time.Instant;
import java.util.List;

public record PublisherDetailResponse(
        Long publisherId,
        String publisherName,
        String siteUrl,
        Instant createdAt,
        List<AdPlacementResponse> adPlacements
) {
    public static PublisherDetailResponse from(
            Publisher publisher,
            List<AdPlacement> adPlacements
    ) {
        return new PublisherDetailResponse(
                publisher.getPublisherId(),
                publisher.getPublisherName(),
                publisher.getSiteUrl(),
                publisher.getCreatedAt(),
                adPlacements.stream()
                        .map(AdPlacementResponse::from)
                        .toList()
        );
    }
}

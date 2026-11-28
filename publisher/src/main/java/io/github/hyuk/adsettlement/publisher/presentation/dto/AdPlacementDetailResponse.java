package io.github.hyuk.adsettlement.publisher.presentation.dto;

import io.github.hyuk.adsettlement.publisher.persistence.AdPlacement;

import java.time.Instant;

public record AdPlacementDetailResponse(
        Long publisherId,
        Long placementId,
        String publisherName,
        String siteUrl,
        String placementName,
        String status,
        Instant createdAt
) {
    public static AdPlacementDetailResponse from(AdPlacement adPlacement) {
        return new AdPlacementDetailResponse(
                adPlacement.getPublisher().getPublisherId(),
                adPlacement.getPlacementId(),
                adPlacement.getPublisher().getPublisherName(),
                adPlacement.getPublisher().getSiteUrl(),
                adPlacement.getPlacementName(),
                adPlacement.getStatus().toString(),
                adPlacement.getCreatedAt()
        );
    }
}

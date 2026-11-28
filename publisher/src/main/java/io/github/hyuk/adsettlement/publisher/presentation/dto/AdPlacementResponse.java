package io.github.hyuk.adsettlement.publisher.presentation.dto;

import io.github.hyuk.adsettlement.publisher.persistence.AdPlacement;

import java.time.Instant;

public record AdPlacementResponse(
        Long publisherId,
        Long placementId,
        String placementName,
        String status,
        Instant createdAt
) {
    public static AdPlacementResponse from(AdPlacement adPlacement) {
        return new AdPlacementResponse(
                adPlacement.getPublisher().getPublisherId(),
                adPlacement.getPlacementId(),
                adPlacement.getPlacementName(),
                adPlacement.getStatus().toString(),
                adPlacement.getCreatedAt()
        );
    }
}

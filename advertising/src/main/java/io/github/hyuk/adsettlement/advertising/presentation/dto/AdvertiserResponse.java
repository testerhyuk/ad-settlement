package io.github.hyuk.adsettlement.advertising.presentation.dto;

import io.github.hyuk.adsettlement.advertising.persistence.Advertiser;

import java.time.Instant;

public record AdvertiserResponse(
        Long advertiserId,
        String advertiserName,
        String businessRegistrationNumber,
        Instant createdAt
) {
    public static AdvertiserResponse from(Advertiser advertiser) {
        return new AdvertiserResponse(
                advertiser.getAdvertiserId(),
                advertiser.getAdvertiserName(),
                advertiser.getBusinessRegistrationNumber(),
                advertiser.getCreatedAt()
        );
    }
}

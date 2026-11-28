package io.github.hyuk.adsettlement.advertising.presentation.dto;

import io.github.hyuk.adsettlement.advertising.persistence.Advertisement;
import io.github.hyuk.adsettlement.advertising.persistence.Advertiser;

import java.time.Instant;
import java.util.List;

public record AdvertiserDetailResponse(
        Long advertiserId,
        String advertiserName,
        String businessRegistrationNumber,
        Instant createdAt,
        List<AdvertisementResponse> advertisements
) {
    public static AdvertiserDetailResponse from(
            Advertiser advertiser,
            List<Advertisement> advertisements
    ) {
        return new AdvertiserDetailResponse(
                advertiser.getAdvertiserId(),
                advertiser.getAdvertiserName(),
                advertiser.getBusinessRegistrationNumber(),
                advertiser.getCreatedAt(),
                advertisements.stream()
                        .map(AdvertisementResponse::from)
                        .toList()
        );
    }
}

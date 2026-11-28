package io.github.hyuk.adsettlement.advertising.presentation.dto;

import io.github.hyuk.adsettlement.advertising.persistence.Advertisement;

import java.math.BigDecimal;
import java.time.Instant;

public record AdvertisementDetailResponse(
        Long advertisementId,
        Long advertiserId,
        String advertiserName,
        String advertisementName,
        BigDecimal budgetAmount,
        BigDecimal impressionPrice,
        BigDecimal conversionPrice,
        String status,
        Instant createdAt,
        Instant startedAt,
        Instant expiredAt
) {
    public static AdvertisementDetailResponse from(Advertisement advertisement) {
        return new AdvertisementDetailResponse(
                advertisement.getAdvertisementId(),
                advertisement.getAdvertiser().getAdvertiserId(),
                advertisement.getAdvertiser().getAdvertiserName(),
                advertisement.getAdvertisementName(),
                advertisement.getBudgetAmount(),
                advertisement.getImpressionPrice(),
                advertisement.getConversionPrice(),
                advertisement.getStatus().toString(),
                advertisement.getCreatedAt(),
                advertisement.getStartedAt(),
                advertisement.getExpiredAt()
        );
    }
}

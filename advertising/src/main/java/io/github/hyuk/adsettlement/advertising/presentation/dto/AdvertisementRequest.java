package io.github.hyuk.adsettlement.advertising.presentation.dto;

import io.github.hyuk.adsettlement.advertising.persistence.enums.AdvertisementStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record AdvertisementRequest(
        Long advertiserId,
        String advertisementName,
        BigDecimal budgetAmount,
        BigDecimal impressionPrice,
        BigDecimal conversionPrice,
        AdvertisementStatus status,
        Instant startedAt,
        Instant expiredAt
) {
}

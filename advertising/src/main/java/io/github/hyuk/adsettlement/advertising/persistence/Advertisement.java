package io.github.hyuk.adsettlement.advertising.persistence;

import io.github.hyuk.adsettlement.advertising.persistence.enums.AdvertisementStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "advertisement")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Advertisement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "advertisement_id")
    private Long advertisementId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advertiser_id", nullable = false)
    private Advertiser advertiser;
    @Column(name = "advertisement_name", nullable = false, length = 100)
    private String advertisementName;
    @Column(name = "budget_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal budgetAmount;
    @Column(name = "impression_price", precision = 19, scale = 2, nullable = false)
    private BigDecimal impressionPrice;
    @Column(name = "conversion_price", precision = 19, scale = 2, nullable = false)
    private BigDecimal conversionPrice;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AdvertisementStatus status;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "started_at", nullable = false)
    private Instant startedAt;
    @Column(name = "expired_at", nullable = false)
    private Instant expiredAt;

    public static Advertisement create(
            Advertiser advertiser, String advertisementName, BigDecimal budgetAmount,
            BigDecimal impressionPrice, BigDecimal conversionPrice, AdvertisementStatus status,
            Instant startedAt, Instant expiredAt
    ) {
        Advertisement advertisement = new Advertisement();

        advertisement.advertiser = advertiser;
        advertisement.advertisementName = advertisementName;
        advertisement.budgetAmount = budgetAmount;
        advertisement.impressionPrice = impressionPrice;
        advertisement.conversionPrice = conversionPrice;
        advertisement.status = status;
        advertisement.createdAt = Instant.now();
        advertisement.startedAt = startedAt;
        advertisement.expiredAt = expiredAt;

        return advertisement;
    }
}

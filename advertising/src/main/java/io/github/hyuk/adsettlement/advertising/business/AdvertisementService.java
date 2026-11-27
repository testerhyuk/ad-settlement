package io.github.hyuk.adsettlement.advertising.business;

import io.github.hyuk.adsettlement.advertising.implement.AdvertisementAppender;
import io.github.hyuk.adsettlement.advertising.implement.AdvertisementReader;
import io.github.hyuk.adsettlement.advertising.implement.AdvertiserReader;
import io.github.hyuk.adsettlement.advertising.persistence.Advertisement;
import io.github.hyuk.adsettlement.advertising.persistence.Advertiser;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertisementRequest;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertisementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdvertisementService {
    private final AdvertisementReader advertisementReader;
    private final AdvertisementAppender advertisementAppender;
    private final AdvertiserReader advertiserReader;

    @Transactional
    public AdvertisementResponse register(AdvertisementRequest request) {
        Advertiser advertiser = advertiserReader.getById(request.advertiserId());

        advertisementReader.validateNotExists(
                request.advertiserId(),
                request.advertisementName()
        );

        Advertisement advertisement = Advertisement.create(
                advertiser,
                request.advertisementName(),
                request.budgetAmount(),
                request.impressionPrice(),
                request.conversionPrice(),
                request.status(),
                request.startedAt(),
                request.expiredAt()
        );

        Advertisement savedAdvertisement = advertisementAppender.append(advertisement);

        return AdvertisementResponse.from(savedAdvertisement);
    }
}

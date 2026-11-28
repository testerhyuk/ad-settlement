package io.github.hyuk.adsettlement.advertising.business;

import io.github.hyuk.adsettlement.advertising.implement.AdvertisementReader;
import io.github.hyuk.adsettlement.advertising.implement.AdvertiserAppender;
import io.github.hyuk.adsettlement.advertising.implement.AdvertiserReader;
import io.github.hyuk.adsettlement.advertising.persistence.Advertisement;
import io.github.hyuk.adsettlement.advertising.persistence.Advertiser;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertiserDetailResponse;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertiserRequest;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertiserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AdvertiserService {
   private final AdvertiserReader advertiserReader;
   private final AdvertiserAppender advertiserAppender;
   private final AdvertisementReader advertisementReader;

   // 광고주 등록
    @Transactional
    public AdvertiserResponse register(AdvertiserRequest request) {
        advertiserReader.validateNotExists(request.businessRegistrationNumber());

        Advertiser advertiser = Advertiser.create(
                request.advertiserName(),
                request.businessRegistrationNumber()
        );

        Advertiser savedAdvertiser = advertiserAppender.append(advertiser);

        return AdvertiserResponse.from(savedAdvertiser);
    }

    // 광고주 조회
    public AdvertiserResponse getAdvertiser(Long advertiserId) {
        Advertiser advertiser = advertiserReader.getById(advertiserId);

        return AdvertiserResponse.from(advertiser);
    }

    // 광고주로 모든 광고 포함 조회
    @Transactional(readOnly = true)
    public AdvertiserDetailResponse getAdvertiserWithAdvertisementList(Long advertiserId) {
        Advertiser advertiser = advertiserReader.getById(advertiserId);

        List<Advertisement> advertisementList = advertisementReader.getAdvertisementList(advertiser.getAdvertiserId());

        return AdvertiserDetailResponse.from(advertiser, advertisementList);
    }
}

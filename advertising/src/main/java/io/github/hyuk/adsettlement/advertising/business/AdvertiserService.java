package io.github.hyuk.adsettlement.advertising.business;

import io.github.hyuk.adsettlement.advertising.implement.AdvertiserAppender;
import io.github.hyuk.adsettlement.advertising.implement.AdvertiserReader;
import io.github.hyuk.adsettlement.advertising.persistence.Advertiser;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertiserRequest;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertiserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AdvertiserService {
   private final AdvertiserReader advertiserReader;
   private final AdvertiserAppender advertiserAppender;

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
}

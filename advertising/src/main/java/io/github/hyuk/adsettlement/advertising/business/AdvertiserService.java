package io.github.hyuk.adsettlement.advertising.business;

import io.github.hyuk.adsettlement.advertising.implement.AdvertiserAppender;
import io.github.hyuk.adsettlement.advertising.implement.AdvertiserReader;
import io.github.hyuk.adsettlement.advertising.persistence.Advertiser;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertiserRequest;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertiserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AdvertiserService {
   private final AdvertiserReader advertiserReader;
   private final AdvertiserAppender advertiserAppender;

   // 광고주 등록
    public AdvertiserResponse register(AdvertiserRequest request) {
        advertiserReader.read(request.advertiserName(), request.businessRegistrationNumber());

        Advertiser advertiser = Advertiser.create(
                request.advertiserName(),
                request.businessRegistrationNumber()
        );

        advertiserAppender.append(advertiser);

        return AdvertiserResponse.from(advertiser);
    }
}

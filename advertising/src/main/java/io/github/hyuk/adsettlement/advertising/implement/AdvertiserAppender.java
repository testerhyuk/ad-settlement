package io.github.hyuk.adsettlement.advertising.implement;

import io.github.hyuk.adsettlement.advertising.persistence.Advertiser;
import io.github.hyuk.adsettlement.advertising.persistence.AdvertiserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdvertiserAppender {
    private final AdvertiserRepository advertiserRepository;

    public Advertiser append(Advertiser advertiser) {
        return advertiserRepository.save(advertiser);
    }
}

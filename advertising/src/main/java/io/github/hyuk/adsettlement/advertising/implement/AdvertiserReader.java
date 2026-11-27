package io.github.hyuk.adsettlement.advertising.implement;

import io.github.hyuk.adsettlement.advertising.persistence.AdvertiserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdvertiserReader {
    private final AdvertiserRepository advertiserRepository;

    public void read(String advertiserName, String businessRegistrationNumber) {
        advertiserRepository
                .findByAdvertiserNameAndBusinessRegistrationNumber(
                    advertiserName,
                    businessRegistrationNumber
                ).ifPresent(ad -> {
                    throw new IllegalStateException("이미 광고주가 존재합니다");
                });
    }
}

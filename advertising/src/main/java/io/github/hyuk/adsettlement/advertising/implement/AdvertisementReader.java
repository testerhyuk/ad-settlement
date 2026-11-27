package io.github.hyuk.adsettlement.advertising.implement;

import io.github.hyuk.adsettlement.advertising.exception.DuplicateAdvertisementNameException;
import io.github.hyuk.adsettlement.advertising.persistence.AdvertisementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdvertisementReader {
    private final AdvertisementRepository advertisementRepository;

    public void validateNotExists(Long advertiserId, String advertisementName) {
        if (advertisementRepository.existsByAdvertiser_AdvertiserIdAndAdvertisementName(
                advertiserId, advertisementName
        )) {
            throw new DuplicateAdvertisementNameException("이미 등록된 광고입니다.");
        }
    }
}

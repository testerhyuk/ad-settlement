package io.github.hyuk.adsettlement.advertising.implement;

import io.github.hyuk.adsettlement.advertising.exception.AdvertisementNotFoundException;
import io.github.hyuk.adsettlement.advertising.exception.DuplicateAdvertisementNameException;
import io.github.hyuk.adsettlement.advertising.persistence.Advertisement;
import io.github.hyuk.adsettlement.advertising.persistence.AdvertisementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public Advertisement getAdvertisement(Long advertisementId) {
        return advertisementRepository.findById(advertisementId)
                .orElseThrow(() -> new AdvertisementNotFoundException("존재하지 않는 광고입니다."));
    }

    public List<Advertisement> getAdvertisementList(Long advertiserId) {
        return advertisementRepository.findAllByAdvertiser_AdvertiserId(advertiserId);
    }
}

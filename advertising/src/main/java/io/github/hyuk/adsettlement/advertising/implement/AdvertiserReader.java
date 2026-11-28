package io.github.hyuk.adsettlement.advertising.implement;

import io.github.hyuk.adsettlement.advertising.exception.AdvertiserNotFoundException;
import io.github.hyuk.adsettlement.advertising.exception.DuplicateBusinessRegistrationNumberException;
import io.github.hyuk.adsettlement.advertising.persistence.Advertiser;
import io.github.hyuk.adsettlement.advertising.persistence.AdvertiserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdvertiserReader {
    private final AdvertiserRepository advertiserRepository;

    public void validateNotExists(String businessRegistrationNumber) {
        if (advertiserRepository.existsByBusinessRegistrationNumber(businessRegistrationNumber)) {
            throw new DuplicateBusinessRegistrationNumberException("이미 등록된 사업자등록번호입니다.");
        }
    }

    public Advertiser getById(Long advertiserId) {
        return advertiserRepository.findById(advertiserId)
                .orElseThrow(() -> new AdvertiserNotFoundException("존재하지 않는 광고주입니다."));
    }

}

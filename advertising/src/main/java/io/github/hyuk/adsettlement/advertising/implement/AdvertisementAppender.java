package io.github.hyuk.adsettlement.advertising.implement;

import io.github.hyuk.adsettlement.advertising.persistence.Advertisement;
import io.github.hyuk.adsettlement.advertising.persistence.AdvertisementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdvertisementAppender {
    private final AdvertisementRepository advertisementRepository;

    public Advertisement append(Advertisement advertisement) {
        return advertisementRepository.save(advertisement);
    }
}

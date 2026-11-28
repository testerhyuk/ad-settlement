package io.github.hyuk.adsettlement.publisher.implement;

import io.github.hyuk.adsettlement.publisher.persistence.AdPlacement;
import io.github.hyuk.adsettlement.publisher.persistence.AdPlacementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdPlacementAppender {
    private final AdPlacementRepository adPlacementRepository;

    public AdPlacement append(AdPlacement adPlacement) {
        return adPlacementRepository.save(adPlacement);
    }
}

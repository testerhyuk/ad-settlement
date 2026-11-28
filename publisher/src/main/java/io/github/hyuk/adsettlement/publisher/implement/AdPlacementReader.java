package io.github.hyuk.adsettlement.publisher.implement;

import io.github.hyuk.adsettlement.publisher.exception.AdPlacementNotFoundException;
import io.github.hyuk.adsettlement.publisher.exception.DuplicatePublisherIdAndPlacementNameException;
import io.github.hyuk.adsettlement.publisher.persistence.AdPlacement;
import io.github.hyuk.adsettlement.publisher.persistence.AdPlacementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdPlacementReader {
    private final AdPlacementRepository adPlacementRepository;

    public void validateNotExists(Long publisherId, String placementName) {
        if (adPlacementRepository.existsByPublisher_PublisherIdAndPlacementName(publisherId, placementName)) {
            throw new DuplicatePublisherIdAndPlacementNameException("이미 등록된 배너 위치입니다");
        }
    }

    public AdPlacement getAdPlacement(Long adPlacementId) {
        return adPlacementRepository.findById(adPlacementId)
                .orElseThrow(() -> new AdPlacementNotFoundException("존재하지 않는 배너 번호입니다"));
    }

    public List<AdPlacement> getAdPlacementList(Long publisherId) {
        return adPlacementRepository.findAllByPublisher_PublisherId(publisherId);
    }
}

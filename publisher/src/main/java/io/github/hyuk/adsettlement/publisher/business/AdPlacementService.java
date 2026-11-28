package io.github.hyuk.adsettlement.publisher.business;

import io.github.hyuk.adsettlement.publisher.implement.AdPlacementAppender;
import io.github.hyuk.adsettlement.publisher.implement.AdPlacementReader;
import io.github.hyuk.adsettlement.publisher.implement.PublisherReader;
import io.github.hyuk.adsettlement.publisher.persistence.AdPlacement;
import io.github.hyuk.adsettlement.publisher.persistence.Publisher;
import io.github.hyuk.adsettlement.publisher.presentation.dto.AdPlacementDetailResponse;
import io.github.hyuk.adsettlement.publisher.presentation.dto.AdPlacementRequest;
import io.github.hyuk.adsettlement.publisher.presentation.dto.AdPlacementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdPlacementService {
    private final AdPlacementReader adPlacementReader;
    private final AdPlacementAppender adPlacementAppender;
    private final PublisherReader publisherReader;

    @Transactional
    public AdPlacementResponse register(AdPlacementRequest request, Long publisherId) {
        Publisher publisher = publisherReader.getById(publisherId);

        adPlacementReader.validateNotExists(publisher.getPublisherId(), request.placementName());

        AdPlacement adPlacement = AdPlacement.create(
                publisher,
                request.placementName()
        );

        AdPlacement savedAdPlacement = adPlacementAppender.append(adPlacement);

        return AdPlacementResponse.from(savedAdPlacement);
    }

    @Transactional(readOnly = true)
    public AdPlacementDetailResponse getAdPlacement(Long adPlacementId) {
        AdPlacement adPlacement = adPlacementReader.getAdPlacement(adPlacementId);

        return AdPlacementDetailResponse.from(adPlacement);
    }
}

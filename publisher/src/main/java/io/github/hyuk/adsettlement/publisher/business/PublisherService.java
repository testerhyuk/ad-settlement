package io.github.hyuk.adsettlement.publisher.business;

import io.github.hyuk.adsettlement.publisher.implement.AdPlacementReader;
import io.github.hyuk.adsettlement.publisher.implement.PublisherAppender;
import io.github.hyuk.adsettlement.publisher.implement.PublisherReader;
import io.github.hyuk.adsettlement.publisher.persistence.AdPlacement;
import io.github.hyuk.adsettlement.publisher.persistence.Publisher;
import io.github.hyuk.adsettlement.publisher.presentation.dto.PublisherDetailResponse;
import io.github.hyuk.adsettlement.publisher.presentation.dto.PublisherRequest;
import io.github.hyuk.adsettlement.publisher.presentation.dto.PublisherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublisherService {
    private final PublisherReader publisherReader;
    private final PublisherAppender publisherAppender;
    private final AdPlacementReader adPlacementReader;

    @Transactional
    public PublisherResponse register(PublisherRequest request) {
        publisherReader.validateNotExists(request.siteUrl());

        Publisher publisher = Publisher.create(
                request.publisherName(),
                request.siteUrl()
        );

        Publisher savedPublisher = publisherAppender.append(publisher);

        return PublisherResponse.from(savedPublisher);
    }

    // 퍼블리셔 조회
    @Transactional(readOnly = true)
    public PublisherResponse getPublisher(Long publisherId) {
        Publisher publisher = publisherReader.getById(publisherId);

        return PublisherResponse.from(publisher);
    }

    // 퍼블리셔를 조회하면서 모든 배너 위치 포함 조회
    @Transactional(readOnly = true)
    public PublisherDetailResponse getPublisherWithAdPlacementList(Long publisherId) {
        Publisher publisher = publisherReader.getById(publisherId);

        List<AdPlacement> adPlacementList = adPlacementReader.getAdPlacementList(publisher.getPublisherId());

        return PublisherDetailResponse.from(publisher, adPlacementList);
    }
}

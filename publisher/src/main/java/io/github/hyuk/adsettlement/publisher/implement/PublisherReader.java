package io.github.hyuk.adsettlement.publisher.implement;

import io.github.hyuk.adsettlement.publisher.exception.DuplicateSiteUrlException;
import io.github.hyuk.adsettlement.publisher.exception.PublisherIdNotFoundException;
import io.github.hyuk.adsettlement.publisher.persistence.Publisher;
import io.github.hyuk.adsettlement.publisher.persistence.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PublisherReader {
    private final PublisherRepository publisherRepository;

    public void validateNotExists(String siteUrl) {
        if (publisherRepository.existsBySiteUrl(siteUrl)) {
            throw new DuplicateSiteUrlException("이미 등록된 퍼블리셔입니다.");
        }
    }

    public Publisher getById(Long publisherId) {
        return publisherRepository.findById(publisherId)
                .orElseThrow(() -> new PublisherIdNotFoundException("존재하지 않는 퍼블리셔입니다."));
    }
}

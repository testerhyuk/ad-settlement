package io.github.hyuk.adsettlement.publisher.implement;

import io.github.hyuk.adsettlement.publisher.persistence.Publisher;
import io.github.hyuk.adsettlement.publisher.persistence.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PublisherAppender {
    private final PublisherRepository publisherRepository;

    public Publisher append(Publisher publisher) {
        return publisherRepository.save(publisher);
    }
}

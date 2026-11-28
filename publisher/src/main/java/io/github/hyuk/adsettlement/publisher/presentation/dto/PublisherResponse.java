package io.github.hyuk.adsettlement.publisher.presentation.dto;

import io.github.hyuk.adsettlement.publisher.persistence.Publisher;

import java.time.Instant;

public record PublisherResponse(
        Long publisherId,
        String publisherName,
        String siteUrl,
        Instant createdAt
) {
    public static PublisherResponse from(Publisher publisher) {
        return new PublisherResponse(
                publisher.getPublisherId(),
                publisher.getPublisherName(),
                publisher.getSiteUrl(),
                publisher.getCreatedAt()
        );
    }
}

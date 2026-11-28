package io.github.hyuk.adsettlement.publisher.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "publisher")
public class Publisher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "publisher_id")
    private Long publisherId;
    @Column(name = "publisher_name", nullable = false, length = 100)
    private String publisherName;
    @Column(name = "site_url", nullable = false, length = 255, unique = true)
    private String siteUrl;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public static Publisher create(String publisherName, String siteUrl) {
        Publisher publisher = new Publisher();

        publisher.publisherName = publisherName;
        publisher.siteUrl = siteUrl;
        publisher.createdAt = Instant.now();

        return publisher;
    }
}

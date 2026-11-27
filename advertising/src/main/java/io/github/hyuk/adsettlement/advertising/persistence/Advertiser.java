package io.github.hyuk.adsettlement.advertising.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "advertiser")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Advertiser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "advertiser_id")
    private Long advertiserId;
    @Column(name = "advertiser_name", nullable = false, length = 100)
    private String advertiserName;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public static Advertiser create(String advertiserName) {
        Advertiser advertiser = new Advertiser();

        advertiser.advertiserName = advertiserName;
        advertiser.createdAt = Instant.now();

        return advertiser;
    }
}

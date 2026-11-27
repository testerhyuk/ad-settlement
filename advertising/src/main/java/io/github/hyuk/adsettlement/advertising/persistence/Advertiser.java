package io.github.hyuk.adsettlement.advertising.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "advertiser")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Advertiser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "advertiser_id")
    private Long advertiserId;
    @Column(name = "advertiser_name", nullable = false, length = 100)
    private String advertiserName;
    @Column(
            name = "business_registration_number",
            nullable = false,
            unique = true,
            length = 10
    )
    private String businessRegistrationNumber;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public static Advertiser create(String advertiserName, String businessRegistrationNumber) {
        Advertiser advertiser = new Advertiser();

        advertiser.advertiserName = advertiserName;
        advertiser.businessRegistrationNumber = businessRegistrationNumber;
        advertiser.createdAt = Instant.now();

        return advertiser;
    }
}

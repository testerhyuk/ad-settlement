package io.github.hyuk.adsettlement.publisher.persistence;

import io.github.hyuk.adsettlement.publisher.persistence.enums.PlacementStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "ad_placement")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdPlacement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "placement_id")
    private Long placementId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id", nullable = false)
    private Publisher publisher;
    @Column(name = "placement_name", length = 100, nullable = false)
    private String placementName;
    @Column(name = "status", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private PlacementStatus status;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public static AdPlacement create(
            Publisher publisher,
            String placementName
    ) {
        AdPlacement adPlacement = new AdPlacement();

        adPlacement.publisher = publisher;
        adPlacement.placementName = placementName;
        adPlacement.status = PlacementStatus.ACTIVE;
        adPlacement.createdAt = Instant.now();

        return adPlacement;
    }
}

package io.github.hyuk.adsettlement.publisher.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdPlacementRepository extends JpaRepository<AdPlacement, Long> {
    boolean existsByPublisher_PublisherIdAndPlacementName(Long publisherId, String placementName);

    List<AdPlacement> findAllByPublisher_PublisherId(Long publisherId);
}

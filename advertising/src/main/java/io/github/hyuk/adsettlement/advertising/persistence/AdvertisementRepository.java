package io.github.hyuk.adsettlement.advertising.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
    boolean existsByAdvertiser_AdvertiserIdAndAdvertisementName(
            Long advertiserId,
            String advertisementName
    );

    List<Advertisement> findAllByAdvertiser_AdvertiserId(Long advertiserId);
}

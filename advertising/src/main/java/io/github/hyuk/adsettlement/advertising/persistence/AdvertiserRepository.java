package io.github.hyuk.adsettlement.advertising.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdvertiserRepository extends JpaRepository<Advertiser, Long> {
    boolean existsByBusinessRegistrationNumber(String businessRegistrationNumber);
}

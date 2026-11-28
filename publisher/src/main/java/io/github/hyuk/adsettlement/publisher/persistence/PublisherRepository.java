package io.github.hyuk.adsettlement.publisher.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    boolean existsBySiteUrl(String siteUrl);
}

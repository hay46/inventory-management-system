package com.injibara.iams.backend.repository;

import com.injibara.iams.backend.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
    boolean existsByName(String name);
}

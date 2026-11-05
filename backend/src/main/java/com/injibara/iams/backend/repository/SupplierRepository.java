package com.injibara.iams.backend.repository;

import com.injibara.iams.backend.domain.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}

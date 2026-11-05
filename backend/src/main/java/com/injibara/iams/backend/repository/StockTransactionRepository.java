package com.injibara.iams.backend.repository;

import com.injibara.iams.backend.domain.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {
    List<StockTransaction> findByItem_IdOrderByTransactionDateDesc(Long itemId);
    List<StockTransaction> findAllByOrderByTransactionDateDesc();
}

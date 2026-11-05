package com.injibara.iams.backend.repository;

import com.injibara.iams.backend.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByItemCode(String itemCode);
    boolean existsByItemCode(String itemCode);

    @Query("SELECT i FROM Item i WHERE " +
           "(:q IS NULL OR LOWER(i.itemName) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(i.itemCode) LIKE LOWER(CONCAT('%',:q,'%'))) " +
           "AND (:categoryId IS NULL OR i.category.id = :categoryId) " +
           "AND (:locationId IS NULL OR i.location.id = :locationId)")
    List<Item> search(@Param("q") String q,
                      @Param("categoryId") Long categoryId,
                      @Param("locationId") Long locationId);

    List<Item> findByQuantityLessThanEqual(Integer threshold);
}

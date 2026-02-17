package com.aprimore.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aprimore.models.Blade;
import com.aprimore.models.Item;

public interface ItemRepository extends JpaRepository<Item, UUID> {

    @Query("""
            SELECT b
            FROM Blade b
            WHERE b.business.id = :businessId
            ORDER BY b.name
            """)
    List<Blade> findBladesByBusinessId(@Param("businessId") UUID businessId);

    @Query("""
            SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END
            FROM Blade b
            WHERE b.business.id = :businessId
              AND LOWER(b.name) = LOWER(:name)
            """)
    boolean existsBladeByBusinessIdAndName(@Param("businessId") UUID businessId, @Param("name") String name);
}

package com.aprimore.repositories;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aprimore.models.ServiceOrder;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long>{

	Page<ServiceOrder> findByClientIdOrderByCreatedAtDesc(UUID clientId, Pageable pageable);

	Page<ServiceOrder> findAllByClientBusinessIdOrderByStatusDescEntryDateDesc(Pageable pageable, UUID businessId);

	@Query("""
			SELECT so
			FROM ServiceOrder so
			JOIN so.client c
			WHERE c.business.id = :businessId
			  AND (
			  	:search IS NULL
			  	OR lower(c.clientName) LIKE lower(concat('%', :search, '%'))
			  	OR (:serviceOrderId IS NOT NULL AND so.id = :serviceOrderId)
			  )
			  AND (:startDate IS NULL OR so.entryDate >= :startDate)
			  AND (:endDate IS NULL OR so.entryDate <= :endDate)
			ORDER BY so.status DESC, so.entryDate DESC
			""")
	Page<ServiceOrder> findAllByBusinessWithFilters(
			@Param("businessId") UUID businessId,
			@Param("search") String search,
			@Param("serviceOrderId") Long serviceOrderId,
			@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate,
			Pageable pageable);

}

package com.aprimore.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aprimore.models.ServiceOrder;
import com.aprimore.models.enuns.ServiceOrderStatus;

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

	long countByClientBusinessIdAndStatus(UUID businessId, ServiceOrderStatus status);

	long countByClientBusinessIdAndStatusAndDeliveryDateBefore(
			UUID businessId,
			ServiceOrderStatus status,
			LocalDate date);

	@Query("""
			SELECT COALESCE(MAX(so.pcpSequence), 0)
			FROM ServiceOrder so
			JOIN so.client c
			WHERE c.business.id = :businessId
			  AND so.status = :status
			""")
	Integer findMaxPcpSequenceByBusinessAndStatus(
			@Param("businessId") UUID businessId,
			@Param("status") ServiceOrderStatus status);

	@Query("""
			SELECT so
			FROM ServiceOrder so
			JOIN so.client c
			WHERE c.business.id = :businessId
			  AND so.status = :status
			ORDER BY
			  CASE WHEN so.pcpSequence IS NULL THEN 1 ELSE 0 END,
			  so.pcpSequence ASC,
			  CASE WHEN so.deliveryDate IS NULL THEN 1 ELSE 0 END,
			  so.deliveryDate ASC,
			  so.createdAt ASC
			""")
	List<ServiceOrder> findPriorityByBusinessAndStatus(
			@Param("businessId") UUID businessId,
			@Param("status") ServiceOrderStatus status);

	@Query("""
			SELECT so
			FROM ServiceOrder so
			JOIN so.client c
			WHERE c.business.id = :businessId
			  AND so.status = :status
			  AND so.id IN :serviceOrderIds
			""")
	List<ServiceOrder> findByBusinessAndStatusAndIdIn(
			@Param("businessId") UUID businessId,
			@Param("status") ServiceOrderStatus status,
			@Param("serviceOrderIds") List<Long> serviceOrderIds);

}

package com.aprimore.repositories;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.aprimore.models.ServiceOrder;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long>{

	Page<ServiceOrder> findByClientIdOrderByCreatedAtDesc(UUID clientId, Pageable pageable);

}

package com.aprimore.repositories;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.aprimore.models.Client;

public interface ClientRepository extends JpaRepository<Client, UUID>{

	Page<Client> findByBusinessIdOrderByClientName(UUID businessId, Pageable pageable);
	
	Page<Client> findByBusinessIdAndClientNameContainingIgnoreCaseOrderByClientName(
			UUID businessId,
			String clientName,
			Pageable pageable
	);

}

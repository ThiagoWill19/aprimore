package com.aprimore.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.aprimore.models.Client;

public interface ClientRepository extends JpaRepository<Client, UUID>{

	Page<Client> findByBusinessIdOrderByClientName(UUID businessId, Pageable pageable);

	List<Client> findByBusinessIdOrderByClientName(UUID businessId);
	
	Page<Client> findByBusinessIdAndClientNameContainingIgnoreCaseOrderByClientName(
			UUID businessId,
			String clientName,
			Pageable pageable
	);
	
	boolean existsByBusinessIdAndClientEmail(UUID businessId, String email);
	boolean existsByBusinessIdAndCnpj(UUID businessId, String cnpj);


}

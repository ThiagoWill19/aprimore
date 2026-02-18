package com.aprimore.repositories;

import java.util.UUID;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.aprimore.models.Machine;

public interface MachineRepository extends JpaRepository<Machine, UUID> {
	
	Page<Machine> findByClientIdOrderByActiveDescNameAsc(UUID clientId, Pageable pageable);
	
	List<Machine> findByClientIdOrderByActiveDescNameAsc(UUID clientId);

}

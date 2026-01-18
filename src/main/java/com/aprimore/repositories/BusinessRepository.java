package com.aprimore.repositories;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aprimore.models.Business;

public interface BusinessRepository extends JpaRepository<Business, UUID>{
	
	Page<Business> findAllByOrderByName(Pageable pageable);
	
	Page<Business> findByCnpjContaining(String cnpj, Pageable pageable);

	
	@Query("""
		    SELECT b FROM Business b
		    WHERE
		        LOWER(b.name) LIKE %:term%
		        OR LOWER(b.tradeName) LIKE %:term%
		        OR b.cnpj LIKE %:term%
		""")
	Page<Business> findByNameOrTradeName(@Param("term") String term, Pageable pageable);
}

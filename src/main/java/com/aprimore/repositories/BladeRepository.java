package com.aprimore.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aprimore.models.Blade;

public interface BladeRepository extends JpaRepository<Blade, UUID>{

	
}

package com.aprimore.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aprimore.models.Business;

public interface BusinessRepository extends JpaRepository<Business, UUID>{

}

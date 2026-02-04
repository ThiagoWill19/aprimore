package com.aprimore.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aprimore.models.Address;

public interface AddressRepository extends JpaRepository<Address, UUID> {

}

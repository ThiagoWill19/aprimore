package com.aprimore.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aprimore.models.Client;

public interface ClientRepository extends JpaRepository<Client, UUID>{

}

package com.aprimore.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aprimore.models.Machine;

public interface MachineRepository extends JpaRepository<Machine, UUID> {

}

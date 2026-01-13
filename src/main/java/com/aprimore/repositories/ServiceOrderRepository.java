package com.aprimore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aprimore.models.ServiceOrder;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long>{

}

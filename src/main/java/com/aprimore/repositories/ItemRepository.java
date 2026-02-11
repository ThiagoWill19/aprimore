package com.aprimore.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aprimore.models.Item;

public interface ItemRepository extends JpaRepository<Item, UUID> {

	
}

package com.aprimore.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aprimore.models.User;

public interface UserRepository extends JpaRepository<User ,UUID>{

	Optional<User> findByEmail(String email);
	
	List<User> findAllByBusinessIdOrderByName(UUID id);
}

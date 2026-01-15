package com.aprimore.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import com.aprimore.models.User;

public interface UserRepository extends JpaRepository<User ,UUID>{

}

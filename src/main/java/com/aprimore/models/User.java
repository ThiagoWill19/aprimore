package com.aprimore.models;

import java.util.UUID;

import com.aprimore.models.enuns.AccountStatus;
import com.aprimore.models.enuns.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	private String name;
	
	private String password;
	
	private String email;
	
	@Enumerated(EnumType.STRING)
	private AccountStatus accountStatus;
	
	@Enumerated(EnumType.STRING)
	private Role role;
	
	@ManyToOne
	private Business business;
}

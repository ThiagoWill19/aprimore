package com.aprimore.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Business {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	private String name;
	
	@OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Client> clients = new ArrayList<>();
	
	@OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<User> users = new ArrayList<>();
}

package com.aprimore.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.aprimore.models.enuns.AccountStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
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
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String tradeName;
	
	@Column(nullable = false, unique = true)
	private String cnpj;
	
	@Column(nullable = false, unique = true)
	private String businessEmail;
	
	@Column(nullable = false)
	private String phone;
	
	@Enumerated(EnumType.STRING)
	private AccountStatus accountStatus;
	
	@Column(nullable = false, updatable = false)
	private LocalDate createdAt;
	
	
	@OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Client> clients = new ArrayList<>();
	
	@OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<User> users = new ArrayList<>();

	@OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Blade> blades = new ArrayList<>();
	
	@PrePersist
	private void prePersist() {
	    this.createdAt = LocalDate.now();
	    this.accountStatus = AccountStatus.ACTIVE;
	}
}

package com.aprimore.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
		name = "client",
		uniqueConstraints = 
		{
			@UniqueConstraint(columnNames = {"business_id", "email"}),
			@UniqueConstraint(columnNames = {"business_id", "cnpj"})
		})
public class Client {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@Column(nullable = false)
	private String cnpj;
	
	@Column(name="client_name", nullable = false)
	private String clientName;
	

	@Column(name="email", nullable = false)
	private String clientEmail;
	
	@Column(name="phone_number")
	private String clientPhoneNumber;
	
	@Column(nullable = false)
	private boolean active;
	
	@Column(nullable = false, updatable = false)
	private LocalDate createdAt;
	
	@Column(name="standard_order_instruction", length = 500)
	@Size(max = 500)
	private String standardOrderInstructions;
	
	@ManyToOne
	private Business business;
	
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private Address address;
	
	@OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Machine> machines = new ArrayList<>();
	
	@OneToMany(mappedBy = "client")
	private List<ServiceOrder> serviceOrders = new ArrayList<>();
	
	@PrePersist
	private void prePersist() {
	    this.createdAt = LocalDate.now();
	    this.active = true;
	}
	
}

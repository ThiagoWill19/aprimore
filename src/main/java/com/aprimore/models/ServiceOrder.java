package com.aprimore.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.aprimore.models.enuns.ServiceOrderStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
public class ServiceOrder {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String workName;
	
	private String reference;
	
	private LocalDate entryDate;
	
	private LocalDate deliveryDate;
	
	private LocalDateTime createdAt;
	
	private String type;
	
	private String internalMeasure;
	
	@ManyToOne
	private Machine machine;
	
	@ManyToOne
	private Client client;
	
	private String arrangement;
	
	private String typeOfWave;
	
	private String servicesToBePerformed;
	
	private String obs;

	@Enumerated(EnumType.STRING)
	private ServiceOrderStatus status;
	
	@ManyToMany
	@JoinTable(
			name = "service_order_blade",
			joinColumns = @JoinColumn(name = "service_order_id"),
			inverseJoinColumns = @JoinColumn(name = "blade_id")
	)
	private List<Blade> blades = new ArrayList<>();

}

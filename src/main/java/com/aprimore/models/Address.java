package com.aprimore.models;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@Column(nullable = false)
	private String street;
	
	@Column(nullable = false)
	private String number;
	
	@Column(nullable = false)
	private String neighborhood;
	
	@Column(nullable = false)
	private String city;
	
	@Column(nullable = false)
	private String state;
	
	private String zipCode;
	
	public String toString() {
		return this.street + " ," +
				this.number + " ," +
				this.neighborhood + " - " +
				this.city + " " +
				this.state + " " +
				this.zipCode;
	}
}

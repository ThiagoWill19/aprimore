package com.aprimore.models.dtos;

import java.util.UUID;
import com.aprimore.models.enuns.Role;
import com.aprimore.models.Business;
import com.aprimore.models.enuns.AccountStatus;

import lombok.Data;

@Data
public class UserDto {

	private UUID id;

	private String name;
	
	private String email;

	private AccountStatus accountStatus;

	private Role role;
	
	private Business business;
}

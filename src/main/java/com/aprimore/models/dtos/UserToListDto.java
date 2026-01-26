package com.aprimore.models.dtos;

import java.util.UUID;

import com.aprimore.models.enuns.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserToListDto {
	
	private UUID id;
	
	private String name;
	
	private String email;
	
	private Role role;

}

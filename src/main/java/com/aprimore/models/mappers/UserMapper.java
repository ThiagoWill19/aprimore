package com.aprimore.models.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.aprimore.models.User;
import com.aprimore.models.dtos.UserDto;
import com.aprimore.models.dtos.UserToListDto;

@Component
public class UserMapper {

	private ModelMapper modelMapper = new ModelMapper();
	
	public UserToListDto mapToUserToListDto(User user) {
		return modelMapper.map(user, UserToListDto.class);
	}
	
	public UserDto mapToUserDto(User user) {
		return modelMapper.map(user, UserDto.class);
	}
}

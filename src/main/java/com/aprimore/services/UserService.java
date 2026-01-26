package com.aprimore.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aprimore.models.User;
import com.aprimore.models.dtos.UserToListDto;
import com.aprimore.models.mappers.UserMapper;
import com.aprimore.repositories.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserMapper userMapper;
	
	public void newUser(User newUser) {
		userRepository.save(newUser);
	}
	
	public List<UserToListDto> findAllByBusiness(UUID businessId) {

		List<User> users = userRepository.findAllByBusinessIdOrderByName(businessId);
		return users.stream()
                .map(userMapper::mapToUserToListDto)
                .toList();
	}

}

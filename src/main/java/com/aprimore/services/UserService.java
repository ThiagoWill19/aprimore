package com.aprimore.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aprimore.models.User;
import com.aprimore.repositories.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	public void newUser(User newUser) {
		userRepository.save(newUser);
	}

}

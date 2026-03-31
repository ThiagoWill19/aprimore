package com.aprimore.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aprimore.exceptions.DomainRuleException;
import com.aprimore.exceptions.ResourceNotFoundException;
import com.aprimore.models.User;
import com.aprimore.models.dtos.UserDto;
import com.aprimore.models.dtos.UserToListDto;
import com.aprimore.models.mappers.UserMapper;
import com.aprimore.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	PasswordEncoder passwordEncoder;	
	
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

	public UserDto findUserById(UUID userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
		
		return userMapper.mapToUserDto(user);
	}

	public void alterPassword(UUID userId, String oldPassword, String newPassword) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
	
		if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
			throw new DomainRuleException("Senha atual incorreta");
		}

		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}

}

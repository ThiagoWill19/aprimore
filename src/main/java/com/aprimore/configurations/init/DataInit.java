package com.aprimore.configurations.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.aprimore.models.User;
import com.aprimore.models.enuns.Role;
import com.aprimore.repositories.UserRepository;

@Configuration
public class DataInit {

	@Bean
	public CommandLineRunner init(UserRepository userRepository) {
		return args -> {
			//Verifica se já existe um admin
			if(userRepository.findByEmail("admin@email.com").isEmpty()) {
				User user = new User();
				user.setName("Thiago");
				user.setEmail("admin@email.com");
				user.setPassword(new BCryptPasswordEncoder().encode("admin123"));
				user.setRole(Role.ADMIN);
				userRepository.save(user);
				System.out.println("✅ admin criado com sucesso.");
			}else {
				System.out.println("ℹ️ Admin já existe.");
			}
		};
	}
}

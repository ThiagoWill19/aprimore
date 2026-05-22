package com.aprimore.configurations.init;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aprimore.models.User;
import com.aprimore.models.enuns.AccountStatus;
import com.aprimore.models.enuns.Role;
import com.aprimore.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
public class DataInit {

	private final PasswordEncoder passwordEncoder;
    
	@Value("${main.email}")
	private String mainEmail;
	@Value("${main.pass}")
	private String mainPass;
	@Value("${main.name}")
	private String mainName;

    DataInit(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

	@Bean
	public CommandLineRunner init(UserRepository userRepository) {
		return args -> {
			//Verifica se já existe um admin
			if(userRepository.findByEmail(mainEmail).isEmpty()) {
				User user = new User();
				user.setName(mainName);
				user.setEmail(mainEmail);
				user.setPassword(passwordEncoder.encode(mainPass));
				user.setRole(Role.ADMIN);
				user.setAccountStatus(AccountStatus.ACTIVE);
				userRepository.save(user);
				log.info("✅ admin criado com sucesso.");
				log.info("Email: " + user.getEmail());
			}else {
				log.info("ℹ️ Admin já existe.");
			}
		};
	}
}

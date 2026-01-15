package com.aprimore;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PassGen {

	public static void main(String[] args) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String senha = "admin123";
        System.out.println(encoder.encode(senha));
	}
}

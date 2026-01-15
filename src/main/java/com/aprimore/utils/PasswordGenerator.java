package com.aprimore.utils;

import java.security.SecureRandom;

public class PasswordGenerator {

	private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static final int TAMANHO = 6;
	private static final SecureRandom random = new SecureRandom();

	public static String gerarSenha() {
		StringBuilder sb = new StringBuilder(TAMANHO);
		for (int i = 0; i < TAMANHO; i++) {
			sb.append(CARACTERES.charAt(random.nextInt(CARACTERES.length())));
		}
		return sb.toString();
	}
}

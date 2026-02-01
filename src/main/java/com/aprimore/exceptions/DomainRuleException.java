package com.aprimore.exceptions;

public class DomainRuleException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DomainRuleException(String message) {
		super(message);
	}
}

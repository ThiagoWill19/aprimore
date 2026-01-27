package com.aprimore.events;

import com.aprimore.models.User;

public class BusinessCreatedEvent {

	private final User user;
	private final String rawPassword;
	
	public BusinessCreatedEvent(User user, String rawPassword) {
		this.user = user;
		this.rawPassword = rawPassword;
	}
	
	public User getUser() {
		return user;
	}
	
	public String getRawPassword() {
		return rawPassword;
	}
}

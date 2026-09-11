package com.aprimore.events;

public class BusinessCreatedEvent {

    private final String email;
    private final String activationLink;

    public BusinessCreatedEvent(String email, String activationLink) {
        this.email = email;
        this.activationLink = activationLink;
    }

    public String getEmail() {
        return email;
    }

    public String getActivationLink() {
        return activationLink;
    }
}

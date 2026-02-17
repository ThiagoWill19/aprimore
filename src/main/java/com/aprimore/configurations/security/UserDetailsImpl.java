package com.aprimore.configurations.security;

import java.util.Collection;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.aprimore.models.User;
import com.aprimore.models.enuns.AccountStatus;

public class UserDetailsImpl implements UserDetails {
	private static final long serialVersionUID = 1L;
	
	private final User user;
	
	public UserDetailsImpl(User user) {
		this.user = user;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
	}

	@Override
	public @Nullable String getPassword() {
		// TODO Auto-generated method stub
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return user.getEmail();
	}
	
	public User getUser() {
		return user;
	}
	
	@Override
	public boolean isEnabled() {
	    if (user.getAccountStatus() != AccountStatus.ACTIVE) {
	        return false;
	    }

	    // Admins seeded for local/test may not be linked to a business.
	    if (user.getBusiness() == null) {
	        return true;
	    }

	    return user.getBusiness().getAccountStatus() == AccountStatus.ACTIVE;
	}

	@Override
	public boolean isAccountNonLocked() {
	    return user.getAccountStatus() != AccountStatus.INACTIVE;
	}

	@Override
	public boolean isAccountNonExpired() {
	    return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
	    return true;
	}

}

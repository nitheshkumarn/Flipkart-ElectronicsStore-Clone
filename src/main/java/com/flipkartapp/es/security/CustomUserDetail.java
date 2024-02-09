package com.flipkartapp.es.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.flipkartapp.es.Exception.UserNotFoundException;
import com.flipkartapp.es.entity.User;

public class CustomUserDetail implements UserDetails{

	/**
	 * 
	 */
	private static final long serialVersionUID = 532035409830167106L;
	
	private User user;

	public CustomUserDetail(User user) {
		if(user.isDeleted() == true) {
			throw new UserNotFoundException("user not found");
		}
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singleton(new SimpleGrantedAuthority(user.getUserRole().name()));
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUserName();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}

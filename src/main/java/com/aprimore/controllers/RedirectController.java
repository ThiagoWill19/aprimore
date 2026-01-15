package com.aprimore.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.aprimore.configurations.security.UserDetailsImpl;
import com.aprimore.models.User;
import com.aprimore.models.enuns.Role;

@Controller
public class RedirectController {

	@GetMapping("/login")
	public String login() {
		return "login"; // → login.html
	}
	
	@GetMapping("/redirect")
public String redirecionar(Authentication auth) {
        
		UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
	    User user = userDetails.getUser(); // ✅ acesso correto

        if (user.getRole() == Role.ADMIN) {
            return "redirect:/admin";
        } else {
            return "redirect:/user";
        }
    }
}

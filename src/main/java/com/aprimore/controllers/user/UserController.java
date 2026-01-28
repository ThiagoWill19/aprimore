package com.aprimore.controllers.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.aprimore.configurations.security.UserDetailsImpl;

@Controller
@RequestMapping("/user")
public class UserController {

	@GetMapping
	public String inicialPage(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		
		model.addAttribute("username", userDetails.getUser().getName());
		model.addAttribute("businessName", userDetails.getUser().getBusiness().getTradeName());
		
		return "/user/UserInitialPage";
	}
}

package com.aprimore.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.aprimore.configurations.security.UserDetailsImpl;
import com.aprimore.models.dtos.NewBusinessDto;
import com.aprimore.services.BusinessService;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private BusinessService businessService;

	@GetMapping
	public String inicialPage(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		
		model.addAttribute("username", userDetails.getUser().getName());
		
		return "/admin/AdminInicialPage";
	}
	
	@PostMapping("/create-new-business")
	public String createNewBusiness(Model model, NewBusinessDto newBusinessDto) {
		businessService.newBusiness(newBusinessDto);
		return "redirect:/admin";
	}
	
}

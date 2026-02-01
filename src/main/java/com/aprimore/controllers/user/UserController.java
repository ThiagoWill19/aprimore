package com.aprimore.controllers.user;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aprimore.configurations.security.UserDetailsImpl;
import com.aprimore.exceptions.DomainRuleException;
import com.aprimore.models.Client;
import com.aprimore.models.dtos.ClientDetailsDto;
import com.aprimore.models.dtos.NewClientDto;
import com.aprimore.services.ClientService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private ClientService clientService;

	@GetMapping
	public String inicialPage(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		
		model.addAttribute("username", userDetails.getUser().getName());
		model.addAttribute("businessName", userDetails.getUser().getBusiness().getTradeName());
		
		return "/user/UserInitialPage";
	}
	
	@PostMapping("/new-client")
	public String createNewClient(
			@Valid NewClientDto newClientDto,
			BindingResult result,
			RedirectAttributes redirectAttributes,
			@AuthenticationPrincipal UserDetailsImpl userDetails
			 ) {
		
		if(result.hasErrors()) {
			redirectAttributes.addFlashAttribute("erro",result.getFieldError().getDefaultMessage());
			return "redirect:/user";
		}
		
		try {
			
			Client client = clientService.newClient(newClientDto, userDetails.getUser().getBusiness().getId());
			
			return "redirect:/user/client/" + client.getId();
			
		} catch (DomainRuleException e) {
			
			redirectAttributes.addFlashAttribute("erro",e.getMessage());
			return "redirect:/user";
		}
	}
	
	@GetMapping("/client/{id}")
	public String findClientById(Model model,
			@PathVariable UUID id,
			RedirectAttributes redirectAttributes,
			@AuthenticationPrincipal UserDetailsImpl userDetails) {
		
		try {
			
			ClientDetailsDto clientDatailsDto = clientService.findById(id, userDetails.getUser());
			model.addAttribute("client",clientDatailsDto);
			return "/user/ClientDetailsPage";
			
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("erro", e.getMessage());
			return "redirect:/user";
		}
		
	}
	
	@PostMapping("/client/clientUpdate")
	public String updateClient(
			
			@Valid ClientDetailsDto clientDetailsDto,
			BindingResult result,
			RedirectAttributes redirectAttributes,
			@AuthenticationPrincipal UserDetailsImpl userDetails,
			Model model) {
		
		if(result.hasErrors()) {
			model.addAttribute("client", clientDetailsDto);
			return "/user/ClientDetailsPage";
		}
		
		try {
			
			clientService.updateClient(clientDetailsDto, userDetails.getUser());
			return "redirect:/user/client/" + clientDetailsDto.getId();
			
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("erro", e.getMessage());
			return "redirect:/user/client/" + clientDetailsDto.getId();
		}
		
	}
}

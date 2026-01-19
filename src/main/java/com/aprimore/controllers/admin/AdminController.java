package com.aprimore.controllers.admin;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aprimore.configurations.security.UserDetailsImpl;
import com.aprimore.exceptions.BusinessRuleException;
import com.aprimore.models.dtos.BusinessListDto;
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
	public String createNewBusiness(Model model, NewBusinessDto newBusinessDto, RedirectAttributes redirectAttributes) {
		
		try {
			
			businessService.newBusiness(newBusinessDto);
			return "redirect:/admin";
			
		} catch (BusinessRuleException e) {
			
			redirectAttributes.addFlashAttribute("erro",e.getMessage());
			return "redirect:/admin/business-list";
		}
		
	}
	
	
	@GetMapping("/business-list")
	public String findAllBusiness(Model model,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "15") int size,
			@RequestParam(defaultValue = "") String search) {
		
		Page<BusinessListDto> businessPage = businessService.findAllByOrderByName(page, size, search);
		model.addAttribute("businessPage", businessPage.getContent());
		model.addAttribute("atualPage", businessPage.getNumber());
		model.addAttribute("totalPages", businessPage.getTotalPages());
		model.addAttribute("search",search);
		
		return "/admin/businessListPage";
	}
	
	@GetMapping("/business/{id}")
	public String findBusinessById(Model model,@PathVariable UUID id, RedirectAttributes redirectAttributes) {
		
		try {
			model.addAttribute("business", businessService.findById(id));
			return "/admin/businessDetailsPage";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("erro",e.getMessage());
			return "redirect:/admin/business-list";
		}
		
	}
	
}

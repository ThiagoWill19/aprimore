package com.aprimore.controllers.admin;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aprimore.configurations.security.UserDetailsImpl;
import com.aprimore.exceptions.BusinessRuleException;
import com.aprimore.exceptions.ResourceNotFoundException;
import com.aprimore.models.dtos.BusinessDetailsDto;
import com.aprimore.models.dtos.BusinessListDto;
import com.aprimore.models.dtos.NewBusinessDto;
import com.aprimore.services.BusinessService;
import com.aprimore.services.UserService;

import jakarta.validation.Valid;


@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private BusinessService businessService;
	
	@Autowired
	private UserService userService;

	@GetMapping
	public String inicialPage(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		
		model.addAttribute("username", userDetails.getUser().getName());
		
		return "/admin/AdminInicialPage";
	}
	
	
	@PostMapping("/create-new-business")
	public String createNewBusiness(
			@Valid NewBusinessDto newBusinessDto,
			BindingResult result,
			RedirectAttributes redirectAttributes) {
		
		if(result.hasErrors()) {
			redirectAttributes.addFlashAttribute("erro",result.getFieldError().getDefaultMessage());
			return "redirect:/admin/business-list/";
		}
		
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
		model.addAttribute("businessList", businessPage.getContent());
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
		} catch (ResourceNotFoundException e) {
			redirectAttributes.addFlashAttribute("erro",e.getMessage());
			return "redirect:/admin/business-list";
		}
		
	}
	
	
	@PostMapping("/business/update")
	public String updateBusiness(Model model,
			@Valid BusinessDetailsDto businessDetailsDto,
			BindingResult result,
			RedirectAttributes redirectAttributes) {
		
		if(result.hasErrors()) {
			redirectAttributes.addFlashAttribute("erro",result.getFieldError().getDefaultMessage());
			return "redirect:/admin/business/" + businessDetailsDto.getId();
		}
			
		try {
			businessService.updateBusiness(businessDetailsDto);
			return "redirect:/admin/business/" + businessDetailsDto.getId();
		} catch (BusinessRuleException e) {
			redirectAttributes.addFlashAttribute("erro",e.getMessage());
			return "redirect:/admin/business/" + businessDetailsDto.getId();
		}
		
	}
	
	@PostMapping("/business/accountStatus")
	public String changeBusinessStatus(@RequestParam UUID id) {
		
		businessService.changeBusinessStatus(id);
		
		return "redirect:/admin/business/" + id;
	}
	
	
	@GetMapping("/business/{id}/users")
	public String findUsers(Model model, @PathVariable UUID id, RedirectAttributes redirectAttributes) {
		
		try {
			BusinessDetailsDto business = businessService.findById(id);
			model.addAttribute("businessName", business.getName());
			model.addAttribute("users", userService.findAllByBusiness(id));
			
			return "/admin/usersPage";
			
		} catch (ResourceNotFoundException e) {
			redirectAttributes.addFlashAttribute("erro",e.getMessage());
			return "redirect:/admin/business-list";
		}
		
	}
	
	
}

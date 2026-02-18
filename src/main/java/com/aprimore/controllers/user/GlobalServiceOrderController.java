package com.aprimore.controllers.user;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aprimore.configurations.security.UserDetailsImpl;
import com.aprimore.models.dtos.SelectServiceOrderClientDto;
import com.aprimore.services.ServiceOrderService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/service-orders")
public class GlobalServiceOrderController {

	@Autowired
	private ServiceOrderService serviceOrderService;

	@GetMapping("/new")
	public String newServiceOrderStart(
			Model model,
			@AuthenticationPrincipal UserDetailsImpl userDetails,
			RedirectAttributes redirectAttributes) {

		try {
			model.addAttribute("selection", new SelectServiceOrderClientDto());
			model.addAttribute("clients", serviceOrderService.findClientsByBusiness(userDetails.getUser()));
			return "/user/NewServiceOrderStartPage";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("erro", e.getMessage());
			return "redirect:/user";
		}
	}

	@PostMapping("/new/select-client")
	public String selectClientForServiceOrder(
			@Valid SelectServiceOrderClientDto selection,
			BindingResult result,
			@AuthenticationPrincipal UserDetailsImpl userDetails,
			Model model,
			RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {
			model.addAttribute("selection", selection);
			model.addAttribute("clients", serviceOrderService.findClientsByBusiness(userDetails.getUser()));
			return "/user/NewServiceOrderStartPage";
		}

		UUID clientId = selection.getClientId();
		redirectAttributes.addFlashAttribute("success", "Cliente selecionado. Complete os dados da OS.");
		return "redirect:/user/client/" + clientId + "/service-orders/new";
	}
}

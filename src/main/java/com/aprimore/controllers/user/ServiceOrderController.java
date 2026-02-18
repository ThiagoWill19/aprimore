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
import com.aprimore.models.dtos.NewServiceOrderDto;
import com.aprimore.services.ServiceOrderService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/client/{clientId}/service-orders")
public class ServiceOrderController {

	@Autowired
	private ServiceOrderService serviceOrderService;

	@GetMapping("/new")
	public String newServiceOrderForm(
			@PathVariable UUID clientId,
			Model model,
			@AuthenticationPrincipal UserDetailsImpl userDetails,
			RedirectAttributes redirectAttributes) {

		try {
			model.addAttribute("clientId", clientId);
			model.addAttribute("serviceOrder", new NewServiceOrderDto());
			model.addAttribute("machines", serviceOrderService.findMachinesByClient(clientId, userDetails.getUser()));
			model.addAttribute("blades", serviceOrderService.findBladesByBusiness(userDetails.getUser()));
			return "/user/NewServiceOrderPage";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("erro", e.getMessage());
			return "redirect:/user/client/" + clientId;
		}
	}

	@PostMapping
	public String createServiceOrder(
			@PathVariable UUID clientId,
			@Valid NewServiceOrderDto newServiceOrderDto,
			BindingResult result,
			@AuthenticationPrincipal UserDetailsImpl userDetails,
			RedirectAttributes redirectAttributes,
			Model model) {

		if (result.hasErrors()) {
			model.addAttribute("clientId", clientId);
			model.addAttribute("serviceOrder", newServiceOrderDto);
			model.addAttribute("machines", serviceOrderService.findMachinesByClient(clientId, userDetails.getUser()));
			model.addAttribute("blades", serviceOrderService.findBladesByBusiness(userDetails.getUser()));
			return "/user/NewServiceOrderPage";
		}

		try {
			serviceOrderService.createServiceOrder(clientId, newServiceOrderDto, userDetails.getUser());
			redirectAttributes.addFlashAttribute("success", "Ordem de servico cadastrada com sucesso!");
			return "redirect:/user/client/" + clientId;
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("erro", e.getMessage());
			return "redirect:/user/client/" + clientId + "/service-orders/new";
		}
	}
}

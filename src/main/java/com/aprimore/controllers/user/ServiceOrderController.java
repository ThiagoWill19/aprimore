package com.aprimore.controllers.user;

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
import com.aprimore.models.dtos.NewServiceOrderDto;
import com.aprimore.models.dtos.ServiceOrderDetailsDto;
import com.aprimore.models.dtos.ServiceOrderListDto;
import com.aprimore.services.ServiceOrderService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/client/{clientId}/service-orders")
public class ServiceOrderController {

	@Autowired
	private ServiceOrderService serviceOrderService;

	@GetMapping
	public String listServiceOrders(
			@PathVariable UUID clientId,
			@RequestParam(defaultValue = "0") int page,
			Model model,
			RedirectAttributes redirectAttributes,
			@AuthenticationPrincipal UserDetailsImpl userDetails) {

		try {
			Page<ServiceOrderListDto> serviceOrders = serviceOrderService.listByClient(clientId, page, 10, userDetails.getUser());
			model.addAttribute("clientId", clientId);
			model.addAttribute("serviceOrders", serviceOrders);
			return "/user/ServiceOrderListPage";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("erro", e.getMessage());
			return "redirect:/user/client/" + clientId;
		}
	}

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

	@GetMapping("/{serviceOrderId}")
	public String serviceOrderDetails(
			@PathVariable UUID clientId,
			@PathVariable Long serviceOrderId,
			Model model,
			RedirectAttributes redirectAttributes,
			@AuthenticationPrincipal UserDetailsImpl userDetails) {

		try {
			ServiceOrderDetailsDto serviceOrder = serviceOrderService.findById(clientId, serviceOrderId, userDetails.getUser());
			model.addAttribute("clientId", clientId);
			model.addAttribute("serviceOrder", serviceOrder);
			model.addAttribute("machines", serviceOrderService.findMachinesByClient(clientId, userDetails.getUser()));
			model.addAttribute("blades", serviceOrderService.findBladesByBusiness(userDetails.getUser()));
			return "/user/ServiceOrderDetailsPage";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("erro", e.getMessage());
			return "redirect:/user/client/" + clientId + "/service-orders";
		}
	}

	@PostMapping("/{serviceOrderId}/update")
	public String updateServiceOrder(
			@PathVariable UUID clientId,
			@PathVariable Long serviceOrderId,
			@Valid ServiceOrderDetailsDto serviceOrderDetailsDto,
			BindingResult result,
			@AuthenticationPrincipal UserDetailsImpl userDetails,
			RedirectAttributes redirectAttributes,
			Model model) {

		if (result.hasErrors()) {
			model.addAttribute("clientId", clientId);
			model.addAttribute("serviceOrder", serviceOrderDetailsDto);
			model.addAttribute("machines", serviceOrderService.findMachinesByClient(clientId, userDetails.getUser()));
			model.addAttribute("blades", serviceOrderService.findBladesByBusiness(userDetails.getUser()));
			return "/user/ServiceOrderDetailsPage";
		}

		try {
			serviceOrderService.updateServiceOrder(clientId, serviceOrderId, serviceOrderDetailsDto, userDetails.getUser());
			redirectAttributes.addFlashAttribute("success", "Ordem de servico atualizada com sucesso!");
			return "redirect:/user/client/" + clientId + "/service-orders/" + serviceOrderId;
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("erro", e.getMessage());
			return "redirect:/user/client/" + clientId + "/service-orders/" + serviceOrderId;
		}
	}
}

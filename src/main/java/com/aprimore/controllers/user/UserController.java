package com.aprimore.controllers.user;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
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
import com.aprimore.exceptions.DomainRuleException;
import com.aprimore.models.Client;
import com.aprimore.models.dtos.ClientDetailsDto;
import com.aprimore.models.dtos.ClientListDto;
import com.aprimore.models.dtos.DashboardDto;
import com.aprimore.models.dtos.NewClientDto;
import com.aprimore.models.dtos.ServiceOrderListDto;
import com.aprimore.models.dtos.UpdateAddressDto;
import com.aprimore.services.AddressService;
import com.aprimore.services.ClientService;
import com.aprimore.services.DashboardService;
import com.aprimore.services.ServiceOrderService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private AddressService addressService;

	@Autowired
	private ServiceOrderService serviceOrderService;

	@Autowired
	private DashboardService dashboardService;

	@GetMapping
	public String inicialPage(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		
		model.addAttribute("username", userDetails.getUser().getName());
		model.addAttribute("businessName", userDetails.getUser().getBusiness().getTradeName());
		DashboardDto dashboard = dashboardService.loadDashboard(userDetails.getUser());
		model.addAttribute("dashboard", dashboard);
		
		return "/user/userInitialPage";
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

	@PostMapping("/dashboard/pcp")
	public String updateDashboardPcp(
			@RequestParam(name = "orderedIds", required = false) List<Long> orderedIds,
			RedirectAttributes redirectAttributes,
			@AuthenticationPrincipal UserDetailsImpl userDetails) {

		try {
			dashboardService.updatePcpSequence(orderedIds, userDetails.getUser());
			redirectAttributes.addFlashAttribute("success", "Sequência PCP atualizada com sucesso.");
			return "redirect:/user";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("erro", e.getMessage());
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
	
	@GetMapping("/client")
	public String listClients(
			Model model,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String search,
			@AuthenticationPrincipal UserDetailsImpl userDetails
	) {

		Page<ClientListDto> clients = clientService
				.findAllClientsByBusiness(
						page,
						size,
						search,
						userDetails.getUser()
				);

		model.addAttribute("clients", clients);
		model.addAttribute("search", search);

		return "/user/ClientListPage";
	}
	
	@PostMapping("/client/address/update")
	public String updateAddress(
	        UpdateAddressDto addressDto,
	        @AuthenticationPrincipal UserDetailsImpl userDetails
	) {
	    addressService.updateAddress(addressDto, userDetails.getUser());
	    return "redirect:/user/client/" + addressDto.getClientId();
	}


	@GetMapping("/service-orders")
	public String findAllByBusiness(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(required = false) String search,
			@RequestParam(required = false) String monthYear,
			Model model,
			RedirectAttributes redirectAttributes,
			@AuthenticationPrincipal UserDetailsImpl userDetails) {

		try {
			LocalDate endDate = LocalDate.now();
			LocalDate startDate = endDate.minusDays(30);
			String normalizedMonthYear = monthYear == null ? null : monthYear.trim();

			if (normalizedMonthYear != null && !normalizedMonthYear.isBlank()) {
				YearMonth selectedMonth = YearMonth.parse(normalizedMonthYear);
				startDate = selectedMonth.atDay(1);
				endDate = selectedMonth.atEndOfMonth();
			}

			Page<ServiceOrderListDto> serviceOrders = serviceOrderService.listAllByBusiness(
					page,
					10,
					search,
					startDate,
					endDate,
					userDetails.getUser());
			model.addAttribute("serviceOrders", serviceOrders);
			model.addAttribute("search", search);
			model.addAttribute("monthYear", normalizedMonthYear);
			model.addAttribute("defaultLast30Days",
					normalizedMonthYear == null || normalizedMonthYear.isBlank());
			return "/user/AllServiceOrderListPage";
		} catch (DateTimeParseException e) {
			redirectAttributes.addFlashAttribute("erro", "Período inválido. Use o formato mês/ano.");
			return "redirect:/user/service-orders";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("erro", e.getMessage());
			return "redirect:/user/dashboard";
		}
	}



}

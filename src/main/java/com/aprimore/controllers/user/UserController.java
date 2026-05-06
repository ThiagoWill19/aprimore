package com.aprimore.controllers.user;

import com.aprimore.services.UserService;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aprimore.configurations.security.UserDetailsImpl;
import com.aprimore.exceptions.DomainRuleException;

import com.aprimore.models.dtos.DashboardDto;

import com.aprimore.models.dtos.ServiceOrderListDto;

import com.aprimore.models.dtos.UserDto;

import com.aprimore.services.DashboardService;

import com.aprimore.services.ServiceOrderService;


@Controller
@RequestMapping("/user")
public class UserController {
	
	private final UserService userService;

	@Autowired
	private ServiceOrderService serviceOrderService;

	@Autowired
	private DashboardService dashboardService;


    UserController(UserService userService) {
        this.userService = userService;
    }

	@GetMapping
	public String inicialPage(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		
		model.addAttribute("username", userDetails.getUser().getName());
		model.addAttribute("businessName", userDetails.getUser().getBusiness().getTradeName());
		DashboardDto dashboard = dashboardService.loadDashboard(userDetails.getUser());
		model.addAttribute("dashboard", dashboard);
		
		return "/user/userInitialPage";
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

	@GetMapping("/configurations")
	public String getMethodName(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {

		UserDto userDto = userService.findUserById(userDetails.getUser().getId());
		model.addAttribute("user", userDto);	
		return "/user/UserConfigurations";
	}
	


    @PostMapping("/alter-password")
	public String alterPassword(
			@RequestParam String oldPassword,
			@RequestParam String newPassword,
			RedirectAttributes redirectAttributes,
			@AuthenticationPrincipal UserDetailsImpl userDetails) {
		
		try {
			userService.alterPassword(userDetails.getUser().getId(), oldPassword, newPassword);
			redirectAttributes.addFlashAttribute("success", "Senha alterada com sucesso.");
			return "redirect:/user";
		} catch (DomainRuleException e) {
			redirectAttributes.addFlashAttribute("erro", e.getMessage());
			return "redirect:/user/configurations";
		}
		
	}

}

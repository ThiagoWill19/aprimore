package com.aprimore.controllers.user;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.aprimore.configurations.security.UserDetailsImpl;
import com.aprimore.models.dtos.MachineListDto;
import com.aprimore.models.dtos.NewMachineDto;
import com.aprimore.services.MachineService;

@Controller
@RequestMapping("/user/client/{clientId}/machines")
public class MachineController {

	@Autowired
	private MachineService machineService;

	@GetMapping
	public String listMachines(
			@PathVariable UUID clientId,
			@RequestParam(defaultValue = "0") int page,
			Model model,
			@AuthenticationPrincipal UserDetailsImpl userDetails
	) {

		Page<MachineListDto> machines =
				machineService.listByClient(clientId, page, 10, userDetails.getUser());

		model.addAttribute("machines", machines);
		model.addAttribute("clientId", clientId);

		return "/user/machineListPage";
	}

	@GetMapping("/new")
	public String newMachineForm(
			@PathVariable UUID clientId,
			Model model
	) {

		model.addAttribute("clientId", clientId);
		model.addAttribute("machine", new NewMachineDto());

		return "/user/NewMachinePage";
	}

	@PostMapping
	public String createMachine(
			@PathVariable UUID clientId,
			NewMachineDto dto,
			@AuthenticationPrincipal UserDetailsImpl userDetails
	) {

		machineService.createMachine(clientId, dto, userDetails.getUser());

		return "redirect:/user/client/" + clientId + "/machines";
	}
}

package com.aprimore.controllers.user;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
			RedirectAttributes redirectAttributes,
			@AuthenticationPrincipal UserDetailsImpl userDetails
	) {

		try {
			Page<MachineListDto> machines =
					machineService.listByClient(clientId, page, 10, userDetails.getUser());
			model.addAttribute("machines", machines);
			
			model.addAttribute("clientId", clientId);

			return "/user/machineListPage";
			
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("erro",e.getMessage());
			return "redirect:/user/client/" + clientId;
		}

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
			RedirectAttributes redirectAttributes,
			@AuthenticationPrincipal UserDetailsImpl userDetails
	) {

		try {
			machineService.createMachine(clientId, dto, userDetails.getUser());
			return "redirect:/user/client/" + clientId + "/machines";
			
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("erro",e.getMessage());
			return "redirect:/user/client/" + clientId;
		}

	}
	
	@PostMapping("/machine/{machineId}/updateStatus")
	public String updateMachine(
			@PathVariable UUID machineId,
			@RequestParam UUID clientId,
			@AuthenticationPrincipal UserDetailsImpl userDetails,
			RedirectAttributes redirectAttributes
	) {

		try {

			machineService.updateMachineStatus(machineId, userDetails.getUser());
			redirectAttributes.addFlashAttribute(
					"success",
					"Máquina removida com sucesso!"
			);

		} catch (Exception e) {

			redirectAttributes.addFlashAttribute(
					"erro",
					e.getMessage()
			);
		}

		return "redirect:/user/client/" + clientId + "/machines";
	}

}

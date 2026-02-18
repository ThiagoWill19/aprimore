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
import com.aprimore.models.dtos.MachineDetailsDto;
import com.aprimore.models.dtos.MachineListDto;
import com.aprimore.models.dtos.NewMachineDto;
import com.aprimore.services.MachineService;

import jakarta.validation.Valid;

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
			@AuthenticationPrincipal UserDetailsImpl userDetails) {

		try {
			Page<MachineListDto> machines = machineService.listByClient(clientId, page, 10, userDetails.getUser());
			model.addAttribute("machines", machines);
			model.addAttribute("clientId", clientId);
			return "/user/machineListPage";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("erro", e.getMessage());
			return "redirect:/user/client/" + clientId;
		}
	}

	@GetMapping("/new")
	public String newMachineForm(@PathVariable UUID clientId, Model model) {
		model.addAttribute("clientId", clientId);
		model.addAttribute("machine", new NewMachineDto());
		return "/user/NewMachinePage";
	}

	@PostMapping
	public String createMachine(
			@PathVariable UUID clientId,
			NewMachineDto dto,
			RedirectAttributes redirectAttributes,
			@AuthenticationPrincipal UserDetailsImpl userDetails) {

		try {
			machineService.createMachine(clientId, dto, userDetails.getUser());
			return "redirect:/user/client/" + clientId + "/machines";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("erro", e.getMessage());
			return "redirect:/user/client/" + clientId;
		}
	}

	@GetMapping("/machine/{machineId}")
	public String machineDetails(
			@PathVariable UUID clientId,
			@PathVariable UUID machineId,
			Model model,
			RedirectAttributes redirectAttributes,
			@AuthenticationPrincipal UserDetailsImpl userDetails) {
		try {
			MachineDetailsDto machine = machineService.findById(clientId, machineId, userDetails.getUser());
			model.addAttribute("machine", machine);
			model.addAttribute("clientId", clientId);
			return "/user/MachineDetailsPage";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("erro", e.getMessage());
			return "redirect:/user/client/" + clientId + "/machines";
		}
	}

	@PostMapping("/machine/{machineId}/update")
	public String updateMachineDetails(
			@PathVariable UUID clientId,
			@PathVariable UUID machineId,
			@Valid MachineDetailsDto machineDetailsDto,
			BindingResult result,
			@AuthenticationPrincipal UserDetailsImpl userDetails,
			RedirectAttributes redirectAttributes,
			Model model) {

		if (result.hasErrors()) {
			machineDetailsDto.setClientId(clientId);
			machineDetailsDto.setId(machineId);
			model.addAttribute("machine", machineDetailsDto);
			model.addAttribute("clientId", clientId);
			return "/user/MachineDetailsPage";
		}

		try {
			machineService.updateMachine(clientId, machineId, machineDetailsDto, userDetails.getUser());
			return "redirect:/user/client/" + clientId + "/machines/machine/" + machineId;
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("erro", e.getMessage());
			return "redirect:/user/client/" + clientId + "/machines/machine/" + machineId;
		}
	}

	@PostMapping("/machine/{machineId}/updateStatus")
	public String updateMachineStatus(
			@PathVariable UUID clientId,
			@PathVariable UUID machineId,
			@AuthenticationPrincipal UserDetailsImpl userDetails,
			RedirectAttributes redirectAttributes) {

		try {
			machineService.updateMachineStatus(clientId, machineId, userDetails.getUser());
			redirectAttributes.addFlashAttribute("success", "Status da maquina atualizado com sucesso!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("erro", e.getMessage());
		}

		return "redirect:/user/client/" + clientId + "/machines";
	}
}

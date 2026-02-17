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
import com.aprimore.exceptions.DomainRuleException;
import com.aprimore.models.dtos.NewBladeDto;
import com.aprimore.services.BladeService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/blades")
public class BladeController {

    @Autowired
    private BladeService bladeService;

    @GetMapping
    public String listBlades(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Model model
    ) {

        model.addAttribute("blades", bladeService.findAllByBusiness(userDetails.getUser()));
        model.addAttribute("newBlade", new NewBladeDto());
        return "/user/bladeListPage";
    }

    @PostMapping
    public String createBlade(
            @Valid NewBladeDto newBladeDto,
            BindingResult result,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            RedirectAttributes redirectAttributes,
            Model model
    ) {

        if (result.hasErrors()) {
            model.addAttribute("blades", bladeService.findAllByBusiness(userDetails.getUser()));
            model.addAttribute("newBlade", newBladeDto);
            return "/user/bladeListPage";
        }

        try {
            bladeService.createBlade(newBladeDto, userDetails.getUser());
            redirectAttributes.addFlashAttribute("success", "Lâmina cadastrada com sucesso!");
            return "redirect:/user/blades";

        } catch (DomainRuleException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/user/blades";
        }
    }

    @PostMapping("/{bladeId}/delete")
    public String deleteBlade(
            @PathVariable UUID bladeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            RedirectAttributes redirectAttributes
    ) {

        try {
            bladeService.deleteBlade(bladeId, userDetails.getUser());
            redirectAttributes.addFlashAttribute("success", "Lâmina removida com sucesso!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/user/blades";
    }
}

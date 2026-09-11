package com.aprimore.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.aprimore.exceptions.DomainRuleException;
import com.aprimore.services.AccountActivationService;

@Controller
public class AccountActivationController {
    private final AccountActivationService accountActivationService;

    public AccountActivationController(AccountActivationService accountActivationService) {
        this.accountActivationService = accountActivationService;
    }

    @GetMapping("/activate-account")
    public String activationForm(@RequestParam(required = false) String token, Model model) {
        if (!accountActivationService.isTokenUsable(token)) {
            model.addAttribute("invalidToken", true);
        }
        model.addAttribute("token", token);
        return "activate-account";
    }

    @PostMapping("/activate-account")
    public String activateAccount(@RequestParam String token,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {
        if (!password.matches("^(?=.*[!@#$%^&*]).{8,}$")) {
            model.addAttribute("error", "A senha deve ter pelo menos 8 caracteres e 1 caractere especial.");
        } else if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "As senhas nao coincidem.");
        } else {
            try {
                accountActivationService.activateAccount(token, password);
                return "redirect:/login?activated";
            } catch (DomainRuleException exception) {
                model.addAttribute("error", exception.getMessage());
            }
        }
        model.addAttribute("token", token);
        return "activate-account";
    }
}

package com.aprimore.controllers.user;

import com.aprimore.configurations.security.UserDetailsImpl;
import com.aprimore.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @PostMapping("/user/dashboard/pcp")
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
}

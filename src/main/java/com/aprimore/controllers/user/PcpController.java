package com.aprimore.controllers.user;

import com.aprimore.configurations.security.UserDetailsImpl;
import com.aprimore.services.PcpPdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PcpController {

    @Autowired
    private PcpPdfService pcpPdfService;

    @GetMapping("/user/pcp-pdf")
    public ResponseEntity<byte[]> downloadPcpPdf(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        byte[] pdf = pcpPdfService.gerarPdf(userDetails.getUser().getBusiness().getId());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=pcp-" + userDetails.getUser().getBusiness().getName() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}

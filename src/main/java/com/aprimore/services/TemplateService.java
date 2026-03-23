package com.aprimore.services;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.aprimore.models.dtos.ServiceOrderPdfDto;

@Service
public class TemplateService {
    
    private final SpringTemplateEngine templateEngine;

    public TemplateService(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String renderServiceOrder(ServiceOrderPdfDto dto) {
    Context context = new Context();
    context.setVariable("os", dto); // mantém "os" por causa do template
    return templateEngine.process("user/service-order-pdf", context);
}

}

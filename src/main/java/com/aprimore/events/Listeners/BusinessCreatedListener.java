package com.aprimore.events.Listeners;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.aprimore.events.BusinessCreatedEvent;
import com.aprimore.services.EmailService;

@Component
public class BusinessCreatedListener {

    private final EmailService emailService;

    public BusinessCreatedListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBusinessCreated(BusinessCreatedEvent event) {
        try {
            emailService.sendMail(
                    event.getEmail(),
                    "Conta Aprimore criada com sucesso",
                    "Sua conta foi criada. Para definir sua senha de acesso, acesse o link abaixo:\n"
                            + event.getActivationLink()
                            + "\n\nEste link e valido por 24 horas e pode ser usado uma unica vez.");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

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
					event.getUser().getEmail(),
					"Conta Aprimore criada com sucesso",
					"Essa é sua senha para acessar a plataforma: "
					+ event.getRawPassword()
					+ "\nAltere sua senha em configurações.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

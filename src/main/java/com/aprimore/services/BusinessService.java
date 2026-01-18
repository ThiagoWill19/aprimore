package com.aprimore.services;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.aprimore.models.Business;
import com.aprimore.models.User;
import com.aprimore.models.dtos.BusinessListDto;
import com.aprimore.models.dtos.NewBusinessDto;
import com.aprimore.models.enuns.AccountStatus;
import com.aprimore.models.enuns.Role;
import com.aprimore.models.mappers.BusinessMapper;
import com.aprimore.repositories.BusinessRepository;
import com.aprimore.utils.PasswordGenerator;

@Service
public class BusinessService {

	@Autowired
	private BusinessRepository businessRepository;
	
	@Autowired
	BusinessMapper businessMaper;
		
	public void newBusiness(NewBusinessDto newBusinessDto) {
		
		Business newBusiness;
		User newUser;
		
		String password = PasswordGenerator.gerarSenha();
		
		newUser = new User();
		newUser.setName(newBusinessDto.getUsername());
		newUser.setEmail(newBusinessDto.getEmail());
		newUser.setRole(Role.USER);
		newUser.setPassword(new BCryptPasswordEncoder().encode(password));
		
		System.out.println(password);  //Criar método para enviar senha para o usuário
		
		newBusiness = new Business();
		newBusiness.setName(newBusinessDto.getBusinessName());
		newBusiness.setTradeName(newBusinessDto.getTradeName());
		newBusiness.setCnpj(newBusinessDto.getCnpj().replaceAll("\\D", "")); //Cadastrar sem mascara de cnpj
		newBusiness.setBusinessEmail(newBusinessDto.getBusinessEmail());
		newBusiness.setPhone(newBusinessDto.getPhone());
		
	
		newBusiness.setAccountStatus(AccountStatus.ACTIVE);	
		newBusiness.setCreatedAt(LocalDate.now());
		
		
		newUser.setBusiness(newBusiness);
		newBusiness.getUsers().add(newUser);
		
		businessRepository.save(newBusiness);
		
	}
	
	
	public Page<BusinessListDto> findAllByOrderByName(int pageNum, int size, String search){
		
		Pageable pageable = PageRequest.of(pageNum, size);
		Page<Business> page;
		
		if (search == null || search.isBlank()) {
			
			page = businessRepository.findAllByOrderByName(pageable);
			return page.map(businessMaper::mapToBusinessListDto);
		}
		
		String term = search.trim();
		
		// Remove máscara se o usuário digitar CNPJ com ponto e traço
	    String numericTerm = term.replaceAll("\\D", "");
		
	    if(numericTerm.length() == 14) {
	    	page = businessRepository.findByCnpjContaining(numericTerm, pageable);
	    	return page.map(businessMaper::mapToBusinessListDto);
	    }
		
	    page = businessRepository.findByNameOrTradeName(term.toLowerCase(), pageable);
	    return page.map(businessMaper::mapToBusinessListDto);
	}
}

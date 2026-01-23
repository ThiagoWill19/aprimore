package com.aprimore.services;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.aprimore.exceptions.BusinessRuleException;
import com.aprimore.exceptions.ResourceNotFoundException;
import com.aprimore.models.Business;
import com.aprimore.models.User;
import com.aprimore.models.dtos.BusinessDetailsDto;
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
	BusinessMapper businessMapper;

	@Autowired
	private EmailService emailService;

	public void newBusiness(NewBusinessDto newBusinessDto) {

		Business newBusiness;
		User newUser;

		String password = PasswordGenerator.gerarSenha();

		newUser = new User();
		newUser.setName(newBusinessDto.getUsername());
		newUser.setEmail(newBusinessDto.getEmail());
		newUser.setRole(Role.USER);
		newUser.setPassword(new BCryptPasswordEncoder().encode(password));

		newBusiness = new Business();
		newBusiness.setName(newBusinessDto.getBusinessName());
		newBusiness.setTradeName(newBusinessDto.getTradeName());
		newBusiness.setCnpj(newBusinessDto.getCnpj().replaceAll("\\D", "")); // Cadastrar sem mascara de cnpj
		newBusiness.setBusinessEmail(newBusinessDto.getBusinessEmail());
		newBusiness.setPhone(newBusinessDto.getPhone());

		newBusiness.setAccountStatus(AccountStatus.ACTIVE);
		newBusiness.setCreatedAt(LocalDate.now());

		newUser.setBusiness(newBusiness);
		newBusiness.getUsers().add(newUser);

		try {
			businessRepository.save(newBusiness);
		} catch (DataIntegrityViolationException e) {
			throw new BusinessRuleException(
					"Ops, você informou dados de uma empresa já cadastrada! Verifique os dados informados.");
		}

		System.out.println(password);
		emailService.sendMail(newUser.getEmail(), "Conta Aprimore criada com sucesso!",
				"Essa é sua senha para acessar a plataforma: " + password + "\nAltere sua senha em configurações.");

	}
	

	public Page<BusinessListDto> findAllByOrderByName(int pageNum, int size, String search) {

		Pageable pageable = PageRequest.of(pageNum, size);
		Page<Business> page;

		if (search == null || search.isBlank()) {

			page = businessRepository.findAllByOrderByName(pageable);
			return page.map(businessMapper::mapToBusinessListDto);
		}

		String term = search.trim();

		// Remove máscara se o usuário digitar CNPJ com ponto e traço
		String numericTerm = term.replaceAll("\\D", "");

		if (numericTerm.length() == 14) {
			page = businessRepository.findByCnpjContaining(numericTerm, pageable);
			return page.map(businessMapper::mapToBusinessListDto);
		}

		page = businessRepository.findByNameOrTradeName(term.toLowerCase(), pageable);
		return page.map(businessMapper::mapToBusinessListDto);
	}
	

	public BusinessDetailsDto findById(UUID id) throws ResourceNotFoundException {

		if (businessRepository.existsById(id)) {

			Business business = businessRepository.findById(id).get();
			BusinessDetailsDto businessDetailsDto = businessMapper.mapToBusinessDetailsDto(business);
			businessDetailsDto.setQuantityUser(business.getUsers().size());
			businessDetailsDto.setQuantityClients(business.getClients().size());
			return businessDetailsDto;

		} else {

			throw new ResourceNotFoundException("Empresa não encontrada com ID informado!");
		}

	}
	

	public BusinessDetailsDto updateBusiness(BusinessDetailsDto businessDetailsDto) {

		Business business = businessRepository.findById(businessDetailsDto.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Empresa com id informado não encontrada"));

		business = businessMapper.mapToBusiness(businessDetailsDto, business);

		Business savedBusiness = businessRepository.save(business);

		return businessMapper.mapToBusinessDetailsDto(savedBusiness);
	}

	
	public void changeBusinessStatus(UUID id) {

		Business business = businessRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Empresa com id informado não encontrada"));
		
		if(business.getAccountStatus() == AccountStatus.ACTIVE) {
			business.setAccountStatus(AccountStatus.INACTIVE);
		}else {
			business.setAccountStatus(AccountStatus.ACTIVE);
		}
		
		businessRepository.save(business);

	}
}

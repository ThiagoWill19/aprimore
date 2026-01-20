package com.aprimore.models.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.aprimore.models.Business;
import com.aprimore.models.dtos.BusinessDetailsDto;
import com.aprimore.models.dtos.BusinessListDto;

@Component
public class BusinessMapper {
	
	private ModelMapper modelMapper = new ModelMapper();
	
	public BusinessListDto mapToBusinessListDto(Business business) {
		return modelMapper.map(business, BusinessListDto.class);
	}
	
	
	public BusinessDetailsDto mapToBusinessDetailsDto(Business business) {
		return modelMapper.map(business, BusinessDetailsDto.class);
	}
	
	public Business mapToBusiness(BusinessDetailsDto dto, Business business) {
		
        business.setName(dto.getName());
        business.setTradeName(dto.getTradeName());
        business.setCnpj(dto.getCnpj());
        business.setBusinessEmail(dto.getBusinessEmail());
        business.setPhone(dto.getPhone());
        
        return business;
	}

}

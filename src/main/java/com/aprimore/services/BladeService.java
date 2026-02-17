package com.aprimore.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aprimore.exceptions.DomainRuleException;
import com.aprimore.exceptions.ResourceNotFoundException;
import com.aprimore.models.Blade;
import com.aprimore.models.Business;
import com.aprimore.models.User;
import com.aprimore.models.dtos.BladeListDto;
import com.aprimore.models.dtos.NewBladeDto;
import com.aprimore.models.mappers.BladeMapper;
import com.aprimore.repositories.BusinessRepository;
import com.aprimore.repositories.ItemRepository;

@Service
public class BladeService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private BladeMapper bladeMapper;

    public List<BladeListDto> findAllByBusiness(User user) {

        return itemRepository.findBladesByBusinessId(user.getBusiness().getId())
                .stream()
                .map(bladeMapper::mapToBladeListDto)
                .toList();
    }

    @Transactional
    public BladeListDto createBlade(NewBladeDto newBladeDto, User user) {

        UUID businessId = user.getBusiness().getId();

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        String normalizedName = newBladeDto.getName().trim();

        if (itemRepository.existsBladeByBusinessIdAndName(businessId, normalizedName)) {
            throw new DomainRuleException("Já existe uma lâmina com esse nome para essa empresa.");
        }

        Blade blade = bladeMapper.mapToBlade(newBladeDto, business);

        return bladeMapper.mapToBladeListDto((Blade) itemRepository.save(blade));
    }

    @Transactional
    public void deleteBlade(UUID bladeId, User user) {

        Blade blade = itemRepository.findById(bladeId)
                .filter(Blade.class::isInstance)
                .map(Blade.class::cast)
                .orElseThrow(() -> new ResourceNotFoundException("Lâmina não encontrada"));

        if (!blade.getBusiness().getId().equals(user.getBusiness().getId())) {
            throw new AccessDeniedException("Você não tem permissão para remover esta lâmina.");
        }

        itemRepository.delete(blade);
    }
}

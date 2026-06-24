package com.truongquoctoan.example01.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.truongquoctoan.example01.dto.PromotionDTO;
import com.truongquoctoan.example01.entity.Promotion;
import com.truongquoctoan.example01.repository.PromotionRepository;
import com.truongquoctoan.example01.service.PromotionService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    private PromotionDTO convertToDTO(Promotion promotion) {
        PromotionDTO dto = new PromotionDTO();
        dto.setId(promotion.getId());
        dto.setName(promotion.getName());
        dto.setDiscountPercentage(promotion.getDiscountPercentage());
        dto.setDiscountAmount(promotion.getDiscountAmount());
        dto.setStartDate(promotion.getStartDate());
        dto.setEndDate(promotion.getEndDate());
        dto.setIsActive(promotion.getIsActive());
        return dto;
    }

    private Promotion convertToEntity(PromotionDTO dto) {
        return Promotion.builder()
                .id(dto.getId())
                .name(dto.getName())
                .discountPercentage(dto.getDiscountPercentage())
                .discountAmount(dto.getDiscountAmount())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();
    }

    @Override
    public PromotionDTO createPromotion(PromotionDTO promotionDTO) {
        Promotion promotion = convertToEntity(promotionDTO);
        Promotion saved = promotionRepository.save(promotion);
        return convertToDTO(saved);
    }

    @Override
    public PromotionDTO updatePromotion(Long id, PromotionDTO promotionDTO) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Promotion not found with id: " + id));

        promotion.setName(promotionDTO.getName());
        promotion.setDiscountPercentage(promotionDTO.getDiscountPercentage());
        promotion.setDiscountAmount(promotionDTO.getDiscountAmount());
        promotion.setStartDate(promotionDTO.getStartDate());
        promotion.setEndDate(promotionDTO.getEndDate());
        promotion.setIsActive(promotionDTO.getIsActive());

        Promotion updated = promotionRepository.save(promotion);
        return convertToDTO(updated);
    }

    @Override
    public void deletePromotion(Long id) {
        if (!promotionRepository.existsById(id)) {
            throw new EntityNotFoundException("Promotion not found with id: " + id);
        }
        promotionRepository.deleteById(id);
    }

    @Override
    public PromotionDTO getPromotionById(Long id) {
        return promotionRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Promotion not found with id: " + id));
    }

    @Override
    public List<PromotionDTO> getAllPromotions() {
        return promotionRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
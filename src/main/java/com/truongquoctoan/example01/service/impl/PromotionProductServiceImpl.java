package com.truongquoctoan.example01.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.truongquoctoan.example01.dto.PromotionProductDto;
import com.truongquoctoan.example01.entity.Product;
import com.truongquoctoan.example01.entity.Promotion;
import com.truongquoctoan.example01.entity.PromotionProduct;
import com.truongquoctoan.example01.repository.ProductRepository;
import com.truongquoctoan.example01.repository.PromotionProductRepository;
import com.truongquoctoan.example01.repository.PromotionRepository;
import com.truongquoctoan.example01.service.PromotionProductService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionProductServiceImpl implements PromotionProductService {

        private final PromotionProductRepository repository;
        private final PromotionRepository promotionRepository;
        private final ProductRepository productRepository;

        @Override
        public PromotionProductDto create(PromotionProductDto dto) {
                Promotion promotion = promotionRepository.findById(dto.getPromotionId())
                                .orElseThrow(() -> new EntityNotFoundException("Promotion not found"));

                Product product = productRepository.findById(dto.getProductId())
                                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

                PromotionProduct entity = PromotionProduct.builder()
                                .promotion(promotion)
                                .product(product)
                                .discountPercent(dto.getDiscountPercent())
                                .build();

                return convertToDto(repository.save(entity));
        }

        @Override
        public PromotionProductDto update(Long id, PromotionProductDto dto) {
                PromotionProduct pp = repository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("PromotionProduct not found"));

                Promotion promotion = promotionRepository.findById(dto.getPromotionId())
                                .orElseThrow(() -> new EntityNotFoundException("Promotion not found"));

                Product product = productRepository.findById(dto.getProductId())
                                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

                pp.setPromotion(promotion);
                pp.setProduct(product);
                pp.setDiscountPercent(dto.getDiscountPercent());

                return convertToDto(repository.save(pp));
        }

        @Override
        public List<PromotionProductDto> getAll() {
                return repository.findAll().stream()
                                .map(this::convertToDto)
                                .collect(Collectors.toList());
        }

        @Override
        public PromotionProductDto getById(Long id) {
                PromotionProduct pp = repository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("PromotionProduct not found"));

                return convertToDto(pp);
        }

        @Override
        public void delete(Long id) {
                repository.deleteById(id);
        }

        private PromotionProductDto convertToDto(PromotionProduct pp) {
                return new PromotionProductDto(
                                pp.getId(),
                                pp.getPromotion().getId(),
                                pp.getProduct().getId(),
                                pp.getDiscountPercent());
        }
}
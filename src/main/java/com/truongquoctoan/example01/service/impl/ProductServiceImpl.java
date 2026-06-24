package com.truongquoctoan.example01.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.truongquoctoan.example01.dto.ProductDto;
import com.truongquoctoan.example01.entity.Category;
import com.truongquoctoan.example01.entity.Product;
import com.truongquoctoan.example01.repository.CategoryRepository;
import com.truongquoctoan.example01.repository.ProductRepository;
import com.truongquoctoan.example01.service.ProductService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ProductDto createProduct(ProductDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stockQuantity(dto.getStockQuantity())
                .imageUrl(dto.getImageUrl())
                .isActive(dto.getIsActive())
                .category(category)
                .build();

        return mapToDto(productRepository.save(product));
    }

    @Override
    public ProductDto getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto updateProduct(Long id, ProductDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setImageUrl(dto.getImageUrl());
        product.setIsActive(dto.getIsActive());
        product.setCategory(category);

        return mapToDto(productRepository.save(product));
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    private ProductDto mapToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .isActive(product.getIsActive())
                .categoryId(product.getCategory().getId())
                .build();
    }
}
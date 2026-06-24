package com.truongquoctoan.example01.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.truongquoctoan.example01.dto.CategoryDto;
import com.truongquoctoan.example01.entity.Category;
import com.truongquoctoan.example01.repository.CategoryRepository;
import com.truongquoctoan.example01.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    private CategoryDto mapToDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .build();
    }

    private Category mapToEntity(CategoryDto dto) {
        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .build();
    }

    @Override
    public CategoryDto createCategory(CategoryDto dto) {
        Category category = mapToEntity(dto);
        return mapToDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public CategoryDto updateCategory(Long id, CategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setImageUrl(dto.getImageUrl());
        return mapToDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
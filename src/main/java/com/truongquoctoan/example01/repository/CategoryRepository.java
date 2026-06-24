package com.truongquoctoan.example01.repository;

import com.truongquoctoan.example01.entity.Category; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
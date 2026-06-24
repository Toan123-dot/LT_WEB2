package com.truongquoctoan.example01.repository;

import com.truongquoctoan.example01.entity.PromotionProduct; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionProductRepository extends JpaRepository<PromotionProduct, Long> {
}
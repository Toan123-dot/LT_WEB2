package com.truongquoctoan.example01.repository;

import com.truongquoctoan.example01.entity.Promotion; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
}
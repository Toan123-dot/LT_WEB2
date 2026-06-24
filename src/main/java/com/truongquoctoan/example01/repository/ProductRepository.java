package com.truongquoctoan.example01.repository;

import com.truongquoctoan.example01.entity.Product; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository 
public interface ProductRepository extends JpaRepository<Product, Long> {
}
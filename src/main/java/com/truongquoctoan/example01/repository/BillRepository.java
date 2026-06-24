package com.truongquoctoan.example01.repository;
import com.truongquoctoan.example01.entity.Bill; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
}
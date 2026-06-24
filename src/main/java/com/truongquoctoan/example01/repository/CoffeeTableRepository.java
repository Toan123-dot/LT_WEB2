package com.truongquoctoan.example01.repository;

import com.truongquoctoan.example01.entity.CoffeeTable; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository 
public interface CoffeeTableRepository extends JpaRepository<CoffeeTable, Long> {
    List<CoffeeTable> findByStatus(CoffeeTable.TableStatus status);
}
package com.truongquoctoan.example01.repository;

import com.truongquoctoan.example01.entity.Settings; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository 
public interface SettingsRepository extends JpaRepository<Settings, Long> {
}
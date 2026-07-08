package com.truongquoctoan.example01.controller;

import com.truongquoctoan.example01.dto.PromotionDTO;
import com.truongquoctoan.example01.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<PromotionDTO> create(@RequestBody PromotionDTO promotionDTO) {
        return ResponseEntity.ok(promotionService.createPromotion(promotionDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<PromotionDTO> update(@PathVariable Long id, @RequestBody PromotionDTO promotionDTO) {
        return ResponseEntity.ok(promotionService.updatePromotion(id, promotionDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ROLE_ADMIN', 'EMPLOYEE', 'ROLE_EMPLOYEE')")
    public ResponseEntity<PromotionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(promotionService.getPromotionById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ROLE_ADMIN', 'EMPLOYEE', 'ROLE_EMPLOYEE')")
    public ResponseEntity<List<PromotionDTO>> getAll() {
        return ResponseEntity.ok(promotionService.getAllPromotions());
    }
}
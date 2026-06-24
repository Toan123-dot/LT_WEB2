package com.truongquoctoan.example01.controller;

import com.truongquoctoan.example01.dto.CoffeeTableDto;
import com.truongquoctoan.example01.service.CoffeeTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
public class CoffeeTableController {

    private final CoffeeTableService tableService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CoffeeTableDto> create(@RequestBody CoffeeTableDto dto) {
        return ResponseEntity.ok(tableService.createTable(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoffeeTableDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tableService.getTableById(id));
    }

    @GetMapping
    public ResponseEntity<List<CoffeeTableDto>> getAll() {
        return ResponseEntity.ok(tableService.getAllTables());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'STAFF')")
    public ResponseEntity<CoffeeTableDto> update(@PathVariable Long id, @RequestBody CoffeeTableDto dto) {
        return ResponseEntity.ok(tableService.updateTable(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/available")
    public ResponseEntity<List<CoffeeTableDto>> getAvailableTables() {
        return ResponseEntity.ok(tableService.getAvailableTables());
    }
}
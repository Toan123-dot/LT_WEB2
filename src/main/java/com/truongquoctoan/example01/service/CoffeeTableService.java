package com.truongquoctoan.example01.service;

import com.truongquoctoan.example01.dto.CoffeeTableDto;

import java.util.List;

public interface CoffeeTableService {
    CoffeeTableDto createTable(CoffeeTableDto dto);

    CoffeeTableDto getTableById(Long id);

    List<CoffeeTableDto> getAllTables();

    CoffeeTableDto updateTable(Long id, CoffeeTableDto dto);

    void deleteTable(Long id);

    List<CoffeeTableDto> getAvailableTables();
}
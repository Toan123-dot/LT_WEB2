package com.truongquoctoan.example01.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.truongquoctoan.example01.dto.CoffeeTableDto;
import com.truongquoctoan.example01.entity.CoffeeTable;
import com.truongquoctoan.example01.repository.CoffeeTableRepository;
import com.truongquoctoan.example01.service.CoffeeTableService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoffeeTableServiceImpl implements CoffeeTableService {

    private final CoffeeTableRepository repository;

    @Override
    public CoffeeTableDto createTable(CoffeeTableDto dto) {
        CoffeeTable table = CoffeeTable.builder()
                .number(dto.getNumber())
                .capacity(dto.getCapacity())
                .status(dto.getStatus())
                .build();
        return mapToDto(repository.save(table));
    }

    @Override
    public CoffeeTableDto getTableById(Long id) {
        return repository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Table not found"));
    }

    @Override
    public List<CoffeeTableDto> getAllTables() {
        return repository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public CoffeeTableDto updateTable(Long id, CoffeeTableDto dto) {
        CoffeeTable table = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found"));

        table.setNumber(dto.getNumber());
        table.setCapacity(dto.getCapacity());
        table.setStatus(dto.getStatus());

        return mapToDto(repository.save(table));
    }

    @Override
    public void deleteTable(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<CoffeeTableDto> getAvailableTables() {
        return repository.findByStatus(CoffeeTable.TableStatus.FREE)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private CoffeeTableDto mapToDto(CoffeeTable table) {
        return CoffeeTableDto.builder()
                .id(table.getId())
                .number(table.getNumber())
                .capacity(table.getCapacity())
                .status(table.getStatus())
                .build();
    }
}
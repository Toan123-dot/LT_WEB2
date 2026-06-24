package com.truongquoctoan.example01.service;

import com.truongquoctoan.example01.dto.BillDTO;
import com.truongquoctoan.example01.entity.Bill;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface BillService {
    List<Bill> getAll();

    Bill getById(Long id);

    Bill create(BillDTO dto);

    ByteArrayInputStream exportPdf(Long billId);
}
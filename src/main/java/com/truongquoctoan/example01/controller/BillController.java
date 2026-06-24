package com.truongquoctoan.example01.controller;

import com.truongquoctoan.example01.dto.BillDTO;
import com.truongquoctoan.example01.entity.Bill;
import com.truongquoctoan.example01.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class BillController {

    private final BillService billService;

    @GetMapping
    public List<Bill> getAll() {
        return billService.getAll();
    }

    @PostMapping
    public Bill create(@RequestBody BillDTO dto) {
        return billService.create(dto);
    }

    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> export(@PathVariable Long id) {
        try (ByteArrayInputStream bis = billService.exportPdf(id)) {
            byte[] pdfBytes = bis.readAllBytes();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bill-" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to export PDF for bill " + id, e);
        }
    }
}
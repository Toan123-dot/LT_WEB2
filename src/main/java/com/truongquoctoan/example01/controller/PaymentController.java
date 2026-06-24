package com.truongquoctoan.example01.controller;

import com.truongquoctoan.example01.dto.PaymentRequest;
import com.truongquoctoan.example01.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/vnpay/create")
    public ResponseEntity<Map<String, String>> createVNPayPayment(@RequestBody PaymentRequest request) {
        String paymentUrl = paymentService.createVNPayPayment(request);
        return ResponseEntity.ok(Map.of("paymentUrl", paymentUrl));
    }

    @GetMapping("/vnpay/callback")
    public ResponseEntity<Map<String, String>> vnpayCallback(@RequestParam Map<String, String> params) {
        boolean isSuccess = paymentService.verifyVNPayCallback(params);
        return ResponseEntity.ok(Map.of("success", String.valueOf(isSuccess)));
    }

    @PostMapping("/momo/create")
    public ResponseEntity<Map<String, String>> createMoMoPayment(@RequestBody PaymentRequest request) {
        String paymentUrl = paymentService.createMoMoPayment(request);
        return ResponseEntity.ok(Map.of("paymentUrl", paymentUrl));
    }
}
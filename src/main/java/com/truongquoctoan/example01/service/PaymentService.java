package com.truongquoctoan.example01.service;

import com.truongquoctoan.example01.dto.PaymentRequest;
import java.util.Map;

public interface PaymentService {
    String createVNPayPayment(PaymentRequest request);

    boolean verifyVNPayCallback(Map<String, String> params);

    String createMoMoPayment(PaymentRequest request);
}   
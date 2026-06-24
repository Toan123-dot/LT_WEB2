package com.truongquoctoan.example01.service.impl;

import org.springframework.stereotype.Service;

import com.truongquoctoan.example01.dto.PaymentRequest;
import com.truongquoctoan.example01.service.PaymentService;

import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Override
    public String createVNPayPayment(PaymentRequest request) {
        // TODO: Implement VNPay API integration
        // Docs: https://sandbox.vnpayment.vn/apis/
        String vnpUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
        return vnpUrl + "?vnp_Amount=" + request.getAmount().multiply(new java.math.BigDecimal("100"));
    }

    @Override
    public boolean verifyVNPayCallback(Map<String, String> params) {
        // TODO: Verify VNPay signature with vnp_SecureHash
        return true; // Placeholder
    }

    @Override
    public String createMoMoPayment(PaymentRequest request) {
        // TODO: Implement MoMo API integration
        // Docs: https://developers.momo.vn/
        return "https://test-payment.momo.vn/pay?amount=" + request.getAmount();
    }
}
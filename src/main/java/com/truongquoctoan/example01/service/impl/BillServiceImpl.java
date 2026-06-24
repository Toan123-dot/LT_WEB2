package com.truongquoctoan.example01.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.truongquoctoan.example01.dto.BillDTO;
import com.truongquoctoan.example01.entity.Bill;
import com.truongquoctoan.example01.entity.Order;
import com.truongquoctoan.example01.repository.BillRepository;
import com.truongquoctoan.example01.repository.OrderRepository;
import com.truongquoctoan.example01.service.BillService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final OrderRepository orderRepository;

    @Override
    public List<Bill> getAll() {
        return billRepository.findAll();
    }

    @Override
    public Bill getById(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Bill not found with id: " + id));
    }

    @Override
    public Bill create(BillDTO dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("❌ Order not found with id: " + dto.getOrderId()));

        Bill bill = Bill.builder()
                .order(order)
                .totalAmount(dto.getTotalAmount() != null ? dto.getTotalAmount() : BigDecimal.ZERO)
                .paymentMethod(Bill.PaymentMethod.valueOf(dto.getPaymentMethod()))
                .paymentStatus(Bill.PaymentStatus.valueOf(dto.getPaymentStatus()))
                .issuedAt(LocalDateTime.now())
                .notes(dto.getNotes())
                .build();

        return billRepository.save(bill);
    }

    @Override
    public ByteArrayInputStream exportPdf(Long billId) {
        Bill bill = getById(billId);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, out);
            document.open();

            // Header
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("☕ COFFEE BILL", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Bill ID: " + bill.getId()));
            document.add(new Paragraph("Order ID: " + bill.getOrder().getId()));
            document.add(new Paragraph("Total: " + bill.getTotalAmount() + " VND"));
            document.add(new Paragraph("Payment Method: " + bill.getPaymentMethod()));
            document.add(new Paragraph("Payment Status: " + bill.getPaymentStatus()));
            document.add(new Paragraph("Issued At: " + bill.getIssuedAt()));
            if (bill.getNotes() != null && !bill.getNotes().isEmpty())
                document.add(new Paragraph("Notes: " + bill.getNotes()));

            document.close();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("❌ Error exporting PDF: " + e.getMessage());
        }
    }
}
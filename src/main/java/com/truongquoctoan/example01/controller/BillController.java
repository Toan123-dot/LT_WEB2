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

@RestController // Định nghĩa lớp này là một REST Controller, các hàm bên trong tự động trả dữ liệu JSON/Stream
@RequestMapping("/api/bills") // Đường dẫn gốc cho tất cả các API quản lý hóa đơn
@RequiredArgsConstructor // Tự động tạo Constructor cho thuộc tính 'final' để Spring ép dữ liệu (Dependency Injection)
@CrossOrigin(origins = "http://localhost:3000") // ⚠️ CẤU HÌNH CORS: Cho phép cổng 3000 gọi API này (Xem lưu ý bên dưới để sửa sang 5173)
public class BillController {

    private final BillService billService; // Gọi đến tầng Service xử lý logic nghiệp vụ hóa đơn

    /**
     * 📋 API LẤY DANH SÁCH TẤT CẢ HÓA ĐƠN
     * URL: GET http://localhost:8080/api/bills
     */
    @GetMapping
    public List<Bill> getAll() {
        return billService.getAll(); // Trả về mảng JSON chứa toàn bộ danh sách hóa đơn trong hệ thống
    }

    /**
     * ➕ API TẠO MỚI MỘT HÓA ĐƠN (KHI KHÁCH ĐẶT MÓN / THANH TOÁN)
     * URL: POST http://localhost:8080/api/bills
     */
    @PostMapping
    public Bill create(@RequestBody BillDTO dto) {
        // @RequestBody giúp hứng dữ liệu JSON từ Frontend gửi lên và tự động chuyển thành Object BillDTO
        return billService.create(dto); // Trả về thông tin hóa đơn vừa được tạo thành công
    }

    /**
     * 📄 API IN/XUẤT FILE PDF HÓA ĐƠN THEO ID
     * URL: GET http://localhost:8080/api/bills/{id}/export
     */
    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> export(@PathVariable Long id) {
        // @PathVariable giúp lấy trực tiếp giá trị {id} trên thanh URL truyền vào tham số hàm
        
        // Sử dụng cấu trúc Try-with-resources để tự động đóng luồng Stream (bis.close()) sau khi xử lý xong, tránh rò rỉ bộ nhớ
        try (ByteArrayInputStream bis = billService.exportPdf(id)) {
            
            // Đọc toàn bộ dữ liệu từ luồng dữ liệu byte sang một mảng byte thuần để chuẩn bị gửi đi
            byte[] pdfBytes = bis.readAllBytes();
            
            // Tạo phản hồi HTTP đặc biệt chứa file thay vì JSON thông thường
            return ResponseEntity.ok()
                    // Cấu hình Header "Content-Disposition" với thuộc tính "attachment" 
                    // Nhằm ép trình duyệt Frontend khi nhận được phải tự động TẢI FILE XUỐNG với tên là bill-{id}.pdf
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bill-" + id + ".pdf")
                    // Cấu hình định dạng dữ liệu trả về là file PDF chuẩn (application/pdf)
                    .contentType(MediaType.APPLICATION_PDF)
                    // Đổ mảng byte dữ liệu file PDF vào phần thân (Body) của phản hồi
                    .body(pdfBytes);
                    
        } catch (IOException e) {
            // Quăng lỗi Runtime nếu quá trình đọc luồng dữ liệu hoặc tạo file PDF gặp sự cố kĩ thuật
            throw new RuntimeException("Failed to export PDF for bill " + id, e);
        }
    }
}
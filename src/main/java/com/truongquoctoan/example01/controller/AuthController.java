package com.truongquoctoan.example01.controller;

import com.truongquoctoan.example01.dto.request.LoginRequest;
import com.truongquoctoan.example01.dto.request.SignupRequest;
import com.truongquoctoan.example01.dto.response.JwtResponse;
import com.truongquoctoan.example01.entity.User;
import com.truongquoctoan.example01.repository.UserRepository;
import com.truongquoctoan.example01.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController // Định nghĩa đây là một API Controller trả về dữ liệu dạng JSON/XML
@RequestMapping("/api/auth") // Cấu hình đường dẫn gốc cho toàn bộ các API bên trong class này
@RequiredArgsConstructor // Tự động tạo Constructor cho các thuộc tính khai báo 'final' (Dependency Injection của Lombok)
public class AuthController {

    private final UserRepository userRepository;       // Giao tiếp với Cơ sở dữ liệu bảng User
    private final PasswordEncoder passwordEncoder;   // Công cụ mã hóa mật khẩu (BCrypt)
    private final JwtUtils jwtUtils;                 // Công cụ tạo và cấu hình mã bảo mật JWT Token

    /**
     * 📥 API ĐĂNG KÝ TÀI KHOẢN MỚI
     * URL: POST http://localhost:8080/api/auth/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        
        // 1. Kiểm tra xem Tên đăng nhập (Username) đã có ai sử dụng trong DB chưa
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Username đã tồn tại");
            return ResponseEntity.badRequest().body(error); // Trả về lỗi 400 Bad Request kèm thông báo JSON
        }

        // 2. Phân quyền: Nếu phía Frontend không truyền quyền (role), hệ thống tự gán mặc định là USER
        User.Role userRole = request.getRole() != null ? request.getRole() : User.Role.USER;

        // 3. Sử dụng Builder Pattern để ánh xạ dữ liệu từ Request DTO vào Object Entity User
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // ⚠️ BẮT BUỘC: Mã hóa mật khẩu trước khi lưu
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(userRole)
                .isActive(true) // Tài khoản mới mặc định ở trạng thái kích hoạt luôn
                .build();

        // 4. Thực hiện lưu thông tin xuống Database
        User saved = userRepository.save(user);

        // Ghi Log log ra màn hình Console của Spring Boot để tiện theo dõi
        System.out.println("✅ [SIGNUP] Created user: " + saved.getUsername() + " | Role: " + saved.getRole());

        // 5. Tạo cấu trúc dữ liệu phản hồi trả về thành công cho Frontend
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Đăng ký thành công");
        response.put("user", saved);

        return ResponseEntity.ok(response); // Trả về HTTP 200 OK kèm dữ liệu
    }

    /**
     * 🔐 API ĐĂNG NHẬP HỆ THỐNG (LẤY JWT TOKEN)
     * URL: POST http://localhost:8080/api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        
        // 1. Tìm kiếm User theo Username, nếu không thấy lập tức quăng lỗi ngắt luồng
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Kiểm tra mật khẩu: So sánh mật khẩu thô gửi lên với mật khẩu đã băm (hashed) trong DB
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // 💡 GÓC TỐI ƯU: Đổi sang trả về object JSON giống Signup thay vì chuỗi Text thô
            Map<String, String> error = new HashMap<>();
            error.put("message", "Sai tài khoản hoặc mật khẩu");
            return ResponseEntity.badRequest().body(error);
        }

        // 3. Lấy tên Quyền của User ra dưới dạng chuỗi kí tự chữ (ví dụ: "ADMIN")
        String role = user.getRole().name();
        System.out.println("🔐 [LOGIN] User: " + user.getUsername() + " | Role: " + role);

        // 4. Sử dụng công cụ JwtUtils để tiến hành "ký và sinh mã" chuỗi Token bảo mật dựa trên Username và Role
        String token = jwtUtils.generateToken(user.getUsername(), role);

        // 5. Trả về Object JwtResponse chứa đầy đủ Token và thông tin cơ bản cho Frontend lưu trữ
        return ResponseEntity.ok(new JwtResponse(
                token,
                user.getId(),
                user.getUsername(),
                role));
    }
}
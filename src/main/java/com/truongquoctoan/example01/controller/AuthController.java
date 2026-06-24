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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        // ✅ Check username đã tồn tại chưa
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Username đã tồn tại");
            return ResponseEntity.badRequest().body(error);
        }

        // ✅ Tạo user với role (mặc định là USER nếu không truyền)
        User.Role userRole = request.getRole() != null ? request.getRole() : User.Role.USER;

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(userRole)
                .isActive(true)
                .build();

        User saved = userRepository.save(user);

        System.out.println("✅ [SIGNUP] Created user: " + saved.getUsername() + " | Role: " + saved.getRole());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Đăng ký thành công");
        response.put("user", saved);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid password");
        }

        String role = user.getRole().name(); // ADMIN, EMPLOYEE, STAFF, USER
        System.out.println("🔐 [LOGIN] User: " + user.getUsername() + " | Role: " + role);

        String token = jwtUtils.generateToken(user.getUsername(), role);

        return ResponseEntity.ok(new JwtResponse(
                token,
                user.getId(),
                user.getUsername(),
                role));
    }
}
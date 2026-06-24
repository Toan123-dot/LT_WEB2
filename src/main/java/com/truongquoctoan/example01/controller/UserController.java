package com.truongquoctoan.example01.controller;

import com.truongquoctoan.example01.entity.User;
import com.truongquoctoan.example01.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ✅ ADMIN tạo EMPLOYEE/ADMIN/STAFF
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "⚠️ Username đã tồn tại"));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);
        return ResponseEntity.ok(saved);
    }

    // ✅ ADMIN xem tất cả users
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // ✅ ADMIN xem chi tiết user
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }

    // ✅ ADMIN xóa user
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ ADMIN cập nhật thông tin user (bao gồm status)
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Cập nhật các trường nếu có trong request
        if (updates.containsKey("fullName")) {
            user.setFullName((String) updates.get("fullName"));
        }
        if (updates.containsKey("email")) {
            user.setEmail((String) updates.get("email"));
        }
        if (updates.containsKey("phone")) {
            user.setPhone((String) updates.get("phone"));
        }
        if (updates.containsKey("address")) {
            user.setAddress((String) updates.get("address"));
        }
        if (updates.containsKey("isActive")) {
            user.setIsActive((Boolean) updates.get("isActive"));
        }
        if (updates.containsKey("role")) {
            user.setRole(User.Role.valueOf((String) updates.get("role")));
        }

        User updated = userRepository.save(user);
        return ResponseEntity.ok(updated);
    }
}
package com.truongquoctoan.example01.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String fullName;
    private String email;
    private String phone;
    private String imageUrl;
    private String address; // ✅ Thêm address

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder.Default
    private Boolean isActive = true;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // ✅ Thêm để hiển thị ngày tham gia

    public enum Role {
        ADMIN,
        EMPLOYEE,
        STAFF, // ✅ Thêm STAFF
        USER
    }
}
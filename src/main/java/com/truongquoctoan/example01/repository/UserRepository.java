package com.truongquoctoan.example01.repository;

import com.truongquoctoan.example01.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    // Kiểm tra username khi tạo tài khoản
    boolean existsByUsername(String username);

    // Lấy danh sách khách hàng
    List<User> findByRole(User.Role role);

    // Lấy khách hàng, sắp xếp mới nhất trước
    List<User> findByRoleOrderByIdDesc(User.Role role);

    // Chỉ lấy khách hàng đang hoạt động
    List<User> findByRoleAndIsActiveTrueOrderByIdDesc(
            User.Role role
    );
}
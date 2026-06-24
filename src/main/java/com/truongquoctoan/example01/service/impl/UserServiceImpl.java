package com.truongquoctoan.example01.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.truongquoctoan.example01.dto.UserDto;
import com.truongquoctoan.example01.entity.User;
import com.truongquoctoan.example01.repository.UserRepository;
import com.truongquoctoan.example01.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(UserDto dto) {
        User user = User.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .fullName(dto.getFullName())
                .role(dto.getRole())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .imageUrl(dto.getImageUrl())
                .isActive(true)
                .build();
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
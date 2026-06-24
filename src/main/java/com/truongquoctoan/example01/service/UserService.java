package com.truongquoctoan.example01.service;

import com.truongquoctoan.example01.dto.UserDto;
import com.truongquoctoan.example01.entity.User;

import java.util.List;

public interface UserService {
    User createUser(UserDto dto);

    List<User> getAllUsers();

    User getUserById(Long id);
}
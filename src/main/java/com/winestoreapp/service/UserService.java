package com.winestoreapp.service;

import com.winestoreapp.dto.user.UserRegistrationRequestDto;
import com.winestoreapp.dto.user.UserResponseDto;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request);

    UserResponseDto updateRole(Long userId, String role);
}

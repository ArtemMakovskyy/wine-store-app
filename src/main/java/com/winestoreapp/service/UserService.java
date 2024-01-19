package com.winestoreapp.service;

import com.winestoreapp.dto.user.UserRegistrationRequestDto;
import com.winestoreapp.dto.user.UserResponseDto;
import org.springframework.security.core.Authentication;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request);

    UserResponseDto getUserFromAuthentication(Authentication authentication);

    UserResponseDto updateRole(Long userId, String role);

    UserResponseDto updateInfo(
            Authentication authentication, UserRegistrationRequestDto requestDto);
}

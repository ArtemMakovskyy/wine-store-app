package com.winestoreapp.controller;

import com.winestoreapp.dto.user.UserLoginRequestDto;
import com.winestoreapp.dto.user.UserLoginResponseDto;
import com.winestoreapp.dto.user.UserRegistrationRequestDto;
import com.winestoreapp.dto.user.UserResponseDto;
import com.winestoreapp.exception.RegistrationException;
import com.winestoreapp.security.AuthenticationService;
import com.winestoreapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Management authentication", description = "Endpoints to login and register")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "Registration of a new user.",
            description = "Save your: email, password,first name, "
                    + "last name")
    @PostMapping("/register")
    public UserResponseDto registerUser(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }

    @Operation(summary = "Registered user login.",
            description = "Input email address and password to login.")
    @PostMapping("/login")
    public UserLoginResponseDto loginUser(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }

    @Operation(summary = "Logout user.",
            description = "Logout user. Disable current token")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest httpRequest) {
        authenticationService.logout(httpRequest);
        return ResponseEntity.ok("Logout successful");
    }
}

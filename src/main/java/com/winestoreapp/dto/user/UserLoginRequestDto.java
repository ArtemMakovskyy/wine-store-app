package com.winestoreapp.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
        @Email(regexp = ".{5,20}@(\\S+)$",
                message = "length must be from 5 characters to 20 before @")
        @Schema(example = "customer@email.com")
        String email,
        @Size(min = 4, max = 20, message = "must be from 4 to 20 characters")
        String password) {
}

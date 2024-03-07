package com.winestoreapp.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    @Schema(example = "customer@email.com")
    private String email;
    @Schema(example = "firstName")
    private String firstName;
    @Schema(example = "lastName")
    private String lastName;
    @Size(min = 10, max = 10, message = "Phone number must be 10 digits. Like 0509876543")
    private String phoneNumber;
    @Schema(example = "ROLE_CUSTOMER | ROLE_MANAGER | ROLE_ADMIN")
    private Set<String> roles;
}

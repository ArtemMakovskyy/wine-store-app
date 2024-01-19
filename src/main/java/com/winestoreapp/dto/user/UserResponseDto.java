package com.winestoreapp.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(example = "ROLE_CUSTOMER | ROLE_MANAGER | ROLE_ADMIN")
    private Set<String> roles;
}

package com.winestoreapp.dto.user;

import com.winestoreapp.validation.ValidUserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserRoleDto(
        @ValidUserRole
        @NotBlank(message = """
                Role should not be blank.
                Available roles: ROLE_CUSTOMER, ROLE_MANAGER, ROLE_ADMIN.""")
        @Schema(example = "ROLE_MANAGER")
        String role) {
}

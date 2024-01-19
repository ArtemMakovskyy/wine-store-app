package com.winestoreapp.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserResponseWithChatIdDto {
    private Long id;
    @Schema(example = "customer@email.com")
    private String email;
    @Schema(example = "firstName")
    private String firstName;
    @Schema(example = "lastName")
    private String lastName;
    private Long telegramChatId;
}

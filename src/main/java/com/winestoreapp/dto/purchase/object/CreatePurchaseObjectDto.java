package com.winestoreapp.dto.purchase.object;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePurchaseObjectDto {
    @NotNull(message = "Please enter wine ID")
    @Schema(example = "2")
    private Long wineId;
    @NotNull(message = "Please enter quantity")
    @Schema(example = "1")
    private Integer quantity;
}

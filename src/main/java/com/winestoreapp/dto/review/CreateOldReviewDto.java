package com.winestoreapp.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOldReviewDto {
    @NotNull(message = "Please enter wine ID")
    @Schema(example = "1")
    private Long wineId;
    @NotNull(message = "Please enter user ID")
    @Schema(example = "2")
    private Long userId;
    @Schema(example = "This is a great wine!")
    private String message;
    @Schema(example = "Enter your rating from 1 to 5")
    @Min(value = 1, message = "must be greater then 0")
    @Max(value = 5, message = "must be less then 6")
    private Integer rating;
}

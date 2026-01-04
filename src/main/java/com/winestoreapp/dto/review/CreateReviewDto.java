package com.winestoreapp.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class CreateReviewDto {
    @NotNull(message = "Please enter wine ID")
    @Schema(example = "1")
    private Long wineId;
    @NotNull(message = "Please input users first and last names")
    @Schema(example = "Ivan Petrov")
    private String userFirstAndLastName;
    @Schema(example = "This is a great wine!")
    @Size(min = 5, max = 255, message = "The message must be between 5 and 255 characters")
    @NotNull(message = "The review shouldn't be empty")
    private String message;
    @Schema(example = "Enter your rating from 1 to 5")
    @Min(value = 1, message = "must be greater then 0")
    @Max(value = 5, message = "must be less then 6")
    private Integer rating;
}

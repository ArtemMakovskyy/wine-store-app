package com.winestoreapp.dto.review;

import lombok.Data;

@Data
public class CreateReviewDto {
    private Long wineId;
    private Long userId;
    private String message;
    private Integer rating;
}

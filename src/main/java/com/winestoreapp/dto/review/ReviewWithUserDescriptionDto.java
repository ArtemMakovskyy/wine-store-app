package com.winestoreapp.dto.review;

import java.time.LocalDate;
import lombok.Data;

@Data
public class ReviewWithUserDescriptionDto {
    private Long id;
    private String userFirstName;
    private String userLastName;
    private String message;
    private Integer rating;
    private LocalDate reviewDate;
}

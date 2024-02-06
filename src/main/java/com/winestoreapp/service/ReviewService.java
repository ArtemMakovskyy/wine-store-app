package com.winestoreapp.service;

import com.winestoreapp.dto.review.CreateReviewDto;
import com.winestoreapp.dto.review.ReviewDto;
import com.winestoreapp.dto.review.ReviewWithUserDescriptionDto;
import java.util.List;

public interface ReviewService {
    ReviewDto addReview(CreateReviewDto createDto);

    List<ReviewWithUserDescriptionDto> findAllByWineId(Long wineId);
}

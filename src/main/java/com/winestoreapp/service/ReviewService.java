package com.winestoreapp.service;

import com.winestoreapp.dto.review.CreateReviewDto;
import com.winestoreapp.dto.review.ReviewWithUserDescriptionDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewWithUserDescriptionDto addReview(CreateReviewDto createDto);

    List<ReviewWithUserDescriptionDto> findAllByWineId(Long wineId, Pageable pageable);
}

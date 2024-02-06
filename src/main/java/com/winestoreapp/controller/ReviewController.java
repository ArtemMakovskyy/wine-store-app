package com.winestoreapp.controller;

import com.winestoreapp.dto.review.CreateReviewDto;
import com.winestoreapp.dto.review.ReviewDto;
import com.winestoreapp.dto.review.ReviewWithUserDescriptionDto;
import com.winestoreapp.service.ReviewService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ReviewDto addReview(@RequestBody CreateReviewDto createDto) {
        return reviewService.addReview(createDto);
    }

    @GetMapping("/wine/{wineId}")
    public List<ReviewWithUserDescriptionDto> findAllReviewsByWineId(
            @PathVariable Long wineId) {
        System.out.println(wineId);
        return reviewService.findAllByWineId(wineId);
    }
}

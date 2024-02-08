package com.winestoreapp.controller;

import com.winestoreapp.dto.review.CreateReviewDto;
import com.winestoreapp.dto.review.ReviewDto;
import com.winestoreapp.dto.review.ReviewWithUserDescriptionDto;
import com.winestoreapp.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Review management", description = "Endpoints to managing reviews")
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @Operation(summary = "Add review to wine.",
            description = """
                    Adds a review to wine from a specific User. A specific user can't leave more
                     than one review of one kind of wine. If a review already exists, an earlier 
                     review with a rating is deleted, new adds.""")
    @PostMapping
    public ReviewDto addReview(@RequestBody @Valid CreateReviewDto createDto) {
        return reviewService.addReview(createDto);
    }

    @Operation(summary = "Find all reviews by wine id.",
            description = """
                    Find all reviews by wine id, sort by reviewDate.DESC, size = 4, page = 0""")
    @GetMapping("/wine/{wineId}")
    public List<ReviewWithUserDescriptionDto> findAllReviewsByWineId(
            @PathVariable Long wineId,
            @PageableDefault(size = 4, page = 0, sort = {"reviewDate"},
                    direction = Sort.Direction.DESC)
                    Pageable pageable) {
        return reviewService.findAllByWineId(wineId, pageable);
    }
}


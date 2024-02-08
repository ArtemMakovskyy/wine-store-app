package com.winestoreapp.service.impl;

import com.winestoreapp.dto.mapper.ReviewMapper;
import com.winestoreapp.dto.review.CreateReviewDto;
import com.winestoreapp.dto.review.ReviewDto;
import com.winestoreapp.dto.review.ReviewWithUserDescriptionDto;
import com.winestoreapp.model.Review;
import com.winestoreapp.model.Wine;
import com.winestoreapp.repository.ReviewRepository;
import com.winestoreapp.repository.UserRepository;
import com.winestoreapp.repository.WineRepository;
import com.winestoreapp.service.ReviewService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserRepository userRepository;
    private final WineRepository wineRepository;
    @Value("${limiter.number.of.recorded.ratings}")
    private int limiterOnTheNumberOfRecordedRatings;

    @Override
    @Transactional
    public ReviewDto addReview(CreateReviewDto createDto) {
        if (removeOutdatedReviews(createDto.getWineId(), createDto.getUserId())) {
            log.info("Outdated reviews were deleted");
        }
        Review review = reviewMapper.createDtoToEntity(createDto);
        review.setReviewDate(LocalDateTime.now());
        review.setUser(userRepository.findById(createDto.getUserId()).get());
        Wine wine = wineRepository.findById(createDto.getWineId()).get();
        review.setWine(wine);
        review.setReviewDate(LocalDateTime.now());
        final ReviewDto reviewDto = reviewMapper.toDto(reviewRepository.save(review));
        System.out.println(reviewDto);
        calculateWineAverageRatingScoreThenSave(createDto.getWineId());
        return reviewDto;
    }

    @Override
    public List<ReviewWithUserDescriptionDto> findAllByWineId(Long wineId, Pageable pageable) {
        return reviewRepository.findAllByWineIdOrderByIdDesc(wineId, pageable).stream()
                .map(reviewMapper::toUserDescriptionDto)
                .toList();
    }

    private boolean removeOutdatedReviews(Long wineId, Long userId) {
        final List<Review> allByWineIdAndUserId
                = reviewRepository.findAllByWineIdAndUserId(
                wineId, userId);
        if (!allByWineIdAndUserId.isEmpty()) {
            for (Review review : allByWineIdAndUserId) {
                reviewRepository.deleteById(review.getId());
            }
        }
        return true;
    }

    private void calculateWineAverageRatingScoreThenSave(Long wineId) {
        final List<Review> allByWineId = reviewRepository.findAllByWineId(wineId);
        if (allByWineId.size() > limiterOnTheNumberOfRecordedRatings) {
            reviewRepository.deleteById(reviewRepository.findMinIdByWineId(wineId));
        }
        double averageRatingByWineId = reviewRepository.findAverageRatingByWineId(wineId)
                + ((double)allByWineId.size() / limiterOnTheNumberOfRecordedRatings);
        wineRepository.updateAverageRatingScore(wineId, averageRatingByWineId);
    }
}

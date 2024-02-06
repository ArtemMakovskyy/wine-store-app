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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    // TODO: 07.02.2024 is it correct?
    @Transactional
    public ReviewDto addReview(CreateReviewDto createDto) {
        System.out.println(createDto);
        final Review review = reviewMapper.createDtoToEntity(createDto);
        review.setReviewDate(LocalDate.now());
        review.setUser(userRepository.findById(createDto.getUserId()).get());
        final Wine wine = wineRepository.findById(createDto.getWineId()).get();
        review.setWine(wine);
        review.setReviewDate(LocalDate.now());
        // TODO: 07.02.2024 clean redundant
        // TODO: 07.02.2024 save average
        // TODO: 07.02.2024 change format in db on DATETIME / TIMESTAMP
        System.out.println(LocalDateTime.now());
        final ReviewDto reviewDto = reviewMapper.toDto(reviewRepository.save(review));
        System.out.println(reviewDto);
        calculateWineAverageRatingScoreThenSave(createDto.getUserId(), createDto.getWineId());
        return reviewDto;
    }

    @Override
    public List<ReviewWithUserDescriptionDto> findAllByWineId(Long wineId) {
        return reviewRepository.findAllByWineIdOrderById(wineId).stream()
                .map(reviewMapper::toUserDescriptionDto)
                .toList();
    }

    private void calculateWineAverageRatingScoreThenSave(Long userId, Long wineId) {
        final long reviewsQuantity = reviewRepository.count();
        if (reviewsQuantity >= limiterOnTheNumberOfRecordedRatings) {
            final Long minIdByUserIdAndWineId = reviewRepository
                    .findMinIdByUserIdAndWineId(userId, wineId);
            reviewRepository.deleteById(minIdByUserIdAndWineId);
            log.info("Added new rating and deleted old with id: " + minIdByUserIdAndWineId);
        }
        final Double averageRatingByWineIdAndUserId =
                reviewRepository.findAverageRatingByWineIdAndUserId(wineId, userId)
                        + ((double) reviewsQuantity / limiterOnTheNumberOfRecordedRatings);
        wineRepository.updateAverageRatingScore(userId, averageRatingByWineIdAndUserId);
    }
}

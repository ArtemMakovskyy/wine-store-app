package com.winestoreapp.service.impl;

import com.winestoreapp.dto.mapper.ReviewMapper;
import com.winestoreapp.dto.review.CreateReviewDto;
import com.winestoreapp.dto.review.ReviewWithUserDescriptionDto;
import com.winestoreapp.exception.EmptyDataException;
import com.winestoreapp.exception.RegistrationException;
import com.winestoreapp.model.Review;
import com.winestoreapp.model.User;
import com.winestoreapp.model.Wine;
import com.winestoreapp.repository.ReviewRepository;
import com.winestoreapp.repository.UserRepository;
import com.winestoreapp.repository.WineRepository;
import com.winestoreapp.service.ReviewService;
import jakarta.transaction.Transactional;
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
    private int limiter;

    @Override
    @Transactional
    public ReviewWithUserDescriptionDto addReview(CreateReviewDto dto) {
        String[] nameParts = dto.getUserFirstAndLastName().strip().split("\\s+");
        if (nameParts.length != 2) {
            throw new RegistrationException("Enter first and last name with a space.");
        }

        User user = userRepository.findFirstByFirstNameAndLastName(nameParts[0], nameParts[1])
                .orElseGet(() -> userRepository.save(new User(nameParts[0], nameParts[1])));

        removeOutdatedReviews(dto.getWineId(), user.getId());

        Wine wine = wineRepository.findById(dto.getWineId())
                .orElseThrow(() -> new EmptyDataException("Wine not found"));

        Review review = new Review(user, wine, dto.getMessage(), dto.getRating());
        Review saved = reviewRepository.save(review);

        calculateWineAverageRatingScoreThenSave(dto.getWineId());
        return reviewMapper.toUserDescriptionDto(saved);
    }

    @Override
    public List<ReviewWithUserDescriptionDto> findAllByWineId(Long wineId, Pageable pageable) {
        return reviewRepository.findAllByWineIdOrderByIdDesc(wineId, pageable).stream()
                .map(reviewMapper::toUserDescriptionDto).toList();
    }

    private void removeOutdatedReviews(Long wineId, Long userId) {
        reviewRepository.findAllByWineIdAndUserId(wineId, userId)
                .forEach(r -> reviewRepository.deleteById(r.getId()));
    }

    private void calculateWineAverageRatingScoreThenSave(Long wineId) {
        List<Review> reviews = reviewRepository.findAllByWineId(wineId);
        if (reviews.size() > limiter) {
            reviewRepository.deleteById(reviewRepository.findMinIdByWineId(wineId));
        }
        double avg = reviewRepository.findAverageRatingByWineId(wineId) + ((double) reviews.size() / limiter);
        wineRepository.updateAverageRatingScore(wineId, avg);
    }
}
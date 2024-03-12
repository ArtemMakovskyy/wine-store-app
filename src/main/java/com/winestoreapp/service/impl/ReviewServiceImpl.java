package com.winestoreapp.service.impl;

import com.winestoreapp.dto.mapper.ReviewMapper;
import com.winestoreapp.dto.review.CreateReviewDto;
import com.winestoreapp.dto.review.ReviewWithUserDescriptionDto;
import com.winestoreapp.exception.EmptyDataException;
import com.winestoreapp.exception.RegistrationException;
import com.winestoreapp.model.Review;
import com.winestoreapp.model.User;
import com.winestoreapp.repository.ReviewRepository;
import com.winestoreapp.repository.UserRepository;
import com.winestoreapp.repository.WineRepository;
import com.winestoreapp.service.ReviewService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private static final int USER_FIRST_NAME_INDEX = 0;
    private static final int USER_LAST_NAME_INDEX = 1;
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserRepository userRepository;
    private final WineRepository wineRepository;
    @Value("${limiter.number.of.recorded.ratings}")
    private int limiterOnTheNumberOfRecordedRatings;

    @Override
    @Transactional
    public ReviewWithUserDescriptionDto addReview(CreateReviewDto createDto) {
        final String[] userFirstAndLastName
                = createDto.getUserFirstAndLastName()
                .strip()
                .split("\\s+");
        if (userFirstAndLastName.length != 2) {
            throw new RegistrationException(
                    "You should enter your first and last name with a space between them");
        }
        final Optional<User> foundUserByFirstNameAndLastName
                = userRepository.findFirstByFirstNameAndLastName(
                userFirstAndLastName[USER_FIRST_NAME_INDEX],
                userFirstAndLastName[USER_LAST_NAME_INDEX]);

        User user = null;
        if (foundUserByFirstNameAndLastName.isEmpty()) {
            user = userRepository.save(
                    new User(userFirstAndLastName[USER_FIRST_NAME_INDEX],
                            userFirstAndLastName[USER_LAST_NAME_INDEX]));
        }

        user = foundUserByFirstNameAndLastName.orElse(user);

        if (removeOutdatedReviews(createDto.getWineId(), user.getId())) {
            log.info("into Outdated reviews were deleted");
        }

        Review review = new Review();
        review.setReviewDate(LocalDateTime.now());
        review.setUser(user);
        review.setMessage(createDto.getMessage());
        review.setWine(wineRepository.findById(createDto.getWineId()).orElseThrow(
                () -> new EmptyDataException("Can't get wine by id " + createDto.getWineId())));
        review.setRating(createDto.getRating());
        final Review savedReview = reviewRepository.save(review);
        calculateWineAverageRatingScoreThenSave(createDto.getWineId());
        return reviewMapper.toUserDescriptionDto(savedReview);
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
                + ((double) allByWineId.size() / limiterOnTheNumberOfRecordedRatings);
        wineRepository.updateAverageRatingScore(wineId, averageRatingByWineId);
    }
}

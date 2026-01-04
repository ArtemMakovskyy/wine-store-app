package com.winestoreapp.service.impl;

import com.winestoreapp.dto.mapper.ReviewMapper;
import com.winestoreapp.dto.review.CreateReviewDto;
import com.winestoreapp.dto.review.ReviewDto;
import com.winestoreapp.dto.review.ReviewWithUserDescriptionDto;
import com.winestoreapp.model.Review;
import com.winestoreapp.model.User;
import com.winestoreapp.model.Wine;
import com.winestoreapp.repository.ReviewRepository;
import com.winestoreapp.repository.UserRepository;
import com.winestoreapp.repository.WineRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private WineRepository wineRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    @DisplayName("Add review with valid data. Return ReviewWithUserDescriptionDto")
    void addReview_ValidData_ShouldReturnReviewWithUserDescriptionDto() {
        // given
        CreateReviewDto createReviewDto = getCreateReviewDto();
        User user = getUser();
        Wine wine = getWine();
        ReviewDto reviewDto = getReviewDto();
        Review review = getReview(reviewDto);
        ReviewWithUserDescriptionDto expected = getReviewWithUserDescriptionDto(reviewDto);

        // Повертаємо логіку: користувача спочатку немає, він створюється
        when(userRepository.findFirstByFirstNameAndLastName(anyString(), anyString()))
                .thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(wineRepository.findById(anyLong())).thenReturn(Optional.of(wine));

        // Мок для видалення старого відгуку (нова логіка)
        Review oldReview = new Review();
        ReflectionTestUtils.setField(oldReview, "id", 99L);
        when(reviewRepository.findAllByWineIdAndUserId(anyLong(), anyLong()))
                .thenReturn(List.of(oldReview));

        // Моки для розрахунку рейтингу вина (нова логіка)
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(reviewRepository.findAllByWineId(anyLong())).thenReturn(List.of(review));
        when(reviewRepository.findAverageRatingByWineId(anyLong())).thenReturn(4.5);
        when(reviewMapper.toUserDescriptionDto(any(Review.class))).thenReturn(expected);

        // when
        ReviewWithUserDescriptionDto actual = reviewService.addReview(createReviewDto);

        // then
        assertEquals(expected, actual);
        verify(userRepository).findFirstByFirstNameAndLastName(anyString(), anyString());
        verify(userRepository).save(any(User.class));
        verify(wineRepository).findById(anyLong());
        verify(reviewRepository).findAllByWineIdAndUserId(anyLong(), anyLong());
        verify(reviewRepository).deleteById(99L); // Перевірка видалення
        verify(reviewRepository).save(any(Review.class));
        verify(wineRepository).updateAverageRatingScore(anyLong(), anyDouble()); // Перевірка оновлення рейтингу
    }

    private User getUser() {
        User user = new User("Ivan", "Ivanov");
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }

    private Wine getWine() {
        Wine wine = new Wine();
        ReflectionTestUtils.setField(wine, "id", 1L);
        wine.setName("Wine Name");
        wine.setPrice(BigDecimal.TEN);
        return wine;
    }

    private Review getReview(ReviewDto reviewDto) {
        Review review = new Review(getUser(), getWine(), reviewDto.getMessage(), reviewDto.getRating());
        ReflectionTestUtils.setField(review, "id", 1L);
        return review;
    }

    private CreateReviewDto getCreateReviewDto() {
        CreateReviewDto dto = new CreateReviewDto();
        dto.setWineId(1L);
        dto.setRating(5);
        dto.setMessage("Good");
        dto.setUserFirstAndLastName("Ivan Ivanov");
        return dto;
    }

    private ReviewDto getReviewDto() {
        ReviewDto dto = new ReviewDto();
        dto.setId(1L);
        dto.setRating(5);
        dto.setMessage("Good");
        return dto;
    }

    private ReviewWithUserDescriptionDto getReviewWithUserDescriptionDto(ReviewDto reviewDto) {
        ReviewWithUserDescriptionDto dto = new ReviewWithUserDescriptionDto();
        dto.setId(reviewDto.getId());
        dto.setRating(reviewDto.getRating());
        dto.setMessage(reviewDto.getMessage());
        return dto;
    }
}

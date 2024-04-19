package com.winestoreapp.service.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.winestoreapp.dto.mapper.ReviewMapper;
import com.winestoreapp.dto.review.CreateReviewDto;
import com.winestoreapp.dto.review.ReviewDto;
import com.winestoreapp.dto.review.ReviewWithUserDescriptionDto;
import com.winestoreapp.model.Review;
import com.winestoreapp.model.User;
import com.winestoreapp.model.Wine;
import com.winestoreapp.model.WineColor;
import com.winestoreapp.model.WineType;
import com.winestoreapp.repository.ReviewRepository;
import com.winestoreapp.repository.UserRepository;
import com.winestoreapp.repository.WineRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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
        //given
        ReviewDto reviewDto = getReviewDto();
        final User user = getUser();
        CreateReviewDto createReviewDto = getCreateReviewDto();
        final Wine wine = getWine();
        Review review = getReview(reviewDto);
        ReviewWithUserDescriptionDto expected
                = getReviewWithUserDescriptionDto(reviewDto);

        Mockito.when(userRepository.findFirstByFirstNameAndLastName(
                        Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Optional.empty());
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        Mockito.when(reviewRepository.findAllByWineIdAndUserId(
                Mockito.anyLong(), Mockito.anyLong())).thenReturn(List.of(new Review()));
        Mockito.when(wineRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(wine));
        Mockito.when(reviewRepository.save(Mockito.any(Review.class))).thenReturn(review);
        Mockito.when(reviewMapper.toUserDescriptionDto(review)).thenReturn(expected);

        //when
        final ReviewWithUserDescriptionDto actual = reviewService.addReview(createReviewDto);

        //then
        Assertions.assertEquals(expected, actual);
        verify(userRepository, times(1))
                .findFirstByFirstNameAndLastName(Mockito.anyString(), Mockito.anyString());
        verify(userRepository, times(1)
        ).save(Mockito.any(User.class));
        verify(reviewRepository, times(1))
                .findAllByWineIdAndUserId(
                        Mockito.anyLong(), Mockito.anyLong());
        verify(wineRepository, times(1))
                .findById(Mockito.anyLong());
        verify(reviewRepository, times(1))
                .save(Mockito.any(Review.class));
        verify(reviewMapper, times(1)).toUserDescriptionDto(review);
    }

    private User getUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user.email.12345@email.com");
        user.setFirstName("Ivan");
        user.setLastName("Ivanov");
        user.setPhoneNumber("+380501234569");
        user.setDeleted(false);
        return user;
    }

    private Wine getWine() {
        Wine wine = new Wine();
        wine.setId(1L);
        wine.setVendorCode("MRD2019");
        wine.setQualityLevel("Select");
        wine.setReserveType(null);
        wine.setName("Prince Trubetskoi Select Riesling");
        wine.setShortName("Riesling");
        wine.setYear(2019);
        wine.setTasteWine("asian food");
        wine.setPrice(new BigDecimal("870"));
        wine.setGrape("Riesling");
        wine.setWineType(WineType.DRY);
        wine.setWineColor(WineColor.WHITE);
        return wine;
    }

    private ReviewWithUserDescriptionDto getReviewWithUserDescriptionDto(ReviewDto reviewDto) {
        ReviewWithUserDescriptionDto dto = new ReviewWithUserDescriptionDto();
        dto.setId(reviewDto.getId());
        dto.setReviewDate(reviewDto.getReviewDate());
        dto.setRating(reviewDto.getRating());
        dto.setMessage(reviewDto.getMessage());
        dto.setUserFirstName("FirstName");
        dto.setUserLastName("LastName");
        return dto;
    }

    private Review getReview(ReviewDto reviewDto) {
        Review review = new Review();
        review.setId(1L);
        review.setWine(getWine());
        review.setUser(getUser());
        review.setMessage(reviewDto.getMessage());
        review.setRating(reviewDto.getRating());
        review.setReviewDate(review.getReviewDate());
        return review;
    }

    private CreateReviewDto getCreateReviewDto() {
        CreateReviewDto dto = new CreateReviewDto();
        dto.setMessage("Any message");
        dto.setRating(5);
        dto.setWineId(1L);
        dto.setUserFirstAndLastName("FirstName LastName");
        return dto;
    }

    private ReviewDto getReviewDto() {
        ReviewDto dto = new ReviewDto();
        dto.setId(1L);
        dto.setWineId(1L);
        dto.setUserId(1L);
        dto.setMessage("Any message");
        dto.setReviewDate(LocalDateTime.now().toLocalDate());
        return dto;
    }
}

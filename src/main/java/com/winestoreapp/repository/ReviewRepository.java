package com.winestoreapp.repository;

import com.winestoreapp.model.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // TODO: 07.02.2024 doesn't use
    List<Review> findAllByUserIdAndWineId(Long userId, Long wineId);

    @Query("SELECT ROUND(AVG(r.rating), 2) AS averageRating "
            + "FROM Review r "
            + "WHERE r.wine.id = :wineId AND r.user.id = :userId AND r.isDeleted = false "
            + "GROUP BY r.wine.id, r.user.id")
    Double findAverageRatingByWineIdAndUserId(Long wineId, Long userId);

    @Query("""
            SELECT MIN(r.id) 
            FROM Review r 
            WHERE r.user.id = :userId AND r.wine.id = :wineId AND r.isDeleted = FALSE""")
    Long findMinIdByUserIdAndWineId(@Param("userId") Long userId, @Param("wineId") Long wineId);

    List<Review> findAllByWineIdOrderById(Long wineId);
}

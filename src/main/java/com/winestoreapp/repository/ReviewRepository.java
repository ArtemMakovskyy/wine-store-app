package com.winestoreapp.repository;

import com.winestoreapp.model.Review;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @EntityGraph(attributePaths = "user")
    List<Review> findAllByWineIdOrderByIdDesc(Long wineId, Pageable pageable);

    List<Review> findAllByWineId(Long wineId);

    List<Review> findAllByWineIdAndUserId(Long wineId, Long userId);

    @Query("SELECT ROUND(AVG(r.rating), 2) AS averageRating "
            + "FROM Review r "
            + "WHERE r.wine.id = :wineId AND r.isDeleted = false "
            + "GROUP BY r.wine.id")
    Double findAverageRatingByWineId(Long wineId);

    @Query("""
            SELECT MIN(r.id) 
            FROM Review r 
            WHERE r.wine.id = :wineId AND r.isDeleted = FALSE""")
    Long findMinIdByWineId(Long wineId);
}

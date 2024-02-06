package com.winestoreapp.repository;

import com.winestoreapp.model.Wine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface WineRepository extends JpaRepository<Wine, Long> {
    // TODO: 06.02.2024 implement lazy
    @Modifying
    @Query("UPDATE Wine w SET w.averageRatingScore = :averageRatingScore WHERE w.id = :wineId")
    void updateAverageRatingScore(Long wineId, Double averageRatingScore);

}

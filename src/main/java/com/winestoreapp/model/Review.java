package com.winestoreapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "reviews")
@Getter
@SQLDelete(sql = "UPDATE reviews SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted=false")
@NoArgsConstructor
public class Review {
    @Column(nullable = false)
    private final boolean isDeleted = false;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wine_id", nullable = false)
    private Wine wine;
    private String message;
    @Column(nullable = false)
    private int rating;
    @Column(nullable = false)
    private LocalDateTime reviewDate;

    public Review(User user, Wine wine, String message, int rating) {
        if (user == null || wine == null) {
            throw new IllegalArgumentException("User and Wine cannot be null");
        }
        this.user = user;
        this.wine = wine;
        this.message = message;
        this.rating = rating;
        this.reviewDate = LocalDateTime.now();
    }

    public Review(Long id, User user, Wine wine, String message, int rating) {
        this.id = id;
        this.user = user;
        this.wine = wine;
        this.message = message;
        this.rating = rating;
    }
}
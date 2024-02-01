package com.winestoreapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@SQLDelete(sql = "UPDATE wines SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted=false")
@Getter
@Setter
@Entity
@Table(name = "wines")
@ToString
public class Wine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vendor_code")
    private String vendorCode;

    @Column(nullable = false)
    private String name;

    @Column(name = "short_name")
    private String shortName;

    @OneToMany(
            mappedBy = "wine",
             fetch = FetchType.EAGER)
    private Set<Review> reviews;

    @Column(name = "average_rating_score")
    private BigDecimal averageRatingScore;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String grape;

    @Column(name = "is_decantation", nullable = false)
    private Boolean isDecantation;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "wine_type", nullable = false)
    private WineType wineType;

    @Column(name = "strength_from", nullable = false)
    private BigDecimal strengthFrom;

    @Column(name = "strength_to", nullable = false)
    private BigDecimal strengthTo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WineColor wineColor;

    @Column(name = "color_describing")
    private String colorDescribing;
    private String taste;
    private String aroma;
    private String gastronomy;
    private String description;

    @Lob
    @Column(name = "picture", columnDefinition = "LONGBLOB")
    private byte[] picture;

    private URL pictureLink;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

}

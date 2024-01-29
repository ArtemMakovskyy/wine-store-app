package com.winestoreapp.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

//@Getter
//@Setter
//@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE id=?")
//@Where(clause = "is_deleted=false")
//@Entity
//@Table(name = "wines")
public class Wine {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String grape;

    @Column(nullable = false)
    private Boolean decantation;

    //witch wine types we will use
    //Do we need to use some other types or should we not use constants?
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private WineType type;

    //10.6% - 12.9%  All wines will have borders of strength?
    private String strength;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private WineColor wineColor;

    private String colorDescribing;
    private String taste;
    private String aroma;
    private String gastronomy;

    @ElementCollection
    @CollectionTable(name = "wine_ratings", joinColumns = @JoinColumn(name = "wine_id"))
    @Column(name = "rating")
    private List<Integer> rating;
    private String description;
    private URL imageLink;
}

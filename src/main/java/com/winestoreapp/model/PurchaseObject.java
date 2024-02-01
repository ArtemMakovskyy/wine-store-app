package com.winestoreapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "purchase_objects")
@Getter
@Setter
@SQLDelete(sql = "UPDATE purchase_objects SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted=false")
public class PurchaseObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Wine wines;

    private Integer quantity;

    private BigDecimal price;

    @Column(nullable = false)
    private boolean isDeleted = false;
}

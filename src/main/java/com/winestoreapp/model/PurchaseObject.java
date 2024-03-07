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
import java.util.Objects;
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

    @ManyToOne(fetch = FetchType.LAZY)
    private Wine wine;

    @ManyToOne(fetch = FetchType.LAZY)
    private ShoppingCard shoppingCard;

    private Integer quantity;

    private BigDecimal price;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PurchaseObject that = (PurchaseObject) o;
        return Objects.equals(id, that.id)
                && Objects.equals(wine, that.wine)
                && Objects.equals(shoppingCard, that.shoppingCard)
                && Objects.equals(quantity, that.quantity)
                && Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, wine, shoppingCard, quantity, price);
    }
}

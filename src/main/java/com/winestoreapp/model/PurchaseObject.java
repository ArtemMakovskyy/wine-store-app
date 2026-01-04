package com.winestoreapp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "purchase_objects")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@SQLDelete(sql = "UPDATE purchase_objects SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted=false")
public class PurchaseObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private Wine wine;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private ShoppingCard shoppingCard;

    @Setter
    private Integer quantity;

    @Setter
    private BigDecimal price;

    @Column(nullable = false)
    private boolean isDeleted = false;

    public BigDecimal getSubtotal() {
        if (price == null || quantity == null) return BigDecimal.ZERO;
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}

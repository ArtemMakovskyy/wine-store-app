package com.winestoreapp.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "shopping_cards")
@Getter
@Setter
@SQLDelete(sql = "UPDATE shopping_cards SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted=false")
public class ShoppingCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "shopping_card_id")
    private Set<PurchaseObject> purchaseObjects;

    private BigDecimal totalCost;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}

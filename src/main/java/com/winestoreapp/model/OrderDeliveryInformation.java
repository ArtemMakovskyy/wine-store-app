package com.winestoreapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "orders_delivery_information")
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE orders_delivery_information SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted=false")
public class OrderDeliveryInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String zipCode;
    @Setter
    private String region;
    @Setter
    private String city;
    @Setter
    private String street;
    @Setter
    private String comment;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private boolean isDeleted = false;

    public void linkToOrder(Order order) {
        this.order = order;
    }
}

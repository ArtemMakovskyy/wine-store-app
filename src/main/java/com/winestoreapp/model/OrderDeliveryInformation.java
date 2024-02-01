package com.winestoreapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "orders_delivery_information")
@Getter
@Setter
@SQLDelete(sql = "UPDATE orders_delivery_information SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted=false")
public class OrderDeliveryInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String city;
    private String street;
    private Integer house;
    private Integer floor;
    private Integer apartment;
    private String phone;
    private String additionally;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id")
    @MapsId
    private Order order;

    @Column(nullable = false)
    private boolean isDeleted = false;
}
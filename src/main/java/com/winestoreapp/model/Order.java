package com.winestoreapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Random;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE orders SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted=false")
public class Order {
    private static final String ORDER_IDENTIFIER = "ORDER_";
    private static final int NUMBER_RANDOM_CHARACTERS = 3;
    private static final int ALPHABET_SIZE = 26;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Setter
    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    private ShoppingCard shoppingCard;

    @Setter
    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    private OrderDeliveryInformation deliveryInformation;

    private LocalDateTime registrationTime;
    private LocalDateTime completedTime;

    @Enumerated(EnumType.STRING)
    private OrderPaymentStatus paymentStatus;

    @Column(nullable = false)
    private boolean isDeleted = false;

    // --- Domain Logic ---

    public void initializeNewOrder(User user) {
        this.user = user;
        this.registrationTime = LocalDateTime.now();
        this.paymentStatus = OrderPaymentStatus.PENDING;
    }

    public void generateAndSetOrderNumber() {
        if (this.id == null) {
            throw new IllegalStateException("ID must be present to generate order number");
        }
        Random random = new Random();
        StringBuilder sb = new StringBuilder(ORDER_IDENTIFIER);
        for (int i = 0; i < NUMBER_RANDOM_CHARACTERS; i++) {
            sb.append((char) ('A' + random.nextInt(ALPHABET_SIZE)));
        }
        this.orderNumber = sb.append(this.id).toString();
    }

    public void markAsPaid() {
        this.paymentStatus = OrderPaymentStatus.PAID;
        this.completedTime = LocalDateTime.now();
    }
}
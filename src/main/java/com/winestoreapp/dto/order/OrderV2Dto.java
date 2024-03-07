package com.winestoreapp.dto.order;

import com.winestoreapp.model.OrderPaymentStatus;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class OrderV2Dto {
    private Long id;
    private Long userId;
    private Long shoppingCardId;
    private Long deliveryInformationId;
    private LocalDateTime registrationTime;
    private LocalDateTime completedTime;
    private OrderPaymentStatus paymentStatus;
}

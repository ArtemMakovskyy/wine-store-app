package com.winestoreapp.dto.order;

import com.winestoreapp.dto.order.delivery.information.CreateOrderDeliveryInformationDto;
import com.winestoreapp.dto.shopping.card.CreateShoppingCardDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class CreateOrderOldDto {
    @NotNull(message = "Please enter user ID ")
    @Schema(example = "2")
    private Long userId;
    private CreateShoppingCardDto createShoppingCardDto;
    private CreateOrderDeliveryInformationDto createOrderDeliveryInformationDto;
}

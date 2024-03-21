package com.winestoreapp.dto.order;

import com.winestoreapp.dto.order.delivery.information.CreateOrderDeliveryInformationDto;
import com.winestoreapp.dto.shopping.card.CreateShoppingCardDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateOrderDto {
    @NotNull(message = "Please input users first and last names")
    @Schema(example = "Ivan Petrov")
    private String userFirstAndLastName;

    @Email(regexp = ".{5,20}@(\\S+)$",
            message = "length must be from 5 characters to 20 before @")
    @Schema(example = "customer@email.com")
    private String email;

    @Size(min = 13, max = 13, message = "Phone number must be 13 symbols. Like +380509876543")
    @NotEmpty(message = "you should enter phone number like 0509876543")
    @Schema(example = "+380509876543")
    private String phoneNumber;

    private CreateShoppingCardDto createShoppingCardDto;
    private CreateOrderDeliveryInformationDto createOrderDeliveryInformationDto;
}

package com.winestoreapp.dto.order.delivery.information;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderDeliveryInformationDto {
    @NotNull(message = "Please enter zip code")
    @Schema(example = "00000")
    private String zipCode;
    @NotNull(message = "Please enter region")
    @Schema(example = "Kyiv region")
    private String region;
    @NotNull(message = "Please enter city")
    @Schema(example = "Kyiv")
    private String city;
    @NotNull(message = "Please street")
    @Schema(example = "Lobanovskogo str, 13/1, ap. 16")
    private String street;
    @NotNull(message = "Please provide any additional info")
    private String comment;
}

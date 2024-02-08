package com.winestoreapp.dto.order.delivery.information;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderDeliveryInformationDto {
    @NotNull(message = "Please enter city")
    @Schema(example = "Kyiv")
    private String city;
    @NotNull(message = "Please street")
    @Schema(example = "Khreshchatyk")
    private String street;
    @NotNull(message = "Please enter house number")
    @Schema(example = "25")
    private Integer house;
    @NotNull(message = "Please enter floor number")
    @Schema(example = "2")
    private Integer floor;
    @NotNull(message = "Please enter apartment number")
    @Schema(example = "25")
    private Integer apartment;
    @NotNull(message = "Please enter phone number")
    private String phone;
    @NotNull(message = "Please enter additionally information if it need")
    private String additionally;
}

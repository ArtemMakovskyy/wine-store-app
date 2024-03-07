package com.winestoreapp.dto.purchase.object;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class PurchaseObjectDto {
    private Long wineId;
    private String winePictureLink;
    private String wineName;
    private Integer quantity;
    private BigDecimal price;
}

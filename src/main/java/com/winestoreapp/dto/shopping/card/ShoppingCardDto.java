package com.winestoreapp.dto.shopping.card;

import com.winestoreapp.dto.purchase.object.PurchaseObjectDto;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;

@Data
public class ShoppingCardDto {
    private Long id;
    private Set<PurchaseObjectDto> purchaseObjects;
    private BigDecimal totalCost;
}

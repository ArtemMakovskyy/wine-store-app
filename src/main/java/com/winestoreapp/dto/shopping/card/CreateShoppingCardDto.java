package com.winestoreapp.dto.shopping.card;

import com.winestoreapp.dto.purchase.object.CreatePurchaseObjectDto;
import java.util.Set;
import lombok.Data;

@Data
public class CreateShoppingCardDto {
    private Set<CreatePurchaseObjectDto> purchaseObjects;
}

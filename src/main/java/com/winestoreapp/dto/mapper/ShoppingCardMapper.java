package com.winestoreapp.dto.mapper;

import com.winestoreapp.config.MapperConfig;
import com.winestoreapp.dto.shopping.card.ShoppingCardDto;
import com.winestoreapp.model.ShoppingCard;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(config = MapperConfig.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = PurchaseObjectMapper.class)
public interface ShoppingCardMapper {

    ShoppingCardDto toDto(ShoppingCard shoppingCard);
}

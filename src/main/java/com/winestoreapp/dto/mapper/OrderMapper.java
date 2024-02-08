package com.winestoreapp.dto.mapper;

import com.winestoreapp.config.MapperConfig;
import com.winestoreapp.dto.order.OrderDto;
import com.winestoreapp.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(config = MapperConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "shoppingCardId", source = "shoppingCard.id")
    @Mapping(target = "deliveryInformationId", source = "deliveryInformation.id")
    OrderDto toDto(Order order);
}

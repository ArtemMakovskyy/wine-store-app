package com.winestoreapp.dto.mapper;

import com.winestoreapp.config.MapperConfig;
import com.winestoreapp.dto.order.delivery.information.CreateOrderDeliveryInformationDto;
import com.winestoreapp.dto.order.delivery.information.OrderDeliveryInformationDto;
import com.winestoreapp.model.OrderDeliveryInformation;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(config = MapperConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderDeliveryInformationMapper {
    OrderDeliveryInformation toEntity(CreateOrderDeliveryInformationDto dto);

    OrderDeliveryInformationDto toDto(OrderDeliveryInformation odi);
}

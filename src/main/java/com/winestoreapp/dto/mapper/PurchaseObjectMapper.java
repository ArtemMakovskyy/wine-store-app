package com.winestoreapp.dto.mapper;

import com.winestoreapp.config.MapperConfig;
import com.winestoreapp.dto.purchase.object.CreatePurchaseObjectDto;
import com.winestoreapp.model.PurchaseObject;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(config = MapperConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PurchaseObjectMapper {

    CreatePurchaseObjectDto toDto(PurchaseObject purchaseObject);
}

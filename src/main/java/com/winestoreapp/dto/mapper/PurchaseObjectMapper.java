package com.winestoreapp.dto.mapper;

import com.winestoreapp.config.MapperConfig;
import com.winestoreapp.dto.purchase.object.PurchaseObjectDto;
import com.winestoreapp.model.PurchaseObject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(config = MapperConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PurchaseObjectMapper {

    //    CreatePurchaseObjectDto toDto(PurchaseObject purchaseObject);

    @Mapping(target = "wineId", source = "purchaseObject.wine.id")
    @Mapping(target = "winePictureLink", source = "purchaseObject.wine.pictureLink")
    @Mapping(target = "wineName", source = "purchaseObject.wine.name")
    PurchaseObjectDto toDto(PurchaseObject purchaseObject);

}

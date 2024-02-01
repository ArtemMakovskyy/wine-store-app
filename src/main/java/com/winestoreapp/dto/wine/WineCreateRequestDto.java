package com.winestoreapp.dto.wine;

import com.winestoreapp.model.WineColor;
import com.winestoreapp.model.WineType;
import java.math.BigDecimal;
import java.net.URL;

public record WineCreateRequestDto(
        String name,
        BigDecimal price,
        String grape,
        Boolean isDecantation,
        WineType wineType,
        BigDecimal strengthFrom,
        BigDecimal strengthTo,
        WineColor wineColor,
        String colorDescribing,
        String taste,
        String aroma,
        String gastronomy,
        String description,
        URL pictureLink
) {
}

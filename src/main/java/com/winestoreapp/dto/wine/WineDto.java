package com.winestoreapp.dto.wine;

import com.winestoreapp.model.WineColor;
import com.winestoreapp.model.WineType;
import java.math.BigDecimal;
import java.net.URL;
import lombok.Data;

@Data
public class WineDto {
    private Long id;
    private String vendorCode;
    private String name;
    private String shortName;
    private BigDecimal averageRatingScore;
    private BigDecimal price;
    private String grape;
    private Boolean isDecantation;
    private WineType wineType;
    private BigDecimal strengthFrom;
    private BigDecimal strengthTo;
    private WineColor wineColor;
    private String colorDescribing;
    private String taste;
    private String aroma;
    private String gastronomy;
    private String description;
    private URL pictureLink;
}

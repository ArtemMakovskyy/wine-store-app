package com.winestoreapp.dto.wine;

import com.winestoreapp.model.WineColor;
import com.winestoreapp.model.WineType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WineCreateRequestDto {
    @Schema(example = "MSD 2019")
    @NotBlank(message = "Vendor Code should not be blank")
    private String vendorCode;
    @NotBlank(message = "Select or Grand Reserve ")
    private String qualityLevel;
    @Schema(example = "Limeted Edition Vine")
    private String reserveType;
    @Schema(example = "Prince Trubetskoi Select Riesling")
    @NotBlank(message = "Name should not be blank")
    private String name;
    @Schema(example = "Prince Trubetskoi")
    private String shortName;
    @Schema(example = "2017")
    private Integer year;
    @Schema(example = "fish")
    private String tasteWine;
    @Schema(example = "25.59")
    private BigDecimal price;
    @Schema(example = "Riesling")
    private String grape;
    @Schema(example = "TRUE | FALSE")
    private Boolean isDecantation;
    @Schema(example = "DRY | SEMI_DRY | MEDIUM_SWEET | SWEET")
    private WineType wineType;
    @Schema(example = "10.9")
    private BigDecimal strengthFrom;
    @Schema(example = "11.8")
    private BigDecimal strengthTo;
    @Schema(example = "RED | ROSE | WHITE")
    private WineColor wineColor;
    @Schema(example = "Deep red")
    private String colorDescribing;
    @Schema(example = "delicate, balanced, round, with a fruity and honey aftertaste.")
    private String taste;
    @Schema(example = "soft, generous, multifaceted, with hints of tropical ")
    private String aroma;
    @Schema(example = "goes well with meat dishes, mature cheeses and stews ")
    private String gastronomy;
    @Schema(example = "description")
    private String description;
    @Schema(example = "Fill this link if you already have file into drive")
    private String pictureLink;
    @Schema(example = "Fill this link if you already have file into drive")
    private String pictureLink2;
}

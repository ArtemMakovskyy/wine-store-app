package com.winestoreapp.model;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Wine {
    private Long id;
    private String name;
    private BigDecimal price;
    private String grape;
    private String decantation;
    private String strength;
    private String colour;
    private String taste;
    private String aroma;
    private String gastronomy;
    private Integer rating;
}

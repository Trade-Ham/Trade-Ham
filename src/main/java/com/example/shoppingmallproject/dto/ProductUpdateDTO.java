package com.example.shoppingmallproject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductUpdateDTO {
    private String name;
    private String description;
    private Long price;
}

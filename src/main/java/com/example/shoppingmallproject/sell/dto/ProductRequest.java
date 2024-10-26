package com.example.shoppingmallproject.sell.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductRequest {
    private String productName;
    private String description;
    private Long price;
}

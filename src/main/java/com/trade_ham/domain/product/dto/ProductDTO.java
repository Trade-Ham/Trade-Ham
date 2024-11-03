package com.trade_ham.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductDTO {
    private String name;
    private String description;
    private Long price;
}

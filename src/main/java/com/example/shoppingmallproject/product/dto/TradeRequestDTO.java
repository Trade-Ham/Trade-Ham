package com.example.shoppingmallproject.product.dto;

import lombok.Data;

@Data
public class TradeRequestDTO {
    private Long buyerId;
    private Long productId;
    private Long lockerId;
}

package com.trade_ham.domain.product.dto;

import com.trade_ham.domain.product.domain.Product;
import com.trade_ham.domain.product.domain.ProductStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductResponseDTO {
    private Long productId;
    private String name;
    private String description;
    private Long price;
    private ProductStatus status;

    public ProductResponseDTO(Product product) {
        this.productId = product.getProductId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.status = product.getStatus();
    }
}
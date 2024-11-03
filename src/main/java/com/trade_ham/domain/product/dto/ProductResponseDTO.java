package com.trade_ham.domain.product.dto;

import com.trade_ham.domain.product.entity.ProductEntity;
import com.trade_ham.domain.product.entity.ProductStatus;
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

    public ProductResponseDTO(ProductEntity productEntity) {
        this.productId = productEntity.getProductId();
        this.name = productEntity.getName();
        this.description = productEntity.getDescription();
        this.price = productEntity.getPrice();
        this.status = productEntity.getStatus();
    }
}
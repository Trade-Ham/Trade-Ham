package com.trade_ham.domain.product.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDocument {
    @Id
    private Long id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Long)
    private Long price;

    @Field(type = FieldType.Keyword)
    private String status;

    public static ProductDocument from(ProductEntity entity) {
        return new ProductDocument(
                entity.getProductId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getStatus().toString()
        );
    }
}
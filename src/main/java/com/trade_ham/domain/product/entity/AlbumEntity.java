package com.trade_ham.domain.product.entity;

import jakarta.persistence.*;

@Entity
public class AlbumEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long albumId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity productEntity;

    private String imageUrl;
    private Double fileSize;

}

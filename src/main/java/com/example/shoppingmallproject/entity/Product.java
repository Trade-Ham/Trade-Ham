package com.example.shoppingmallproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "product")
public class Product extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Long price;
}

package com.trade_ham.domain.product.entity;

import com.trade_ham.domain.auth.entity.UserEntity;
import com.trade_ham.domain.locker.entity.LockerEntity;
import com.trade_ham.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private UserEntity seller;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private UserEntity buyer;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private ProductStatus status;

    @Column(nullable = false)
    private Long price;

    @Setter
    @OneToOne
    @JoinColumn(name = "locker_id")
    private LockerEntity lockerEntity;


    public void updateProduct(String name, String description, Long price){
        this.name = name;
        this.description = description;
        this.price = price;
    }

}
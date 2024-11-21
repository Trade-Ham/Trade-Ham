package com.trade_ham.domain.product.entity;

import com.trade_ham.domain.auth.entity.UserEntity;
import com.trade_ham.domain.locker.entity.LockerEntity;
import com.trade_ham.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

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

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer view;


    // Redis 좋아요 개수
    @Setter
    @ColumnDefault("0")
    private Long likeCount;

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
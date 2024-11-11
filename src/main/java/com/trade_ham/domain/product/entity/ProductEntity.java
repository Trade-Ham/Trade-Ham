package com.trade_ham.domain.product.entity;

import com.trade_ham.domain.auth.entity.UserEntity;
import com.trade_ham.domain.locker.entity.LockerEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {
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

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;


    public void updateProduct(String name, String description, Long price){
        this.name = name;
        this.description = description;
        this.price = price;
    }

}
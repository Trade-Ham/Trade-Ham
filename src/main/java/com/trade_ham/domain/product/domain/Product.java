package com.trade_ham.domain.product.domain;

import com.trade_ham.domain.auth.entity.UserEntity;
import com.trade_ham.domain.locker.domain.Locker;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
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

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @Column(nullable = false)
    private Long price;

    @Setter
    @OneToOne
    @JoinColumn(name = "locker_id")
    private Locker locker;


    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void updateProduct(String name, String description, Long price){
        this.name = name;
        this.description = description;
        this.price = price;
    }

}
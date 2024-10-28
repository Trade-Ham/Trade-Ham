package com.trade_ham.domain.product.domain;

import com.trade_ham.domain.auth.entity.UserEntity;
import com.trade_ham.domain.locker.domain.Locker;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tradeId;
    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private UserEntity buyer;
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private UserEntity seller;
    @ManyToOne
    @JoinColumn(name = "locker_id")
    private Locker locker;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Trade setBuyer(UserEntity buyer) {
        this.buyer = buyer;
        return this;
    }

    public Trade setSeller(UserEntity seller) {
        this.seller = seller;
        return this;
    }

    public Trade setLocker(Locker locker) {
        this.locker = locker;
        return this;
    }

    public Trade setProduct(Product product) {
        this.product = product;
        return this;
    }
}

package com.example.shoppingmallproject.product.domain;

import com.example.shoppingmallproject.locker.domain.Locker;
import com.example.shoppingmallproject.user.domain.User;
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
    private User buyer;
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;
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

    public Trade setBuyer(User buyer) {
        this.buyer = buyer;
        return this;
    }

    public Trade setSeller(User seller) {
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

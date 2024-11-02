package com.example.shoppingmallproject.sell.domain;

import com.example.shoppingmallproject.login.domain.User;
import com.example.shoppingmallproject.trade.domain.Trades;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Products {

    @Id
    @GeneratedValue
    @Column(name = "product_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(name = "name", nullable = false)
    private String productName;

    @Column(name = "description", nullable = false)
    private String productDescription;

    @Enumerated(EnumType.STRING)
    @Column(length = 5, nullable = false)
    private StatusType status;

    @Column(name = "price", nullable = false)
    private Long price;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Trades> trades = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

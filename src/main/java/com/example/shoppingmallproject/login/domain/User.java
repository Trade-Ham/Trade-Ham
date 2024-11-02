package com.example.shoppingmallproject.login.domain;

import com.example.shoppingmallproject.sell.domain.Products;
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
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String nickname;

    @Column(length = 255, nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(length = 5, nullable = false)
    private RoleType role;

    @Enumerated(EnumType.STRING)
    @Column(length = 5, nullable = false)
    private ProviderType provider;

    @Column(length = 50)
    private String account;

    @Column(length = 10)
    private String realname;

    @Column(length = 255, nullable = false)
    private String profileImage;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
    private List<Products> products = new ArrayList<>();

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL)
    private List<Trades> boughtTrades = new ArrayList<>();

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
    private List<Trades> soldTrades = new ArrayList<>();

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

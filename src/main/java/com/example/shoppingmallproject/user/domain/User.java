package com.example.shoppingmallproject.user.domain;

import com.example.shoppingmallproject.product.domain.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AuthProvider provider;

    @Column(length = 50)
    private String account;

    @Column(length = 10)
    private String realname;

    private String profileImage;
    @OneToMany(mappedBy = "seller")
    private List<Product> sellingProducts = new ArrayList<>();

    @OneToMany(mappedBy = "buyer")
    private List<Product> purchasedProducts = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public User(String email, String nickname, UserRole role, AuthProvider provider, String account, String realname, String profileImage) {
        this.email = email;
        this.nickname = nickname;
        this.role = role;
        this.provider = provider;
        this.account = account;
        this.realname = realname;
        this.profileImage = profileImage;
    }

    public User(String email, String nickname, String password, UserRole role, AuthProvider provider, String account, String realname, String profileImage) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
        this.provider = provider;
        this.account = account;
        this.realname = realname;
        this.profileImage = profileImage;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void addSellingProduct(Product product) {
        this.sellingProducts.add(product);
    }

    public void addPurchasedProduct(Product product) {
        this.purchasedProducts.add(product);
    }

    public void deleteSellingProduct(Product product) {
        this.sellingProducts.remove(product);
    }
}
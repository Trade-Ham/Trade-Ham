package com.trade_ham.domain.product.entity;

import com.trade_ham.domain.auth.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"})
)
public class LikeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    private LikeEntity(UserEntity user, ProductEntity product) {
        this.user = user;
        this.product = product;
    }

    public static LikeEntity of (UserEntity user, ProductEntity product) {
        return new LikeEntity(user, product);
    }
}

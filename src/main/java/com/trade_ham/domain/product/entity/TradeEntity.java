package com.trade_ham.domain.product.entity;

import com.trade_ham.domain.auth.entity.UserEntity;
import com.trade_ham.domain.locker.entity.LockerEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeEntity {
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
    private LockerEntity lockerEntity;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity productEntity;

}

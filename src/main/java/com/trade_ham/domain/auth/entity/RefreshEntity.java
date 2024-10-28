package com.trade_ham.domain.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RefreshEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refresh_id;

    @Column(length = 512)
    private String refresh;

    private String expiration;
}

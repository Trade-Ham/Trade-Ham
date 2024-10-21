package com.example.shoppingmallproject.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash
@Getter @Setter
public class Refresh {

    @Id
    private String refresh;

    private String email;
}

package com.example.shoppingmallproject.dto;

import com.example.shoppingmallproject.entity.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCreatedDTO {
    private Long sellerId;
    private String name;
    private String description;
    private Status status;
    private Long price;
}

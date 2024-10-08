package com.example.shoppingmallproject.login.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class User {

    @Id @GeneratedValue
    private Long id;

    private String email;
    private String name;

    public void setEmail(String email) {
    }

    public void setName(String name) {
    }
}

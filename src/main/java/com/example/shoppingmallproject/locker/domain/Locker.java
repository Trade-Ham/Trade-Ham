package com.example.shoppingmallproject.locker.domain;

import com.example.shoppingmallproject.trade.domain.Trades;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Locker {

    @Id
    @GeneratedValue
    private Long lockerId;

    private int lockerNumber;
    private Boolean lockerStatus;

    @OneToMany(mappedBy = "locker", cascade = CascadeType.ALL)
    private List<Trades> trades = new ArrayList<>();
}

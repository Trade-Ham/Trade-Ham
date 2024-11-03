package com.trade_ham.domain.locker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class LockerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "locker_id")
    private Long id;

    @Column(name = "locker_number", nullable = false, length = 50)
    private String lockerNumber;

    @Column(name = "locker_password", length = 50)
    private String lockerPassword;

    @Setter
    @Column(name = "locker_status", nullable = false)
    private Boolean lockerStatus;

    public LockerEntity(String lockerNumber, String lockerPassword, Boolean lockerStatus) {
        this.lockerNumber = lockerNumber;
        this.lockerPassword = lockerPassword;
        this.lockerStatus = lockerStatus;
    }
}
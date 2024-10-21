package com.example.shoppingmallproject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LockerStatusDTO {
    private Long id;
    private Boolean lockerStatus;

    public LockerStatusDTO(Long id, Boolean lockerStatus) {
        this.id = id;
        this.lockerStatus = lockerStatus;
    }
}

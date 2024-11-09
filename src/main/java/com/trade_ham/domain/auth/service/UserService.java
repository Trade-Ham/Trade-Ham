package com.trade_ham.domain.auth.service;

import com.trade_ham.domain.auth.dto.UserUpdateDTO;
import com.trade_ham.domain.auth.entity.UserEntity;
import com.trade_ham.domain.auth.repository.UserRepository;
import com.trade_ham.global.common.exception.ErrorCode;
import com.trade_ham.global.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserUpdateDTO updateUser(Long sellerId, UserUpdateDTO userUpdateDTO) {
        UserEntity userEntity = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));

        userEntity.setAccount(userUpdateDTO.getAccount());
        userEntity.setNickname(userUpdateDTO.getRealname());

        userRepository.save(userEntity);

        return userUpdateDTO;
    }
}

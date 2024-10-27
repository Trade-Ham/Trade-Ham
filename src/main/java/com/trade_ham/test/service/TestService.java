package com.trade_ham.test.service;

import com.trade_ham.common.exception.AccessDeniedException;
import com.trade_ham.common.exception.ErrorCode;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    public String test(String str){
        if (str.equals("error")){
            throw new AccessDeniedException(ErrorCode.ACCESS_DENIED);
        }

        return "Success";
    }
}

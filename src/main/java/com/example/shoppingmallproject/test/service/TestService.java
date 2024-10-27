package com.example.shoppingmallproject.test.service;

import com.example.shoppingmallproject.common.exception.AccessDeniedException;
import com.example.shoppingmallproject.common.exception.ErrorCode;
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

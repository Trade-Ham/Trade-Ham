package com.example.shoppingmallproject.test.controller;


import com.example.shoppingmallproject.common.response.ApiResponse;
import com.example.shoppingmallproject.test.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;

    @RequestMapping("/test")
    public ApiResponse<String> test(){
        String code = "Success";

        testService.test(code);

        return ApiResponse.success(code);
    }
}

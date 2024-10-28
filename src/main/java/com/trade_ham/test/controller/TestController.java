//package com.trade_ham.test.controller;
//
//
//import com.trade_ham.global.common.response.ApiResponse;
//import com.trade_ham.test.service.TestService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//public class TestController {
//    private final TestService testService;
//
//    @RequestMapping("/test")
//    public ApiResponse<String> test(){
//        String code = "Success";
//
//        testService.test(code);
//
//        return ApiResponse.success(code);
//    }
//}

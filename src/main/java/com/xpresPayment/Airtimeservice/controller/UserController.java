package com.xpresPayment.Airtimeservice.controller;

import com.xpresPayment.Airtimeservice.dto.response.UserDto;
import com.xpresPayment.Airtimeservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping()
    public ResponseEntity<UserDto> getCurrentUser(){
        return userService.getCurrentUser();
    }
}

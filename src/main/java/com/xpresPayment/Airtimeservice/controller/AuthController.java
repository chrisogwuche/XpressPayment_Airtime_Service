package com.xpresPayment.Airtimeservice.controller;

import com.xpresPayment.Airtimeservice.dto.request.LoginRequest;
import com.xpresPayment.Airtimeservice.dto.request.RegRequest;
import com.xpresPayment.Airtimeservice.dto.response.RegisterResponse;
import com.xpresPayment.Airtimeservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegRequest request){
        return authService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){
        return authService.login(request);
    }

}
package com.xpresPayment.Airtimeservice.service;

import com.xpresPayment.Airtimeservice.dto.request.LoginRequest;
import com.xpresPayment.Airtimeservice.dto.request.RegRequest;
import com.xpresPayment.Airtimeservice.dto.response.RegisterResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface AuthService {
    ResponseEntity<RegisterResponse> register(RegRequest request);
    ResponseEntity<?> login(LoginRequest request);
}

package com.xpresPayment.Airtimeservice.service;

import com.xpresPayment.Airtimeservice.dto.response.UserDto;
import com.xpresPayment.Airtimeservice.service.serviceImpl.UserUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    ResponseEntity<UserDto> getCurrentUser();
}

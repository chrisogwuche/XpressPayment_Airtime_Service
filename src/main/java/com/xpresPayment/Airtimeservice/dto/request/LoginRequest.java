package com.xpresPayment.Airtimeservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginRequest {
    @NotEmpty(message = "Email must not be empty")
    @Email(message = "Email must be in an email format")
    private String email;
    @NotEmpty(message = "password must not be empty")
    private String password;
}
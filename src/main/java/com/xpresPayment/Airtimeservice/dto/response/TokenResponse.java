package com.xpresPayment.Airtimeservice.dto.response;

import lombok.Data;

import java.util.Date;

@Data
public class TokenResponse {
    private String token;
    private String refreshToken;
    private Date issuedAt;
    private Date expiresAt;

}

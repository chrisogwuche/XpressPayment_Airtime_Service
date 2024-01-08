package com.xpresPayment.Airtimeservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterResponse {
    private String status;
    private String message;
    private TokenResponse data;
}

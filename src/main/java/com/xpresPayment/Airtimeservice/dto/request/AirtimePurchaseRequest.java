package com.xpresPayment.Airtimeservice.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AirtimePurchaseRequest {
    @NotEmpty(message = "phone_number must not be empty")
    @Pattern(regexp = "\\d+", message = "phone_number must be a digit")
    private String phone_number;
    @NotEmpty(message = "amount must not be empty")
    @Pattern(regexp = "\\d+", message = "amount must be a digit")
    private String amount;
    @NotEmpty(message = "network_code must not be empty")
    private String network_code;
}

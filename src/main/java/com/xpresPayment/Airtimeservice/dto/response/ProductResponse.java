package com.xpresPayment.Airtimeservice.dto.response;

import lombok.Data;

@Data
public class ProductResponse {

    private String responseCode;
    private String responseMessage;
    private ProductData data;
}

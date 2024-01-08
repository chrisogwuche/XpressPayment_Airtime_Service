package com.xpresPayment.Airtimeservice.dto.request;

import lombok.Data;

@Data
public class XpressAirtimeRequest {
    private String requestId;
    private String uniqueCode;
    private AirtimeDetails details;
}

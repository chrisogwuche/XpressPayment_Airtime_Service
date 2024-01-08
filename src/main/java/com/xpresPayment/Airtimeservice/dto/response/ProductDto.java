package com.xpresPayment.Airtimeservice.dto.response;

import lombok.Data;

@Data
public class ProductDto {
    private String name;
    private String uniqueCode;
    private Long minimumAmount;
    private Long maximumAmount;
}

package com.xpresPayment.Airtimeservice.service;

import com.xpresPayment.Airtimeservice.dto.request.AirtimePurchaseRequest;
import com.xpresPayment.Airtimeservice.dto.response.ProductDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AirtimeService {
    ResponseEntity<List<ProductDto>> getAirtimeProducts();

    ResponseEntity<?> purchaseAirtime(AirtimePurchaseRequest request);
}

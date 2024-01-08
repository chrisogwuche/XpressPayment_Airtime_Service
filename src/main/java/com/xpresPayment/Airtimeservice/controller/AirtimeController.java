package com.xpresPayment.Airtimeservice.controller;

import com.xpresPayment.Airtimeservice.dto.request.AirtimePurchaseRequest;
import com.xpresPayment.Airtimeservice.dto.response.ProductDto;
import com.xpresPayment.Airtimeservice.service.AirtimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/airtime")
public class AirtimeController {

    private final AirtimeService airtimeService;

    @GetMapping("/all")
    public ResponseEntity<List<ProductDto>> getAllAirtimeProduct(){
        return airtimeService.getAirtimeProducts();
    }

    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseAirtime(@Valid @RequestBody AirtimePurchaseRequest request){
        return airtimeService.purchaseAirtime(request);
    }
}

package com.xpresPayment.Airtimeservice.service.serviceImpl;

import com.xpresPayment.Airtimeservice.dto.request.AirtimeDetails;
import com.xpresPayment.Airtimeservice.dto.request.AirtimePurchaseRequest;
import com.xpresPayment.Airtimeservice.dto.request.ProductRequest;
import com.xpresPayment.Airtimeservice.dto.request.XpressAirtimeRequest;
import com.xpresPayment.Airtimeservice.dto.response.ProductDto;
import com.xpresPayment.Airtimeservice.dto.response.ProductResponse;
import com.xpresPayment.Airtimeservice.dto.response.ResponseDto;
import com.xpresPayment.Airtimeservice.dto.response.XpressAirtimeResponse;
import com.xpresPayment.Airtimeservice.enums.TransactionStatus;
import com.xpresPayment.Airtimeservice.exceptions.InvalidInputException;
import com.xpresPayment.Airtimeservice.exceptions.NotFoundException;
import com.xpresPayment.Airtimeservice.exceptions.ServiceException;
import com.xpresPayment.Airtimeservice.model.Invoice;
import com.xpresPayment.Airtimeservice.model.User;
import com.xpresPayment.Airtimeservice.repository.InvoiceRepository;
import com.xpresPayment.Airtimeservice.repository.UserRepository;
import com.xpresPayment.Airtimeservice.service.AirtimeService;
import kong.unirest.JsonObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AirtimeServiceImpl implements AirtimeService {

    private final HttpHeaders httpHeaders;
    private final JsonObjectMapper jsonObjectMapper;
    private final UserUtils userUtils;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;

    @Value("${xpress.baseUrl}")
    private String xpressBaseUrl;
    @Value("${xpress.productUrl}")
    private String xpressProductUrl;
    @Value("${xpress.public.key}")
    private String xpressPublicKey;
    @Value("${xpress.airtimeUrl}")
    private String xpressAirtimeUrl;
    @Value("${xpress.private.key}")
    private String xpressPrivateKey;


    @Override
    public ResponseEntity<List<ProductDto>> getAirtimeProducts() {
        return ResponseEntity.ok(getAllAirtimeProduct());
    }

    @Override
    public ResponseEntity<?> purchaseAirtime(AirtimePurchaseRequest request) {
        long airtimeAmount = Long.parseLong(request.getAmount());

        if(airtimeAmount >= 100 && airtimeAmount <= 10000){
            User currentUser = userUtils.currentUser();
            Invoice savedInvoice = generateAndSaveInvoice(request,currentUser);
            currentUser.addInvoice(savedInvoice);
            userRepository.save(currentUser);

            return ResponseEntity.ok(purchaseAirtime(savedInvoice));
        }
        else if(airtimeAmount<100) {
            throw new InvalidInputException("Airtime amount should be atleast 100");
        }
        else {
            throw new InvalidInputException("Airtime amount should not exceed 10,000");
        }
    }

    //Handle airtime purchase with already created and saved invoice
    public ResponseDto purchaseAirtime(Invoice invoice){

        String url = xpressBaseUrl+xpressAirtimeUrl;
        XpressAirtimeRequest request = new XpressAirtimeRequest();
        AirtimeDetails details = new AirtimeDetails();
        details.setAmount(Long.parseLong(invoice.getAirtimeAmount()));
        details.setPhoneNumber(invoice.getPhoneNumber());

        request.setRequestId(invoice.getUniqueId());
        request.setUniqueCode(invoice.getNetworkCode());
        request.setDetails(details);
        String requestToString = userUtils.getString(request);

        String hMac = userUtils.calculateHMAC512(requestToString,xpressPrivateKey); // get the hashed value

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", "Bearer " +xpressPublicKey);
        httpHeaders.set("PaymentHash",hMac);
        httpHeaders.set("Channel","API");

        ResponseEntity<String> response = userUtils.restTemplateRequest(url,HttpMethod.POST,httpHeaders,requestToString);
        log.info(response.toString());

        return getAirtimePurchaseResponse(response);
    }

    //Verify the status of the http Response gotten when purchasing an airtime
    private ResponseDto getAirtimePurchaseResponse(ResponseEntity<String> response) {

        if(response.getStatusCode().value() == 200){
            XpressAirtimeResponse airtimeResponse =
                    jsonObjectMapper.readValue(response.getBody(),XpressAirtimeResponse.class); // map the String to an Object
            log.info(airtimeResponse.toString());

            return getResponseDtoAndUpdateInvoice(airtimeResponse);
        }
        else if(response.getStatusCode().is5xxServerError()){
            throw new ServiceException(response.getBody());
        }
        else {
            throw new NotFoundException(response.getBody());
        }
    }

    //Update invoice Trasaction status and returns a response
    private ResponseDto getResponseDtoAndUpdateInvoice(XpressAirtimeResponse airtimeResponse) {
        ResponseDto responseDto = new ResponseDto();

        Invoice invoice = invoiceRepository.findInvoiceByUniqueId(airtimeResponse.getRequestId())
                .orElse(null);

        if(invoice!=null) {
            if(Objects.equals(airtimeResponse.getResponseCode(),"00")
                    && airtimeResponse.getResponseMessage().equalsIgnoreCase("successful")) {

                invoice.setTransactionStatus(TransactionStatus.SUCCESSFUL);
                responseDto.setMessage("Airtime purchase successful");
                responseDto.setStatus("SUCCESS");
            }
            else {
                invoice.setTransactionStatus(TransactionStatus.FAILED);
                responseDto.setMessage("Airtime purchase failed");
                responseDto.setStatus("FAILED");
            }
            invoiceRepository.save(invoice);
        }
        else {
            responseDto.setMessage("invoice with not found with requestID: "+airtimeResponse.getRequestId());
            responseDto.setStatus("FAILED");
        }
        return responseDto;
    }

    //Generate and save invoice which will be used for airtime purchase
    private Invoice generateAndSaveInvoice(AirtimePurchaseRequest request, User user){
        Invoice invoice = new Invoice();
        invoice.setUser(user);
        invoice.setAirtimeAmount(request.getAmount());
        invoice.setNetworkCode(request.getNetwork_code());
        invoice.setTransactionStatus(TransactionStatus.PENDING);
        invoice.setUniqueId(String.valueOf(UUID.randomUUID()));
        invoice.setPhoneNumber(request.getPhone_number());
        return invoiceRepository.save(invoice);
    }

    //Returns a list of XpressPayment Airtime products
    public List<ProductDto> getAllAirtimeProduct() {

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", "Bearer " +xpressPublicKey);
        String url = xpressBaseUrl + xpressProductUrl + "?categoryId=1";
        ProductRequest productRequest = new ProductRequest();
        productRequest.setPage(0);
        productRequest.setSize(5);
        String request = userUtils.getString(productRequest);

        ResponseEntity<String> response = userUtils.restTemplateRequest(url, HttpMethod.POST, httpHeaders, request);
        log.info(response.toString());

        return getProductDtoList(response);
    }

    private List<ProductDto> getProductDtoList(ResponseEntity<String> response) {
        List<ProductDto> productDtoList = new ArrayList<>();

        if(response.getStatusCode().value()== 200){
            ProductResponse prodResponse = jsonObjectMapper.readValue(response.getBody(),ProductResponse.class);

            if(Objects.equals(prodResponse.getResponseCode(),"00")
                    && prodResponse.getResponseMessage().equalsIgnoreCase("Successful")){

                productDtoList = prodResponse.getData().getProductDTOList();
            }
        }
        else if(response.getStatusCode().is5xxServerError()){
            throw new ServiceException(response.getBody());
        }
        else {
            throw new NotFoundException(response.getBody());
        }
        return productDtoList;
    }
}

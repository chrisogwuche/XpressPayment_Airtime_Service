package com.xpresPayment.Airtimeservice.service.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xpresPayment.Airtimeservice.exceptions.NotFoundException;
import com.xpresPayment.Airtimeservice.model.User;
import com.xpresPayment.Airtimeservice.repository.UserRepository;
import com.xpresPayment.Airtimeservice.security.UserDetailsDto;
import com.xpresPayment.Airtimeservice.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserUtils {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    //Get current user
    public User currentUser(){
        UserDetailsDto userDetailsDto = SecurityUtils.getAuthenticatedUser(UserDetailsDto.class); // get Principal
        User user = userRepository.findByEmail(userDetailsDto.getUsername())
                .orElseThrow(()->new NotFoundException("User not found"));
        return user;
    }

    //Handle Hashing of String data
    public String calculateHMAC512(String data, String key) {
        String HMAC_SHA512 = "HmacSHA512";
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), HMAC_SHA512);
        Mac mac = null;

        try {
            mac = Mac.getInstance(HMAC_SHA512);
            mac.init(secretKeySpec);
            return Hex.encodeHexString(mac.doFinal(data.getBytes()));

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    //Handles RestTemplate requests
    public ResponseEntity<String> restTemplateRequest(String url, HttpMethod httpMethod, HttpHeaders httpHeader, String payload) {
        log.info("restTemplateRequest:--");
        log.info("request payload ----: " + payload);

        HttpEntity<String> request = new HttpEntity<>(payload, httpHeader);
        log.info("Http Entity----: " + request);
        return restTemplate.exchange(url, httpMethod, request, String.class);
    }

    //Converts Objects to String
    public String getString(Object object) {
        String httpRequest;
        try {
            httpRequest = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return httpRequest;
    }
}

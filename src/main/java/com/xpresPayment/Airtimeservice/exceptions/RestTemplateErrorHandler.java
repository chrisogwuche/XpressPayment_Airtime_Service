package com.xpresPayment.Airtimeservice.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Component
@Slf4j
public class RestTemplateErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
//        if(response.getStatusCode().is4xxClientError()){
//            throw new NotFoundException(response.getBody().toString());
////        }else if(response.getStatusCode().is5xxServerError()){
////            throw new ServiceException(response.getStatusCode().toString());
//        }
//        else {
//            throw new UsernameNotFoundException(response.getStatusCode().toString());
//        }
    }
}

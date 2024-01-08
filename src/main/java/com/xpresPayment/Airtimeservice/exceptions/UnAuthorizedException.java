package com.xpresPayment.Airtimeservice.exceptions;

public class UnAuthorizedException extends RuntimeException {

    public UnAuthorizedException(String message){
        super(message);
    }
}

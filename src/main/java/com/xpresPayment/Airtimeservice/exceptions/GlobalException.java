package com.xpresPayment.Airtimeservice.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoleNotFoundException(NotFoundException ex,HttpServletRequest request){
        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .url(request.getRequestURI())
                .message(ex.getMessage()).build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e
    ){
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error)->{
            String fieldName = ((FieldError)error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> genericHandler(Exception ex, HttpServletRequest request){
        ErrorResponse errorResponse =
                new ErrorResponse(ex.getMessage(),HttpStatus.BAD_REQUEST,request.getRequestURI());

        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<ErrorResponse> unauthorizedHandler(Exception ex, HttpServletRequest request){
        ErrorResponse errorResponse =
                new ErrorResponse(ex.getMessage(),HttpStatus.UNAUTHORIZED,request.getRequestURI());

        return new ResponseEntity<>(errorResponse,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> serviceExceptionHandler(Exception ex, HttpServletRequest request){
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE,request.getRequestURI());

        return new ResponseEntity<>(errorResponse,HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ErrorResponse> wrongInputHandler(Exception ex, HttpServletRequest request){
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(),HttpStatus.BAD_REQUEST,request.getRequestURI());

        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }
}

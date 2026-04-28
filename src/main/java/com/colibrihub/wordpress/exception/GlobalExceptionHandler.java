package com.colibrihub.wordpress.exception;

import com.colibrihub.wordpress.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleGlobalException(Exception ex, WebRequest request){

        ErrorDto error = new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Error",
                ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorDto> handleHttpClientError(HttpClientErrorException ex, WebRequest request){

        ErrorDto error = new ErrorDto(ex.getStatusCode().value(), ex.getStatusText(), ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(error, ex.getStatusCode());
    }
}

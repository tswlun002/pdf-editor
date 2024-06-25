package com.documentservice.user;

import com.documentservice.exception.AppException;
import com.documentservice.exception.EntityAlreadyExistException;
import com.documentservice.exception.EntityNotFoundException;
import com.documentservice.exception.InternalServerError;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.apache.kafka.common.errors.InvalidRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.handler.advice.RequestHandlerCircuitBreakerAdvice;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;


public class UserServiceFallback implements UserApi{
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceFallback.class);
    private final Throwable cause;

    public UserServiceFallback(Throwable cause) {this.cause=cause;}
    @Override
    public ResponseEntity<?> getUser(String traceId, String username) {
        LOGGER.error("Error cause: {}, trace-id:{}",cause,traceId);
        AppException error = null;
        var timeoutOrServiceDown=false;
        try{
            String bodyIs = cause.getMessage();
            if(cause.getCause() instanceof RequestNotPermitted||
            Objects.equals(bodyIs,"TimeLimiter 'UserApigetUserStringString' recorded a timeout exception.")){
                bodyIs= new AppException(HttpStatus.REQUEST_TIMEOUT.name(),"Timeout, try connect to internet.","pdf-editor/users/**",
                        LocalDate.now().toString(),HttpStatus.REQUEST_TIMEOUT.value()).toJson();
                timeoutOrServiceDown=true;
            }
            else  if(cause.getCause() instanceof RequestHandlerCircuitBreakerAdvice.CircuitBreakerOpenException
            || Objects.equals(bodyIs,"CircuitBreaker 'UserApigetUserStringString' is OPEN and does not permit further calls")){
                bodyIs= new AppException(HttpStatus.SERVICE_UNAVAILABLE.name(),"Service is down, too many failed call.","pdf-editor/users/**",
                        LocalDate.now().toString(),HttpStatus.SERVICE_UNAVAILABLE.value()).toJson();
                timeoutOrServiceDown=true;
            }

              ObjectMapper mapper = new ObjectMapper().
                      configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                      .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
            error = mapper.readValue(bodyIs, AppException.class);

        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw  new RuntimeException(e.getMessage());
        }

       var isErrorMessage=error.message()!=null;
       switch (error.status()) {
           case 400 -> {
               LOGGER.error("User invalid request, trace-id: {}",traceId);
               throw new InvalidRequestException(" invalid request.");
           }
           case 404 -> {
               LOGGER.error(isErrorMessage ? "Error: " + error : "User is not found, trace-id: {}",traceId);
               throw new EntityNotFoundException(isErrorMessage ? error.message() : "User is not found.");
           }
           case 409 -> {
               LOGGER.error(isErrorMessage ? "Error: " + error : "User already exists, trace-id: {}",traceId);
               throw new EntityAlreadyExistException(isErrorMessage ? error.message() : "User already exists.");
           }
           default -> {
               if(timeoutOrServiceDown){
                   var string = String.format("Hello %s!, we notice your request please try sometime later or contact the support team ", username);
                   LOGGER.error("User service is not available, error: {}, trace-id:{}",error,traceId);
                   throw new InternalServerError(string);
               }
               else {
                   LOGGER.error(isErrorMessage ? "Error: " + error : "User service internal server error, trace-id: {}",traceId);
                   throw new InternalServerError(isErrorMessage ? error.message() : "User service internal server error.");
               }
           }
       }

    }
}

package com.documentservice.user;

import com.documentservice.exception.InternalServerError;
import feign.RetryableException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.concurrent.TimeoutException;

@FeignClient(value = "users")
public interface UserApi {
    @CircuitBreaker(name = "users", fallbackMethod = "getUserFallback")
    @Retry(name = "users")
    @GetMapping(value = "/pdf-editor/users/{username}",produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getUser(@RequestHeader("trace-Id") String traceId, @PathVariable("username") String username);
    default ResponseEntity<String> getUserFallback( @RequestHeader("trace-Id") String traceId, @PathVariable("username") String username,TimeoutException ex){
        final Logger logger = LoggerFactory.getLogger(UserApi.class);
        logger.error("User service is not available, error: {}, trace-id:{}",ex.getCause(),traceId,ex);
        throw new InternalServerError( "Request timed out, please try again sometime later.");
    }
    default ResponseEntity<String> getUserFallback(@RequestHeader("trace-Id") String traceId, @PathVariable("username") String username, RetryableException ex) {
        final Logger logger = LoggerFactory.getLogger(UserApi.class);
        logger.error("User service is not available, error: {}, trace-id:{}",ex,traceId);
        throw new InternalServerError("Service is down, please try again sometime later.");

    }
    default ResponseEntity<String> getUserFallback( @RequestHeader("trace-Id") String traceId, @PathVariable("username") String username,CallNotPermittedException ex){
        final Logger logger = LoggerFactory.getLogger(UserApi.class);
        logger.error("User service is not available, error: {}, trace-id:{}",ex.getCause(),traceId,ex);
           var string = String.format("Hello %s!, we notice your request please try sometime later or contact the support team ", username);
           throw new InternalServerError(string);

    }

}

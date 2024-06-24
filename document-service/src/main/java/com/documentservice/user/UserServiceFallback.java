package com.documentservice.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UserServiceFallback implements UserApi{
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceFallback.class);
   // private final Throwable cause;

    //public UserServiceFallback(Throwable cause) {this.cause=cause;}
    @Override
    public ResponseEntity<?> getUser(String traceId, String username) {
        var string =String.format("Hello %s!, we notice your request please try sometime later or contact the support team ", username);
        LOGGER.error(string,"\ntraceId:{}",traceId);
//        if(cause.getCause().getClass()== InternalServerError.class){
//            LOGGER.error("Service user service is not available.");
            return new ResponseEntity<>(string, HttpStatus.SERVICE_UNAVAILABLE);
//        }
//        throw  cause;
    }





}

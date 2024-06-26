package com.documentservice.user;


import com.documentservice.exception.InvalidUser;
import org.springframework.stereotype.Service;

@Service
public interface User {
    public UserDto getUser( String traceId,String username) throws InvalidUser;
}

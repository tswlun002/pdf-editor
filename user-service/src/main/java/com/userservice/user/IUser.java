package com.userservice.user;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;
@Service
@Validated
public interface IUser {

    boolean createUser(String traceId,@Valid UserRegister userRegister) ;
    Optional<User> findUserByEmail(String traceId,String email);
}

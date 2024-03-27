package com.userservice.email;

import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public interface IEmail {

    Optional<Boolean> sendEmail( String username);
}

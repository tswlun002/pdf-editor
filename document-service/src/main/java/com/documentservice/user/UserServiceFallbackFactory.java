package com.documentservice.user;

import org.springframework.cloud.openfeign.FallbackFactory;


public class UserServiceFallbackFactory implements FallbackFactory<UserServiceFallback> {

    @Override
    public UserServiceFallback create(Throwable cause) {
        return new UserServiceFallback(cause);
    }
}

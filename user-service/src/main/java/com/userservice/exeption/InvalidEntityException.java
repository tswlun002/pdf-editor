package com.userservice.exeption;

import com.userservice.utils.ExceptionMessages;

public class InvalidEntityException extends RuntimeException {
    public InvalidEntityException(String resource) {
        super(ExceptionMessages.FORMAT_EXCEPTION_MESSAGE.apply(ExceptionMessages.INVALID_RESOURCE, new String[]{resource}));

    }

}

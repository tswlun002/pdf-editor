package com.userservice.exeption;

import com.userservice.utils.ExceptionMessages;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String resource, String field,String value ) {
        super(ExceptionMessages.FORMAT_EXCEPTION_MESSAGE.apply(ExceptionMessages.RESOURCE_NOT_FOUND, new String[]{resource,field,value}));
    }


}

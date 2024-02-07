package com.userservice.exeption;

import com.userservice.utils.ExceptionMessages;

public  class EntityAlreadyExistException extends RuntimeException {
    public EntityAlreadyExistException(String resource, String field, String value) {

        super( ExceptionMessages.FORMAT_EXCEPTION_MESSAGE.apply(ExceptionMessages.RESOURCE_EXIST, new String[]{resource,field,value}));
    }
}

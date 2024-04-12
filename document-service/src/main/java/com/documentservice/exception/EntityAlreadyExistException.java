package com.documentservice.exception;

public  class EntityAlreadyExistException extends RuntimeException {
    public EntityAlreadyExistException( String s) {

        super(s);
    }
}

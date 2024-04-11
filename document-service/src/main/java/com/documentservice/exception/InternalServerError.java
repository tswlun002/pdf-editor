package com.documentservice.exception;

public class InternalServerError extends  InternalError{
    public InternalServerError(String message){
        super(message);
    }
}

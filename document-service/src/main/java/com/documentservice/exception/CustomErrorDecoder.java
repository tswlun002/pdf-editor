package com.documentservice.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.kafka.common.errors.InvalidRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomErrorDecoder implements ErrorDecoder {

    private static final Logger log = LoggerFactory.getLogger(CustomErrorDecoder.class);

    @Override
    public Exception decode(String s, Response response) {
        log.info(s,response);
        var api = s.split("#")[0];
        var entity = api.substring(0,api.length()-3);
        switch (response.status()){

            case 400->throw new InvalidRequestException(entity+" invalid request.");
            case 404->throw  new EntityNotFoundException(entity+" is not found.");
            case 409 -> throw  new EntityAlreadyExistException(entity+" already exists.");
            default -> throw  new InternalServerError(entity+" internal server error.");

        }
    }
}
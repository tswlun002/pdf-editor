package com.documentservice.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.errors.InvalidRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

@AllArgsConstructor
public class CustomErrorDecoder implements ErrorDecoder {
    private static  final Logger LOGGER = LoggerFactory.getLogger(CustomErrorDecoder.class);
    @Override
    public Exception decode(String s, Response response) {

        var api = s.split("#")[0];
        var entity = api.substring(0,api.length()-3);
        AppException message = null;
        try (InputStream bodyIs = response.body()
                .asInputStream()) {
             ObjectMapper mapper = new ObjectMapper();
            message = mapper.readValue(bodyIs, AppException.class);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(),e);
            return new Exception(e.getMessage());
        }
        var isErrorMessage=message.message()!=null;
        switch (response.status()){

            case 400->{
                LOGGER.error(isErrorMessage?"Error: "+message:entity+" invalid request.");
                 throw new InvalidRequestException(isErrorMessage? message.message():entity+" invalid request.");
            }
            case 404->{
                LOGGER.error(isErrorMessage?"Error: "+message:entity+" is not found.");
                 throw  new EntityNotFoundException(isErrorMessage? message.message():entity+" is not found.");
            }
            case 409 ->{
                LOGGER.error(isErrorMessage?"Error: "+message:entity+" already exists.");
                 throw  new EntityAlreadyExistException(isErrorMessage? message.message():entity+" already exists.");
            }
            default -> {
                LOGGER.error(isErrorMessage?"Error: "+message:entity+" service internal server error.");
                 throw  new InternalServerError(isErrorMessage? message.message():entity+" service internal server error.");
            }

        }
    }

}
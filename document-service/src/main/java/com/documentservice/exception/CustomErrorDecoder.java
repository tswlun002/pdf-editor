package com.documentservice.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.kafka.common.errors.InvalidRequestException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

@AllArgsConstructor
public class CustomErrorDecoder implements ErrorDecoder {
    private static  final Logger LOGGER = LoggerFactory.getLogger(CustomErrorDecoder.class);
    private final ModelMapper modelMapper;
    @Override
    public Exception decode(String s, Response response) {

        var api = s.split("#")[0];
        var entity = api.substring(0,api.length()-3);
        ExceptionMessage message = null;
        try (InputStream bodyIs = response.body()
                .asInputStream()) {
             ObjectMapper mapper = new ObjectMapper();
            message = mapper.readValue(bodyIs, ExceptionMessage.class);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(),e);
            return new Exception(e.getMessage());
        }
        LOGGER.info("Error: {}",message.toString());
        var isErrorMessage=message.getMessage()!=null;
        switch (response.status()){

            case 400->{
                LOGGER.error(isErrorMessage?"Error: "+message:entity+" invalid request.");
                 throw new InvalidRequestException(isErrorMessage? message.getMessage():entity+" invalid request.");
            }
            case 404->{
                LOGGER.error(isErrorMessage?"Error: "+message:entity+" is not found.");
                 throw  new EntityNotFoundException(isErrorMessage? message.getMessage():entity+" is not found.");
            }
            case 409 ->{
                LOGGER.error(isErrorMessage?"Error: "+message:entity+" already exists.");
                 throw  new EntityAlreadyExistException(isErrorMessage? message.getMessage():entity+" already exists.");
            }
            default -> {
                LOGGER.error(isErrorMessage?"Error: "+message:entity+" service internal server error.");
                 throw  new InternalServerError(isErrorMessage? message.getMessage():entity+" service internal server error.");
            }

        }
    }
    @Getter
    @Setter
    @ToString
    public static class ExceptionMessage {
        int statusCode;
        String statusCodeMessage;
        String message;
        String time;
        String path;
        // standard getters and setters

    }
}
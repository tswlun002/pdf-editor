package com.documentservice.exception;

import com.fasterxml.jackson.databind.DeserializationFeature;
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
        AppException error = null;
        ObjectMapper mapper = new ObjectMapper().
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        try (InputStream bodyIs = response.body()
                .asInputStream()) {

            error = mapper.readValue(bodyIs, AppException.class);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(),e);
            return new Exception(e.getMessage());
        }
        LOGGER.error("User service error: {}",error);
        var isErrorMessage=error.message()!=null;
        var traceId=response.request().headers().get("trace-id");
        switch (error.status()) {
            case 400 -> {
                LOGGER.error("User invalid request, trace-id: {}",traceId);
                throw new InvalidRequestException(" invalid request.");
            }
            case 404 -> {
                LOGGER.error(isErrorMessage ? "Error: " + error : "User is not found, trace-id: {}",traceId);
                throw new EntityNotFoundException(isErrorMessage ? error.message() : "User is not found.");
            }
            case 409 -> {
                LOGGER.error(isErrorMessage ? "Error: " + error : "User already exists, trace-id: {}",traceId);
                throw new EntityAlreadyExistException(isErrorMessage ? error.message() : "User already exists.");
            }
            default -> {

                    LOGGER.error("Unexpected Error: trace-id:{}, error: {}",traceId,error);
                    throw new InternalServerError(isErrorMessage ? error.message() : "User service internal server error.");

            }
        }
    }

}
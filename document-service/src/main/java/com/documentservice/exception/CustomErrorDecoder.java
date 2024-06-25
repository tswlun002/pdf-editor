package com.documentservice.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.AllArgsConstructor;
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
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream bodyIs = response.body()
                .asInputStream()) {

            message = mapper.readValue(bodyIs, AppException.class);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(),e);
            return new Exception(e.getMessage());
        }

        LOGGER.error("User service error: {}",message);
        try {
            return new UserServiceException(message.toJson());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
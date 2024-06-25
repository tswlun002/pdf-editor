package com.documentservice.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;

import java.io.Serializable;

@Builder

public record AppException (
        String statusCodeMessage,
        String message,
        String path,
        String timestamp,
        int status
        ) implements Serializable{

        public String toJson() throws JsonProcessingException {
                ObjectMapper objectMapper = new ObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
                return objectMapper.writeValueAsString(this);
        }
}

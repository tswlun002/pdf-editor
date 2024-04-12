package com.documentservice.exception;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AppException(
        int statusCode,
        String statusCodeMessage,
        String message,
        LocalDateTime time,String path
        ) {
}

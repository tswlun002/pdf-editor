package com.documentservice.exception;

import lombok.Builder;

@Builder
public record AppException(
        String statusCodeMessage,
        String message,
        String path,
        String timestamp,
        int status
        ) {
}

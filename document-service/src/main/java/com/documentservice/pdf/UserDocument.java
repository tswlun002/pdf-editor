package com.documentservice.pdf;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.NonNull;

public record UserDocument(
        @Email(message = "Email must be a valid email address")
        String email, byte[] file,
        @NonNull@Size(message = "File name must be at least 1 character", min = 1)
        String name
) {
}

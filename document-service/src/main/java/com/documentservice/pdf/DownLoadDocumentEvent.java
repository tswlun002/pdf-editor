package com.documentservice.pdf;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record DownLoadDocumentEvent(
        @NotBlank
        @Email String email,
        byte @NonNull [] file
) {
}

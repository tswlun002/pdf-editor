package com.userservice.user;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
public record UserRegister(
        @NotBlank(message = "email is required")
        @NotEmpty(message = "email is required")
        @Email(message = "Enter valid email, e.g tswlun@gmail.com")
        String email
) {
}

package com.userservice.user;

import com.userservice.utils.Constant;
import jakarta.validation.constraints.Email;

public record UserResponse(
        @Email(message = Constant.EMAIL_VALID_MESS) String email
        ) {
}

package com.example.bojchallenge.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(
    @Email(message = "must be a valid email")
    @NotBlank(message = "email is required")
    String email,

    @NotBlank(message = "password is required")
    @Size(min = 8, max = 64, message = "password length must be between 8 and 64")
    String password,

    @NotBlank(message = "bojHandle is required")
    @Pattern(regexp = "^[A-Za-z0-9_]{3,20}$", message = "handle must be 3-20 characters")
    String bojHandle
) {
}

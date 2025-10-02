package com.example.bojchallenge.group.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record JoinGroupRequest(
    @NotBlank(message = "pin4 is required")
    @Pattern(regexp = "^\\d{4}$", message = "PIN must be 4 digits")
    String pin4
) {
}

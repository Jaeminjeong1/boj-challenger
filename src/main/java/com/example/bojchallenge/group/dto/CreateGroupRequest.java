package com.example.bojchallenge.group.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateGroupRequest(
    @NotBlank(message = "name is required")
    @Size(min = 2, max = 40, message = "name length must be 2-40")
    String name,

    @NotBlank(message = "pin4 is required")
    @Pattern(regexp = "^\\d{4}$", message = "PIN must be 4 digits")
    String pin4
) {
}

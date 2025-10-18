package com.prz.edu.pl.ogloszone.add;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


public record Contact(
        @Pattern(regexp = "^\\+48[0-9]{9}$",
                message = "Phone number must be in format +48123456789") @Schema(description = "Phone number in format +48123456789",
                example = "+48123456789",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String phone,

        @Email(message = "Invalid email format")
        @NotNull(message = "Mail is required")
        @Schema(description = "Email address of the contact",
                example = "user@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String email) {
}
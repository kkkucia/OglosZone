package com.prz.edu.pl.ogloszone.add;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.mongodb.core.index.Indexed;

public record AddRequest(
        @Schema(description = "Title of the announcement",
                example = "Praca jako programista")
        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title cannot exceed 200 characters")
        String title,

        @Schema(description = "Content of the announcement",
                example = "Poszukujemy programisty Java...")
        @NotBlank(message = "Content is required")
        @Size(max = 1500, message = "Content cannot exceed 1500 characters")
        String content,

        @Schema(description = "Category of the announcement",
                example = "JOB",
                allowableValues = {"JOB", "HOUSING", "FOR_SALE", "SERVICES", "TRANSPORT", "EDUCATION", "EVENTS", "PETS", "HEALTH", "OTHER"})
        @Indexed
        @NotBlank(message = "Category is required")
        String category,

        @Schema(description = "Contact information for the announcement")
        @NotNull(message = "Contact is required")
        Contact contact) {
}
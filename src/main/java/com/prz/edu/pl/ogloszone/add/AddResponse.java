package com.prz.edu.pl.ogloszone.add;

import com.prz.edu.pl.ogloszone.category.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.UUID;

public record AddResponse(
        @Id
        @NotNull(message = "Id is required")
        @Schema(description = "Unique identifier of the announcement, automatically generated",
                example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,
        @Schema(description = "Title of the announcement",
                example = "Praca jako programista")
        @NotBlank(message = "Title is required")
        String title,

        @Schema(description = "Content of the announcement",
                example = "Poszukujemy programisty Java...")
        @NotBlank(message = "Content is required")
        String content,

        @Schema(description = "Category of the announcement",
                example = "JOB")
        @Indexed
        @NotNull(message = "Category is required")
        Category category,

        @Schema(description = "Contact information for the announcement")
        @NotNull(message = "Contact is required")
        Contact contact,

        @Schema(description = "Creation date and time of the announcement, used for expiration after 30 days",
                example = "2025-10-18T12:00:00")
        @NotNull(message = "Creation date is required")
        @Indexed(expireAfter = "30d")
        LocalDateTime dateTime) {

    public AddResponse(Add add) {
        this(UUID.fromString(add.id()), add.title(), add.content(), add.category(), add.contact(), add.dateTime());
    }
}

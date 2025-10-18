package com.prz.edu.pl.ogloszone.add;

import com.prz.edu.pl.ogloszone.category.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "announcements")
public record Add(
        @Id String id,
        @NotBlank(message = "Title is required") String title,
        @NotBlank(message = "Content is required") String content,
        @Indexed @NotNull(message = "Category is required") Category category,
        @NotNull(message = "Contact is required") Contact contact,
        @Indexed(expireAfter = "30d") LocalDateTime dateTime,
        @Schema(hidden = true) String editCode) {
}
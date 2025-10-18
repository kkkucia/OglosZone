package com.prz.edu.pl.ogloszone.category;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @GetMapping
    @Operation(summary = "Get all categories",
            description = "Retrieves a sorted list of all available category display names.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Iterable.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error during retrieval",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    public ResponseEntity<Iterable<String>> getAllCategories() {
        Iterable<String> sortedCategories = Arrays.stream(Category.values())
                .sorted(Comparator.comparing(Category::getDisplayName))
                .map(Category::getDisplayName)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sortedCategories);
    }
}
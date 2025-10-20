package com.prz.edu.pl.ogloszone.add;

import com.prz.edu.pl.ogloszone.util.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/add")
public class AddController {

    private final AddService service;

    @Autowired
    public AddController(AddService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Create a new announcement",
            description = "Creates a new announcement and sends a confirmation email.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Announcement created successfully",
                    content = @Content(schema = @Schema(implementation = AddResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request: invalid category or validation failure",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error: email sending failed",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    public ResponseEntity<AddResponse> createAdd(@Valid @RequestBody AddRequest add) {
        return ResponseEntity.status(201).body(service.createAdd(add));
    }

    @GetMapping
    @Operation(summary = "Get all announcements with pagination",
            description = "Retrieves a paginated list of announcements with optional filters for category, date, and keyword.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Announcements retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PagedResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request, e.g., invalid page, size, date format, or category",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error during retrieval",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    public ResponseEntity<PagedResponse<AddResponse>> getAllAnnouncements(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String dateAfter,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponse<AddResponse> response = service.getAllAnnouncements(category, dateAfter, keyword, page, size);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get an announcement by ID",
            description = "Retrieves a single announcement based on its unique identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Announcement retrieved successfully",
                    content = @Content(schema = @Schema(implementation = AddResponse.class))),
            @ApiResponse(responseCode = "404", description = "Announcement not found for the given ID",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error during retrieval",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    public ResponseEntity<AddResponse> getAdd(@PathVariable UUID id) {
        AddResponse add = service.getAddById(id);
        return ResponseEntity.ok(add);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Edit an existing announcement",
            description = "Updates an announcement if the provided edit code matches. Returns the updated announcement.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Announcement updated successfully",
                    content = @Content(schema = @Schema(implementation = AddResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request: invalid category, validation failure, or missing parameters",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden: invalid edit code",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "404", description = "Announcement not found for the given ID",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error during update or sending email",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    public ResponseEntity<AddResponse> updateAdd(@PathVariable UUID id, @RequestParam UUID editCode,
                                                 @Valid @RequestBody AddRequest updatedAdd) {
        AddResponse add = service.updateAdd(id, editCode, updatedAdd);
        return ResponseEntity.ok(add);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an announcement",
            description = "Deletes an announcement if the provided edit code matches.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Announcement deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request: invalid parameters",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden: invalid edit code",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "404", description = "Announcement not found for the given ID",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error during deletion",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    public ResponseEntity<String> deleteAdd(@PathVariable UUID id, @RequestParam UUID editCode) {
        service.deleteAdd(id, editCode);
        return ResponseEntity.status(204).body(String.format("Announcement with id %s deleted successfully", id.toString()));
    }
}
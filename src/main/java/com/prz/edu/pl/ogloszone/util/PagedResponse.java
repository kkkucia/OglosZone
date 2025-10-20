package com.prz.edu.pl.ogloszone.util;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PagedResponse<AddResponse>(
        @Schema(description = "List of items on the current page")
        List<com.prz.edu.pl.ogloszone.add.AddResponse> content,

        @Schema(description = "Current page number (zero-based), where page=0 is the first page",
                example = "0")
        int pageNumber,

        @Schema(description = "Number of items per page",
                example = "10")
        int pageSize,

        @Schema(description = "Total number of elements across all pages",
                example = "50")
        long totalElements,

        @Schema(description = "Total number of pages",
                example = "5")
        int totalPages,

        @Schema(description = "Indicates if the current page is the last one (true if there are no more pages; false if there are additional pages)",
                example = "false")
        boolean last) {
}
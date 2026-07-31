package com.dbtraining.reconx.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO used for PATCH /api/v1/trades/{id}/status
 * Allows updating ONLY the status field with strict validation.
 */
public record StatusUpdate(

        @NotBlank(message = "Status must not be blank")
        @Pattern(
                regexp = "PENDING|MATCHED|UNMATCHED|DISPUTED|CANCELLED",
                message = "Invalid status value"
        )
        String status
) {}

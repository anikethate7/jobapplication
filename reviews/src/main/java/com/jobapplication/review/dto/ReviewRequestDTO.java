package com.jobapplication.review.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Review body is required")
    private String body;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Double rating;

    @NotBlank(message = "Reviewer name is required")
    private String reviewerName;

    @NotNull(message = "Company ID is required")
    private Long companyId;
}
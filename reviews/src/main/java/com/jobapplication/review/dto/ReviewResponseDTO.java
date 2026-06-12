package com.jobapplication.review.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponseDTO {

    private Long id;
    private String title;
    private String body;
    private Double rating;
    private String reviewerName;
    private Long companyId;

    // Enriched from Company Service
    private String companyName;
    private String companyDescription;

    private LocalDateTime createdAt;
}
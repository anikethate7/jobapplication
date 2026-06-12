package com.jobapplication.company.external;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    private Long id;
    private String title;
    private String body;
    private Double rating;
    private String reviewerName;
    private Long companyId;
    private LocalDateTime createdAt;
}
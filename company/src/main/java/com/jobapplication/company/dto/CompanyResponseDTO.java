package com.jobapplication.company.dto;

import com.jobapplication.company.external.JobResponse;
import com.jobapplication.company.external.ReviewResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CompanyResponseDTO {

    private Long id;
    private String name;
    private String description;
    private Double averageRating;
    private List<JobResponse> jobs;
    private List<ReviewResponse> reviews;
    private LocalDateTime createdAt;
}
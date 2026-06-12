package com.jobapplication.company.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompanyRequestDTO {

    @NotBlank(message = "Company name is required")
    private String name;

    private String description;
}
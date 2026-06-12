package com.jobapplication.company.external;

import lombok.Data;

@Data
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private String minSalary;
    private String maxSalary;
    private String location;
    private Long companyId;
}
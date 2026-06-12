package com.jobapplication.company.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CompanyListDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}
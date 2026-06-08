package com.jobapplication.company.company.dto;

import com.jobapplication.company.company.external.JobResponse;

import java.util.List;

public class CompanyResponseDTO {
    private Long id;
    private String name;
    private String description;
    private List<JobResponse> jobs;   // fetched from Job service



    public CompanyResponseDTO(Long id, String name, String description, List<JobResponse> jobs) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.jobs = jobs;
    }

    public CompanyResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<JobResponse> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobResponse> jobs) {
        this.jobs = jobs;
    }

    @Override
    public String toString() {
        return "CompanyResponseDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", jobs=" + jobs +
                '}';
    }
}
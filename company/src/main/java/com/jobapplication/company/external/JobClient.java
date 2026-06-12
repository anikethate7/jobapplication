package com.jobapplication.company.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collections;
import java.util.List;

@FeignClient(name = "JOB-SERVICE", fallback = JobClient.JobClientFallback.class)
public interface JobClient {

    @GetMapping("/jobs/company/{companyId}")
    List<JobResponse> getJobsByCompany(@PathVariable Long companyId);

    @DeleteMapping("/jobs/company/{companyId}")
    void deleteJobsByCompany(@PathVariable Long companyId);

    class JobClientFallback implements JobClient {

        @Override
        public List<JobResponse> getJobsByCompany(Long companyId) {
            return Collections.emptyList();
        }

        @Override
        public void deleteJobsByCompany(Long companyId) {
            // Kafka handles this — fallback is a no-op
        }
    }
}
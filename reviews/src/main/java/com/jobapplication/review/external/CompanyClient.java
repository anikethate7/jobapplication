package com.jobapplication.review.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "COMPANY-SERVICE", fallback = CompanyClient.CompanyClientFallback.class)
public interface CompanyClient {

    @GetMapping("/companies/{id}")
    CompanyResponse getCompanyById(@PathVariable Long id);

    class CompanyClientFallback implements CompanyClient {

        @Override
        public CompanyResponse getCompanyById(Long id) {
            return null;
        }
    }
}
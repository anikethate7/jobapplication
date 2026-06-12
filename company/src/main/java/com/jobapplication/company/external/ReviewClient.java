package com.jobapplication.company.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collections;
import java.util.List;

@FeignClient(name = "REVIEW-SERVICE", fallback = ReviewClient.ReviewClientFallback.class)
public interface ReviewClient {

    @GetMapping("/reviews/company/{companyId}")
    List<ReviewResponse> getReviewsByCompany(@PathVariable Long companyId);

    @GetMapping("/reviews/company/{companyId}/average-rating")
    Double getAverageRating(@PathVariable Long companyId);

    class ReviewClientFallback implements ReviewClient {

        @Override
        public List<ReviewResponse> getReviewsByCompany(Long companyId) {
            return Collections.emptyList();
        }

        @Override
        public Double getAverageRating(Long companyId) {
            return 0.0;
        }
    }
}
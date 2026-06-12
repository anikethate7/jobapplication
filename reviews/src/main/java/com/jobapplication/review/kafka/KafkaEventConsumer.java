package com.jobapplication.review.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobapplication.review.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// FIX #3: Constructor injection
@Component
@Slf4j
public class CompanyEventConsumer {

    private final ReviewService reviewService;
    private final ObjectMapper objectMapper;

    public CompanyEventConsumer(ReviewService reviewService, ObjectMapper objectMapper) {
        this.reviewService = reviewService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "company-deleted", groupId = "review-service-group")
    public void handleCompanyDeleted(String payload) {
        try {
            JsonNode event = objectMapper.readTree(payload);
            Long companyId = event.get("companyId").asLong();
            log.info("Received company-deleted event for companyId: {}", companyId);
            reviewService.deleteReviewsByCompanyId(companyId);
        } catch (Exception e) {
            log.error("Failed to process company-deleted event: {}", e.getMessage());
            throw new RuntimeException("Event processing failed — Kafka will retry", e);
        }
    }
}
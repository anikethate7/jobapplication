package com.jobapplication.company.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CompanyEventProducer {

    private static final String TOPIC = "company-deleted";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public CompanyEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                                ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishCompanyDeleted(Long companyId) {
        try {
            String payload = objectMapper.writeValueAsString(new CompanyDeletedEvent(companyId));
            kafkaTemplate.send(TOPIC, String.valueOf(companyId), payload);
            log.info("Published company-deleted event for companyId: {}", companyId);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize company-deleted event for companyId: {}", companyId, e);
            throw new RuntimeException("Event serialization failed", e);
        }
    }
}